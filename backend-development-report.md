# Backend Development Report
## Integrated Polar Expedition Logistics and Asset Management System (SIH PS 26062)

**Purpose of this document:** Give the backend developer (or an AI coding assistant) a complete, unambiguous spec to implement the backend — entities, relationships, APIs, workflows, and architectural patterns — consistent with the project's frontend report and overall design.

---

## 0. Reference Material Note

Alongside this report, the codebase of BAS's open-source **`assets-tracking-service`** (github.com/antarctica/assets-tracking-service) is being provided for **architectural and design reference only.**

**Explicit instruction: study this repo for design and architecture inspiration. Do NOT copy code, structure, or files from it directly.**

Specifically worth learning from (as concepts, then re-implemented in our own stack):
- Their **adapter/exporter pattern** — a base interface (`BaseExporter`) that each external data source implements, managed centrally by an `ExportersManager`. We should replicate this *pattern* in Java (e.g., a `StatusProvider` interface with a `StatusProviderManager` orchestrator) for our own status-ping sources.
- Their separation of **"current position" (latest known state, fast to query)** from **"historical position" (append-only log)** — apply the same split to our `StatusPing` design (Section 4.2).
- Their tolerance for **staleness** — assets not updating frequently still show a "last known" state with a timestamp rather than erroring out.

Do not reuse their Python code, their database schema verbatim, their provider integrations (Iridium/AIS — not relevant to us), or their file/folder structure. Our stack, entities, and domain (cargo/personnel/inventory, not ships/aircraft) are different enough that direct code reuse would not fit and would introduce unnecessary complexity.

---

## 1. Tech Stack

- **Language/Framework:** Java, Spring Boot
- **Security:** Spring Security + JWT (role-based access control)
- **Databases:**
  - **PostgreSQL** — structured, relational data: Expeditions, Personnel, Cargo, Inventory, Emergencies
  - **MongoDB** — high-frequency, less-structured data: StatusPing logs, activity feed entries
- **Testing:** JUnit + Mockito
- **Code Quality:** SonarQube
- **API style:** REST, JSON, versioned (`/api/v1/...`)

---

## 2. High-Level Architecture

```
Client (React frontend)
      |
      v
  REST API Layer (Controllers)
      |
      v
  Service Layer (business logic, workflows)
      |
      v
  Repository Layer (Spring Data JPA / MongoRepository)
      |
      v
  PostgreSQL (relational)      MongoDB (status pings, logs)
```

**Cross-cutting layer:** A shared `StatusTrackingService` sits alongside the module services and is used by Cargo, Personnel, and Emergency modules alike — this is the direct architectural takeaway from the BAS reference repo (Section 0), reimplemented in our stack.

---

## 3. Core Entities & Relationships

### 3.1 Expedition (PostgreSQL)
```
Expedition {
  id: UUID
  name: String
  objective: String
  startDate: Date
  endDate: Date
  status: Enum [PLANNED, ACTIVE, COMPLETED, CANCELLED]
  createdBy: User (FK)
}
```

### 3.2 TransitLeg (PostgreSQL)
Represents one segment of an expedition's route (e.g., Mumbai → Cape Town, Cape Town → Maitri).
```
TransitLeg {
  id: UUID
  expedition: Expedition (FK)
  sequenceOrder: Integer
  origin: String
  destination: String
  mode: Enum [SHIP, FLIGHT, GROUND]
  expectedDeparture: DateTime
  expectedArrival: DateTime
  actualDeparture: DateTime (nullable)
  actualArrival: DateTime (nullable)
}
```
Both `CargoItem` and `Person` reference the *current* `TransitLeg` they're on (or null if not currently in transit) — this is the shared "where are they right now" concept described in the frontend report.

### 3.3 Person (PostgreSQL)
```
Person {
  id: UUID
  name: String
  role: Enum [SCIENTIST, LOGISTICS_STAFF, MEDICAL, SUPPORT, LEADER]
  fitnessClearanceStatus: Enum [CLEARED, PENDING, NOT_CLEARED]
  currentStatus: Enum [IN_INDIA, IN_TRANSIT, AT_STATION]
  currentLocation: String (station name or transit leg reference)
  currentTransitLeg: TransitLeg (FK, nullable)
  expedition: Expedition (FK)
}
```

### 3.4 CargoItem (PostgreSQL)
```
CargoItem {
  id: UUID
  name: String
  category: Enum [SCIENTIFIC, GENERAL, HAZARDOUS]
  weightKg: Decimal
  status: Enum [PACKED, DISPATCHED, IN_TRANSIT, ARRIVED, STORED, CONSUMED]
  expedition: Expedition (FK)
  currentTransitLeg: TransitLeg (FK, nullable)
  currentStationLocation: Enum [MAITRI, BHARATI, IN_TRANSIT, INDIA] (nullable when in transit)
  linkedInventoryItem: InventoryItem (FK, nullable — set once stored)
}
```

### 3.5 InventoryItem (PostgreSQL)
```
InventoryItem {
  id: UUID
  name: String
  category: String
  station: Enum [MAITRI, BHARATI]
  quantity: Integer
  unit: String
  reorderThreshold: Integer
  expiryDate: Date (nullable)
  lastUpdated: DateTime
}
```

### 3.6 StatusPing (MongoDB — high frequency, append-only)
The shared tracking primitive referenced in Section 0.
```
StatusPing {
  _id: ObjectId
  entityType: Enum [PERSON, CARGO, VEHICLE]
  entityId: UUID (references Person.id or CargoItem.id)
  timestamp: DateTime
  latitude: Double (nullable)
  longitude: Double (nullable)
  statusNote: String (nullable — e.g. "checked in at Cape Town")
  source: Enum [MANUAL, SCHEDULED_CHECKIN, SYSTEM]
}
```
The **latest** ping per entity is what drives the "current location/status" fields on `Person`/`CargoItem` (denormalized for fast dashboard queries); the **full history** stays in MongoDB for the timeline views.

### 3.7 EmergencyIncident (PostgreSQL)
```
EmergencyIncident {
  id: UUID
  triggerType: Enum [MANUAL_SOS, MISSED_PING]
  triggeredBy: Person (FK, nullable if system-detected)
  relatedExpedition: Expedition (FK)
  status: Enum [ACTIVE, RESOLVED]
  lastKnownLatitude: Double
  lastKnownLongitude: Double
  triggeredAt: DateTime
  resolvedAt: DateTime (nullable)
  resolutionNotes: String (nullable)
}
```

### 3.8 EmergencyResponseLog (PostgreSQL)
```
EmergencyResponseLog {
  id: UUID
  incident: EmergencyIncident (FK)
  actionTaken: String
  performedBy: User (FK)
  timestamp: DateTime
}
```

### 3.9 User (PostgreSQL — auth)
```
User {
  id: UUID
  username: String
  passwordHash: String
  role: Enum [HQ_ADMIN, LOGISTICS_COORDINATOR, STATION_COMMANDER, EXPEDITION_MEMBER]
}
```

---

## 4. Module-by-Module Backend Behavior

### 4.1 Expedition Planning Service
- `POST /api/v1/expeditions` — create expedition + initial transit legs in one transaction
- `PUT /api/v1/expeditions/{id}/legs` — reorder/edit transit legs (validate sequence integrity — no overlapping `sequenceOrder`)
- `GET /api/v1/expeditions/{id}` — returns expedition with nested legs, assigned personnel, assigned cargo summary
- Business rule: an expedition cannot move to `ACTIVE` status unless it has at least one transit leg and at least one assigned person with `fitnessClearanceStatus = CLEARED`.

### 4.2 Status Tracking Service (shared/cross-cutting)
- `POST /api/v1/status-pings` — accepts a ping for any entity type; on save, updates the denormalized "current status/location" field on the corresponding `Person` or `CargoItem` record
- `GET /api/v1/status-pings/{entityType}/{entityId}/history` — returns full ping history from MongoDB for timeline views
- **Missed-ping detection job**: a scheduled task (Spring `@Scheduled`) runs every N minutes, checks for any `Person` marked `IN_TRANSIT` or `AT_STATION` whose last `StatusPing` is older than a configurable threshold (e.g., 24 hours) — if so, auto-creates an `EmergencyIncident` with `triggerType = MISSED_PING`.
- This job is the direct implementation of the "passive detection" concept from the frontend report, built using the adapter-pattern idea referenced in Section 0 — each future data source (manual check-in now, a GPS device later) implements a common `StatusProvider` interface, so adding a new source doesn't change this core job's logic.

### 4.3 Cargo Tracking Service
- `POST /api/v1/cargo` — create cargo item, initial status `PACKED`
  - **Duplicate-stock check**: before creation, query `InventoryItem` for matching `name`+`category` at the destination station; if found with sufficient quantity, return a warning payload (not a hard block) for the frontend's inline callout (Section 5.4 of frontend report)
- `PATCH /api/v1/cargo/{id}/status` — update lifecycle status; validates legal transitions only (e.g., cannot go from `PACKED` directly to `STORED`)
- On transition to `STORED`: automatically create or increment the linked `InventoryItem` quantity at the relevant station — this is the direct link between Cargo and Inventory modules.

### 4.4 Inventory Management Service
- `GET /api/v1/inventory?station={station}` — current stock, sorted by urgency (below-threshold items first)
- `PATCH /api/v1/inventory/{id}` — manual adjustment (with an audit log entry — who changed what, when)
- **Low-stock alert job**: scheduled check comparing `quantity` vs `reorderThreshold`; generates alert entries surfaced via the notification/activity feed endpoint
- **Expiry check job**: daily scheduled task flags items within 30 days of `expiryDate`

### 4.5 Personnel Movement Service
- `POST /api/v1/personnel/{id}/assign-expedition` — assigns a person to an expedition
- `PATCH /api/v1/personnel/{id}/status` — manual status update (used alongside automatic StatusPing-driven updates)
- `GET /api/v1/personnel/roster?expeditionId={id}` — roster view for the frontend table

### 4.6 Emergency Response Service
- `POST /api/v1/emergencies/sos` — manual trigger, requires `personId` (or vehicle/cargo entity) and current lat/long
- `GET /api/v1/emergencies/active` — polled by frontend for the persistent emergency banner (Section 5.2 of frontend report); consider WebSocket/SSE push instead of polling if time allows, for true real-time banner updates
- `PATCH /api/v1/emergencies/{id}/resolve` — requires `resolutionNotes`, sets `status = RESOLVED`, `resolvedAt = now()`
- `POST /api/v1/emergencies/{id}/response-log` — add a response action entry

### 4.7 Reports/Export Service
- `GET /api/v1/reports/expedition-summary/{id}` — aggregated data for export (CSV/PDF generation can happen frontend-side or via a lightweight backend export using a library like Apache POI/iText if needed)

---

## 5. Authentication & Authorization

- JWT-based stateless auth; token includes `userId` and `role`.
- Role-based endpoint guarding via Spring Security method annotations (`@PreAuthorize("hasRole('HQ_ADMIN')")` etc.)
- Suggested role permissions:

| Action | HQ Admin | Logistics Coordinator | Station Commander | Expedition Member |
|---|---|---|---|---|
| Create/edit expedition | ✅ | ✅ | ❌ | ❌ |
| Update cargo status | ✅ | ✅ | ✅ (own station) | ❌ |
| Adjust inventory | ✅ | ✅ | ✅ (own station) | ❌ |
| Trigger SOS | ✅ | ✅ | ✅ | ✅ |
| Resolve emergency | ✅ | ✅ | ✅ | ❌ |
| View dashboards | ✅ | ✅ | ✅ (own station) | ✅ (own profile/expedition) |

---

## 6. Validation & Business Rules Summary

- Cargo/Personnel status transitions must follow the defined lifecycle order — reject illegal jumps at the service layer, not just the frontend.
- An `EmergencyIncident` cannot be created if an active one already exists for the same person/entity (prevent duplicate spam from repeated missed pings).
- `TransitLeg.sequenceOrder` must be unique and contiguous within an expedition.
- Inventory quantity can never go negative — reject or clamp with a validation error.

---

## 7. Testing Expectations

- Unit tests (JUnit/Mockito) for all service-layer business rules, especially:
  - Status transition validation logic
  - Missed-ping → emergency creation job
  - Duplicate-stock detection logic
- Integration tests for the Cargo → Inventory linkage (storing cargo correctly updates inventory quantity)
- SonarQube pass required before merge (per your existing engineering practice)

---

## 8. What's Intentionally Deferred (Not in This Version)

- Offline/low-connectivity sync handling — to be specified in a follow-up report once finalized
- Real GPS/IoT hardware integration — current design assumes manual/scheduled check-ins as the StatusPing source; the `StatusProvider` interface (Section 4.2) is designed so a real hardware feed can be added later without restructuring the core system

---

## 9. Summary for the Developer / AI Assistant

Build each module as a clean Service + Repository pair behind REST controllers, with the **StatusPing/StatusTrackingService as the shared backbone** connecting Cargo, Personnel, and Emergency modules — this is the single most important architectural decision in the system and should not be duplicated per-module. Reference the BAS `assets-tracking-service` repo only for its adapter-pattern and current-vs-historical-position design ideas (see Section 0) — reimplement these ideas natively in Spring Boot; do not port or copy any of its Python code or schema directly.

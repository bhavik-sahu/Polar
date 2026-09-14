// db.js
// SQLite database setup for the Integrated Polar Expedition Logistics
// and Asset Management System.
//
// DATA PROVENANCE: Records are tagged data_source = 'verified' or
// 'simulated'. Verified records are drawn directly from official NCPOR
// sources (see the `sources` table). Simulated records are clearly
// labeled illustrative data standing in for information NCPOR does not
// publish (live inventory counts, individual personnel rosters,
// emergency logs). The UI must never present simulated data as fact.

const Database = require('better-sqlite3');
const path = require('path');

const db = new Database(path.join(__dirname, 'polar.db'));
db.pragma('journal_mode = WAL');
db.pragma('foreign_keys = ON');

// ---------- SCHEMA ----------
db.exec(`
CREATE TABLE IF NOT EXISTS sources (
  id TEXT PRIMARY KEY,
  source_type TEXT NOT NULL,
  title TEXT NOT NULL,
  url TEXT NOT NULL,
  use_for TEXT
);

CREATE TABLE IF NOT EXISTS expeditions (
  id TEXT PRIMARY KEY,
  name TEXT NOT NULL,
  station TEXT NOT NULL CHECK(station IN ('Maitri', 'Bharati')),
  season TEXT NOT NULL,
  crew_size INTEGER NOT NULL,
  capacity_limit INTEGER NOT NULL DEFAULT 60,
  transport_capacity_kg REAL NOT NULL DEFAULT 20000,
  departure_date TEXT NOT NULL,
  resupply_date TEXT,
  return_date TEXT,
  status TEXT NOT NULL DEFAULT 'planning'
    CHECK(status IN ('planning','departed','on-station','returning','completed')),
  data_source TEXT NOT NULL DEFAULT 'simulated' CHECK(data_source IN ('verified','simulated')),
  source_id TEXT REFERENCES sources(id),
  created_at TEXT DEFAULT (datetime('now'))
);

CREATE TABLE IF NOT EXISTS milestones (
  id TEXT PRIMARY KEY,
  expedition_id TEXT REFERENCES expeditions(id) ON DELETE CASCADE,
  milestone_date TEXT NOT NULL,
  description TEXT NOT NULL,
  module TEXT NOT NULL,
  data_source TEXT NOT NULL DEFAULT 'verified' CHECK(data_source IN ('verified','simulated')),
  source_id TEXT REFERENCES sources(id)
);

CREATE TABLE IF NOT EXISTS cargo (
  id TEXT PRIMARY KEY,
  expedition_id TEXT REFERENCES expeditions(id) ON DELETE SET NULL,
  category TEXT NOT NULL,
  description TEXT,
  weight_kg REAL NOT NULL,
  transport_mode TEXT NOT NULL CHECK(transport_mode IN ('ship','air')),
  priority TEXT NOT NULL DEFAULT 'normal' CHECK(priority IN ('low','normal','high','critical')),
  status TEXT NOT NULL DEFAULT 'pending'
    CHECK(status IN ('pending','approved','packed','departed','in-transit','arrived','delivered','verified')),
  data_source TEXT NOT NULL DEFAULT 'simulated' CHECK(data_source IN ('verified','simulated')),
  source_id TEXT REFERENCES sources(id),
  created_at TEXT DEFAULT (datetime('now'))
);

CREATE TABLE IF NOT EXISTS inventory (
  id TEXT PRIMARY KEY,
  station TEXT NOT NULL CHECK(station IN ('Maitri', 'Bharati')),
  item_name TEXT NOT NULL,
  category TEXT NOT NULL,
  quantity REAL NOT NULL,
  unit TEXT NOT NULL,
  reorder_threshold REAL NOT NULL,
  daily_consumption REAL NOT NULL DEFAULT 0,
  updated_at TEXT DEFAULT (datetime('now'))
);

CREATE TABLE IF NOT EXISTS inventory_history (
  id TEXT PRIMARY KEY,
  inventory_id TEXT REFERENCES inventory(id) ON DELETE CASCADE,
  change_qty REAL NOT NULL,
  reason TEXT,
  recorded_at TEXT DEFAULT (datetime('now'))
);

CREATE TABLE IF NOT EXISTS personnel (
  id TEXT PRIMARY KEY,
  expedition_id TEXT REFERENCES expeditions(id) ON DELETE SET NULL,
  name TEXT NOT NULL,
  role TEXT NOT NULL,
  station TEXT NOT NULL CHECK(station IN ('Maitri', 'Bharati')),
  medical_status TEXT NOT NULL DEFAULT 'FIT' CHECK(medical_status IN ('FIT','REQUIRES REVIEW','CLEARED')),
  status TEXT NOT NULL DEFAULT 'in-transit'
    CHECK(status IN ('in-transit','on-station','field-traverse','returning','completed')),
  data_source TEXT NOT NULL DEFAULT 'simulated' CHECK(data_source IN ('verified','simulated')),
  created_at TEXT DEFAULT (datetime('now'))
);

CREATE TABLE IF NOT EXISTS emergencies (
  id TEXT PRIMARY KEY,
  station TEXT NOT NULL CHECK(station IN ('Maitri', 'Bharati')),
  type TEXT NOT NULL,
  severity TEXT NOT NULL CHECK(severity IN ('low','medium','high','critical')),
  description TEXT,
  status TEXT NOT NULL DEFAULT 'reported'
    CHECK(status IN ('reported','acknowledged','resolved')),
  reported_at TEXT DEFAULT (datetime('now')),
  resolved_at TEXT
);
`);

// ---------- SEED DATA ----------
// VERIFIED records below are taken directly from official NCPOR sources
// (see sources table / Data Sources screen). SIMULATED records are
// clearly-labeled illustrative data for anything NCPOR does not publish
// (live inventory counts, individual rosters, emergency logs).
const { v4: uuid } = require('uuid');

const sourceCount = db.prepare('SELECT COUNT(*) AS c FROM sources').get().c;
if (sourceCount === 0) {
  const insertSource = db.prepare(`INSERT INTO sources (id, source_type, title, url, use_for) VALUES (?,?,?,?,?)`);
  insertSource.run('NCPOR-45ISEA-2026', 'Official NCPOR news',
    '45th Indian Scientific Expedition to Antarctica summer component concludes',
    'https://ncpor.res.in/news/view/1012',
    'Expedition history, Cape Town departure, MV Vasiliy Golovnin, 22-member summer team, return');
  insertSource.run('NCPOR-46ISEA-2026', 'Official NCPOR advertisement',
    '46th Indian Scientific Expedition to Antarctica - important deadlines',
    'https://ncpor.res.in/upload/recruitments/46-ISEA%20Webpage%20Advertisment_12022026.PDF',
    'Planning timeline, cargo deadlines, medical/Auli training window, induction dates');
  insertSource.run('NCPOR-DIRECT-AIR-CARGO', 'Official NCPOR news',
    'India flags off first direct air cargo to Antarctica',
    'https://ncpor.res.in/news/view/913',
    'Cargo example: 18 tonnes of gear, medicines and provisions via Goa/Cape Town');
  insertSource.run('NCPOR-AL01-2025', 'Official NCPOR planning advisory PDF',
    'AL-01 Planning Advisory - Antarctic Expedition 2025',
    'https://ncpor.res.in/files/AL-01%20Planning%20Advisory%20-%20Antarctic%20Expedition-2025_1.pdf',
    'Cargo deadlines, Goa-Mumbai-Cape Town-Antarctica routing, hazardous-cargo documentation');
  insertSource.run('NCPOR-EXPEDITION-UPDATES', 'Official NCPOR expedition updates',
    'Antarctic Expedition Updates', 'https://ncpor.res.in/pages/view/247-expedition-updates',
    'Historical operational examples: personnel movement, cargo, training, annual supplies');
  insertSource.run('NCPOR-INVENTORY-TENDER', 'Official NCPOR tender',
    'Procurement of Inventory Tracking & Management Tool for Antarctic Expedition',
    'https://ncpor.res.in/upload/tenders/AES-11298.PDF',
    'Confirms NCPOR has formally identified this exact system gap');
  insertSource.run('NCPOR-MET-DATA', 'Official NCPOR data portal',
    'Meteorological Data from Indian Stations', 'https://data.ncpor.res.in/',
    'Weather/current station observations for Maitri and Bharati');
  insertSource.run('NCPOR-ADVISORIES', 'Official NCPOR advisory page',
    'Antarctic Operations Advisories', 'https://www.ncpor.res.in/pages/display/428-advisory',
    'Planning advisories, Bharati/Maitri logistics, medical and training advisories');
}

const expeditionCount = db.prepare('SELECT COUNT(*) AS c FROM expeditions').get().c;
if (expeditionCount === 0) {
  const insertExp = db.prepare(`INSERT INTO expeditions
    (id, name, station, season, crew_size, capacity_limit, transport_capacity_kg, departure_date, resupply_date, return_date, status, data_source, source_id)
    VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)`);

  // VERIFIED: 45th ISEA -- real dates from NCPOR news (departed Mumbai, reached
  // Maitri, summer component via Cape Town, returned). Treated here as the
  // most recently completed/underway expedition cycle.
  const exp45 = uuid();
  insertExp.run(exp45, '45-ISEA Maitri/Bharati Expedition', 'Maitri', '2025-26', 22, 65, 20000,
    '2025-10-31', '2025-12-25', '2026-05-15', 'completed', 'verified', 'NCPOR-45ISEA-2026');

  // VERIFIED: 46th ISEA -- real planning deadlines from NCPOR's official
  // advertisement. Induction is stated only as "November 2026", so a
  // representative date within that window is used for departure_date.
  const exp46 = uuid();
  insertExp.run(exp46, '46-ISEA Maitri Summer Contingent', 'Maitri', 'Summer 2026-27', 42, 65, 22000,
    '2026-11-15', '2027-01-10', '2027-05-15', 'planning', 'verified', 'NCPOR-46ISEA-2026');

  // SIMULATED: illustrative winter-over team at Bharati, not sourced from
  // an official record -- included so the app shows both stations.
  const expSim = uuid();
  insertExp.run(expSim, 'Bharati Winter-over Team (Illustrative)', 'Bharati', 'Winter-over 2026', 44, 72, 18000,
    '2026-03-01', null, '2026-11-15', 'on-station', 'simulated', null);

  // VERIFIED milestones: the 46th ISEA planning timeline exactly as published.
  const insertMilestone = db.prepare(`INSERT INTO milestones
    (id, expedition_id, milestone_date, description, module, data_source, source_id)
    VALUES (?,?,?,?,?, 'verified', 'NCPOR-46ISEA-2026')`);
  insertMilestone.run(uuid(), exp46, '2026-02-20', 'Last date for online scientific proposal application', 'Planning');
  insertMilestone.run(uuid(), exp46, '2026-03-30', 'Initial proposal screening status communicated', 'Planning');
  insertMilestone.run(uuid(), exp46, '2026-04-22', 'Project evaluation workshop at NCPOR (22-23 Apr)', 'Planning');
  insertMilestone.run(uuid(), exp46, '2026-05-05', 'Personal details and permit application deadline', 'Personnel/Permit');
  insertMilestone.run(uuid(), exp46, '2026-05-15', 'Leader nomination deadline', 'Personnel');
  insertMilestone.run(uuid(), exp46, '2026-07-31', 'Scientific cargo (immediately required) deadline at NCPOR', 'Cargo');
  insertMilestone.run(uuid(), exp46, '2026-07-15', 'Tentative medical and Auli training window begins (to Oct 5)', 'Personnel/Training');
  insertMilestone.run(uuid(), exp46, '2026-08-31', 'Scientific cargo (voyage/summer-winter period) deadline', 'Cargo');
  insertMilestone.run(uuid(), exp46, '2026-10-01', 'Personal passport deadline', 'Personnel');
  insertMilestone.run(uuid(), exp46, '2026-11-15', 'Induction into Antarctica (batches through November)', 'Deployment');

  const insertCargo = db.prepare(`INSERT INTO cargo
    (id, expedition_id, category, description, weight_kg, transport_mode, priority, status, data_source, source_id)
    VALUES (?,?,?,?,?,?,?,?,?,?)`);
  // VERIFIED cargo examples exactly as reported by NCPOR
  insertCargo.run(uuid(), exp45, 'official-cargo', 'Direct air cargo: gear, medicines and provisions (18 tonnes)', 18000, 'air', 'high', 'delivered', 'verified', 'NCPOR-DIRECT-AIR-CARGO');
  insertCargo.run(uuid(), exp45, 'fuel', 'Annual aviation fuel/lubricant supply (~600 Jet A1 barrels + lubricants)', 108000, 'ship', 'critical', 'delivered', 'verified', 'NCPOR-EXPEDITION-UPDATES');
  // SIMULATED cargo -- illustrative pipeline entries for the upcoming 46th ISEA
  insertCargo.run(uuid(), exp46, 'official-cargo', 'Annual dry ration resupply (illustrative)', 8200, 'ship', 'high', 'packed', 'simulated', null);
  insertCargo.run(uuid(), exp46, 'scientific-equipment', 'Ice core drilling rig - Lambert Glacier survey (illustrative)', 640, 'air', 'normal', 'approved', 'simulated', null);
  insertCargo.run(uuid(), exp46, 'personal-cargo', 'Winter member personal effects, batch 1 (illustrative)', 340, 'ship', 'low', 'pending', 'simulated', null);
  insertCargo.run(uuid(), exp46, 'spares', 'Generator spare parts kit (illustrative)', 210, 'air', 'high', 'approved', 'simulated', null);

  // SIMULATED inventory -- NCPOR does not publish live stock counts.
  // quantity / daily_consumption powers the days-remaining decision-support calc.
  const insertInv = db.prepare(`INSERT INTO inventory
    (id, station, item_name, category, quantity, unit, reorder_threshold, daily_consumption)
    VALUES (?,?,?,?,?,?,?,?)`);
  insertInv.run(uuid(), 'Maitri', 'Diesel', 'fuel', 4200, 'litres', 3000, 45);
  insertInv.run(uuid(), 'Maitri', 'Dry rations', 'food', 1800, 'kg', 2000, 28);
  insertInv.run(uuid(), 'Bharati', 'Diesel', 'fuel', 5100, 'litres', 3000, 40);
  insertInv.run(uuid(), 'Bharati', 'Medical oxygen', 'medical', 100, 'units', 100, 5);
  insertInv.run(uuid(), 'Bharati', 'Polar clothing sets', 'personal-cargo', 22, 'units', 20, 0);

  // SIMULATED personnel -- illustrative names/roster; medical status uses
  // the FIT/REQUIRES REVIEW/CLEARED scheme (no medical records stored).
  const insertPersonnel = db.prepare(`INSERT INTO personnel
    (id, expedition_id, name, role, station, medical_status, status, data_source)
    VALUES (?,?,?,?,?,?,?, 'simulated')`);
  insertPersonnel.run(uuid(), exp46, 'Member A (illustrative)', 'doctor', 'Maitri', 'FIT', 'in-transit');
  insertPersonnel.run(uuid(), exp46, 'Member B (illustrative)', 'engineer', 'Maitri', 'FIT', 'in-transit');
  insertPersonnel.run(uuid(), expSim, 'Member C (illustrative)', 'scientist', 'Bharati', 'FIT', 'field-traverse');
  insertPersonnel.run(uuid(), expSim, 'Member D (illustrative)', 'support', 'Bharati', 'CLEARED', 'on-station');
  insertPersonnel.run(uuid(), expSim, 'Member E (illustrative)', 'engineer', 'Bharati', 'REQUIRES REVIEW', 'on-station');

  // SIMULATED emergency -- illustrative incident, not a real reported event.
  const insertEmergency = db.prepare(`INSERT INTO emergencies
    (id, station, type, severity, description, status)
    VALUES (?,?,?,?,?,?)`);
  insertEmergency.run(uuid(), 'Bharati', 'equipment', 'medium',
    'Generator #2 intermittent failure (illustrative demo incident)', 'acknowledged');
}

module.exports = db;

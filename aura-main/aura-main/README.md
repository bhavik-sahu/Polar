# Integrated Polar Expedition Logistics and Asset Management System

Built for **SIH26062** — Ministry of Earth Sciences (NCPOR).

A web app covering the 5 core modules: Expedition Planning, Cargo & Asset
Tracking, Inventory Management, Personnel Movement Tracking, and Emergency
Response — plus a unified Dashboard and a **Data Sources** screen.

## Data provenance — read this before your demo
Every record is tagged **VERIFIED** or **SIMULATED**, shown as a small tag
next to the record in every table:
- **VERIFIED** — taken directly from official NCPOR documents (45th/46th
  ISEA news releases, the 46-ISEA planning advertisement, the direct
  air-cargo announcement, NCPOR's own inventory-tracking tender). Full
  source list with links is on the **Data Sources** screen in the app.
- **SIMULATED** — clearly-labeled illustrative data standing in for
  anything NCPOR doesn't publish (live inventory counts, individual
  personnel names, emergency incidents). This is deliberate and honest —
  say so directly if a judge asks.

This separation is itself worth mentioning in your pitch: it shows you
did real research rather than presenting placeholder data as fact.

## Stack
- **Backend:** Node.js + Express + SQLite (via `better-sqlite3`) — zero
  external database setup required.
- **Frontend:** Plain HTML/CSS/JavaScript (no build step, no framework) —
  runs by opening a file server, no `npm install` needed on the frontend.

## Project structure
```
polar-logistics/
├── backend/
│   ├── server.js          # Express app entry point
│   ├── db.js               # SQLite schema + seed data
│   ├── routes/
│   │   ├── expeditions.js
│   │   ├── cargo.js
│   │   ├── inventory.js
│   │   ├── personnel.js
│   │   ├── emergencies.js
│   │   └── dashboard.js
│   └── package.json
└── frontend/
    ├── index.html
    ├── style.css
    ├── config.js            # set API_BASE here for deployment
    ├── api.js
    ├── app.js
    └── views/
        ├── dashboard.js
        ├── expeditions.js
        ├── cargo.js
        ├── inventory.js
        ├── personnel.js
        └── emergencies.js
```

## Run locally

**1. Backend**
```bash
cd backend
npm install
npm start
```
This starts the API at `http://localhost:4000`. On first run it creates
`polar.db` (SQLite file) and seeds it with sample expeditions, cargo,
inventory, personnel, and one emergency record so the dashboard isn't empty.

**2. Frontend**
The frontend is static files — no build step. Easiest options:
```bash
cd frontend
npx serve .
# or: python3 -m http.server 5500
```
Then open the printed URL (e.g. `http://localhost:5500`) in your browser.
It's already configured to call the backend at `http://localhost:4000/api`
(see `frontend/config.js`).

## Push to GitHub

```bash
cd polar-logistics
git init
git add .
git commit -m "Initial commit: Integrated Polar Expedition Logistics System"
git branch -M main
git remote add origin https://github.com/<your-username>/<repo-name>.git
git push -u origin main
```
Create the empty repo on GitHub first (github.com → New repository), then
run the commands above with your actual repo URL.

## Make it public / deployable

You need to host the **backend** and **frontend** separately (or together —
see note at the end). Both have free tiers suitable for a hackathon demo.

### Deploy the backend (Render — easiest free option)
1. Go to [render.com](https://render.com) → sign in with GitHub.
2. **New → Web Service** → select your GitHub repo.
3. Set **Root Directory** to `backend`.
4. Build Command: `npm install`
5. Start Command: `npm start`
6. Deploy. Render gives you a URL like `https://polar-logistics-backend.onrender.com`.

> Note: `better-sqlite3` compiles a native module — Render's default Node
> build environment handles this fine. If it fails, add a `render.yaml` or
> switch the DB to Postgres later (see "Scaling notes" below).

### Deploy the frontend (Vercel or Netlify — both free)
1. Go to [vercel.com](https://vercel.com) (or netlify.com) → New Project →
   import the same GitHub repo.
2. Set **Root Directory** to `frontend`.
3. No build command needed (static site) — Framework preset: "Other".
4. Before deploying, update `frontend/config.js`:
   ```js
   window.API_BASE = "https://polar-logistics-backend.onrender.com/api";
   ```
   Commit and push this change so the deployed frontend points at your live
   backend instead of `localhost`.
5. Deploy. You'll get a public URL like `https://polar-logistics.vercel.app`
   — this is what you share/demo.

### One-service alternative (simpler, one URL)
If you'd rather avoid two separate deployments, have Express serve the
frontend as static files too:
```js
// add near the top of backend/server.js
app.use(express.static(path.join(__dirname, '../frontend')));
```
Then deploy only the `backend` folder (with the whole repo available so the
relative `../frontend` path resolves) as a single Render/Railway web
service. Simpler for a demo, but two-service setup above scales better and
is more typical of real deployments — good talking point for judges either way.

## Sample data
The database seeds itself automatically on first run with 2 sample
expeditions (Maitri summer team, Bharati winter-over), cargo records,
inventory items (including a couple already near their reorder threshold
so the dashboard alert banner has something to show), personnel, and one
open emergency — so your demo has content immediately without manual entry.

## API quick reference
All endpoints are under `/api`:
- `GET/POST /expeditions`, `PUT/:id`, `PATCH /:id/status`, `GET /:id/timeline`
- `GET/POST /cargo`, `PATCH /:id/assign`, `PATCH /:id/status`
- `GET/POST /inventory`, `PATCH /:id/adjust`, `GET /alerts/low-stock`
- `GET/POST /personnel`, `PATCH /:id/status`, `GET /headcount`
- `GET/POST /emergencies`, `PATCH /:id/status`, `GET /active`
- `GET /dashboard` — unified snapshot for the home screen

## Scaling notes (good to mention to judges)
- Swapping SQLite for Postgres later only requires changing `db.js` —
  route files use plain SQL-ish prepared statements, easy to port.
- Auth is not implemented (fine for a hackathon demo) — a real deployment
  would add role-based login (NCPOR HQ admin vs. station lead vs. viewer).
- Emergency response could integrate with satellite comms APIs in a real
  deployment; here it's a self-contained workflow ready to plug that in.

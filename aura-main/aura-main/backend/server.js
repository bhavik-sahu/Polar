// server.js
// Entry point for the Integrated Polar Expedition Logistics and
// Asset Management System API (SIH26062).

const express = require('express');
const cors = require('cors');

require('./db'); // ensures schema + seed data are initialized on boot

const expeditionsRouter = require('./routes/expeditions');
const cargoRouter = require('./routes/cargo');
const inventoryRouter = require('./routes/inventory');
const personnelRouter = require('./routes/personnel');
const emergenciesRouter = require('./routes/emergencies');
const dashboardRouter = require('./routes/dashboard');
const sourcesRouter = require('./routes/sources');

const app = express();
app.use(cors());
app.use(express.json());

// Simple request log -- helpful during demo/debugging
app.use((req, res, next) => {
  console.log(`${new Date().toISOString()} ${req.method} ${req.path}`);
  next();
});

app.get('/api/health', (req, res) => res.json({ status: 'ok', service: 'polar-logistics-backend' }));

app.use('/api/expeditions', expeditionsRouter);
app.use('/api/cargo', cargoRouter);
app.use('/api/inventory', inventoryRouter);
app.use('/api/personnel', personnelRouter);
app.use('/api/emergencies', emergenciesRouter);
app.use('/api/dashboard', dashboardRouter);
app.use('/api/sources', sourcesRouter);

// Fallback 404 for unmatched API routes
app.use('/api', (req, res) => res.status(404).json({ error: 'Route not found' }));

const PORT = process.env.PORT || 4000;
app.listen(PORT, () => {
  console.log(`Polar Logistics API running on http://localhost:${PORT}`);
});

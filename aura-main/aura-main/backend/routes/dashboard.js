// routes/dashboard.js
// Unified snapshot across all modules -- the "Integrated" view the
// problem statement asks for. Separates verified vs simulated data
// counts so the UI can be transparent about data provenance.

const express = require('express');
const db = require('../db');

const router = express.Router();

function daysUntilNextResupply(station) {
  const today = new Date().toISOString().slice(0, 10);
  const row = db.prepare(`
    SELECT MIN(COALESCE(resupply_date, departure_date)) as next_date
    FROM expeditions
    WHERE station = ? AND status != 'completed'
      AND COALESCE(resupply_date, departure_date) >= ?
  `).get(station, today);
  if (!row || !row.next_date) return null;
  return Math.round((new Date(row.next_date) - new Date(today)) / (1000 * 60 * 60 * 24));
}

router.get('/', (req, res) => {
  const expeditions = db.prepare("SELECT * FROM expeditions WHERE status != 'completed' ORDER BY departure_date").all();
  const cargoInTransit = db.prepare("SELECT COUNT(*) as c FROM cargo WHERE status IN ('in-transit','departed')").get().c;
  const cargoPending = db.prepare("SELECT COUNT(*) as c FROM cargo WHERE status IN ('pending','approved','packed')").get().c;

  const inventoryRows = db.prepare('SELECT * FROM inventory').all().map(row => {
    const daysRemaining = row.daily_consumption > 0 ? Math.floor(row.quantity / row.daily_consumption) : null;
    const daysUntilResupply = daysUntilNextResupply(row.station);
    const critical = daysRemaining != null && daysUntilResupply != null && daysRemaining < daysUntilResupply;
    return { ...row, low_stock: row.quantity <= row.reorder_threshold, days_remaining: daysRemaining, days_until_next_resupply: daysUntilResupply, critical };
  });
  const lowStockAlerts = inventoryRows.filter(r => r.low_stock);
  const criticalAlerts = inventoryRows.filter(r => r.critical);

  const personnelByStation = db.prepare(`SELECT station, status, COUNT(*) as count FROM personnel GROUP BY station, status`).all();
  const headcount = {};
  for (const row of personnelByStation) {
    if (!headcount[row.station]) headcount[row.station] = { 'in-transit': 0, 'on-station': 0, 'field-traverse': 0, returning: 0, completed: 0, total: 0 };
    headcount[row.station][row.status] = row.count;
    headcount[row.station].total += row.count;
  }

  const activeEmergencies = db.prepare("SELECT * FROM emergencies WHERE status != 'resolved' ORDER BY severity DESC").all();

  const verifiedCount = db.prepare(`
    SELECT
      (SELECT COUNT(*) FROM expeditions WHERE data_source='verified') +
      (SELECT COUNT(*) FROM cargo WHERE data_source='verified') +
      (SELECT COUNT(*) FROM milestones WHERE data_source='verified') as c
  `).get().c;
  const simulatedCount = db.prepare(`
    SELECT
      (SELECT COUNT(*) FROM expeditions WHERE data_source='simulated') +
      (SELECT COUNT(*) FROM cargo WHERE data_source='simulated') +
      (SELECT COUNT(*) FROM personnel WHERE data_source='simulated') +
      (SELECT COUNT(*) FROM inventory) +
      (SELECT COUNT(*) FROM emergencies) as c
  `).get().c;

  // Station capacity context (real NCPOR-published figures)
  const stationCapacity = {
    Maitri: { year_round: 25, summer_total: 65, note: 'Schirmacher Oasis' },
    Bharati: { year_round: 47, summer_total: 72, note: 'Larsemann Hills' },
  };

  res.json({
    active_expeditions: expeditions,
    cargo_summary: { in_transit: cargoInTransit, pending: cargoPending },
    low_stock_alerts: lowStockAlerts,
    critical_alerts: criticalAlerts,
    personnel_headcount: headcount,
    active_emergencies: activeEmergencies,
    station_capacity: stationCapacity,
    data_provenance: { verified_records: verifiedCount, simulated_records: simulatedCount },
    generated_at: new Date().toISOString()
  });
});

module.exports = router;

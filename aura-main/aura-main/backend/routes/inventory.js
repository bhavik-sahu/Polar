// routes/inventory.js
// Module 3: Inventory Management
// Decision-support calculation: days_remaining = quantity / daily_consumption,
// compared against days until the station's next scheduled resupply --
// this answers "will we run out before the next resupply window?" rather
// than just "is stock below a threshold."

const express = require('express');
const { v4: uuid } = require('uuid');
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
  const diffMs = new Date(row.next_date) - new Date(today);
  return Math.round(diffMs / (1000 * 60 * 60 * 24));
}

function enrich(row) {
  const daysRemaining = row.daily_consumption > 0 ? Math.floor(row.quantity / row.daily_consumption) : null;
  const daysUntilResupply = daysUntilNextResupply(row.station);
  const willRunOutBeforeResupply = daysRemaining != null && daysUntilResupply != null && daysRemaining < daysUntilResupply;
  return {
    ...row,
    low_stock: row.quantity <= row.reorder_threshold,
    days_remaining: daysRemaining,
    days_until_next_resupply: daysUntilResupply,
    critical: willRunOutBeforeResupply,
  };
}

router.get('/', (req, res) => {
  const { station } = req.query;
  let rows;
  if (station) {
    rows = db.prepare('SELECT * FROM inventory WHERE station = ? ORDER BY item_name').all(station);
  } else {
    rows = db.prepare('SELECT * FROM inventory ORDER BY station, item_name').all();
  }
  res.json(rows.map(enrich));
});

router.get('/alerts/low-stock', (req, res) => {
  const rows = db.prepare('SELECT * FROM inventory').all().map(enrich);
  res.json(rows.filter(r => r.low_stock || r.critical));
});

router.get('/:id', (req, res) => {
  const item = db.prepare('SELECT * FROM inventory WHERE id = ?').get(req.params.id);
  if (!item) return res.status(404).json({ error: 'Inventory item not found' });
  const history = db.prepare('SELECT * FROM inventory_history WHERE inventory_id = ? ORDER BY recorded_at DESC').all(req.params.id);
  res.json({ ...enrich(item), history });
});

router.post('/', (req, res) => {
  const { station, item_name, category, quantity, unit, reorder_threshold, daily_consumption } = req.body;
  if (!station || !item_name || !category || quantity == null || !unit || reorder_threshold == null) {
    return res.status(400).json({ error: 'Missing required fields: station, item_name, category, quantity, unit, reorder_threshold' });
  }
  const id = uuid();
  db.prepare(`INSERT INTO inventory (id, station, item_name, category, quantity, unit, reorder_threshold, daily_consumption)
    VALUES (?,?,?,?,?,?,?,?)`)
    .run(id, station, item_name, category, quantity, unit, reorder_threshold, daily_consumption || 0);
  res.status(201).json(enrich(db.prepare('SELECT * FROM inventory WHERE id = ?').get(id)));
});

router.patch('/:id/adjust', (req, res) => {
  const { change_qty, reason } = req.body;
  const item = db.prepare('SELECT * FROM inventory WHERE id = ?').get(req.params.id);
  if (!item) return res.status(404).json({ error: 'Inventory item not found' });
  if (change_qty == null) return res.status(400).json({ error: 'change_qty is required (negative = consumption, positive = restock)' });

  const newQty = item.quantity + change_qty;
  if (newQty < 0) return res.status(400).json({ error: 'Resulting quantity cannot be negative' });

  db.prepare("UPDATE inventory SET quantity = ?, updated_at = datetime('now') WHERE id = ?").run(newQty, req.params.id);
  db.prepare('INSERT INTO inventory_history (id, inventory_id, change_qty, reason) VALUES (?,?,?,?)')
    .run(uuid(), req.params.id, change_qty, reason || null);

  res.json(enrich(db.prepare('SELECT * FROM inventory WHERE id = ?').get(req.params.id)));
});

router.put('/:id', (req, res) => {
  const existing = db.prepare('SELECT * FROM inventory WHERE id = ?').get(req.params.id);
  if (!existing) return res.status(404).json({ error: 'Inventory item not found' });
  const merged = { ...existing, ...req.body };
  db.prepare(`UPDATE inventory SET item_name=?, category=?, unit=?, reorder_threshold=?, daily_consumption=?, updated_at=datetime('now') WHERE id=?`)
    .run(merged.item_name, merged.category, merged.unit, merged.reorder_threshold, merged.daily_consumption, req.params.id);
  res.json(enrich(db.prepare('SELECT * FROM inventory WHERE id = ?').get(req.params.id)));
});

router.delete('/:id', (req, res) => {
  const info = db.prepare('DELETE FROM inventory WHERE id = ?').run(req.params.id);
  if (info.changes === 0) return res.status(404).json({ error: 'Inventory item not found' });
  res.status(204).send();
});

module.exports = router;

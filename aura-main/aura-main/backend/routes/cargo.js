// routes/cargo.js
// Module 2: Cargo & Asset Tracking
// Lifecycle: pending -> approved -> packed -> departed -> in-transit -> arrived -> delivered -> verified
// (matches NCPOR's documented cargo chain-of-custody process)

const express = require('express');
const { v4: uuid } = require('uuid');
const db = require('../db');

const router = express.Router();
const STATUS_FLOW = ['pending', 'approved', 'packed', 'departed', 'in-transit', 'arrived', 'delivered', 'verified'];

router.get('/', (req, res) => {
  const { expedition_id, status } = req.query;
  let query = 'SELECT * FROM cargo WHERE 1=1';
  const params = [];
  if (expedition_id) { query += ' AND expedition_id = ?'; params.push(expedition_id); }
  if (status) { query += ' AND status = ?'; params.push(status); }
  query += ' ORDER BY created_at DESC';
  res.json(db.prepare(query).all(...params));
});

router.get('/:id', (req, res) => {
  const row = db.prepare('SELECT * FROM cargo WHERE id = ?').get(req.params.id);
  if (!row) return res.status(404).json({ error: 'Cargo item not found' });
  res.json(row);
});

router.post('/', (req, res) => {
  const { expedition_id, category, description, weight_kg, transport_mode, priority } = req.body;
  if (!category || !weight_kg || !transport_mode) {
    return res.status(400).json({ error: 'Missing required fields: category, weight_kg, transport_mode' });
  }
  if (!['ship', 'air'].includes(transport_mode)) {
    return res.status(400).json({ error: "transport_mode must be 'ship' or 'air'" });
  }

  let capacityWarning = null;
  if (expedition_id) {
    const exp = db.prepare('SELECT * FROM expeditions WHERE id = ?').get(expedition_id);
    if (!exp) return res.status(400).json({ error: 'expedition_id does not match any expedition' });

    // Transport capacity conflict detection (from planning module requirements)
    const currentWeight = db.prepare(
      `SELECT COALESCE(SUM(weight_kg),0) as total FROM cargo WHERE expedition_id = ?`
    ).get(expedition_id).total;
    if (currentWeight + Number(weight_kg) > exp.transport_capacity_kg) {
      capacityWarning = `Transport capacity conflict: assigning this item brings total to ${currentWeight + Number(weight_kg)}kg, exceeding the expedition's ${exp.transport_capacity_kg}kg capacity`;
    }
  }

  const id = uuid();
  db.prepare(`INSERT INTO cargo (id, expedition_id, category, description, weight_kg, transport_mode, priority, status, data_source)
    VALUES (?,?,?,?,?,?,?, 'pending', 'simulated')`)
    .run(id, expedition_id || null, category, description || '', weight_kg, transport_mode, priority || 'normal');

  const created = db.prepare('SELECT * FROM cargo WHERE id = ?').get(id);
  res.status(201).json(capacityWarning ? { ...created, warning: capacityWarning } : created);
});

router.patch('/:id/assign', (req, res) => {
  const { expedition_id } = req.body;
  const cargo = db.prepare('SELECT * FROM cargo WHERE id = ?').get(req.params.id);
  if (!cargo) return res.status(404).json({ error: 'Cargo item not found' });

  let capacityWarning = null;
  if (expedition_id) {
    const exp = db.prepare('SELECT * FROM expeditions WHERE id = ?').get(expedition_id);
    if (!exp) return res.status(400).json({ error: 'expedition_id does not match any expedition' });
    const currentWeight = db.prepare(
      `SELECT COALESCE(SUM(weight_kg),0) as total FROM cargo WHERE expedition_id = ? AND id != ?`
    ).get(expedition_id, req.params.id).total;
    if (currentWeight + cargo.weight_kg > exp.transport_capacity_kg) {
      capacityWarning = `Transport capacity conflict: this expedition's assigned cargo would total ${currentWeight + cargo.weight_kg}kg, exceeding its ${exp.transport_capacity_kg}kg capacity`;
    }
  }

  db.prepare('UPDATE cargo SET expedition_id = ? WHERE id = ?').run(expedition_id || null, req.params.id);
  const updated = db.prepare('SELECT * FROM cargo WHERE id = ?').get(req.params.id);
  res.json(capacityWarning ? { ...updated, warning: capacityWarning } : updated);
});

router.patch('/:id/status', (req, res) => {
  const { status } = req.body;
  const cargo = db.prepare('SELECT * FROM cargo WHERE id = ?').get(req.params.id);
  if (!cargo) return res.status(404).json({ error: 'Cargo item not found' });

  if (!STATUS_FLOW.includes(status)) {
    return res.status(400).json({ error: `Invalid status. Must be one of: ${STATUS_FLOW.join(', ')}` });
  }
  const currentIdx = STATUS_FLOW.indexOf(cargo.status);
  const nextIdx = STATUS_FLOW.indexOf(status);
  if (nextIdx < currentIdx) {
    return res.status(400).json({ error: `Cannot move status backward from '${cargo.status}' to '${status}'` });
  }

  db.prepare('UPDATE cargo SET status = ? WHERE id = ?').run(status, req.params.id);
  res.json(db.prepare('SELECT * FROM cargo WHERE id = ?').get(req.params.id));
});

router.delete('/:id', (req, res) => {
  const info = db.prepare('DELETE FROM cargo WHERE id = ?').run(req.params.id);
  if (info.changes === 0) return res.status(404).json({ error: 'Cargo item not found' });
  res.status(204).send();
});

module.exports = router;

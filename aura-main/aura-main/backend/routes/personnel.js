// routes/personnel.js
// Module 4: Personnel Movement Tracking
// Movement states: in-transit -> on-station -> field-traverse -> returning -> completed
// medical_status uses FIT/REQUIRES REVIEW/CLEARED only -- no medical records stored,
// per the privacy guidance in the research (avoid exposing sensitive medical info).

const express = require('express');
const { v4: uuid } = require('uuid');
const db = require('../db');

const router = express.Router();
const STATUS_FLOW = ['in-transit', 'on-station', 'field-traverse', 'returning', 'completed'];
const MEDICAL_STATUSES = ['FIT', 'REQUIRES REVIEW', 'CLEARED'];

router.get('/', (req, res) => {
  const { expedition_id, station } = req.query;
  let query = 'SELECT * FROM personnel WHERE 1=1';
  const params = [];
  if (expedition_id) { query += ' AND expedition_id = ?'; params.push(expedition_id); }
  if (station) { query += ' AND station = ?'; params.push(station); }
  query += ' ORDER BY name';
  res.json(db.prepare(query).all(...params));
});

router.get('/headcount', (req, res) => {
  const rows = db.prepare(`
    SELECT station, status, COUNT(*) as count
    FROM personnel
    GROUP BY station, status
  `).all();

  const summary = {};
  for (const row of rows) {
    if (!summary[row.station]) summary[row.station] = { 'in-transit': 0, 'on-station': 0, 'field-traverse': 0, returning: 0, completed: 0, total: 0 };
    summary[row.station][row.status] = row.count;
    summary[row.station].total += row.count;
  }
  res.json(summary);
});

router.get('/:id', (req, res) => {
  const row = db.prepare('SELECT * FROM personnel WHERE id = ?').get(req.params.id);
  if (!row) return res.status(404).json({ error: 'Person not found' });
  res.json(row);
});

router.post('/', (req, res) => {
  const { expedition_id, name, role, station, medical_status } = req.body;
  if (!name || !role || !station) {
    return res.status(400).json({ error: 'Missing required fields: name, role, station' });
  }
  if (medical_status && !MEDICAL_STATUSES.includes(medical_status)) {
    return res.status(400).json({ error: `medical_status must be one of: ${MEDICAL_STATUSES.join(', ')}` });
  }

  if (expedition_id) {
    const exp = db.prepare('SELECT * FROM expeditions WHERE id = ?').get(expedition_id);
    if (!exp) return res.status(400).json({ error: 'expedition_id does not match any expedition' });
    const currentCount = db.prepare('SELECT COUNT(*) as c FROM personnel WHERE expedition_id = ?').get(expedition_id).c;
    if (currentCount >= exp.crew_size) {
      return res.status(409).json({ error: `Expedition roster already at planned crew size (${exp.crew_size})` });
    }
  }

  const id = uuid();
  db.prepare(`INSERT INTO personnel (id, expedition_id, name, role, station, medical_status, status, data_source)
    VALUES (?,?,?,?,?,?, 'in-transit', 'simulated')`)
    .run(id, expedition_id || null, name, role, station, medical_status || 'FIT');

  res.status(201).json(db.prepare('SELECT * FROM personnel WHERE id = ?').get(id));
});

router.patch('/:id/status', (req, res) => {
  const { status } = req.body;
  const person = db.prepare('SELECT * FROM personnel WHERE id = ?').get(req.params.id);
  if (!person) return res.status(404).json({ error: 'Person not found' });

  if (!STATUS_FLOW.includes(status)) {
    return res.status(400).json({ error: `Invalid status. Must be one of: ${STATUS_FLOW.join(', ')}` });
  }

  db.prepare('UPDATE personnel SET status = ? WHERE id = ?').run(status, req.params.id);
  res.json(db.prepare('SELECT * FROM personnel WHERE id = ?').get(req.params.id));
});

router.patch('/:id/medical-status', (req, res) => {
  const { medical_status } = req.body;
  const person = db.prepare('SELECT * FROM personnel WHERE id = ?').get(req.params.id);
  if (!person) return res.status(404).json({ error: 'Person not found' });
  if (!MEDICAL_STATUSES.includes(medical_status)) {
    return res.status(400).json({ error: `medical_status must be one of: ${MEDICAL_STATUSES.join(', ')}` });
  }
  db.prepare('UPDATE personnel SET medical_status = ? WHERE id = ?').run(medical_status, req.params.id);
  res.json(db.prepare('SELECT * FROM personnel WHERE id = ?').get(req.params.id));
});

router.delete('/:id', (req, res) => {
  const info = db.prepare('DELETE FROM personnel WHERE id = ?').run(req.params.id);
  if (info.changes === 0) return res.status(404).json({ error: 'Person not found' });
  res.status(204).send();
});

module.exports = router;

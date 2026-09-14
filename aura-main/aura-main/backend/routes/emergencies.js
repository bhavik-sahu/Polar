// routes/emergencies.js
// Module 5: Emergency Response
// - Report/log emergency events (type, severity)
// - Escalation workflow (reported -> acknowledged -> resolved)
// - Status visibility for station leads

const express = require('express');
const { v4: uuid } = require('uuid');
const db = require('../db');

const router = express.Router();
const STATUS_FLOW = ['reported', 'acknowledged', 'resolved'];
const SEVERITIES = ['low', 'medium', 'high', 'critical'];

// GET all emergencies (optionally filter by station or status) -- most recent first
router.get('/', (req, res) => {
  const { station, status } = req.query;
  let query = 'SELECT * FROM emergencies WHERE 1=1';
  const params = [];
  if (station) { query += ' AND station = ?'; params.push(station); }
  if (status) { query += ' AND status = ?'; params.push(status); }
  query += ' ORDER BY reported_at DESC';
  res.json(db.prepare(query).all(...params));
});

// GET only active (unresolved) emergencies -- powers dashboard banner
router.get('/active', (req, res) => {
  const rows = db.prepare("SELECT * FROM emergencies WHERE status != 'resolved' ORDER BY severity DESC, reported_at DESC").all();
  res.json(rows);
});

// GET single emergency
router.get('/:id', (req, res) => {
  const row = db.prepare('SELECT * FROM emergencies WHERE id = ?').get(req.params.id);
  if (!row) return res.status(404).json({ error: 'Emergency record not found' });
  res.json(row);
});

// POST report a new emergency
router.post('/', (req, res) => {
  const { station, type, severity, description } = req.body;
  if (!station || !type || !severity) {
    return res.status(400).json({ error: 'Missing required fields: station, type, severity' });
  }
  if (!SEVERITIES.includes(severity)) {
    return res.status(400).json({ error: `severity must be one of: ${SEVERITIES.join(', ')}` });
  }

  const id = uuid();
  db.prepare(`INSERT INTO emergencies (id, station, type, severity, description, status)
    VALUES (?,?,?,?,?, 'reported')`)
    .run(id, station, type, severity, description || '');

  res.status(201).json(db.prepare('SELECT * FROM emergencies WHERE id = ?').get(id));
});

// PATCH escalation workflow: reported -> acknowledged -> resolved
router.patch('/:id/status', (req, res) => {
  const { status } = req.body;
  const emergency = db.prepare('SELECT * FROM emergencies WHERE id = ?').get(req.params.id);
  if (!emergency) return res.status(404).json({ error: 'Emergency record not found' });

  if (!STATUS_FLOW.includes(status)) {
    return res.status(400).json({ error: `Invalid status. Must be one of: ${STATUS_FLOW.join(', ')}` });
  }
  const currentIdx = STATUS_FLOW.indexOf(emergency.status);
  const nextIdx = STATUS_FLOW.indexOf(status);
  if (nextIdx < currentIdx) {
    return res.status(400).json({ error: `Cannot move status backward from '${emergency.status}' to '${status}'` });
  }

  const resolvedAt = status === 'resolved' ? new Date().toISOString() : emergency.resolved_at;
  db.prepare('UPDATE emergencies SET status = ?, resolved_at = ? WHERE id = ?')
    .run(status, resolvedAt, req.params.id);

  res.json(db.prepare('SELECT * FROM emergencies WHERE id = ?').get(req.params.id));
});

module.exports = router;

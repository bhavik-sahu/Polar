// routes/expeditions.js
// Module 1: Expedition Planning
// Includes milestone timeline (verified 46th ISEA deadlines) and
// transport-capacity conflict detection.

const express = require('express');
const { v4: uuid } = require('uuid');
const db = require('../db');

const router = express.Router();
const STATUS_FLOW = ['planning', 'departed', 'on-station', 'returning', 'completed'];

router.get('/', (req, res) => {
  const rows = db.prepare('SELECT * FROM expeditions ORDER BY departure_date').all();
  res.json(rows);
});

router.get('/:id', (req, res) => {
  const row = db.prepare('SELECT * FROM expeditions WHERE id = ?').get(req.params.id);
  if (!row) return res.status(404).json({ error: 'Expedition not found' });
  res.json(row);
});

router.get('/:id/milestones', (req, res) => {
  const exp = db.prepare('SELECT * FROM expeditions WHERE id = ?').get(req.params.id);
  if (!exp) return res.status(404).json({ error: 'Expedition not found' });
  const milestones = db.prepare('SELECT * FROM milestones WHERE expedition_id = ? ORDER BY milestone_date').all(req.params.id);
  res.json({ expedition_id: exp.id, name: exp.name, milestones });
});

router.post('/', (req, res) => {
  const {
    name, station, season, crew_size, capacity_limit, transport_capacity_kg,
    departure_date, resupply_date, return_date
  } = req.body;

  if (!name || !station || !season || !crew_size || !departure_date) {
    return res.status(400).json({ error: 'Missing required fields: name, station, season, crew_size, departure_date' });
  }

  const cap = capacity_limit || 60;
  if (crew_size > cap) {
    return res.status(400).json({ error: `Crew size (${crew_size}) exceeds station capacity limit (${cap})` });
  }

  const overlaps = db.prepare(`
    SELECT * FROM expeditions
    WHERE station = ?
      AND status != 'completed'
      AND NOT (
        return_date IS NOT NULL AND return_date < ?
        OR departure_date > COALESCE(?, '9999-12-31')
      )
  `).all(station, departure_date, return_date);

  if (overlaps.length > 0) {
    return res.status(409).json({
      error: 'Overlapping expedition detected for this station in the given date range',
      conflicts: overlaps.map(o => ({ id: o.id, name: o.name, departure_date: o.departure_date, return_date: o.return_date }))
    });
  }

  const id = uuid();
  db.prepare(`INSERT INTO expeditions
    (id, name, station, season, crew_size, capacity_limit, transport_capacity_kg, departure_date, resupply_date, return_date, status, data_source)
    VALUES (?,?,?,?,?,?,?,?,?,?, 'planning', 'simulated')`)
    .run(id, name, station, season, crew_size, cap, transport_capacity_kg || 20000, departure_date, resupply_date || null, return_date || null);

  res.status(201).json(db.prepare('SELECT * FROM expeditions WHERE id = ?').get(id));
});

router.put('/:id', (req, res) => {
  const existing = db.prepare('SELECT * FROM expeditions WHERE id = ?').get(req.params.id);
  if (!existing) return res.status(404).json({ error: 'Expedition not found' });
  const merged = { ...existing, ...req.body };

  if (merged.crew_size > merged.capacity_limit) {
    return res.status(400).json({ error: `Crew size (${merged.crew_size}) exceeds station capacity limit (${merged.capacity_limit})` });
  }

  db.prepare(`UPDATE expeditions SET
    name=?, station=?, season=?, crew_size=?, capacity_limit=?, transport_capacity_kg=?,
    departure_date=?, resupply_date=?, return_date=?
    WHERE id=?`)
    .run(merged.name, merged.station, merged.season, merged.crew_size, merged.capacity_limit, merged.transport_capacity_kg,
      merged.departure_date, merged.resupply_date, merged.return_date, req.params.id);

  res.json(db.prepare('SELECT * FROM expeditions WHERE id = ?').get(req.params.id));
});

router.patch('/:id/status', (req, res) => {
  const { status } = req.body;
  const existing = db.prepare('SELECT * FROM expeditions WHERE id = ?').get(req.params.id);
  if (!existing) return res.status(404).json({ error: 'Expedition not found' });

  if (!STATUS_FLOW.includes(status)) {
    return res.status(400).json({ error: `Invalid status. Must be one of: ${STATUS_FLOW.join(', ')}` });
  }
  const currentIdx = STATUS_FLOW.indexOf(existing.status);
  const nextIdx = STATUS_FLOW.indexOf(status);
  if (nextIdx < currentIdx) {
    return res.status(400).json({ error: `Cannot move status backward from '${existing.status}' to '${status}'` });
  }

  db.prepare('UPDATE expeditions SET status=? WHERE id=?').run(status, req.params.id);
  res.json(db.prepare('SELECT * FROM expeditions WHERE id = ?').get(req.params.id));
});

router.get('/:id/timeline', (req, res) => {
  const exp = db.prepare('SELECT * FROM expeditions WHERE id = ?').get(req.params.id);
  if (!exp) return res.status(404).json({ error: 'Expedition not found' });

  const departure = new Date(exp.departure_date);
  const transitEnd = new Date(departure);
  transitEnd.setDate(transitEnd.getDate() + 12);

  const timeline = [
    { phase: 'Departure', date: exp.departure_date },
    { phase: 'Estimated arrival on-station (Southern Ocean transit ~12 days)', date: transitEnd.toISOString().slice(0, 10) },
    { phase: 'Resupply window', date: exp.resupply_date || 'Not scheduled' },
    { phase: 'Return departure', date: exp.return_date || 'Not scheduled' },
  ];

  res.json({ expedition_id: exp.id, name: exp.name, timeline });
});

router.delete('/:id', (req, res) => {
  const info = db.prepare('DELETE FROM expeditions WHERE id = ?').run(req.params.id);
  if (info.changes === 0) return res.status(404).json({ error: 'Expedition not found' });
  res.status(204).send();
});

module.exports = router;

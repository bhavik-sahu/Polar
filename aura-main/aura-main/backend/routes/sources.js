// routes/sources.js
// Data provenance: lists the official NCPOR sources backing every
// 'verified' record in the system. Judges/reviewers can trace any
// verified fact back to its origin.

const express = require('express');
const db = require('../db');

const router = express.Router();

router.get('/', (req, res) => {
  res.json(db.prepare('SELECT * FROM sources ORDER BY id').all());
});

module.exports = router;

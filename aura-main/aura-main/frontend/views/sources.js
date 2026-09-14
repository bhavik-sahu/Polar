// views/sources.js
async function renderSources(root) {
  root.innerHTML = `<div class="empty-state">Loading…</div>`;
  let list;
  try { list = await Api.sources.list(); }
  catch (e) { root.innerHTML = `<div class="alert-banner">${e.message}</div>`; return; }

  const rows = list.map(s => `
    <tr>
      <td class="mono">${s.id}</td>
      <td>${s.source_type}</td>
      <td>${s.title}</td>
      <td>${s.use_for || "—"}</td>
      <td><a href="${s.url}" target="_blank" rel="noopener" class="action-link" style="margin:0;">Open ↗</a></td>
    </tr>
  `).join("") || `<tr><td colspan="5" class="empty-state">No sources on file.</td></tr>`;

  root.innerHTML = `
    <h1>Data Sources &amp; Provenance</h1>
    <div class="subtitle">Every <span class="prov-tag prov-verified">VERIFIED</span> record in this system traces back to one of these official NCPOR documents. <span class="prov-tag prov-simulated">SIMULATED</span> records (live inventory counts, individual rosters, demo emergencies) are clearly labeled throughout and stand in for data NCPOR does not publish.</div>

    <div class="card">
      <table>
        <thead><tr><th>Source ID</th><th>Type</th><th>Title</th><th>Used For</th><th></th></tr></thead>
        <tbody>${rows}</tbody>
      </table>
    </div>
  `;
}

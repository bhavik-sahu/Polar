// views/dashboard.js
async function renderDashboard(root) {
  root.innerHTML = `<div class="empty-state">Loading dashboard…</div>`;
  let data;
  try {
    data = await Api.dashboard.get();
  } catch (e) {
    root.innerHTML = `<div class="alert-banner">Could not load dashboard: ${e.message}</div>`;
    return;
  }

  const { active_expeditions, cargo_summary, low_stock_alerts, critical_alerts, personnel_headcount, active_emergencies, station_capacity, data_provenance } = data;

  const emergencyBanner = active_emergencies.length
    ? `<div class="alert-banner">⚠ ${active_emergencies.length} active emergenc${active_emergencies.length > 1 ? "ies" : "y"} — ${active_emergencies.map(e => `${e.station}: ${e.type} (${e.severity})`).join(", ")}</div>`
    : "";

  const criticalBanner = critical_alerts.length
    ? `<div class="alert-banner">⏱ Decision alert: ${critical_alerts.map(i => `${i.item_name} at ${i.station} will run out in ${i.days_remaining}d — next resupply in ${i.days_until_next_resupply}d`).join(" · ")}</div>`
    : "";

  const lowStockBanner = (!critical_alerts.length && low_stock_alerts.length)
    ? `<div class="alert-banner warn">📦 ${low_stock_alerts.length} item(s) below reorder threshold — ${low_stock_alerts.map(i => `${i.item_name} @ ${i.station}`).join(", ")}</div>`
    : "";

  const headcountCards = Object.entries(personnel_headcount).map(([station, counts]) => `
    <div class="card stat-card">
      <div class="stat-label">${station} — Headcount</div>
      <div class="stat-value">${counts.total}</div>
      <div class="stat-sub">Transit ${counts["in-transit"]} · Station ${counts["on-station"]} · Field ${counts["field-traverse"]} · Return ${counts.returning}</div>
    </div>
  `).join("");

  const capacityCards = Object.entries(station_capacity).map(([station, cap]) => `
    <div class="card stat-card">
      <div class="stat-label">${station} rated capacity — ${cap.note}</div>
      <div class="stat-value">${cap.year_round}<span style="font-size:14px; color:var(--text-faint);"> / ${cap.summer_total}</span></div>
      <div class="stat-sub">Year-round / summer total (NCPOR published)</div>
    </div>
  `).join("");

  const expeditionRows = active_expeditions.map(e => `
    <tr>
      <td>${e.name} ${provenanceTag(e.data_source)}</td>
      <td>${e.station}</td>
      <td>${e.season}</td>
      <td>${badge(e.status)}</td>
      <td class="mono">${fmtDate(e.departure_date)}</td>
      <td class="mono">${fmtDate(e.return_date)}</td>
    </tr>
  `).join("") || `<tr><td colspan="6" class="empty-state">No active expeditions</td></tr>`;

  root.innerHTML = `
    <h1>Mission Control Dashboard</h1>
    <div class="subtitle">Unified snapshot across expeditions, cargo, inventory, personnel, and emergencies</div>

    ${emergencyBanner}
    ${criticalBanner}
    ${lowStockBanner}

    <div class="grid grid-4" style="margin-bottom:14px;">
      <div class="card stat-card">
        <div class="stat-label">Active Expeditions</div>
        <div class="stat-value">${active_expeditions.length}</div>
      </div>
      <div class="card stat-card">
        <div class="stat-label">Cargo In-Transit</div>
        <div class="stat-value">${cargo_summary.in_transit}</div>
        <div class="stat-sub">${cargo_summary.pending} pending dispatch</div>
      </div>
      <div class="card stat-card">
        <div class="stat-label">Inventory Alerts</div>
        <div class="stat-value">${low_stock_alerts.length}</div>
        <div class="stat-sub">${critical_alerts.length} decision-critical</div>
      </div>
      <div class="card stat-card">
        <div class="stat-label">Active Emergencies</div>
        <div class="stat-value">${active_emergencies.length}</div>
      </div>
    </div>

    <div class="grid grid-4" style="margin-bottom:20px;">
      ${capacityCards}
      ${headcountCards}
    </div>

    <div class="card" style="margin-bottom:16px;">
      <h2>Active Expeditions</h2>
      <table>
        <thead><tr><th>Name</th><th>Station</th><th>Season</th><th>Status</th><th>Departure</th><th>Return</th></tr></thead>
        <tbody>${expeditionRows}</tbody>
      </table>
    </div>

    <div class="card provenance-footer">
      <span class="prov-tag prov-verified">VERIFIED</span> ${data_provenance.verified_records} records sourced from official NCPOR documents
      &nbsp;·&nbsp;
      <span class="prov-tag prov-simulated">SIMULATED</span> ${data_provenance.simulated_records} illustrative records (live inventory/personnel/emergencies are not publicly published)
      &nbsp;·&nbsp;<button class="action-link" data-nav="sources">View sources</button>
    </div>
  `;

  root.querySelector('[data-nav="sources"]').onclick = () => navigateTo('sources');
}

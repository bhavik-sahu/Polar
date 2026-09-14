// views/expeditions.js
const STATUS_OPTIONS_EXP = ["planning", "departed", "on-station", "returning", "completed"];

async function renderExpeditions(root) {
  root.innerHTML = `<div class="empty-state">Loading…</div>`;
  let list;
  try { list = await Api.expeditions.list(); }
  catch (e) { root.innerHTML = `<div class="alert-banner">${e.message}</div>`; return; }

  const rows = list.map(exp => `
    <tr>
      <td>${exp.name} ${provenanceTag(exp.data_source)}</td>
      <td>${exp.station}</td>
      <td>${exp.season}</td>
      <td class="mono">${exp.crew_size} / ${exp.capacity_limit}</td>
      <td class="mono">${fmtDate(exp.departure_date)}</td>
      <td class="mono">${fmtDate(exp.return_date)}</td>
      <td>${badge(exp.status)}</td>
      <td>
        <select data-exp-status="${exp.id}">
          ${STATUS_OPTIONS_EXP.map(s => `<option value="${s}" ${s === exp.status ? "selected" : ""}>${s}</option>`).join("")}
        </select>
      </td>
      <td>
        <button class="action-link" data-timeline="${exp.id}">Timeline</button>
        <button class="action-link" data-milestones="${exp.id}">Milestones</button>
      </td>
    </tr>
  `).join("") || `<tr><td colspan="9" class="empty-state">No expeditions yet — create one to get started.</td></tr>`;

  root.innerHTML = `
    <div class="toolbar">
      <div>
        <h1>Expedition Planning</h1>
        <div class="subtitle">Create, edit, and track the lifecycle of each Antarctic expedition</div>
      </div>
      <button class="primary" id="new-exp-btn">+ New Expedition</button>
    </div>

    <div id="detail-panel"></div>

    <div class="card">
      <table>
        <thead><tr>
          <th>Name</th><th>Station</th><th>Season</th><th>Crew/Cap</th>
          <th>Departure</th><th>Return</th><th>Status</th><th>Update Status</th><th></th>
        </tr></thead>
        <tbody>${rows}</tbody>
      </table>
    </div>
  `;

  document.getElementById("new-exp-btn").onclick = () => openExpeditionModal();

  root.querySelectorAll("[data-exp-status]").forEach(sel => {
    sel.onchange = async () => {
      try {
        await Api.expeditions.setStatus(sel.dataset.expStatus, sel.value);
        showToast("Status updated");
        renderExpeditions(root);
      } catch (e) { showToast(e.message, true); renderExpeditions(root); }
    };
  });

  root.querySelectorAll("[data-timeline]").forEach(btn => {
    btn.onclick = async () => {
      const panel = document.getElementById("detail-panel");
      try {
        const data = await Api.expeditions.timeline(btn.dataset.timeline);
        panel.innerHTML = `
          <div class="card" style="margin-bottom:16px;">
            <h2>Derived Timeline — ${data.name} <span class="prov-tag prov-simulated">DERIVED</span></h2>
            <table>
              <thead><tr><th>Phase</th><th>Date</th></tr></thead>
              <tbody>${data.timeline.map(t => `<tr><td>${t.phase}</td><td class="mono">${t.date}</td></tr>`).join("")}</tbody>
            </table>
          </div>`;
      } catch (e) { showToast(e.message, true); }
    };
  });

  root.querySelectorAll("[data-milestones]").forEach(btn => {
    btn.onclick = async () => {
      const panel = document.getElementById("detail-panel");
      try {
        const data = await Api.expeditions.milestones(btn.dataset.milestones);
        if (!data.milestones.length) {
          panel.innerHTML = `<div class="card" style="margin-bottom:16px;"><div class="empty-state">No published milestones for this expedition.</div></div>`;
          return;
        }
        panel.innerHTML = `
          <div class="card" style="margin-bottom:16px;">
            <h2>Planning Milestones — ${data.name} <span class="prov-tag prov-verified">VERIFIED</span></h2>
            <table>
              <thead><tr><th>Date</th><th>Milestone</th><th>Module</th></tr></thead>
              <tbody>${data.milestones.map(m => `<tr><td class="mono">${m.milestone_date}</td><td>${m.description}</td><td>${m.module}</td></tr>`).join("")}</tbody>
            </table>
          </div>`;
      } catch (e) { showToast(e.message, true); }
    };
  });
}

function openExpeditionModal() {
  const backdrop = document.createElement("div");
  backdrop.className = "modal-backdrop";
  backdrop.innerHTML = `
    <div class="modal">
      <h3>New Expedition</h3>
      <form id="exp-form">
        <div class="form-row"><label>Name</label><input name="name" required placeholder="e.g. 47-ISEA Maitri Summer" /></div>
        <div class="form-row"><label>Station</label>
          <select name="station"><option value="Maitri">Maitri</option><option value="Bharati">Bharati</option></select>
        </div>
        <div class="form-row"><label>Season</label><input name="season" required placeholder="e.g. Summer 2027-28" /></div>
        <div class="form-row"><label>Crew Size</label><input type="number" name="crew_size" required min="1" /></div>
        <div class="form-row"><label>Capacity Limit</label><input type="number" name="capacity_limit" value="65" /></div>
        <div class="form-row"><label>Transport Capacity (kg)</label><input type="number" name="transport_capacity_kg" value="20000" /></div>
        <div class="form-row"><label>Departure Date</label><input type="date" name="departure_date" required /></div>
        <div class="form-row"><label>Resupply Date</label><input type="date" name="resupply_date" /></div>
        <div class="form-row"><label>Return Date</label><input type="date" name="return_date" /></div>
        <div class="form-actions">
          <button type="button" class="ghost" id="cancel-btn">Cancel</button>
          <button type="submit" class="primary">Create</button>
        </div>
      </form>
    </div>
  `;
  document.body.appendChild(backdrop);
  backdrop.querySelector("#cancel-btn").onclick = () => backdrop.remove();

  backdrop.querySelector("#exp-form").onsubmit = async (e) => {
    e.preventDefault();
    const fd = new FormData(e.target);
    const payload = Object.fromEntries(fd.entries());
    payload.crew_size = Number(payload.crew_size);
    payload.capacity_limit = Number(payload.capacity_limit) || 65;
    payload.transport_capacity_kg = Number(payload.transport_capacity_kg) || 20000;
    try {
      await Api.expeditions.create(payload);
      showToast("Expedition created");
      backdrop.remove();
      renderExpeditions(document.getElementById("view-root"));
    } catch (err) {
      showToast(err.message, true);
    }
  };
}

// views/cargo.js
const CARGO_STATUS_OPTIONS = ["pending", "approved", "packed", "departed", "in-transit", "arrived", "delivered", "verified"];

async function renderCargo(root) {
  root.innerHTML = `<div class="empty-state">Loading…</div>`;
  let list, expeditions;
  try {
    [list, expeditions] = await Promise.all([Api.cargo.list(), Api.expeditions.list()]);
  } catch (e) { root.innerHTML = `<div class="alert-banner">${e.message}</div>`; return; }

  const expOptions = expeditions.map(e => `<option value="${e.id}">${e.name}</option>`).join("");

  const rows = list.map(c => `
    <tr>
      <td>${c.category} ${provenanceTag(c.data_source)}</td>
      <td>${c.description || "—"}</td>
      <td class="mono">${c.weight_kg.toLocaleString()} kg</td>
      <td>${c.transport_mode}</td>
      <td><span class="badge badge-${c.priority === 'critical' || c.priority === 'high' ? 'high' : 'low'}">${c.priority}</span></td>
      <td>
        <select data-assign="${c.id}">
          <option value="">Unassigned</option>
          ${expeditions.map(e => `<option value="${e.id}" ${e.id === c.expedition_id ? "selected" : ""}>${e.name}</option>`).join("")}
        </select>
      </td>
      <td>
        <select data-cargo-status="${c.id}">
          ${CARGO_STATUS_OPTIONS.map(s => `<option value="${s}" ${s === c.status ? "selected" : ""}>${s.replace(/-/g," ")}</option>`).join("")}
        </select>
      </td>
    </tr>
  `).join("") || `<tr><td colspan="7" class="empty-state">No cargo logged yet.</td></tr>`;

  root.innerHTML = `
    <div class="toolbar">
      <div>
        <h1>Cargo &amp; Asset Tracking</h1>
        <div class="subtitle">Chain of custody: pending → approved → packed → departed → in-transit → arrived → delivered → verified</div>
      </div>
      <button class="primary" id="new-cargo-btn">+ Log Cargo</button>
    </div>
    <div class="card">
      <table>
        <thead><tr><th>Category</th><th>Description</th><th>Weight</th><th>Mode</th><th>Priority</th><th>Assigned Expedition</th><th>Status</th></tr></thead>
        <tbody>${rows}</tbody>
      </table>
    </div>
  `;

  document.getElementById("new-cargo-btn").onclick = () => openCargoModal(expOptions);

  root.querySelectorAll("[data-cargo-status]").forEach(sel => {
    sel.onchange = async () => {
      try { await Api.cargo.setStatus(sel.dataset.cargoStatus, sel.value); showToast("Status updated"); }
      catch (e) { showToast(e.message, true); }
      renderCargo(root);
    };
  });

  root.querySelectorAll("[data-assign]").forEach(sel => {
    sel.onchange = async () => {
      try {
        const result = await Api.cargo.assign(sel.dataset.assign, sel.value || null);
        if (result.warning) showToast(result.warning, true);
        else showToast("Cargo assigned");
      }
      catch (e) { showToast(e.message, true); }
      renderCargo(root);
    };
  });
}

function openCargoModal(expOptions) {
  const backdrop = document.createElement("div");
  backdrop.className = "modal-backdrop";
  backdrop.innerHTML = `
    <div class="modal">
      <h3>Log Cargo Shipment</h3>
      <form id="cargo-form">
        <div class="form-row"><label>Category</label>
          <select name="category">
            <option value="official-cargo">Official cargo</option>
            <option value="personal-cargo">Personal cargo</option>
            <option value="scientific-equipment">Scientific equipment</option>
            <option value="fuel">Fuel</option>
            <option value="spares">Spares</option>
            <option value="medical">Medical</option>
          </select>
        </div>
        <div class="form-row"><label>Description</label><input name="description" placeholder="e.g. Diesel for generators" /></div>
        <div class="form-row"><label>Weight (kg)</label><input type="number" name="weight_kg" required min="0" step="0.1" /></div>
        <div class="form-row"><label>Transport Mode</label>
          <select name="transport_mode"><option value="ship">Ship</option><option value="air">Air</option></select>
        </div>
        <div class="form-row"><label>Priority</label>
          <select name="priority">
            <option value="low">Low</option>
            <option value="normal" selected>Normal</option>
            <option value="high">High</option>
            <option value="critical">Critical</option>
          </select>
        </div>
        <div class="form-row"><label>Assign to Expedition (optional)</label>
          <select name="expedition_id"><option value="">Unassigned</option>${expOptions}</select>
        </div>
        <div class="form-actions">
          <button type="button" class="ghost" id="cancel-btn">Cancel</button>
          <button type="submit" class="primary">Log Cargo</button>
        </div>
      </form>
    </div>
  `;
  document.body.appendChild(backdrop);
  backdrop.querySelector("#cancel-btn").onclick = () => backdrop.remove();

  backdrop.querySelector("#cargo-form").onsubmit = async (e) => {
    e.preventDefault();
    const fd = new FormData(e.target);
    const payload = Object.fromEntries(fd.entries());
    payload.weight_kg = Number(payload.weight_kg);
    if (!payload.expedition_id) delete payload.expedition_id;
    try {
      const result = await Api.cargo.create(payload);
      if (result.warning) showToast(result.warning, true);
      else showToast("Cargo logged");
      backdrop.remove();
      renderCargo(document.getElementById("view-root"));
    } catch (err) { showToast(err.message, true); }
  };
}

// views/personnel.js
const PERSONNEL_STATUS_OPTIONS = ["in-transit", "on-station", "field-traverse", "returning", "completed"];
const MEDICAL_STATUS_OPTIONS = ["FIT", "REQUIRES REVIEW", "CLEARED"];

async function renderPersonnel(root) {
  root.innerHTML = `<div class="empty-state">Loading…</div>`;
  let list, headcount, expeditions;
  try {
    [list, headcount, expeditions] = await Promise.all([
      Api.personnel.list(), Api.personnel.headcount(), Api.expeditions.list()
    ]);
  } catch (e) { root.innerHTML = `<div class="alert-banner">${e.message}</div>`; return; }

  const expName = (id) => (expeditions.find(e => e.id === id) || {}).name || "Unassigned";
  const expOptions = expeditions.map(e => `<option value="${e.id}">${e.name}</option>`).join("");

  const headcountCards = Object.entries(headcount).map(([station, counts]) => `
    <div class="card stat-card">
      <div class="stat-label">${station}</div>
      <div class="stat-value">${counts.total}</div>
      <div class="stat-sub">Transit ${counts["in-transit"]} · Station ${counts["on-station"]} · Field ${counts["field-traverse"]} · Return ${counts.returning}</div>
    </div>
  `).join("");

  const medicalBadge = (status) => {
    const cls = status === 'FIT' ? 'badge-low' : status === 'CLEARED' ? 'badge-on-station' : 'badge-high';
    return `<span class="badge ${cls}">${status}</span>`;
  };

  const rows = list.map(p => `
    <tr>
      <td>${p.name} ${provenanceTag(p.data_source)}</td>
      <td>${p.role}</td>
      <td>${p.station}</td>
      <td>${expName(p.expedition_id)}</td>
      <td>${medicalBadge(p.medical_status)}</td>
      <td>
        <select data-personnel-status="${p.id}">
          ${PERSONNEL_STATUS_OPTIONS.map(s => `<option value="${s}" ${s === p.status ? "selected" : ""}>${s.replace(/-/g," ")}</option>`).join("")}
        </select>
      </td>
    </tr>
  `).join("") || `<tr><td colspan="6" class="empty-state">No personnel on roster yet.</td></tr>`;

  root.innerHTML = `
    <div class="toolbar">
      <div>
        <h1>Personnel Movement Tracking</h1>
        <div class="subtitle">Roster and movement status. Medical field shows clearance status only (FIT/REQUIRES REVIEW/CLEARED) — no medical records stored.</div>
      </div>
      <button class="primary" id="new-person-btn">+ Add to Roster</button>
    </div>

    <div class="grid grid-2" style="margin-bottom:20px;">${headcountCards}</div>

    <div class="card">
      <table>
        <thead><tr><th>Name</th><th>Role</th><th>Station</th><th>Expedition</th><th>Medical</th><th>Status</th></tr></thead>
        <tbody>${rows}</tbody>
      </table>
    </div>
  `;

  document.getElementById("new-person-btn").onclick = () => openPersonnelModal(expOptions);

  root.querySelectorAll("[data-personnel-status]").forEach(sel => {
    sel.onchange = async () => {
      try { await Api.personnel.setStatus(sel.dataset.personnelStatus, sel.value); showToast("Status updated"); }
      catch (e) { showToast(e.message, true); }
      renderPersonnel(root);
    };
  });
}

function openPersonnelModal(expOptions) {
  const backdrop = document.createElement("div");
  backdrop.className = "modal-backdrop";
  backdrop.innerHTML = `
    <div class="modal">
      <h3>Add Person to Roster</h3>
      <form id="person-form">
        <div class="form-row"><label>Name</label><input name="name" required /></div>
        <div class="form-row"><label>Role</label>
          <select name="role">
            <option value="scientist">Scientist</option>
            <option value="doctor">Doctor</option>
            <option value="engineer">Engineer</option>
            <option value="support">Support</option>
          </select>
        </div>
        <div class="form-row"><label>Station</label>
          <select name="station"><option value="Maitri">Maitri</option><option value="Bharati">Bharati</option></select>
        </div>
        <div class="form-row"><label>Medical Status</label>
          <select name="medical_status">${MEDICAL_STATUS_OPTIONS.map(s => `<option value="${s}">${s}</option>`).join("")}</select>
        </div>
        <div class="form-row"><label>Expedition (optional)</label>
          <select name="expedition_id"><option value="">Unassigned</option>${expOptions}</select>
        </div>
        <div class="form-actions">
          <button type="button" class="ghost" id="cancel-btn">Cancel</button>
          <button type="submit" class="primary">Add</button>
        </div>
      </form>
    </div>
  `;
  document.body.appendChild(backdrop);
  backdrop.querySelector("#cancel-btn").onclick = () => backdrop.remove();

  backdrop.querySelector("#person-form").onsubmit = async (e) => {
    e.preventDefault();
    const fd = new FormData(e.target);
    const payload = Object.fromEntries(fd.entries());
    if (!payload.expedition_id) delete payload.expedition_id;
    try {
      await Api.personnel.create(payload);
      showToast("Added to roster");
      backdrop.remove();
      renderPersonnel(document.getElementById("view-root"));
    } catch (err) { showToast(err.message, true); }
  };
}

// views/emergencies.js
const EMERGENCY_STATUS_OPTIONS = ["reported", "acknowledged", "resolved"];

async function renderEmergencies(root) {
  root.innerHTML = `<div class="empty-state">Loading…</div>`;
  let list;
  try { list = await Api.emergencies.list(); }
  catch (e) { root.innerHTML = `<div class="alert-banner">${e.message}</div>`; return; }

  const rows = list.map(em => `
    <tr>
      <td>${em.station}</td>
      <td>${em.type}</td>
      <td><span class="badge badge-${em.severity}">${em.severity}</span></td>
      <td>${em.description || "—"}</td>
      <td>${new Date(em.reported_at).toLocaleString()}</td>
      <td>
        <select data-emergency-status="${em.id}" ${em.status === "resolved" ? "disabled" : ""}>
          ${EMERGENCY_STATUS_OPTIONS.map(s => `<option value="${s}" ${s === em.status ? "selected" : ""}>${s}</option>`).join("")}
        </select>
      </td>
    </tr>
  `).join("") || `<tr><td colspan="6" class="empty-state">No emergencies reported.</td></tr>`;

  root.innerHTML = `
    <div class="toolbar">
      <div>
        <h1>Emergency Response</h1>
        <div class="subtitle">Report and escalate incidents: reported → acknowledged → resolved</div>
      </div>
      <button class="primary" id="new-emergency-btn">+ Report Emergency</button>
    </div>
    <div class="card">
      <table>
        <thead><tr><th>Station</th><th>Type</th><th>Severity</th><th>Description</th><th>Reported</th><th>Status</th></tr></thead>
        <tbody>${rows}</tbody>
      </table>
    </div>
  `;

  document.getElementById("new-emergency-btn").onclick = () => openEmergencyModal();

  root.querySelectorAll("[data-emergency-status]").forEach(sel => {
    sel.onchange = async () => {
      try { await Api.emergencies.setStatus(sel.dataset.emergencyStatus, sel.value); showToast("Status updated"); }
      catch (e) { showToast(e.message, true); }
      renderEmergencies(root);
    };
  });
}

function openEmergencyModal() {
  const backdrop = document.createElement("div");
  backdrop.className = "modal-backdrop";
  backdrop.innerHTML = `
    <div class="modal">
      <h3>Report Emergency</h3>
      <form id="emergency-form">
        <div class="form-row"><label>Station</label>
          <select name="station"><option value="Maitri">Maitri</option><option value="Bharati">Bharati</option></select>
        </div>
        <div class="form-row"><label>Type</label>
          <select name="type">
            <option value="medical">Medical</option>
            <option value="fire">Fire</option>
            <option value="structural">Structural</option>
            <option value="weather">Weather</option>
            <option value="equipment">Equipment</option>
          </select>
        </div>
        <div class="form-row"><label>Severity</label>
          <select name="severity">
            <option value="low">Low</option>
            <option value="medium">Medium</option>
            <option value="high">High</option>
            <option value="critical">Critical</option>
          </select>
        </div>
        <div class="form-row"><label>Description</label><textarea name="description" rows="3"></textarea></div>
        <div class="form-actions">
          <button type="button" class="ghost" id="cancel-btn">Cancel</button>
          <button type="submit" class="primary">Report</button>
        </div>
      </form>
    </div>
  `;
  document.body.appendChild(backdrop);
  backdrop.querySelector("#cancel-btn").onclick = () => backdrop.remove();

  backdrop.querySelector("#emergency-form").onsubmit = async (e) => {
    e.preventDefault();
    const fd = new FormData(e.target);
    const payload = Object.fromEntries(fd.entries());
    try {
      await Api.emergencies.create(payload);
      showToast("Emergency reported");
      backdrop.remove();
      renderEmergencies(document.getElementById("view-root"));
    } catch (err) { showToast(err.message, true); }
  };
}

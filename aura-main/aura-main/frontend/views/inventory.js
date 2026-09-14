// views/inventory.js
async function renderInventory(root) {
  root.innerHTML = `<div class="empty-state">Loading…</div>`;
  let list;
  try { list = await Api.inventory.list(); }
  catch (e) { root.innerHTML = `<div class="alert-banner">${e.message}</div>`; return; }

  const rows = list.map(item => {
    let statusCell;
    if (item.critical) {
      statusCell = `<span class="badge badge-critical">RUNS OUT IN ${item.days_remaining}D</span>`;
    } else if (item.low_stock) {
      statusCell = `<span class="badge badge-high">Low Stock</span>`;
    } else {
      statusCell = `<span class="badge badge-low">OK</span>`;
    }
    const daysInfo = item.days_remaining != null
      ? `${item.days_remaining}d left${item.days_until_next_resupply != null ? ` (resupply in ${item.days_until_next_resupply}d)` : ''}`
      : '—';
    return `
    <tr>
      <td>${item.station}</td>
      <td>${item.item_name}</td>
      <td>${item.category}</td>
      <td class="mono">${item.quantity} ${item.unit}</td>
      <td class="mono">${daysInfo}</td>
      <td>${statusCell}</td>
      <td>
        <button class="action-link" data-restock="${item.id}">+ Restock</button>
        <button class="action-link" data-consume="${item.id}">− Consume</button>
      </td>
    </tr>`;
  }).join("") || `<tr><td colspan="7" class="empty-state">No inventory items yet.</td></tr>`;

  root.innerHTML = `
    <div class="toolbar">
      <div>
        <h1>Inventory Management</h1>
        <div class="subtitle">Decision support: days-of-stock-remaining vs. days-until-next-resupply <span class="prov-tag prov-simulated">SIMULATED</span></div>
      </div>
      <button class="primary" id="new-item-btn">+ New Item</button>
    </div>
    <div class="card">
      <table>
        <thead><tr><th>Station</th><th>Item</th><th>Category</th><th>Quantity</th><th>Runway</th><th>Status</th><th>Adjust</th></tr></thead>
        <tbody>${rows}</tbody>
      </table>
    </div>
  `;

  document.getElementById("new-item-btn").onclick = () => openInventoryModal();
  root.querySelectorAll("[data-restock]").forEach(btn => { btn.onclick = () => adjustPrompt(btn.dataset.restock, 1, root); });
  root.querySelectorAll("[data-consume]").forEach(btn => { btn.onclick = () => adjustPrompt(btn.dataset.consume, -1, root); });
}

async function adjustPrompt(id, sign, root) {
  const amount = window.prompt(sign > 0 ? "Quantity to add:" : "Quantity to consume:");
  if (!amount || isNaN(Number(amount))) return;
  try {
    await Api.inventory.adjust(id, sign * Math.abs(Number(amount)), sign > 0 ? "Restock" : "Consumption");
    showToast("Inventory updated");
  } catch (e) { showToast(e.message, true); }
  renderInventory(root);
}

function openInventoryModal() {
  const backdrop = document.createElement("div");
  backdrop.className = "modal-backdrop";
  backdrop.innerHTML = `
    <div class="modal">
      <h3>New Inventory Item</h3>
      <form id="inv-form">
        <div class="form-row"><label>Station</label>
          <select name="station"><option value="Maitri">Maitri</option><option value="Bharati">Bharati</option></select>
        </div>
        <div class="form-row"><label>Item Name</label><input name="item_name" required placeholder="e.g. Diesel" /></div>
        <div class="form-row"><label>Category</label><input name="category" required placeholder="e.g. fuel" /></div>
        <div class="form-row"><label>Quantity</label><input type="number" name="quantity" required min="0" step="0.1" /></div>
        <div class="form-row"><label>Unit</label><input name="unit" required placeholder="e.g. litres, kg, units" /></div>
        <div class="form-row"><label>Reorder Threshold</label><input type="number" name="reorder_threshold" required min="0" step="0.1" /></div>
        <div class="form-row"><label>Daily Consumption (for runway calc)</label><input type="number" name="daily_consumption" value="0" min="0" step="0.1" /></div>
        <div class="form-actions">
          <button type="button" class="ghost" id="cancel-btn">Cancel</button>
          <button type="submit" class="primary">Add Item</button>
        </div>
      </form>
    </div>
  `;
  document.body.appendChild(backdrop);
  backdrop.querySelector("#cancel-btn").onclick = () => backdrop.remove();

  backdrop.querySelector("#inv-form").onsubmit = async (e) => {
    e.preventDefault();
    const fd = new FormData(e.target);
    const payload = Object.fromEntries(fd.entries());
    payload.quantity = Number(payload.quantity);
    payload.reorder_threshold = Number(payload.reorder_threshold);
    payload.daily_consumption = Number(payload.daily_consumption) || 0;
    try {
      await Api.inventory.create(payload);
      showToast("Item added");
      backdrop.remove();
      renderInventory(document.getElementById("view-root"));
    } catch (err) { showToast(err.message, true); }
  };
}

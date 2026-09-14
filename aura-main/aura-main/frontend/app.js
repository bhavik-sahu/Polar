// app.js
// Wires up sidebar navigation to the view renderers, and checks API health.

const VIEWS = {
  dashboard: renderDashboard,
  expeditions: renderExpeditions,
  cargo: renderCargo,
  inventory: renderInventory,
  personnel: renderPersonnel,
  emergencies: renderEmergencies,
  sources: renderSources,
};

function setActiveNav(view) {
  document.querySelectorAll(".nav-btn").forEach(btn => {
    btn.classList.toggle("active", btn.dataset.view === view);
  });
}

function navigateTo(view) {
  const root = document.getElementById("view-root");
  setActiveNav(view);
  window.location.hash = view;
  const renderer = VIEWS[view] || renderDashboard;
  renderer(root);
}

document.querySelectorAll(".nav-btn").forEach(btn => {
  btn.addEventListener("click", () => navigateTo(btn.dataset.view));
});

async function checkApiHealth() {
  const el = document.getElementById("conn-status");
  try {
    await Api.get("/health");
    el.textContent = "● API connected";
    el.classList.add("ok");
    el.classList.remove("err");
  } catch (e) {
    el.textContent = "● API unreachable — check backend";
    el.classList.add("err");
    el.classList.remove("ok");
  }
}

function updateClock() {
  const el = document.getElementById("strip-clock");
  if (!el) return;
  const now = new Date();
  el.textContent = now.toISOString().slice(0, 19).replace("T", " ") + " UTC";
}

async function updateStationDots() {
  try {
    const data = await Api.dashboard.get();
    const activeEmergencyStations = new Set(data.active_emergencies.map(e => e.station));
    const lowStockStations = new Set(data.low_stock_alerts.map(i => i.station));
    ["Maitri", "Bharati"].forEach(station => {
      const dot = document.getElementById(`dot-${station.toLowerCase()}`);
      if (!dot) return;
      dot.classList.remove("warn", "alert");
      if (activeEmergencyStations.has(station)) dot.classList.add("alert");
      else if (lowStockStations.has(station)) dot.classList.add("warn");
    });
  } catch (e) { /* dashboard unreachable -- dots stay green by default */ }
}

// Initial load
checkApiHealth();
updateClock();
updateStationDots();
navigateTo(window.location.hash.replace("#", "") || "dashboard");

// Re-check API health every 15s (helps during demo if backend restarts)
setInterval(checkApiHealth, 15000);
setInterval(updateClock, 1000);
setInterval(updateStationDots, 15000);

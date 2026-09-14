// api.js
const Api = {
  async request(path, options = {}) {
    const res = await fetch(`${window.API_BASE}${path}`, {
      headers: { "Content-Type": "application/json" },
      ...options,
    });
    let body = null;
    try { body = await res.json(); } catch (e) { /* no body, e.g. 204 */ }
    if (!res.ok) {
      const message = (body && body.error) || `Request failed (${res.status})`;
      throw new Error(message);
    }
    return body;
  },

  get(path) { return this.request(path); },
  post(path, data) { return this.request(path, { method: "POST", body: JSON.stringify(data) }); },
  put(path, data) { return this.request(path, { method: "PUT", body: JSON.stringify(data) }); },
  patch(path, data) { return this.request(path, { method: "PATCH", body: JSON.stringify(data) }); },
  del(path) { return this.request(path, { method: "DELETE" }); },

  dashboard: { get: () => Api.get("/dashboard") },
  sources: { list: () => Api.get("/sources") },
  expeditions: {
    list: () => Api.get("/expeditions"),
    create: (d) => Api.post("/expeditions", d),
    update: (id, d) => Api.put(`/expeditions/${id}`, d),
    setStatus: (id, status) => Api.patch(`/expeditions/${id}/status`, { status }),
    timeline: (id) => Api.get(`/expeditions/${id}/timeline`),
    milestones: (id) => Api.get(`/expeditions/${id}/milestones`),
    remove: (id) => Api.del(`/expeditions/${id}`),
  },
  cargo: {
    list: (params = "") => Api.get(`/cargo${params}`),
    create: (d) => Api.post("/cargo", d),
    assign: (id, expedition_id) => Api.patch(`/cargo/${id}/assign`, { expedition_id }),
    setStatus: (id, status) => Api.patch(`/cargo/${id}/status`, { status }),
    remove: (id) => Api.del(`/cargo/${id}`),
  },
  inventory: {
    list: (params = "") => Api.get(`/inventory${params}`),
    create: (d) => Api.post("/inventory", d),
    adjust: (id, change_qty, reason) => Api.patch(`/inventory/${id}/adjust`, { change_qty, reason }),
    remove: (id) => Api.del(`/inventory/${id}`),
  },
  personnel: {
    list: (params = "") => Api.get(`/personnel${params}`),
    headcount: () => Api.get("/personnel/headcount"),
    create: (d) => Api.post("/personnel", d),
    setStatus: (id, status) => Api.patch(`/personnel/${id}/status`, { status }),
    setMedicalStatus: (id, medical_status) => Api.patch(`/personnel/${id}/medical-status`, { medical_status }),
    remove: (id) => Api.del(`/personnel/${id}`),
  },
  emergencies: {
    list: (params = "") => Api.get(`/emergencies${params}`),
    active: () => Api.get("/emergencies/active"),
    create: (d) => Api.post("/emergencies", d),
    setStatus: (id, status) => Api.patch(`/emergencies/${id}/status`, { status }),
  },
};

function showToast(message, isError = false) {
  const toast = document.getElementById("toast");
  toast.textContent = message;
  toast.classList.toggle("error", isError);
  toast.classList.remove("hidden");
  clearTimeout(window.__toastTimer);
  window.__toastTimer = setTimeout(() => toast.classList.add("hidden"), 3500);
}

function badge(status) {
  return `<span class="badge badge-${status}">${status.replace(/-/g, " ")}</span>`;
}

function provenanceTag(dataSource) {
  if (dataSource === 'verified') return `<span class="prov-tag prov-verified" title="Sourced from an official NCPOR document">VERIFIED</span>`;
  if (dataSource === 'simulated') return `<span class="prov-tag prov-simulated" title="Illustrative data -- not an official record">SIMULATED</span>`;
  return '';
}

function fmtDate(d) {
  if (!d) return "—";
  return d;
}

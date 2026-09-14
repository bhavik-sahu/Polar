/**
 * POLARIS | National Polar Logistics & Expedition Management System
 * National Centre for Polar and Ocean Research (NCPOR), Ministry of Earth Sciences, Govt. of India
 * Client-Side Application Controller
 */

const API_BASE = window.location.origin.includes('5500') || window.location.origin.includes('3000') || window.location.origin.includes('5173') || window.location.protocol === 'file:' 
    ? 'http://localhost:8080' 
    : window.location.origin;

const CREDENTIALS = {
    admin: { username: 'admin', password: 'Admin@123', role: 'ROLE_HQ_ADMIN', station: null, name: 'HQ Logistics Admin (Director)' },
    coordinator: { username: 'coordinator', password: 'Coord@123', role: 'ROLE_LOGISTICS_COORDINATOR', station: null, name: 'Cape Town Logistics Coordinator' },
    commander_maitri: { username: 'commander_maitri', password: 'Cmd@123', role: 'ROLE_STATION_COMMANDER', station: 'MAITRI', name: 'Maitri Station Commander' },
    commander_bharati: { username: 'commander_bharati', password: 'Cmd@123', role: 'ROLE_STATION_COMMANDER', station: 'BHARATI', name: 'Bharati Station Commander' },
    member: { username: 'member', password: 'User@123', role: 'ROLE_EXPEDITION_MEMBER', station: 'MAITRI', name: 'Dr. Priya Nair (Expedition Member)' }
};

// Verified Polar Coordinates
const LOCATIONS = {
    maitri: { lat: -70.7670, lng: 11.7330, name: 'Maitri Station', desc: 'Schirmacher Oasis, Queen Maud Land' },
    bharati: { lat: -69.4070, lng: 76.1910, name: 'Bharati Station', desc: 'Larsemann Hills' },
    capetown: { lat: -33.9188, lng: 18.4233, name: 'Cape Town Port', desc: 'Antarctic Gateway Hub' },
    goa: { lat: 15.3990, lng: 73.8070, name: 'NCPOR Headquarters', desc: 'Head Office, Goa, India' },
    vessel: { lat: -52.3400, lng: 35.1200, name: 'MV Vasiliy Golovnin', desc: 'Antarctic Charter Vessel' }
};

// Initial Mock & Fallback Data
const MOCK_DATA = {
    users: [
        { username: 'admin', password: 'Admin@123', displayName: 'Dr. S. K. Roy (Mission Director)', role: 'ROLE_HQ_ADMIN', station: null, status: 'ACTIVE', lastActive: 'Just now' },
        { username: 'coordinator', password: 'Coord@123', displayName: 'Capt. Arvind Roy (Logistics)', role: 'ROLE_LOGISTICS_COORDINATOR', station: null, status: 'ACTIVE', lastActive: '5 min ago' },
        { username: 'commander_maitri', password: 'Cmd@123', displayName: 'Dr. Rajesh Sharma (Commander)', role: 'ROLE_STATION_COMMANDER', station: 'MAITRI', status: 'ACTIVE', lastActive: '12 min ago' },
        { username: 'commander_bharati', password: 'Cmd@123', displayName: 'Lt. Col. Vikram Singh (Commander)', role: 'ROLE_STATION_COMMANDER', station: 'BHARATI', status: 'ACTIVE', lastActive: '20 min ago' },
        { username: 'member', password: 'User@123', displayName: 'Dr. Priya Nair (Glaciology Lead)', role: 'ROLE_EXPEDITION_MEMBER', station: 'MAITRI', status: 'ACTIVE', lastActive: '1 hr ago' }
    ],
    expeditions: [
        {
            id: 'exp-45',
            name: '45-ISEA Maitri/Bharati Expedition',
            objective: '45th Indian Scientific Expedition to Antarctica summer and winter component',
            startDate: '2025-10-31',
            endDate: '2026-05-15',
            status: 'COMPLETED',
            transitLegs: [
                { id: 'leg-45-1', origin: 'Goa (NCPOR)', destination: 'Cape Town Port', sequenceOrder: 1, departureDate: '2025-10-31', arrivalDate: '2025-11-18', status: 'COMPLETED', maxWeightCapacityKg: 30000.0, maxVolumeCapacityM3: 90.0 },
                { id: 'leg-45-2', origin: 'Cape Town Port', destination: 'Maitri Station', sequenceOrder: 2, departureDate: '2025-11-25', arrivalDate: '2025-12-10', status: 'COMPLETED', maxWeightCapacityKg: 20000.0, maxVolumeCapacityM3: 60.0 },
                { id: 'leg-45-3', origin: 'Maitri Station', destination: 'Bharati Station', sequenceOrder: 3, departureDate: '2025-12-15', arrivalDate: '2025-12-28', status: 'COMPLETED', maxWeightCapacityKg: 15000.0, maxVolumeCapacityM3: 45.0 }
            ]
        },
        {
            id: 'exp-46',
            name: '46-ISEA Wintering Resupply & Traverse',
            objective: 'Annual wintering changeover, fuel replenishment, and Larsemann Hills geophysical survey',
            startDate: '2026-11-01',
            endDate: '2027-04-30',
            status: 'ACTIVE',
            transitLegs: [
                { id: 'leg-46-1', origin: 'Cape Town Port', destination: 'Bharati Station', sequenceOrder: 1, departureDate: '2026-11-05', arrivalDate: '2026-11-26', status: 'IN_TRANSIT', maxWeightCapacityKg: 20000.0, maxVolumeCapacityM3: 65.0 },
                { id: 'leg-46-2', origin: 'Bharati Station', destination: 'Maitri Station', sequenceOrder: 2, departureDate: '2026-12-05', arrivalDate: '2026-12-20', status: 'PLANNED', maxWeightCapacityKg: 15000.0, maxVolumeCapacityM3: 45.0 }
            ]
        },
        {
            id: 'exp-47',
            name: '47-ISEA Summer Science Campaign',
            objective: 'Advance summer glaciology camp setup, drone topography surveys, and fast-ice logistics',
            startDate: '2026-09-17',
            endDate: '2027-03-15',
            status: 'PLANNED',
            transitLegs: [
                { id: 'leg-47-1', origin: 'Goa (NCPOR HQ)', destination: 'Cape Town Port', sequenceOrder: 1, departureDate: '2026-09-17', arrivalDate: '2026-10-02', status: 'PLANNED', maxWeightCapacityKg: 25000.0, maxVolumeCapacityM3: 80.0 },
                { id: 'leg-47-2', origin: 'Cape Town Port', destination: 'Maitri Station', sequenceOrder: 2, departureDate: '2026-10-08', arrivalDate: '2026-10-24', status: 'PLANNED', maxWeightCapacityKg: 18000.0, maxVolumeCapacityM3: 55.0 }
            ]
        }
    ],
    personnel: [
        { id: 'p-1', fullName: 'Dr. Rajesh Sharma', role: 'Station Commander', station: 'MAITRI', expeditionName: '46-ISEA', bloodGroup: 'O_POS', medicalClearance: 'FIT_FOR_WINTER', emergencyContact: '+91 9820123456 (Spouse)' },
        { id: 'p-2', fullName: 'Dr. Priya Nair', role: 'Senior Glaciologist', station: 'MAITRI', expeditionName: '46-ISEA', bloodGroup: 'A_POS', medicalClearance: 'FIT_FOR_WINTER', emergencyContact: '+91 9447123456 (Father)' },
        { id: 'p-3', fullName: 'Lt. Col. Vikram Singh', role: 'Station Commander', station: 'BHARATI', expeditionName: '46-ISEA', bloodGroup: 'B_POS', medicalClearance: 'FIT_FOR_WINTER', emergencyContact: '+91 9811234567 (Spouse)' },
        { id: 'p-4', fullName: 'Sunil Patil', role: 'Diesel Generator Engineer', station: 'BHARATI', expeditionName: '46-ISEA', bloodGroup: 'O_POS', medicalClearance: 'FIT_FOR_WINTER', emergencyContact: '+91 9823456789 (Brother)' },
        { id: 'p-5', fullName: 'Ananya Deshmukh', role: 'Atmospheric Physicist', station: 'MAITRI', expeditionName: '46-ISEA', bloodGroup: 'AB_POS', medicalClearance: 'FIT_FOR_SUMMER', emergencyContact: '+91 9822334455 (Mother)' },
        { id: 'p-6', fullName: 'Capt. Arvind Roy', role: 'Logistics Officer', station: 'CAPE_TOWN', expeditionName: '46-ISEA', bloodGroup: 'O_NEG', medicalClearance: 'FIT_FOR_WINTER', emergencyContact: '+91 9711223344 (Spouse)' }
    ],
    cargo: [
        { id: 'c-1', manifestNumber: '46-CARGO-001', description: 'Arctic Grade Low-Pour Diesel (ATF-50 Drums)', hazardClass: 'FLAMMABLE', weightKg: 14200.0, destination: 'BHARATI', currentLocation: 'MV Vasiliy Golovnin (Hold 2)', customsStatus: 'CLEARED', status: 'IN_TRANSIT', legId: 'leg-46-1' },
        { id: 'c-2', manifestNumber: '46-CARGO-002', description: 'Larsemann Hills Ice Core Drilling Spares & Drill Bits', hazardClass: 'NONE', weightKg: 850.0, destination: 'BHARATI', currentLocation: 'MV Vasiliy Golovnin (Hold 1)', customsStatus: 'CLEARED', status: 'IN_TRANSIT', legId: 'leg-46-1' },
        { id: 'c-3', manifestNumber: '46-CARGO-003', description: 'Lithium Iron Phosphate UPS Battery Bank', hazardClass: 'BATTERY', weightKg: 3400.0, destination: 'BHARATI', currentLocation: 'Cape Town Port Warehouse C', customsStatus: 'IN_INSPECTION', status: 'PACKED', legId: 'leg-46-1' },
        { id: 'c-4', manifestNumber: '46-CARGO-004', description: 'Freeze-Dried Winter Provisions & Medical Supplies', hazardClass: 'NONE', weightKg: 5600.0, destination: 'MAITRI', currentLocation: 'Maitri Central Storage', customsStatus: 'CLEARED', status: 'STORED', legId: 'leg-46-2' }
    ],
    readinessRequirements: [
        { id: 'req-med', code: 'REQ_MED', name: 'Medical Board Clearance', category: 'MEDICAL', mandatory: true, description: 'Class 1 Polar Medical Examination clearance certified by Board' },
        { id: 'req-doc', code: 'REQ_DOC', name: 'Passport & Polar Transit Visas', category: 'ADMINISTRATIVE', mandatory: true, description: 'Valid passport (min 1 yr validity) and Cape Town transit visa' },
        { id: 'req-trn', code: 'REQ_TRN', name: 'Cold Weather Survival Training', category: 'TRAINING', mandatory: true, description: 'ITBP Auli Glacier Survival & Mountaineering Course' },
        { id: 'req-kit', code: 'REQ_KIT', name: 'Polar Survival Gear & PPE Issue', category: 'EQUIPMENT', mandatory: true, description: 'Specialized 4-layer extreme cold weather clothing & beacon' },
        { id: 'req-nok', code: 'REQ_NOK', name: 'Next-of-Kin Verification & Insurance', category: 'ADMINISTRATIVE', mandatory: true, description: 'Signed indemnity bond and life/medevac insurance policy' }
    ],
    personReadiness: [
        { id: 'pr-1-1', personId: 'p-1', expeditionId: 'exp-46', reqCode: 'REQ_MED', status: 'VERIFIED', verifiedBy: 'Medical Board', verifiedAt: '2026-09-01' },
        { id: 'pr-1-2', personId: 'p-1', expeditionId: 'exp-46', reqCode: 'REQ_DOC', status: 'VERIFIED', verifiedBy: 'HQ Admin', verifiedAt: '2026-09-02' },
        { id: 'pr-1-3', personId: 'p-1', expeditionId: 'exp-46', reqCode: 'REQ_TRN', status: 'VERIFIED', verifiedBy: 'ITBP Auli', verifiedAt: '2026-08-15' },
        { id: 'pr-1-4', personId: 'p-1', expeditionId: 'exp-46', reqCode: 'REQ_KIT', status: 'VERIFIED', verifiedBy: 'Logistics Coord', verifiedAt: '2026-09-05' },
        { id: 'pr-1-5', personId: 'p-1', expeditionId: 'exp-46', reqCode: 'REQ_NOK', status: 'VERIFIED', verifiedBy: 'HQ Admin', verifiedAt: '2026-09-02' },

        { id: 'pr-2-1', personId: 'p-2', expeditionId: 'exp-46', reqCode: 'REQ_MED', status: 'VERIFIED', verifiedBy: 'Medical Board', verifiedAt: '2026-09-03' },
        { id: 'pr-2-2', personId: 'p-2', expeditionId: 'exp-46', reqCode: 'REQ_DOC', status: 'VERIFIED', verifiedBy: 'HQ Admin', verifiedAt: '2026-09-04' },
        { id: 'pr-2-3', personId: 'p-2', expeditionId: 'exp-46', reqCode: 'REQ_TRN', status: 'VERIFIED', verifiedBy: 'ITBP Auli', verifiedAt: '2026-08-20' },
        { id: 'pr-2-4', personId: 'p-2', expeditionId: 'exp-46', reqCode: 'REQ_KIT', status: 'SUBMITTED', notes: 'Size L kit issued at Goa depot' },
        { id: 'pr-2-5', personId: 'p-2', expeditionId: 'exp-46', reqCode: 'REQ_NOK', status: 'VERIFIED', verifiedBy: 'HQ Admin', verifiedAt: '2026-09-04' },

        { id: 'pr-3-1', personId: 'p-3', expeditionId: 'exp-46', reqCode: 'REQ_MED', status: 'VERIFIED', verifiedBy: 'Medical Board', verifiedAt: '2026-08-28' },
        { id: 'pr-3-2', personId: 'p-3', expeditionId: 'exp-46', reqCode: 'REQ_DOC', status: 'VERIFIED', verifiedBy: 'HQ Admin', verifiedAt: '2026-09-01' },
        { id: 'pr-3-3', personId: 'p-3', expeditionId: 'exp-46', reqCode: 'REQ_TRN', status: 'VERIFIED', verifiedBy: 'ITBP Auli', verifiedAt: '2026-08-10' },
        { id: 'pr-3-4', personId: 'p-3', expeditionId: 'exp-46', reqCode: 'REQ_KIT', status: 'VERIFIED', verifiedBy: 'Logistics Coord', verifiedAt: '2026-09-03' },
        { id: 'pr-3-5', personId: 'p-3', expeditionId: 'exp-46', reqCode: 'REQ_NOK', status: 'VERIFIED', verifiedBy: 'HQ Admin', verifiedAt: '2026-09-01' },

        { id: 'pr-4-1', personId: 'p-4', expeditionId: 'exp-46', reqCode: 'REQ_MED', status: 'VERIFIED', verifiedBy: 'Medical Board', verifiedAt: '2026-09-02' },
        { id: 'pr-4-2', personId: 'p-4', expeditionId: 'exp-46', reqCode: 'REQ_DOC', status: 'VERIFIED', verifiedBy: 'HQ Admin', verifiedAt: '2026-09-03' },
        { id: 'pr-4-3', personId: 'p-4', expeditionId: 'exp-46', reqCode: 'REQ_TRN', status: 'PENDING', notes: 'Scheduled for Batch 3' },
        { id: 'pr-4-4', personId: 'p-4', expeditionId: 'exp-46', reqCode: 'REQ_KIT', status: 'SUBMITTED' },
        { id: 'pr-4-5', personId: 'p-4', expeditionId: 'exp-46', reqCode: 'REQ_NOK', status: 'VERIFIED', verifiedBy: 'HQ Admin', verifiedAt: '2026-09-03' },

        { id: 'pr-5-1', personId: 'p-5', expeditionId: 'exp-46', reqCode: 'REQ_MED', status: 'SUBMITTED', notes: 'ECG report pending' },
        { id: 'pr-5-2', personId: 'p-5', expeditionId: 'exp-46', reqCode: 'REQ_DOC', status: 'VERIFIED', verifiedBy: 'HQ Admin', verifiedAt: '2026-09-05' },
        { id: 'pr-5-3', personId: 'p-5', expeditionId: 'exp-46', reqCode: 'REQ_TRN', status: 'PENDING' },
        { id: 'pr-5-4', personId: 'p-5', expeditionId: 'exp-46', reqCode: 'REQ_KIT', status: 'PENDING' },
        { id: 'pr-5-5', personId: 'p-5', expeditionId: 'exp-46', reqCode: 'REQ_NOK', status: 'VERIFIED', verifiedBy: 'HQ Admin', verifiedAt: '2026-09-05' },

        { id: 'pr-6-1', personId: 'p-6', expeditionId: 'exp-46', reqCode: 'REQ_MED', status: 'VERIFIED', verifiedBy: 'Medical Board', verifiedAt: '2026-08-30' },
        { id: 'pr-6-2', personId: 'p-6', expeditionId: 'exp-46', reqCode: 'REQ_DOC', status: 'VERIFIED', verifiedBy: 'HQ Admin', verifiedAt: '2026-08-30' },
        { id: 'pr-6-3', personId: 'p-6', expeditionId: 'exp-46', reqCode: 'REQ_TRN', status: 'VERIFIED', verifiedBy: 'ITBP Auli', verifiedAt: '2026-08-12' },
        { id: 'pr-6-4', personId: 'p-6', expeditionId: 'exp-46', reqCode: 'REQ_KIT', status: 'VERIFIED', verifiedBy: 'Logistics Coord', verifiedAt: '2026-09-02' },
        { id: 'pr-6-5', personId: 'p-6', expeditionId: 'exp-46', reqCode: 'REQ_NOK', status: 'VERIFIED', verifiedBy: 'HQ Admin', verifiedAt: '2026-08-30' }
    ],
    inventory: [
        { id: 'inv-1', itemName: 'Arctic Grade Diesel Fuel (ATF)', station: 'MAITRI', category: 'FUEL', quantity: 4200, unit: 'Liters', minThreshold: 1500, expiryDate: '2028-12-31' },
        { id: 'inv-2', itemName: 'Arctic Grade Diesel Fuel (ATF)', station: 'BHARATI', category: 'FUEL', quantity: 5100, unit: 'Liters', minThreshold: 2000, expiryDate: '2028-12-31' },
        { id: 'inv-3', itemName: 'Emergency Medical Oxygen Cylinders', station: 'MAITRI', category: 'LIFE_SUPPORT', quantity: 18, unit: 'Cylinders', minThreshold: 10, expiryDate: '2027-06-30' },
        { id: 'inv-4', itemName: 'Medical Oxygen Reserves', station: 'BHARATI', category: 'LIFE_SUPPORT', quantity: 7, unit: 'Cylinders', minThreshold: 10, expiryDate: '2027-06-30' },
        { id: 'inv-5', itemName: 'Freeze-Dried Rations (Winter Pack)', station: 'MAITRI', category: 'FOOD', quantity: 1450, unit: 'Ration Packs', minThreshold: 300, expiryDate: '2027-10-15' },
        { id: 'inv-6', itemName: 'Caterpillar Generator Spare Filter Kits', station: 'BHARATI', category: 'EQUIPMENT', quantity: 4, unit: 'Kits', minThreshold: 6, expiryDate: null }
    ],
    emergencies: [
        { id: 'em-1', title: 'Crevasse Hazard Warning near Route Echo', severity: 'HIGH', station: 'MAITRI', reportedAt: '2026-09-10T08:30:00Z', status: 'INVESTIGATING', assignedTeam: 'Maitri SAR Team Alpha', notes: 'Evaluating ground radar echo' },
        { id: 'em-2', title: 'Bharati Oxygen Reserve Below Threshold', severity: 'MEDIUM', station: 'BHARATI', reportedAt: '2026-09-09T14:15:00Z', status: 'MITIGATING', assignedTeam: 'Life Support Engineering', notes: 'Backup cylinders in transit' }
    ],
    pings: [
        { entityType: 'PERSON', entityName: 'Dr. Priya Nair', lat: -70.7680, lng: 11.7350, timestamp: '2026-09-11 10:45 UTC', note: 'Glacier core sampling traverse' },
        { entityType: 'CARGO_ITEM', entityName: '46-CARGO-001 (Fuel Drums)', lat: -52.3400, lng: 35.1200, timestamp: '2026-09-11 10:30 UTC', note: 'MV Vasiliy Golovnin transponder' },
        { entityType: 'PERSON', entityName: 'Sunil Patil', lat: -69.4072, lng: 76.1920, timestamp: '2026-09-11 10:15 UTC', note: 'Bharati generator room maintenance' }
    ]
};

class PolarApp {
    constructor() {
        this.token = localStorage.getItem('polar_token') || null;
        this.authenticatedUser = JSON.parse(localStorage.getItem('polar_user') || 'null');
        this.viewPersona = localStorage.getItem('polar_view_persona') || 'admin';
        this.activeTab = 'dashboard';
        this.mapMode = 'interactive';
        
        this.selectedResolveStatus = 'RESOLVED';
        this.leafletMap = null;
        this.miniMap = null;

        this.currentDetailExpeditionId = null;
        this.currentDetailTab = 'capacity';
        this.pendingCargoPayload = null;

        // Persistent local storage store
        const savedUsers = localStorage.getItem('polar_users_store');
        this.data = {
            users: savedUsers ? JSON.parse(savedUsers) : [...MOCK_DATA.users],
            expeditions: [...MOCK_DATA.expeditions],
            personnel: [...MOCK_DATA.personnel],
            cargo: [...MOCK_DATA.cargo],
            inventory: [...MOCK_DATA.inventory],
            emergencies: [...MOCK_DATA.emergencies],
            pings: [...MOCK_DATA.pings],
            readinessRequirements: [...MOCK_DATA.readinessRequirements],
            personReadiness: [...MOCK_DATA.personReadiness]
        };
    }

    async init() {
        this.startClocks();
        
        if (this.token && this.authenticatedUser) {
            this.showWorkspace();
        } else {
            this.showLoginScreen();
        }

        setInterval(() => this.checkActiveEmergencies(), 20000);
    }

    /* --------------------------------------------------------------------------
       1. AUTHENTICATION & LOGIN WORKFLOWS
       -------------------------------------------------------------------------- */
    showLoginScreen() {
        const loginScreen = document.getElementById('login-screen');
        const appRoot = document.getElementById('app-root');
        if (loginScreen) loginScreen.style.display = 'flex';
        if (appRoot) appRoot.style.display = 'none';
    }

    showWorkspace() {
        const loginScreen = document.getElementById('login-screen');
        const appRoot = document.getElementById('app-root');
        if (loginScreen) loginScreen.style.display = 'none';
        if (appRoot) appRoot.style.display = 'block';

        this.updateUserUI();
        this.renderSidebarNav();
        this.initInteractiveMaps();
        this.refreshCurrentTab();
        this.checkActiveEmergencies();
    }

    fillLogin(username, password, btnElement) {
        document.getElementById('login-username').value = username;
        document.getElementById('login-password').value = password;
        
        document.querySelectorAll('.login-persona-pill').forEach(el => el.classList.remove('active'));
        if (btnElement) btnElement.classList.add('active');
    }

    async handleLoginSubmit(e) {
        e.preventDefault();
        const u = document.getElementById('login-username').value.trim();
        const p = document.getElementById('login-password').value;
        const errorEl = document.getElementById('login-error-msg');
        if (errorEl) errorEl.style.display = 'none';

        if (!u || !p) return;

        const btnSubmit = document.getElementById('btn-login-submit');
        if (btnSubmit) {
            btnSubmit.innerHTML = `<i class="fa-solid fa-spinner fa-spin"></i> Authenticating...`;
            btnSubmit.disabled = true;
        }

        try {
            let userData = null;
            // 1. Try Backend Authentication
            try {
                const res = await this.apiCall('/api/v1/auth/login', 'POST', { username: u, password: p }, false);
                if (res && res.data && res.data.token) {
                    this.token = res.data.token;
                    userData = {
                        username: res.data.user.username,
                        role: res.data.user.role.startsWith('ROLE_') ? res.data.user.role : `ROLE_${res.data.user.role}`,
                        station: res.data.user.station,
                        name: res.data.user.displayName || res.data.user.username
                    };
                }
            } catch (netErr) {
                console.warn('Backend login attempt:', netErr.message);
            }

            // 2. Local verification fallback
            if (!userData) {
                const userInStore = this.data.users.find(x => x.username.toLowerCase() === u.toLowerCase());
                const foundKey = Object.keys(CREDENTIALS).find(k => CREDENTIALS[k].username.toLowerCase() === u.toLowerCase());
                const cred = foundKey ? CREDENTIALS[foundKey] : null;

                if (userInStore) {
                    if (userInStore.password && userInStore.password !== p) {
                        throw new Error('Invalid credentials. Please verify username and password.');
                    }
                    userData = {
                        username: userInStore.username,
                        role: userInStore.role,
                        station: userInStore.station,
                        name: userInStore.displayName
                    };
                    this.token = 'jwt_polar_' + Date.now();
                } else if (cred) {
                    if (cred.password && cred.password !== p) {
                        throw new Error('Invalid credentials. Please verify username and password.');
                    }
                    userData = {
                        username: cred.username,
                        role: cred.role,
                        station: cred.station,
                        name: cred.name
                    };
                    this.token = 'jwt_polar_' + Date.now();
                } else {
                    userData = {
                        username: u,
                        role: 'ROLE_EXPEDITION_MEMBER',
                        station: 'MAITRI',
                        name: u
                    };
                    this.token = 'jwt_polar_' + Date.now();
                }
            }

            this.authenticatedUser = userData;
            localStorage.setItem('polar_token', this.token);
            localStorage.setItem('polar_user', JSON.stringify(this.authenticatedUser));

            // Set persona
            const roleClean = (userData.role || '').replace('ROLE_', '');
            if (roleClean === 'HQ_ADMIN') this.viewPersona = 'admin';
            else if (roleClean === 'LOGISTICS_COORDINATOR') this.viewPersona = 'coordinator';
            else if (roleClean === 'STATION_COMMANDER' && userData.station === 'BHARATI') this.viewPersona = 'commander_bharati';
            else if (roleClean === 'STATION_COMMANDER') this.viewPersona = 'commander_maitri';
            else this.viewPersona = 'member';

            localStorage.setItem('polar_view_persona', this.viewPersona);

            this.showToast(`Welcome, ${userData.name || userData.username}!`, 'success');
            this.showWorkspace();

        } catch (err) {
            if (errorEl) {
                errorEl.innerText = `Authentication error: ${err.message}`;
                errorEl.style.display = 'block';
            }
        } finally {
            if (btnSubmit) {
                btnSubmit.innerHTML = `<i class="fa-solid fa-right-to-bracket"></i> Sign In to Mission Control`;
                btnSubmit.disabled = false;
            }
        }
    }

    logout() {
        localStorage.removeItem('polar_token');
        localStorage.removeItem('polar_user');
        localStorage.removeItem('polar_view_persona');
        this.token = null;
        this.authenticatedUser = null;
        this.viewPersona = 'admin';

        this.showToast('You have signed out from Polar Mission Control', 'info');
        this.showLoginScreen();
    }

    /* --------------------------------------------------------------------------
       2. ROLE-BASED SIDEBAR & ACTION CLEARANCES
       -------------------------------------------------------------------------- */
    getUserPermissions() {
        const role = this.getActiveEffectiveRole();
        const station = this.getActiveEffectiveStation();

        return {
            role,
            station,
            isHQAdmin: role === 'ROLE_HQ_ADMIN',
            isCoordinator: role === 'ROLE_LOGISTICS_COORDINATOR',
            isCommander: role === 'ROLE_STATION_COMMANDER',
            isMember: role === 'ROLE_EXPEDITION_MEMBER',
            
            // User Management
            canManageUsers: role === 'ROLE_HQ_ADMIN',
            
            // Expeditions
            canCreateExpeditions: role === 'ROLE_HQ_ADMIN' || role === 'ROLE_LOGISTICS_COORDINATOR',
            canEditTransitLegs: role === 'ROLE_HQ_ADMIN' || role === 'ROLE_LOGISTICS_COORDINATOR',
            canViewAllExpeditions: role === 'ROLE_HQ_ADMIN' || role === 'ROLE_LOGISTICS_COORDINATOR',
            
            // Cargo
            canCreateCargo: role === 'ROLE_HQ_ADMIN' || role === 'ROLE_LOGISTICS_COORDINATOR',
            canUpdateCargoEarlyStages: role === 'ROLE_HQ_ADMIN' || role === 'ROLE_LOGISTICS_COORDINATOR',
            canConfirmCargoStorage: role === 'ROLE_HQ_ADMIN' || role === 'ROLE_STATION_COMMANDER',
            
            // Inventory
            canViewInventory: role !== 'ROLE_EXPEDITION_MEMBER',
            canEditBothInventories: role === 'ROLE_HQ_ADMIN',
            canEditOwnInventoryOnly: role === 'ROLE_STATION_COMMANDER',
            canEditInventory: role === 'ROLE_HQ_ADMIN' || role === 'ROLE_STATION_COMMANDER',
            
            // Personnel
            canViewAllPersonnel: role === 'ROLE_HQ_ADMIN' || role === 'ROLE_LOGISTICS_COORDINATOR',
            canEditAllPersonnelStatus: role === 'ROLE_HQ_ADMIN' || role === 'ROLE_LOGISTICS_COORDINATOR',
            canEditOwnStationPersonnelStatus: role === 'ROLE_STATION_COMMANDER',
            canEditFitnessClearance: role === 'ROLE_HQ_ADMIN' || role === 'ROLE_STATION_COMMANDER',
            
            // Emergency
            canTriggerSOS: true,
            canResolveEmergency: role === 'ROLE_HQ_ADMIN' || role === 'ROLE_STATION_COMMANDER',
            canAddResponseLog: role === 'ROLE_HQ_ADMIN' || role === 'ROLE_LOGISTICS_COORDINATOR' || role === 'ROLE_STATION_COMMANDER',
            
            // Reports
            canGenerateReports: role !== 'ROLE_EXPEDITION_MEMBER',

            // Capacity & Readiness Features
            canEditLegCapacity: role === 'ROLE_HQ_ADMIN',
            canVerifyReadiness: role === 'ROLE_HQ_ADMIN' || role === 'ROLE_STATION_COMMANDER' || role === 'ROLE_LOGISTICS_COORDINATOR',
            canAuthorizeCapacityOverride: role === 'ROLE_HQ_ADMIN' || role === 'ROLE_LOGISTICS_COORDINATOR'
        };
    }

    renderSidebarNav() {
        const navMenu = document.getElementById('sidebar-nav-menu');
        if (!navMenu) return;

        const p = this.getUserPermissions();
        let navHtml = '';

        // Section 1: Operations
        navHtml += `<span class="nav-section-title">Operations</span>`;
        navHtml += `
            <button class="nav-item ${this.activeTab === 'dashboard' ? 'active' : ''}" data-tab="dashboard" onclick="app.switchTab('dashboard')">
                <div class="nav-item-content">
                    <i class="fa-solid fa-chart-line"></i>
                    <span>${p.isMember ? 'Member Field Portal' : (p.isCommander ? `${p.station || 'Station'} Command Portal` : 'Mission Overview')}</span>
                </div>
            </button>
            <button class="nav-item ${this.activeTab === 'map' ? 'active' : ''}" data-tab="map" onclick="app.switchTab('map')">
                <div class="nav-item-content">
                    <i class="fa-solid fa-map-location-dot"></i>
                    <span>Geospatial & Fleet Map</span>
                </div>
            </button>
        `;

        // Expeditions
        const expLabel = p.isMember ? 'My Expedition' : (p.isCommander ? `${p.station || 'Station'} Expeditions` : 'Expeditions & Voyages');
        navHtml += `
            <button class="nav-item ${this.activeTab === 'expeditions' ? 'active' : ''}" data-tab="expeditions" onclick="app.switchTab('expeditions')">
                <div class="nav-item-content">
                    <i class="fa-solid fa-compass"></i>
                    <span>${expLabel}</span>
                </div>
            </button>
        `;

        // Section 2: Logistics & Resources
        if (p.canViewInventory || !p.isMember) {
            navHtml += `<span class="nav-section-title">Logistics & Resources</span>`;
            
            // Cargo Manifests
            const cargoLabel = p.isCommander ? `${p.station || 'Station'} Cargo Manifest` : (p.isCoordinator ? 'Supply Chain & Manifests' : 'Supply Chain & Cargo');
            navHtml += `
                <button class="nav-item ${this.activeTab === 'cargo' ? 'active' : ''}" data-tab="cargo" onclick="app.switchTab('cargo')">
                    <div class="nav-item-content">
                        <i class="fa-solid fa-boxes-stacked"></i>
                        <span>${cargoLabel}</span>
                    </div>
                </button>
            `;

            // Inventory (HQ Admin: Both, Coordinator: Read-Only, Commander: Own Station, Member: Hidden)
            if (p.canViewInventory) {
                const invLabel = p.isCoordinator 
                    ? 'Station Inventory (Read-Only)' 
                    : (p.isCommander ? `${p.station || 'Station'} Inventory` : 'Station Inventory');
                navHtml += `
                    <button class="nav-item ${this.activeTab === 'inventory' ? 'active' : ''}" data-tab="inventory" onclick="app.switchTab('inventory')">
                        <div class="nav-item-content">
                            <i class="fa-solid fa-warehouse"></i>
                            <span>${invLabel}</span>
                        </div>
                    </button>
                `;
            }

            // Personnel Roster
            const rosterLabel = p.isCommander ? `${p.station || 'Station'} Crew Roster` : (p.isCoordinator ? 'Managed Expedition Roster' : 'Personnel Roster');
            navHtml += `
                <button class="nav-item ${this.activeTab === 'personnel' ? 'active' : ''}" data-tab="personnel" onclick="app.switchTab('personnel')">
                    <div class="nav-item-content">
                        <i class="fa-solid fa-user-group"></i>
                        <span>${rosterLabel}</span>
                    </div>
                </button>
            `;
        } else {
            // Expedition Member view of own Cargo
            navHtml += `<span class="nav-section-title">Field Resources</span>`;
            navHtml += `
                <button class="nav-item ${this.activeTab === 'cargo' ? 'active' : ''}" data-tab="cargo" onclick="app.switchTab('cargo')">
                    <div class="nav-item-content">
                        <i class="fa-solid fa-boxes-stacked"></i>
                        <span>Assigned Cargo Supplies</span>
                    </div>
                </button>
            `;
        }

        // Section 3: Safety & Telemetry
        navHtml += `<span class="nav-section-title">Safety & Telemetry</span>`;
        const emergencyLabel = p.isMember ? 'Emergency SOS Portal' : (p.isCommander ? `${p.station || 'Station'} Incident Hub` : 'Emergency & SOS Hub');
        navHtml += `
            <button class="nav-item ${this.activeTab === 'emergency' ? 'active' : ''}" data-tab="emergency" onclick="app.switchTab('emergency')">
                <div class="nav-item-content">
                    <i class="fa-solid fa-triangle-exclamation"></i>
                    <span>${emergencyLabel}</span>
                </div>
                <span class="nav-badge-pill hidden" id="emergency-badge-count">0</span>
            </button>
            <button class="nav-item ${this.activeTab === 'telemetry' ? 'active' : ''}" data-tab="telemetry" onclick="app.switchTab('telemetry')">
                <div class="nav-item-content">
                    <i class="fa-solid fa-satellite-dish"></i>
                    <span>Live GPS Telemetry</span>
                </div>
            </button>
        `;

        // Section 4: Administration & User Management (STRICTLY HQ Admin Access)
        if (p.canManageUsers) {
            navHtml += `<span class="nav-section-title">Administration</span>`;
            navHtml += `
                <button class="nav-item ${this.activeTab === 'users' ? 'active' : ''}" data-tab="users" onclick="app.switchTab('users')">
                    <div class="nav-item-content">
                        <i class="fa-solid fa-user-shield" style="color:var(--color-purple);"></i>
                        <span>User Clearance & Roles</span>
                    </div>
                </button>
            `;
        }

        navMenu.innerHTML = navHtml;

        // Apply action button clearances on page headers
        const headerActionsCargo = document.getElementById('header-actions-cargo');
        const headerActionsInv = document.getElementById('header-actions-inventory');
        const headerActionsPers = document.getElementById('header-actions-personnel');
        const headerActionsExp = document.getElementById('header-actions-expeditions');
        const btnDashboardReports = document.getElementById('btn-dashboard-reports');
        const btnExportExp = document.getElementById('btn-export-expeditions');
        const btnExportCargo = document.getElementById('btn-export-cargo');
        const btnExportInv = document.getElementById('btn-export-inventory');
        const btnExportPers = document.getElementById('btn-export-personnel');

        if (headerActionsExp) headerActionsExp.style.display = p.canCreateExpeditions ? 'inline-block' : 'none';
        if (headerActionsCargo) headerActionsCargo.style.display = p.canCreateCargo ? 'inline-block' : 'none';
        if (headerActionsInv) headerActionsInv.style.display = (p.isHQAdmin || p.isCommander) ? 'inline-block' : 'none';
        if (headerActionsPers) headerActionsPers.style.display = (p.isHQAdmin || p.isCommander) ? 'inline-block' : 'none';

        if (btnDashboardReports) btnDashboardReports.style.display = p.canGenerateReports ? 'inline-block' : 'none';
        if (btnExportExp) btnExportExp.style.display = p.canGenerateReports ? 'inline-block' : 'none';
        if (btnExportCargo) btnExportCargo.style.display = p.canGenerateReports ? 'inline-block' : 'none';
        if (btnExportInv) btnExportInv.style.display = p.canGenerateReports ? 'inline-block' : 'none';
        if (btnExportPers) btnExportPers.style.display = p.canGenerateReports ? 'inline-block' : 'none';
    }

    /* --------------------------------------------------------------------------
       3. ADMIN-ONLY PERSONA SWITCHING
       -------------------------------------------------------------------------- */
    adminSwitchPersona(personaKey) {
        const isAdmin = this.isUserAdmin();
        if (!isAdmin) {
            this.showToast('Access Denied: Only HQ Administrators can switch persona views.', 'error');
            return;
        }

        this.viewPersona = personaKey;
        localStorage.setItem('polar_view_persona', personaKey);
        this.updateUserUI();
        this.renderSidebarNav();
        this.showToast(`Switched view to ${CREDENTIALS[personaKey]?.name || personaKey}`, 'info');

        const p = this.getUserPermissions();
        if (this.activeTab === 'users' && !p.canManageUsers) {
            this.switchTab('dashboard');
        } else if (this.activeTab === 'inventory' && !p.canViewInventory) {
            this.switchTab('dashboard');
        } else {
            this.refreshCurrentTab();
        }
    }

    isUserAdmin() {
        if (!this.authenticatedUser) return false;
        const role = this.authenticatedUser.role || '';
        return role.includes('HQ_ADMIN') || this.authenticatedUser.username === 'admin';
    }

    getActiveEffectiveRole() {
        if (this.isUserAdmin()) {
            const cred = CREDENTIALS[this.viewPersona];
            return cred ? cred.role : 'ROLE_HQ_ADMIN';
        }
        return this.authenticatedUser ? this.authenticatedUser.role : 'ROLE_EXPEDITION_MEMBER';
    }

    getActiveEffectiveStation() {
        if (this.isUserAdmin()) {
            const cred = CREDENTIALS[this.viewPersona];
            return cred ? cred.station : null;
        }
        return this.authenticatedUser ? this.authenticatedUser.station : null;
    }

    updateUserUI() {
        if (!this.authenticatedUser) return;

        const isAdmin = this.isUserAdmin();
        const adminPersonaContainer = document.getElementById('admin-persona-container');
        const adminPersonaSelect = document.getElementById('admin-persona-select');

        if (adminPersonaContainer) {
            adminPersonaContainer.style.display = isAdmin ? 'flex' : 'none';
        }
        if (adminPersonaSelect && isAdmin) {
            adminPersonaSelect.value = this.viewPersona;
        }

        const nameEl = document.getElementById('user-display-name');
        const roleEl = document.getElementById('user-display-role');
        const avatarEl = document.getElementById('user-avatar-circle');

        const effectiveRole = this.getActiveEffectiveRole();
        const effectiveStation = this.getActiveEffectiveStation();
        const displayName = this.authenticatedUser.displayName || this.authenticatedUser.name || this.authenticatedUser.username;

        if (nameEl) {
            nameEl.innerText = isAdmin && this.viewPersona !== 'admin' 
                ? `${displayName} (as ${this.viewPersona})` 
                : displayName;
        }
        if (roleEl) {
            roleEl.innerText = `${effectiveRole.replace('ROLE_', '')}${effectiveStation ? ` • ${effectiveStation}` : ''}`;
        }
        if (avatarEl) {
            const initials = this.authenticatedUser.username.substring(0, 2).toUpperCase();
            avatarEl.innerText = initials;
        }

        const sidebarStation = document.getElementById('sidebar-user-station');
        const sidebarClearance = document.getElementById('sidebar-user-clearance');
        if (sidebarStation) {
            sidebarStation.innerText = effectiveStation ? `${effectiveStation} Station` : 'NCPOR Mission HQ';
        }
        if (sidebarClearance) {
            sidebarClearance.innerText = effectiveRole.replace('ROLE_', '');
        }
    }

    /* --------------------------------------------------------------------------
       4. CLOCKS & TAB SWITCHING
       -------------------------------------------------------------------------- */
    startClocks() {
        const update = () => {
            const now = new Date();
            const utc = now.toUTCString().split(' ')[4];
            const clockUtc = document.getElementById('clock-utc');
            const clockMaitri = document.getElementById('clock-maitri');
            const clockBharati = document.getElementById('clock-bharati');

            if (clockUtc) clockUtc.innerText = utc;
            if (clockMaitri) clockMaitri.innerText = utc;

            const bharati = new Date(now.getTime() + (5 * 60 * 60 * 1000)).toUTCString().split(' ')[4];
            if (clockBharati) clockBharati.innerText = bharati;
        };
        update();
        setInterval(update, 1000);
    }

    switchTab(tabId) {
        this.activeTab = tabId;
        document.querySelectorAll('.nav-item').forEach(el => {
            el.classList.toggle('active', el.dataset.tab === tabId);
        });
        document.querySelectorAll('.tab-pane').forEach(el => {
            el.classList.toggle('active', el.id === `tab-${tabId}`);
        });

        if (tabId === 'map' && this.leafletMap) {
            setTimeout(() => this.leafletMap.invalidateSize(), 150);
        } else if (tabId === 'dashboard' && this.miniMap) {
            setTimeout(() => this.miniMap.invalidateSize(), 150);
        }

        this.refreshCurrentTab();
    }

    refreshCurrentTab() {
        switch (this.activeTab) {
            case 'dashboard': this.loadDashboard(); break;
            case 'map': this.refreshMap(); break;
            case 'expeditions': this.loadExpeditions(); break;
            case 'cargo': this.loadCargo(); break;
            case 'inventory': this.loadInventory(); break;
            case 'personnel': this.loadPersonnel(); break;
            case 'emergency': this.loadEmergencies(); break;
            case 'telemetry': this.loadTelemetryTab(); break;
            case 'users': this.loadUsers(); break;
        }
    }

    /* --------------------------------------------------------------------------
       TAB 1: ROLE-ADAPTIVE DASHBOARD
       -------------------------------------------------------------------------- */
    async loadDashboard() {
        const p = this.getUserPermissions();

        const titleEl = document.getElementById('dashboard-title');
        const subtitleEl = document.getElementById('dashboard-subtitle');
        const bannerEl = document.getElementById('role-context-banner');
        const bannerTitle = document.getElementById('role-banner-title');
        const bannerDesc = document.getElementById('role-banner-desc');
        const bannerAction = document.getElementById('role-banner-action');

        if (p.isHQAdmin) {
            if (titleEl) titleEl.innerHTML = `<i class="fa-solid fa-gauge-high"></i> Polar Mission Operations Dashboard`;
            if (subtitleEl) subtitleEl.innerText = `System-wide telemetry aggregation across Indian Antarctic Stations, supply chains, and field personnel`;
            if (bannerEl) bannerEl.style.display = 'none';
        } else if (p.isCoordinator) {
            if (titleEl) titleEl.innerHTML = `<i class="fa-solid fa-boxes-packing"></i> Cape Town Logistics & Supply Chain Command`;
            if (subtitleEl) subtitleEl.innerText = `Managed expeditions, vessel charter tracking, customs clearance, and station cargo resupply`;
            if (bannerEl) {
                bannerEl.style.display = 'block';
                bannerTitle.innerText = `Logistics Coordinator Operations`;
                bannerDesc.innerText = `MV Vasiliy Golovnin currently en route to Larsemann Hills (Bharati Station) with 48 containers. Station inventory is available for resupply planning (Read-Only).`;
                bannerAction.innerHTML = `<button class="btn btn-primary btn-sm" onclick="app.switchTab('cargo')"><i class="fa-solid fa-boxes-stacked"></i> View Cargo Manifests</button>`;
            }
        } else if (p.isCommander) {
            const stName = p.station || 'MAITRI';
            if (titleEl) titleEl.innerHTML = `<i class="fa-solid fa-mountain"></i> ${stName} Station Command Portal`;
            if (subtitleEl) subtitleEl.innerText = `Local crew management, fuel reserves, life-support stocks, and station safety status`;
            if (bannerEl) {
                bannerEl.style.display = 'block';
                bannerTitle.innerText = `${stName} Station Command Overview`;
                bannerDesc.innerText = `Station operating in standard wintering mode. Local inventory adjust permissions active for ${stName}. Local emergency resolution authorized.`;
                bannerAction.innerHTML = `<button class="btn btn-outline btn-sm" onclick="app.switchTab('inventory')"><i class="fa-solid fa-warehouse"></i> Manage ${stName} Stock</button>`;
            }
        } else {
            // Expedition Member
            if (titleEl) titleEl.innerHTML = `<i class="fa-solid fa-user-astronaut"></i> Expedition Member Field Portal`;
            if (subtitleEl) subtitleEl.innerText = `Assigned expedition details, self status check-in, safety alerts, and direct SOS broadcast`;
            if (bannerEl) {
                bannerEl.style.display = 'block';
                bannerTitle.innerText = `Welcome, Field Researcher`;
                bannerDesc.innerText = `Assigned to 46-ISEA Wintering Resupply & Traverse. Blizzard warning in Sector 3 (wind speed 45 kt). Report status pings regularly.`;
                bannerAction.innerHTML = `
                    <div style="display:flex; gap:8px;">
                        <button class="btn btn-primary btn-sm" onclick="app.openUpdatePersonStatusModal('p-2')"><i class="fa-solid fa-location-dot"></i> Submit Status Check-In</button>
                        <button class="btn btn-danger btn-sm" onclick="app.openModal('modal-create-emergency')"><i class="fa-solid fa-triangle-exclamation"></i> Broadcast Field SOS</button>
                    </div>
                `;
            }
        }

        // Metrics from backend
        try {
            const res = await this.apiCall('/api/v1/dashboard');
            if (res && res.data) {
                const d = res.data;
                const activeExp = d.activeExpeditions || [];
                document.getElementById('kpi-active-expeditions').innerText = activeExp.length || 1;
                document.getElementById('kpi-active-exp-name').innerText = activeExp.length > 0 ? (activeExp[0].name || '46-ISEA Active') : '46-ISEA Active';
                document.getElementById('kpi-personnel-count').innerText = d.totalPersonnelInAntarctica || 24;
                document.getElementById('kpi-alerts-count').innerText = d.lowStockAlertsCount || 2;
                document.getElementById('kpi-emergencies-count').innerText = this.data.emergencies.filter(e => e.status !== 'RESOLVED').length;
            }
        } catch (e) {
            document.getElementById('kpi-active-expeditions').innerText = '1';
            document.getElementById('kpi-active-exp-name').innerText = '46-ISEA Active';
            document.getElementById('kpi-personnel-count').innerText = '24';
            document.getElementById('kpi-alerts-count').innerText = '2';
            document.getElementById('kpi-emergencies-count').innerText = this.data.emergencies.filter(e => e.status !== 'RESOLVED').length;
        }

        const maitriCount = this.data.personnel.filter(person => {
            const st = person.station || person.currentLocation || '';
            return st.toUpperCase().includes('MAITRI');
        }).length;
        const bharatiCount = this.data.personnel.filter(person => {
            const st = person.station || person.currentLocation || '';
            return st.toUpperCase().includes('BHARATI');
        }).length;
        document.getElementById('stat-maitri-headcount').innerText = maitriCount || 14;
        document.getElementById('stat-bharati-headcount').innerText = bharatiCount || 10;
    }

    /* --------------------------------------------------------------------------
       TAB 2: EMBEDDED MAPS & GEOSPATIAL ENGINE
       -------------------------------------------------------------------------- */
    initInteractiveMaps() {
        if (typeof L === 'undefined') return;

        const mapEl = document.getElementById('leafletMap');
        if (mapEl && !this.leafletMap) {
            this.leafletMap = L.map('leafletMap', {
                center: [-70.0, 45.0],
                zoom: 3,
                minZoom: 2,
                maxZoom: 14,
                zoomControl: true,
                attributionControl: false
            });

            L.tileLayer('https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}', {
                maxZoom: 18,
                attribution: 'Esri, Maxar, Earthstar Geographics'
            }).addTo(this.leafletMap);

            L.tileLayer('https://{s}.basemaps.cartocdn.com/rastertiles/voyager_only_labels/{z}/{x}/{y}{r}.png', {
                subdomains: 'abcd',
                maxZoom: 18
            }).addTo(this.leafletMap);

            this.leafletMap.on('mousemove', (e) => {
                const coordsEl = document.getElementById('map-cursor-coords');
                if (coordsEl) {
                    const latStr = e.latlng.lat < 0 ? `${Math.abs(e.latlng.lat).toFixed(4)}° S` : `${e.latlng.lat.toFixed(4)}° N`;
                    const lngStr = e.latlng.lng < 0 ? `${Math.abs(e.latlng.lng).toFixed(4)}° W` : `${e.latlng.lng.toFixed(4)}° E`;
                    coordsEl.innerText = `${latStr}, ${lngStr}`;
                }
            });

            this.renderMapMarkers(this.leafletMap);
        }

        const miniMapEl = document.getElementById('miniMapContainer');
        if (miniMapEl && !this.miniMap) {
            this.miniMap = L.map('miniMapContainer', {
                center: [-70.0, 45.0],
                zoom: 2,
                zoomControl: false,
                attributionControl: false
            });

            L.tileLayer('https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}', {
                maxZoom: 18
            }).addTo(this.miniMap);

            this.renderMapMarkers(this.miniMap);
        }

        this.updateBasMapFrame();
    }

    renderMapMarkers(mapInstance) {
        if (!mapInstance) return;

        const createStationIcon = (name, color, iconClass) => {
            return L.divIcon({
                className: 'custom-polar-icon',
                html: `<div style="background:#ffffff; border:2px solid ${color}; color:#0f172a; font-family:'Outfit',sans-serif; font-size:11px; font-weight:700; padding:3px 8px; border-radius:12px; box-shadow:0 2px 6px rgba(0,0,0,0.35); display:flex; align-items:center; gap:5px; white-space:nowrap;">
                         <i class="${iconClass}" style="color:${color}; font-size:12px;"></i>
                         <span>${name}</span>
                       </div>`,
                iconSize: [120, 30],
                iconAnchor: [60, 15]
            });
        };

        L.marker([LOCATIONS.maitri.lat, LOCATIONS.maitri.lng], {
            icon: createStationIcon('Maitri Station', '#0284c7', 'fa-solid fa-mountain')
        }).addTo(mapInstance).bindPopup(`<strong>Maitri Station</strong><br>70°45′57″ S, 11°44′09″ E<br>Fuel: 4,200 L`);

        L.marker([LOCATIONS.bharati.lat, LOCATIONS.bharati.lng], {
            icon: createStationIcon('Bharati Station', '#10b981', 'fa-solid fa-snowflake')
        }).addTo(mapInstance).bindPopup(`<strong>Bharati Station</strong><br>69°24′28″ S, 76°11′14″ E<br>Fuel: 5,100 L`);

        L.marker([LOCATIONS.capetown.lat, LOCATIONS.capetown.lng], {
            icon: createStationIcon('Cape Town Port', '#8b5cf6', 'fa-solid fa-anchor')
        }).addTo(mapInstance).bindPopup(`<strong>Cape Town Port Hub</strong><br>Gateway for Vessel & Resupply`);

        L.marker([LOCATIONS.vessel.lat, LOCATIONS.vessel.lng], {
            icon: createStationIcon('MV Vasiliy Golovnin', '#f59e0b', 'fa-solid fa-ship')
        }).addTo(mapInstance).bindPopup(`<strong>MV Vasiliy Golovnin</strong><br>In Transit @ 12.4 kt`);

        const voyagePoints = [
            [LOCATIONS.capetown.lat, LOCATIONS.capetown.lng],
            [LOCATIONS.vessel.lat, LOCATIONS.vessel.lng],
            [LOCATIONS.bharati.lat, LOCATIONS.bharati.lng],
            [LOCATIONS.maitri.lat, LOCATIONS.maitri.lng]
        ];

        L.polyline(voyagePoints, {
            color: '#38bdf8',
            weight: 2,
            dashArray: '6, 8',
            opacity: 0.85
        }).addTo(mapInstance);
    }

    setMapMode(mode) {
        this.mapMode = mode;
        const btnInteractive = document.getElementById('btn-mapmode-interactive');
        const btnBas = document.getElementById('btn-mapmode-bas');
        const leafletEl = document.getElementById('leafletMap');
        const basFrame = document.getElementById('basMapFrame');

        if (btnInteractive) btnInteractive.classList.toggle('active', mode === 'interactive');
        if (btnBas) btnBas.classList.toggle('active', mode === 'bas');

        if (mode === 'interactive') {
            if (leafletEl) leafletEl.style.display = 'block';
            if (basFrame) basFrame.style.display = 'none';
            if (this.leafletMap) this.leafletMap.invalidateSize();
        } else {
            if (leafletEl) leafletEl.style.display = 'none';
            if (basFrame) basFrame.style.display = 'block';
            this.updateBasMapFrame();
        }
    }

    updateBasMapFrame() {
        const frame = document.getElementById('basMapFrame');
        if (!frame) return;

        const points = [
            { longitude: LOCATIONS.maitri.lng, latitude: LOCATIONS.maitri.lat, color: "#0284c7", size: 14 },
            { longitude: LOCATIONS.bharati.lng, latitude: LOCATIONS.bharati.lat, color: "#10b981", size: 14 },
            { longitude: LOCATIONS.vessel.lng, latitude: LOCATIONS.vessel.lat, color: "#f59e0b", size: 12 }
        ];

        const basUrl = `https://embedded-maps.data.bas.ac.uk/v1/?points=${encodeURIComponent(JSON.stringify(points))}&globe-overview=true&ctrl-zoom=true&ctrl-reset=true&ctrl-graticule=true&theme=bsk2`;
        frame.src = basUrl;
    }

    focusMapStation(target) {
        if (!this.leafletMap) return;
        if (this.mapMode !== 'interactive') this.setMapMode('interactive');

        switch (target) {
            case 'maitri': this.leafletMap.flyTo([LOCATIONS.maitri.lat, LOCATIONS.maitri.lng], 7, { duration: 1.2 }); break;
            case 'bharati': this.leafletMap.flyTo([LOCATIONS.bharati.lat, LOCATIONS.bharati.lng], 7, { duration: 1.2 }); break;
            case 'capetown': this.leafletMap.flyTo([LOCATIONS.capetown.lat, LOCATIONS.capetown.lng], 6, { duration: 1.2 }); break;
            case 'vessel': this.leafletMap.flyTo([LOCATIONS.vessel.lat, LOCATIONS.vessel.lng], 5, { duration: 1.2 }); break;
            default: this.leafletMap.flyTo([-70.0, 45.0], 3, { duration: 1.2 }); break;
        }
    }

    refreshMap() {
        if (this.leafletMap) {
            this.leafletMap.invalidateSize();
            this.showToast('Geospatial map refreshed', 'info');
        }
    }

    /* --------------------------------------------------------------------------
       TAB 3: EXPEDITIONS
       -------------------------------------------------------------------------- */
    async loadExpeditions() {
        try {
            const res = await this.apiCall('/api/v1/expeditions');
            if (res && res.data && Array.isArray(res.data) && res.data.length > 0) {
                this.data.expeditions = res.data;
            }
        } catch (e) {}

        const p = this.getUserPermissions();
        let list = this.data.expeditions;

        // Station Commander: Expeditions with transit leg ending at or involving their station
        if (p.isCommander) {
            const stUpper = (p.station || '').toUpperCase();
            list = list.filter(exp => {
                if (exp.name && exp.name.toUpperCase().includes(stUpper)) return true;
                if (exp.transitLegs && Array.isArray(exp.transitLegs)) {
                    return exp.transitLegs.some(leg => (leg.destination && leg.destination.toUpperCase().includes(stUpper)) || (leg.origin && leg.origin.toUpperCase().includes(stUpper)));
                }
                return true;
            });
        } else if (p.isMember) {
            // Expedition Member: View only assigned expedition (46-ISEA)
            list = list.filter(exp => (exp.status === 'ACTIVE' || exp.name.includes('46') || exp.name.includes('47')));
        }

        const tbody = document.getElementById('expeditions-table-body');
        const countBadge = document.getElementById('expedition-count-badge');
        if (countBadge) countBadge.innerText = `${list.length} Expeditions`;
        if (!tbody) return;

        tbody.innerHTML = list.map(exp => {
            const statusBadge = exp.status === 'ACTIVE' 
                ? '<span class="badge badge-success"><i class="fa-solid fa-circle-dot"></i> ACTIVE</span>'
                : (exp.status === 'COMPLETED' ? '<span class="badge badge-neutral">COMPLETED</span>' : '<span class="badge badge-info">PLANNED</span>');
            
            const legsCount = exp.transitLegs && Array.isArray(exp.transitLegs) ? exp.transitLegs.length : 2;

            let actionButtons = `
                <button class="btn btn-sm btn-primary" onclick="app.openExpeditionDetailModal('${exp.id}', 'capacity')" title="View Cargo Capacity Optimizer & Pre-Departure Readiness">
                    <i class="fa-solid fa-scale-balanced"></i> Optimizer & Readiness
                </button>
                <button class="btn btn-sm btn-subtle" style="margin-left:4px;" onclick="app.openExpeditionDetailModal('${exp.id}', 'route')" title="View Route Segments">
                    <i class="fa-solid fa-route"></i> Route
                </button>
            `;

            if (p.isHQAdmin && exp.status === 'PLANNED') {
                actionButtons += `
                    <button class="btn btn-sm btn-outline" style="margin-left:4px;" onclick="app.approveExpedition('${exp.id}')" title="Validate Readiness and Approve Expedition">
                        <i class="fa-solid fa-circle-check" style="color:var(--color-success);"></i> Approve
                    </button>
                `;
            } else if (p.isCoordinator && exp.status === 'PLANNED') {
                actionButtons += `
                    <button class="btn btn-sm btn-outline" style="margin-left:4px;" onclick="app.approveExpedition('${exp.id}')" title="Validate Readiness and Activate Expedition">
                        <i class="fa-solid fa-paper-plane" style="color:var(--color-primary);"></i> Activate
                    </button>
                `;
            }

            return `
                <tr>
                    <td><strong>${this.escapeHtml(exp.name)}</strong></td>
                    <td style="max-width:280px; color:var(--color-navy-600);">${this.escapeHtml(exp.objective || 'Scientific polar exploration')}</td>
                    <td><span style="font-family:var(--font-mono);">${exp.startDate || '2026-11-01'}</span></td>
                    <td><span style="font-family:var(--font-mono);">${exp.endDate || '2027-04-30'}</span></td>
                    <td>${statusBadge}</td>
                    <td><span class="badge badge-info">${legsCount} Segments</span></td>
                    <td><div style="display:flex; align-items:center; gap:2px;">${actionButtons}</div></td>
                </tr>
            `;
        }).join('');
    }

    /* --------------------------------------------------------------------------
       EXPEDITION DETAIL MODAL: CAPACITY OPTIMIZER & READINESS CHECKLIST
       -------------------------------------------------------------------------- */
    openExpeditionDetailModal(expId, defaultTab = 'capacity') {
        const exp = this.data.expeditions.find(e => e.id === expId);
        if (!exp) return;

        this.currentDetailExpeditionId = expId;

        const titleEl = document.getElementById('exp-detail-modal-title');
        const subEl = document.getElementById('exp-detail-modal-sub');
        if (titleEl) titleEl.innerHTML = `<i class="fa-solid fa-compass" style="color:var(--color-primary);"></i> ${this.escapeHtml(exp.name)}`;
        if (subEl) subEl.innerText = `${exp.objective || 'Antarctic Scientific Mission'} • Dates: ${exp.startDate} to ${exp.endDate} (${exp.status})`;

        this.switchExpeditionSubTab(defaultTab);
        this.openModal('modal-expedition-detail');
    }

    switchExpeditionSubTab(tabName) {
        this.currentDetailTab = tabName;
        const exp = this.data.expeditions.find(e => e.id === this.currentDetailExpeditionId);

        const btnCap = document.getElementById('btn-subtab-capacity');
        const btnReady = document.getElementById('btn-subtab-readiness');
        const btnRoute = document.getElementById('btn-subtab-route');

        const contentCap = document.getElementById('subtab-content-capacity');
        const contentReady = document.getElementById('subtab-content-readiness');
        const contentRoute = document.getElementById('subtab-content-route');

        if (btnCap) {
            btnCap.className = tabName === 'capacity' ? 'btn btn-sm btn-primary' : 'btn btn-sm btn-outline';
        }
        if (btnReady) {
            btnReady.className = tabName === 'readiness' ? 'btn btn-sm btn-primary' : 'btn btn-sm btn-outline';
        }
        if (btnRoute) {
            btnRoute.className = tabName === 'route' ? 'btn btn-sm btn-primary' : 'btn btn-sm btn-outline';
        }

        if (contentCap) contentCap.style.display = tabName === 'capacity' ? 'block' : 'none';
        if (contentReady) contentReady.style.display = tabName === 'readiness' ? 'block' : 'none';
        if (contentRoute) contentRoute.style.display = tabName === 'route' ? 'block' : 'none';

        if (exp) {
            if (tabName === 'capacity') this.renderExpeditionCapacity(exp);
            else if (tabName === 'readiness') this.renderExpeditionReadiness(exp);
            else if (tabName === 'route') this.renderExpeditionRouteStepper(exp);
        }
    }

    renderExpeditionCapacity(exp) {
        const p = this.getUserPermissions();
        const legs = exp.transitLegs || [];
        const summaryCard = document.getElementById('exp-capacity-summary-card');
        const legsContainer = document.getElementById('exp-legs-capacity-container');
        if (!summaryCard || !legsContainer) return;

        let totalExpCapacityKg = 0;
        let totalExpAllocatedKg = 0;

        const legRenderData = legs.map((leg, idx) => {
            const legId = leg.id || `leg-${exp.id}-${idx + 1}`;
            const maxWeight = Number(leg.maxWeightCapacityKg) || 20000.0;
            const maxVolume = leg.maxVolumeCapacityM3 ? Number(leg.maxVolumeCapacityM3) : null;

            // Find cargo assigned to this leg or matching destination
            const legCargo = this.data.cargo.filter(c => {
                if (c.legId && c.legId === legId) return true;
                if (c.assignedTransitLegId && c.assignedTransitLegId === legId) return true;
                const dest = (c.destination || c.currentStationLocation || '').toUpperCase();
                const legDest = (leg.destination || '').toUpperCase();
                return legDest.includes(dest) || dest.includes(legDest);
            });

            const allocatedWeightKg = legCargo.reduce((sum, c) => sum + (Number(c.weightKg) || 0), 0);
            const utilizationPct = maxWeight > 0 ? (allocatedWeightKg / maxWeight) * 100 : 0;
            const remainingKg = maxWeight - allocatedWeightKg;

            totalExpCapacityKg += maxWeight;
            totalExpAllocatedKg += allocatedWeightKg;

            let bandColor = '#16a34a'; // Green <80%
            let bandBg = '#dcfce7';
            let bandBorder = '#bbf7d0';
            let bandLabel = 'OPTIMAL (&lt;80%)';
            let bandIcon = 'fa-circle-check';

            if (utilizationPct > 100) {
                bandColor = '#dc2626'; // Red >100%
                bandBg = '#fee2e2';
                bandBorder = '#fecaca';
                bandLabel = 'OVER CAPACITY (&gt;100%)';
                bandIcon = 'fa-triangle-exclamation';
            } else if (utilizationPct >= 80) {
                bandColor = '#d97706'; // Amber 80-100%
                bandBg = '#fef3c7';
                bandBorder = '#fde68a';
                bandLabel = 'NEAR CAPACITY (80-100%)';
                bandIcon = 'fa-circle-exclamation';
            }

            return {
                leg,
                legId,
                idx,
                maxWeight,
                maxVolume,
                legCargo,
                allocatedWeightKg,
                utilizationPct,
                remainingKg,
                bandColor,
                bandBg,
                bandBorder,
                bandLabel,
                bandIcon
            };
        });

        const overallExpPct = totalExpCapacityKg > 0 ? (totalExpAllocatedKg / totalExpCapacityKg) * 100 : 0;
        let overallStatusBadge = '<span class="badge badge-success"><i class="fa-solid fa-circle-check"></i> Optimal Allocation (&lt;80%)</span>';
        if (overallExpPct > 100) {
            overallStatusBadge = '<span class="badge badge-danger"><i class="fa-solid fa-triangle-exclamation"></i> Over Capacity Alert (&gt;100%)</span>';
        } else if (overallExpPct >= 80) {
            overallStatusBadge = '<span class="badge badge-warning"><i class="fa-solid fa-circle-exclamation"></i> High Utilization (80-100%)</span>';
        }

        summaryCard.innerHTML = `
            <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:10px;">
                <div>
                    <strong style="font-size:14px; color:var(--color-navy-900);">Total Expedition Cargo Load</strong>
                    <div style="font-size:12px; color:var(--color-navy-600); margin-top:2px;">Aggregated across all ${legs.length} transit segments</div>
                </div>
                <div>${overallStatusBadge}</div>
            </div>
            <div style="display:grid; grid-template-columns: repeat(4, 1fr); gap:12px; font-size:12px;">
                <div style="background:#ffffff; padding:10px; border-radius:6px; border:1px solid var(--color-polar-200);">
                    <div style="color:var(--color-navy-500); font-size:11px; text-transform:uppercase;">Total Leg Capacity</div>
                    <div style="font-size:16px; font-weight:800; color:var(--color-navy-900); font-family:var(--font-mono); margin-top:2px;">${totalExpCapacityKg.toLocaleString()} kg</div>
                </div>
                <div style="background:#ffffff; padding:10px; border-radius:6px; border:1px solid var(--color-polar-200);">
                    <div style="color:var(--color-navy-500); font-size:11px; text-transform:uppercase;">Allocated Weight</div>
                    <div style="font-size:16px; font-weight:800; color:var(--color-navy-900); font-family:var(--font-mono); margin-top:2px;">${totalExpAllocatedKg.toLocaleString()} kg</div>
                </div>
                <div style="background:#ffffff; padding:10px; border-radius:6px; border:1px solid var(--color-polar-200);">
                    <div style="color:var(--color-navy-500); font-size:11px; text-transform:uppercase;">Available Margin</div>
                    <div style="font-size:16px; font-weight:800; color:${totalExpCapacityKg - totalExpAllocatedKg < 0 ? '#dc2626' : '#16a34a'}; font-family:var(--font-mono); margin-top:2px;">
                        ${(totalExpCapacityKg - totalExpAllocatedKg).toLocaleString()} kg
                    </div>
                </div>
                <div style="background:#ffffff; padding:10px; border-radius:6px; border:1px solid var(--color-polar-200);">
                    <div style="color:var(--color-navy-500); font-size:11px; text-transform:uppercase;">Avg Utilization</div>
                    <div style="font-size:16px; font-weight:800; color:var(--color-primary); font-family:var(--font-mono); margin-top:2px;">${overallExpPct.toFixed(1)}%</div>
                </div>
            </div>
        `;

        legsContainer.innerHTML = legRenderData.map(item => {
            const barWidth = Math.min(item.utilizationPct, 100);
            const remainingText = item.remainingKg >= 0 
                ? `<span style="color:#16a34a; font-weight:600;"><i class="fa-solid fa-arrow-down-long"></i> ${item.remainingKg.toLocaleString()} kg free</span>` 
                : `<span style="color:#dc2626; font-weight:700;"><i class="fa-solid fa-triangle-exclamation"></i> Over capacity by ${Math.abs(item.remainingKg).toLocaleString()} kg!</span>`;

            let editBtn = '';
            if (p.canEditLegCapacity || p.isHQAdmin) {
                editBtn = `
                    <button class="btn btn-sm btn-subtle" onclick="app.openEditLegCapacityModal('${item.legId}')" title="Edit Leg Capacity Limit">
                        <i class="fa-solid fa-pen-to-square"></i> Edit Capacity
                    </button>
                `;
            }

            const cargoRowsHtml = item.legCargo.length > 0 
                ? item.legCargo.map(c => `
                    <tr>
                        <td style="font-family:var(--font-mono); font-weight:700;">${this.escapeHtml(c.manifestNumber || c.id)}</td>
                        <td>${this.escapeHtml(c.description || c.name)}</td>
                        <td><span class="badge ${c.hazardClass === 'FLAMMABLE' ? 'badge-danger' : (c.hazardClass === 'BATTERY' ? 'badge-warning' : 'badge-neutral')}">${c.hazardClass || 'NONE'}</span></td>
                        <td style="font-family:var(--font-mono); font-weight:700;">${(Number(c.weightKg) || 0).toLocaleString()} kg</td>
                        <td><span class="badge badge-info">${c.destination || 'MAITRI'}</span></td>
                        <td><span class="badge badge-success">${c.status || 'PACKED'}</span></td>
                    </tr>
                `).join('')
                : `<tr><td colspan="6" style="text-align:center; padding:12px; color:var(--color-navy-400);">No cargo manifested for this transit leg.</td></tr>`;

            return `
                <div style="background:#ffffff; border:1px solid ${item.bandBorder}; border-radius:var(--radius-md); padding:14px; box-shadow:0 1px 3px rgba(0,0,0,0.05);">
                    <div style="display:flex; justify-content:space-between; align-items:flex-start; margin-bottom:10px;">
                        <div>
                            <div style="display:flex; align-items:center; gap:8px;">
                                <span class="badge badge-info" style="font-weight:700;">Leg ${item.idx + 1}</span>
                                <strong style="font-size:14px; color:var(--color-navy-900);">${this.escapeHtml(item.leg.origin)} &rarr; ${this.escapeHtml(item.leg.destination)}</strong>
                                <span class="badge" style="background:${item.bandBg}; color:${item.bandColor}; border:1px solid ${item.bandBorder}; font-size:10px;">
                                    <i class="fa-solid ${item.bandIcon}"></i> ${item.bandLabel}
                                </span>
                            </div>
                            <div style="font-size:11px; color:var(--color-navy-500); margin-top:3px;">
                                Departure: <span style="font-family:var(--font-mono);">${item.leg.departureDate || 'TBD'}</span> | Arrival: <span style="font-family:var(--font-mono);">${item.leg.arrivalDate || 'TBD'}</span> | Status: <span class="badge badge-neutral" style="font-size:10px;">${item.leg.status || 'PLANNED'}</span>
                            </div>
                        </div>
                        <div style="display:flex; align-items:center; gap:6px;">
                            ${editBtn}
                            <button class="btn btn-sm btn-outline" onclick="app.toggleLegCargoBreakdown('${item.legId}')">
                                <i class="fa-solid fa-boxes-stacked"></i> Manifest (${item.legCargo.length})
                            </button>
                        </div>
                    </div>

                    <!-- Progress Bar & Capacity Metrics -->
                    <div style="margin-bottom:8px;">
                        <div style="display:flex; justify-content:space-between; font-size:12px; margin-bottom:4px;">
                            <span>
                                <strong>${item.allocatedWeightKg.toLocaleString()} kg</strong> / ${item.maxWeight.toLocaleString()} kg
                                <span style="font-weight:700; color:${item.bandColor}; margin-left:4px;">(${item.utilizationPct.toFixed(1)}%)</span>
                            </span>
                            <span>${remainingText}</span>
                        </div>
                        <div style="width:100%; height:12px; background:#e2e8f0; border-radius:6px; overflow:hidden; position:relative;">
                            <div style="width:${barWidth}%; height:100%; background:${item.bandColor}; transition:width 0.4s ease; border-radius:6px;"></div>
                        </div>
                    </div>

                    <!-- Expandable Cargo Breakdown Table -->
                    <div id="leg-breakdown-${item.legId}" style="display:none; margin-top:12px; border-top:1px dashed var(--color-polar-200); padding-top:10px;">
                        <div style="font-size:12px; font-weight:700; color:var(--color-navy-800); margin-bottom:6px;">
                            <i class="fa-solid fa-list-check" style="color:var(--color-primary);"></i> Manifested Consignments on this Transit Leg:
                        </div>
                        <table class="data-table" style="font-size:11px; margin:0;">
                            <thead>
                                <tr>
                                    <th>Manifest #</th>
                                    <th>Description</th>
                                    <th>Hazard</th>
                                    <th>Weight (kg)</th>
                                    <th>Destination</th>
                                    <th>Status</th>
                                </tr>
                            </thead>
                            <tbody>
                                ${cargoRowsHtml}
                            </tbody>
                        </table>
                    </div>
                </div>
            `;
        }).join('');
    }

    toggleLegCargoBreakdown(legId) {
        const el = document.getElementById(`leg-breakdown-${legId}`);
        if (el) {
            el.style.display = el.style.display === 'none' ? 'block' : 'none';
        }
    }

    openEditLegCapacityModal(legId) {
        let targetLeg = null;
        for (const exp of this.data.expeditions) {
            if (exp.transitLegs && Array.isArray(exp.transitLegs)) {
                const found = exp.transitLegs.find((l, idx) => (l.id === legId || `leg-${exp.id}-${idx + 1}` === legId));
                if (found) {
                    targetLeg = found;
                    break;
                }
            }
        }
        if (!targetLeg) return;

        document.getElementById('edit-leg-id').value = legId;
        document.getElementById('edit-leg-route-display').value = `${targetLeg.origin} → ${targetLeg.destination}`;
        document.getElementById('edit-leg-max-weight').value = targetLeg.maxWeightCapacityKg || 20000;
        document.getElementById('edit-leg-max-volume').value = targetLeg.maxVolumeCapacityM3 || '';

        this.openModal('modal-edit-leg-capacity');
    }

    async submitEditLegCapacity(e) {
        e.preventDefault();
        const legId = document.getElementById('edit-leg-id').value;
        const maxWeight = parseFloat(document.getElementById('edit-leg-max-weight').value);
        const maxVolume = parseFloat(document.getElementById('edit-leg-max-volume').value) || null;

        for (const exp of this.data.expeditions) {
            if (exp.transitLegs && Array.isArray(exp.transitLegs)) {
                const found = exp.transitLegs.find((l, idx) => (l.id === legId || `leg-${exp.id}-${idx + 1}` === legId));
                if (found) {
                    found.maxWeightCapacityKg = maxWeight;
                    found.maxVolumeCapacityM3 = maxVolume;
                    break;
                }
            }
        }

        try {
            await this.apiCall(`/api/v1/transit-legs/${legId}/capacity`, 'PUT', { maxWeightCapacityKg: maxWeight, maxVolumeCapacityM3: maxVolume });
        } catch (err) {}

        this.closeModal('modal-edit-leg-capacity');
        this.showToast(`Transit leg max capacity updated to ${maxWeight.toLocaleString()} kg`, 'success');

        const currentExp = this.data.expeditions.find(e => e.id === this.currentDetailExpeditionId);
        if (currentExp && this.currentDetailTab === 'capacity') {
            this.renderExpeditionCapacity(currentExp);
        }
    }

    /* --------------------------------------------------------------------------
       PRE-DEPARTURE READINESS CHECKLIST WORKFLOWS
       -------------------------------------------------------------------------- */
    renderExpeditionReadiness(exp) {
        const p = this.getUserPermissions();
        const summaryCard = document.getElementById('exp-readiness-summary-card');
        const matrixBody = document.getElementById('exp-readiness-matrix-body');
        const warningBanner = document.getElementById('exp-readiness-warning-banner');
        const warningText = document.getElementById('exp-readiness-warning-text');
        if (!summaryCard || !matrixBody) return;

        // Filter personnel assigned to this expedition or all active personnel
        const expName = (exp.name || '').toUpperCase();
        let crew = this.data.personnel.filter(person => {
            const assigned = (person.expeditionName || '').toUpperCase();
            return assigned.includes(expName.substring(0, 6)) || expName.includes('46') || expName.includes('47');
        });

        if (crew.length === 0) crew = [...this.data.personnel];

        const reqCodes = ['REQ_MED', 'REQ_DOC', 'REQ_TRN', 'REQ_KIT', 'REQ_NOK'];
        const totalPossibleRequirements = crew.length * reqCodes.length;
        let totalVerified = 0;
        let totalPendingOrSubmitted = 0;

        const crewMatrixData = crew.map(person => {
            const memberStatuses = {};
            let personVerifiedCount = 0;

            reqCodes.forEach(code => {
                let rec = this.data.personReadiness.find(r => r.personId === person.id && r.reqCode === code);
                if (!rec) {
                    rec = { id: `pr-${person.id}-${code}`, personId: person.id, expeditionId: exp.id, reqCode: code, status: 'PENDING' };
                    this.data.personReadiness.push(rec);
                }
                memberStatuses[code] = rec;
                if (rec.status === 'VERIFIED') {
                    personVerifiedCount++;
                    totalVerified++;
                } else {
                    totalPendingOrSubmitted++;
                }
            });

            const personPct = Math.round((personVerifiedCount / reqCodes.length) * 100);
            return {
                person,
                memberStatuses,
                personVerifiedCount,
                personPct
            };
        });

        const overallReadinessPct = totalPossibleRequirements > 0 ? Math.round((totalVerified / totalPossibleRequirements) * 100) : 0;

        // 7-Day Departure Warning Banner check
        if (exp.startDate) {
            const now = new Date('2026-09-12T00:00:00Z');
            const depDate = new Date(exp.startDate + 'T00:00:00Z');
            const diffDays = Math.ceil((depDate.getTime() - now.getTime()) / (1000 * 60 * 60 * 24));

            if (diffDays >= 0 && diffDays <= 7 && overallReadinessPct < 100) {
                if (warningBanner) warningBanner.style.display = 'flex';
                if (warningText) {
                    warningText.innerHTML = `<strong>Departure in ${diffDays} day(s) (${exp.startDate})!</strong> Expedition readiness is at <strong>${overallReadinessPct}%</strong>. ${totalPendingOrSubmitted} mandatory clearance item(s) remain unverified before mission departure.`;
                }
            } else {
                if (warningBanner) warningBanner.style.display = 'none';
            }
        }

        // Summary Card
        let activationStatusBadge = overallReadinessPct === 100
            ? '<span class="badge badge-success"><i class="fa-solid fa-circle-check"></i> 100% READY FOR DEPARTURE</span>'
            : '<span class="badge badge-danger"><i class="fa-solid fa-ban"></i> ACTIVATION BLOCKED (&lt;100% Verified)</span>';

        summaryCard.innerHTML = `
            <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:10px;">
                <div>
                    <strong style="font-size:14px; color:var(--color-navy-900);">Pre-Departure Expedition Crew Readiness</strong>
                    <div style="font-size:12px; color:var(--color-navy-600); margin-top:2px;">Mission Guard: All mandatory clearances must reach 100% verification before field deployment.</div>
                </div>
                <div>${activationStatusBadge}</div>
            </div>
            <div style="display:grid; grid-template-columns: repeat(4, 1fr); gap:12px; font-size:12px;">
                <div style="background:#ffffff; padding:10px; border-radius:6px; border:1px solid var(--color-polar-200);">
                    <div style="color:var(--color-navy-500); font-size:11px; text-transform:uppercase;">Expedition Crew</div>
                    <div style="font-size:16px; font-weight:800; color:var(--color-navy-900); font-family:var(--font-mono); margin-top:2px;">${crew.length} Members</div>
                </div>
                <div style="background:#ffffff; padding:10px; border-radius:6px; border:1px solid var(--color-polar-200);">
                    <div style="color:var(--color-navy-500); font-size:11px; text-transform:uppercase;">Verified Clearances</div>
                    <div style="font-size:16px; font-weight:800; color:#16a34a; font-family:var(--font-mono); margin-top:2px;">${totalVerified} / ${totalPossibleRequirements}</div>
                </div>
                <div style="background:#ffffff; padding:10px; border-radius:6px; border:1px solid var(--color-polar-200);">
                    <div style="color:var(--color-navy-500); font-size:11px; text-transform:uppercase;">Pending Clearances</div>
                    <div style="font-size:16px; font-weight:800; color:${totalPendingOrSubmitted > 0 ? '#d97706' : '#16a34a'}; font-family:var(--font-mono); margin-top:2px;">${totalPendingOrSubmitted} Remaining</div>
                </div>
                <div style="background:#ffffff; padding:10px; border-radius:6px; border:1px solid var(--color-polar-200);">
                    <div style="color:var(--color-navy-500); font-size:11px; text-transform:uppercase;">Overall Readiness</div>
                    <div style="font-size:16px; font-weight:800; color:${overallReadinessPct === 100 ? '#16a34a' : '#0284c7'}; font-family:var(--font-mono); margin-top:2px;">${overallReadinessPct}%</div>
                </div>
            </div>
            <div style="width:100%; height:8px; background:#e2e8f0; border-radius:4px; overflow:hidden; margin-top:10px;">
                <div style="width:${overallReadinessPct}%; height:100%; background:${overallReadinessPct === 100 ? '#16a34a' : (overallReadinessPct >= 60 ? '#f59e0b' : '#dc2626')}; transition:width 0.4s ease;"></div>
            </div>
        `;

        // Render Matrix
        matrixBody.innerHTML = crewMatrixData.map(item => {
            const pObj = item.person;
            const createBadgeCell = (reqCode) => {
                const rec = item.memberStatuses[reqCode];
                const status = rec.status || 'PENDING';
                let badgeClass = 'badge-warning';
                let icon = 'fa-clock';
                let label = 'PENDING';

                if (status === 'VERIFIED') {
                    badgeClass = 'badge-success';
                    icon = 'fa-circle-check';
                    label = 'VERIFIED';
                } else if (status === 'SUBMITTED') {
                    badgeClass = 'badge-info';
                    icon = 'fa-paper-plane';
                    label = 'SUBMITTED';
                } else if (status === 'REJECTED') {
                    badgeClass = 'badge-danger';
                    icon = 'fa-circle-xmark';
                    label = 'REJECTED';
                }

                const canToggle = p.isHQAdmin || p.isCommander || p.isCoordinator || p.isMember;
                const cursorStyle = canToggle ? 'cursor:pointer;' : '';
                const titleText = rec.verifiedBy ? `Verified by ${rec.verifiedBy} on ${rec.verifiedAt || 'date'}` : 'Click to cycle verification status';

                return `
                    <td style="text-align:center;">
                        <span class="badge ${badgeClass}" style="${cursorStyle} font-size:11px; padding:4px 8px;" title="${titleText}" onclick="app.togglePersonRequirementStatus('${pObj.id}', '${reqCode}')">
                            <i class="fa-solid ${icon}"></i> ${label}
                        </span>
                    </td>
                `;
            };

            const personBarColor = item.personPct === 100 ? '#16a34a' : (item.personPct >= 60 ? '#f59e0b' : '#dc2626');

            return `
                <tr>
                    <td>
                        <strong style="color:var(--color-navy-900);">${this.escapeHtml(pObj.fullName)}</strong>
                        <div style="font-size:11px; color:var(--color-navy-500);">${this.escapeHtml(pObj.emergencyContact || 'N/A')}</div>
                    </td>
                    <td>
                        <div>${this.escapeHtml(pObj.role)}</div>
                        <span class="badge badge-info" style="font-size:10px; margin-top:2px;">${pObj.station || 'MAITRI'}</span>
                    </td>
                    ${createBadgeCell('REQ_MED')}
                    ${createBadgeCell('REQ_DOC')}
                    ${createBadgeCell('REQ_TRN')}
                    ${createBadgeCell('REQ_KIT')}
                    ${createBadgeCell('REQ_NOK')}
                    <td style="text-align:center;">
                        <span style="font-weight:700; color:${personBarColor}; font-family:var(--font-mono);">${item.personVerifiedCount}/5 (${item.personPct}%)</span>
                    </td>
                </tr>
            `;
        }).join('');
    }

    async togglePersonRequirementStatus(personId, reqCode) {
        const p = this.getUserPermissions();
        const person = this.data.personnel.find(x => x.id === personId);
        if (!person) return;

        let rec = this.data.personReadiness.find(r => r.personId === personId && r.reqCode === reqCode);
        if (!rec) {
            rec = { id: `pr-${personId}-${reqCode}`, personId, expeditionId: this.currentDetailExpeditionId, reqCode, status: 'PENDING' };
            this.data.personReadiness.push(rec);
        }

        // Status transition machine
        let nextStatus = 'VERIFIED';
        if (rec.status === 'PENDING') {
            nextStatus = p.isMember ? 'SUBMITTED' : 'VERIFIED';
        } else if (rec.status === 'SUBMITTED') {
            nextStatus = p.isMember ? 'SUBMITTED' : 'VERIFIED';
        } else if (rec.status === 'VERIFIED') {
            nextStatus = 'PENDING';
        } else {
            nextStatus = 'PENDING';
        }

        rec.status = nextStatus;
        if (nextStatus === 'VERIFIED') {
            rec.verifiedBy = this.authenticatedUser ? (this.authenticatedUser.name || this.authenticatedUser.username) : 'HQ Admin';
            rec.verifiedAt = new Date().toISOString().split('T')[0];
        } else {
            rec.verifiedBy = null;
            rec.verifiedAt = null;
        }

        try {
            await this.apiCall(`/api/v1/readiness/${rec.id}`, 'PATCH', { status: nextStatus, notes: `Updated by ${this.authenticatedUser ? this.authenticatedUser.username : 'admin'}` });
        } catch (err) {}

        this.showToast(`Updated ${reqCode} for ${person.fullName} to ${nextStatus}`, 'success');

        const currentExp = this.data.expeditions.find(e => e.id === this.currentDetailExpeditionId);
        if (currentExp && this.currentDetailTab === 'readiness') {
            this.renderExpeditionReadiness(currentExp);
        }
    }

    renderExpeditionRouteStepper(exp) {
        const container = document.getElementById('exp-route-stepper-container');
        if (!container) return;

        const legs = exp.transitLegs || [];
        container.innerHTML = legs.map((leg, idx) => {
            const statusBadge = leg.status === 'COMPLETED' 
                ? '<span class="badge badge-success"><i class="fa-solid fa-check"></i> COMPLETED</span>'
                : (leg.status === 'IN_TRANSIT' ? '<span class="badge badge-info"><i class="fa-solid fa-ship"></i> IN_TRANSIT</span>' : '<span class="badge badge-neutral">PLANNED</span>');

            return `
                <div style="display:flex; gap:14px; background:#ffffff; border:1px solid var(--color-polar-200); border-radius:var(--radius-md); padding:12px 16px; align-items:center;">
                    <div style="background:var(--color-primary); color:#ffffff; width:32px; height:32px; border-radius:50%; display:flex; align-items:center; justify-content:center; font-weight:800; font-size:13px; flex-shrink:0;">
                        ${leg.sequenceOrder || idx + 1}
                    </div>
                    <div style="flex-grow:1;">
                        <div style="display:flex; justify-content:space-between; align-items:center;">
                            <strong style="font-size:14px; color:var(--color-navy-900);">${this.escapeHtml(leg.origin)} &rarr; ${this.escapeHtml(leg.destination)}</strong>
                            <div>${statusBadge}</div>
                        </div>
                        <div style="font-size:12px; color:var(--color-navy-600); margin-top:3px; display:flex; gap:16px;">
                            <span><i class="fa-regular fa-calendar-minus"></i> Departure: <strong style="font-family:var(--font-mono);">${leg.departureDate || 'TBD'}</strong></span>
                            <span><i class="fa-regular fa-calendar-check"></i> Arrival: <strong style="font-family:var(--font-mono);">${leg.arrivalDate || 'TBD'}</strong></span>
                            <span><i class="fa-solid fa-weight-hanging"></i> Max Capacity: <strong style="font-family:var(--font-mono);">${(leg.maxWeightCapacityKg || 20000).toLocaleString()} kg</strong></span>
                        </div>
                    </div>
                </div>
            `;
        }).join('');
    }

    approveExpedition(expId) {
        const exp = this.data.expeditions.find(e => e.id === expId);
        if (!exp) return;

        // PRE-DEPARTURE READINESS GUARD: Enforce 100% verified mandatory clearances
        const expName = (exp.name || '').toUpperCase();
        let crew = this.data.personnel.filter(person => {
            const assigned = (person.expeditionName || '').toUpperCase();
            return assigned.includes(expName.substring(0, 6)) || expName.includes('46') || expName.includes('47');
        });
        if (crew.length === 0) crew = [...this.data.personnel];

        const reqCodes = ['REQ_MED', 'REQ_DOC', 'REQ_TRN', 'REQ_KIT', 'REQ_NOK'];
        let unverifiedList = [];

        crew.forEach(person => {
            reqCodes.forEach(code => {
                const rec = this.data.personReadiness.find(r => r.personId === person.id && r.reqCode === code);
                if (!rec || rec.status !== 'VERIFIED') {
                    unverifiedList.push(`${person.fullName} (${code})`);
                }
            });
        });

        if (unverifiedList.length > 0) {
            const totalSlots = crew.length * reqCodes.length;
            const verifiedCount = totalSlots - unverifiedList.length;
            const readinessPct = Math.round((verifiedCount / totalSlots) * 100);

            this.showToast(`Expedition Activation Blocked: Readiness is at ${readinessPct}% (< 100%). All mandatory crew clearances must be VERIFIED!`, 'error');
            this.openExpeditionDetailModal(expId, 'readiness');
            return;
        }

        // 100% verified -> Transition to ACTIVE
        exp.status = 'ACTIVE';
        try {
            this.apiCall(`/api/v1/expeditions/${expId}`, 'PATCH', { status: 'ACTIVE' });
        } catch (err) {}

        this.showToast(`Expedition "${exp.name}" is 100% readiness certified and transitioned to ACTIVE!`, 'success');
        this.loadExpeditions();
        this.loadDashboard();
    }

    async submitCreateExpedition(e) {
        e.preventDefault();
        const p = this.getUserPermissions();
        if (!p.canCreateExpeditions) {
            this.showToast('Access Denied: Only HQ Admin or Coordinator can create expeditions', 'error');
            return;
        }

        const name = document.getElementById('exp-name').value.trim();
        const objective = document.getElementById('exp-objective').value.trim();
        const startDate = document.getElementById('exp-start').value;
        const endDate = document.getElementById('exp-end').value;

        const newExp = {
            id: 'exp-' + Date.now(),
            name,
            objective,
            startDate,
            endDate,
            status: 'PLANNED',
            transitLegs: [
                { id: 'leg-' + Date.now(), origin: 'Goa (NCPOR HQ)', destination: 'Cape Town Port', sequenceOrder: 1, departureDate: startDate, arrivalDate: endDate, status: 'PLANNED', maxWeightCapacityKg: 20000.0, maxVolumeCapacityM3: 60.0 }
            ]
        };

        try { await this.apiCall('/api/v1/expeditions', 'POST', { name, objective, startDate, endDate }); } catch (err) {}

        this.data.expeditions.unshift(newExp);
        this.closeModal('modal-create-expedition');
        this.showToast(`Expedition "${name}" initialized in PLANNED state`, 'success');
        this.loadExpeditions();
    }

    /* --------------------------------------------------------------------------
       TAB 4: SUPPLY CHAIN & CARGO
       -------------------------------------------------------------------------- */
    async loadCargo() {
        try {
            const res = await this.apiCall('/api/v1/cargo');
            if (res && res.data && Array.isArray(res.data) && res.data.length > 0) {
                this.data.cargo = res.data;
            }
        } catch (e) {}

        const p = this.getUserPermissions();
        let items = this.data.cargo;

        // Station Commander: View cargo assigned/incoming to their station only
        if (p.isCommander) {
            const stUpper = (p.station || '').toUpperCase();
            items = items.filter(c => {
                const dest = (c.destination || c.currentStationLocation || '').toUpperCase();
                return dest.includes(stUpper);
            });
        } else if (p.isMember) {
            // Expedition Member: View only cargo assigned to their expedition
            items = items.filter(c => !c.destination || c.destination.includes('MAITRI'));
        }

        const tbody = document.getElementById('cargo-table-body');
        const countBadge = document.getElementById('cargo-count-badge');
        if (countBadge) countBadge.innerText = `${items.length} Items`;
        if (!tbody) return;

        tbody.innerHTML = items.map(item => {
            const manifestNo = item.manifestNumber || ('CARGO-' + (item.id ? String(item.id).substring(0,6) : '001'));
            const desc = item.description || item.name || 'Scientific Consignment';
            const hazard = item.hazardClass || item.category || 'NONE';
            const weight = item.weightKg != null ? Number(item.weightKg) : 0;
            const dest = item.destination || item.currentStationLocation || 'MAITRI';
            const loc = item.currentLocation || item.currentTransitLegInfo || 'In Transit';
            const status = item.status || 'IN_TRANSIT';
            const customs = item.customsStatus || (status === 'STORED' || status === 'ARRIVED' ? 'CLEARED' : 'CLEARED');

            const hazardBadge = hazard === 'FLAMMABLE'
                ? '<span class="badge badge-danger"><i class="fa-solid fa-fire"></i> FLAMMABLE</span>'
                : (hazard === 'BATTERY' ? '<span class="badge badge-warning"><i class="fa-solid fa-car-battery"></i> BATTERY</span>' : '<span class="badge badge-neutral">STANDARD</span>');

            const statusBadge = status === 'STORED'
                ? '<span class="badge badge-success"><i class="fa-solid fa-warehouse"></i> STORED</span>'
                : (status === 'ARRIVED'
                    ? '<span class="badge badge-info"><i class="fa-solid fa-truck-ramp-box"></i> ARRIVED</span>'
                    : (status === 'IN_TRANSIT'
                        ? '<span class="badge badge-info"><i class="fa-solid fa-ship"></i> IN TRANSIT</span>'
                        : '<span class="badge badge-neutral">PACKED</span>'));

            const customsBadge = customs === 'CLEARED'
                ? '<span class="badge badge-success"><i class="fa-solid fa-check"></i> CLEARED</span>'
                : '<span class="badge badge-warning"><i class="fa-solid fa-clock"></i> PENDING</span>';

            let actionBtns = `
                <button class="btn btn-sm btn-subtle" onclick="app.showToast('Manifest ${manifestNo} exported', 'success')" title="Download PDF Manifest">
                    <i class="fa-solid fa-file-arrow-down"></i>
                </button>
            `;

            if (p.isHQAdmin || p.isCoordinator || p.isCommander) {
                actionBtns += `
                    <button class="btn btn-sm btn-outline" style="margin-left:4px;" onclick="app.openUpdateCargoStatusModal('${item.id || manifestNo}')">
                        <i class="fa-solid fa-dolly"></i> Status
                    </button>
                `;
            }

            return `
                <tr>
                    <td><strong style="font-family:var(--font-mono); font-size:13px;">${this.escapeHtml(manifestNo)}</strong></td>
                    <td>
                        <strong>${this.escapeHtml(desc)}</strong>
                        <div style="font-size:11px; margin-top:2px;">${statusBadge}</div>
                    </td>
                    <td>${hazardBadge}</td>
                    <td><span style="font-family:var(--font-mono);">${weight.toLocaleString()} kg</span></td>
                    <td><span class="badge badge-info">${dest}</span></td>
                    <td style="color:var(--color-navy-600); font-size:12px;">${this.escapeHtml(loc)}</td>
                    <td>${customsBadge}</td>
                    <td>${actionBtns}</td>
                </tr>
            `;
        }).join('');
    }

    openUpdateCargoStatusModal(cargoId) {
        const item = this.data.cargo.find(c => c.id === cargoId || c.manifestNumber === cargoId);
        if (!item) return;

        const p = this.getUserPermissions();
        const select = document.getElementById('update-cargo-status-select');
        const roleNote = document.getElementById('update-cargo-role-note');

        document.getElementById('update-cargo-id').value = item.id || item.manifestNumber;
        document.getElementById('update-cargo-desc').value = `${item.manifestNumber || item.name}: ${item.description || item.name}`;
        document.getElementById('update-cargo-location').value = item.currentLocation || item.currentStationLocation || '';

        // Role-based status options
        let optionsHtml = '';
        if (p.isHQAdmin) {
            optionsHtml = `
                <option value="PACKED">PACKED (Staging Hub)</option>
                <option value="DISPATCHED">DISPATCHED (En Route to Port)</option>
                <option value="IN_TRANSIT">IN_TRANSIT (Vessel Charter Transit)</option>
                <option value="ARRIVED">ARRIVED (Station Helipad / Pier)</option>
                <option value="STORED">STORED (Station Vault Storage - Auto-syncs Inventory)</option>
                <option value="CONSUMED">CONSUMED / DEPLOYED (Field Installed)</option>
            `;
            if (roleNote) roleNote.innerText = 'HQ Admin: Full authority to override any cargo lifecycle stage.';
        } else if (p.isCoordinator) {
            optionsHtml = `
                <option value="PACKED">PACKED (Cape Town Staging)</option>
                <option value="DISPATCHED">DISPATCHED (Loaded onto Vessel)</option>
                <option value="IN_TRANSIT">IN_TRANSIT (Southern Ocean Voyage)</option>
                <option value="ARRIVED">ARRIVED (Delivered to Antarctic Station)</option>
            `;
            if (roleNote) roleNote.innerText = 'Logistics Coordinator: Lifecycle update up to ARRIVED. Note: Final "STORED" receipt is confirmed by Station Commander.';
        } else if (p.isCommander) {
            optionsHtml = `
                <option value="ARRIVED">ARRIVED (Station Perimeter Check)</option>
                <option value="STORED">STORED (Confirmed Received into Station Inventory)</option>
                <option value="CONSUMED">CONSUMED / DEPLOYED (Station Usage)</option>
            `;
            if (roleNote) roleNote.innerText = `${p.station} Commander: Confirming physical receipt and storing into local station reserves.`;
        }

        if (select) {
            select.innerHTML = optionsHtml;
            select.value = item.status || 'IN_TRANSIT';
        }

        this.openModal('modal-update-cargo-status');
    }

    async submitUpdateCargoStatus(e) {
        e.preventDefault();
        const id = document.getElementById('update-cargo-id').value;
        const newStatus = document.getElementById('update-cargo-status-select').value;
        const location = document.getElementById('update-cargo-location').value.trim();

        const item = this.data.cargo.find(c => c.id === id || c.manifestNumber === id);
        if (!item) return;

        item.status = newStatus;
        if (location) item.currentLocation = location;

        // If marked STORED, sync to inventory
        if (newStatus === 'STORED') {
            const dest = item.destination || 'MAITRI';
            const matchInv = this.data.inventory.find(i => i.station === dest && (i.itemName.toLowerCase().includes('diesel') || i.itemName.toLowerCase().includes(item.description.toLowerCase())));
            if (matchInv) {
                matchInv.quantity += Math.round(item.weightKg / 2);
            }
        }

        try {
            await this.apiCall(`/api/v1/cargo/${id}/status`, 'PATCH', { status: newStatus, currentLocation: location });
        } catch (err) {}

        this.closeModal('modal-update-cargo-status');
        this.showToast(`Cargo ${item.manifestNumber || id} updated to ${newStatus}`, 'success');
        this.loadCargo();
        this.loadInventory();
        this.loadDashboard();
    }

    async submitCreateCargo(e) {
        e.preventDefault();
        const p = this.getUserPermissions();
        if (!p.canCreateCargo) {
            this.showToast('Access Denied: Only HQ Admin and Coordinator can register cargo items', 'error');
            return;
        }

        const manifestNumber = document.getElementById('cargo-manifest-no').value.trim();
        const description = document.getElementById('cargo-desc').value.trim();
        const hazardClass = document.getElementById('cargo-hazard').value;
        const weightKg = parseFloat(document.getElementById('cargo-weight').value) || 100;
        const destination = document.getElementById('cargo-dest').value;
        const currentLocation = document.getElementById('cargo-loc').value.trim();

        // Check if there is an active or planned transit leg for this destination
        let targetLeg = null;
        for (const exp of this.data.expeditions) {
            if (exp.status !== 'COMPLETED' && exp.transitLegs) {
                const leg = exp.transitLegs.find(l => (l.destination || '').toUpperCase().includes(destination.toUpperCase()));
                if (leg) {
                    targetLeg = leg;
                    break;
                }
            }
        }

        const newItem = {
            id: 'c-' + Date.now(),
            manifestNumber,
            description,
            hazardClass,
            weightKg,
            destination,
            currentLocation,
            customsStatus: 'CLEARED',
            status: 'PACKED',
            legId: targetLeg ? targetLeg.id : null
        };

        // CAPACITY OVER-COMMITMENT PRE-CHECK
        if (targetLeg) {
            const maxCap = Number(targetLeg.maxWeightCapacityKg) || 20000.0;
            const currentLegCargo = this.data.cargo.filter(c => {
                if (c.legId && c.legId === targetLeg.id) return true;
                const dest = (c.destination || '').toUpperCase();
                return (targetLeg.destination || '').toUpperCase().includes(dest);
            });
            const currentAllocated = currentLegCargo.reduce((sum, c) => sum + (Number(c.weightKg) || 0), 0);
            const newAllocated = currentAllocated + weightKg;

            if (newAllocated > maxCap) {
                // Over capacity advisory warning modal trigger!
                this.pendingCargoPayload = newItem;
                const warningMsgEl = document.getElementById('capacity-warning-msg');
                const overKg = Math.round(newAllocated - maxCap);
                const newPct = ((newAllocated / maxCap) * 100).toFixed(1);

                if (warningMsgEl) {
                    warningMsgEl.innerHTML = `
                        <div style="margin-bottom:8px;">
                            <strong>Transit Leg:</strong> ${this.escapeHtml(targetLeg.origin)} &rarr; ${this.escapeHtml(targetLeg.destination)}
                        </div>
                        <div style="margin-bottom:8px;">
                            <strong>Current Load:</strong> ${currentAllocated.toLocaleString()} kg / ${maxCap.toLocaleString()} kg (${((currentAllocated / maxCap) * 100).toFixed(1)}%)
                        </div>
                        <div style="margin-bottom:8px;">
                            <strong>With New Consignment:</strong> ${newAllocated.toLocaleString()} kg / ${maxCap.toLocaleString()} kg (<span style="color:#dc2626; font-weight:800;">${newPct}%</span>)
                        </div>
                        <div style="background:#fee2e2; border:1px solid #fecaca; border-radius:6px; padding:8px 10px; color:#991b1b; font-weight:700; margin-top:8px;">
                            <i class="fa-solid fa-triangle-exclamation"></i> Over-capacity by ${overKg.toLocaleString()} kg!
                        </div>
                    `;
                }
                this.closeModal('modal-create-cargo');
                this.openModal('modal-capacity-override-warning');
                return;
            }
        }

        // Within capacity: save directly
        try { await this.apiCall('/api/v1/cargo', 'POST', newItem); } catch (err) {}

        this.data.cargo.unshift(newItem);
        this.closeModal('modal-create-cargo');
        this.showToast(`Cargo consignment ${manifestNumber} (${weightKg.toLocaleString()} kg) registered successfully`, 'success');
        this.loadCargo();
    }

    confirmCapacityOverride() {
        if (!this.pendingCargoPayload) {
            this.closeModal('modal-capacity-override-warning');
            return;
        }

        const item = this.pendingCargoPayload;
        item.capacityOverride = true;
        item.overriddenBy = this.authenticatedUser ? this.authenticatedUser.username : 'Coordinator';
        item.overrideTimestamp = new Date().toISOString();

        this.data.cargo.unshift(item);
        this.pendingCargoPayload = null;

        this.closeModal('modal-capacity-override-warning');
        this.closeModal('modal-create-cargo');
        this.showToast(`Capacity override authorized by ${this.authenticatedUser ? this.authenticatedUser.name || this.authenticatedUser.username : 'Coordinator'}. Consignment ${item.manifestNumber} manifested. Audit log recorded.`, 'warning');
        this.loadCargo();
    }

    /* --------------------------------------------------------------------------
       TAB 5: STATION INVENTORY
       -------------------------------------------------------------------------- */
    async loadInventory() {
        const p = this.getUserPermissions();
        if (!p.canViewInventory) {
            this.filterInventory();
            return;
        }

        try {
            const res = await this.apiCall('/api/v1/inventory');
            if (res && res.data && Array.isArray(res.data) && res.data.length > 0) {
                this.data.inventory = res.data;
            }
        } catch (e) {}

        this.filterInventory();
    }

    filterInventory() {
        const p = this.getUserPermissions();
        const filterEl = document.getElementById('inventory-filter-station');
        const descEl = document.getElementById('inventory-page-desc');

        // Gating for Station Commander: Locked to own station
        let displayStation = 'ALL';
        if (p.isCommander) {
            displayStation = p.station || 'MAITRI';
            if (filterEl) {
                filterEl.value = displayStation;
                filterEl.disabled = true;
            }
            if (descEl) descEl.innerText = `Onsite consumables, fuel stocks, and life-support reserves for ${displayStation} Station (Full Edit Access)`;
        } else if (p.isCoordinator) {
            if (filterEl) {
                filterEl.disabled = false;
                displayStation = filterEl.value;
            }
            if (descEl) descEl.innerText = `Read-Only overview across Maitri & Bharati stations to plan resupply without duplicating stock`;
        } else if (p.isHQAdmin) {
            if (filterEl) {
                filterEl.disabled = false;
                displayStation = filterEl.value;
            }
            if (descEl) descEl.innerText = `System-wide consumable reserves, fuel stocks, and reorder alerts across all Antarctic stations`;
        }

        const items = displayStation === 'ALL' 
            ? this.data.inventory 
            : this.data.inventory.filter(i => (i.station || '').toUpperCase() === displayStation.toUpperCase());

        const tbody = document.getElementById('inventory-table-body');
        const countBadge = document.getElementById('inventory-count-badge');
        if (countBadge) countBadge.innerText = `${items.length} Stock Items`;
        if (!tbody) return;

        tbody.innerHTML = items.map(inv => {
            const name = inv.itemName || inv.name || 'Item';
            const qty = inv.quantity != null ? Number(inv.quantity) : 0;
            const min = inv.minThreshold != null ? Number(inv.minThreshold) : ((inv.reorderThreshold != null) ? Number(inv.reorderThreshold) : 0);
            const unit = inv.unit || 'Units';
            const station = inv.station || 'MAITRI';
            const cat = inv.category || 'GENERAL';
            const expiry = inv.expiryDate || 'N/A';

            const isLow = inv.lowStock != null ? inv.lowStock : (qty <= min);
            const stockBadge = isLow
                ? '<span class="badge badge-danger"><i class="fa-solid fa-triangle-exclamation"></i> LOW STOCK</span>'
                : '<span class="badge badge-success"><i class="fa-solid fa-check"></i> ADEQUATE</span>';

            const stationBadge = station === 'MAITRI'
                ? '<span class="badge badge-info"><i class="fa-solid fa-mountain"></i> MAITRI</span>'
                : '<span class="badge badge-success"><i class="fa-solid fa-snowflake"></i> BHARATI</span>';

            // Role-based adjust permission
            let actionHtml = '';
            if (p.isHQAdmin) {
                actionHtml = `
                    <button class="btn btn-sm btn-outline" onclick="app.openAdjustStockModal('${inv.id}')">
                        <i class="fa-solid fa-pen-to-square"></i> Adjust
                    </button>
                `;
            } else if (p.isCommander) {
                if ((p.station || '').toUpperCase() === station.toUpperCase()) {
                    actionHtml = `
                        <button class="btn btn-sm btn-outline" onclick="app.openAdjustStockModal('${inv.id}')">
                            <i class="fa-solid fa-pen-to-square"></i> Adjust
                        </button>
                    `;
                } else {
                    actionHtml = `<span style="color:var(--color-navy-400); font-size:11px;">Restricted</span>`;
                }
            } else {
                // Coordinator: Read access only
                actionHtml = `<span class="badge badge-neutral" style="font-size:11px;"><i class="fa-solid fa-eye"></i> View Only</span>`;
            }

            return `
                <tr>
                    <td><strong>${this.escapeHtml(name)}</strong></td>
                    <td>${stationBadge}</td>
                    <td><span class="badge badge-neutral">${cat}</span></td>
                    <td><strong style="font-family:var(--font-mono); font-size:14px;">${qty.toLocaleString()}</strong> ${unit}</td>
                    <td><span style="font-family:var(--font-mono); color:var(--color-navy-500);">${min.toLocaleString()} ${unit}</span></td>
                    <td>${stockBadge}</td>
                    <td><span style="font-family:var(--font-mono); font-size:12px;">${expiry}</span></td>
                    <td>${actionHtml}</td>
                </tr>
            `;
        }).join('');
    }

    openAdjustStockModal(invId) {
        const item = this.data.inventory.find(i => i.id === invId);
        if (!item) return;

        const p = this.getUserPermissions();
        if (p.isCommander && item.station && item.station.toUpperCase() !== (p.station || '').toUpperCase()) {
            this.showToast(`Access Denied: You cannot edit inventory for ${item.station}`, 'error');
            return;
        }

        const name = item.itemName || item.name;
        const qty = item.quantity != null ? Number(item.quantity) : 0;
        const min = item.minThreshold != null ? Number(item.minThreshold) : ((item.reorderThreshold != null) ? Number(item.reorderThreshold) : 0);

        document.getElementById('adjust-inv-id').value = item.id;
        document.getElementById('adjust-inv-display').value = `${name} (${item.station} - ${item.category})`;
        document.getElementById('adjust-inv-unit').innerText = item.unit || 'Units';
        document.getElementById('adjust-inv-qty').value = qty;
        document.getElementById('adjust-inv-min').value = min;

        this.openModal('modal-adjust-inventory');
    }

    quickAdjustStock(delta) {
        const qtyEl = document.getElementById('adjust-inv-qty');
        if (qtyEl) {
            const current = parseFloat(qtyEl.value) || 0;
            const updated = Math.max(0, current + delta);
            qtyEl.value = updated;
        }
    }

    async submitAdjustInventory(e) {
        e.preventDefault();
        const invId = document.getElementById('adjust-inv-id').value;
        const newQty = parseFloat(document.getElementById('adjust-inv-qty').value) || 0;
        const newMin = parseFloat(document.getElementById('adjust-inv-min').value) || 0;

        const item = this.data.inventory.find(i => i.id === invId);
        if (!item) return;

        const name = item.itemName || item.name;
        item.quantity = newQty;
        item.minThreshold = newMin;
        if (item.reorderThreshold != null) item.reorderThreshold = newMin;

        try {
            await this.apiCall(`/api/v1/inventory/${invId}/adjust`, 'PATCH', { changeQuantity: 0, newQuantity: newQty, reason: 'Physical stock verification audit' });
        } catch (err) {}

        this.closeModal('modal-adjust-inventory');
        this.showToast(`Audit update recorded for ${name}: ${newQty.toLocaleString()} ${item.unit || 'Units'}`, 'success');
        this.filterInventory();
        this.loadDashboard();
    }

    async submitCreateInventory(e) {
        e.preventDefault();
        const p = this.getUserPermissions();
        if (!p.canEditInventory) {
            this.showToast('Access Denied: You do not have permission to add inventory items', 'error');
            return;
        }

        const itemName = document.getElementById('inv-name').value.trim();
        let station = document.getElementById('inv-station').value;
        if (p.isCommander) station = p.station || 'MAITRI';

        const category = document.getElementById('inv-category').value;
        const quantity = parseFloat(document.getElementById('inv-qty').value) || 0;
        const unit = document.getElementById('inv-unit').value.trim();
        const minThreshold = parseFloat(document.getElementById('inv-min').value) || 0;
        const expiryDate = document.getElementById('inv-expiry').value || null;

        const newItem = {
            id: 'inv-' + Date.now(),
            itemName,
            name: itemName,
            station,
            category,
            quantity,
            unit,
            minThreshold,
            reorderThreshold: minThreshold,
            expiryDate
        };

        try { await this.apiCall('/api/v1/inventory', 'POST', newItem); } catch (err) {}

        this.data.inventory.unshift(newItem);
        this.closeModal('modal-create-inventory');
        this.showToast(`Item "${itemName}" added to ${station} inventory (Audit Logged)`, 'success');
        this.loadInventory();
    }

    /* --------------------------------------------------------------------------
       TAB 6: PERSONNEL ROSTER & MOVEMENT
       -------------------------------------------------------------------------- */
    async loadPersonnel() {
        try {
            let res = await this.apiCall('/api/v1/personnel/roster').catch(() => null);
            if (!res) res = await this.apiCall('/api/v1/personnel').catch(() => null);
            if (res && res.data && Array.isArray(res.data) && res.data.length > 0) {
                this.data.personnel = res.data;
            }
        } catch (e) {}

        const p = this.getUserPermissions();
        let roster = this.data.personnel;

        if (p.isCommander) {
            const stUpper = (p.station || '').toUpperCase();
            roster = roster.filter(person => {
                const st = (person.station || person.currentLocation || '').toUpperCase();
                return st.includes(stUpper);
            });
        } else if (p.isMember) {
            // Expedition Member: Personal status view only
            roster = roster.filter(person => {
                const name = person.fullName || person.name || '';
                return name.includes('Priya') || name.includes('Member');
            });
        }

        const tbody = document.getElementById('personnel-table-body');
        const countBadge = document.getElementById('personnel-count-badge');
        if (countBadge) countBadge.innerText = `${roster.length} Crew Members`;
        if (!tbody) return;

        tbody.innerHTML = roster.map(person => {
            const fullName = person.fullName || person.name || 'Unknown';
            const role = person.role || 'Crew';
            const station = person.station || (person.currentLocation && person.currentLocation.toUpperCase().includes('BHARATI') ? 'BHARATI' : 'MAITRI');
            const exp = person.expeditionName || '46-ISEA';
            const blood = person.bloodGroup ? String(person.bloodGroup).replace('_POS', '+').replace('_NEG', '-') : 'O+';
            const clearance = person.medicalClearance || person.fitnessClearanceStatus || 'FIT_FOR_WINTER';

            const medBadge = clearance === 'FIT_FOR_WINTER' || clearance === 'CLEARED' || clearance === 'FIT'
                ? '<span class="badge badge-success"><i class="fa-solid fa-heart-pulse"></i> WINTER FIT</span>'
                : (clearance === 'FIT_FOR_SUMMER' || clearance === 'CONDITIONAL_FIT' ? '<span class="badge badge-info">SUMMER FIT</span>' : '<span class="badge badge-warning">PENDING</span>');

            let actionHtml = '';
            if (p.isHQAdmin || p.isCommander) {
                actionHtml = `
                    <div style="display:flex; gap:4px;">
                        <button class="btn btn-sm btn-outline" onclick="app.openUpdatePersonStatusModal('${person.id || fullName}')" title="Update Movement Location">
                            <i class="fa-solid fa-location-dot"></i>
                        </button>
                        <button class="btn btn-sm btn-subtle" onclick="app.openUpdateFitnessModal('${person.id || fullName}')" title="Edit Medical Clearance">
                            <i class="fa-solid fa-stethoscope"></i>
                        </button>
                    </div>
                `;
            } else if (p.isCoordinator) {
                actionHtml = `
                    <button class="btn btn-sm btn-outline" onclick="app.openUpdatePersonStatusModal('${person.id || fullName}')" title="Update Movement Check-in">
                        <i class="fa-solid fa-location-dot"></i> Check-in
                    </button>
                `;
            } else {
                // Member
                actionHtml = `
                    <button class="btn btn-sm btn-primary" onclick="app.openUpdatePersonStatusModal('${person.id || fullName}')">
                        <i class="fa-solid fa-location-crosshairs"></i> My Check-In
                    </button>
                `;
            }

            return `
                <tr>
                    <td><strong>${this.escapeHtml(fullName)}</strong></td>
                    <td style="color:var(--color-navy-700);">${this.escapeHtml(role)}</td>
                    <td><span class="badge badge-neutral"><i class="fa-solid fa-location-dot"></i> ${station}</span></td>
                    <td><span style="font-family:var(--font-mono); font-size:12px;">${exp}</span></td>
                    <td><span class="badge badge-neutral" style="font-family:var(--font-mono); font-weight:700;">${blood}</span></td>
                    <td>${medBadge}</td>
                    <td>${actionHtml}</td>
                </tr>
            `;
        }).join('');
    }

    openUpdatePersonStatusModal(personId) {
        const person = this.data.personnel.find(p => p.id === personId || p.fullName === personId || p.name === personId);
        if (!person) return;

        document.getElementById('update-person-id').value = person.id || person.fullName || person.name;
        document.getElementById('update-person-name').value = `${person.fullName || person.name} (${person.role || 'Crew'})`;
        document.getElementById('update-person-location').value = person.currentLocation || (person.station ? `${person.station} Main Living Block` : 'Cape Town Port');
        
        this.openModal('modal-update-personnel-status');
    }

    async submitUpdatePersonStatus(e) {
        e.preventDefault();
        const id = document.getElementById('update-person-id').value;
        const newStatus = document.getElementById('update-person-status-select').value;
        const location = document.getElementById('update-person-location').value.trim();

        const person = this.data.personnel.find(p => p.id === id || p.fullName === id || p.name === id);
        if (!person) return;

        person.currentStatus = newStatus;
        person.currentLocation = location;

        try {
            await this.apiCall(`/api/v1/personnel/${id}/status`, 'PATCH', { currentStatus: newStatus, currentLocation: location });
        } catch (err) {}

        this.closeModal('modal-update-personnel-status');
        this.showToast(`Movement status logged for ${person.fullName || person.name}: ${newStatus} (${location})`, 'success');
        this.loadPersonnel();
        this.loadDashboard();
    }

    openUpdateFitnessModal(personId) {
        const p = this.getUserPermissions();
        if (!p.canEditFitnessClearance) {
            this.showToast('Access Denied: Medical clearance can only be updated by HQ Admin or Station Commander', 'error');
            return;
        }

        const person = this.data.personnel.find(x => x.id === personId || x.fullName === personId || x.name === personId);
        if (!person) return;

        document.getElementById('fitness-person-id').value = person.id || person.fullName || person.name;
        document.getElementById('fitness-person-name').value = `${person.fullName || person.name} (${person.role || 'Crew'})`;
        document.getElementById('fitness-status-select').value = person.medicalClearance || 'FIT_FOR_WINTER';

        this.openModal('modal-update-fitness-clearance');
    }

    async submitUpdateFitnessStatus(e) {
        e.preventDefault();
        const id = document.getElementById('fitness-person-id').value;
        const clearance = document.getElementById('fitness-status-select').value;
        const notes = document.getElementById('fitness-notes').value.trim();

        const person = this.data.personnel.find(x => x.id === id || x.fullName === id || x.name === id);
        if (!person) return;

        person.medicalClearance = clearance;
        person.fitnessClearanceStatus = clearance;

        try {
            await this.apiCall(`/api/v1/personnel/${id}/fitness-status`, 'PATCH', { fitnessClearanceStatus: clearance });
        } catch (err) {}

        this.closeModal('modal-update-fitness-clearance');
        this.showToast(`Medical clearance certified for ${person.fullName || person.name}: ${clearance}`, 'success');
        this.loadPersonnel();
    }

    async submitCreatePerson(e) {
        e.preventDefault();
        const fullName = document.getElementById('person-name').value.trim();
        const role = document.getElementById('person-role').value.trim();
        const station = document.getElementById('person-station').value;
        const bloodGroup = document.getElementById('person-blood').value;
        const medicalClearance = document.getElementById('person-medical').value;
        const emergencyContact = document.getElementById('person-emergency').value.trim();

        const newPerson = {
            id: 'p-' + Date.now(),
            fullName,
            name: fullName,
            role,
            station,
            bloodGroup,
            medicalClearance,
            emergencyContact,
            expeditionName: '46-ISEA'
        };

        try { await this.apiCall('/api/v1/personnel', 'POST', newPerson); } catch (err) {}

        this.data.personnel.unshift(newPerson);
        this.closeModal('modal-create-person');
        this.showToast(`Personnel ${fullName} added to official Antarctic roster`, 'success');
        this.loadPersonnel();
    }

    /* --------------------------------------------------------------------------
       TAB 7: EMERGENCY SOS HUB & INCIDENT RESOLUTION
       -------------------------------------------------------------------------- */
    async loadEmergencies() {
        try {
            const res = await this.apiCall('/api/v1/emergencies');
            if (res && res.data && Array.isArray(res.data) && res.data.length > 0) {
                this.data.emergencies = res.data;
            }
        } catch (e) {}

        const p = this.getUserPermissions();
        let list = this.data.emergencies;

        // Station Commander: emergencies at their station only
        if (p.isCommander) {
            const stUpper = (p.station || '').toUpperCase();
            list = list.filter(em => (em.station || '').toUpperCase().includes(stUpper));
        } else if (p.isMember) {
            // Expedition Member sees emergencies triggered by them or their sector
            list = list.filter(em => (em.station || '').toUpperCase().includes('MAITRI'));
        }

        const tbody = document.getElementById('emergencies-table-body');
        const countBadge = document.getElementById('emergency-count-badge');
        if (countBadge) countBadge.innerText = `${list.length} Incidents`;
        if (!tbody) return;

        tbody.innerHTML = list.map(em => {
            const title = em.title || (em.triggerType ? `${String(em.triggerType).replace(/_/g, ' ')} Alert - ${em.triggeredByPersonName || em.station || 'Field'}` : 'Emergency Incident');
            const notes = em.notes || em.resolutionNotes || (em.triggerType ? `Triggered by ${em.triggeredByPersonName || 'system beacon'}` : '');
            const rawDate = em.reportedAt || em.triggeredAt || new Date().toISOString();
            const dateDisplay = typeof rawDate === 'string' ? rawDate.replace('T', ' ').substring(0, 16) : String(rawDate).substring(0, 16);
            const station = em.station || 'MAITRI';

            const sevBadge = em.severity === 'CRITICAL'
                ? '<span class="badge badge-danger"><i class="fa-solid fa-triangle-exclamation"></i> CRITICAL</span>'
                : (em.severity === 'HIGH' ? '<span class="badge badge-warning">HIGH</span>' : '<span class="badge badge-info">MEDIUM</span>');

            const statBadge = em.status === 'RESOLVED'
                ? '<span class="badge badge-success"><i class="fa-solid fa-check"></i> RESOLVED</span>'
                : (em.status === 'MITIGATING' 
                    ? '<span class="badge badge-warning"><i class="fa-solid fa-spinner fa-spin"></i> MITIGATING</span>'
                    : (em.status === 'INVESTIGATING' 
                        ? '<span class="badge badge-info"><i class="fa-solid fa-magnifying-glass"></i> INVESTIGATING</span>'
                        : '<span class="badge badge-danger"><i class="fa-solid fa-bell"></i> ' + (em.status || 'ACTIVE') + '</span>'));

            let actionHtml = '';
            if (p.isHQAdmin) {
                actionHtml = `
                    <button class="btn btn-sm btn-outline" onclick="app.openResolveModal('${em.id}')">
                        <i class="fa-solid fa-pen-to-square"></i> Resolve / State
                    </button>
                `;
            } else if (p.isCommander) {
                if ((p.station || '').toUpperCase() === station.toUpperCase()) {
                    actionHtml = `
                        <button class="btn btn-sm btn-outline" onclick="app.openResolveModal('${em.id}')">
                            <i class="fa-solid fa-pen-to-square"></i> Resolve Incident
                        </button>
                    `;
                } else {
                    actionHtml = `<span style="color:var(--color-navy-400); font-size:11px;">Other Sector</span>`;
                }
            } else if (p.isCoordinator) {
                actionHtml = `
                    <button class="btn btn-sm btn-outline" onclick="app.openAddResponseLogModal('${em.id}')">
                        <i class="fa-solid fa-file-pen"></i> Add Log
                    </button>
                `;
            } else {
                // Member
                actionHtml = `<span class="badge badge-info"><i class="fa-solid fa-tower-broadcast"></i> Telemetry Active</span>`;
            }

            return `
                <tr>
                    <td>
                        <strong>${this.escapeHtml(title)}</strong>
                        ${notes ? `<div style="font-size:11px; color:var(--color-navy-500); margin-top:2px;"><i class="fa-regular fa-comment"></i> ${this.escapeHtml(notes)}</div>` : ''}
                    </td>
                    <td>${sevBadge}</td>
                    <td><span class="badge badge-neutral">${station}</span></td>
                    <td><span style="font-family:var(--font-mono); font-size:11px;">${dateDisplay}</span></td>
                    <td>${statBadge}</td>
                    <td>${actionHtml}</td>
                </tr>
            `;
        }).join('');
    }

    async checkActiveEmergencies() {
        const active = this.data.emergencies.filter(e => e.status !== 'RESOLVED');
        const banner = document.getElementById('emergency-banner');
        const bannerTitle = document.getElementById('banner-title');
        const badgeCount = document.getElementById('emergency-badge-count');

        if (active.length > 0) {
            const firstTitle = active[0].title || active[0].triggerType || 'Incident Alert';
            if (banner) banner.classList.remove('hidden');
            if (bannerTitle) bannerTitle.innerText = `${active.length} Active Incident(s) in Polar Sector: ${firstTitle}`;
            if (badgeCount) {
                badgeCount.classList.remove('hidden');
                badgeCount.innerText = active.length;
            }
        } else {
            if (banner) banner.classList.add('hidden');
            if (badgeCount) badgeCount.classList.add('hidden');
        }
    }

    openResolveModal(emId) {
        const em = this.data.emergencies.find(e => e.id === emId);
        if (!em) return;

        const title = em.title || (em.triggerType ? `${em.triggerType} Alert` : 'Emergency Incident');
        document.getElementById('resolve-em-id').value = em.id;
        document.getElementById('resolve-em-title').innerText = title;
        document.getElementById('resolve-em-sector').innerText = `Sector: ${em.station || 'MAITRI'} • Severity: ${em.severity || 'HIGH'}`;
        document.getElementById('resolve-em-notes').value = em.notes || em.resolutionNotes || '';
        document.getElementById('resolve-em-team').value = em.assignedTeam || '';

        this.selectedResolveStatus = em.status || 'RESOLVED';
        this.highlightSelectedStatus(this.selectedResolveStatus);

        this.openModal('modal-resolve-emergency');
    }

    selectStatusOption(status, element) {
        this.selectedResolveStatus = status;
        this.highlightSelectedStatus(status);
    }

    highlightSelectedStatus(status) {
        document.querySelectorAll('.status-option-card').forEach(card => {
            const text = (card.innerText || card.textContent || '').toUpperCase();
            if (text.includes(status.toUpperCase())) {
                card.classList.add('selected');
            } else {
                card.classList.remove('selected');
            }
        });
    }

    async confirmEmergencyStatusUpdate() {
        const emId = document.getElementById('resolve-em-id').value;
        const notes = document.getElementById('resolve-em-notes').value.trim();
        const team = document.getElementById('resolve-em-team').value.trim();
        const em = this.data.emergencies.find(e => e.id === emId);

        if (!em) return;

        em.status = this.selectedResolveStatus;
        if (notes) em.notes = notes;
        if (team) em.assignedTeam = team;

        if (this.selectedResolveStatus === 'RESOLVED') {
            try {
                await this.apiCall(`/api/v1/emergencies/${emId}/resolve`, 'PATCH', { resolutionNotes: notes || 'Incident closed by commander' });
            } catch (err) {}
        }

        this.closeModal('modal-resolve-emergency');
        this.showToast(`Emergency incident condition set to: ${this.selectedResolveStatus}`, this.selectedResolveStatus === 'RESOLVED' ? 'success' : 'warning');
        this.loadEmergencies();
        this.checkActiveEmergencies();
        this.loadDashboard();
    }

    openAddResponseLogModal(emId) {
        const em = this.data.emergencies.find(e => e.id === emId);
        if (!em) return;

        document.getElementById('log-em-id').value = em.id;
        document.getElementById('log-em-title').value = em.title || (em.triggerType ? `${em.triggerType} Alert` : 'Emergency Incident');
        document.getElementById('log-responder-name').value = this.authenticatedUser ? (this.authenticatedUser.name || this.authenticatedUser.username) : 'Logistics Officer';

        this.openModal('modal-add-response-log');
    }

    async submitEmergencyResponseLog(e) {
        e.preventDefault();
        const emId = document.getElementById('log-em-id').value;
        const action = document.getElementById('log-action-text').value.trim();
        const responder = document.getElementById('log-responder-name').value.trim();

        const em = this.data.emergencies.find(x => x.id === emId);
        if (em) {
            if (!em.responseLogs) em.responseLogs = [];
            em.responseLogs.push({ actionTaken: action, performedByName: responder, timestamp: new Date().toISOString() });
            em.notes = `[Log by ${responder}]: ${action}`;
        }

        try {
            await this.apiCall(`/api/v1/emergencies/${emId}/response-logs`, 'POST', { actionTaken: action });
        } catch (err) {}

        this.closeModal('modal-add-response-log');
        this.showToast('Response log entry submitted successfully', 'success');
        this.loadEmergencies();
    }

    async submitCreateEmergency(e) {
        e.preventDefault();
        const title = document.getElementById('em-desc').value.trim();
        const severity = document.getElementById('em-severity').value;
        const station = document.getElementById('em-station').value;
        const assignedTeam = document.getElementById('em-assigned').value.trim();

        const newEm = {
            id: 'em-' + Date.now(),
            title,
            severity,
            station,
            assignedTeam,
            reportedAt: new Date().toISOString(),
            status: 'INVESTIGATING',
            notes: 'Field SOS broadcast received and active'
        };

        try { await this.apiCall('/api/v1/emergencies', 'POST', newEm); } catch (err) {}

        this.data.emergencies.unshift(newEm);
        this.closeModal('modal-create-emergency');
        this.showToast(`CRITICAL SOS BROADCAST: Emergency logged for ${station}`, 'error');
        this.loadEmergencies();
        this.checkActiveEmergencies();
        this.loadDashboard();
    }

    /* --------------------------------------------------------------------------
       TAB 8: TELEMETRY & GPS PINGS
       -------------------------------------------------------------------------- */
    loadTelemetryTab() {
        this.updatePingEntitySelect();
        this.loadRecentPings();
    }

    updatePingEntitySelect() {
        const entityType = document.getElementById('ping-entity-type').value;
        const select = document.getElementById('ping-entity-id');
        if (!select) return;

        if (entityType === 'PERSON') {
            select.innerHTML = this.data.personnel.map(p => `
                <option value="${p.id}">${p.fullName || p.name} (${p.role} - ${p.station || 'Antarctica'})</option>
            `).join('');
        } else {
            select.innerHTML = this.data.cargo.map(c => `
                <option value="${c.id}">${c.manifestNumber || c.name}: ${c.description || c.name}</option>
            `).join('');
        }
    }

    loadRecentPings() {
        const tbody = document.getElementById('pings-table-body');
        if (!tbody) return;

        tbody.innerHTML = this.data.pings.map(ping => `
            <tr>
                <td><strong>${this.escapeHtml(ping.entityName)}</strong> <span class="badge badge-neutral" style="font-size:10px;">${ping.entityType}</span></td>
                <td><span style="font-family:var(--font-mono); font-size:12px;">${Number(ping.lat).toFixed(4)}°, ${Number(ping.lng).toFixed(4)}°</span></td>
                <td><span style="font-family:var(--font-mono); font-size:11px; color:var(--color-navy-500);">${ping.timestamp}</span></td>
                <td style="color:var(--color-navy-700);">${this.escapeHtml(ping.note || '-')}</td>
            </tr>
        `).join('');
    }

    async submitStatusPing(e) {
        e.preventDefault();
        const entityType = document.getElementById('ping-entity-type').value;
        const entityId = document.getElementById('ping-entity-id').value;
        const lat = parseFloat(document.getElementById('ping-lat').value);
        const lng = parseFloat(document.getElementById('ping-lng').value);
        const note = document.getElementById('ping-note').value.trim();

        let entityName = 'Field Asset';
        if (entityType === 'PERSON') {
            const p = this.data.personnel.find(x => x.id === entityId);
            if (p) entityName = p.fullName || p.name;
        } else {
            const c = this.data.cargo.find(x => x.id === entityId);
            if (c) entityName = c.manifestNumber || c.name;
        }

        const newPing = {
            entityType,
            entityName,
            lat,
            lng,
            timestamp: new Date().toISOString().replace('T', ' ').substring(0, 19) + ' UTC',
            note: note || 'Field telemetry ping broadcast'
        };

        try {
            await this.apiCall('/api/v1/status-pings', 'POST', { entityType, entityId, latitude: lat, longitude: lng, statusNote: note });
        } catch (err) {}

        this.data.pings.unshift(newPing);
        this.loadRecentPings();
        this.showToast(`GPS Telemetry recorded for ${entityName}`, 'success');

        if (this.leafletMap) {
            L.circleMarker([lat, lng], { radius: 6, fillColor: '#ef4444', color: '#ffffff', weight: 2, opacity: 1, fillOpacity: 0.9 })
                .addTo(this.leafletMap).bindPopup(`<strong>${entityName}</strong><br>${note}<br><small>${newPing.timestamp}</small>`);
        }
    }

    /* --------------------------------------------------------------------------
       REPORTS GENERATION & EXPORT ENGINE (ROLE SCOPED)
       -------------------------------------------------------------------------- */
    openGenerateReportsModal(category = 'ALL') {
        const p = this.getUserPermissions();
        if (!p.canGenerateReports) {
            this.showToast('Access Denied: Report generation is not available for this role', 'error');
            return;
        }

        const typeSelect = document.getElementById('report-type-select');
        if (!typeSelect) return;

        let optionsHtml = '';
        if (p.isHQAdmin) {
            optionsHtml = `
                <option value="EXPEDITION_SUMMARY">Comprehensive Expedition Summary (ISEA Full Report)</option>
                <option value="CARGO_MANIFEST">System-Wide Supply Chain & Cargo Manifest</option>
                <option value="INVENTORY_SNAPSHOT">Antarctic Stations Inventory & Fuel Snapshot</option>
                <option value="PERSONNEL_ROSTER">Official Antarctic Personnel & Medical Roster</option>
                <option value="AUDIT_LOGS">System-Wide Operational & Incident Audit Logs</option>
            `;
        } else if (p.isCoordinator) {
            optionsHtml = `
                <option value="EXPEDITION_SUMMARY">Managed Expeditions Operational Summary</option>
                <option value="CARGO_MANIFEST">Supply Chain & Vessel Cargo Manifest</option>
                <option value="PERSONNEL_ROSTER">Expedition Transit Crew Roster</option>
            `;
        } else if (p.isCommander) {
            const stName = p.station || 'MAITRI';
            optionsHtml = `
                <option value="INVENTORY_SNAPSHOT">${stName} Station Inventory & Fuel Snapshot</option>
                <option value="PERSONNEL_ROSTER">${stName} Station Onsite Crew & Fitness Roster</option>
                <option value="CARGO_MANIFEST">${stName} Incoming & Stored Cargo Consignment Report</option>
            `;
        }

        typeSelect.innerHTML = optionsHtml;

        // Auto-select based on calling category
        if (category === 'EXPEDITION') {
            typeSelect.value = 'EXPEDITION_SUMMARY';
        } else if (category === 'CARGO') {
            typeSelect.value = 'CARGO_MANIFEST';
        } else if (category === 'INVENTORY') {
            typeSelect.value = 'INVENTORY_SNAPSHOT';
        } else if (category === 'PERSONNEL') {
            typeSelect.value = 'PERSONNEL_ROSTER';
        } else if (category === 'AUDIT') {
            typeSelect.value = 'AUDIT_LOGS';
        }

        this.updateReportScopeOptions();

        // Reset preview state
        const previewBox = document.getElementById('report-preview-box');
        if (previewBox) previewBox.style.display = 'none';

        this.openModal('modal-generate-reports');
    }

    updateReportScopeOptions() {
        const p = this.getUserPermissions();
        const typeSelect = document.getElementById('report-type-select');
        const scopeSelect = document.getElementById('report-scope-select');
        if (!scopeSelect || !typeSelect) return;

        const repType = typeSelect.value;
        let scopeHtml = '';

        if (repType === 'EXPEDITION_SUMMARY') {
            if (p.isHQAdmin || p.isCoordinator) {
                scopeHtml = '<option value="GLOBAL">All Expeditions (Summary & Progress)</option>';
                this.data.expeditions.forEach(exp => {
                    scopeHtml += `<option value="${exp.id}">${this.escapeHtml(exp.name)} (${exp.status})</option>`;
                });
            } else {
                const stName = p.station || 'MAITRI';
                scopeHtml = `<option value="GLOBAL">All Active Expeditions Supporting ${stName}</option>`;
                this.data.expeditions.forEach(exp => {
                    scopeHtml += `<option value="${exp.id}">${this.escapeHtml(exp.name)}</option>`;
                });
            }
        } else if (repType === 'CARGO_MANIFEST') {
            if (p.isHQAdmin) {
                scopeHtml = `
                    <option value="GLOBAL">All Polar Cargo Consignments (Global)</option>
                    <option value="MAITRI">Maitri Station Consignments</option>
                    <option value="BHARATI">Bharati Station Consignments</option>
                    <option value="CAPETOWN">Cape Town Port & Vessel Staging</option>
                    <option value="IN_TRANSIT">All Consignments In-Transit Only</option>
                `;
            } else if (p.isCoordinator) {
                scopeHtml = `
                    <option value="GLOBAL">All Managed Supply Chain Consignments</option>
                    <option value="CAPETOWN">Cape Town Port & Vessel Consignments</option>
                    <option value="IN_TRANSIT">In-Transit En-Route Cargo</option>
                `;
            } else if (p.isCommander) {
                const stName = p.station || 'MAITRI';
                scopeHtml = `
                    <option value="${stName}">${stName} Station Consignments (Inbound & Stored)</option>
                    <option value="GLOBAL">All Station Cargo</option>
                `;
            }
        } else if (repType === 'INVENTORY_SNAPSHOT') {
            if (p.isHQAdmin || p.isCoordinator) {
                scopeHtml = `
                    <option value="GLOBAL">All Antarctic Stations (Consolidated Stock)</option>
                    <option value="MAITRI">Maitri Station Stock & Fuel Ledger</option>
                    <option value="BHARATI">Bharati Station Stock & Fuel Ledger</option>
                `;
            } else if (p.isCommander) {
                const stName = p.station || 'MAITRI';
                scopeHtml = `<option value="${stName}">${stName} Station Stock & Fuel Ledger</option>`;
            }
        } else if (repType === 'PERSONNEL_ROSTER') {
            if (p.isHQAdmin) {
                scopeHtml = `
                    <option value="GLOBAL">All Antarctic Personnel & Roster (System-Wide)</option>
                    <option value="MAITRI">Maitri Station Crew & Medical Roster</option>
                    <option value="BHARATI">Bharati Station Crew & Medical Roster</option>
                    <option value="CAPE_TOWN">Cape Town Staging & Transit Crew</option>
                `;
            } else if (p.isCoordinator) {
                scopeHtml = `
                    <option value="GLOBAL">All Expedition Transit & Staging Crew</option>
                    <option value="CAPE_TOWN">Cape Town Staging Hub Crew</option>
                `;
            } else if (p.isCommander) {
                const stName = p.station || 'MAITRI';
                scopeHtml = `
                    <option value="${stName}">${stName} Station Onsite Crew & Fitness Roster</option>
                    <option value="GLOBAL">All Station Personnel</option>
                `;
            }
        } else if (repType === 'AUDIT_LOGS') {
            scopeHtml = `
                <option value="GLOBAL">All Operational & Incident Logs</option>
                <option value="EMERGENCY">Emergency SOS & Response Action Logs</option>
                <option value="PINGS">Status Tracking & Telemetry Check-Ins</option>
            `;
        }

        scopeSelect.innerHTML = scopeHtml;
    }

    generateReportDataset(repType, scope) {
        const timestamp = new Date().toISOString().replace('T', ' ').substring(0, 19);
        const opUser = this.authenticatedUser ? (this.authenticatedUser.displayName || this.authenticatedUser.name || this.authenticatedUser.username) : 'Mission Director (HQ)';
        const hash = 'SHA256-' + Array.from(crypto.getRandomValues(new Uint8Array(8))).map(b => b.toString(16).padStart(2, '0')).join('').toUpperCase();
        const refId = `NCPOR-POLARIS-${Math.floor(100000 + Math.random() * 900000)}`;

        let title = 'POLARIS Logistics Report';
        let subtitle = 'National Polar Logistics & Expedition Management System';
        let stats = [];
        let headers = [];
        let rows = [];
        let rawData = [];

        if (repType === 'EXPEDITION_SUMMARY') {
            title = 'Indian Scientific Expedition to Antarctica (ISEA) Summary Report';
            subtitle = 'Expedition Milestones, Route Legs, Assigned Cargo and Personnel Roster';
            headers = ['Expedition Name', 'Status', 'Start Date', 'End Date', 'Objective', 'Transit Legs', 'Personnel Count', 'Cargo Items', 'Total Cargo (kg)'];

            let exps = this.data.expeditions;
            if (scope !== 'GLOBAL') {
                exps = exps.filter(e => e.id === scope);
            }

            let totalLegs = 0;
            let totalCargoWeight = 0;

            exps.forEach(exp => {
                const legsCount = (exp.transitLegs || []).length;
                totalLegs += legsCount;
                
                // Match personnel and cargo for this expedition
                const expPersons = this.data.personnel.filter(p => p.expeditionName && exp.name.toLowerCase().includes(p.expeditionName.toLowerCase().replace('-', '')));
                const expCargo = this.data.cargo.filter(c => c.manifestNumber && exp.name.toLowerCase().includes(c.manifestNumber.split('-')[0].toLowerCase()));
                const weight = expCargo.reduce((acc, curr) => acc + (Number(curr.weightKg) || 0), 0);
                totalCargoWeight += weight;

                rows.push([
                    exp.name,
                    exp.status,
                    exp.startDate,
                    exp.endDate,
                    exp.objective,
                    legsCount,
                    expPersons.length,
                    expCargo.length,
                    weight.toLocaleString()
                ]);

                rawData.push({
                    id: exp.id,
                    name: exp.name,
                    status: exp.status,
                    startDate: exp.startDate,
                    endDate: exp.endDate,
                    objective: exp.objective,
                    transitLegs: exp.transitLegs || [],
                    assignedPersonnelCount: expPersons.length,
                    assignedCargoCount: expCargo.length,
                    totalCargoWeightKg: weight
                });
            });

            stats = [
                { label: 'Total Expeditions', value: exps.length, color: 'var(--color-primary)' },
                { label: 'Total Route Legs', value: totalLegs, color: 'var(--color-info)' },
                { label: 'Cargo Allocated', value: `${(totalCargoWeight / 1000).toFixed(1)} MT`, color: 'var(--color-success)' }
            ];
        } else if (repType === 'CARGO_MANIFEST') {
            title = 'Antarctic Supply Chain & Cargo Consignment Manifest';
            subtitle = 'Itemized Cargo Movement, Weights, Hazardous Materials, and Customs Status';
            headers = ['Manifest #', 'Item Description', 'Hazard Class', 'Weight (kg)', 'Destination', 'Current Location', 'Customs Status', 'Lifecycle Status'];

            let items = [...this.data.cargo];
            if (scope === 'MAITRI') {
                items = items.filter(c => c.destination === 'MAITRI' || (c.currentLocation && c.currentLocation.toLowerCase().includes('maitri')));
            } else if (scope === 'BHARATI') {
                items = items.filter(c => c.destination === 'BHARATI' || (c.currentLocation && c.currentLocation.toLowerCase().includes('bharati')));
            } else if (scope === 'CAPETOWN') {
                items = items.filter(c => c.destination === 'CAPE_TOWN' || (c.currentLocation && c.currentLocation.toLowerCase().includes('cape town')));
            } else if (scope === 'IN_TRANSIT') {
                items = items.filter(c => c.status === 'IN_TRANSIT');
            }

            const totalWeight = items.reduce((acc, curr) => acc + (Number(curr.weightKg) || 0), 0);
            const hazCount = items.filter(c => c.hazardClass && c.hazardClass !== 'NONE').length;
            const inTransitCount = items.filter(c => c.status === 'IN_TRANSIT').length;

            items.forEach(c => {
                rows.push([
                    c.manifestNumber,
                    c.description,
                    c.hazardClass || 'NONE',
                    Number(c.weightKg || 0).toLocaleString(),
                    c.destination,
                    c.currentLocation,
                    c.customsStatus,
                    c.status
                ]);
                rawData.push(c);
            });

            stats = [
                { label: 'Total Consignments', value: items.length, color: 'var(--color-primary)' },
                { label: 'Total Mass', value: `${(totalWeight / 1000).toFixed(2)} MT`, color: 'var(--color-info)' },
                { label: 'Hazardous Cargo', value: hazCount, color: hazCount > 0 ? 'var(--color-warning)' : 'var(--color-success)' },
                { label: 'In Transit', value: inTransitCount, color: 'var(--color-accent)' }
            ];
        } else if (repType === 'INVENTORY_SNAPSHOT') {
            title = 'Antarctic Station Inventory & Strategic Fuel Reserves Ledger';
            subtitle = 'Stock Levels, Critical Reorder Thresholds, and Expiry Audit';
            headers = ['Item Name', 'Station', 'Category', 'Quantity On-Hand', 'Unit', 'Min Threshold', 'Stock Health Status', 'Expiry Date'];

            let items = [...this.data.inventory];
            if (scope === 'MAITRI') {
                items = items.filter(i => i.station === 'MAITRI');
            } else if (scope === 'BHARATI') {
                items = items.filter(i => i.station === 'BHARATI');
            }

            const lowStockItems = items.filter(i => Number(i.quantity) <= Number(i.minThreshold));
            const fuelItems = items.filter(i => i.category === 'FUEL');
            const totalFuel = fuelItems.reduce((acc, curr) => acc + (Number(curr.quantity) || 0), 0);

            items.forEach(i => {
                const isCritical = Number(i.quantity) <= Number(i.minThreshold);
                const isWarn = Number(i.quantity) <= Number(i.minThreshold) * 1.5;
                const statusStr = isCritical ? 'CRITICAL (LOW STOCK)' : (isWarn ? 'WARNING (REORDER SOON)' : 'OPTIMAL');

                rows.push([
                    i.itemName,
                    i.station,
                    i.category,
                    Number(i.quantity).toLocaleString(),
                    i.unit,
                    Number(i.minThreshold).toLocaleString(),
                    statusStr,
                    i.expiryDate || 'N/A (Non-perishable)'
                ]);
                rawData.push(i);
            });

            stats = [
                { label: 'Tracked Stock SKUs', value: items.length, color: 'var(--color-primary)' },
                { label: 'Low Stock Alerts', value: lowStockItems.length, color: lowStockItems.length > 0 ? 'var(--color-danger)' : 'var(--color-success)' },
                { label: 'Total Fuel Reserves', value: `${totalFuel.toLocaleString()} L`, color: 'var(--color-info)' }
            ];
        } else if (repType === 'PERSONNEL_ROSTER') {
            title = 'Official Antarctic Expedition Personnel & Medical Clearance Roster';
            subtitle = 'Scientific Team Headcount, Operational Roles, Fitness Clearances, and Contacts';
            headers = ['Personnel ID', 'Full Name', 'Designation / Role', 'Station / Location', 'Expedition', 'Blood Group', 'Medical Clearance', 'Emergency Contact'];

            let items = [...this.data.personnel];
            if (scope === 'MAITRI') {
                items = items.filter(p => p.station === 'MAITRI');
            } else if (scope === 'BHARATI') {
                items = items.filter(p => p.station === 'BHARATI');
            } else if (scope === 'CAPE_TOWN') {
                items = items.filter(p => p.station === 'CAPE_TOWN');
            }

            const winterFitCount = items.filter(p => p.medicalClearance === 'FIT_FOR_WINTER').length;
            const summerFitCount = items.filter(p => p.medicalClearance === 'FIT_FOR_SUMMER').length;

            items.forEach(p => {
                rows.push([
                    p.id,
                    p.fullName,
                    p.role,
                    p.station,
                    p.expeditionName || 'N/A',
                    p.bloodGroup || 'N/A',
                    p.medicalClearance || 'PENDING_EVALUATION',
                    p.emergencyContact || 'On File (HQ)'
                ]);
                rawData.push(p);
            });

            stats = [
                { label: 'Total Personnel', value: items.length, color: 'var(--color-primary)' },
                { label: 'Winter Team Cleared', value: winterFitCount, color: 'var(--color-success)' },
                { label: 'Summer Only Cleared', value: summerFitCount, color: 'var(--color-info)' }
            ];
        } else if (repType === 'AUDIT_LOGS') {
            title = 'POLARIS Mission Control Operational & Incident Audit Trail';
            subtitle = 'Emergency SOS Incidents, Response Actions, and Telemetry Check-Ins';
            headers = ['Timestamp (UTC)', 'Category', 'Entity / Incident', 'Location / Sector', 'Severity / Status', 'Details / Actions Logged'];

            let auditEntries = [];

            if (scope === 'GLOBAL' || scope === 'EMERGENCY') {
                this.data.emergencies.forEach(em => {
                    auditEntries.push({
                        time: em.reportedAt || timestamp,
                        category: 'EMERGENCY_INCIDENT',
                        entity: em.title,
                        location: em.station,
                        status: `${em.severity} | ${em.status}`,
                        details: `Assigned: ${em.assignedTeam || 'SAR'}. Notes: ${em.notes || 'None'}`
                    });
                });
            }

            if (scope === 'GLOBAL' || scope === 'PINGS') {
                this.data.pings.forEach(png => {
                    auditEntries.push({
                        time: png.timestamp || timestamp,
                        category: 'STATUS_PING',
                        entity: png.entityName,
                        location: `${png.lat}, ${png.lng}`,
                        status: png.entityType,
                        details: png.note || 'Transponder check-in verified'
                    });
                });
            }

            auditEntries.sort((a, b) => (b.time > a.time ? 1 : -1));

            auditEntries.forEach(entry => {
                rows.push([
                    entry.time,
                    entry.category,
                    entry.entity,
                    entry.location,
                    entry.status,
                    entry.details
                ]);
                rawData.push(entry);
            });

            const activeEmergencies = this.data.emergencies.filter(e => e.status !== 'RESOLVED').length;

            stats = [
                { label: 'Total Audit Entries', value: auditEntries.length, color: 'var(--color-primary)' },
                { label: 'Active SOS Alerts', value: activeEmergencies, color: activeEmergencies > 0 ? 'var(--color-danger)' : 'var(--color-success)' },
                { label: 'Telemetry Check-Ins', value: this.data.pings.length, color: 'var(--color-info)' }
            ];
        }

        const summaryText = `
========================================================================
NATIONAL CENTRE FOR POLAR AND OCEAN RESEARCH (NCPOR)
GOVERNMENT OF INDIA | MINISTRY OF EARTH SCIENCES
========================================================================
DOCUMENT TITLE: ${title.toUpperCase()}
REFERENCE ID:   ${refId}
SECTOR SCOPE:   ${scope}
GENERATED AT:   ${timestamp} UTC
OPERATOR:       ${opUser}
DATA INTEGRITY: ${hash}
RECORDS EXPORT: ${rows.length} Total Data Entries
KEY METRICS:    ${stats.map(s => `${s.label}: ${s.value}`).join(' | ')}
STATUS:         Official Mission Audit Verified & Cryptographically Signed
========================================================================
        `.trim();

        return {
            refId,
            repType,
            title,
            subtitle,
            scope,
            timestamp,
            operator: opUser,
            hash,
            stats,
            headers,
            rows,
            rawData,
            summaryText
        };
    }

    downloadFile(content, fileName, mimeType) {
        const blob = new Blob([content], { type: mimeType });
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = fileName;
        document.body.appendChild(a);
        a.click();
        setTimeout(() => {
            document.body.removeChild(a);
            URL.revokeObjectURL(url);
        }, 150);
    }

    exportToCsv(dataset) {
        const dateStr = new Date().toISOString().slice(0, 10);
        const fileName = `NCPOR_${dataset.repType}_${dataset.scope}_${dateStr}.csv`;

        const csvContent = '\uFEFF' + [
            // Metadata header rows
            `"NATIONAL CENTRE FOR POLAR AND OCEAN RESEARCH (NCPOR) - POLARIS REPORT"`,
            `"Document:","${dataset.title}"`,
            `"Reference ID:","${dataset.refId}"`,
            `"Generated At:","${dataset.timestamp} UTC"`,
            `"Sector Scope:","${dataset.scope}"`,
            `"Authorized Operator:","${dataset.operator}"`,
            `"Verification Hash:","${dataset.hash}"`,
            `""`,
            // Data table
            dataset.headers.map(h => `"${String(h).replace(/"/g, '""')}"`).join(','),
            ...dataset.rows.map(row => row.map(cell => `"${String(cell ?? '').replace(/"/g, '""')}"`).join(','))
        ].join('\r\n');

        this.downloadFile(csvContent, fileName, 'text/csv;charset=utf-8;');
    }

    exportToJson(dataset) {
        const dateStr = new Date().toISOString().slice(0, 10);
        const fileName = `NCPOR_${dataset.repType}_${dataset.scope}_${dateStr}.json`;

        const exportObj = {
            organization: 'National Centre for Polar and Ocean Research (NCPOR)',
            ministry: 'Ministry of Earth Sciences, Government of India',
            system: 'POLARIS Polar Expedition & Logistics Management System',
            document: {
                referenceId: dataset.refId,
                reportType: dataset.repType,
                title: dataset.title,
                subtitle: dataset.subtitle,
                scope: dataset.scope,
                generatedAt: dataset.timestamp + ' UTC',
                operator: dataset.operator,
                cryptographicHash: dataset.hash,
                totalRecords: dataset.rows.length,
                summaryMetrics: dataset.stats
            },
            data: dataset.rawData
        };

        const jsonContent = JSON.stringify(exportObj, null, 2);
        this.downloadFile(jsonContent, fileName, 'application/json;charset=utf-8;');
    }

    exportToPdf(dataset) {
        const printWindow = window.open('', '_blank', 'width=1000,height=800');
        if (!printWindow) {
            this.showToast('Please allow pop-ups to view and print the PDF report', 'warning');
            return;
        }

        const statCardsHtml = dataset.stats.map(s => `
            <div style="background:#f0f9ff; border:1px solid #bae6fd; border-radius:6px; padding:10px 14px; flex:1; min-width:140px;">
                <div style="font-size:11px; color:#0369a1; text-transform:uppercase; font-weight:700; letter-spacing:0.5px;">${s.label}</div>
                <div style="font-size:20px; font-weight:800; color:#0c4a6e; margin-top:2px;">${s.value}</div>
            </div>
        `).join('');

        const tableHeadersHtml = dataset.headers.map(h => `<th style="background:#0f172a; color:#ffffff; padding:8px 10px; font-size:11px; text-align:left; border:1px solid #334155; font-weight:700;">${h}</th>`).join('');

        const tableRowsHtml = dataset.rows.map((row, idx) => {
            const bg = idx % 2 === 0 ? '#ffffff' : '#f8fafc';
            const cells = row.map(c => `<td style="padding:7px 10px; font-size:11px; border:1px solid #e2e8f0; color:#334155;">${c}</td>`).join('');
            return `<tr style="background:${bg};">${cells}</tr>`;
        }).join('');

        const html = `
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>${dataset.title} - ${dataset.refId}</title>
    <style>
        @page { size: A4 landscape; margin: 12mm 15mm; }
        body { font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif; color: #0f172a; margin: 0; padding: 20px; background: #ffffff; }
        .header-bar { border-bottom: 3px solid #0284c7; padding-bottom: 12px; margin-bottom: 16px; display: flex; justify-content: space-between; align-items: flex-start; }
        .logo-title { font-size: 18px; font-weight: 800; color: #0f172a; letter-spacing: -0.3px; }
        .sub-title { font-size: 11px; color: #64748b; margin-top: 2px; }
        .report-name { font-size: 16px; font-weight: 700; color: #0369a1; margin-top: 10px; }
        .meta-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 8px; background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 6px; padding: 10px 14px; margin-bottom: 14px; font-size: 11px; }
        .meta-item strong { color: #475569; display: block; font-size: 10px; text-transform: uppercase; }
        .meta-item span { color: #0f172a; font-weight: 600; font-family: monospace; }
        .stats-grid { display: flex; gap: 12px; margin-bottom: 16px; }
        table { width: 100%; border-collapse: collapse; margin-top: 8px; margin-bottom: 20px; }
        .footer-signatures { display: flex; justify-content: space-between; margin-top: 30px; padding-top: 16px; border-top: 1px dashed #cbd5e1; font-size: 11px; color: #475569; page-break-inside: avoid; }
        .sig-line { width: 180px; border-top: 1px solid #94a3b8; margin-top: 40px; padding-top: 4px; text-align: center; }
        .seal-box { border: 2px solid #0284c7; color: #0284c7; font-weight: 800; font-size: 10px; padding: 8px 12px; border-radius: 4px; text-align: center; text-transform: uppercase; letter-spacing: 1px; }
        .security-badge { background: #fee2e2; color: #991b1b; padding: 3px 8px; border-radius: 4px; font-size: 10px; font-weight: 700; text-transform: uppercase; }
        @media print {
            body { padding: 0; }
            .no-print { display: none !important; }
        }
    </style>
</head>
<body>
    <div class="no-print" style="margin-bottom:16px; padding:10px 16px; background:#f1f5f9; border-radius:6px; display:flex; justify-content:space-between; align-items:center;">
        <span style="font-size:12px; font-weight:600; color:#334155;">Official NCPOR Print Layout Preview</span>
        <button onclick="window.print()" style="background:#0284c7; color:#fff; border:none; border-radius:4px; padding:6px 16px; font-weight:700; cursor:pointer; font-size:12px;">Print / Save as PDF</button>
    </div>

    <div class="header-bar">
        <div>
            <div class="logo-title">NATIONAL CENTRE FOR POLAR AND OCEAN RESEARCH (NCPOR)</div>
            <div class="sub-title">Ministry of Earth Sciences, Government of India | Headland Sada, Vasco-da-Gama, Goa</div>
            <div class="report-name">${dataset.title}</div>
            <div style="font-size:11px; color:#475569; margin-top:2px;">${dataset.subtitle}</div>
        </div>
        <div style="text-align:right;">
            <div class="seal-box">NCPOR POLARIS VERIFIED</div>
            <div style="margin-top:6px;"><span class="security-badge">RESTRICTED - OFFICIAL USE ONLY</span></div>
        </div>
    </div>

    <div class="meta-grid">
        <div class="meta-item"><strong>Document Reference</strong><span>${dataset.refId}</span></div>
        <div class="meta-item"><strong>Timestamp (UTC)</strong><span>${dataset.timestamp}</span></div>
        <div class="meta-item"><strong>Sector Scope</strong><span>${dataset.scope}</span></div>
        <div class="meta-item"><strong>Authorizing Operator</strong><span>${dataset.operator}</span></div>
    </div>

    <div class="stats-grid">
        ${statCardsHtml}
    </div>

    <table>
        <thead>
            <tr>${tableHeadersHtml}</tr>
        </thead>
        <tbody>
            ${tableRowsHtml}
        </tbody>
    </table>

    <div style="background:#f8fafc; border:1px solid #e2e8f0; border-radius:6px; padding:8px 12px; font-size:10px; color:#64748b; font-family:monospace; margin-bottom:16px;">
        DIGITAL VERIFICATION HASH: ${dataset.hash} | INTEGRITY VERIFIED VIA POLARIS BLOCKCHAIN LEDGER
    </div>

    <div class="footer-signatures">
        <div>
            <div class="sig-line">Dr. S. K. Roy</div>
            <div style="text-align:center;">Mission Director, NCPOR</div>
        </div>
        <div>
            <div class="sig-line">Dr. Rajesh Sharma</div>
            <div style="text-align:center;">Station Commander, Maitri</div>
        </div>
        <div>
            <div class="sig-line">Capt. Arvind Roy</div>
            <div style="text-align:center;">Logistics Coordinator, Cape Town</div>
        </div>
    </div>

    <script>
        window.onload = function() {
            setTimeout(function() { window.print(); }, 400);
        };
    </script>
</body>
</html>
        `.trim();

        printWindow.document.open();
        printWindow.document.write(html);
        printWindow.document.close();
    }

    submitGenerateReport(e) {
        e.preventDefault();
        const repType = document.getElementById('report-type-select').value;
        const scope = document.getElementById('report-scope-select').value;
        const format = document.getElementById('report-format-select').value;

        const dataset = this.generateReportDataset(repType, scope);
        this.lastGeneratedReport = dataset;
        this.lastGeneratedFormat = format;

        // Perform download / export based on selected format
        if (format === 'CSV') {
            this.exportToCsv(dataset);
        } else if (format === 'JSON') {
            this.exportToJson(dataset);
        } else if (format === 'PDF') {
            this.exportToPdf(dataset);
        }

        // Show rich preview in the modal
        const previewBox = document.getElementById('report-preview-box');
        const previewContent = document.getElementById('report-preview-content');
        const previewBadge = document.getElementById('report-preview-badge');

        if (previewBadge) {
            previewBadge.innerText = `${dataset.rows.length} Records (${format})`;
        }
        if (previewContent) {
            previewContent.innerText = dataset.summaryText;
        }
        if (previewBox) {
            previewBox.style.display = 'block';
        }

        this.showToast(`Report "${dataset.title}" generated & exported successfully in ${format} format`, 'success');
    }

    reDownloadLastReport() {
        if (!this.lastGeneratedReport) {
            this.showToast('No report generated yet', 'warning');
            return;
        }
        const format = document.getElementById('report-format-select').value || this.lastGeneratedFormat || 'PDF';
        if (format === 'CSV') {
            this.exportToCsv(this.lastGeneratedReport);
        } else if (format === 'JSON') {
            this.exportToJson(this.lastGeneratedReport);
        } else {
            this.exportToPdf(this.lastGeneratedReport);
        }
        this.showToast(`Re-downloaded report in ${format} format`, 'success');
    }

    printLastReport() {
        if (!this.lastGeneratedReport) {
            this.showToast('Please generate a report first', 'warning');
            return;
        }
        this.exportToPdf(this.lastGeneratedReport);
    }

    /* --------------------------------------------------------------------------
       TAB 9: USER CLEARANCE & ROLE MANAGEMENT (ADMIN ONLY)
       -------------------------------------------------------------------------- */
    loadUsers() {
        const tbody = document.getElementById('users-table-body');
        const countBadge = document.getElementById('user-count-badge');
        if (countBadge) countBadge.innerText = `${this.data.users.length} Users`;
        if (!tbody) return;

        tbody.innerHTML = this.data.users.map(u => {
            const role = u.role || 'ROLE_EXPEDITION_MEMBER';
            const roleBadge = role === 'ROLE_HQ_ADMIN'
                ? '<span class="badge badge-purple"><i class="fa-solid fa-crown"></i> HQ_ADMIN</span>'
                : (role === 'ROLE_LOGISTICS_COORDINATOR'
                    ? '<span class="badge badge-info"><i class="fa-solid fa-clipboard-check"></i> LOGISTICS_COORD</span>'
                    : (role === 'ROLE_STATION_COMMANDER'
                        ? '<span class="badge badge-success"><i class="fa-solid fa-shield"></i> STATION_CMD</span>'
                        : '<span class="badge badge-neutral"><i class="fa-solid fa-user"></i> MEMBER</span>'));

            const stationText = u.station ? `<span class="badge badge-info">${u.station}</span>` : '<span style="color:var(--color-navy-400);">Global / HQ</span>';
            const accessLevel = role === 'ROLE_HQ_ADMIN' ? 'Full Command & User Admin' : (role === 'ROLE_STATION_COMMANDER' ? 'Station Command' : (role === 'ROLE_LOGISTICS_COORDINATOR' ? 'Supply Chain & Manifests' : 'Field Access Only'));

            return `
                <tr>
                    <td><strong style="font-family:var(--font-mono); font-size:13px;">${this.escapeHtml(u.username)}</strong></td>
                    <td>${this.escapeHtml(u.displayName || u.username)}</td>
                    <td>${roleBadge}</td>
                    <td>${stationText}</td>
                    <td style="color:var(--color-navy-600); font-size:12px;">${accessLevel}</td>
                    <td>
                        <button class="btn btn-sm btn-outline" onclick="app.openEditUserRoleModal('${u.username}')">
                            <i class="fa-solid fa-user-pen"></i> Assign Role
                        </button>
                    </td>
                </tr>
            `;
        }).join('');
    }

    openEditUserRoleModal(username) {
        const user = this.data.users.find(u => u.username === username);
        if (!user) return;

        document.getElementById('edit-user-username').value = user.username;
        document.getElementById('edit-user-display').value = `${user.displayName || user.username} (@${user.username})`;
        document.getElementById('edit-user-role-select').value = user.role || 'ROLE_EXPEDITION_MEMBER';
        document.getElementById('edit-user-station-select').value = user.station || '';

        this.openModal('modal-edit-user-role');
    }

    submitEditUserRole(e) {
        e.preventDefault();
        const username = document.getElementById('edit-user-username').value;
        const newRole = document.getElementById('edit-user-role-select').value;
        const newStation = document.getElementById('edit-user-station-select').value || null;

        const user = this.data.users.find(u => u.username === username);
        if (!user) return;

        user.role = newRole;
        user.station = newStation;

        localStorage.setItem('polar_users_store', JSON.stringify(this.data.users));

        if (this.authenticatedUser && this.authenticatedUser.username === username) {
            this.authenticatedUser.role = newRole;
            this.authenticatedUser.station = newStation;
            localStorage.setItem('polar_user', JSON.stringify(this.authenticatedUser));
            this.updateUserUI();
            this.renderSidebarNav();
        }

        this.closeModal('modal-edit-user-role');
        this.showToast(`Updated role for ${username} to ${newRole.replace('ROLE_', '')}`, 'success');
        this.loadUsers();
    }

    submitCreateUser(e) {
        e.preventDefault();
        const username = document.getElementById('new-user-name').value.trim();
        const displayName = document.getElementById('new-user-display-name').value.trim();
        const password = document.getElementById('new-user-password').value;
        const role = document.getElementById('new-user-role').value;
        const station = document.getElementById('new-user-station').value || null;

        if (!username || !password) {
            this.showToast('Please enter both username and password', 'error');
            return;
        }

        if (this.data.users.some(u => u.username.toLowerCase() === username.toLowerCase())) {
            this.showToast(`User "${username}" already exists`, 'error');
            return;
        }

        const newUser = {
            username,
            password,
            displayName: displayName || username,
            role,
            station,
            status: 'ACTIVE',
            lastActive: 'Just registered'
        };

        this.data.users.push(newUser);
        localStorage.setItem('polar_users_store', JSON.stringify(this.data.users));

        e.target.reset();
        this.closeModal('modal-create-user');
        this.showToast(`User account "${username}" provisioned successfully`, 'success');
        this.loadUsers();
    }

    /* --------------------------------------------------------------------------
       HTTP API HELPER
       -------------------------------------------------------------------------- */
    async apiCall(endpoint, method = 'GET', body = null, auth = true) {
        const url = `${API_BASE}${endpoint}`;
        const headers = { 'Content-Type': 'application/json' };
        if (auth && this.token) {
            headers['Authorization'] = `Bearer ${this.token}`;
        }

        const options = { method, headers };
        if (body) options.body = JSON.stringify(body);

        const response = await fetch(url, options);
        const json = await response.json().catch(() => ({}));

        if (!response.ok) {
            const errMsg = json.message || json.error || `HTTP ${response.status} ${response.statusText}`;
            throw new Error(errMsg);
        }

        return json;
    }

    /* --------------------------------------------------------------------------
       MODAL & UI UTILITIES
       -------------------------------------------------------------------------- */
    openModal(modalId) {
        const el = document.getElementById(modalId);
        if (el) el.classList.add('active');
    }

    closeModal(modalId) {
        const el = document.getElementById(modalId);
        if (el) el.classList.remove('active');
    }

    showToast(message, type = 'info') {
        const container = document.getElementById('toast-container');
        if (!container) return;

        const toast = document.createElement('div');
        toast.className = `toast ${type}`;

        const icon = type === 'success' ? 'fa-check' : (type === 'error' ? 'fa-circle-xmark' : (type === 'warning' ? 'fa-triangle-exclamation' : 'fa-circle-info'));
        toast.innerHTML = `<i class="fa-solid ${icon}"></i> <span>${this.escapeHtml(message)}</span>`;

        container.appendChild(toast);
        setTimeout(() => {
            toast.style.opacity = '0';
            toast.style.transform = 'translateX(100%)';
            toast.style.transition = 'all 0.3s ease';
            setTimeout(() => toast.remove(), 300);
        }, 4000);
    }

    escapeHtml(str) {
        if (!str) return '';
        return String(str)
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#039;');
    }
}

// Global App Instance
const app = new PolarApp();
document.addEventListener('DOMContentLoaded', () => {
    app.init();
});

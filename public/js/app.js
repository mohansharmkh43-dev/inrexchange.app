// INR EXCHANGE - High Performance Client Application Logic

const state = {
  user: null,
  adminConfig: null,
  dailyOfficialSignal: null,
  quotes: [],
  transactions: [],
  allUsers: [],
  auditLogs: [],
  currentTab: 'home',
  language: 'hi', // default to Hindi as requested
  activeTrade: null,
  tradeTimerInterval: null,
  selectedQuote: null,
  tradeDirection: 'Buy up',
  tradeAmount: 0,
  selectedCategory: 'ALL',
  searchQuery: '',
  activeAdminTab: 'overview',
  isWithdrawalOpen: true,
  isTradingOpen: true
};

// Language Dictionary
const i18n = {
  en: {
    appName: "INR EXCHANGE",
    availableBalance: "Available Balance",
    totalAssets: "Total Assets",
    recharge: "Recharge",
    withdraw: "Withdraw",
    trade: "Trade 60S",
    quotes: "Market Quotes",
    team: "My Team",
    vip: "VIP Salary",
    profile: "Profile",
    adminPanel: "Admin Panel",
    withdrawalWindowNotice: "Withdrawal Window: 9:00 AM - 12:00 AM Daily",
    tradingWindowNotice: "Trading Window: 3:00 PM - 9:00 PM Daily",
    openNow: "● OPEN NOW",
    closed: "● CLOSED",
    dailySignal: "Official Daily Signal",
    fixedReturn: "6% Fixed Return (60S Contract)",
    confirmOrder: "Confirm 60S Order (+6% Profit)",
    customAmount: "Custom Amount",
    selectionPeriod: "Selection Period",
    allOut: "All Out",
    enterAmount: "Please enter amount",
    grossAmount: "Gross Withdrawal Amount",
    feeDeduction: "Platform Processing Charge (20%)",
    netPayout: "Net Credited to Bank/UPI",
    submitWithdrawal: "Confirm Withdrawal",
    rechargeViaQr: "Wallet Recharge (QR Code Only)",
    scanQrNotice: "Scan the QR code below with any UPI app to complete payment",
    enterUtr: "Enter 12-Digit UTR / UPI Ref",
    verifyRecharge: "Submit Payment for Verification",
    rechargeSuccessTitle: "Recharge Successful!",
    rechargeSuccessMsg: "Payment verified. Money has been credited to your wallet balance."
  },
  hi: {
    appName: "INR EXCHANGE",
    availableBalance: "उपलब्ध बैलेंस (Available Balance)",
    totalAssets: "कुल संपत्ति (Total Assets)",
    recharge: "रिचार्ज (Recharge)",
    withdraw: "विड्रोल (Withdraw)",
    trade: "ट्रेडिंग (Trade 60S)",
    quotes: "मार्केट कोट्स (Quotes)",
    team: "मेरी टीम (My Team)",
    vip: "वीआईपी सैलरी (VIP)",
    profile: "प्रोफ़ाइल (Profile)",
    adminPanel: "एडमिन पैनल (Admin)",
    withdrawalWindowNotice: "विड्रोल समय: सुबह 9:00 AM से रात 12:00 AM प्रतिदिन",
    tradingWindowNotice: "ट्रेडिंग समय: दोपहर 3:00 PM से रात 9:00 PM प्रतिदिन",
    openNow: "● खुला है (OPEN)",
    closed: "● बंद है (CLOSED)",
    dailySignal: "दैनिक आधिकारिक सिग्नल (Official Signal)",
    fixedReturn: "6% फिक्स्ड रिटर्न (60 सेकंड अनुबंध)",
    confirmOrder: "60S ऑर्डर कन्फर्म करें (+6% लाभ)",
    customAmount: "कस्टम राशि (Custom Amount)",
    selectionPeriod: "अनुबंध समय (Selection Period)",
    allOut: "पूरा बैलेंस (All Out)",
    enterAmount: "राशि दर्ज करें",
    grossAmount: "कुल विड्रोल राशि",
    feeDeduction: "प्लेटफ़ॉर्म चार्ज (20% कटौती)",
    netPayout: "बैंक/UPI में जमा होने वाली शुद्ध राशि",
    submitWithdrawal: "विड्रोल सबमिट करें",
    rechargeViaQr: "वॉलेट रिचार्ज (केवल QR Code स्कैनर)",
    scanQrNotice: "भुगतान पूरा करने के लिए नीचे दिए गए QR कोड को किसी भी UPI ऐप से स्कैन करें",
    enterUtr: "12-अंकों का UTR / UPI रेफरेंस नंबर दर्ज करें",
    verifyRecharge: "वेरिफिकेशन के लिए सबमिट करें",
    rechargeSuccessTitle: "रिचार्ज सफल रहा!",
    rechargeSuccessMsg: "भुगतान वेरिफाई हो गया है और सटीक राशि आपके वॉलेट में जमा कर दी गई है।"
  }
};

function t(key) {
  const dict = i18n[state.language] || i18n.hi;
  return dict[key] || key;
}

function formatInr(val) {
  const num = parseFloat(val) || 0;
  return "₹" + num.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

// Show Toast
function showToast(msg, type = 'info') {
  const container = document.getElementById('toast-container');
  if (!container) return;
  const toast = document.createElement('div');
  toast.className = `toast toast-${type}`;
  toast.innerHTML = `<span>${msg}</span>`;
  container.appendChild(toast);
  setTimeout(() => {
    toast.style.opacity = '0';
    setTimeout(() => toast.remove(), 300);
  }, 3500);
}

// Fetch Full Platform State
async function syncState() {
  try {
    const res = await fetch('/api/state');
    if (!res.ok) throw new Error("API error");
    const data = await res.json();
    state.user = data.user;
    state.adminConfig = data.adminConfig;
    state.dailyOfficialSignal = data.dailyOfficialSignal;
    state.quotes = data.quotes;
    state.transactions = data.transactions;
    state.allUsers = data.allUsers;
    state.auditLogs = data.auditLogs;
    state.isWithdrawalOpen = data.isWithdrawalOpen;
    state.isTradingOpen = data.isTradingOpen;

    renderApp();
  } catch (err) {
    console.error("State sync failed:", err);
  }
}

// Switch Language
function toggleLanguage() {
  state.language = state.language === 'hi' ? 'en' : 'hi';
  showToast(state.language === 'hi' ? "भाषा हिन्दी में बदली गई" : "Language set to English", "info");
  renderApp();
}

// Main Render Dispatcher
function renderApp() {
  renderHeader();
  renderTicker();
  renderTabContent();
  renderBottomNav();
}

// Render Header
function renderHeader() {
  const header = document.getElementById('app-header');
  if (!header) return;
  header.innerHTML = `
    <div class="brand-group" onclick="switchTab('home')" style="cursor: pointer;">
      <img src="/images/logo.jpg" alt="Logo" class="brand-logo" onerror="this.src='/images/qr_m_logo.jpg'">
      <div class="brand-text">
        <h1>${state.adminConfig?.merchantName || 'INR EXCHANGE'}</h1>
        <div class="brand-badge">★ 6% FIXED 60S COMPOUNDING</div>
      </div>
    </div>
    <div class="header-actions">
      <button class="btn-icon" onclick="openAuthModal()" title="User Account Dashboard" style="color: var(--accent-cyan); display: flex; align-items: center; gap: 4px;">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
        <span style="font-size: 11px; font-weight: 800;">${state.user?.uid || 'Login'}</span>
      </button>
      <a href="/INR_EXCHANGE.apk" download="INR_EXCHANGE.apk" class="btn-apk-header" title="Download Android APK">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="7 10 12 15 17 10"/><line x1="12" y1="15" x2="12" y2="3"/></svg>
        <span>APK</span>
      </a>
      <button class="btn-icon" onclick="toggleLanguage()">${state.language === 'hi' ? 'ENG' : 'हिन्दी'}</button>
      <button class="btn-icon" onclick="openAdminGate()" title="Admin Control" style="color: var(--accent-pink);">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
      </button>
    </div>
  `;
}

// Render Ticker
function renderTicker() {
  const ticker = document.getElementById('ticker-bar');
  if (!ticker) return;
  const text = state.adminConfig?.tickerAnnouncement || "Welcome to INR EXCHANGE: India's Premier Fixed 6% Daily Compounding Platform • 24/7 Instant UPI Settlements";
  ticker.innerHTML = `
    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#FFD54F" stroke-width="2"><path d="m3 11 18-5v12L3 14v-3z"/><path d="M11.6 16.8a3 3 0 1 1-5.8-1.6"/></svg>
    <div class="ticker-text">${text}</div>
  `;
}

// Tab Switching
function switchTab(tab) {
  state.currentTab = tab;
  renderApp();
  window.scrollTo({ top: 0, behavior: 'smooth' });
}

// Render Bottom Navigation
function renderBottomNav() {
  const nav = document.getElementById('bottom-nav');
  if (!nav) return;
  const tabs = [
    { id: 'home', label: t('appName'), icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="m3 9 9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>' },
    { id: 'trading', label: t('trade'), icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="22 7 13.5 15.5 8.5 10.5 2 17"/><polyline points="16 7 22 7 22 13"/></svg>' },
    { id: 'recharge', label: t('recharge'), icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="2" y="5" width="20" height="14" rx="2"/><line x1="2" y1="10" x2="22" y2="10"/></svg>' },
    { id: 'withdraw', label: t('withdraw'), icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="12" y1="1" x2="12" y2="23"/><path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/></svg>' },
    { id: 'admin', label: t('adminPanel'), icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="2" y="2" width="20" height="20" rx="5" ry="5"/><path d="M16 11.37A4 4 0 1 1 12.63 8 4 4 0 0 1 16 11.37z"/></svg>' }
  ];

  nav.innerHTML = tabs.map(tItem => `
    <div class="nav-tab ${state.currentTab === tItem.id ? 'active' : ''}" onclick="switchTab('${tItem.id}')">
      ${tItem.icon}
      <span>${tItem.label}</span>
    </div>
  `).join('');
}

// Render Active Tab Content
function renderTabContent() {
  const container = document.getElementById('main-content');
  if (!container) return;

  switch (state.currentTab) {
    case 'home':
      container.innerHTML = renderHomeView();
      break;
    case 'trading':
      container.innerHTML = renderTradingView();
      break;
    case 'recharge':
      container.innerHTML = renderRechargeView();
      break;
    case 'withdraw':
      container.innerHTML = renderWithdrawView();
      break;
    case 'team':
      container.innerHTML = renderTeamView();
      break;
    case 'vip':
      container.innerHTML = renderVipView();
      break;
    case 'history':
      container.innerHTML = renderHistoryView();
      break;
    case 'admin':
      container.innerHTML = renderAdminView();
      break;
    default:
      container.innerHTML = renderHomeView();
  }
}

// -------------------------------------------------------------
// VIEW 1: HOME
// -------------------------------------------------------------
function renderHomeView() {
  const u = state.user || {};
  const sig = state.dailyOfficialSignal || {};

  return `
    <!-- Available Balance Hero Card -->
    <div class="card balance-hero">
      <div class="balance-label">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="1" y="4" width="22" height="16" rx="2" ry="2"/><line x1="1" y1="10" x2="23" y2="10"/></svg>
        <span>${t('availableBalance')}</span>
      </div>
      <div class="balance-amount">
        <span class="balance-currency">₹</span>
        <span>${(u.availableBalance || 0).toLocaleString('en-IN', { minimumFractionDigits: 2 })}</span>
      </div>

      <div class="balance-grid">
        <div class="grid-cell">
          <div class="cell-title">Contract In-Trade</div>
          <div class="cell-value">${formatInr(u.contractAmount || 0)}</div>
        </div>
        <div class="grid-cell">
          <div class="cell-title">Yesterday Income</div>
          <div class="cell-value" style="color: var(--gain-green);">+${formatInr(u.yesterdayIncome || 0)}</div>
        </div>
        <div class="grid-cell">
          <div class="cell-title">Today Compounded</div>
          <div class="cell-value" style="color: var(--gain-green);">+${formatInr(u.todayIncome || 0)}</div>
        </div>
      </div>

      <div class="action-buttons">
        <button class="btn-primary" onclick="switchTab('recharge')">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
          <span>${t('recharge')} (QR)</span>
        </button>
        <button class="btn-secondary" onclick="switchTab('withdraw')">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="5" y1="12" x2="19" y2="12"/></svg>
          <span>${t('withdraw')} (20% Fee)</span>
        </button>
      </div>
    </div>

    <!-- Official Telegram Channel Card -->
    <div class="telegram-card" onclick="window.open('${state.adminConfig?.telegramChannelLink || 'https://t.me/inrexchange_vip'}', '_blank')">
      <div class="tg-info">
        <h3>
          <svg width="16" height="16" viewBox="0 0 24 24" fill="#2F80ED"><path d="m22 2-7 20-4-9-9-4Z"/></svg>
          ${state.adminConfig?.telegramTitle || 'INR EXCHANGE Official Signals'}
        </h3>
        <p>${state.adminConfig?.telegramHandle || '@INREXCHANGE_VIP'} • Daily 6% Signals & Updates</p>
      </div>
      <a href="${state.adminConfig?.telegramChannelLink || 'https://t.me/inrexchange_vip'}" target="_blank" class="btn-join-tg">JOIN VIP</a>
    </div>

    <!-- Daily Signal Spotlight Card -->
    <div class="card card-elevated" style="border-color: rgba(0, 230, 118, 0.4);">
      <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px;">
        <span style="font-size: 11px; font-weight: 800; color: var(--gain-green); letter-spacing: 0.5px;">★ TODAY'S OFFICIAL TRADING OPTION</span>
        <span class="badge-open">6.00% REWARD</span>
      </div>
      <div style="display: flex; justify-content: space-between; align-items: center;">
        <div>
          <div style="font-size: 20px; font-weight: 900; color: #FFF;">${sig.pairDisplayName || 'DOGE/USDT'}</div>
          <div style="font-size: 11px; color: var(--text-muted); margin-top: 2px;">
            Direction: <b style="color: ${sig.direction === 'Buy up' ? 'var(--gain-green)' : 'var(--loss-red)'}">${sig.direction}</b> • Duration: 60s
          </div>
        </div>
        <button class="btn-primary" style="padding: 9px 18px; font-size: 12px;" onclick="openTradeModal('${sig.symbol}')">
          Trade Now
        </button>
      </div>
      <div style="margin-top: 10px; font-size: 11px; color: var(--accent-cyan); background: rgba(0, 229, 255, 0.08); padding: 6px 10px; border-radius: 8px;">
        Trading Hours: 3:00 PM to 9:00 PM Daily • 1 Trade per 24h per user
      </div>
    </div>

    <!-- Quick Invitation Banner on Home Screen -->
    <div class="card" style="background: linear-gradient(135deg, rgba(250, 62, 92, 0.18), rgba(0, 229, 255, 0.12)); border: 1.5px solid var(--accent-pink); padding: 12px; display: flex; justify-content: space-between; align-items: center; cursor: pointer; margin-bottom: 14px;" onclick="switchTab('team')">
      <div style="display: flex; gap: 10px; align-items: center;">
        <div style="font-size: 28px;">🎁</div>
        <div>
          <div style="font-size: 13px; font-weight: 900; color: #FFF;">रेफरल इनविटेशन लिंक (10% कमीशन)</div>
          <div style="font-size: 10px; color: var(--accent-cyan); margin-top: 2px;">दोस्तों को इनवाइट करें और ऐप डाउनलोड कराएं</div>
        </div>
      </div>
      <button class="btn-primary" style="padding: 6px 12px; font-size: 11px; flex-shrink: 0;" onclick="event.stopPropagation(); switchTab('team');">इनवाइट करें</button>
    </div>

    <!-- Quick Navigation Shortcuts -->
    <div style="display: grid; grid-template-columns: repeat(4, 1fr); gap: 10px; margin-bottom: 14px;">
      <div class="card" style="text-align: center; padding: 12px 6px; cursor: pointer; margin: 0;" onclick="switchTab('trading')">
        <div style="font-size: 20px; margin-bottom: 4px;">📈</div>
        <div style="font-size: 11px; font-weight: 800; color: #FFF;">Quotes</div>
      </div>
      <div class="card" style="text-align: center; padding: 12px 6px; cursor: pointer; margin: 0;" onclick="switchTab('team')">
        <div style="font-size: 20px; margin-bottom: 4px;">👥</div>
        <div style="font-size: 11px; font-weight: 800; color: #FFF;">10% Fission</div>
      </div>
      <div class="card" style="text-align: center; padding: 12px 6px; cursor: pointer; margin: 0;" onclick="switchTab('vip')">
        <div style="font-size: 20px; margin-bottom: 4px;">👑</div>
        <div style="font-size: 11px; font-weight: 800; color: #FFF;">Daily Salary</div>
      </div>
      <div class="card" style="text-align: center; padding: 12px 6px; cursor: pointer; margin: 0;" onclick="switchTab('history')">
        <div style="font-size: 20px; margin-bottom: 4px;">📜</div>
        <div style="font-size: 11px; font-weight: 800; color: #FFF;">History</div>
      </div>
    </div>

    <!-- 90 Days Compounding Promo Card -->
    <div class="card" style="background: linear-gradient(135deg, #221430, #131525); border-color: rgba(250, 62, 92, 0.3);">
      <div style="display: flex; gap: 12px; align-items: center;">
        <img src="/images/promo_trading.jpg" style="width: 70px; height: 70px; border-radius: 12px; object-fit: cover;" onerror="this.src='/images/logo.jpg'">
        <div>
          <h3 style="font-size: 15px; font-weight: 900; color: #FFF;">90 Days Compounding Scheme</h3>
          <p style="font-size: 11px; color: var(--text-muted); margin-top: 4px;">
            Grow your wallet exponentially with 6% daily compounding return. Safe, secure, automated.
          </p>
        </div>
      </div>
    </div>

    <!-- Live Market Preview -->
    <div style="display: flex; justify-content: space-between; align-items: center; margin: 16px 0 10px 0;">
      <h3 style="font-size: 15px; font-weight: 900; color: #FFF;">Live Market Pairs (₹)</h3>
      <span style="font-size: 12px; color: var(--accent-cyan); cursor: pointer;" onclick="switchTab('trading')">View All (${state.quotes.length}) →</span>
    </div>
    ${renderPairList(state.quotes.slice(0, 5))}
  `;
}

// -------------------------------------------------------------
// VIEW 2: TRADING / QUOTES (Matches User Reference Image)
// -------------------------------------------------------------
function renderTradingView() {
  const isTradingOpen = state.isTradingOpen;
  const categories = ['ALL', 'CRYPTO', 'INDICES', 'COMMODITIES', 'FOREX'];

  const filtered = state.quotes.filter(q => {
    const matchCat = state.selectedCategory === 'ALL' || q.category === state.selectedCategory;
    const matchSearch = !state.searchQuery || q.symbol.toLowerCase().includes(state.searchQuery.toLowerCase()) || q.name.toLowerCase().includes(state.searchQuery.toLowerCase());
    return matchCat && matchSearch;
  });

  return `
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px;">
      <div>
        <h2 style="font-size: 20px; font-weight: 900; color: #FFF;">Trading Market Pairs</h2>
        <p style="font-size: 11px; color: var(--text-muted);">60-Second Fixed 6% Compounding Contracts</p>
      </div>
      <div class="${isTradingOpen ? 'badge-open' : 'badge-closed'}">
        ${isTradingOpen ? '● TRADING ACTIVE (3PM-9PM)' : '● TRADING CLOSED (OPENS 3PM)'}
      </div>
    </div>

    <!-- Category Chips -->
    <div class="category-chips">
      ${categories.map(c => `
        <div class="chip ${state.selectedCategory === c ? 'active' : ''}" onclick="selectCategory('${c}')">${c}</div>
      `).join('')}
    </div>

    <!-- Search Input -->
    <input type="text" class="search-input" placeholder="Search by symbol or name (e.g. BTC, ETH, NIFTY)" value="${state.searchQuery}" oninput="onSearchInput(this.value)">

    <!-- Pair List -->
    ${renderPairList(filtered)}
  `;
}

function selectCategory(cat) {
  state.selectedCategory = cat;
  renderApp();
}

function onSearchInput(val) {
  state.searchQuery = val;
  const container = document.getElementById('main-content');
  if (state.currentTab === 'trading') {
    container.innerHTML = renderTradingView();
  }
}

function renderPairList(pairs) {
  if (!pairs || pairs.length === 0) {
    return `<div class="card" style="text-align: center; color: var(--text-muted); padding: 30px;">No trading pairs found.</div>`;
  }

  return pairs.map(q => `
    <div class="pair-item" onclick="openTradeModal('${q.symbol}')">
      <div class="pair-left">
        <div>
          <div style="display: flex; align-items: center;">
            <span class="pair-symbol">${q.symbol}/USDT</span>
            ${!q.isEnabled ? '<span class="pair-badge-paused">PAUSED</span>' : ''}
          </div>
          <div class="pair-name">${q.name} • ${q.category}</div>
        </div>
      </div>
      <div class="pair-right">
        <div class="pair-price">₹${q.priceInInr.toLocaleString('en-IN', { minimumFractionDigits: 2 })}</div>
        <div class="badge-change ${q.change24hPercent >= 0 ? 'change-green' : 'change-red'}">
          ${q.change24hPercent >= 0 ? '+' : ''}${q.change24hPercent.toFixed(2)}%
        </div>
      </div>
    </div>
  `).join('');
}

// -------------------------------------------------------------
// VIEW 3: RECHARGE (QR CODE ONLY - User Explicit Requirement)
// -------------------------------------------------------------
function renderRechargeView() {
  const qrImage = state.adminConfig?.customUploadedQrUrl || '/images/qr_m_logo.jpg';
  const upiId = state.adminConfig?.upiId || 'inrexchange.pay@hdfcbank';
  const merchantName = state.adminConfig?.merchantName || 'INR EXCHANGE';
  const qrTitle = state.adminConfig?.activeQrTitle || 'Official Payment QR';

  return `
    <div class="card balance-hero">
      <div style="font-size: 11px; color: var(--text-muted); text-transform: uppercase;">Current Wallet Balance</div>
      <div class="balance-amount">
        <span class="balance-currency">₹</span>
        <span>${(state.user?.availableBalance || 0).toLocaleString('en-IN', { minimumFractionDigits: 2 })}</span>
      </div>
    </div>

    <!-- Recharge Input Form Card -->
    <div class="card card-elevated">
      <h3 style="font-size: 16px; font-weight: 900; color: #FFF; margin-bottom: 4px;">
        ${t('rechargeViaQr')}
      </h3>
      <p style="font-size: 11px; color: var(--text-muted); margin-bottom: 14px;">
        Enter any recharge amount to generate your official payment QR code.
      </p>

      <label style="font-size: 11px; font-weight: 800; color: var(--text-secondary);">Enter Recharge Amount (₹)</label>
      <div class="amount-input-box" style="margin: 8px 0 12px 0;">
        <span style="font-size: 20px; font-weight: 900; color: var(--accent-pink); margin-right: 8px;">₹</span>
        <input type="number" id="recharge-amount-input" placeholder="Enter amount (e.g. 1000)" value="1000" oninput="updateQrDisplayAmount(this.value)">
      </div>

      <!-- Quick Amount Buttons -->
      <div style="display: grid; grid-template-columns: repeat(4, 1fr); gap: 8px; margin-bottom: 16px;">
        ${[500, 1000, 2000, 5000].map(amt => `
          <button class="btn-secondary" style="padding: 8px 4px; font-size: 11px;" onclick="setRechargeAmount(${amt})">+₹${amt}</button>
        `).join('')}
      </div>

      <!-- Live Generated QR Code Card -->
      <div class="card" style="background: #0B0E1B; border: 1.5px solid var(--accent-cyan); text-align: center; padding: 18px 12px;">
        <div style="font-size: 12px; font-weight: 800; color: var(--accent-cyan); margin-bottom: 4px;">
          ${qrTitle.toUpperCase()}
        </div>
        <div style="font-size: 10px; color: var(--text-muted); margin-bottom: 12px;">
          Merchant: <b style="color: #FFF;">${merchantName}</b> • Any UPI App (GPay, PhonePe, Paytm, BHIM)
        </div>

        <div class="qr-container">
          <img id="active-recharge-qr-img" src="${qrImage}" alt="Official Payment QR" class="qr-image" onerror="this.src='/images/qr_m_logo.jpg'">
          <div class="qr-amount-badge" id="qr-live-amount">₹1,000.00</div>
        </div>

        <!-- Copy UPI ID Row -->
        <div style="background: #141A2E; border-radius: 10px; padding: 10px; display: flex; justify-content: space-between; align-items: center; margin: 12px 0;">
          <div style="text-align: left;">
            <div style="font-size: 10px; color: var(--text-muted);">Official Merchant UPI ID:</div>
            <div style="font-size: 12px; font-weight: 800; color: #FFF;" id="merchant-upi">${upiId}</div>
          </div>
          <button class="btn-secondary" style="padding: 6px 12px; font-size: 11px;" onclick="copyUpiId()">Copy UPI</button>
        </div>

        <!-- Step-by-Step Payment Instructions -->
        <div class="step-guide" style="text-align: left; margin: 12px 0;">
          <div style="font-size: 11px; font-weight: 800; color: var(--accent-cyan); margin-bottom: 8px;">भुगतान निर्देश (Step-by-Step Guide)</div>
          <div class="step-item">
            <div class="step-number">1</div>
            <div class="step-desc">ऊपर दिए गए QR कोड को <b>PhonePe, GPay, Paytm, या BHIM</b> से स्कैन करें।</div>
          </div>
          <div class="step-item">
            <div class="step-number">2</div>
            <div class="step-desc">सटीक राशि का भुगतान पूरा करें।</div>
          </div>
          <div class="step-item">
            <div class="step-number">3</div>
            <div class="step-desc">भुगतान रसीद से 12 अंकों का <b>UTR / UPI Ref Number</b> कॉपी करें।</div>
          </div>
          <div class="step-item">
            <div class="step-number">4</div>
            <div class="step-desc">नीचे UTR नंबर दर्ज करके <b>वेरिफिकेशन के लिए सबमिट करें</b> दबाएं। पैसा तुरंत आपके वॉलेट में जोड़ दिया जाएगा।</div>
          </div>
        </div>

        <!-- UTR / Reference Number Input -->
        <div style="text-align: left; margin-top: 14px;">
          <label style="font-size: 11px; font-weight: 800; color: var(--text-secondary);">${t('enterUtr')}</label>
          <input type="text" id="utr-input" class="search-input" placeholder="e.g. 432109876543 (12 digits)" maxlength="16" style="margin-top: 6px;">
        </div>

        <button class="btn-primary" style="width: 100%; margin-top: 8px;" onclick="submitRecharge()">
          ${t('verifyRecharge')}
        </button>
      </div>
    </div>
  `;
}

function setRechargeAmount(amt) {
  const input = document.getElementById('recharge-amount-input');
  if (input) {
    input.value = amt;
    updateQrDisplayAmount(amt);
  }
}

function updateQrDisplayAmount(amt) {
  const badge = document.getElementById('qr-live-amount');
  if (badge) {
    const val = parseFloat(amt) || 0;
    badge.innerText = "₹" + val.toLocaleString('en-IN', { minimumFractionDigits: 2 });
  }
}

function copyUpiId() {
  const upi = state.adminConfig?.upiId || 'inrexchange.pay@hdfcbank';
  navigator.clipboard.writeText(upi).then(() => {
    showToast("UPI ID copied to clipboard!", "success");
  });
}

async function submitRecharge() {
  const amtInput = document.getElementById('recharge-amount-input');
  const utrInput = document.getElementById('utr-input');
  const amount = parseFloat(amtInput?.value) || 0;
  const utr = utrInput?.value?.trim() || "";

  if (amount < 100) {
    return showToast("Minimum recharge is ₹100", "error");
  }

  try {
    const res = await fetch('/api/recharge', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ amount, utr, method: "Official QR Code UPI" })
    });
    const data = await res.json();
    if (data.success) {
      // Trigger "Recharge Successful" Popup Modal
      openRechargeSuccessModal(amount, data.receipt?.utr || utr);
      syncState();
    } else {
      showToast(data.message || "Recharge failed", "error");
    }
  } catch (err) {
    showToast("Network error. Please try again.", "error");
  }
}

// -------------------------------------------------------------
// VIEW 4: WITHDRAW (20% Fee & 9:00 AM - 12:00 AM Time Schedule)
// -------------------------------------------------------------
function renderWithdrawView() {
  const u = state.user || {};
  const isWithdrawalOpen = state.isWithdrawalOpen;
  const feePct = state.adminConfig?.withdrawalFeePercent || 20.0;

  return `
    <div class="card balance-hero">
      <div style="font-size: 11px; color: var(--text-muted); text-transform: uppercase;">Available Withdrawable Balance</div>
      <div class="balance-amount">
        <span class="balance-currency">₹</span>
        <span>${(u.availableBalance || 0).toLocaleString('en-IN', { minimumFractionDigits: 2 })}</span>
      </div>
    </div>

    <!-- Withdrawal Timing Schedule Card (9:00 AM - 12:00 AM) -->
    <div class="schedule-banner ${isWithdrawalOpen ? 'schedule-open' : 'schedule-closed'}">
      <div>
        <div style="font-weight: 800; color: #FFF;">Daily Withdrawal Schedule (विड्रोल समय)</div>
        <div style="font-size: 11px; color: ${isWithdrawalOpen ? 'var(--gain-green)' : 'var(--loss-red)'}; margin-top: 2px;">
          9:00 AM to 12:00 AM Daily (सुबह 9:00 AM से रात 12:00 AM)
        </div>
      </div>
      <div class="${isWithdrawalOpen ? 'badge-open' : 'badge-closed'}">
        ${isWithdrawalOpen ? t('openNow') : t('closed')}
      </div>
    </div>

    <!-- Withdrawal Form Card -->
    <div class="card card-elevated">
      <h3 style="font-size: 16px; font-weight: 900; color: #FFF; margin-bottom: 12px;">Request Payout</h3>

      <!-- Destination Choice -->
      <div style="background: #0F1424; border-radius: 12px; padding: 12px; margin-bottom: 14px;">
        <div style="font-size: 11px; color: var(--text-muted); margin-bottom: 4px;">Linked Bank Account</div>
        <div style="font-size: 13px; font-weight: 800; color: #FFF;">${u.bankName || 'HDFC Bank Ltd'} • ${u.bankAccountNumber || '••••••••6789'}</div>
        <div style="font-size: 11px; color: var(--text-muted);">IFSC: ${u.bankIfsc || 'HDFC0001234'} | UPI: ${u.upiId || 'rohit@okhdfcbank'}</div>
      </div>

      <!-- Amount Input -->
      <label style="font-size: 11px; font-weight: 800; color: var(--text-secondary);">Withdrawal Amount (₹)</label>
      <div class="amount-input-box" style="margin: 8px 0;">
        <span style="font-size: 20px; font-weight: 900; color: var(--accent-pink); margin-right: 8px;">₹</span>
        <input type="number" id="withdraw-amount-input" placeholder="Min ₹500" value="${u.availableBalance > 500 ? 500 : (u.availableBalance || 0)}" oninput="updateWithdrawalBreakdown(this.value)">
        <button class="btn-auto-sync" onclick="setWithdrawAll()">${t('allOut')}</button>
      </div>

      <!-- 6-digit Security PIN -->
      <label style="font-size: 11px; font-weight: 800; color: var(--text-secondary); margin-top: 12px; display: block;">Payment Security PIN</label>
      <input type="password" id="withdraw-pin-input" class="search-input" placeholder="Enter 6-digit PIN (e.g. 123456)" maxlength="6" style="margin-top: 6px;">

      <!-- 20% Fee Deduction Breakdown Card -->
      <div class="breakdown-box">
        <div class="breakdown-row">
          <span style="color: var(--text-muted);">${t('grossAmount')}</span>
          <span id="bd-gross" style="font-weight: 800; color: #FFF;">₹0.00</span>
        </div>
        <div class="breakdown-row">
          <span style="color: #FFB74D;">${t('feeDeduction')} (${feePct}%)</span>
          <span id="bd-fee" style="font-weight: 800; color: var(--loss-red);">-₹0.00</span>
        </div>
        <div class="breakdown-divider"></div>
        <div class="breakdown-row">
          <span style="font-weight: 900; color: #FFF;">${t('netPayout')}</span>
          <span id="bd-net" style="font-weight: 900; color: var(--gain-green); font-size: 14px;">₹0.00</span>
        </div>
      </div>

      <div style="background: rgba(255, 152, 0, 0.1); border: 1px solid rgba(255, 152, 0, 0.3); border-radius: 8px; padding: 8px 12px; font-size: 10px; color: #FFB74D; margin-bottom: 14px;">
        सूचना: सभी विड्रोल पर 20% प्लेटफ़ॉर्म सर्विस चार्ज काटा जाता है। विड्रोल केवल सुबह 9:00 AM से रात 12:00 AM के बीच प्रोसेस किया जाता है।
      </div>

      <button id="btn-submit-withdraw" class="btn-primary" style="width: 100%; ${!isWithdrawalOpen ? 'opacity: 0.5; cursor: not-allowed;' : ''}" onclick="submitWithdrawal()" ${!isWithdrawalOpen ? 'disabled' : ''}>
        ${isWithdrawalOpen ? t('submitWithdrawal') : 'Withdrawals Closed (Opens at 9:00 AM)'}
      </button>
    </div>
  `;
}

function setWithdrawAll() {
  const input = document.getElementById('withdraw-amount-input');
  if (input && state.user) {
    input.value = Math.floor(state.user.availableBalance);
    updateWithdrawalBreakdown(input.value);
  }
}

function updateWithdrawalBreakdown(amtStr) {
  const amt = parseFloat(amtStr) || 0;
  const feePct = state.adminConfig?.withdrawalFeePercent || 20.0;
  const fee = amt * (feePct / 100.0);
  const net = Math.max(0, amt - fee);

  const bdGross = document.getElementById('bd-gross');
  const bdFee = document.getElementById('bd-fee');
  const bdNet = document.getElementById('bd-net');

  if (bdGross) bdGross.innerText = formatInr(amt);
  if (bdFee) bdFee.innerText = "-" + formatInr(fee);
  if (bdNet) bdNet.innerText = formatInr(net);
}

async function submitWithdrawal() {
  if (!state.isWithdrawalOpen && state.adminConfig?.enforceWithdrawalHours) {
    return showToast("विड्रोल का समय सुबह 9:00 AM से रात 12:00 AM तक है।", "error");
  }

  const amtInput = document.getElementById('withdraw-amount-input');
  const pinInput = document.getElementById('withdraw-pin-input');
  const amount = parseFloat(amtInput?.value) || 0;
  const pin = pinInput?.value?.trim() || "";

  if (amount < 500) {
    return showToast("Minimum withdrawal is ₹500", "error");
  }
  if (!pin || pin.length < 4) {
    return showToast("Please enter valid 6-digit security PIN", "error");
  }

  try {
    const res = await fetch('/api/withdraw', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        amount,
        destination: `${state.user?.bankName} (${state.user?.bankAccountNumber})`,
        pin
      })
    });
    const data = await res.json();
    if (data.success) {
      showToast(data.message, "success");
      syncState();
      switchTab('home');
    } else {
      showToast(data.message || "Withdrawal failed", "error");
    }
  } catch (err) {
    showToast("Network error. Please try again.", "error");
  }
}

// -------------------------------------------------------------
// VIEW 5: TEAM & REFERRALS (3-Level Fission)
// -------------------------------------------------------------
function renderTeamView() {
  const u = state.user || {};
  const cfg = state.adminConfig || {};
  const refCode = u.referralCode || 'INR9988';
  const inviteUrl = `${window.location.origin}/?ref=${refCode}`;

  return `
    <!-- Referral Hero Card -->
    <div class="card balance-hero">
      <div style="font-size: 11px; color: var(--text-muted); text-transform: uppercase;">मेरा रेफरल कोड (My Referral Code)</div>
      <div style="font-size: 32px; font-weight: 900; color: #FFF; margin: 4px 0; letter-spacing: 1px;">${refCode}</div>
      <div style="font-size: 11px; color: var(--accent-cyan); margin-bottom: 12px;">आपके रेफरल लिंक से जुड़ने वाले हर यूज़र से 3-लेवल कमीशन पाएं</div>

      <!-- Invitation Link Box -->
      <div style="background: #0B0E1B; border: 1px solid var(--border-subtle); border-radius: 10px; padding: 10px; display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; text-align: left;">
        <div style="font-size: 11px; color: #FFF; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; margin-right: 8px;">
          ${inviteUrl}
        </div>
        <button class="btn-secondary" style="padding: 6px 12px; font-size: 11px; flex-shrink: 0;" onclick="copyReferralLink()">Copy Link</button>
      </div>

      <!-- WhatsApp Share Button -->
      <button class="btn-whatsapp" onclick="shareOnWhatsApp()">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor"><path d="M12.04 2c-5.46 0-9.91 4.45-9.91 9.91 0 1.75.46 3.45 1.32 4.95L2.05 22l5.25-1.38c1.45.79 3.08 1.21 4.74 1.21 5.46 0 9.91-4.45 9.91-9.91 0-2.65-1.03-5.14-2.9-7.01A9.82 9.82 0 0 0 12.04 2m.01 1.67c2.2 0 4.26.86 5.82 2.42a8.23 8.23 0 0 1 2.41 5.83c0 4.54-3.7 8.24-8.24 8.24-1.42 0-2.82-.37-4.04-1.08l-.29-.17-3.01.79.8-2.93-.19-.3a8.16 8.16 0 0 1-1.25-4.29c0-4.54 3.7-8.24 8.24-8.24m4.52 11.66c-.25-.13-1.47-.72-1.7-.81-.23-.08-.39-.13-.56.13-.17.25-.64.81-.79.97-.14.17-.29.19-.54.06-.25-.13-1.06-.39-2.02-1.25-.75-.67-1.25-1.5-1.4-1.75-.14-.25-.02-.39.11-.51.11-.11.25-.29.38-.44.12-.14.17-.25.25-.42.08-.17.04-.31-.02-.44-.06-.13-.56-1.35-.77-1.85-.2-.49-.41-.42-.56-.43h-.48c-.17 0-.44.06-.67.31-.23.25-.88.86-.88 2.1 0 1.24.9 2.44 1.03 2.61.12.17 1.78 2.71 4.3 3.8.6.26 1.07.42 1.44.54.61.19 1.16.17 1.6.1.49-.07 1.47-.6 1.68-1.18.21-.58.21-1.07.15-1.18-.06-.12-.23-.19-.48-.31z"/></svg>
        <span>व्हाट्सएप पर शेयर करें (Share on WhatsApp)</span>
      </button>

      <!-- App Download Shortcuts -->
      <div class="download-options-grid">
        <a href="/INR_EXCHANGE.apk" download="INR_EXCHANGE.apk" class="btn-download-action btn-dl-apk">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="7 10 12 15 17 10"/><line x1="12" y1="15" x2="12" y2="3"/></svg>
          <span>डाउनलोड APK</span>
        </a>
        <button class="btn-download-action btn-dl-pwa" onclick="installPwa()">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><rect x="5" y="2" width="14" height="20" rx="2" ry="2"/><line x1="12" y1="18" x2="12.01" y2="18"/></svg>
          <span>Chrome App जोड़ें</span>
        </button>
      </div>
    </div>

    <!-- 3-Level Commission System Card -->
    <div class="card card-elevated">
      <h3 style="font-size: 16px; font-weight: 900; color: #FFF; margin-bottom: 4px;">3-Level Referral Commission</h3>
      <p style="font-size: 11px; color: var(--text-muted); margin-bottom: 12px;">आपकी टीम के हर सदस्य के दैनिक लाभ और रिचार्ज पर कमीशन पाएं।</p>
      <div style="display: grid; grid-template-columns: repeat(3, 1fr); gap: 10px; text-align: center;">
        <div style="background: #141A2E; padding: 12px; border-radius: 12px; border: 1px solid var(--accent-pink);">
          <div style="font-size: 11px; color: var(--text-muted);">Level 1 (Direct)</div>
          <div style="font-size: 22px; font-weight: 900; color: var(--accent-pink);">${cfg.l1CommissionPercent || 10}%</div>
          <div style="font-size: 9px; color: var(--text-muted); margin-top: 2px;">सीधा रेफरल</div>
        </div>
        <div style="background: #141A2E; padding: 12px; border-radius: 12px; border: 1px solid var(--accent-cyan);">
          <div style="font-size: 11px; color: var(--text-muted);">Level 2 (Secondary)</div>
          <div style="font-size: 22px; font-weight: 900; color: var(--accent-cyan);">${cfg.l2CommissionPercent || 3}%</div>
          <div style="font-size: 9px; color: var(--text-muted); margin-top: 2px;">टीम का रेफरल</div>
        </div>
        <div style="background: #141A2E; padding: 12px; border-radius: 12px; border: 1px solid var(--gain-green);">
          <div style="font-size: 11px; color: var(--text-muted);">Level 3 (Tertiary)</div>
          <div style="font-size: 22px; font-weight: 900; color: var(--gain-green);">${cfg.l3CommissionPercent || 1}%</div>
          <div style="font-size: 9px; color: var(--text-muted); margin-top: 2px;">थर्ड लेवल</div>
        </div>
      </div>
    </div>
  `;
}

function copyReferralLink() {
  const code = state.user?.referralCode || 'INR9988';
  const url = `${window.location.origin}/?ref=${code}`;
  navigator.clipboard.writeText(url).then(() => {
    showToast("रेफरल लिंक कॉपी हो गया! दोस्तों को भेजें।", "success");
  });
}

function shareOnWhatsApp() {
  const code = state.user?.referralCode || 'INR9988';
  const url = `${window.location.origin}/?ref=${code}`;
  const text = `🚀 *INR EXCHANGE* - India's Premier Fixed 6% Daily 60S Compounding Platform!%0A%0Aमेरी टीम में शामिल होकर प्रतिदिन 6% लाभ कमाएं। अभी लिंक पर क्लिक करके रजिस्टर और ऐप डाउनलोड करें:%0A👉 ${encodeURIComponent(url)}%0A%0Aरेफरल कोड: *${code}*`;
  window.open(`https://api.whatsapp.com/send?text=${text}`, '_blank');
}

// -------------------------------------------------------------
// VIEW 6: VIP & DAILY SALARY
// -------------------------------------------------------------
function renderVipView() {
  return `
    <div class="card card-elevated">
      <h3 style="font-size: 16px; font-weight: 900; color: #FFF; margin-bottom: 6px;">VIP Leadership Salaries</h3>
      <p style="font-size: 11px; color: var(--text-muted); margin-bottom: 14px;">Daily fixed salary credited directly to available balance.</p>

      ${[
        { lvl: 'VIP 1', team: 10, salary: 500 },
        { lvl: 'VIP 2', team: 30, salary: 1000 },
        { lvl: 'VIP 3', team: 80, salary: 2000 },
        { lvl: 'VIP 4', team: 200, salary: 5000 },
        { lvl: 'VIP 5', team: 600, salary: 20000 },
        { lvl: 'VIP 6', team: 1500, salary: 100000 },
        { lvl: 'VIP 7', team: 3000, salary: 200000 }
      ].map(v => `
        <div style="background: #131726; border: 1px solid var(--border-subtle); border-radius: 12px; padding: 12px; margin-bottom: 8px; display: flex; justify-content: space-between; align-items: center;">
          <div>
            <div style="font-size: 14px; font-weight: 900; color: #FFF;">${v.lvl}</div>
            <div style="font-size: 11px; color: var(--text-muted);">Team Required: ${v.team} Members</div>
          </div>
          <div style="text-align: right;">
            <div style="font-size: 15px; font-weight: 900; color: var(--gain-green);">+₹${v.salary.toLocaleString('en-IN')}/day</div>
          </div>
        </div>
      `).join('')}
    </div>
  `;
}

// -------------------------------------------------------------
// VIEW 7: HISTORY
// -------------------------------------------------------------
function renderHistoryView() {
  const txs = state.transactions || [];
  return `
    <h3 style="font-size: 18px; font-weight: 900; color: #FFF; margin-bottom: 14px;">Transaction Records</h3>
    ${txs.length === 0 ? '<div class="card" style="text-align: center; color: var(--text-muted); padding: 30px;">No transaction records yet.</div>' : ''}
    ${txs.map(tItem => `
      <div class="card" style="margin-bottom: 8px; padding: 12px;">
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <div style="font-weight: 800; color: #FFF;">${tItem.type} • ${tItem.id}</div>
          <div style="font-weight: 900; color: ${tItem.type === 'RECHARGE' || tItem.type === 'TRADING_PROFIT' ? 'var(--gain-green)' : 'var(--loss-red)'}; font-size: 14px;">
            ${tItem.type === 'WITHDRAWAL' ? '-' : '+'}${formatInr(tItem.amountInr)}
          </div>
        </div>
        <div style="font-size: 11px; color: var(--text-muted); margin-top: 4px;">${tItem.method} • Ref: ${tItem.referenceId}</div>
        <div style="font-size: 10px; color: var(--text-secondary); margin-top: 2px;">${tItem.remark}</div>
      </div>
    `).join('')}
  `;
}

// -------------------------------------------------------------
// 60-SECOND TRADING MODAL (Exact Reference Screenshot UI)
// -------------------------------------------------------------
function openTradeModal(sym) {
  const quote = state.quotes.find(q => q.symbol.toUpperCase() === sym.toUpperCase()) || state.quotes[0];
  state.selectedQuote = quote;
  state.tradeDirection = state.dailyOfficialSignal?.direction || 'Buy up';
  state.tradeAmount = state.user?.availableBalance || 0;

  const modal = document.getElementById('trade-modal');
  if (!modal) return;
  modal.classList.add('active');
  renderTradeModalContent();
}

function closeTradeModal() {
  const modal = document.getElementById('trade-modal');
  if (modal) modal.classList.remove('active');
}

function renderTradeModalContent() {
  const q = state.selectedQuote;
  if (!q) return;
  const sheet = document.getElementById('trade-sheet-content');
  if (!sheet) return;

  const isWindowOpen = state.isTradingOpen;
  const isSignalMatch = q.symbol.toUpperCase() === state.dailyOfficialSignal?.symbol?.toUpperCase();

  sheet.innerHTML = `
    <div class="sheet-handle"></div>
    <div class="sheet-header">
      <div>
        <div style="font-size: 20px; font-weight: 900; color: #FFF;">${q.symbol}/USDT</div>
        <div style="font-size: 11px; color: var(--accent-cyan);">Quick Margin • 60S Fixed 6% Contract</div>
      </div>
      <button class="btn-close" onclick="closeTradeModal()">&times;</button>
    </div>

    <!-- Live Price & 24h Stats -->
    <div style="display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: 8px;">
      <div>
        <div style="font-size: 28px; font-weight: 900; color: ${q.change24hPercent >= 0 ? 'var(--gain-green)' : 'var(--loss-red)'}">
          ${q.priceInInr.toLocaleString('en-IN', { minimumFractionDigits: 2 })}
        </div>
      </div>
      <div style="text-align: right; font-size: 11px; color: var(--text-muted);">
        <div>Low: ${q.low24h.toLocaleString('en-IN', { minimumFractionDigits: 2 })}</div>
        <div>High: ${q.high24h.toLocaleString('en-IN', { minimumFractionDigits: 2 })}</div>
      </div>
    </div>

    <!-- Candlestick Simulation Box -->
    <div class="chart-box">
      <div class="candle candle-green"><div class="candle-wick" style="height: 20px;"></div><div class="candle-body" style="height: 50px;"></div><div class="candle-wick" style="height: 15px;"></div></div>
      <div class="candle candle-red"><div class="candle-wick" style="height: 15px;"></div><div class="candle-body" style="height: 70px;"></div><div class="candle-wick" style="height: 25px;"></div></div>
      <div class="candle candle-green"><div class="candle-wick" style="height: 10px;"></div><div class="candle-body" style="height: 90px;"></div><div class="candle-wick" style="height: 15px;"></div></div>
      <div class="candle candle-green"><div class="candle-wick" style="height: 25px;"></div><div class="candle-body" style="height: 110px;"></div><div class="candle-wick" style="height: 20px;"></div></div>
      <div class="candle candle-red"><div class="candle-wick" style="height: 10px;"></div><div class="candle-body" style="height: 40px;"></div><div class="candle-wick" style="height: 15px;"></div></div>
      <div class="candle candle-green"><div class="candle-wick" style="height: 15px;"></div><div class="candle-body" style="height: 80px;"></div><div class="candle-wick" style="height: 10px;"></div></div>
      <div class="candle candle-green"><div class="candle-wick" style="height: 20px;"></div><div class="candle-body" style="height: 95px;"></div><div class="candle-wick" style="height: 20px;"></div></div>
    </div>

    <!-- Active Trade Countdown Box (If in progress) -->
    <div id="active-countdown-container"></div>

    <!-- Selection Period (Exact Coral Card: 60 S / 6%) -->
    <div style="margin: 12px 0;">
      <div style="font-size: 13px; font-weight: 800; color: #FFF; margin-bottom: 8px;">${t('selectionPeriod')}</div>
      <div class="period-badge-coral">
        <div class="seconds">60 S</div>
        <div class="percent">6%</div>
      </div>
    </div>

    <!-- Custom Amount with Auto Sync -->
    <div style="margin: 14px 0;">
      <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 6px;">
        <span style="font-size: 13px; font-weight: 800; color: #FFF;">${t('customAmount')}</span>
        <button class="btn-auto-sync" onclick="autoSyncTradeAmount()">Auto Sync (100%)</button>
      </div>
      <div class="amount-input-box">
        <input type="number" id="trade-amount-input" placeholder="${t('enterAmount')}" value="${state.user?.availableBalance > 0 ? state.user.availableBalance : 0}" oninput="onTradeAmountChange(this.value)">
        <span style="font-size: 14px; font-weight: 900; color: #FFF;">INR</span>
      </div>
      <div style="display: flex; justify-content: space-between; font-size: 11px; color: var(--text-muted); margin-top: 6px;">
        <span>Available: ${formatInr(state.user?.availableBalance || 0)}</span>
        <span id="investing-label">Investing: ${formatInr(state.user?.availableBalance || 0)}</span>
      </div>
    </div>

    <!-- Summary Box (Matching Screenshot) -->
    <div style="background: #131726; border-radius: 12px; padding: 12px; margin-bottom: 16px;">
      <div style="display: grid; grid-template-columns: repeat(5, 1fr); gap: 6px; text-align: center; font-size: 11px;">
        <div>
          <div style="color: var(--text-muted); font-size: 10px;">Symbol</div>
          <div style="font-weight: 800; color: #FFF; margin-top: 2px;">${q.symbol}</div>
        </div>
        <div>
          <div style="color: var(--text-muted); font-size: 10px;">Direction</div>
          <div style="font-weight: 800; color: var(--gain-green); margin-top: 2px;">${state.tradeDirection}</div>
        </div>
        <div>
          <div style="color: var(--text-muted); font-size: 10px;">Price</div>
          <div style="font-weight: 800; color: var(--loss-red); margin-top: 2px;">${q.priceInInr.toFixed(2)}</div>
        </div>
        <div>
          <div style="color: var(--text-muted); font-size: 10px;">Money</div>
          <div id="sum-money" style="font-weight: 800; color: #FFF; margin-top: 2px;">${(state.user?.availableBalance || 0).toFixed(0)} INR</div>
        </div>
        <div>
          <div style="color: var(--text-muted); font-size: 10px;">Expected (+6%)</div>
          <div id="sum-expected" style="font-weight: 900; color: var(--gain-green); margin-top: 2px;">+${((state.user?.availableBalance || 0) * 0.06).toFixed(2)} INR</div>
        </div>
      </div>
    </div>

    <!-- Confirm 60S Order Button -->
    <button id="btn-confirm-trade" class="btn-primary" style="width: 100%; height: 50px; font-size: 15px;" onclick="execute60sTrade()">
      ${t('confirmOrder')}
    </button>
  `;
}

function autoSyncTradeAmount() {
  const input = document.getElementById('trade-amount-input');
  if (input && state.user) {
    input.value = state.user.availableBalance;
    onTradeAmountChange(input.value);
  }
}

function onTradeAmountChange(val) {
  const num = parseFloat(val) || 0;
  const label = document.getElementById('investing-label');
  const sumMoney = document.getElementById('sum-money');
  const sumExpected = document.getElementById('sum-expected');

  if (label) label.innerText = `Investing: ${formatInr(num)}`;
  if (sumMoney) sumMoney.innerText = `${num.toFixed(0)} INR`;
  if (sumExpected) sumExpected.innerText = `+${(num * 0.06).toFixed(2)} INR`;
}

async function execute60sTrade() {
  const input = document.getElementById('trade-amount-input');
  const amount = parseFloat(input?.value) || 0;
  const symbol = state.selectedQuote?.symbol || "DOGE";
  const direction = state.tradeDirection || "Buy up";

  if (amount <= 0) {
    return showToast("Please enter investment amount", "error");
  }
  if (amount > (state.user?.availableBalance || 0)) {
    return showToast("Insufficient balance! Please recharge via QR Code.", "error");
  }

  try {
    const res = await fetch('/api/trade', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ symbol, direction, amount })
    });
    const data = await res.json();
    if (!data.success) {
      return showToast(data.message || "Trade order failed", "error");
    }

    // Start 60-second visual countdown
    start60sCountdown(symbol, direction, amount);
  } catch (err) {
    showToast("Network error. Please try again.", "error");
  }
}

function start60sCountdown(symbol, direction, amount) {
  let secondsRemaining = 60;
  const container = document.getElementById('active-countdown-container');
  const btn = document.getElementById('btn-confirm-trade');
  if (btn) btn.disabled = true;

  if (container) {
    container.innerHTML = `
      <div class="countdown-box">
        <div style="font-size: 11px; font-weight: 800; color: var(--accent-cyan); letter-spacing: 1px;">60-SECOND TRADE IN PROGRESS</div>
        <div class="countdown-circle">
          <div class="countdown-seconds" id="countdown-num">60s</div>
          <div style="font-size: 9px; color: var(--text-muted);">COUNTDOWN</div>
        </div>
        <div style="font-size: 12px; color: #FFF;">Direction: <b style="color: var(--gain-green);">${direction}</b> | Principal: <b>${formatInr(amount)}</b></div>
        <div style="font-size: 13px; font-weight: 900; color: var(--gain-green); margin-top: 4px;">Expected Reward (+6%): +${formatInr(amount * 0.06)}</div>
      </div>
    `;
  }

  const interval = setInterval(async () => {
    secondsRemaining--;
    const numEl = document.getElementById('countdown-num');
    if (numEl) numEl.innerText = `${secondsRemaining}s`;

    if (secondsRemaining <= 0) {
      clearInterval(interval);
      // Settle trade
      try {
        const settleRes = await fetch('/api/trade/settle', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ symbol, direction, amount })
        });
        const settleData = await settleRes.json();
        closeTradeModal();
        openTradeSettledModal(symbol, amount, settleData.profitEarned || (amount * 0.06));
        syncState();
      } catch (e) {
        showToast("Error settling trade", "error");
      }
    }
  }, 1000);
}

// -------------------------------------------------------------
// SUCCESS NOTIFICATION POPUPS
// -------------------------------------------------------------
function openRechargeSuccessModal(amount, utr) {
  const modal = document.getElementById('general-modal');
  if (!modal) return;
  modal.innerHTML = `
    <div class="bottom-sheet" style="text-align: center; border-color: var(--gain-green);">
      <div style="font-size: 48px; margin-bottom: 10px;">🎉</div>
      <h2 style="font-size: 22px; font-weight: 900; color: var(--gain-green);">${t('rechargeSuccessTitle')}</h2>
      <p style="font-size: 13px; color: var(--text-muted); margin: 6px 0 16px 0;">${t('rechargeSuccessMsg')}</p>
      <div style="background: #141A2E; border-radius: 12px; padding: 14px; margin-bottom: 20px;">
        <div style="font-size: 12px; color: var(--text-muted);">Credited Amount</div>
        <div style="font-size: 28px; font-weight: 900; color: #FFF;">${formatInr(amount)}</div>
        <div style="font-size: 11px; color: var(--accent-cyan); margin-top: 4px;">UTR: ${utr}</div>
      </div>
      <button class="btn-primary" style="width: 100%;" onclick="closeGeneralModal()">OK, Got It</button>
    </div>
  `;
  modal.classList.add('active');
}

function openTradeSettledModal(symbol, amount, profit) {
  const modal = document.getElementById('general-modal');
  if (!modal) return;
  modal.innerHTML = `
    <div class="bottom-sheet" style="text-align: center; border-color: var(--gain-green);">
      <div style="font-size: 48px; margin-bottom: 10px;">🏆</div>
      <h2 style="font-size: 22px; font-weight: 900; color: var(--gain-green);">Trade Completed Successfully!</h2>
      <p style="font-size: 12px; color: var(--text-muted); margin: 4px 0 14px 0;">60-Second contract settled with fixed 6.00% daily profit return.</p>
      <div style="background: #141A2E; border-radius: 12px; padding: 14px; margin-bottom: 18px;">
        <div style="font-size: 12px; color: var(--text-muted);">Profit Credited (+6%)</div>
        <div style="font-size: 26px; font-weight: 900; color: var(--gain-green);">+${formatInr(profit)}</div>
        <div style="font-size: 11px; color: #FFF; margin-top: 4px;">Principal ${formatInr(amount)} returned to Available Balance</div>
      </div>
      <button class="btn-primary" style="width: 100%;" onclick="closeGeneralModal()">Great!</button>
    </div>
  `;
  modal.classList.add('active');
}

function closeGeneralModal() {
  const modal = document.getElementById('general-modal');
  if (modal) modal.classList.remove('active');
}

// -------------------------------------------------------------
// ADMIN PANEL (Complete Powerful Management System)
// -------------------------------------------------------------
function openAdminGate() {
  const modal = document.getElementById('general-modal');
  if (!modal) return;
  modal.innerHTML = `
    <div class="bottom-sheet" style="border-color: var(--accent-pink);">
      <div class="sheet-handle"></div>
      <div class="sheet-header">
        <h3 style="font-size: 18px; font-weight: 900; color: #FFF;">Admin Authentication</h3>
        <button class="btn-close" onclick="closeGeneralModal()">&times;</button>
      </div>
      <p style="font-size: 12px; color: var(--text-muted); margin-bottom: 14px;">Enter Master Admin PIN to access the complete admin control panel.</p>
      <input type="password" id="admin-pin-input" class="search-input" placeholder="Default Master PIN: 8888" maxlength="8">
      <button class="btn-primary" style="width: 100%; margin-top: 8px;" onclick="verifyAdminPin()">Unlock Admin Dashboard</button>
    </div>
  `;
  modal.classList.add('active');
}

function verifyAdminPin() {
  const pin = document.getElementById('admin-pin-input')?.value?.trim();
  const actualPin = state.adminConfig?.masterAdminPin || "8888";
  if (pin === actualPin || pin === "8888") {
    closeGeneralModal();
    switchTab('admin');
    showToast("Admin Dashboard Unlocked", "success");
  } else {
    showToast("Invalid Admin PIN", "error");
  }
}

function renderAdminView() {
  const cfg = state.adminConfig || {};
  const sig = state.dailyOfficialSignal || {};
  const activeTab = state.activeAdminTab || 'overview';

    const tabs = [
    { id: 'overview', label: '1. Overview' },
    { id: 'users', label: '2. Users' },
    { id: 'balances', label: '3. Balances' },
    { id: 'withdrawals', label: '4. Withdrawals & 20% Fee' },
    { id: 'trading', label: '5. Trade Pairs & Signals' },
    { id: 'scanner', label: '6. Scanner & QR' },
    { id: 'referrals', label: '7. Referral Links & Trees' },
    { id: 'content', label: '8. Ticker & Telegram' },
    { id: 'logs', label: '9. Audit Logs' }
  ];

  return `
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px;">
      <div>
        <h2 style="font-size: 20px; font-weight: 900; color: var(--accent-pink);">Master Admin Control Panel</h2>
        <p style="font-size: 11px; color: var(--text-muted);">Full administrative access to manage entire platform</p>
      </div>
      <button class="btn-secondary" style="padding: 6px 12px; font-size: 11px;" onclick="switchTab('home')">Exit Admin</button>
    </div>

    <!-- Admin Tab Navigation -->
    <div class="admin-tab-nav">
      ${tabs.map(tItem => `
        <button class="admin-tab-btn ${activeTab === tItem.id ? 'active' : ''}" onclick="setAdminTab('${tItem.id}')">${tItem.label}</button>
      `).join('')}
    </div>

    <!-- Active Admin Module Content -->
    ${renderAdminModuleContent(activeTab)}
  `;
}

function setAdminTab(tab) {
  state.activeAdminTab = tab;
  const container = document.getElementById('main-content');
  if (state.currentTab === 'admin') {
    container.innerHTML = renderAdminView();
  }
}

function renderAdminModuleContent(tab) {
  const cfg = state.adminConfig || {};
  const sig = state.dailyOfficialSignal || {};

  switch (tab) {
    case 'overview':
      return `
        <div class="card card-elevated">
          <h3 style="font-size: 15px; font-weight: 800; color: #FFF; margin-bottom: 12px;">Platform Summary</h3>
          <div style="display: grid; grid-template-columns: repeat(2, 1fr); gap: 10px;">
            <div style="background: #141A2E; padding: 12px; border-radius: 12px;">
              <div style="font-size: 11px; color: var(--text-muted);">Total Registered Users</div>
              <div style="font-size: 22px; font-weight: 900; color: #FFF; margin-top: 4px;">${state.allUsers.length}</div>
            </div>
            <div style="background: #141A2E; padding: 12px; border-radius: 12px;">
              <div style="font-size: 11px; color: var(--text-muted);">Active Trading Pairs</div>
              <div style="font-size: 22px; font-weight: 900; color: var(--accent-cyan); margin-top: 4px;">${state.quotes.filter(q => q.isEnabled).length} / ${state.quotes.length}</div>
            </div>
            <div style="background: #141A2E; padding: 12px; border-radius: 12px;">
              <div style="font-size: 11px; color: var(--text-muted);">Daily Trading Option</div>
              <div style="font-size: 16px; font-weight: 900; color: var(--gain-green); margin-top: 4px;">${sig.pairDisplayName}</div>
            </div>
            <div style="background: #141A2E; padding: 12px; border-radius: 12px;">
              <div style="font-size: 11px; color: var(--text-muted);">Withdrawal Fee / Hours</div>
              <div style="font-size: 14px; font-weight: 900; color: #FFB74D; margin-top: 4px;">${cfg.withdrawalFeePercent}% • 9AM-12AM</div>
            </div>
          </div>
        </div>
      `;

    case 'withdrawals':
      return `
        <div class="card card-elevated">
          <h3 style="font-size: 15px; font-weight: 900; color: #FFF; margin-bottom: 4px;">Withdrawal Policy & Schedule Control</h3>
          <p style="font-size: 11px; color: var(--text-muted); margin-bottom: 12px;">Manage 20% deduction charge and daily payout window (9:00 AM - 12:00 AM).</p>

          <label style="font-size: 11px; color: var(--text-secondary);">Fee Percentage (%)</label>
          <input type="number" id="adm-fee-input" class="search-input" value="${cfg.withdrawalFeePercent || 20.0}">

          <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 8px;">
            <div>
              <label style="font-size: 11px; color: var(--text-secondary);">Open Hour (24h, 9 = 9 AM)</label>
              <input type="number" id="adm-start-hour" class="search-input" value="${cfg.withdrawalStartHour || 9}">
            </div>
            <div>
              <label style="font-size: 11px; color: var(--text-secondary);">Close Hour (24h, 24 = 12 AM)</label>
              <input type="number" id="adm-end-hour" class="search-input" value="${cfg.withdrawalEndHour || 24}">
            </div>
          </div>

          <div style="display: flex; align-items: center; justify-content: space-between; margin: 10px 0;">
            <span style="font-size: 12px; color: #FFF;">Enforce 9AM-12AM Schedule Restriction</span>
            <input type="checkbox" id="adm-enforce-with" ${cfg.enforceWithdrawalHours ? 'checked' : ''} style="width: 20px; height: 20px;">
          </div>

          <button class="btn-primary" style="width: 100%; margin-top: 6px;" onclick="saveWithdrawalPolicy()">Save Withdrawal Policy</button>
        </div>

        <h4 style="font-size: 14px; font-weight: 800; color: #FFF; margin: 16px 0 8px 0;">Recent Withdrawal Requests</h4>
        ${renderAdminWithdrawalList()}
      `;

    case 'trading':
      return `
        <!-- Publish Daily Signal -->
        <div class="card card-elevated" style="border-color: rgba(0, 230, 118, 0.4);">
          <h3 style="font-size: 15px; font-weight: 900; color: #FFF; margin-bottom: 4px;">Publish Daily Official Signal</h3>
          <p style="font-size: 11px; color: var(--text-muted); margin-bottom: 12px;">Select and publish one trading option daily. All users see and trade only this active signal for the 6% reward.</p>

          <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 8px; margin-bottom: 10px;">
            <div>
              <label style="font-size: 10px; color: var(--text-muted);">Pair Name</label>
              <input type="text" id="adm-sig-pair" class="search-input" value="${sig.pairDisplayName || 'DOGE/USDT'}">
            </div>
            <div>
              <label style="font-size: 10px; color: var(--text-muted);">Direction</label>
              <select id="adm-sig-dir" class="search-input">
                <option value="Buy up" ${sig.direction === 'Buy up' ? 'selected' : ''}>Buy up</option>
                <option value="Buy down" ${sig.direction === 'Buy down' ? 'selected' : ''}>Buy down</option>
              </select>
            </div>
          </div>
          <button class="btn-primary" style="width: 100%;" onclick="publishSignalFromAdmin()">Publish Signal For All Users</button>
        </div>

        <!-- Trading Schedule & 24h Limits -->
        <div class="card card-elevated">
          <h3 style="font-size: 15px; font-weight: 900; color: #FFF; margin-bottom: 10px;">Trading Schedule & 24h Limits</h3>
          <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 8px;">
            <div>
              <label style="font-size: 10px; color: var(--text-muted);">Open (15 = 3 PM)</label>
              <input type="number" id="adm-tr-start" class="search-input" value="${cfg.tradingWindowStartHour || 15}">
            </div>
            <div>
              <label style="font-size: 10px; color: var(--text-muted);">Close (21 = 9 PM)</label>
              <input type="number" id="adm-tr-end" class="search-input" value="${cfg.tradingWindowEndHour || 21}">
            </div>
          </div>
          <div style="display: flex; justify-content: space-between; align-items: center; margin: 8px 0;">
            <span style="font-size: 12px; color: #FFF;">1 Trade per 24h Restriction</span>
            <input type="checkbox" id="adm-enforce-24h" ${cfg.enforce24hTradingLimit ? 'checked' : ''} style="width: 18px; height: 18px;">
          </div>
          <div style="display: flex; justify-content: space-between; align-items: center; margin: 8px 0;">
            <span style="font-size: 12px; color: #FFF;">Lock Rewards to Daily Signal</span>
            <input type="checkbox" id="adm-lock-sig" ${cfg.requireSignalMatch ? 'checked' : ''} style="width: 18px; height: 18px;">
          </div>
          <button class="btn-secondary" style="width: 100%; margin-top: 6px;" onclick="saveTradingSchedule()">Save Trading Schedule</button>
          <button class="btn-secondary" style="width: 100%; margin-top: 6px; border-color: var(--accent-cyan); color: var(--accent-cyan);" onclick="resetUserTradeCooldown()">Reset 24h Trade Quota for Current User</button>
        </div>

        <!-- All Trading Pairs Control -->
        <div class="card card-elevated">
          <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px;">
            <div>
              <h3 style="font-size: 15px; font-weight: 900; color: #FFF;">All Trade Pairs Control (${state.quotes.length})</h3>
              <p style="font-size: 11px; color: var(--text-muted);">Toggle active/suspended, edit price, or set as daily signal</p>
            </div>
            <button class="btn-primary" style="padding: 6px 12px; font-size: 11px;" onclick="openAddPairDialog()">+ Add Pair</button>
          </div>
          ${renderAdminPairList()}
        </div>
      `;

    case 'scanner':
      const isCustomQr = cfg.activeQrCodeType === 'UPLOADED';
      return `
        <!-- QR Code Management Section -->
        <div class="card card-elevated" style="border-color: rgba(250, 62, 92, 0.4);">
          <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px;">
            <h3 style="font-size: 15px; font-weight: 900; color: #FFF;">QR Code Management</h3>
            <span class="${isCustomQr ? 'badge-open' : 'badge-closed'}" style="font-size: 10px;">
              ${isCustomQr ? '● CUSTOM QR ACTIVE' : '● DEFAULT M-LOGO QR ACTIVE'}
            </span>
          </div>
          <p style="font-size: 11px; color: var(--text-muted); margin-bottom: 12px;">
            Upload your payment QR image. The latest active QR code automatically appears on every user's Recharge page without requiring an app reinstall.
          </p>

          <!-- Current Active QR Preview -->
          <div style="text-align: center; background: #0B0E1B; border-radius: 14px; padding: 14px; margin-bottom: 14px; border: 1px solid var(--border-subtle);">
            <div style="font-size: 11px; color: var(--text-secondary); margin-bottom: 6px;">CURRENT ACTIVE RECHARGE QR (SHOWN TO ALL USERS):</div>
            <div class="qr-preview-box">
              <img id="adm-active-qr-display" src="${cfg.customUploadedQrUrl || '/images/qr_m_logo.jpg'}" style="width: 170px; height: 170px; object-fit: contain; display: block;" onerror="this.src='/images/qr_m_logo.jpg'">
            </div>
            <div style="font-size: 11px; color: var(--accent-cyan); font-weight: 800; margin-top: 4px;">${cfg.activeQrTitle || 'Official Payment QR'}</div>
          </div>

          <!-- File Upload Zone -->
          <div class="qr-upload-zone" onclick="document.getElementById('adm-qr-file-input').click()">
            <input type="file" id="adm-qr-file-input" accept="image/*" style="display: none;" onchange="handleAdminQrFile(event)">
            <div style="font-size: 32px; margin-bottom: 4px;">📤</div>
            <div style="font-size: 13px; font-weight: 800; color: #FFF;">Click to Browse or Upload New QR Image</div>
            <div style="font-size: 10px; color: var(--text-muted); margin-top: 4px;">Supports PNG, JPG, JPEG from your phone gallery or computer</div>
          </div>

          <!-- Staged Upload Preview (Hidden until image selected) -->
          <div id="adm-staged-qr-preview-box" style="display: none; background: #141A2E; border: 1.5px solid var(--gain-green); border-radius: 12px; padding: 12px; text-align: center; margin-bottom: 14px;">
            <div style="font-size: 11px; font-weight: 800; color: var(--gain-green); margin-bottom: 6px;">✓ READY TO ACTIVATE FOR ALL USERS</div>
            <img id="adm-staged-qr-img" src="" style="width: 160px; height: 160px; object-fit: contain; background: #FFF; border-radius: 8px; padding: 4px; display: inline-block;">
            <div style="display: flex; gap: 8px; margin-top: 10px;">
              <button class="btn-primary" style="flex: 1;" onclick="uploadAndActivateQr()">Activate Now For All Users</button>
              <button class="btn-secondary" style="flex: 1; border-color: var(--loss-red); color: var(--loss-red);" onclick="cancelStagedQr()">Cancel</button>
            </div>
          </div>

          <!-- Action Buttons -->
          <div style="display: flex; gap: 8px; margin-bottom: 16px;">
            <button class="btn-secondary" style="flex: 1; border-color: #FFB74D; color: #FFB74D; font-size: 11px;" onclick="revertToDefaultQr()">
              Revert to Default Official QR
            </button>
          </div>

          <!-- UPI ID & Merchant Name Configuration -->
          <h4 style="font-size: 13px; font-weight: 800; color: #FFF; margin-bottom: 8px;">UPI Gateway Settings</h4>
          <label style="font-size: 10px; color: var(--text-muted);">Merchant UPI ID (VPA)</label>
          <input type="text" id="adm-upi-id" class="search-input" value="${cfg.upiId || 'inrexchange.pay@hdfcbank'}">
          <label style="font-size: 10px; color: var(--text-muted);">Merchant Display Name</label>
          <input type="text" id="adm-merchant-name" class="search-input" value="${cfg.merchantName || 'INR EXCHANGE'}">
          <label style="font-size: 10px; color: var(--text-muted);">QR Code Heading / Subtitle</label>
          <input type="text" id="adm-qr-title" class="search-input" value="${cfg.activeQrTitle || 'Official Payment QR with M Logo'}">
          <button class="btn-primary" style="width: 100%; margin-top: 6px;" onclick="saveScannerSettings()">Save UPI Gateway Details</button>
        </div>
      `;

    case 'content':
      return `
        <div class="card card-elevated">
          <h3 style="font-size: 15px; font-weight: 900; color: #FFF; margin-bottom: 12px;">App Ticker & Telegram Configuration</h3>
          <label style="font-size: 11px; color: var(--text-secondary);">Announcement Ticker</label>
          <input type="text" id="adm-ticker" class="search-input" value="${cfg.tickerAnnouncement || ''}">
          <label style="font-size: 11px; color: var(--text-secondary);">Telegram Channel Link</label>
          <input type="text" id="adm-tg-link" class="search-input" value="${cfg.telegramChannelLink || 'https://t.me/inrexchange_vip'}">
          <label style="font-size: 11px; color: var(--text-secondary);">Telegram VIP Handle</label>
          <input type="text" id="adm-tg-handle" class="search-input" value="${cfg.telegramHandle || '@INREXCHANGE_VIP'}">
          <button class="btn-primary" style="width: 100%; margin-top: 6px;" onclick="saveContentSettings()">Save Content & Telegram</button>
        </div>
      `;

    case 'balances':
      return `
        <div class="card card-elevated">
          <h3 style="font-size: 15px; font-weight: 900; color: #FFF; margin-bottom: 12px;">User Wallet Adjustment</h3>
          <p style="font-size: 11px; color: var(--text-muted); margin-bottom: 10px;">Directly adjust wallet balance for ${state.user?.name} (${state.user?.uid})</p>
          <label style="font-size: 11px; color: var(--text-secondary);">Available Balance Delta (₹)</label>
          <input type="number" id="adm-bal-delta" class="search-input" placeholder="e.g. 10000 or -5000">
          <button class="btn-primary" style="width: 100%;" onclick="adjustWalletBalance()">Apply Wallet Adjustment</button>
          <button class="btn-secondary" style="width: 100%; margin-top: 8px; border-color: var(--loss-red); color: var(--loss-red);" onclick="resetAllBalances()">Reset All Balances to ₹0.00</button>
        </div>
    case 'referrals':
      return `
        <div class="card card-elevated" style="border-color: rgba(0, 229, 255, 0.4);">
          <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px;">
            <h3 style="font-size: 15px; font-weight: 900; color: #FFF;">Referral Links & Team Management</h3>
            <span class="badge-open" style="font-size: 10px;">URL: /Login/register/qr/{CODE}</span>
          </div>
          <p style="font-size: 11px; color: var(--text-muted); margin-bottom: 12px;">
            Manage referral URL system, view user invitation trees, and validate partner referral codes.
          </p>

          <!-- System Settings Card -->
          <div style="background: #0B0E1B; border: 1px solid var(--border-subtle); border-radius: 12px; padding: 12px; margin-bottom: 14px;">
            <div style="font-size: 12px; font-weight: 800; color: var(--accent-pink); margin-bottom: 8px;">Standard URL Template Configuration</div>
            <label style="font-size: 10px; color: var(--text-muted);">Default Master Referral Code</label>
            <input type="text" id="adm-def-ref" class="search-input" value="${cfg.defaultReferralCode || 'INR9988'}" style="margin-top: 2px;">
            <label style="font-size: 10px; color: var(--text-muted); margin-top: 6px; display: block;">Official Domain Base URL</label>
            <input type="text" id="adm-base-url" class="search-input" value="${cfg.referralUrlBase || 'https://inrexchange.app/Login/register/qr/'}" style="margin-top: 2px;">
            <button class="btn-primary" style="width: 100%; margin-top: 8px;" onclick="saveReferralSettings()">Save Referral System Configuration</button>
          </div>

          <!-- Live Backend Referral Code Validator -->
          <div style="background: #141A2E; border: 1px solid var(--accent-cyan); border-radius: 12px; padding: 12px; margin-bottom: 14px;">
            <div style="font-size: 12px; font-weight: 800; color: var(--accent-cyan); margin-bottom: 4px;">Live Backend Referral Code Validator</div>
            <div style="font-size: 10px; color: var(--text-muted); margin-bottom: 8px;">Test if any referral code is active and verified by backend.</div>
            <div style="display: flex; gap: 8px;">
              <input type="text" id="adm-test-ref-input" class="search-input" placeholder="Enter code (e.g. INR9988)" value="INR9988" style="margin: 0;">
              <button class="btn-secondary" style="padding: 8px 14px; font-size: 11px; flex-shrink: 0;" onclick="testValidateReferralCode()">Validate Code</button>
            </div>
            <div id="adm-val-result" style="margin-top: 8px; font-size: 11px;"></div>
          </div>

          <!-- All Users Referral Records Table -->
          <h4 style="font-size: 13px; font-weight: 800; color: #FFF; margin-bottom: 8px;">All Registered Users & Referral Links (${state.allUsers.length})</h4>
          <div style="overflow-x: auto;">
            <table style="width: 100%; border-collapse: collapse; font-size: 11px; text-align: left;">
              <thead>
                <tr style="border-bottom: 1px solid var(--border-subtle); color: var(--text-muted);">
                  <th style="padding: 6px 4px;">User</th>
                  <th style="padding: 6px 4px;">Referral Code</th>
                  <th style="padding: 6px 4px;">Invited By</th>
                  <th style="padding: 6px 4px;">Team (L1)</th>
                  <th style="padding: 6px 4px;">Action</th>
                </tr>
              </thead>
              <tbody>
                ${state.allUsers.map(u => `
                  <tr style="border-bottom: 1px solid rgba(255,255,255,0.05);">
                    <td style="padding: 8px 4px;">
                      <div style="font-weight: 800; color: #FFF;">${u.name}</div>
                      <div style="font-size: 10px; color: var(--text-muted);">${u.uid}</div>
                    </td>
                    <td style="padding: 8px 4px;">
                      <span style="font-weight: 900; color: var(--accent-pink);">${u.referralCode || 'INR9988'}</span>
                    </td>
                    <td style="padding: 8px 4px;">
                      <span style="color: var(--accent-cyan); font-weight: 800;">${u.invitedBy || 'OFFICIAL'}</span>
                    </td>
                    <td style="padding: 8px 4px;">
                      <span style="color: var(--gain-green); font-weight: 800;">${u.directReferralsCount || 0} members</span>
                    </td>
                    <td style="padding: 8px 4px;">
                      <button class="btn-secondary" style="padding: 4px 8px; font-size: 10px;" onclick="copySpecificReferralLink('${u.referralCode || 'INR9988'}')">Copy Link</button>
                    </td>
                  </tr>
                `).join('')}
              </tbody>
            </table>
          </div>
        </div>
      `;

    case 'logs':
      return `
        <div class="card card-elevated">
          <h3 style="font-size: 15px; font-weight: 900; color: #FFF; margin-bottom: 12px;">Platform Real-time Audit Logs</h3>
          <div style="background: #0B0E1B; border-radius: 10px; padding: 12px; font-family: monospace; font-size: 11px; max-height: 400px; overflow-y: auto;">
            ${state.auditLogs.map(l => `<div style="margin-bottom: 4px; color: #81D4FA;">${l}</div>`).join('')}
          </div>
        </div>
      `;

    default:
      return `<div class="card">Select an admin module from above.</div>`;
  }
}

function renderAdminPairList() {
  return state.quotes.map(q => `
    <div style="background: #141A2E; border-radius: 10px; padding: 10px; margin-bottom: 8px;">
      <div style="display: flex; justify-content: space-between; align-items: center;">
        <span style="font-weight: 900; color: #FFF;">${q.symbol}/USDT <span style="font-size: 10px; color: ${q.isEnabled ? 'var(--gain-green)' : 'var(--loss-red)'}">[${q.isEnabled ? 'ACTIVE' : 'SUSPENDED'}]</span></span>
        <div style="display: flex; gap: 6px;">
          <button class="btn-secondary" style="padding: 4px 8px; font-size: 10px;" onclick="toggleQuoteStatus('${q.symbol}', ${!q.isEnabled})">${q.isEnabled ? 'Pause' : 'Activate'}</button>
          <button class="btn-primary" style="padding: 4px 8px; font-size: 10px;" onclick="setAsSignal('${q.symbol}')">★ Set Signal</button>
        </div>
      </div>
      <div style="display: flex; justify-content: space-between; font-size: 11px; color: var(--text-muted); margin-top: 4px;">
        <span>${q.name}</span>
        <span>₹${q.priceInInr.toFixed(2)} (${q.change24hPercent.toFixed(2)}%)</span>
      </div>
    </div>
  `).join('');
}

function renderAdminWithdrawalList() {
  const withs = state.transactions.filter(t => t.type === 'WITHDRAWAL');
  if (withs.length === 0) return `<div style="color: var(--text-muted); font-size: 12px;">No withdrawal requests on platform.</div>`;

  return withs.map(tx => `
    <div style="background: #141A2E; border-radius: 10px; padding: 10px; margin-bottom: 8px;">
      <div style="display: flex; justify-content: space-between;">
        <span style="font-weight: 800; color: #FFF;">${tx.id} (${tx.status})</span>
        <span style="font-weight: 900; color: var(--loss-red);">${formatInr(tx.amountInr)}</span>
      </div>
      <div style="font-size: 11px; color: var(--text-muted); margin-top: 2px;">${tx.remark}</div>
      ${tx.status === 'PENDING' ? `
        <div style="display: flex; gap: 8px; margin-top: 8px;">
          <button class="btn-primary" style="flex: 1; padding: 6px; font-size: 11px;" onclick="approveTx('${tx.id}')">Approve Payout</button>
          <button class="btn-secondary" style="flex: 1; padding: 6px; font-size: 11px; border-color: var(--loss-red);" onclick="rejectTx('${tx.id}')">Reject & Refund</button>
        </div>
      ` : ''}
    </div>
  `).join('');
}

// Admin Action Handlers
async function saveWithdrawalPolicy() {
  const feePercent = parseFloat(document.getElementById('adm-fee-input')?.value) || 20.0;
  const startHour = parseInt(document.getElementById('adm-start-hour')?.value) || 9;
  const endHour = parseInt(document.getElementById('adm-end-hour')?.value) || 24;
  const enforceHours = document.getElementById('adm-enforce-with')?.checked;

  const res = await fetch('/api/admin/update-withdrawal-config', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ feePercent, startHour, endHour, enforceHours })
  });
  const d = await res.json();
  showToast(d.message || "Saved", "success");
  syncState();
}

async function publishSignalFromAdmin() {
  const pair = document.getElementById('adm-sig-pair')?.value || 'DOGE/USDT';
  const dir = document.getElementById('adm-sig-dir')?.value || 'Buy up';
  const sym = pair.split('/')[0].trim();

  const res = await fetch('/api/admin/publish-signal', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ symbol: sym, pairName: pair, direction: dir, profitPercent: 6.00 })
  });
  const d = await res.json();
  showToast(d.message || "Signal Published", "success");
  syncState();
}

async function toggleQuoteStatus(symbol, isEnabled) {
  const res = await fetch('/api/admin/toggle-quote', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ symbol, isEnabled })
  });
  const d = await res.json();
  showToast(d.message || "Status Updated", "success");
  syncState();
}

async function setAsSignal(symbol) {
  const res = await fetch('/api/admin/publish-signal', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ symbol, pairName: `${symbol}/USDT`, direction: 'Buy up', profitPercent: 6.00 })
  });
  const d = await res.json();
  showToast(`Official signal set to ${symbol}/USDT`, "success");
  syncState();
}

async function saveTradingSchedule() {
  const startHour = parseInt(document.getElementById('adm-tr-start')?.value) || 15;
  const endHour = parseInt(document.getElementById('adm-tr-end')?.value) || 21;
  const enforce24h = document.getElementById('adm-enforce-24h')?.checked;
  const requireSignalMatch = document.getElementById('adm-lock-sig')?.checked;

  const res = await fetch('/api/admin/update-trading-config', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ startHour, endHour, enforceHours: true, enforce24h, requireSignalMatch })
  });
  const d = await res.json();
  showToast(d.message || "Saved", "success");
  syncState();
}

async function resetUserTradeCooldown() {
  const res = await fetch('/api/admin/reset-trade-cooldown', { method: 'POST' });
  const d = await res.json();
  showToast(d.message || "Reset done", "success");
  syncState();
}

async function adjustWalletBalance() {
  const delta = parseFloat(document.getElementById('adm-bal-delta')?.value) || 0;
  const res = await fetch('/api/admin/adjust-balance', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ availableDelta: delta, contractDelta: 0, bonusDelta: 0 })
  });
  const d = await res.json();
  showToast(d.message || "Adjusted", "success");
  syncState();
}

async function resetAllBalances() {
  if (!confirm("Are you sure you want to reset all balances to ₹0.00?")) return;
  const res = await fetch('/api/admin/reset-data', { method: 'POST' });
  const d = await res.json();
  showToast(d.message || "Reset to ₹0.00", "info");
  syncState();
}

async function approveTx(txId) {
  const res = await fetch('/api/admin/approve-tx', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ txId })
  });
  const d = await res.json();
  showToast(d.message || "Approved", "success");
  syncState();
}

async function rejectTx(txId) {
  const res = await fetch('/api/admin/reject-tx', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ txId })
  });
  const d = await res.json();
  showToast(d.message || "Rejected", "info");
  syncState();
}

function openAddPairDialog() {
  const sym = prompt("Enter Trading Pair Symbol (e.g. SOL):");
  if (!sym) return;
  const name = prompt("Enter Pair Full Name (e.g. Solana):", sym);
  const price = prompt("Enter Live Price in INR (₹):", "12500");

  fetch('/api/admin/add-quote', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ symbol: sym, name, category: 'CRYPTO', price: parseFloat(price) || 100, change: 2.5 })
  }).then(r => r.json()).then(d => {
    showToast(d.message || "Pair added", "success");
    syncState();
  });
}

// Staged QR upload state
let stagedQrBase64 = null;

function handleAdminQrFile(event) {
  const file = event.target.files?.[0];
  if (!file) return;

  if (file.size > 10 * 1024 * 1024) {
    return showToast("Image file size exceeds 10MB limit", "error");
  }

  const reader = new FileReader();
  reader.onload = function(e) {
    stagedQrBase64 = e.target.result;
    const box = document.getElementById('adm-staged-qr-preview-box');
    const img = document.getElementById('adm-staged-qr-img');
    if (box && img) {
      img.src = stagedQrBase64;
      box.style.display = 'block';
    }
    showToast("QR Image selected. Click 'Activate Now' to publish to all users.", "info");
  };
  reader.readAsDataURL(file);
}

function cancelStagedQr() {
  stagedQrBase64 = null;
  const box = document.getElementById('adm-staged-qr-preview-box');
  const input = document.getElementById('adm-qr-file-input');
  if (box) box.style.display = 'none';
  if (input) input.value = '';
}

async function uploadAndActivateQr() {
  if (!stagedQrBase64) {
    return showToast("Please select a QR image first.", "error");
  }

  const upiId = document.getElementById('adm-upi-id')?.value?.trim();
  const merchantName = document.getElementById('adm-merchant-name')?.value?.trim();
  const title = document.getElementById('adm-qr-title')?.value?.trim();

  try {
    const res = await fetch('/api/admin/upload-qr', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        imageBase64: stagedQrBase64,
        upiId,
        merchantName,
        title
      })
    });
    const data = await res.json();
    if (data.success) {
      showToast("Custom QR Code activated successfully for all users!", "success");
      cancelStagedQr();
      syncState();
    } else {
      showToast(data.message || "Failed to upload QR", "error");
    }
  } catch (err) {
    showToast("Error uploading QR code. Please try again.", "error");
  }
}

async function revertToDefaultQr() {
  if (!confirm("Revert to default official 'M' logo QR code for all users?")) return;
  try {
    const res = await fetch('/api/admin/remove-qr', { method: 'POST' });
    const data = await res.json();
    showToast(data.message || "Reverted to default QR", "info");
    syncState();
  } catch (err) {
    showToast("Error updating QR code", "error");
  }
}

async function saveScannerSettings() {
  const upiId = document.getElementById('adm-upi-id')?.value?.trim();
  const merchantName = document.getElementById('adm-merchant-name')?.value?.trim();
  const activeQrTitle = document.getElementById('adm-qr-title')?.value?.trim();

  try {
    const res = await fetch('/api/admin/update-settings', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ upiId, merchantName, activeQrTitle })
    });
    const d = await res.json();
    showToast(d.message || "Gateway details saved!", "success");
    syncState();
  } catch (e) {
    showToast("Failed to save gateway details", "error");
  }
}

async function saveContentSettings() {
  const tickerAnnouncement = document.getElementById('adm-ticker')?.value?.trim();
  const telegramChannelLink = document.getElementById('adm-tg-link')?.value?.trim();
  const telegramHandle = document.getElementById('adm-tg-handle')?.value?.trim();

  try {
    const res = await fetch('/api/admin/update-settings', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ tickerAnnouncement, telegramChannelLink, telegramHandle })
    });
    const d = await res.json();
    showToast(d.message || "Content settings updated!", "success");
    syncState();
  } catch (e) {
    showToast("Failed to save content", "error");
  }
}

// -------------------------------------------------------------
// USER AUTHENTICATION & PROFILE DASHBOARD (Web / Chrome)
// -------------------------------------------------------------
let currentAuthTab = 'login';

function openAuthModal() {
  const modal = document.getElementById('general-modal');
  if (!modal) return;

  const u = state.user;
  if (u && u.uid) {
    modal.innerHTML = `
      <div class="bottom-sheet" style="border-color: var(--accent-cyan);">
        <div class="sheet-handle"></div>
        <div class="sheet-header">
          <div>
            <h3 style="font-size: 18px; font-weight: 900; color: #FFF;">User Dashboard</h3>
            <div style="font-size: 11px; color: var(--accent-cyan);">Unique User ID: <b>${u.uid}</b></div>
          </div>
          <button class="btn-close" onclick="closeGeneralModal()">&times;</button>
        </div>

        <div style="background: #141A2E; border-radius: 12px; padding: 14px; margin: 12px 0;">
          <div style="display: flex; justify-content: space-between; margin-bottom: 8px;">
            <span style="font-size: 11px; color: var(--text-muted);">Name</span>
            <span style="font-size: 12px; font-weight: 800; color: #FFF;">${u.name}</span>
          </div>
          <div style="display: flex; justify-content: space-between; margin-bottom: 8px;">
            <span style="font-size: 11px; color: var(--text-muted);">Mobile Number</span>
            <span style="font-size: 12px; font-weight: 800; color: #FFF;">${u.mobile}</span>
          </div>
          <div style="display: flex; justify-content: space-between; margin-bottom: 8px;">
            <span style="font-size: 11px; color: var(--text-muted);">Referral Code</span>
            <span style="font-size: 13px; font-weight: 900; color: var(--accent-pink);">${u.referralCode || 'INR9988'}</span>
          </div>
          <div style="display: flex; justify-content: space-between;">
            <span style="font-size: 11px; color: var(--text-muted);">Wallet Balance</span>
            <span style="font-size: 14px; font-weight: 900; color: var(--gain-green);">${formatInr(u.availableBalance || 0)}</span>
          </div>
        </div>

        <div style="display: flex; gap: 8px;">
          <button class="btn-primary" style="flex: 1;" onclick="closeGeneralModal(); switchTab('recharge');">Recharge Wallet</button>
          <button class="btn-secondary" style="flex: 1; border-color: var(--accent-pink); color: var(--accent-pink);" onclick="showSwitchAccountForm()">Switch / Register</button>
        </div>
      </div>
    `;
  } else {
    showSwitchAccountForm();
    return;
  }
  modal.classList.add('active');
}

function showSwitchAccountForm(defaultTab = 'register') {
  currentAuthTab = defaultTab;
  const modal = document.getElementById('general-modal');
  if (!modal) return;

  modal.innerHTML = `
    <div class="bottom-sheet" style="border-color: var(--accent-pink);">
      <div class="sheet-handle"></div>
      <div class="sheet-header">
        <h3 style="font-size: 18px; font-weight: 900; color: #FFF;">Google Chrome Account Portal</h3>
        <button class="btn-close" onclick="closeGeneralModal()">&times;</button>
      </div>

      <!-- Auth Tab Switcher -->
      <div style="display: flex; gap: 8px; margin: 12px 0 16px 0;">
        <button id="btn-auth-tab-reg" class="admin-tab-btn ${currentAuthTab === 'register' ? 'active' : ''}" style="flex: 1;" onclick="renderAuthForm('register')">Register New Account</button>
        <button id="btn-auth-tab-login" class="admin-tab-btn ${currentAuthTab === 'login' ? 'active' : ''}" style="flex: 1;" onclick="renderAuthForm('login')">Login Existing</button>
      </div>

      <div id="auth-form-container">
        ${renderAuthFormHtml(currentAuthTab)}
      </div>
    </div>
  `;
  modal.classList.add('active');
}

function renderAuthForm(tab) {
  currentAuthTab = tab;
  const regBtn = document.getElementById('btn-auth-tab-reg');
  const loginBtn = document.getElementById('btn-auth-tab-login');
  if (regBtn && loginBtn) {
    regBtn.className = `admin-tab-btn ${tab === 'register' ? 'active' : ''}`;
    loginBtn.className = `admin-tab-btn ${tab === 'login' ? 'active' : ''}`;
  }
  const container = document.getElementById('auth-form-container');
  if (container) {
    container.innerHTML = renderAuthFormHtml(tab);
  }
}

function renderAuthFormHtml(tab) {
  if (tab === 'register') {
    return `
      <div>
        <label style="font-size: 11px; color: var(--text-secondary);">Your Full Name</label>
        <input type="text" id="auth-reg-name" class="search-input" placeholder="e.g. Rahul Verma">

        <label style="font-size: 11px; color: var(--text-secondary);">10-Digit Mobile Number</label>
        <input type="tel" id="auth-reg-mobile" class="search-input" placeholder="e.g. 9876543210" maxlength="10">

        <label style="font-size: 11px; color: var(--text-secondary);">Create Account Password</label>
        <input type="password" id="auth-reg-pwd" class="search-input" placeholder="Enter password (e.g. 123456)">

        <label style="font-size: 11px; color: var(--text-secondary);">Referral Code (Optional / Default)</label>
        <input type="text" id="auth-reg-ref" class="search-input" value="INR9988" style="font-weight: 800; color: var(--accent-pink);">

        <div style="background: rgba(0, 230, 118, 0.08); border-radius: 8px; padding: 8px 10px; font-size: 10px; color: var(--gain-green); margin: 10px 0;">
          ✓ Unique User ID (INR-XXXXXX) assigned automatically<br>
          ✓ Initial balance starts strictly at ₹0.00 until recharge
        </div>

        <button class="btn-primary" style="width: 100%; margin-top: 6px;" onclick="submitRegister()">Complete Registration</button>
      </div>
    `;
  } else {
    return `
      <div>
        <label style="font-size: 11px; color: var(--text-secondary);">Registered Mobile Number</label>
        <input type="tel" id="auth-login-mobile" class="search-input" placeholder="e.g. 9876543210" maxlength="10">

        <label style="font-size: 11px; color: var(--text-secondary);">Password</label>
        <input type="password" id="auth-login-pwd" class="search-input" placeholder="Enter password or default 123">

        <button class="btn-primary" style="width: 100%; margin-top: 14px;" onclick="submitLogin()">Login to Dashboard</button>
      </div>
    `;
  }
}

async function submitRegister() {
  const name = document.getElementById('auth-reg-name')?.value?.trim();
  const mobile = document.getElementById('auth-reg-mobile')?.value?.trim();
  const password = document.getElementById('auth-reg-pwd')?.value?.trim();
  const referralCode = document.getElementById('auth-reg-ref')?.value?.trim() || 'INR9988';

  if (!mobile || mobile.length < 10) {
    return showToast("Please enter a valid 10-digit mobile number.", "error");
  }
  if (!password) {
    return showToast("Please enter a password.", "error");
  }

  try {
    const res = await fetch('/api/auth/register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ name, mobile, password, referralCode })
    });
    const d = await res.json();
    if (d.success) {
      showToast(`Registered successfully! Your User ID: ${d.user.uid}`, "success");
      state.user = d.user;
      closeGeneralModal();
      syncState();
    } else {
      showToast(d.message || "Registration failed", "error");
    }
  } catch (e) {
    showToast("Network error during registration", "error");
  }
}

async function submitLogin() {
  const mobile = document.getElementById('auth-login-mobile')?.value?.trim();
  const password = document.getElementById('auth-login-pwd')?.value?.trim();

  if (!mobile || mobile.length < 10) {
    return showToast("Please enter valid mobile number.", "error");
  }

  try {
    const res = await fetch('/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ mobile, password })
    });
    const d = await res.json();
    if (d.success) {
      showToast("Welcome back! Login successful.", "success");
      state.user = d.user;
      closeGeneralModal();
      syncState();
    } else {
      showToast(d.message || "Login failed", "error");
    }
  } catch (e) {
    showToast("Network error during login", "error");
  }
}

// -------------------------------------------------------------
// PWA INSTALLATION & SERVICE WORKER (Google Chrome Web)
// -------------------------------------------------------------
let deferredPwaPrompt = null;

window.addEventListener('beforeinstallprompt', (e) => {
  e.preventDefault();
  deferredPwaPrompt = e;
  const banner = document.getElementById('pwa-install-banner');
  if (banner) banner.style.display = 'flex';
});

function dismissPwaBanner() {
  const banner = document.getElementById('pwa-install-banner');
  if (banner) banner.style.display = 'none';
}

async function installPwa() {
  if (deferredPwaPrompt) {
    deferredPwaPrompt.prompt();
    const { outcome } = await deferredPwaPrompt.userChoice;
    if (outcome === 'accepted') {
      showToast("App installed to home screen!", "success");
    }
    deferredPwaPrompt = null;
    dismissPwaBanner();
  } else {
    showToast("To install on Chrome: Tap the ⋮ browser menu and select 'Add to Home screen' or 'Install App'.", "info");
  }
}

// Register Service Worker
if ('serviceWorker' in navigator) {
  window.addEventListener('load', () => {
    navigator.serviceWorker.register('/sw.js').catch(err => console.log('SW registration error:', err));
  });
}

// -------------------------------------------------------------
// INVITATION & REFERRAL REGISTRATION SYSTEM (URL: /Login/register/qr/INR9988)
// -------------------------------------------------------------
function extractReferralCodeFromUrl() {
  const path = window.location.pathname;
  // Match standard URL structure: /Login/register/qr/INR9988
  const match = path.match(/\/(login\/register\/qr|register\/qr|register)\/([a-zA-Z0-9_-]+)/i);
  if (match && match[2]) {
    return match[2].toUpperCase();
  }
  // Match query parameters: ?ref=... or ?invite=... or ?code=...
  const urlParams = new URLSearchParams(window.location.search);
  const paramCode = urlParams.get('ref') || urlParams.get('invite') || urlParams.get('code');
  if (paramCode) {
    return paramCode.trim().toUpperCase();
  }
  return null;
}

async function openInvitationLandingModal(refCode = 'INR9988') {
  const modal = document.getElementById('general-modal');
  if (!modal) return;

  const currentRef = (refCode || localStorage.getItem('inr_invite_ref') || state.adminConfig?.defaultReferralCode || 'INR9988').toUpperCase();
  const canonicalUrl = `https://inrexchange.app/Login/register/qr/${currentRef}`;

  // Validate referral code securely against backend
  let inviterInfo = { inviterName: "INR EXCHANGE Official Partner", valid: true };
  try {
    const valRes = await fetch(`/api/referral/validate?code=${encodeURIComponent(currentRef)}`);
    const valData = await valRes.json();
    if (valData) {
      inviterInfo = valData;
    }
  } catch (err) {
    console.warn("Referral validate error:", err);
  }

  modal.innerHTML = `
    <div class="bottom-sheet" style="border-color: var(--accent-pink); padding-top: 14px;">
      <div class="sheet-handle"></div>
      
      <!-- Invitation Header -->
      <div style="text-align: center; margin-bottom: 10px;">
        <div style="font-size: 38px; line-height: 1; margin-bottom: 6px;">🎉</div>
        <h2 style="font-size: 20px; font-weight: 900; color: #FFF; margin: 0;">स्वागत है! (Invitation Accepted)</h2>
        <div style="font-size: 11px; color: var(--accent-cyan); font-weight: 700; margin-top: 4px;">
          ${canonicalUrl}
        </div>
      </div>

      <!-- Referral Code Glow Badge -->
      <div class="invite-ref-badge">
        <div style="font-size: 10px; color: var(--accent-cyan); text-transform: uppercase; letter-spacing: 0.5px;">
          ✓ बाउंडेड रेफरल कोड (Invited By):
        </div>
        <div style="font-size: 28px; font-weight: 900; color: var(--accent-pink); letter-spacing: 2px; margin-top: 2px;">
          ${currentRef}
        </div>
        <div style="font-size: 11px; color: var(--gain-green); font-weight: 800; margin-top: 2px;">
          ${inviterInfo.inviterName || 'Verified Official Partner'}
        </div>
        <div style="font-size: 9px; color: var(--text-muted); margin-top: 4px;">
          🔒 सुरक्षा नियम: यूज़र किसी अन्य का रेफरल कोड नहीं बदल सकते (Immutable Ownership)
        </div>
      </div>

      <!-- Direct App Download Shortcuts -->
      <div class="download-options-grid" style="margin-bottom: 12px;">
        <a href="/INR_EXCHANGE.apk" download="INR_EXCHANGE.apk" class="btn-download-action btn-dl-apk" style="font-size: 12px; padding: 10px;">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="7 10 12 15 17 10"/><line x1="12" y1="15" x2="12" y2="3"/></svg>
          <span>डाउनलोड APK</span>
        </a>
        <button class="btn-download-action btn-dl-pwa" style="font-size: 12px; padding: 10px;" onclick="installPwa()">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><rect x="5" y="2" width="14" height="20" rx="2" ry="2"/><line x1="12" y1="18" x2="12.01" y2="18"/></svg>
          <span>Chrome App जोड़ें</span>
        </button>
      </div>

      <!-- Instant Mobile-Friendly Registration Form -->
      <div id="invite-form-box" style="background: #0B0E1B; border: 1px solid var(--border-subtle); border-radius: 14px; padding: 14px;">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px;">
          <span style="font-size: 13px; font-weight: 900; color: #FFF;">सीधा लॉगिन / रजिस्ट्रेशन</span>
          <span style="font-size: 10px; color: var(--gain-green); background: rgba(0,230,118,0.1); padding: 3px 8px; border-radius: 6px;">1-टैप साइन इन</span>
        </div>

        <label style="font-size: 11px; font-weight: 800; color: var(--text-secondary);">10-अंकों का मोबाइल नंबर (Mobile Number)</label>
        <input type="tel" id="invite-mobile" class="search-input" placeholder="e.g. 9876543210" maxlength="10" style="margin-top: 4px;">

        <label style="font-size: 11px; font-weight: 800; color: var(--text-secondary); margin-top: 8px; display: block;">पासवर्ड (Password)</label>
        <input type="password" id="invite-pwd" class="search-input" placeholder="पासवर्ड दर्ज करें" style="margin-top: 4px;">

        <label style="font-size: 11px; font-weight: 800; color: var(--text-secondary); margin-top: 8px; display: block;">रेफरल पार्टनर (Locked & Verified)</label>
        <input type="text" id="invite-locked-ref" class="search-input" value="${currentRef}" readonly style="margin-top: 4px; background: rgba(250, 62, 92, 0.08); border-color: var(--accent-pink); color: var(--accent-pink); font-weight: 900; cursor: not-allowed;">

        <!-- Demo Mode Notice -->
        <div style="background: rgba(255, 183, 77, 0.08); border: 1px solid rgba(255, 183, 77, 0.3); border-radius: 8px; padding: 8px 10px; font-size: 10px; color: #FFB74D; margin: 10px 0;">
          ⚠️ <b>डेमो सिमुलेशन मोड (Demo Test Mode)</b>: यह प्लेटफ़ॉर्म टेस्ट और सिमुलेशन वातावरण में संचालित होता है। वास्तविक वित्तीय सेवाओं के लिए कानूनी प्राधिकरण लंबित है।
        </div>

        <div style="display: flex; gap: 8px; margin-top: 12px;">
          <button class="btn-primary" style="flex: 2; height: 46px; font-size: 13px; font-weight: 900;" onclick="submitInviteRegister('${currentRef}')">
            रजिस्टर करके सीधा लॉगिन करें
          </button>
          <button class="btn-secondary" style="flex: 1; height: 46px; font-size: 11px;" onclick="submitInviteLogin()">
            सीधा लॉगिन
          </button>
        </div>

        <div style="font-size: 10px; color: var(--text-muted); text-align: center; margin-top: 10px;">
          नया खाता तुरंत बनेगा और आप रेफरल कोड <b>${currentRef}</b> के तहत पंजीकृत हो जाएंगे।
        </div>
      </div>

      <button class="btn-secondary" style="width: 100%; margin-top: 10px; border-color: transparent; color: var(--text-muted); font-size: 11px;" onclick="closeGeneralModal()">
        ब्राउज़ जारी रखें (Continue as Guest)
      </button>
    </div>
  `;
  modal.classList.add('active');
}

async function submitInviteRegister(refCode) {
  const mobile = document.getElementById('invite-mobile')?.value?.trim();
  const password = document.getElementById('invite-pwd')?.value?.trim();
  const referralCode = refCode || localStorage.getItem('inr_invite_ref') || state.adminConfig?.defaultReferralCode || 'INR9988';

  if (!mobile || mobile.length < 10) {
    return showToast("कृपया 10 अंकों का वैध मोबाइल नंबर दर्ज करें।", "error");
  }
  if (!password) {
    return showToast("कृपया एक पासवर्ड दर्ज करें।", "error");
  }

  try {
    const res = await fetch('/api/auth/register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        name: "Trader " + mobile.slice(-4),
        mobile,
        password,
        referralCode
      })
    });
    const d = await res.json();
    if (d.success) {
      state.user = d.user;
      localStorage.setItem('inr_auth_user', JSON.stringify(d.user));
      closeGeneralModal();
      showToast(`रजिस्ट्रेशन सफल! आप रेफरल कोड ${referralCode} के साथ लॉगिन हो चुके हैं। आपका यूज़र आईडी: ${d.user.uid}`, "success");
      syncState();
    } else {
      if (d.message && d.message.includes("already registered")) {
        showToast("मोबाइल नंबर पहले से रजिस्टर्ड है, लॉगिन किया जा रहा है...", "info");
        submitInviteLoginDirect(mobile, password);
      } else {
        showToast(d.message || "रजिस्ट्रेशन विफल रहा", "error");
      }
    }
  } catch (e) {
    showToast("नेटवर्क त्रुटि, कृपया पुनः प्रयास करें।", "error");
  }
}

async function submitInviteLogin() {
  const mobile = document.getElementById('invite-mobile')?.value?.trim();
  const password = document.getElementById('invite-pwd')?.value?.trim();
  if (!mobile || mobile.length < 10) {
    return showToast("कृपया वैध मोबाइल नंबर दर्ज करें।", "error");
  }
  submitInviteLoginDirect(mobile, password);
}

async function submitInviteLoginDirect(mobile, password) {
  try {
    const res = await fetch('/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ mobile, password })
    });
    const d = await res.json();
    if (d.success) {
      state.user = d.user;
      localStorage.setItem('inr_auth_user', JSON.stringify(d.user));
      closeGeneralModal();
      showToast(`लॉगिन सफल! स्वागत है ${d.user.name}`, "success");
      syncState();
    } else {
      showToast(d.message || "लॉगिन विफल रहा", "error");
    }
  } catch (e) {
    showToast("लॉगिन में त्रुटि", "error");
  }
}

// Admin Referral System Actions
async function saveReferralSettings() {
  const defaultReferralCode = document.getElementById('adm-def-ref')?.value?.trim() || 'INR9988';
  const referralUrlBase = document.getElementById('adm-base-url')?.value?.trim() || 'https://inrexchange.app/Login/register/qr/';

  try {
    const res = await fetch('/api/admin/update-settings', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ defaultReferralCode, referralUrlBase })
    });
    const d = await res.json();
    showToast(d.message || "Referral configuration saved!", "success");
    syncState();
  } catch (err) {
    showToast("Failed to save referral configuration", "error");
  }
}

async function testValidateReferralCode() {
  const code = document.getElementById('adm-test-ref-input')?.value?.trim();
  const resultDiv = document.getElementById('adm-val-result');
  if (!code) return showToast("Enter code to validate", "error");

  try {
    const res = await fetch(`/api/referral/validate?code=${encodeURIComponent(code)}`);
    const d = await res.json();
    if (resultDiv) {
      if (d.valid) {
        resultDiv.innerHTML = `
          <div style="background: rgba(0, 230, 118, 0.1); border: 1px solid var(--gain-green); border-radius: 8px; padding: 8px; color: var(--gain-green);">
            ✓ <b>VALID REFERRAL PARTNER</b><br>
            Code: <b>${d.code}</b> | Owner: <b>${d.inviterName}</b> (${d.inviterUid})<br>
            Registration Link: <a href="${d.url}" target="_blank" style="color: var(--accent-cyan);">${d.url}</a>
          </div>
        `;
      } else {
        resultDiv.innerHTML = `
          <div style="background: rgba(255, 51, 102, 0.1); border: 1px solid var(--loss-red); border-radius: 8px; padding: 8px; color: #FF8A80;">
            ✗ <b>UNKNOWN CODE</b>: ${d.message}
          </div>
        `;
      }
    }
  } catch (err) {
    showToast("Validation check failed", "error");
  }
}

function copySpecificReferralLink(code) {
  const url = `https://inrexchange.app/Login/register/qr/${code}`;
  navigator.clipboard.writeText(url).then(() => {
    showToast(`Referral link for ${code} copied!`, "success");
  });
}

// Initialize on DOM Ready
document.addEventListener('DOMContentLoaded', () => {
  const installBtn = document.getElementById('pwa-install-btn');
  if (installBtn) {
    installBtn.addEventListener('click', installPwa);
  }

  // Detect referral code from URL: /Login/register/qr/INR9988 or query ?ref=...
  const extractedRef = extractReferralCodeFromUrl();
  if (extractedRef) {
    localStorage.setItem('inr_invite_ref', extractedRef);
    // Automatically pop open invitation landing modal with verified code!
    setTimeout(() => {
      openInvitationLandingModal(extractedRef);
    }, 450);
  }

  syncState();
  // Poll state every 10 seconds for real-time consistency
  setInterval(syncState, 10000);
});

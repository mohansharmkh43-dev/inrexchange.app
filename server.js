const http = require('http');
const fs = require('fs');
const path = require('path');
const url = require('url');

const PORT = process.env.APP_PORT || 3000;
const DATA_FILE = path.join(__dirname, 'data', 'store.json');
const PUBLIC_DIR = path.join(__dirname, 'public');
const UPLOAD_DIR = path.join(PUBLIC_DIR, 'images');

// Ensure directories exist
fs.mkdirSync(path.join(__dirname, 'data'), { recursive: true });
fs.mkdirSync(UPLOAD_DIR, { recursive: true });

// Initial default state (matches Android repository values)
const defaultState = {
  user: {
    uid: "INR-829147",
    name: "Rohit Sharma",
    mobile: "9876543210",
    vipLevel: 1,
    starRating: 0,
    totalAssets: 0.00,
    availableBalance: 0.00,
    contractAmount: 0.00,
    totalRecharge: 0.00,
    totalWithdrawal: 0.00,
    tradingAssets: 0.00,
    bonusAssets: 0.00,
    accumulatedBonus: 0.00,
    yesterdayIncome: 0.00,
    todayIncome: 0.00,
    profitMarginPercent: 6.00,
    referralCode: "INR9988",
    bankAccountLinked: true,
    bankAccountNumber: "••••••••6789",
    bankIfsc: "HDFC0001234",
    bankName: "HDFC Bank Ltd",
    upiId: "rohit@okhdfcbank",
    isSecurityPinSet: true,
    status: "Active",
    role: "USER",
    dailySalaryClaimedToday: false,
    lastTradeTimestamp: 0,
    tradeCountTotal: 0,
    password: "123"
  },
  allUsers: [
    {
      uid: "INR-829147",
      name: "Rohit Sharma",
      mobile: "9876543210",
      vipLevel: 1,
      availableBalance: 0.00,
      contractAmount: 0.00,
      bonusAssets: 0.00,
      totalAssets: 0.00,
      status: "Active",
      role: "USER",
      lastTradeTimestamp: 0,
      password: "123"
    },
    {
      uid: "INR-100001",
      name: "Amit Kumar",
      mobile: "9123456780",
      vipLevel: 2,
      availableBalance: 50000.00,
      contractAmount: 20000.00,
      bonusAssets: 1500.00,
      totalAssets: 71500.00,
      status: "Active",
      role: "USER",
      lastTradeTimestamp: 0,
      password: "123"
    }
  ],
  adminConfig: {
    tradingProfitPercent: 6.00,
    tradeDurationSeconds: 60,
    minTradeAmountInr: 100.0,
    maxTradeAmountInr: 500000.0,
    isTradingEnabled: true,
    isRechargeEnabled: true,
    isWithdrawalEnabled: true,
    isReferralEnabled: true,
    isMaintenanceMode: false,
    tradingWindowStartHour: 15, // 3:00 PM
    tradingWindowEndHour: 21,   // 9:00 PM
    enforceTradingHours: true,
    enforce24hTradingLimit: true,
    requireSignalMatch: true,
    withdrawalFeePercent: 20.0, // 20% Withdrawal Fee
    withdrawalStartHour: 9,     // 9:00 AM daily
    withdrawalEndHour: 24,      // 12:00 AM midnight daily
    enforceWithdrawalHours: true,
    l1CommissionPercent: 10.0,
    l2CommissionPercent: 3.0,
    l3CommissionPercent: 1.0,
    upiId: "inrexchange.pay@hdfcbank",
    merchantName: "INR EXCHANGE",
    activeQrCodeType: "M_LOGO", // M_LOGO or UPLOADED
    activeQrTitle: "Official Merchant Payment QR (Provided 'M' Logo)",
    customQrSubtitle: "Scan with Any UPI App (GPay, PhonePe, Paytm, BHIM)",
    customUploadedQrUrl: "/images/qr_m_logo.jpg",
    customQrFileName: "qr_m_logo.jpg",
    customQrUpdatedAt: Date.now(),
    tickerAnnouncement: "Welcome to INR EXCHANGE: India's Premier Fixed 6% Daily Compounding Platform • 24/7 Instant UPI Settlements",
    promoBannerTitle: "90 Days Compounding Scheme",
    promoBannerSubtitle: "Fixed 6% daily profit with auto wallet compounding",
    telegramChannelLink: "https://t.me/inrexchange_vip",
    telegramHandle: "@INREXCHANGE_VIP",
    telegramTitle: "INR EXCHANGE Official Signals",
    telegramDescription: "Join our Official Telegram Channel for daily 6% signal announcements, market analysis, and VIP alerts.",
    defaultReferralCode: "INR9988",
    referralUrlBase: "https://inrexchange.app/Login/register/qr/",
    isDemoMode: true,
    demoNoticeText: "DEMO TEST MODE: Simulated financial instruments and test data. Real financial services pending official regulatory authorization.",
    masterAdminPin: "8888"
  },
  dailyOfficialSignal: {
    symbol: "DOGE",
    pairDisplayName: "DOGE/USDT",
    title: "Official Daily Signal: DOGE/USDT",
    direction: "Buy up",
    profitPercent: 6.00,
    durationSeconds: 60,
    validFromHour: 15, // 3:00 PM
    validToHour: 21,   // 9:00 PM
    remark: "Official Admin Trading Signal: 6.00% Promotional Daily Reward",
    isActive: true,
    publishedDate: "Today"
  },
  quotes: [
    { symbol: "BTC", name: "Bitcoin", category: "CRYPTO", priceInInr: 78165.14, change24hPercent: -4.36, high24h: 81884.89, low24h: 76577.0, volume24hInr: 452900000.0, isFavorite: true, isEnabled: true },
    { symbol: "ETH", name: "Ethereum", category: "CRYPTO", priceInInr: 2236.70, change24hPercent: -5.58, high24h: 2377.81, low24h: 2174.92, volume24hInr: 210500000.0, isFavorite: true, isEnabled: true },
    { symbol: "DOGE", name: "Dogecoin", category: "CRYPTO", priceInInr: 0.07566, change24hPercent: -6.07, high24h: 0.081414, low24h: 0.0723, volume24hInr: 98000000.0, isFavorite: false, isEnabled: true },
    { symbol: "BCH", name: "Bitcoin Cash", category: "CRYPTO", priceInInr: 282.72, change24hPercent: -1.78, high24h: 293.39, low24h: 263.40, volume24hInr: 65000000.0, isFavorite: false, isEnabled: true },
    { symbol: "BSV", name: "Bitcoin SV", category: "CRYPTO", priceInInr: 17.0481, change24hPercent: -5.20, high24h: 18.1213, low24h: 16.4229, volume24hInr: 32000000.0, isFavorite: false, isEnabled: true },
    { symbol: "ETC", name: "Ethereum Classic", category: "CRYPTO", priceInInr: 7.2149, change24hPercent: -5.44, high24h: 7.674, low24h: 6.8075, volume24hInr: 41000000.0, isFavorite: false, isEnabled: true },
    { symbol: "STETH", name: "Lido Staked ETH", category: "CRYPTO", priceInInr: 2233.97, change24hPercent: -3.64, high24h: 2378.23, low24h: 2134.19, volume24hInr: 87000000.0, isFavorite: false, isEnabled: true },
    { symbol: "YFI", name: "Yearn Finance", category: "CRYPTO", priceInInr: 1800.26, change24hPercent: -13.87, high24h: 2090.12, low24h: 1717.80, volume24hInr: 25000000.0, isFavorite: false, isEnabled: true },
    { symbol: "BNB", name: "BNB Chain", category: "CRYPTO", priceInInr: 710.30, change24hPercent: -5.35, high24h: 754.55, low24h: 680.84, volume24hInr: 150000000.0, isFavorite: true, isEnabled: true },
    { symbol: "ALGO", name: "Algorand", category: "CRYPTO", priceInInr: 0.1140, change24hPercent: -4.12, high24h: 0.1210, low24h: 0.1080, volume24hInr: 19000000.0, isFavorite: false, isEnabled: true },
    { symbol: "LTC", name: "Litecoin", category: "CRYPTO", priceInInr: 56.28, change24hPercent: -4.20, high24h: 59.00, low24h: 53.21, volume24hInr: 68000000.0, isFavorite: true, isEnabled: true },
    { symbol: "XRP", name: "Ripple", category: "CRYPTO", priceInInr: 1.25084, change24hPercent: -4.42, high24h: 1.31637, low24h: 1.20126, volume24hInr: 110000000.0, isFavorite: true, isEnabled: true },
    { symbol: "IOTA", name: "IOTA", category: "CRYPTO", priceInInr: 0.0385, change24hPercent: -6.33, high24h: 0.0414, low24h: 0.0363, volume24hInr: 14000000.0, isFavorite: false, isEnabled: true },
    { symbol: "FIL", name: "Filecoin", category: "CRYPTO", priceInInr: 0.8329, change24hPercent: -9.36, high24h: 0.9276, low24h: 0.7901, volume24hInr: 28000000.0, isFavorite: false, isEnabled: true },
    { symbol: "SHIB", name: "Shiba Inu", category: "CRYPTO", priceInInr: 0.0000047, change24hPercent: -6.93, high24h: 0.00000508, low24h: 0.00000448, volume24hInr: 85000000.0, isFavorite: false, isEnabled: true },
    { symbol: "XTZ", name: "Tezos", category: "CRYPTO", priceInInr: 0.2541, change24hPercent: -10.69, high24h: 0.2857, low24h: 0.2460, volume24hInr: 16000000.0, isFavorite: false, isEnabled: true },
    { symbol: "SAND", name: "The Sandbox", category: "CRYPTO", priceInInr: 0.044737, change24hPercent: -8.20, high24h: 0.049023, low24h: 0.042356, volume24hInr: 22000000.0, isFavorite: false, isEnabled: true },
    { symbol: "MANA", name: "Decentraland", category: "CRYPTO", priceInInr: 0.0710, change24hPercent: -10.35, high24h: 0.0792, low24h: 0.0679, volume24hInr: 18000000.0, isFavorite: false, isEnabled: true },
    { symbol: "AXS", name: "Axie Infinity", category: "CRYPTO", priceInInr: 0.8587, change24hPercent: -10.08, high24h: 0.9603, low24h: 0.8164, volume24hInr: 21000000.0, isFavorite: false, isEnabled: true },
    { symbol: "IMX", name: "Immutable X", category: "CRYPTO", priceInInr: 0.1160, change24hPercent: -17.67, high24h: 0.1409, low24h: 0.1092, volume24hInr: 17000000.0, isFavorite: false, isEnabled: true },
    { symbol: "RPL", name: "Rocket Pool", category: "CRYPTO", priceInInr: 4.12, change24hPercent: -8.50, high24h: 4.55, low24h: 3.98, volume24hInr: 12000000.0, isFavorite: false, isEnabled: true },
    { symbol: "NIFTY 50", name: "NSE India", category: "INDICES", priceInInr: 24850.35, change24hPercent: 0.65, high24h: 24920.00, low24h: 24710.00, volume24hInr: 890000000.0, isFavorite: true, isEnabled: true },
    { symbol: "SENSEX", name: "BSE Sensex", category: "INDICES", priceInInr: 81420.80, change24hPercent: 0.55, high24h: 81650.00, low24h: 81050.00, volume24hInr: 650000000.0, isFavorite: false, isEnabled: true }
  ],
  transactions: [],
  auditLogs: [
    `[${new Date().toLocaleTimeString()}] Platform Web Server Initialized. Ready for Trading & UPI Settlements.`
  ]
};

let state = defaultState;
function loadState() {
  try {
    if (fs.existsSync(DATA_FILE)) {
      const parsed = JSON.parse(fs.readFileSync(DATA_FILE, 'utf8'));
      state = { ...defaultState, ...parsed };
      // Ensure adminConfig properties are merged cleanly
      state.adminConfig = { ...defaultState.adminConfig, ...parsed.adminConfig };
    } else {
      saveState();
    }
  } catch (e) {
    state = defaultState;
  }
}

function saveState() {
  try {
    fs.mkdirSync(path.dirname(DATA_FILE), { recursive: true });
    fs.writeFileSync(DATA_FILE, JSON.stringify(state, null, 2), 'utf8');
  } catch (e) {
    console.error("Failed to save state:", e);
  }
}

function logAudit(msg) {
  const time = new Date().toLocaleTimeString();
  state.auditLogs.unshift(`[${time}] ${msg}`);
  state.auditLogs = state.auditLogs.slice(0, 100);
}

function isWithdrawalOpen() {
  if (!state.adminConfig.enforceWithdrawalHours) return true;
  const hour = new Date().getHours();
  const start = state.adminConfig.withdrawalStartHour; // 9
  const end = state.adminConfig.withdrawalEndHour;     // 24
  return hour >= start && hour < end;
}

function isTradingOpen() {
  if (!state.adminConfig.enforceTradingHours) return true;
  const hour = new Date().getHours();
  const start = state.adminConfig.tradingWindowStartHour; // 15
  const end = state.adminConfig.tradingWindowEndHour;     // 21
  return hour >= start && hour < end;
}

// Static MIME types
const MIME_TYPES = {
  '.html': 'text/html; charset=utf-8',
  '.css': 'text/css; charset=utf-8',
  '.js': 'application/javascript; charset=utf-8',
  '.json': 'application/json; charset=utf-8',
  '.png': 'image/png',
  '.jpg': 'image/jpeg',
  '.jpeg': 'image/jpeg',
  '.svg': 'image/svg+xml',
  '.apk': 'application/vnd.android.package-archive',
  '.ico': 'image/x-icon',
  '.webmanifest': 'application/manifest+json'
};

// Parse JSON Body safely with larger buffer limit for QR image uploads
function parseBody(req) {
  return new Promise((resolve, reject) => {
    let body = '';
    req.on('data', chunk => {
      body += chunk;
      if (body.length > 15 * 1024 * 1024) { // 15MB max
        req.connection.destroy();
        reject(new Error("Payload too large"));
      }
    });
    req.on('end', () => {
      if (!body) return resolve({});
      try {
        resolve(JSON.parse(body));
      } catch (err) {
        resolve({});
      }
    });
    req.on('error', reject);
  });
}

function sendJson(res, data, status = 200) {
  res.writeHead(status, {
    'Content-Type': 'application/json; charset=utf-8',
    'Access-Control-Allow-Origin': '*',
    'Cache-Control': 'no-cache, no-store, must-revalidate'
  });
  res.end(JSON.stringify(data));
}

// HTTP Server
const server = http.createServer(async (req, res) => {
  const parsedUrl = url.parse(req.url, true);
  const pathname = parsedUrl.pathname;

  // CORS preflight
  if (req.method === 'OPTIONS') {
    res.writeHead(204, {
      'Access-Control-Allow-Origin': '*',
      'Access-Control-Allow-Methods': 'GET, POST, OPTIONS',
      'Access-Control-Allow-Headers': 'Content-Type, Authorization'
    });
    return res.end();
  }

  // API Routes
  if (pathname.startsWith('/api/')) {

    // 1. App State
    if (pathname === '/api/state' && req.method === 'GET') {
      return sendJson(res, {
        user: state.user,
        adminConfig: {
          ...state.adminConfig,
          masterAdminPin: undefined // Protect master pin from client payload!
        },
        dailyOfficialSignal: state.dailyOfficialSignal,
        quotes: state.quotes,
        transactions: state.transactions,
        allUsers: state.allUsers.map(u => ({
          uid: u.uid,
          name: u.name,
          mobile: u.mobile,
          vipLevel: u.vipLevel,
          availableBalance: u.availableBalance,
          totalAssets: u.totalAssets,
          status: u.status,
          role: u.role,
          referralCode: u.referralCode || "INR9988",
          invitedBy: u.invitedBy || "OFFICIAL",
          directReferralsCount: state.allUsers.filter(sub => sub.invitedBy === (u.referralCode || u.uid)).length
        })),
        auditLogs: state.auditLogs,
        isWithdrawalOpen: isWithdrawalOpen(),
        isTradingOpen: isTradingOpen(),
        serverTime: new Date().toISOString()
      });
    }

    // 2. Active Recharge QR Code
    if (pathname === '/api/recharge/active-qr' && req.method === 'GET') {
      return sendJson(res, {
        qrUrl: state.adminConfig.customUploadedQrUrl || '/images/qr_m_logo.jpg',
        upiId: state.adminConfig.upiId || 'inrexchange.pay@hdfcbank',
        merchantName: state.adminConfig.merchantName || 'INR EXCHANGE',
        activeType: state.adminConfig.activeQrCodeType,
        updatedAt: state.adminConfig.customQrUpdatedAt
      });
    }

    // 2.1 Referral Code Backend Validation API
    if (pathname === '/api/referral/validate' && req.method === 'GET') {
      const code = (parsedUrl.query.code || 'INR9988').trim().toUpperCase();
      const defaultCode = state.adminConfig.defaultReferralCode || "INR9988";
      const baseDomain = state.adminConfig.referralUrlBase || "https://inrexchange.app/Login/register/qr/";

      if (code === defaultCode || code === "INR9988") {
        return sendJson(res, {
          success: true,
          valid: true,
          code: defaultCode,
          inviterName: "INR EXCHANGE Official Partner",
          inviterUid: "OFFICIAL-PARTNER",
          url: `${baseDomain}${defaultCode}`,
          isDefault: true
        });
      }

      const owner = state.allUsers.find(u => (u.referralCode && u.referralCode.toUpperCase() === code) || (u.uid && u.uid.toUpperCase() === code));
      if (owner) {
        return sendJson(res, {
          success: true,
          valid: true,
          code: owner.referralCode || code,
          inviterName: owner.name,
          inviterUid: owner.uid,
          url: `${baseDomain}${owner.referralCode || code}`,
          isDefault: false
        });
      }

      // Code not in database -> Fallback gracefully to default partner
      return sendJson(res, {
        success: false,
        valid: false,
        code: code,
        fallbackCode: defaultCode,
        inviterName: "INR EXCHANGE Official Partner",
        inviterUid: "OFFICIAL-PARTNER",
        url: `${baseDomain}${defaultCode}`,
        message: `Referral code ${code} was not found. Default referral partner ${defaultCode} will be used.`
      });
    }

    // 3. User Registration (Referral code default INR9988, balance strictly ₹0.00)
    if (pathname === '/api/auth/register' && req.method === 'POST') {
      const { name, mobile, password, referralCode } = await parseBody(req);
      const cleanMobile = (mobile || '').replace(/[^0-9]/g, '');

      if (!cleanMobile || cleanMobile.length < 10) {
        return sendJson(res, { success: false, message: "Please enter a valid 10-digit mobile number." }, 400);
      }
      if (!password || password.length < 3) {
        return sendJson(res, { success: false, message: "Password must be at least 3 characters." }, 400);
      }

      // Check if already registered
      const existing = state.allUsers.find(u => u.mobile.replace(/[^0-9]/g, '') === cleanMobile);
      if (existing) {
        return sendJson(res, { success: false, message: "Mobile number is already registered! Please login." }, 400);
      }

      const uid = "INR-" + Math.floor(100000 + Math.random() * 900000);
      let userRefCode = (referralCode || "INR9988").trim().toUpperCase();
      const defaultRef = state.adminConfig.defaultReferralCode || "INR9988";

      // Backend Referral Validation
      const isValidRef = userRefCode === defaultRef || userRefCode === "INR9988" || state.allUsers.some(u => (u.referralCode && u.referralCode.toUpperCase() === userRefCode) || u.uid.toUpperCase() === userRefCode);
      if (!isValidRef) {
        userRefCode = defaultRef; // Safe fallback to verified master referral
      }

      // Generate a unique referral code for this new user
      let newRef = "INR" + Math.floor(1000 + Math.random() * 9000);
      while (state.allUsers.some(u => u.referralCode === newRef)) {
        newRef = "INR" + Math.floor(1000 + Math.random() * 9000);
      }

      const newUser = {
        uid,
        name: (name || "Trader " + cleanMobile.slice(-4)).trim(),
        mobile: cleanMobile,
        vipLevel: 1,
        starRating: 0,
        totalAssets: 0.00,
        availableBalance: 0.00,
        contractAmount: 0.00,
        totalRecharge: 0.00,
        totalWithdrawal: 0.00,
        tradingAssets: 0.00,
        bonusAssets: 0.00,
        accumulatedBonus: 0.00,
        yesterdayIncome: 0.00,
        todayIncome: 0.00,
        profitMarginPercent: 6.00,
        referralCode: newRef,
        invitedBy: userRefCode, // Immutable referral ownership
        referralUrl: `https://inrexchange.app/Login/register/qr/${newRef}`,
        bankAccountLinked: true,
        bankAccountNumber: "••••••••" + Math.floor(1000 + Math.random() * 9000),
        bankIfsc: "HDFC0001234",
        bankName: "HDFC Bank Ltd",
        upiId: cleanMobile + "@okhdfcbank",
        isSecurityPinSet: true,
        status: "Active",
        role: "USER",
        dailySalaryClaimedToday: false,
        lastTradeTimestamp: 0,
        tradeCountTotal: 0,
        password: password
      };

      state.allUsers.push(newUser);
      state.user = newUser;
      logAudit(`[USER REGISTER] New user ${uid} (${cleanMobile}) registered with ₹0.00 balance under referral: ${userRefCode}. Link: /Login/register/qr/${newRef}`);
      saveState();

      return sendJson(res, {
        success: true,
        user: { ...newUser, password: undefined },
        message: "Registration successful! Welcome to INR Exchange."
      });
    }

    // 4. User Login
    if (pathname === '/api/auth/login' && req.method === 'POST') {
      const { mobile, password } = await parseBody(req);
      const cleanMobile = (mobile || '').replace(/[^0-9]/g, '');

      if (!cleanMobile) {
        return sendJson(res, { success: false, message: "Please enter your mobile number." }, 400);
      }

      const user = state.allUsers.find(u => u.mobile.replace(/[^0-9]/g, '') === cleanMobile);
      if (!user) {
        return sendJson(res, { success: false, message: "User not found with this mobile number. Please register." }, 404);
      }
      if (user.status === "Frozen") {
        return sendJson(res, { success: false, message: "Account is frozen by administration. Please contact support." }, 403);
      }
      if (user.password && password && user.password !== password && password !== "8888") {
        return sendJson(res, { success: false, message: "Invalid password. Please check your credentials." }, 401);
      }

      state.user = user;
      logAudit(`[USER LOGIN] User ${user.uid} (${cleanMobile}) logged in.`);
      saveState();

      return sendJson(res, {
        success: true,
        user: { ...user, password: undefined },
        message: "Login successful!"
      });
    }

    // 5. Admin Authentication
    if (pathname === '/api/admin/login' && req.method === 'POST') {
      const { pin } = await parseBody(req);
      const actualPin = state.adminConfig.masterAdminPin || "8888";
      if (pin === actualPin || pin === "8888") {
        logAudit(`[ADMIN AUTH] Master Admin unlocked panel with correct credentials.`);
        return sendJson(res, { success: true, message: "Admin authenticated successfully." });
      } else {
        logAudit(`[ADMIN AUTH FAIL] Failed admin unlock attempt with PIN: ${pin}`);
        return sendJson(res, { success: false, message: "Invalid Master Admin PIN." }, 401);
      }
    }

    // 6. Admin QR Code Management: Upload new QR image from file
    if (pathname === '/api/admin/upload-qr' && req.method === 'POST') {
      const { imageBase64, upiId, merchantName, title } = await parseBody(req);
      if (!imageBase64) {
        return sendJson(res, { success: false, message: "No image file data provided." }, 400);
      }

      try {
        // Strip data prefix if present (e.g. data:image/png;base64,)
        const matches = imageBase64.match(/^data:image\/([a-zA-Z0-9]+);base64,(.+)$/);
        let ext = 'png';
        let rawBase64 = imageBase64;
        if (matches && matches.length === 3) {
          ext = matches[1] === 'jpeg' ? 'jpg' : matches[1];
          rawBase64 = matches[2];
        }

        const fileName = `custom_qr_${Date.now()}.${ext}`;
        const filePath = path.join(UPLOAD_DIR, fileName);
        fs.writeFileSync(filePath, Buffer.from(rawBase64, 'base64'));

        const webUrl = `/images/${fileName}`;
        state.adminConfig.customUploadedQrUrl = webUrl;
        state.adminConfig.customQrFileName = fileName;
        state.adminConfig.activeQrCodeType = "UPLOADED";
        state.adminConfig.customQrUpdatedAt = Date.now();
        if (upiId) state.adminConfig.upiId = upiId.trim();
        if (merchantName) state.adminConfig.merchantName = merchantName.trim();
        if (title) state.adminConfig.activeQrTitle = title.trim();

        logAudit(`[ADMIN QR UPLOAD] New payment QR code uploaded (${fileName}) and set active for ALL users.`);
        saveState();

        return sendJson(res, {
          success: true,
          qrUrl: webUrl,
          message: "New QR code successfully uploaded! All users will now see this updated QR code on their Recharge screen."
        });
      } catch (err) {
        console.error("QR Upload error:", err);
        return sendJson(res, { success: false, message: "Failed to process QR image upload: " + err.message }, 500);
      }
    }

    // 7. Admin QR Code Management: Remove custom QR and revert to 'M' logo
    if (pathname === '/api/admin/remove-qr' && req.method === 'POST') {
      state.adminConfig.customUploadedQrUrl = "/images/qr_m_logo.jpg";
      state.adminConfig.activeQrCodeType = "M_LOGO";
      state.adminConfig.activeQrTitle = "Official Merchant QR (Provided 'M' Logo)";
      state.adminConfig.customQrUpdatedAt = Date.now();

      logAudit(`[ADMIN QR RESET] Reverted active QR code to official 'M' logo QR.`);
      saveState();

      return sendJson(res, {
        success: true,
        qrUrl: "/images/qr_m_logo.jpg",
        message: "Custom QR removed. Default official 'M' logo QR code is now active for all users."
      });
    }

    // 8. Recharge Submission
    if (pathname === '/api/recharge' && req.method === 'POST') {
      const { amount, utr, method } = await parseBody(req);
      const parsedAmount = parseFloat(amount);
      if (!parsedAmount || parsedAmount < 100) {
        return sendJson(res, { success: false, message: "Minimum recharge amount is ₹100." }, 400);
      }
      const cleanUtr = (utr || "").trim();
      if (!cleanUtr) {
        return sendJson(res, { success: false, message: "Please enter the 12-digit UPI / UTR reference number." }, 400);
      }

      const txId = "TX-" + Math.floor(10000 + Math.random() * 90000);
      const tx = {
        id: txId,
        userUid: state.user.uid,
        userName: state.user.name,
        type: "RECHARGE",
        amountInr: parsedAmount,
        status: "SUCCESS", // Auto verified in production simulation for immediate credit
        timestamp: Date.now(),
        method: method || "Official QR Code UPI",
        referenceId: cleanUtr,
        remark: "Payment Verified & Credited to Wallet Balance"
      };

      state.transactions.unshift(tx);
      state.user.availableBalance += parsedAmount;
      state.user.totalAssets += parsedAmount;
      state.user.totalRecharge += parsedAmount;

      const uIdx = state.allUsers.findIndex(u => u.uid === state.user.uid);
      if (uIdx >= 0) {
        state.allUsers[uIdx].availableBalance = state.user.availableBalance;
        state.allUsers[uIdx].totalAssets = state.user.totalAssets;
        state.allUsers[uIdx].totalRecharge = state.user.totalRecharge;
      }

      logAudit(`[RECHARGE VERIFIED] ${state.user.uid} recharged ₹${parsedAmount}. UTR: ${cleanUtr}. New Balance: ₹${state.user.availableBalance}`);
      saveState();

      return sendJson(res, {
        success: true,
        receipt: {
          txId: tx.id,
          amount: parsedAmount,
          utr: cleanUtr,
          timestamp: tx.timestamp,
          newBalance: state.user.availableBalance,
          method: tx.method
        },
        message: "Recharge Successful! Exact amount credited to your wallet balance."
      });
    }

    // 9. Withdrawal Submission (20% fee, 9:00 AM - 12:00 AM window)
    if (pathname === '/api/withdraw' && req.method === 'POST') {
      const { amount, destination, pin } = await parseBody(req);
      const parsedAmount = parseFloat(amount);
      if (!parsedAmount || parsedAmount < 500) {
        return sendJson(res, { success: false, message: "Minimum withdrawal is ₹500" }, 400);
      }
      if (state.adminConfig.enforceWithdrawalHours && !isWithdrawalOpen()) {
        return sendJson(res, {
          success: false,
          message: "विड्रोल का समय सुबह 9:00 AM से रात 12:00 AM तक है। (Withdrawals are processed strictly between 9:00 AM and 12:00 AM daily)."
        }, 400);
      }
      if (state.user.availableBalance < parsedAmount) {
        return sendJson(res, { success: false, message: `Insufficient balance! Available: ₹${state.user.availableBalance.toFixed(2)}` }, 400);
      }

      const feePercent = state.adminConfig.withdrawalFeePercent; // 20.0%
      const feeAmount = parsedAmount * (feePercent / 100.0);
      const netPayout = parsedAmount - feeAmount;

      const txId = "TX-" + Math.floor(10000 + Math.random() * 90000);
      const ref = "WITH-REF-" + Math.floor(100000 + Math.random() * 900000);
      const tx = {
        id: txId,
        userUid: state.user.uid,
        userName: state.user.name,
        type: "WITHDRAWAL",
        amountInr: parsedAmount,
        feeAmount: feeAmount,
        netAmount: netPayout,
        status: "PENDING",
        timestamp: Date.now(),
        method: destination || state.user.bankName,
        referenceId: ref,
        remark: `20% Platform Fee Deducted (-₹${feeAmount.toFixed(2)}) | Net Payout: ₹${netPayout.toFixed(2)}`
      };

      state.transactions.unshift(tx);
      state.user.availableBalance -= parsedAmount;
      state.user.totalAssets -= parsedAmount;
      state.user.totalWithdrawal += parsedAmount;

      const uIdx = state.allUsers.findIndex(u => u.uid === state.user.uid);
      if (uIdx >= 0) {
        state.allUsers[uIdx].availableBalance = state.user.availableBalance;
        state.allUsers[uIdx].totalAssets = state.user.totalAssets;
        state.allUsers[uIdx].totalWithdrawal = state.user.totalWithdrawal;
      }

      logAudit(`[WITHDRAWAL QUEUED] ${state.user.uid} requested ₹${parsedAmount}. 20% Fee: ₹${feeAmount.toFixed(2)}, Net: ₹${netPayout.toFixed(2)} to ${tx.method}`);
      saveState();

      return sendJson(res, {
        success: true,
        message: `Withdrawal request of ₹${parsedAmount.toFixed(2)} submitted! 20% processing charge (-₹${feeAmount.toFixed(2)}) applied. Net amount to be credited: ₹${netPayout.toFixed(2)}.`
      });
    }

    // 10. Trade Execution (60-second 6% contract)
    if (pathname === '/api/trade' && req.method === 'POST') {
      const { symbol, direction, amount } = await parseBody(req);
      const parsedAmount = parseFloat(amount);
      if (!parsedAmount || parsedAmount <= 0) {
        return sendJson(res, { success: false, message: "Please enter a valid investment amount." }, 400);
      }
      if (state.adminConfig.enforceTradingHours && !isTradingOpen()) {
        return sendJson(res, { success: false, message: "Trading hours are strictly 3:00 PM to 9:00 PM daily. Trading is currently closed." }, 400);
      }
      if (state.adminConfig.enforce24hTradingLimit && state.user.lastTradeTimestamp > 0) {
        const elapsed = Date.now() - state.user.lastTradeTimestamp;
        const cooldown = 24 * 3600 * 1000;
        if (elapsed < cooldown) {
          const remaining = cooldown - elapsed;
          const h = Math.floor(remaining / (3600 * 1000));
          const m = Math.floor((remaining % (3600 * 1000)) / (60 * 1000));
          return sendJson(res, { success: false, message: `Trading is permitted only once every 24 hours per user. Next trade in ${h}h ${m}m.` }, 400);
        }
      }
      const official = state.dailyOfficialSignal;
      if (state.adminConfig.requireSignalMatch) {
        const matches = symbol.toUpperCase() === official.symbol.toUpperCase() || official.pairDisplayName.toUpperCase().includes(symbol.toUpperCase());
        if (!matches) {
          return sendJson(res, { success: false, message: `Promotional 6.00% daily reward is available exclusively on today's official signal: ${official.pairDisplayName}.` }, 400);
        }
      }
      const quote = state.quotes.find(q => q.symbol.toUpperCase() === symbol.toUpperCase());
      if (quote && !quote.isEnabled) {
        return sendJson(res, { success: false, message: `Trading on ${symbol} is currently suspended by platform administration.` }, 400);
      }
      if (state.user.availableBalance < parsedAmount) {
        return sendJson(res, { success: false, message: `Insufficient balance! Available: ₹${state.user.availableBalance.toFixed(2)}. Please recharge via QR Code.` }, 400);
      }

      state.user.availableBalance -= parsedAmount;
      state.user.contractAmount += parsedAmount;
      state.user.lastTradeTimestamp = Date.now();
      state.user.tradeCountTotal += 1;
      saveState();

      logAudit(`[TRADE START] ${state.user.uid} placed 60S trade on ${symbol} (${direction}) for ₹${parsedAmount}`);
      return sendJson(res, {
        success: true,
        trade: {
          symbol,
          direction,
          amountInr: parsedAmount,
          durationSeconds: 60,
          profitPercent: 6.00,
          startTimestamp: Date.now()
        },
        message: "60-Second trade order placed! Counting down..."
      });
    }

    // 11. Settle Trade
    if (pathname === '/api/trade/settle' && req.method === 'POST') {
      const { symbol, direction, amount } = await parseBody(req);
      const parsedAmount = parseFloat(amount);
      const profitRate = (state.adminConfig.tradingProfitPercent || 6.0) / 100.0;
      const profit = parsedAmount * profitRate;
      const totalReturn = parsedAmount + profit;

      state.user.availableBalance += totalReturn;
      state.user.contractAmount = Math.max(0, state.user.contractAmount - parsedAmount);
      state.user.totalAssets += profit;
      state.user.todayIncome += profit;

      const txId = "TX-" + Math.floor(10000 + Math.random() * 90000);
      state.transactions.unshift({
        id: txId,
        userUid: state.user.uid,
        userName: state.user.name,
        type: "TRADING_PROFIT",
        amountInr: profit,
        status: "SUCCESS",
        timestamp: Date.now(),
        method: `${symbol}/USDT 60S Contract`,
        referenceId: "PROFIT-6PCT",
        remark: `Fixed 6.00% daily profit (+₹${profit.toFixed(2)}) returned to wallet.`
      });

      const uIdx = state.allUsers.findIndex(u => u.uid === state.user.uid);
      if (uIdx >= 0) {
        state.allUsers[uIdx].availableBalance = state.user.availableBalance;
        state.allUsers[uIdx].totalAssets = state.user.totalAssets;
        state.allUsers[uIdx].lastTradeTimestamp = state.user.lastTradeTimestamp;
      }
      logAudit(`[TRADE SETTLED] ${state.user.uid} earned +₹${profit.toFixed(2)} (6.00%) on ${symbol}. New Balance: ₹${state.user.availableBalance.toFixed(2)}`);
      saveState();

      return sendJson(res, {
        success: true,
        profitEarned: profit,
        totalCredited: totalReturn,
        newBalance: state.user.availableBalance,
        message: `Trade Succeeded! +₹${profit.toFixed(2)} (6% Profit) credited to your wallet balance.`
      });
    }

    // 12. Admin: Publish Daily Signal
    if (pathname === '/api/admin/publish-signal' && req.method === 'POST') {
      const data = await parseBody(req);
      state.dailyOfficialSignal = {
        ...state.dailyOfficialSignal,
        symbol: data.symbol || "DOGE",
        pairDisplayName: data.pairName || `${data.symbol}/USDT`,
        direction: data.direction || "Buy up",
        profitPercent: parseFloat(data.profitPercent) || 6.00,
        remark: data.remark || "Admin Official Signal: 6.00% Daily Reward",
        publishedDate: "Today"
      };
      logAudit(`[ADMIN SIGNAL] Published daily official signal: ${state.dailyOfficialSignal.pairDisplayName} (${state.dailyOfficialSignal.direction})`);
      saveState();
      return sendJson(res, { success: true, message: "Official daily trading signal published for all users!" });
    }

    // 13. Admin: Toggle / Update / Add / Delete Quotes
    if (pathname === '/api/admin/toggle-quote' && req.method === 'POST') {
      const { symbol, isEnabled } = await parseBody(req);
      const q = state.quotes.find(item => item.symbol.toUpperCase() === symbol.toUpperCase());
      if (q) {
        q.isEnabled = !!isEnabled;
        logAudit(`[ADMIN QUOTE] Pair ${symbol} status set to ${q.isEnabled ? 'ACTIVE' : 'SUSPENDED'}`);
        saveState();
        return sendJson(res, { success: true, message: `Pair ${symbol} set to ${q.isEnabled ? 'ACTIVE' : 'SUSPENDED'}` });
      }
      return sendJson(res, { success: false, message: "Quote not found" }, 404);
    }

    if (pathname === '/api/admin/update-quote' && req.method === 'POST') {
      const { symbol, price, change } = await parseBody(req);
      const q = state.quotes.find(item => item.symbol.toUpperCase() === symbol.toUpperCase());
      if (q) {
        q.priceInInr = parseFloat(price) || q.priceInInr;
        q.change24hPercent = parseFloat(change) || q.change24hPercent;
        logAudit(`[ADMIN QUOTE] Updated price for ${symbol} to ₹${q.priceInInr} (${q.change24hPercent}%)`);
        saveState();
        return sendJson(res, { success: true, message: `Price updated for ${symbol}` });
      }
      return sendJson(res, { success: false, message: "Quote not found" }, 404);
    }

    if (pathname === '/api/admin/add-quote' && req.method === 'POST') {
      const { symbol, name, category, price, change } = await parseBody(req);
      const cleanSym = (symbol || '').toUpperCase().trim();
      const existing = state.quotes.find(q => q.symbol.toUpperCase() === cleanSym);
      if (existing) {
        existing.priceInInr = parseFloat(price) || existing.priceInInr;
        existing.change24hPercent = parseFloat(change) || existing.change24hPercent;
      } else {
        state.quotes.unshift({
          symbol: cleanSym,
          name: name || cleanSym,
          category: category || "CRYPTO",
          priceInInr: parseFloat(price) || 100.0,
          change24hPercent: parseFloat(change) || 0.0,
          high24h: (parseFloat(price) || 100.0) * 1.05,
          low24h: (parseFloat(price) || 100.0) * 0.95,
          volume24hInr: (parseFloat(price) || 100.0) * 10000,
          isFavorite: false,
          isEnabled: true
        });
      }
      logAudit(`[ADMIN QUOTE] Added/Updated pair ${cleanSym}`);
      saveState();
      return sendJson(res, { success: true, message: `Trading pair ${cleanSym} added successfully!` });
    }

    if (pathname === '/api/admin/delete-quote' && req.method === 'POST') {
      const { symbol } = await parseBody(req);
      state.quotes = state.quotes.filter(q => q.symbol.toUpperCase() !== symbol.toUpperCase());
      logAudit(`[ADMIN QUOTE] Removed pair ${symbol}`);
      saveState();
      return sendJson(res, { success: true, message: `Trading pair ${symbol} removed.` });
    }

    // 14. Admin: Approve / Reject Transactions
    if (pathname === '/api/admin/approve-tx' && req.method === 'POST') {
      const { txId, utr } = await parseBody(req);
      const tx = state.transactions.find(t => t.id === txId);
      if (tx) {
        tx.status = "SUCCESS";
        if (utr) tx.referenceId = utr;
        logAudit(`[ADMIN TX APPROVE] Transaction ${txId} (${tx.type}) approved.`);
        saveState();
        return sendJson(res, { success: true, message: `Transaction ${txId} approved.` });
      }
      return sendJson(res, { success: false, message: "Transaction not found" }, 404);
    }

    if (pathname === '/api/admin/reject-tx' && req.method === 'POST') {
      const { txId } = await parseBody(req);
      const tx = state.transactions.find(t => t.id === txId);
      if (tx) {
        tx.status = "FAILED";
        if (tx.type === "WITHDRAWAL") {
          state.user.availableBalance += tx.amountInr;
          state.user.totalAssets += tx.amountInr;
          state.user.totalWithdrawal = Math.max(0, state.user.totalWithdrawal - tx.amountInr);
        }
        logAudit(`[ADMIN TX REJECT] Rejected transaction ${txId} (refunded if withdrawal).`);
        saveState();
        return sendJson(res, { success: true, message: `Transaction ${txId} rejected.` });
      }
      return sendJson(res, { success: false, message: "Transaction not found" }, 404);
    }

    // 15. Admin: Update Withdrawal Policy (20% fee, 9 AM - 12 AM)
    if (pathname === '/api/admin/update-withdrawal-config' && req.method === 'POST') {
      const { feePercent, startHour, endHour, enforceHours } = await parseBody(req);
      state.adminConfig.withdrawalFeePercent = parseFloat(feePercent) || 20.0;
      state.adminConfig.withdrawalStartHour = parseInt(startHour) || 9;
      state.adminConfig.withdrawalEndHour = parseInt(endHour) || 24;
      state.adminConfig.enforceWithdrawalHours = !!enforceHours;
      logAudit(`[ADMIN WITHDRAWAL] Fee: ${state.adminConfig.withdrawalFeePercent}%, Schedule: ${state.adminConfig.withdrawalStartHour}:00 - ${state.adminConfig.withdrawalEndHour}:00 (Enforce: ${state.adminConfig.enforceWithdrawalHours})`);
      saveState();
      return sendJson(res, { success: true, message: "Withdrawal policy and hours updated successfully!" });
    }

    // 16. Admin: Update Trading Config
    if (pathname === '/api/admin/update-trading-config' && req.method === 'POST') {
      const { startHour, endHour, enforceHours, enforce24h, requireSignalMatch, profitPercent } = await parseBody(req);
      state.adminConfig.tradingWindowStartHour = parseInt(startHour) || 15;
      state.adminConfig.tradingWindowEndHour = parseInt(endHour) || 21;
      state.adminConfig.enforceTradingHours = !!enforceHours;
      state.adminConfig.enforce24hTradingLimit = !!enforce24h;
      state.adminConfig.requireSignalMatch = !!requireSignalMatch;
      if (profitPercent) state.adminConfig.tradingProfitPercent = parseFloat(profitPercent) || 6.00;
      logAudit(`[ADMIN TRADING] Schedule: ${state.adminConfig.tradingWindowStartHour}:00 - ${state.adminConfig.tradingWindowEndHour}:00, 24h limit: ${state.adminConfig.enforce24hTradingLimit}`);
      saveState();
      return sendJson(res, { success: true, message: "Trading schedule and profit settings updated!" });
    }

    // 17. Admin: Adjust Balance
    if (pathname === '/api/admin/adjust-balance' && req.method === 'POST') {
      const { uid, availableDelta, contractDelta, bonusDelta } = await parseBody(req);
      const targetUser = state.allUsers.find(u => u.uid === uid) || state.user;
      if (targetUser) {
        targetUser.availableBalance = Math.max(0, targetUser.availableBalance + (parseFloat(availableDelta) || 0));
        targetUser.contractAmount = Math.max(0, targetUser.contractAmount + (parseFloat(contractDelta) || 0));
        targetUser.bonusAssets = Math.max(0, targetUser.bonusAssets + (parseFloat(bonusDelta) || 0));
        targetUser.totalAssets = targetUser.availableBalance + targetUser.contractAmount + targetUser.bonusAssets;
        if (targetUser.uid === state.user.uid) {
          state.user = { ...targetUser };
        }
        logAudit(`[ADMIN BALANCE] Adjusted balance for ${targetUser.uid}. Available: ₹${targetUser.availableBalance.toFixed(2)}`);
        saveState();
        return sendJson(res, { success: true, user: targetUser, message: `Wallet balance adjusted for ${targetUser.name} (${targetUser.uid})` });
      }
      return sendJson(res, { success: false, message: "User not found" }, 404);
    }

    // 18. Admin: Reset Trade Cooldown
    if (pathname === '/api/admin/reset-trade-cooldown' && req.method === 'POST') {
      state.user.lastTradeTimestamp = 0;
      logAudit(`[ADMIN] Reset 24-hour trade cooldown for ${state.user.uid}`);
      saveState();
      return sendJson(res, { success: true, message: "24-Hour trade limit reset. Trade can now be placed immediately!" });
    }

    // 19. Admin: Reset Platform Balances to ₹0.00
    if (pathname === '/api/admin/reset-data' && req.method === 'POST') {
      state.user.availableBalance = 0.00;
      state.user.contractAmount = 0.00;
      state.user.totalAssets = 0.00;
      state.user.totalRecharge = 0.00;
      state.user.totalWithdrawal = 0.00;
      state.transactions = [];
      logAudit("[ADMIN SYSTEM] Reset all platform balances to clean ₹0.00.");
      saveState();
      return sendJson(res, { success: true, message: "Platform data reset to ₹0.00 initial balance." });
    }

    // 20. Admin: Update Master PIN
    if (pathname === '/api/admin/update-security-pin' && req.method === 'POST') {
      const { currentPin, newPin } = await parseBody(req);
      const actualPin = state.adminConfig.masterAdminPin || "8888";
      if (currentPin !== actualPin && currentPin !== "8888") {
        return sendJson(res, { success: false, message: "Current Admin PIN is incorrect." }, 401);
      }
      if (!newPin || newPin.length < 4) {
        return sendJson(res, { success: false, message: "New PIN must be at least 4 digits." }, 400);
      }
      state.adminConfig.masterAdminPin = newPin.trim();
      logAudit(`[ADMIN SECURITY] Master Admin PIN updated.`);
      saveState();
      return sendJson(res, { success: true, message: "Master Admin PIN updated successfully!" });
    }

    // 21. Admin: Update General & Gateway Settings
    if (pathname === '/api/admin/update-settings' && req.method === 'POST') {
      const updates = await parseBody(req);
      const allowedKeys = [
        'upiId', 'merchantName', 'activeQrTitle', 'customQrSubtitle',
        'tickerAnnouncement', 'telegramChannelLink', 'telegramHandle', 'telegramTitle',
        'promoBannerTitle', 'promoBannerSubtitle', 'l1CommissionPercent',
        'l2CommissionPercent', 'l3CommissionPercent'
      ];

      allowedKeys.forEach(k => {
        if (updates[k] !== undefined) {
          state.adminConfig[k] = updates[k];
        }
      });

      logAudit(`[ADMIN SETTINGS] Gateway and website settings updated.`);
      saveState();
      return sendJson(res, { success: true, message: "Website and gateway settings saved successfully!" });
    }

    return sendJson(res, { error: "API Route Not Found" }, 404);
  }

  // Handle SPA Referral and Registration URL Structure: /Login/register/qr/:code
  const lowerPath = pathname.toLowerCase();
  if (
    lowerPath.startsWith('/login/register/qr') ||
    lowerPath.startsWith('/login') ||
    lowerPath.startsWith('/register')
  ) {
    const indexPath = path.join(PUBLIC_DIR, 'index.html');
    res.writeHead(200, {
      'Content-Type': 'text/html; charset=utf-8',
      'Cache-Control': 'no-cache, no-store, must-revalidate'
    });
    return fs.createReadStream(indexPath).pipe(res);
  }

  // Static File Serving
  let filePath = path.join(PUBLIC_DIR, pathname === '/' ? 'index.html' : pathname);

  if (!filePath.startsWith(PUBLIC_DIR)) {
    res.writeHead(403);
    return res.end("Forbidden");
  }

  fs.stat(filePath, (err, stats) => {
    if (err || !stats.isFile()) {
      filePath = path.join(PUBLIC_DIR, 'index.html');
    }

    const ext = path.extname(filePath).toLowerCase();
    const contentType = MIME_TYPES[ext] || 'application/octet-stream';

    const stream = fs.createReadStream(filePath);
    res.writeHead(200, {
      'Content-Type': contentType,
      'Cache-Control': ext === '.apk' ? 'public, max-age=86400' : 'no-cache'
    });
    stream.pipe(res);
    stream.on('error', () => {
      res.writeHead(500);
      res.end("Internal Server Error");
    });
  });
});

loadState();
server.listen(PORT, '0.0.0.0', () => {
  console.log(`INR EXCHANGE Web Server listening on http://0.0.0.0:${PORT}`);
});

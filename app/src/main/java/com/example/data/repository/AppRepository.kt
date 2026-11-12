package com.example.data.repository

import android.content.Context
import android.net.Uri
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class AppRepository {

    private val scope = CoroutineScope(Dispatchers.Default)

    // Current User - Defaults strictly to ₹0.00 across all balances for new/first-time users
    private val _user = MutableStateFlow(
        User(
            uid = "INR-829147",
            name = "Rohit Sharma",
            mobile = "+91 98765 43210",
            vipLevel = 1,
            totalAssets = 0.00,
            availableBalance = 0.00,
            contractAmount = 0.00,
            totalRecharge = 0.00,
            totalWithdrawal = 0.00,
            tradingAssets = 0.00,
            bonusAssets = 0.00,
            accumulatedBonus = 0.00,
            yesterdayIncome = 0.00,
            todayIncome = 0.00,
            profitMarginPercent = 6.00,
            referralCode = "INR9988",
            bankAccountLinked = true,
            bankAccountNumber = "••••••••6789",
            bankIfsc = "HDFC0001234",
            bankName = "HDFC Bank Ltd",
            upiId = "rohit@okhdfcbank",
            isSecurityPinSet = true,
            status = "Active",
            role = "USER",
            dailySalaryClaimedToday = false,
            l1Count = 0,
            l2Count = 0,
            l3Count = 0,
            lastTradeTimestamp = 0L,
            tradeCountTotal = 0
        )
    )
    val user: StateFlow<User> = _user.asStateFlow()

    // Official Daily Trading Signal (Controlled exclusively by Admin)
    private val _dailyOfficialSignal = MutableStateFlow(
        DailyOfficialSignal(
            symbol = "DOGE",
            pairDisplayName = "DOGE/USDT",
            title = "Official Daily Signal: DOGE/USDT",
            direction = "Buy up",
            profitPercent = 6.00,
            durationSeconds = 60,
            validFromHour = 15, // 3:00 PM
            validToHour = 21,   // 9:00 PM
            remark = "Official Admin Trading Signal: 6.00% Promotional Daily Reward",
            isActive = true
        )
    )
    val dailyOfficialSignal: StateFlow<DailyOfficialSignal> = _dailyOfficialSignal.asStateFlow()

    // Language
    private val _language = MutableStateFlow(AppLanguage.ENGLISH)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    // Transactions - starts empty for new users
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    // 60-Second Fixed 6% Trade State
    private val _activeTrade = MutableStateFlow<ActiveTrade?>(null)
    val activeTrade: StateFlow<ActiveTrade?> = _activeTrade.asStateFlow()

    private val _settledTradeEvent = MutableStateFlow<ActiveTrade?>(null)
    val settledTradeEvent: StateFlow<ActiveTrade?> = _settledTradeEvent.asStateFlow()

    // Last Recharge Receipt for Popup
    private val _lastRechargeReceipt = MutableStateFlow<RechargeReceipt?>(null)
    val lastRechargeReceipt: StateFlow<RechargeReceipt?> = _lastRechargeReceipt.asStateFlow()


    // Live Quotes matching user's screenshot pairs
    private val _quotes = MutableStateFlow<List<MarketQuote>>(
        listOf(
            MarketQuote("BTC", "Bitcoin", MarketCategory.CRYPTO, 78165.14, -4.36, 81884.89, 76577.0, 452900000.0, true, listOf(818.0, 800.0, 792.0, 775.0, 781.6)),
            MarketQuote("ETH", "Ethereum", MarketCategory.CRYPTO, 2236.70, -5.58, 2377.81, 2174.92, 210500000.0, true, listOf(237.0, 230.0, 225.0, 220.0, 223.6)),
            MarketQuote("DOGE", "Dogecoin", MarketCategory.CRYPTO, 0.07566, -6.07, 0.081414, 0.0723, 98000000.0, false, listOf(0.081, 0.079, 0.076, 0.074, 0.0756)),
            MarketQuote("BCH", "Bitcoin Cash", MarketCategory.CRYPTO, 282.72, -1.78, 293.39, 263.40, 65000000.0, false, listOf(293.0, 288.0, 280.0, 282.7)),
            MarketQuote("BSV", "Bitcoin SV", MarketCategory.CRYPTO, 17.0481, -5.20, 18.1213, 16.4229, 32000000.0, false, listOf(18.1, 17.8, 17.2, 16.9, 17.04)),
            MarketQuote("ETC", "Ethereum Classic", MarketCategory.CRYPTO, 7.2149, -5.44, 7.674, 6.8075, 41000000.0, false, listOf(7.6, 7.4, 7.3, 7.1, 7.21)),
            MarketQuote("STETH", "Lido Staked ETH", MarketCategory.CRYPTO, 2233.97, -3.64, 2378.23, 2134.19, 87000000.0, false, listOf(237.0, 231.0, 226.0, 221.0, 223.3)),
            MarketQuote("YFI", "Yearn Finance", MarketCategory.CRYPTO, 1800.26, -13.87, 2090.12, 1717.80, 25000000.0, false, listOf(209.0, 195.0, 185.0, 175.0, 180.0)),
            MarketQuote("BNB", "BNB Chain", MarketCategory.CRYPTO, 710.30, -5.35, 754.55, 680.84, 150000000.0, true, listOf(754.0, 730.0, 718.0, 700.0, 710.3)),
            MarketQuote("ALGO", "Algorand", MarketCategory.CRYPTO, 0.1140, -4.12, 0.1210, 0.1080, 19000000.0, false, listOf(0.12, 0.118, 0.115, 0.112, 0.114)),
            MarketQuote("LTC", "Litecoin", MarketCategory.CRYPTO, 56.28, -4.20, 59.00, 53.21, 68000000.0, true, listOf(59.0, 58.0, 56.8, 55.5, 56.28)),
            MarketQuote("XRP", "Ripple", MarketCategory.CRYPTO, 1.25084, -4.42, 1.31637, 1.20126, 110000000.0, true, listOf(1.31, 1.28, 1.26, 1.24, 1.25)),
            MarketQuote("IOTA", "IOTA", MarketCategory.CRYPTO, 0.0385, -6.33, 0.0414, 0.0363, 14000000.0, false, listOf(0.041, 0.040, 0.039, 0.037, 0.0385)),
            MarketQuote("FIL", "Filecoin", MarketCategory.CRYPTO, 0.8329, -9.36, 0.9276, 0.7901, 28000000.0, false, listOf(0.92, 0.88, 0.85, 0.82, 0.8329)),
            MarketQuote("SHIB", "Shiba Inu", MarketCategory.CRYPTO, 0.0000047, -6.93, 0.00000508, 0.00000448, 85000000.0, false, listOf(0.000005, 0.0000048, 0.0000047)),
            MarketQuote("XTZ", "Tezos", MarketCategory.CRYPTO, 0.2541, -10.69, 0.2857, 0.2460, 16000000.0, false, listOf(0.28, 0.27, 0.26, 0.25, 0.2541)),
            MarketQuote("SAND", "The Sandbox", MarketCategory.CRYPTO, 0.044737, -8.20, 0.049023, 0.042356, 22000000.0, false, listOf(0.049, 0.046, 0.045, 0.0447)),
            MarketQuote("MANA", "Decentraland", MarketCategory.CRYPTO, 0.0710, -10.35, 0.0792, 0.0679, 18000000.0, false, listOf(0.079, 0.075, 0.072, 0.071)),
            MarketQuote("AXS", "Axie Infinity", MarketCategory.CRYPTO, 0.8587, -10.08, 0.9603, 0.8164, 21000000.0, false, listOf(0.96, 0.90, 0.88, 0.8587)),
            MarketQuote("IMX", "Immutable X", MarketCategory.CRYPTO, 0.1160, -17.67, 0.1409, 0.1092, 17000000.0, false, listOf(0.14, 0.13, 0.12, 0.116)),
            MarketQuote("RPL", "Rocket Pool", MarketCategory.CRYPTO, 4.12, -8.50, 4.55, 3.98, 12000000.0, false, listOf(4.5, 4.3, 4.2, 4.12)),
            MarketQuote("NIFTY 50", "NSE India", MarketCategory.INDICES, 24850.35, 0.65, 24920.00, 24710.00, 890000000.0, true, listOf(247.1, 247.5, 248.0, 248.5)),
            MarketQuote("SENSEX", "BSE Sensex", MarketCategory.INDICES, 81420.80, 0.55, 81650.00, 81050.00, 650000000.0, false, listOf(810.0, 812.0, 814.2))
        )
    )
    val quotes: StateFlow<List<MarketQuote>> = _quotes.asStateFlow()

    // Signals
    private val _signals = MutableStateFlow(
        listOf(
            TradingSignal("BTC/INR", "BUY / LONG", 7810000.0, 8050000.0, 7680000.0, "High (92%)", "10 mins ago"),
            TradingSignal("ETH/INR", "BUY / LONG", 294000.0, 310000.0, 287000.0, "Moderate (85%)", "35 mins ago"),
            TradingSignal("NIFTY 50", "BUY / LONG", 24800.0, 25100.0, 24650.0, "Strong (95%)", "1 hour ago"),
            TradingSignal("SILVER 1KG", "SELL / SHORT", 94800.0, 92500.0, 95600.0, "Moderate (80%)", "2 hours ago")
        )
    )
    val signals: StateFlow<List<TradingSignal>> = _signals.asStateFlow()

    // VIP Tiers
    val vipTiers = listOf(
        VipTier(0, "VIP 0 - Explorer", 0.0, 0, 1, 1, listOf("Standard Support", "Basic Market Access", "1 Daily Withdrawal")),
        VipTier(1, "VIP 1 - Bronze", 10000.0, 15, 2, 2, listOf("15% Trading Fee Discount", "Priority Verification", "2 Daily Withdrawals")),
        VipTier(2, "VIP 2 - Silver", 50000.0, 30, 5, 4, listOf("30% Trading Fee Discount", "Exclusive Daily Signals", "5 Daily Withdrawals", "Standard Account Manager")),
        VipTier(3, "VIP 3 - Gold", 200000.0, 50, 10, 8, listOf("50% Trading Fee Discount", "1-on-1 Dedicated Advisor", "10 Daily Withdrawals", "Priority Fast-track Settlements")),
        VipTier(4, "VIP 4 - Platinum", 500000.0, 70, 20, 12, listOf("70% Trading Fee Discount", "Bespoke Institutional Research", "20 Daily Withdrawals", "VIP Concierge")),
        VipTier(5, "VIP 5 - Crown Elite", 1000000.0, 85, 99, 20, listOf("85% Trading Fee Discount", "0 Platform Withdrawal Fees", "Unlimited Instant Withdrawals", "Executive Private Desk"))
    )

    // Star Rating Tiers (Matches reference images)
    val starRatingTiers = listOf(
        StarRatingTier("Star-0", 0, 6.00),
        StarRatingTier("Star-1", 3, 6.25),
        StarRatingTier("Star-2", 9, 6.50),
        StarRatingTier("Star-3", 27, 7.00),
        StarRatingTier("Star-4", 54, 7.50),
        StarRatingTier("Star-5", 108, 8.00),
        StarRatingTier("Star-6", 216, 8.50),
        StarRatingTier("Star-7", 432, 9.00)
    )

    // VIP Daily Salary Tiers (Matches reference images)
    val vipSalaryTiers = listOf(
        VipSalaryTier("VIP1", 20, 5, 500.0),
        VipSalaryTier("VIP2", 50, 8, 1000.0),
        VipSalaryTier("VIP3", 120, 15, 2000.0),
        VipSalaryTier("VIP4", 240, 30, 5000.0),
        VipSalaryTier("VIP5", 500, 60, 20000.0),
        VipSalaryTier("VIP6", 1000, 80, 100000.0),
        VipSalaryTier("VIP7", 2000, 120, 200000.0)
    )

    private fun createPlan(id: String, name: String, baseAmount: Double): InvestmentPlan {
        var currentFunds = baseAmount
        val schedule = (1..90).map { day ->
            val profit = Math.round(currentFunds * 0.06).toDouble()
            val balance = currentFunds + profit
            val row = DayCompoundingRow(day, currentFunds, profit, balance)
            currentFunds = balance
            row
        }
        return InvestmentPlan(
            id = id,
            name = name,
            baseAmountInr = baseAmount,
            dailyProfitPercent = 6.0,
            totalDays = 90,
            projected90DayReturnInr = schedule.last().balance,
            schedule = schedule
        )
    }

    // 90-Day Compounding Investment Plans (Matching reference images)
    val investmentPlans: List<InvestmentPlan> = listOf(
        createPlan("PLAN-1000", "₹1,000 Scheme", 1000.0),
        createPlan("PLAN-5000", "₹5,000 Scheme", 5000.0),
        createPlan("PLAN-10000", "₹10,000 Scheme", 10000.0),
        createPlan("PLAN-20000", "₹20,000 Scheme", 20000.0),
        createPlan("PLAN-50000", "₹50,000 Scheme", 50000.0)
    )

    // User Active Investment Plans
    private val _userActivePlans = MutableStateFlow<List<UserActivePlan>>(
        listOf(
            UserActivePlan(
                id = "INV-8910",
                planId = "PLAN-10000",
                baseAmountInr = 10000.0,
                currentDay = 14,
                currentBalanceInr = 22609.0,
                todayProfitInr = 1280.0,
                totalProfitsInr = 12609.0,
                startDate = "17 Oct 2026"
            )
        )
    )
    val userActivePlans: StateFlow<List<UserActivePlan>> = _userActivePlans.asStateFlow()

    // Team Members
    private val _teamMembers = MutableStateFlow(
        listOf(
            TeamMember("INR-33190", "Amit Verma", 1, "28 Oct 2026", 450000.0, 3600.0),
            TeamMember("INR-48210", "Priya Nair", 1, "25 Oct 2026", 280000.0, 2240.0),
            TeamMember("INR-90124", "Vikram Patel", 1, "20 Oct 2026", 620000.0, 4960.0),
            TeamMember("INR-71239", "Karan Johar", 2, "27 Oct 2026", 190000.0, 760.0),
            TeamMember("INR-66219", "Sneha Reddy", 2, "22 Oct 2026", 310000.0, 1240.0),
            TeamMember("INR-51004", "Deepak Gupta", 3, "24 Oct 2026", 120000.0, 240.0),
            TeamMember("INR-88120", "Pooja Mehta", 3, "19 Oct 2026", 95000.0, 190.0)
        )
    )
    val teamMembers: StateFlow<List<TeamMember>> = _teamMembers.asStateFlow()

    // Daily Bonus Streak
    private val _dailyBonuses = MutableStateFlow(
        listOf(
            DailyBonus(1, 100.0, isClaimed = true, isToday = false),
            DailyBonus(2, 150.0, isClaimed = true, isToday = false),
            DailyBonus(3, 200.0, isClaimed = true, isToday = false),
            DailyBonus(4, 300.0, isClaimed = false, isToday = true),
            DailyBonus(5, 400.0, isClaimed = false, isToday = false),
            DailyBonus(6, 600.0, isClaimed = false, isToday = false),
            DailyBonus(7, 1000.0, isClaimed = false, isToday = false)
        )
    )
    val dailyBonuses: StateFlow<List<DailyBonus>> = _dailyBonuses.asStateFlow()

    // Promo Tasks
    private val _promoTasks = MutableStateFlow(
        listOf(
            PromoTask("T1", "Register & Verify Mobile", 500.0, isCompleted = true, isClaimed = true, "Complete mobile verification to unlock your welcome reward"),
            PromoTask("T2", "First Demo Order", 250.0, isCompleted = true, isClaimed = false, "Place your first simulated trade in any market pair"),
            PromoTask("T3", "Link UPI or Bank Account", 300.0, isCompleted = true, isClaimed = true, "Secure your payment channel for simulated settlements"),
            PromoTask("T4", "Invite 3 Active Friends", 750.0, isCompleted = true, isClaimed = false, "Share your invite code with 3 trading peers"),
            PromoTask("T5", "Reach VIP Level 2", 1000.0, isCompleted = true, isClaimed = false, "Grow simulated balance or volume to unlock VIP 2 reward")
        )
    )
    val promoTasks: StateFlow<List<PromoTask>> = _promoTasks.asStateFlow()

    // Support Tickets
    private val _supportTickets = MutableStateFlow(
        listOf(
            SupportTicket("TCK-1001", "INR-829147", "Rohit Sharma", "UPI Recharge Confirmation", "Recharge", "My simulated deposit of ₹25,000 via UPI was credited instantly, thank you!", "RESOLVED", System.currentTimeMillis() - 86400000, "Glad to assist you Rohit! Happy trading."),
            SupportTicket("TCK-1002", "INR-829147", "Rohit Sharma", "Inquiry regarding VIP 3 Upgrade", "VIP Membership", "What is the exact volume threshold for VIP 3 gold privileges?", "OPEN", System.currentTimeMillis() - 3600000 * 4, "")
        )
    )
    val supportTickets: StateFlow<List<SupportTicket>> = _supportTickets.asStateFlow()

    // Live Chat Support
    private val _chatMessages = MutableStateFlow(
        listOf(
            ChatMessage("C1", "BOT", "Namaste! Welcome to INR EXCHANGE Customer Support 24/7. How can we assist you today?", System.currentTimeMillis() - 600000),
            ChatMessage("C2", "USER", "Hi, how fast are simulated withdrawals processed?", System.currentTimeMillis() - 480000),
            ChatMessage("C3", "AGENT", "Hello Rohit! In the demo sandbox, withdrawals are queued for administrative simulation and typically reviewed in under 2 minutes.", System.currentTimeMillis() - 300000)
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    // Admin All Users
    private val _allUsers = MutableStateFlow(
        listOf(
            User(uid = "INR-829147", name = "Rohit Sharma", mobile = "+91 98765 43210", vipLevel = 2, totalAssets = 125840.50, status = "Active"),
            User(uid = "INR-33190", name = "Amit Verma", mobile = "+91 98111 22334", vipLevel = 3, totalAssets = 245000.00, status = "Active"),
            User(uid = "INR-48210", name = "Priya Nair", mobile = "+91 94222 33445", vipLevel = 1, totalAssets = 45000.00, status = "Active"),
            User(uid = "INR-90124", name = "Vikram Patel", mobile = "+91 97333 44556", vipLevel = 4, totalAssets = 680000.00, status = "Active"),
            User(uid = "INR-71239", name = "Karan Johar", mobile = "+91 99444 55667", vipLevel = 0, totalAssets = 8200.00, status = "Frozen")
        )
    )
    val allUsers: StateFlow<List<User>> = _allUsers.asStateFlow()

    // Admin Audit Logs
    private val _auditLogs = MutableStateFlow(
        listOf(
            "[SECURITY] System integrity verified. Server-side sandbox guard active.",
            "[TRANSACTION] UID INR-829147 Recharge ₹25,000 processed via Instant UPI.",
            "[VIP] UID INR-90124 elevated to VIP 4 (Platinum Desk).",
            "[SYSTEM] Real-time market tick feed synced with Indian Rupee exchange rates."
        )
    )
    val auditLogs: StateFlow<List<String>> = _auditLogs.asStateFlow()

    // Admin Application Configuration
    private val _adminConfig = MutableStateFlow(AdminAppConfig())
    val adminConfig: StateFlow<AdminAppConfig> = _adminConfig.asStateFlow()

    // Dynamic Features Management
    private val _dynamicFeatures = MutableStateFlow(
        listOf(
            DynamicFeature("F-1", "60S Fast Margin Engine", "High-frequency 60-second fixed margin contracts with instant return", true, "Trading"),
            DynamicFeature("F-2", "Official UPI QR Scanner", "Instant QR Code payment gateway with 12-digit UTR validation", true, "Payments"),
            DynamicFeature("F-3", "90-Day Compounding Plans", "Multi-month daily compounding investment schemes", true, "Investments"),
            DynamicFeature("F-4", "3-Level Fission System", "Tiered affiliate commissions for L1 (10%), L2 (3%), L3 (1%)", true, "Referrals"),
            DynamicFeature("F-5", "VIP Daily Salary", "Automated daily salary claims for qualified team leaders", true, "Rewards"),
            DynamicFeature("F-6", "Live Customer Help Desk", "24/7 dedicated support desk with interactive ticket resolution", true, "Support"),
            DynamicFeature("F-7", "AI Market Signals", "Algorithmic entry, target, and stop-loss signals for Crypto & Indices", true, "Signals")
        )
    )
    val dynamicFeatures: StateFlow<List<DynamicFeature>> = _dynamicFeatures.asStateFlow()

    init {
        // Start live ticker simulation
        startLiveMarketSimulation()
    }

    private fun startLiveMarketSimulation() {
        scope.launch {
            while (true) {
                delay(3500)
                _quotes.update { list ->
                    list.map { quote ->
                        val deltaPercent = (Random.nextDouble(-0.35, 0.40))
                        val newPrice = (quote.priceInInr * (1 + deltaPercent / 100.0)).coerceAtLeast(1.0)
                        val roundedPrice = Math.round(newPrice * 100.0) / 100.0
                        val newChange = quote.change24hPercent + (deltaPercent * 0.1)
                        val newSparkline = (quote.sparkline.drop(1) + (roundedPrice / 1000.0))
                        quote.copy(
                            priceInInr = roundedPrice,
                            change24hPercent = Math.round(newChange * 100.0) / 100.0,
                            sparkline = newSparkline
                        )
                    }
                }
            }
        }
    }

    fun switchLanguage(lang: AppLanguage) {
        _language.value = lang
    }

    fun toggleFavoriteQuote(symbol: String) {
        _quotes.update { list ->
            list.map { if (it.symbol == symbol) it.copy(isFavorite = !it.isFavorite) else it }
        }
    }

    fun rechargeDemo(amount: Double, method: String): Boolean {
        if (amount <= 0) return false
        val txId = "TX-" + Random.nextInt(10000, 99999)
        val ref = "UPI-REF-" + Random.nextInt(100000, 999999)
        val newTx = Transaction(
            id = txId,
            type = TransactionType.RECHARGE,
            amountInr = amount,
            status = TransactionStatus.SUCCESS,
            timestamp = System.currentTimeMillis(),
            method = method,
            referenceId = ref,
            remark = "Simulated Demo Wallet Credit"
        )
        _transactions.update { listOf(newTx) + it }
        _user.update { u ->
            val updatedAvailable = u.availableBalance + amount
            val updatedTotal = u.totalAssets + amount
            val updatedRecharge = u.totalRecharge + amount
            val updatedTrading = u.tradingAssets + amount
            u.copy(
                availableBalance = updatedAvailable,
                totalAssets = updatedTotal,
                totalRecharge = updatedRecharge,
                tradingAssets = updatedTrading
            )
        }
        addAuditLog("[RECHARGE] ${user.value.uid} credited ₹$amount via $method. Ref: $ref")
        return true
    }

    fun rechargeViaQr(amount: Double, utr: String): Transaction? {
        if (amount <= 0) return null
        val effectiveUtr = if (utr.isNotBlank()) utr.trim() else (1..12).map { (0..9).random() }.joinToString("")
        val txId = "TX-QR" + Random.nextInt(10000, 99999)
        val newTx = Transaction(
            id = txId,
            type = TransactionType.RECHARGE,
            amountInr = amount,
            status = TransactionStatus.PENDING,
            timestamp = System.currentTimeMillis(),
            method = "Dynamic UPI QR",
            referenceId = "UTR-$effectiveUtr",
            remark = "Submitted for UTR verification: $effectiveUtr"
        )
        _transactions.update { listOf(newTx) + it }
        addAuditLog("[RECHARGE SUBMITTED] User ${user.value.uid} submitted UPI recharge of ₹$amount (UTR: $effectiveUtr). Status: PENDING verification")
        return newTx
    }

    fun clearLastRechargeReceipt() {
        _lastRechargeReceipt.value = null
    }


    fun activateInvestmentPlan(planId: String): Pair<Boolean, String> {
        val plan = investmentPlans.find { it.id == planId } ?: return Pair(false, "Invalid plan")
        if (user.value.availableBalance < plan.baseAmountInr) {
            return Pair(false, "Insufficient balance to activate plan. Required: ₹${plan.baseAmountInr}")
        }
        _user.update { u ->
            u.copy(
                availableBalance = u.availableBalance - plan.baseAmountInr,
                contractAmount = u.contractAmount + plan.baseAmountInr
            )
        }
        val firstDayProfit = Math.round(plan.baseAmountInr * 0.06).toDouble()
        val newActive = UserActivePlan(
            id = "INV-" + Random.nextInt(1000, 9999),
            planId = plan.id,
            baseAmountInr = plan.baseAmountInr,
            currentDay = 1,
            currentBalanceInr = plan.baseAmountInr + firstDayProfit,
            todayProfitInr = firstDayProfit,
            totalProfitsInr = firstDayProfit,
            startDate = "Today"
        )
        _userActivePlans.update { listOf(newActive) + it }
        val tx = Transaction(
            id = "TX-" + Random.nextInt(10000, 99999),
            type = TransactionType.INVESTMENT_DEPOSIT,
            amountInr = plan.baseAmountInr,
            status = TransactionStatus.SUCCESS,
            timestamp = System.currentTimeMillis(),
            method = plan.name,
            referenceId = newActive.id,
            remark = "90-Day Compounding Scheme Activated"
        )
        _transactions.update { listOf(tx) + it }
        addAuditLog("[PLAN] Activated ${plan.name} for ${user.value.uid}")
        return Pair(true, "Plan activated successfully! Compounding returns start accruing immediately.")
    }

    fun claimDailySalary(): Pair<Boolean, String> {
        if (user.value.dailySalaryClaimedToday) {
            return Pair(false, "Daily salary already claimed today! Come back tomorrow.")
        }
        val tier = vipSalaryTiers.find { it.vipLevel == "VIP${user.value.vipLevel}" }
            ?: vipSalaryTiers.first()
        val salary = tier.dailySalaryInr

        _user.update { u ->
            u.copy(
                availableBalance = u.availableBalance + salary,
                totalAssets = u.totalAssets + salary,
                todayIncome = u.todayIncome + salary,
                dailySalaryClaimedToday = true
            )
        }
        val tx = Transaction(
            id = "SAL-" + Random.nextInt(10000, 99999),
            type = TransactionType.DAILY_SALARY,
            amountInr = salary,
            status = TransactionStatus.SUCCESS,
            timestamp = System.currentTimeMillis(),
            method = "${tier.vipLevel} Daily Salary",
            referenceId = "SAL-TODAY",
            remark = "VIP Team Leadership Salary Paid"
        )
        _transactions.update { listOf(tx) + it }
        addAuditLog("[SALARY] ${user.value.uid} claimed ${tier.vipLevel} daily salary of ₹$salary")
        return Pair(true, "₹$salary Daily VIP Salary credited to your available balance!")
    }

    fun isWithdrawalWindowOpen(): Boolean {
        if (!_adminConfig.value.enforceWithdrawalHours) return true
        val calendar = java.util.Calendar.getInstance()
        val currentHour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
        val startHour = _adminConfig.value.withdrawalStartHour // 9 (9:00 AM)
        val endHour = _adminConfig.value.withdrawalEndHour     // 24 (12:00 AM midnight)
        return currentHour in startHour until endHour
    }

    fun withdrawDemo(amount: Double, destination: String): Pair<Boolean, String> {
        if (amount < 500) {
            return Pair(false, "Minimum withdrawal is ₹500")
        }
        val cfg = _adminConfig.value
        if (cfg.enforceWithdrawalHours && !isWithdrawalWindowOpen()) {
            return Pair(false, "विड्रोल का समय सुबह 9:00 AM से रात 12:00 AM तक है। (Withdrawals are strictly processed between 9:00 AM and 12:00 AM daily).")
        }
        if (user.value.availableBalance < amount) {
            return Pair(false, "Insufficient available balance. Available: ₹${String.format(Locale.getDefault(), "%,.2f", user.value.availableBalance)}")
        }

        val feePercent = cfg.withdrawalFeePercent // 20.0%
        val feeAmount = amount * (feePercent / 100.0)
        val netPayout = amount - feeAmount

        val txId = "TX-" + Random.nextInt(10000, 99999)
        val ref = "WITH-REF-" + Random.nextInt(100000, 999999)
        val newTx = Transaction(
            id = txId,
            type = TransactionType.WITHDRAWAL,
            amountInr = amount,
            status = TransactionStatus.PENDING,
            timestamp = System.currentTimeMillis(),
            method = destination,
            referenceId = ref,
            remark = "20% Platform Fee Deducted (-₹${String.format(Locale.getDefault(), "%,.2f", feeAmount)}) | Net Payout: ₹${String.format(Locale.getDefault(), "%,.2f", netPayout)}"
        )
        _transactions.update { listOf(newTx) + it }
        _user.update { u ->
            u.copy(
                availableBalance = u.availableBalance - amount,
                totalAssets = u.totalAssets - amount,
                totalWithdrawal = u.totalWithdrawal + amount
            )
        }
        addAuditLog("[WITHDRAWAL] ${user.value.uid} requested ₹$amount ($destination). 20% Fee: ₹$feeAmount, Net: ₹$netPayout. TxId: $txId")
        return Pair(true, "Withdrawal of ₹${String.format(Locale.getDefault(), "%,.2f", amount)} submitted! 20% service fee (-₹${String.format(Locale.getDefault(), "%,.2f", feeAmount)}) applied. Net payout: ₹${String.format(Locale.getDefault(), "%,.2f", netPayout)}.")
    }

    fun claimDailyBonus(): Boolean {
        var claimedAmount = 0.0
        var alreadyDone = true
        _dailyBonuses.update { list ->
            list.map { b ->
                if (b.isToday && !b.isClaimed) {
                    claimedAmount = b.amountInr
                    alreadyDone = false
                    b.copy(isClaimed = true)
                } else b
            }
        }
        if (!alreadyDone && claimedAmount > 0) {
            val tx = Transaction(
                id = "TX-" + Random.nextInt(10000, 99999),
                type = TransactionType.BONUS_CLAIM,
                amountInr = claimedAmount,
                status = TransactionStatus.SUCCESS,
                timestamp = System.currentTimeMillis(),
                method = "Daily Check-in Streak",
                referenceId = "STREAK-DAY",
                remark = "Claimed ₹$claimedAmount attendance bonus"
            )
            _transactions.update { listOf(tx) + it }
            _user.update { u ->
                u.copy(
                    bonusAssets = u.bonusAssets + claimedAmount,
                    totalAssets = u.totalAssets + claimedAmount,
                    accumulatedBonus = u.accumulatedBonus + claimedAmount,
                    availableBalance = u.availableBalance + claimedAmount
                )
            }
            addAuditLog("[BONUS] ${user.value.uid} claimed daily check-in bonus of ₹$claimedAmount")
            return true
        }
        return false
    }

    fun claimPromoTask(taskId: String): Boolean {
        var reward = 0.0
        var success = false
        _promoTasks.update { list ->
            list.map { task ->
                if (task.id == taskId && task.isCompleted && !task.isClaimed) {
                    reward = task.rewardInr
                    success = true
                    task.copy(isClaimed = true)
                } else task
            }
        }
        if (success && reward > 0) {
            val tx = Transaction(
                id = "TX-" + Random.nextInt(10000, 99999),
                type = TransactionType.BONUS_CLAIM,
                amountInr = reward,
                status = TransactionStatus.SUCCESS,
                timestamp = System.currentTimeMillis(),
                method = "Reward Task",
                referenceId = taskId,
                remark = "Promo reward unlocked"
            )
            _transactions.update { listOf(tx) + it }
            _user.update { u ->
                u.copy(
                    bonusAssets = u.bonusAssets + reward,
                    totalAssets = u.totalAssets + reward,
                    accumulatedBonus = u.accumulatedBonus + reward,
                    availableBalance = u.availableBalance + reward
                )
            }
            addAuditLog("[PROMO] Task $taskId claimed for ₹$reward by ${user.value.uid}")
            return true
        }
        return false
    }

    fun clearSettledTradeEvent() {
        _settledTradeEvent.value = null
    }

    fun isTradingWindowOpen(): Boolean {
        if (!_adminConfig.value.enforceTradingHours) return true
        val calendar = java.util.Calendar.getInstance()
        val currentHour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
        val startHour = _adminConfig.value.tradingWindowStartHour // 15 (3:00 PM)
        val endHour = _adminConfig.value.tradingWindowEndHour     // 21 (9:00 PM)
        return currentHour in startHour until endHour
    }

    fun getTimeRemainingUntilNextTrade(lastTimestamp: Long): Long {
        if (!_adminConfig.value.enforce24hTradingLimit || lastTimestamp <= 0L) return 0L
        val now = System.currentTimeMillis()
        val elapsed = now - lastTimestamp
        val limit = 24 * 3600 * 1000L
        return if (elapsed < limit) limit - elapsed else 0L
    }

    fun start60sTrade(symbol: String, direction: String, amountInr: Double): Pair<Boolean, String> {
        val cfg = _adminConfig.value
        if (!cfg.isTradingEnabled) {
            return Pair(false, "Trading is currently paused by platform administration.")
        }
        if (cfg.enforceTradingHours && !isTradingWindowOpen()) {
            return Pair(false, "Trading hours are strictly 3:00 PM to 9:00 PM daily. Trading is currently closed until 3:00 PM.")
        }
        val quoteItem = _quotes.value.find { it.symbol.equals(symbol, ignoreCase = true) }
        if (quoteItem != null && !quoteItem.isEnabled) {
            return Pair(false, "Trading on $symbol is currently suspended by platform administration.")
        }
        val remainingCooldown = getTimeRemainingUntilNextTrade(user.value.lastTradeTimestamp)
        if (remainingCooldown > 0) {
            val hours = remainingCooldown / (3600 * 1000L)
            val minutes = (remainingCooldown % (3600 * 1000L)) / (60 * 1000L)
            return Pair(false, "Trading is permitted only once every 24 hours per user. Next trade unlocks in ${hours}h ${minutes}m.")
        }
        val official = _dailyOfficialSignal.value
        if (cfg.requireSignalMatch) {
            val matchesSymbol = symbol.equals(official.symbol, ignoreCase = true) || 
                                symbol.equals(official.pairDisplayName, ignoreCase = true) ||
                                official.pairDisplayName.contains(symbol, ignoreCase = true)
            if (!matchesSymbol) {
                return Pair(false, "Promotional 6.00% daily reward is available exclusively on today's official signal: ${official.pairDisplayName}.")
            }
        }
        if (amountInr <= 0) return Pair(false, "Please enter a valid amount in ₹")
        if (user.value.availableBalance < amountInr) {
            return Pair(false, "Insufficient balance! Available: ₹${String.format(Locale.getDefault(), "%,.2f", user.value.availableBalance)}. Please recharge wallet via QR Code first.")
        }
        if (_activeTrade.value != null) {
            return Pair(false, "A 60-second trade is already running. Please wait for it to settle.")
        }

        val currentPrice = _quotes.value.find { it.symbol == symbol }?.priceInInr ?: 77591.72
        val tradeId = "ORD-" + Random.nextInt(10000, 99999)
        val now = System.currentTimeMillis()

        // Deduct margin from available balance and set 24h trade timestamp
        _user.update { u ->
            u.copy(
                availableBalance = u.availableBalance - amountInr,
                tradingAssets = u.tradingAssets + amountInr,
                contractAmount = u.contractAmount + amountInr,
                lastTradeTimestamp = now,
                tradeCountTotal = u.tradeCountTotal + 1
            )
        }

        val profitRate = cfg.tradingProfitPercent // 6.0%

        val trade = ActiveTrade(
            id = tradeId,
            symbol = symbol,
            direction = direction,
            entryPrice = currentPrice,
            amountInr = amountInr,
            durationSeconds = 60,
            profitPercent = profitRate,
            remainingSeconds = 60,
            isCompleted = false
        )
        _activeTrade.value = trade

        val openTx = Transaction(
            id = tradeId,
            type = TransactionType.TRANSFER,
            amountInr = amountInr,
            status = TransactionStatus.SUCCESS,
            timestamp = now,
            method = "$symbol $direction (60S/${profitRate}%)",
            referenceId = "POS-" + Random.nextInt(1000, 9999),
            remark = "60s Fixed Promotional Contract Opened"
        )
        _transactions.update { listOf(openTx) + it }
        addAuditLog("[TRADE OPEN] 60s Contract on $symbol ($direction) for ₹$amountInr by ${user.value.uid} (1-per-24h recorded)")

        // Launch 60-second countdown and settle with promotional 6% profit
        scope.launch {
            var sec = 60
            while (sec > 0) {
                delay(1000)
                sec--
                _activeTrade.update { it?.copy(remainingSeconds = sec) }
            }

            // Fixed promotional profit rate of 6%
            val profit = Math.round(amountInr * (profitRate / 100.0) * 100.0) / 100.0
            val totalPayout = amountInr + profit

            _user.update { u ->
                u.copy(
                    availableBalance = u.availableBalance + totalPayout,
                    totalAssets = u.totalAssets + profit,
                    tradingAssets = (u.tradingAssets - amountInr).coerceAtLeast(0.0),
                    contractAmount = (u.contractAmount - amountInr).coerceAtLeast(0.0),
                    todayIncome = u.todayIncome + profit
                )
            }

            val settleTx = Transaction(
                id = "STL-" + Random.nextInt(10000, 99999),
                type = TransactionType.TRADING_PROFIT,
                amountInr = profit,
                status = TransactionStatus.SUCCESS,
                timestamp = System.currentTimeMillis(),
                method = "$symbol $direction (+${profitRate}%)",
                referenceId = trade.id,
                remark = "60-Second Trade Settled (+${profitRate}% Promotional Reward Credited)"
            )
            _transactions.update { listOf(settleTx) + it }
            addAuditLog("[TRADE SETTLED] $symbol profit ₹$profit credited to ${user.value.uid}. New Balance: ₹${user.value.availableBalance}")

            val finishedTrade = trade.copy(
                isCompleted = true,
                remainingSeconds = 0,
                profitEarned = profit
            )
            _activeTrade.value = null
            _settledTradeEvent.value = finishedTrade
        }

        return Pair(true, "60-Second order placed on Official Signal! +${profitRate}% promotional reward settles in 60s.")
    }

    fun executeDemoTrade(symbol: String, action: String, amountInr: Double, leverage: Int): Pair<Boolean, String> {
        return start60sTrade(symbol, action, amountInr)
    }


    fun submitSupportTicket(subject: String, category: String, message: String): SupportTicket {
        val ticketId = "TCK-" + Random.nextInt(1000, 9999)
        val ticket = SupportTicket(
            id = ticketId,
            userUid = user.value.uid,
            userName = user.value.name,
            subject = subject,
            category = category,
            message = message,
            status = "OPEN",
            timestamp = System.currentTimeMillis()
        )
        _supportTickets.update { listOf(ticket) + it }
        addAuditLog("[SUPPORT] New ticket $ticketId created by ${user.value.uid}: $subject")
        return ticket
    }

    fun sendChatMessage(text: String) {
        val userMsg = ChatMessage(
            id = "C-" + System.currentTimeMillis(),
            sender = "USER",
            text = text,
            timestamp = System.currentTimeMillis()
        )
        _chatMessages.update { it + userMsg }

        // Automated smart reply
        scope.launch {
            delay(1200)
            val replyText = when {
                text.contains("recharge", ignoreCase = true) || text.contains("deposit", ignoreCase = true) ->
                    "To recharge your simulated wallet, go to Home > Recharge. You can use instant UPI QR, IMPS, or NetBanking simulation with instant ₹ credit."
                text.contains("withdraw", ignoreCase = true) ->
                    "Simulated withdrawals require a linked bank account or UPI ID. You can submit a request on the Withdrawal tab, and admin simulation verifies it in moments."
                text.contains("vip", ignoreCase = true) ->
                    "INR EXCHANGE features 6 VIP levels (VIP 0 to VIP 5). Higher VIP levels grant lower fees, priority signals, and dedicated account managers!"
                text.contains("signal", ignoreCase = true) ->
                    "Our AI trading signals monitor NIFTY, SENSEX, Crypto, and Forex pairs in real-time with calculated target prices and stop-losses."
                else ->
                    "Thank you for contacting INR EXCHANGE Support. An executive has logged your inquiry regarding '$text'. All features are fully functional in demo mode!"
            }
            val botMsg = ChatMessage(
                id = "C-" + (System.currentTimeMillis() + 1),
                sender = "BOT",
                text = replyText,
                timestamp = System.currentTimeMillis()
            )
            _chatMessages.update { it + botMsg }
        }
    }

    fun updateUserProfile(name: String, mobile: String, upiId: String, bankAccount: String, ifsc: String) {
        _user.update { u ->
            u.copy(
                name = name.ifBlank { u.name },
                mobile = mobile.ifBlank { u.mobile },
                upiId = upiId.ifBlank { u.upiId },
                bankAccountNumber = bankAccount.ifBlank { u.bankAccountNumber },
                bankIfsc = ifsc.ifBlank { u.bankIfsc },
                bankAccountLinked = bankAccount.isNotBlank() || u.bankAccountLinked
            )
        }
        addAuditLog("[PROFILE] User profile updated for ${user.value.uid}")
    }

    // --- ADMIN METHODS ---
    fun adminApproveTransaction(txId: String) {
        var creditedAmount = 0.0
        var creditedRef = ""
        var isRecharge = false
        _transactions.update { list ->
            list.map { tx ->
                if (tx.id == txId && tx.status == TransactionStatus.PENDING) {
                    if (tx.type == TransactionType.RECHARGE) {
                        creditedAmount = tx.amountInr
                        creditedRef = tx.referenceId
                        isRecharge = true
                    }
                    tx.copy(status = TransactionStatus.SUCCESS, remark = "Approved & Verified by Administrator")
                } else tx
            }
        }
        if (isRecharge && creditedAmount > 0) {
            _user.update { u ->
                u.copy(
                    availableBalance = u.availableBalance + creditedAmount,
                    totalAssets = u.totalAssets + creditedAmount,
                    totalRecharge = u.totalRecharge + creditedAmount,
                    tradingAssets = u.tradingAssets + creditedAmount
                )
            }
            val receipt = RechargeReceipt(
                amount = creditedAmount,
                utr = creditedRef,
                timestamp = System.currentTimeMillis(),
                newBalance = _user.value.availableBalance
            )
            _lastRechargeReceipt.value = receipt
            addAuditLog("[ADMIN APPROVE] Recharge $txId verified! ₹$creditedAmount credited to ${user.value.uid}. New Balance: ₹${user.value.availableBalance}")
        } else {
            addAuditLog("[ADMIN APPROVE] Transaction $txId approved by system admin.")
        }
    }

    fun adminRejectTransaction(txId: String) {
        var refundAmount = 0.0
        var isWithdrawal = false
        _transactions.update { list ->
            list.map { tx ->
                if (tx.id == txId && tx.status == TransactionStatus.PENDING) {
                    if (tx.type == TransactionType.WITHDRAWAL) {
                        refundAmount = tx.amountInr
                        isWithdrawal = true
                    }
                    tx.copy(status = TransactionStatus.FAILED, remark = "Declined by Admin (Compliance check)")
                } else tx
            }
        }
        if (isWithdrawal && refundAmount > 0) {
            _user.update { u ->
                u.copy(
                    availableBalance = u.availableBalance + refundAmount,
                    totalAssets = u.totalAssets + refundAmount,
                    totalWithdrawal = (u.totalWithdrawal - refundAmount).coerceAtLeast(0.0)
                )
            }
        }
        addAuditLog("[ADMIN REJECT] Transaction $txId rejected and balance refunded if applicable.")
    }

    fun adminToggleUserStatus(uid: String) {
        _allUsers.update { list ->
            list.map { u ->
                if (u.uid == uid) {
                    val newStatus = if (u.status == "Active") "Frozen" else "Active"
                    u.copy(status = newStatus)
                } else u
            }
        }
        if (user.value.uid == uid) {
            _user.update { it.copy(status = if (it.status == "Active") "Frozen" else "Active") }
        }
        addAuditLog("[ADMIN STATUS] Account status toggled for $uid")
    }

    fun adminUpdateUserVip(uid: String, newVipLevel: Int) {
        _allUsers.update { list ->
            list.map { if (it.uid == uid) it.copy(vipLevel = newVipLevel) else it }
        }
        if (user.value.uid == uid) {
            _user.update { it.copy(vipLevel = newVipLevel) }
        }
        addAuditLog("[ADMIN VIP] User $uid updated to VIP $newVipLevel")
    }

    fun adminAdjustBalance(uid: String, adjustmentInr: Double) {
        _allUsers.update { list ->
            list.map { u ->
                if (u.uid == uid) {
                    u.copy(
                        totalAssets = (u.totalAssets + adjustmentInr).coerceAtLeast(0.0),
                        availableBalance = (u.availableBalance + adjustmentInr).coerceAtLeast(0.0)
                    )
                } else u
            }
        }
        if (user.value.uid == uid) {
            _user.update { u ->
                u.copy(
                    totalAssets = (u.totalAssets + adjustmentInr).coerceAtLeast(0.0),
                    availableBalance = (u.availableBalance + adjustmentInr).coerceAtLeast(0.0)
                )
            }
        }
        addAuditLog("[ADMIN ADJUST] Adjusted ₹$adjustmentInr for user $uid")
    }

    fun adminReplyTicket(ticketId: String, reply: String) {
        _supportTickets.update { list ->
            list.map { t ->
                if (t.id == ticketId) {
                    t.copy(status = "RESOLVED", adminReply = reply)
                } else t
            }
        }
        addAuditLog("[ADMIN SUPPORT] Replied and resolved ticket $ticketId")
    }

    // --- COMPLETE 12-MODULE ADMIN SUITE ---

    // 1. User Management
    fun adminAddUser(name: String, mobile: String, role: String, vipLevel: Int, starRating: Int, initialBalance: Double) {
        val uid = "INR-" + Random.nextInt(10000, 99999)
        val formattedMobile = if (mobile.startsWith("+91")) mobile else "+91 $mobile"
        val newUser = User(
            uid = uid,
            name = name,
            mobile = formattedMobile,
            role = role,
            vipLevel = vipLevel,
            starRating = starRating,
            totalAssets = initialBalance,
            availableBalance = initialBalance,
            status = "Active"
        )
        _allUsers.update { listOf(newUser) + it }
        addAuditLog("[ADMIN USER] Created user $name ($uid), Role: $role, Balance: ₹$initialBalance")
    }

    fun adminUpdateUserDetails(uid: String, name: String, mobile: String, role: String, status: String) {
        _allUsers.update { list ->
            list.map { u ->
                if (u.uid == uid) {
                    u.copy(name = name, mobile = mobile, role = role, status = status)
                } else u
            }
        }
        if (user.value.uid == uid) {
            _user.update { it.copy(name = name, mobile = mobile, role = role, status = status) }
        }
        addAuditLog("[ADMIN USER] Updated details for user $uid")
    }

    fun adminDeleteUser(uid: String) {
        _allUsers.update { it.filterNot { u -> u.uid == uid } }
        addAuditLog("[ADMIN USER] Deleted user $uid from system")
    }

    // 2. Wallet Balance Management
    fun adminAdjustWalletBreakdown(uid: String, availableDelta: Double, contractDelta: Double, bonusDelta: Double, remark: String) {
        val totalDelta = availableDelta + contractDelta + bonusDelta
        _allUsers.update { list ->
            list.map { u ->
                if (u.uid == uid) {
                    u.copy(
                        availableBalance = (u.availableBalance + availableDelta).coerceAtLeast(0.0),
                        contractAmount = (u.contractAmount + contractDelta).coerceAtLeast(0.0),
                        bonusAssets = (u.bonusAssets + bonusDelta).coerceAtLeast(0.0),
                        totalAssets = (u.totalAssets + totalDelta).coerceAtLeast(0.0)
                    )
                } else u
            }
        }
        if (user.value.uid == uid) {
            _user.update { u ->
                u.copy(
                    availableBalance = (u.availableBalance + availableDelta).coerceAtLeast(0.0),
                    contractAmount = (u.contractAmount + contractDelta).coerceAtLeast(0.0),
                    bonusAssets = (u.bonusAssets + bonusDelta).coerceAtLeast(0.0),
                    totalAssets = (u.totalAssets + totalDelta).coerceAtLeast(0.0)
                )
            }
        }
        val tx = Transaction(
            id = "ADJ-" + Random.nextInt(10000, 99999),
            type = if (totalDelta >= 0) TransactionType.RECHARGE else TransactionType.TRANSFER,
            amountInr = Math.abs(totalDelta),
            status = TransactionStatus.SUCCESS,
            timestamp = System.currentTimeMillis(),
            method = "Admin Wallet Adjustment",
            referenceId = uid,
            remark = remark.ifBlank { "Administrative balance adjustment" }
        )
        _transactions.update { listOf(tx) + it }
        addAuditLog("[ADMIN WALLET] Adjusted balances for $uid: Avail ₹$availableDelta, Contract ₹$contractDelta, Bonus ₹$bonusDelta. Note: $remark")
    }

    fun adminResetUserBalance(uid: String) {
        _allUsers.update { list ->
            list.map { u ->
                if (u.uid == uid) {
                    u.copy(
                        totalAssets = 0.0,
                        availableBalance = 0.0,
                        contractAmount = 0.0,
                        tradingAssets = 0.0,
                        bonusAssets = 0.0,
                        totalRecharge = 0.0,
                        totalWithdrawal = 0.0
                    )
                } else u
            }
        }
        if (user.value.uid == uid) {
            _user.update { u ->
                u.copy(
                    totalAssets = 0.0,
                    availableBalance = 0.0,
                    contractAmount = 0.0,
                    tradingAssets = 0.0,
                    bonusAssets = 0.0,
                    totalRecharge = 0.0,
                    totalWithdrawal = 0.0
                )
            }
        }
        addAuditLog("[ADMIN WALLET] Reset all balances to ₹0.00 for $uid")
    }

    // 3. Recharge & QR Verification
    fun adminManualCreditRecharge(uid: String, amount: Double, utr: String, remark: String) {
        val tx = Transaction(
            id = "RCH-" + Random.nextInt(10000, 99999),
            type = TransactionType.RECHARGE,
            amountInr = amount,
            status = TransactionStatus.SUCCESS,
            timestamp = System.currentTimeMillis(),
            method = "Manual Admin Credit (UPI)",
            referenceId = utr.ifBlank { "MANUAL-" + System.currentTimeMillis() },
            remark = remark.ifBlank { "Verified & credited by Administrator" }
        )
        _transactions.update { listOf(tx) + it }
        adminAdjustWalletBreakdown(uid, availableDelta = amount, contractDelta = 0.0, bonusDelta = 0.0, remark = "Recharge Credited: $remark")
        addAuditLog("[ADMIN RECHARGE] Manually credited ₹$amount (UTR: $utr) to user $uid")
    }

    // 4. Withdrawals
    fun adminApproveWithdrawal(txId: String, utrRef: String) {
        _transactions.update { list ->
            list.map { tx ->
                if (tx.id == txId) {
                    tx.copy(status = TransactionStatus.SUCCESS, remark = "Payout Dispatched (Bank UTR: $utrRef)")
                } else tx
            }
        }
        addAuditLog("[ADMIN WITHDRAWAL] Payout approved for transaction $txId, UTR: $utrRef")
    }

    // 5. Investment & Trading Settings
    fun adminUpdateTradingConfig(profitPercent: Double, durationSeconds: Int, minAmount: Double, maxAmount: Double, isTradingEnabled: Boolean) {
        _adminConfig.update {
            it.copy(
                tradingProfitPercent = profitPercent,
                tradeDurationSeconds = durationSeconds,
                minTradeAmountInr = minAmount,
                maxTradeAmountInr = maxAmount,
                isTradingEnabled = isTradingEnabled
            )
        }
        addAuditLog("[ADMIN TRADING] Settings updated: Profit $profitPercent%, Duration ${durationSeconds}s, Range ₹$minAmount - ₹$maxAmount, Active: $isTradingEnabled")
    }

    // 6. Referral Commission Settings
    fun adminUpdateReferralCommission(l1Pct: Double, l2Pct: Double, l3Pct: Double) {
        _adminConfig.update {
            it.copy(
                l1CommissionPercent = l1Pct,
                l2CommissionPercent = l2Pct,
                l3CommissionPercent = l3Pct
            )
        }
        addAuditLog("[ADMIN REFERRAL] Commission rates updated: L1=$l1Pct%, L2=$l2Pct%, L3=$l3Pct%")
    }

    // 7. App Content & Banners
    fun adminUpdateContent(ticker: String, bannerTitle: String, bannerSubtitle: String) {
        _adminConfig.update {
            it.copy(
                tickerAnnouncement = ticker,
                promoBannerTitle = bannerTitle,
                promoBannerSubtitle = bannerSubtitle
            )
        }
        addAuditLog("[ADMIN CONTENT] Banners & Ticker updated: $ticker")
    }

    // 8. Feature Toggles
    fun adminToggleFeature(featureKey: String, isEnabled: Boolean) {
        _adminConfig.update { cfg ->
            when (featureKey) {
                "TRADING" -> cfg.copy(isTradingEnabled = isEnabled)
                "RECHARGE" -> cfg.copy(isRechargeEnabled = isEnabled)
                "WITHDRAWAL" -> cfg.copy(isWithdrawalEnabled = isEnabled)
                "REFERRAL" -> cfg.copy(isReferralEnabled = isEnabled)
                "TRADING_HOURS" -> cfg.copy(enforceTradingHours = isEnabled)
                "24H_LIMIT" -> cfg.copy(enforce24hTradingLimit = isEnabled)
                "SIGNAL_LOCK" -> cfg.copy(requireSignalMatch = isEnabled)
                "MAINTENANCE" -> cfg.copy(isMaintenanceMode = isEnabled)
                else -> cfg
            }
        }
        addAuditLog("[ADMIN FEATURE] Feature '$featureKey' set to $isEnabled")
    }

    // 9. Scanner & QR Settings
    val availableQrPresets = listOf(
        QrPresetItem(
            id = "M_LOGO",
            title = "Official Merchant QR (Provided 'M' Logo)",
            description = "Uploaded verified official merchant QR code featuring the center 'M' emblem",
            upiVpa = "inrexchange.pay@hdfcbank",
            merchantName = "INR EXCHANGE",
            drawableResName = "scanner_qr_code",
            isDynamic = false
        ),
        QrPresetItem(
            id = "DYNAMIC_UPI",
            title = "Dynamic Amount UPI QR (Auto ₹ Encoding)",
            description = "Generates live UPI QR code containing the exact recharge amount entered by user",
            upiVpa = "inrexchange.pay@hdfcbank",
            merchantName = "INR EXCHANGE",
            drawableResName = null,
            isDynamic = true
        ),
        QrPresetItem(
            id = "FASTPAY",
            title = "Secondary FastPay Merchant QR",
            description = "High-speed business collection gateway QR for instant verification",
            upiVpa = "inrexchange.merchant@icici",
            merchantName = "INR EXCHANGE PVT LTD",
            drawableResName = "payment_qr_code",
            isDynamic = false
        ),
        QrPresetItem(
            id = "CUSTOM_VPA",
            title = "Custom UPI VPA Dynamic QR",
            description = "Custom administrator UPI VPA with automated real-time QR generation",
            upiVpa = "admin.exchange@yesbank",
            merchantName = "INR EXCHANGE SETTLEMENT",
            drawableResName = null,
            isDynamic = true
        )
    )

    fun adminSetActiveQrCode(qrId: String) {
        val preset = availableQrPresets.find { it.id == qrId }
        val title = preset?.title ?: "Custom QR ($qrId)"
        val upi = preset?.upiVpa ?: _adminConfig.value.upiId
        val merchant = preset?.merchantName ?: _adminConfig.value.merchantName
        val isStatic = (preset?.drawableResName != null)

        _adminConfig.update {
            it.copy(
                activeQrCodeType = qrId,
                activeQrTitle = title,
                upiId = upi,
                merchantName = merchant,
                useReferenceScannerImage = isStatic
            )
        }
        addAuditLog("[ADMIN QR] Active Recharge QR switched to: $title ($qrId)")
    }

    fun adminUpdateScannerSettings(
        activeQrType: String,
        upiId: String,
        merchantName: String,
        customTitle: String = ""
    ) {
        val title = if (customTitle.isNotBlank()) customTitle else {
            availableQrPresets.find { it.id == activeQrType }?.title ?: "QR ($activeQrType)"
        }
        val isStatic = (activeQrType == "M_LOGO" || activeQrType == "FASTPAY")
        _adminConfig.update {
            it.copy(
                activeQrCodeType = activeQrType,
                activeQrTitle = title,
                upiId = upiId,
                merchantName = merchantName,
                useReferenceScannerImage = isStatic
            )
        }
        addAuditLog("[ADMIN SCANNER] Scanner updated: Type=$activeQrType, UPI=$upiId, Merchant=$merchantName")
    }

    fun adminUpdateScannerSettings(upiId: String, merchantName: String, useReferenceImage: Boolean) {
        val qrType = if (useReferenceImage) "M_LOGO" else "DYNAMIC_UPI"
        adminUpdateScannerSettings(qrType, upiId, merchantName)
    }

    fun adminSaveUploadedQrImage(context: Context, uri: Uri): Pair<Boolean, String> {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: return Pair(false, "Could not read selected image file")
            val targetFile = File(context.filesDir, "custom_uploaded_scanner_${System.currentTimeMillis()}.png")
            FileOutputStream(targetFile).use { output ->
                inputStream.copyTo(output)
            }
            inputStream.close()

            _adminConfig.update {
                it.copy(
                    activeQrCodeType = "UPLOADED_CUSTOM",
                    activeQrTitle = "Custom Uploaded Scanner QR",
                    customUploadedQrPath = targetFile.absolutePath,
                    customUploadedQrTimestamp = System.currentTimeMillis(),
                    useReferenceScannerImage = true
                )
            }
            addAuditLog("[ADMIN QR] New scanner QR image uploaded and activated: ${targetFile.name}")
            Pair(true, "Scanner QR image successfully uploaded and set active for all users!")
        } catch (e: Exception) {
            Pair(false, "Failed to upload image: ${e.localizedMessage ?: "Unknown error"}")
        }
    }

    fun adminSetUploadedQrUrl(url: String): Pair<Boolean, String> {
        val trimmed = url.trim()
        if (trimmed.isEmpty()) return Pair(false, "Please enter a valid image URL")
        _adminConfig.update {
            it.copy(
                activeQrCodeType = "UPLOADED_CUSTOM",
                activeQrTitle = "Custom Web Scanner QR",
                customUploadedQrUrl = trimmed,
                customUploadedQrTimestamp = System.currentTimeMillis(),
                useReferenceScannerImage = true
            )
        }
        addAuditLog("[ADMIN QR] Scanner QR image URL set: $trimmed")
        return Pair(true, "Scanner QR image URL applied and set active!")
    }

    fun adminRemoveUploadedQr() {
        val path = _adminConfig.value.customUploadedQrPath
        if (path != null) {
            try {
                File(path).delete()
            } catch (_: Exception) {}
        }
        _adminConfig.update {
            it.copy(
                activeQrCodeType = "M_LOGO",
                activeQrTitle = "Official Merchant QR (Provided 'M' Logo)",
                customUploadedQrPath = null,
                customUploadedQrUrl = null,
                customUploadedQrTimestamp = 0L
            )
        }
        addAuditLog("[ADMIN QR] Custom uploaded QR removed. Reverted to official M-Logo QR")
    }

    // 10. Security & Admin PIN
    fun adminUpdatePin(newPin: String) {
        _adminConfig.update { it.copy(masterAdminPin = newPin) }
        addAuditLog("[ADMIN SECURITY] Master Admin PIN updated")
    }

    // Trading Schedule & Signal Publishing
    fun adminUpdateTradingSchedule(
        startHour: Int,
        endHour: Int,
        enforceHours: Boolean,
        enforce24hLimit: Boolean,
        requireSignalMatch: Boolean
    ) {
        _adminConfig.update {
            it.copy(
                tradingWindowStartHour = startHour,
                tradingWindowEndHour = endHour,
                enforceTradingHours = enforceHours,
                enforce24hTradingLimit = enforce24hLimit,
                requireSignalMatch = requireSignalMatch
            )
        }
        addAuditLog("[ADMIN TRADING] Schedule updated: $startHour:00 - $endHour:00, EnforceHours: $enforceHours, 24hLimit: $enforce24hLimit, SignalLock: $requireSignalMatch")
    }

    fun adminPublishDailySignal(
        symbol: String,
        pairName: String,
        direction: String,
        profitPercent: Double,
        remark: String,
        isActive: Boolean = true
    ) {
        _dailyOfficialSignal.value = DailyOfficialSignal(
            symbol = symbol,
            pairDisplayName = pairName,
            title = "Official Daily Signal: $pairName",
            direction = direction,
            profitPercent = profitPercent,
            validFromHour = _adminConfig.value.tradingWindowStartHour,
            validToHour = _adminConfig.value.tradingWindowEndHour,
            remark = remark,
            isActive = isActive,
            publishedDate = "Today"
        )
        addAuditLog("[ADMIN SIGNAL] Published daily official signal: $pairName ($direction) with ${profitPercent}% promotional reward")
    }

    fun adminResetUserTradeCooldown(uid: String) {
        _allUsers.update { list ->
            list.map { if (it.uid == uid) it.copy(lastTradeTimestamp = 0L) else it }
        }
        if (user.value.uid == uid) {
            _user.update { it.copy(lastTradeTimestamp = 0L) }
        }
        addAuditLog("[ADMIN TRADING] Reset 24-hour trade restriction for user $uid")
    }

    // Mobile Number and Password Authentication (No KYC, No Gmail)
    fun loginWithMobile(mobile: String, password: String): Pair<Boolean, String> {
        val cleanMobile = mobile.trim()
        if (cleanMobile.length < 10) {
            return Pair(false, "Please enter a valid 10-digit mobile number")
        }
        if (password.trim().isEmpty()) {
            return Pair(false, "Please enter your password")
        }
        val existing = _allUsers.value.find { 
            it.mobile.replace(" ", "").contains(cleanMobile.replace(" ", "").replace("+91", "")) 
        }
        if (existing != null) {
            if (existing.password.isNotEmpty() && existing.password != password.trim()) {
                return Pair(false, "Incorrect password. Please enter the correct password.")
            }
            _user.value = existing
            addAuditLog("[AUTH] User ${existing.uid} logged in with mobile ${existing.mobile}")
            return Pair(true, "Login successful! Welcome back, ${existing.name}.")
        }
        // If not found, automatically register new user with password & ₹0.00 initial balance
        val newUid = "INR-" + Random.nextInt(100000, 999999)
        val formatted = if (cleanMobile.startsWith("+91")) cleanMobile else "+91 $cleanMobile"
        val newUser = User(
            uid = newUid,
            name = "Trader " + cleanMobile.takeLast(4),
            mobile = formatted,
            password = password.trim(),
            totalAssets = 0.00,
            availableBalance = 0.00,
            contractAmount = 0.00,
            totalRecharge = 0.00,
            totalWithdrawal = 0.00
        )
        _allUsers.update { listOf(newUser) + it }
        _user.value = newUser
        addAuditLog("[AUTH] New mobile user registered: $formatted with password")
        return Pair(true, "Account created successfully with password! Wallet initialized with ₹0.00.")
    }

    fun registerWithMobile(name: String, mobile: String, password: String): Pair<Boolean, String> {
        val cleanName = name.trim().ifBlank { "User " + mobile.takeLast(4) }
        val cleanMobile = mobile.trim()
        if (cleanMobile.length < 10) {
            return Pair(false, "Please enter a valid 10-digit mobile number")
        }
        if (password.trim().length < 4) {
            return Pair(false, "Password must be at least 4 characters long")
        }
        val newUid = "INR-" + Random.nextInt(100000, 999999)
        val formatted = if (cleanMobile.startsWith("+91")) cleanMobile else "+91 $cleanMobile"
        val newUser = User(
            uid = newUid,
            name = cleanName,
            mobile = formatted,
            password = password.trim(),
            totalAssets = 0.00,
            availableBalance = 0.00,
            contractAmount = 0.00,
            totalRecharge = 0.00,
            totalWithdrawal = 0.00
        )
        _allUsers.update { listOf(newUser) + it }
        _user.value = newUser
        addAuditLog("[AUTH] User $cleanName registered via mobile: $formatted with password")
        return Pair(true, "Registration successful! Wallet initialized with ₹0.00 balance.")
    }

    fun updatePassword(newPass: String): Pair<Boolean, String> {
        if (newPass.trim().length < 4) {
            return Pair(false, "Password must be at least 4 characters long")
        }
        _user.update { it.copy(password = newPass.trim()) }
        _allUsers.update { list ->
            list.map { if (it.uid == _user.value.uid) it.copy(password = newPass.trim()) else it }
        }
        addAuditLog("[AUTH] Password updated for user ${_user.value.uid}")
        return Pair(true, "Password updated successfully!")
    }

    fun adminUpdateTelegramConfig(
        link: String,
        handle: String,
        title: String,
        description: String
    ) {
        _adminConfig.update {
            it.copy(
                telegramChannelLink = link.trim(),
                telegramHandle = if (handle.trim().startsWith("@")) handle.trim() else "@${handle.trim()}",
                telegramTitle = title.trim().ifBlank { it.telegramTitle },
                telegramDescription = description.trim().ifBlank { it.telegramDescription }
            )
        }
        addAuditLog("[ADMIN TELEGRAM] Telegram link updated: $link, Handle: $handle")
    }

    fun logout() {
        // Reset to fresh guest user with ₹0.00 balance
        _user.value = User(
            uid = "INR-" + Random.nextInt(100000, 999999),
            name = "Guest User",
            mobile = "+91 90000 00000",
            totalAssets = 0.00,
            availableBalance = 0.00,
            contractAmount = 0.00,
            totalRecharge = 0.00,
            totalWithdrawal = 0.00
        )
        addAuditLog("[AUTH] User logged out")
    }

    fun adminResetDemoData() {
        _user.value = User(
            totalAssets = 0.00,
            availableBalance = 0.00,
            contractAmount = 0.00,
            totalRecharge = 0.00,
            totalWithdrawal = 0.00
        )
        _transactions.value = emptyList()
        addAuditLog("[ADMIN SYSTEM] Platform data reset to zero balances")
    }

    // 12. Dynamic Features
    fun adminAddDynamicFeature(name: String, description: String, category: String) {
        val id = "F-" + (_dynamicFeatures.value.size + 1)
        val feat = DynamicFeature(id, name, description, true, category)
        _dynamicFeatures.update { it + feat }
        addAuditLog("[ADMIN DYNAMIC] Added new feature: $name ($category)")
    }

    fun adminToggleDynamicFeature(id: String, isEnabled: Boolean) {
        _dynamicFeatures.update { list ->
            list.map { if (it.id == id) it.copy(isEnabled = isEnabled) else it }
        }
        addAuditLog("[ADMIN DYNAMIC] Feature $id toggled to $isEnabled")
    }

    fun adminDeleteDynamicFeature(id: String) {
        _dynamicFeatures.update { it.filterNot { f -> f.id == id } }
        addAuditLog("[ADMIN DYNAMIC] Removed feature $id")
    }

    // 13. Trade Pairs & Markets Admin Control
    fun adminToggleQuoteStatus(symbol: String, isEnabled: Boolean) {
        _quotes.update { list ->
            list.map { q ->
                if (q.symbol.equals(symbol, ignoreCase = true)) q.copy(isEnabled = isEnabled) else q
            }
        }
        addAuditLog("[TRADING] Trade pair $symbol ${if (isEnabled) "enabled" else "suspended"}")
    }

    fun adminUpdateQuotePrice(symbol: String, newPrice: Double, changePercent: Double) {
        _quotes.update { list ->
            list.map { q ->
                if (q.symbol.equals(symbol, ignoreCase = true)) {
                    q.copy(
                        priceInInr = newPrice,
                        change24hPercent = changePercent,
                        high24h = maxOf(q.high24h, newPrice),
                        low24h = minOf(q.low24h, newPrice)
                    )
                } else q
            }
        }
        addAuditLog("[TRADING] Updated price for $symbol to ₹$newPrice ($changePercent%)")
    }

    fun adminAddQuote(symbol: String, name: String, category: MarketCategory, priceInr: Double, changePct: Double) {
        val cleanSymbol = symbol.uppercase().trim()
        val exists = _quotes.value.any { it.symbol.equals(cleanSymbol, ignoreCase = true) }
        if (exists) {
            adminUpdateQuotePrice(cleanSymbol, priceInr, changePct)
            return
        }
        val newQuote = MarketQuote(
            symbol = cleanSymbol,
            name = name.ifBlank { cleanSymbol },
            category = category,
            priceInInr = priceInr,
            change24hPercent = changePct,
            high24h = priceInr * 1.05,
            low24h = priceInr * 0.95,
            volume24hInr = priceInr * 10000.0,
            isFavorite = false,
            sparkline = listOf(priceInr * 0.98, priceInr * 0.99, priceInr * 1.01, priceInr),
            isEnabled = true
        )
        _quotes.update { listOf(newQuote) + it }
        addAuditLog("[TRADING] Added new trade pair $cleanSymbol ($name) at ₹$priceInr")
    }

    fun adminDeleteQuote(symbol: String) {
        _quotes.update { list -> list.filterNot { it.symbol.equals(symbol, ignoreCase = true) } }
        addAuditLog("[TRADING] Deleted trade pair $symbol")
    }

    // 14. Withdrawal Settings & 20% Fee Admin Control
    fun adminUpdateWithdrawalSettings(feePercent: Double, startHour: Int, endHour: Int, enforceHours: Boolean) {
        _adminConfig.update { it.copy(
            withdrawalFeePercent = feePercent,
            withdrawalStartHour = startHour,
            withdrawalEndHour = endHour,
            enforceWithdrawalHours = enforceHours
        )}
        addAuditLog("[WITHDRAWAL CONFIG] Fee: ${feePercent}%, Schedule: ${startHour}:00 - ${endHour}:00 (Enforce: $enforceHours)")
    }

    private fun addAuditLog(log: String) {
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val time = sdf.format(Date())
        _auditLogs.update { listOf("[$time] $log") + it.take(40) }
    }
}

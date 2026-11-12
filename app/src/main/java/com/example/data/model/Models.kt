package com.example.data.model

enum class TransactionType {
    RECHARGE,
    WITHDRAWAL,
    TRADING_PROFIT,
    BONUS_CLAIM,
    REFERRAL_COMMISSION,
    INVESTMENT_DEPOSIT,
    INVESTMENT_RETURN,
    DAILY_SALARY,
    TRANSFER
}

enum class TransactionStatus {
    PENDING,
    SUCCESS,
    FAILED
}

enum class MarketCategory {
    ALL,
    CRYPTO,
    INDICES,
    FOREX,
    COMMODITIES
}

data class User(
    val uid: String = "INR-829147",
    val name: String = "Rohit Sharma",
    val mobile: String = "+91 98765 43210",
    val starRating: Int = 0,        // Star-0
    val vipLevel: Int = 1,          // VIP 1
    val totalAssets: Double = 0.00,
    val availableBalance: Double = 0.00,
    val contractAmount: Double = 0.00,
    val totalRecharge: Double = 0.00,
    val totalWithdrawal: Double = 0.00,
    val tradingAssets: Double = 0.00,
    val bonusAssets: Double = 0.00,
    val accumulatedBonus: Double = 0.00,
    val yesterdayIncome: Double = 0.00,
    val todayIncome: Double = 0.00,
    val profitMarginPercent: Double = 6.00, // Fixed 6.00%
    val referralCode: String = "INR9988",
    val bankAccountLinked: Boolean = true,
    val bankAccountNumber: String = "••••••••6789",
    val bankIfsc: String = "HDFC0001234",
    val bankName: String = "HDFC Bank",
    val upiId: String = "rohit@okhdfcbank",
    val isSecurityPinSet: Boolean = true,
    val status: String = "Active", // "Active" or "Frozen"
    val role: String = "USER",      // "USER" or "ADMIN"
    val dailySalaryClaimedToday: Boolean = false,
    val l1Count: Int = 0,
    val l2Count: Int = 0,
    val l3Count: Int = 0,
    val lastTradeTimestamp: Long = 0L,
    val tradeCountTotal: Int = 0,
    val password: String = "123456"
)

data class ActiveTrade(
    val id: String = "TRD-" + System.currentTimeMillis(),
    val symbol: String,
    val direction: String, // "Buy up" or "Buy down"
    val entryPrice: Double,
    val amountInr: Double,
    val durationSeconds: Int = 60,
    val profitPercent: Double = 6.0,
    val remainingSeconds: Int = 60,
    val isCompleted: Boolean = false,
    val profitEarned: Double = 0.0,
    val startTimestamp: Long = System.currentTimeMillis()
)

data class RechargeReceipt(
    val amount: Double,
    val utr: String,
    val timestamp: Long = System.currentTimeMillis(),
    val newBalance: Double
)


data class Transaction(
    val id: String,
    val type: TransactionType,
    val amountInr: Double,
    val status: TransactionStatus,
    val timestamp: Long,
    val method: String,
    val referenceId: String,
    val remark: String = ""
)

data class MarketQuote(
    val symbol: String,
    val name: String,
    val category: MarketCategory,
    val priceInInr: Double,
    val change24hPercent: Double,
    val high24h: Double,
    val low24h: Double,
    val volume24hInr: Double,
    val isFavorite: Boolean = false,
    val sparkline: List<Double> = emptyList(),
    val isEnabled: Boolean = true
)

data class DayCompoundingRow(
    val day: Int,
    val funds: Double,
    val profit: Double,
    val balance: Double
)

data class InvestmentPlan(
    val id: String,
    val name: String,
    val baseAmountInr: Double,
    val dailyProfitPercent: Double = 6.0,
    val totalDays: Int = 90,
    val projected90DayReturnInr: Double,
    val schedule: List<DayCompoundingRow>
)

data class UserActivePlan(
    val id: String,
    val planId: String,
    val baseAmountInr: Double,
    val currentDay: Int,
    val currentBalanceInr: Double,
    val todayProfitInr: Double,
    val totalProfitsInr: Double,
    val startDate: String,
    val status: String = "ACTIVE" // ACTIVE, COMPLETED
)

data class StarRatingTier(
    val rating: String,      // "Star-0", "Star-1", ...
    val l1Quantity: Int,     // 0, 3, 9, 27, 54, 108, 216, 432
    val dailyProfitPercent: Double // 6.00%, 6.25%, ... 9.00%
)

data class VipSalaryTier(
    val vipLevel: String,    // "VIP1", "VIP2", ... "VIP7"
    val totalTeamRequired: Int, // L1+L2+L3
    val l1Required: Int,     // L1 direct
    val dailySalaryInr: Double // 500, 1000, 2000, 5000, 20000, 100000, 200000
)

data class VipTier(
    val level: Int,
    val title: String,
    val minAssetsRequired: Double,
    val feeDiscountPercent: Int,
    val dailyWithdrawalLimit: Int,
    val dailySignalsCount: Int,
    val perks: List<String>
)

data class SupportTicket(
    val id: String,
    val userUid: String,
    val userName: String,
    val subject: String,
    val category: String,
    val message: String,
    val status: String = "OPEN", // "OPEN", "IN_PROGRESS", "RESOLVED"
    val timestamp: Long,
    val adminReply: String = ""
)

data class ChatMessage(
    val id: String,
    val sender: String, // "USER", "BOT", "AGENT"
    val text: String,
    val timestamp: Long
)

data class TeamMember(
    val uid: String,
    val name: String,
    val tier: Int, // 1 (Direct), 2 (Secondary), 3 (Tertiary)
    val joinedDate: String,
    val tradingVolumeInr: Double,
    val commissionEarnedInr: Double
)

data class DailyBonus(
    val day: Int,
    val amountInr: Double,
    val isClaimed: Boolean,
    val isToday: Boolean
)

data class PromoTask(
    val id: String,
    val title: String,
    val rewardInr: Double,
    val isCompleted: Boolean,
    val isClaimed: Boolean,
    val description: String
)

data class TradingSignal(
    val symbol: String,
    val action: String, // "BUY / LONG" or "SELL / SHORT"
    val entryPriceInr: Double,
    val targetPriceInr: Double,
    val stopLossInr: Double,
    val confidence: String,
    val timestamp: String
)

enum class AppLanguage {
    ENGLISH,
    HINDI
}

data class DailyOfficialSignal(
    val id: String = "SIG-OFFICIAL",
    val symbol: String = "DOGE",
    val pairDisplayName: String = "DOGE/USDT",
    val title: String = "Official Daily Signal: DOGE/USDT",
    val direction: String = "Buy up", // "Buy up" or "Buy down"
    val profitPercent: Double = 6.00,
    val durationSeconds: Int = 60,
    val validFromHour: Int = 15, // 3:00 PM (15:00)
    val validToHour: Int = 21,   // 9:00 PM (21:00)
    val remark: String = "Admin Official Signal: 6.00% Promotional Daily Return",
    val isActive: Boolean = true,
    val publishedDate: String = "Today",
    val totalTradesToday: Int = 648
)

data class AdminAppConfig(
    val tradingProfitPercent: Double = 6.00,
    val tradeDurationSeconds: Int = 60,
    val minTradeAmountInr: Double = 100.0,
    val maxTradeAmountInr: Double = 500000.0,
    val isTradingEnabled: Boolean = true,
    val isRechargeEnabled: Boolean = true,
    val isWithdrawalEnabled: Boolean = true,
    val isReferralEnabled: Boolean = true,
    val isMaintenanceMode: Boolean = false,
    val tradingWindowStartHour: Int = 15, // 3:00 PM
    val tradingWindowEndHour: Int = 21,   // 9:00 PM
    val enforceTradingHours: Boolean = true,
    val enforce24hTradingLimit: Boolean = true,
    val requireSignalMatch: Boolean = true,
    val withdrawalFeePercent: Double = 20.0, // Fixed 20% platform withdrawal fee
    val withdrawalStartHour: Int = 9,       // 9:00 AM daily
    val withdrawalEndHour: Int = 24,        // 12:00 AM midnight daily (09:00 to 24:00)
    val enforceWithdrawalHours: Boolean = true,
    val l1CommissionPercent: Double = 10.0,
    val l2CommissionPercent: Double = 3.0,
    val l3CommissionPercent: Double = 1.0,
    val upiId: String = "inrexchange.pay@hdfcbank",
    val merchantName: String = "INR EXCHANGE",
    val useReferenceScannerImage: Boolean = true,
    val activeQrCodeType: String = "M_LOGO", // "M_LOGO", "UPLOADED_CUSTOM", "DYNAMIC_UPI", "FASTPAY", "CUSTOM_VPA"
    val activeQrTitle: String = "Official Merchant QR (Provided 'M' Logo)",
    val customQrSubtitle: String = "Scan with Any UPI App (GPay, PhonePe, Paytm, BHIM)",
    val customUploadedQrPath: String? = null,
    val customUploadedQrUrl: String? = null,
    val customUploadedQrTimestamp: Long = 0L,
    val tickerAnnouncement: String = "Welcome to INR EXCHANGE: India's Premier Fixed 6% Daily Compounding Platform • 24/7 Instant UPI Settlements",
    val promoBannerTitle: String = "90 Days Compounding Scheme",
    val promoBannerSubtitle: String = "Fixed 6% daily profit with auto wallet compounding",
    val telegramChannelLink: String = "https://t.me/inrexchange_vip",
    val telegramHandle: String = "@INREXCHANGE_VIP",
    val telegramTitle: String = "INR EXCHANGE Official Signals",
    val telegramDescription: String = "Join our Official Telegram Channel for daily 6% signal announcements, market analysis, and VIP alerts.",
    val masterAdminPin: String = "8888"
)

data class QrPresetItem(
    val id: String,
    val title: String,
    val description: String,
    val upiVpa: String,
    val merchantName: String,
    val drawableResName: String?,
    val isDynamic: Boolean
)

data class DynamicFeature(
    val id: String,
    val name: String,
    val description: String,
    val isEnabled: Boolean,
    val category: String = "General"
)



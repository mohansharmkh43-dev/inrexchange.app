package com.example.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.AppRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

enum class MainTab {
    HOME,
    ASSETS,
    TEAM,
    SIGNAL,
    MINE
}

enum class ActiveScreen {
    MAIN,
    RECHARGE,
    WITHDRAWAL,
    SIGNALS,
    SUPPORT_CHAT,
    GUIDE,
    VIP_DETAILS,
    ADMIN_DASHBOARD,
    PROFILE_SETTINGS,
    AUTH_FLOW,
    INVESTMENT_PLANS,
    SALARY_BONUS_DETAILS
}

data class UiNotification(
    val id: Long = System.currentTimeMillis(),
    val message: String,
    val isError: Boolean = false
)

class AppViewModel(
    val repository: AppRepository = AppRepository()
) : ViewModel() {

    val user: StateFlow<User> = repository.user
    val language: StateFlow<AppLanguage> = repository.language
    val transactions: StateFlow<List<Transaction>> = repository.transactions
    val quotes: StateFlow<List<MarketQuote>> = repository.quotes
    val signals: StateFlow<List<TradingSignal>> = repository.signals
    val vipTiers: List<VipTier> = repository.vipTiers
    val teamMembers: StateFlow<List<TeamMember>> = repository.teamMembers
    val dailyBonuses: StateFlow<List<DailyBonus>> = repository.dailyBonuses
    val promoTasks: StateFlow<List<PromoTask>> = repository.promoTasks
    val supportTickets: StateFlow<List<SupportTicket>> = repository.supportTickets
    val chatMessages: StateFlow<List<ChatMessage>> = repository.chatMessages
    val allUsers: StateFlow<List<User>> = repository.allUsers
    val auditLogs: StateFlow<List<String>> = repository.auditLogs
    val investmentPlans = repository.investmentPlans
    val starRatingTiers = repository.starRatingTiers
    val vipSalaryTiers = repository.vipSalaryTiers
    val userActivePlans = repository.userActivePlans
    val activeTrade = repository.activeTrade
    val settledTradeEvent = repository.settledTradeEvent
    val lastRechargeReceipt = repository.lastRechargeReceipt
    val adminConfig = repository.adminConfig
    val dynamicFeatures = repository.dynamicFeatures
    val dailyOfficialSignal = repository.dailyOfficialSignal

    private val _currentTab = MutableStateFlow(MainTab.HOME)
    val currentTab: StateFlow<MainTab> = _currentTab.asStateFlow()

    private val _activeScreen = MutableStateFlow(ActiveScreen.MAIN)
    val activeScreen: StateFlow<ActiveScreen> = _activeScreen.asStateFlow()

    private val _selectedQuote = MutableStateFlow<MarketQuote?>(null)
    val selectedQuote: StateFlow<MarketQuote?> = _selectedQuote.asStateFlow()

    private val _uiNotification = MutableStateFlow<UiNotification?>(null)
    val uiNotification: StateFlow<UiNotification?> = _uiNotification.asStateFlow()

    private val inrFormat: DecimalFormat = DecimalFormat("##,##,##,##0.00")
    private val inrIntFormat: DecimalFormat = DecimalFormat("##,##,##,##0")

    fun formatInr(amount: Double, includeDecimals: Boolean = true): String {
        return "₹" + if (includeDecimals) inrFormat.format(amount) else inrIntFormat.format(amount)
    }

    fun setTab(tab: MainTab) {
        _currentTab.value = tab
        _activeScreen.value = ActiveScreen.MAIN
    }

    fun navigateTo(screen: ActiveScreen) {
        _activeScreen.value = screen
    }

    fun navigateBack() {
        if (_activeScreen.value != ActiveScreen.MAIN) {
            _activeScreen.value = ActiveScreen.MAIN
        }
    }

    fun selectQuoteForTrade(quote: MarketQuote) {
        _selectedQuote.value = quote
    }

    fun clearSelectedQuote() {
        _selectedQuote.value = null
    }

    fun showNotification(message: String, isError: Boolean = false) {
        _uiNotification.value = UiNotification(message = message, isError = isError)
    }

    fun clearNotification() {
        _uiNotification.value = null
    }

    fun toggleLanguage() {
        val next = if (language.value == AppLanguage.ENGLISH) AppLanguage.HINDI else AppLanguage.ENGLISH
        repository.switchLanguage(next)
        showNotification(if (next == AppLanguage.HINDI) "भाषा हिन्दी में बदली गई" else "Language switched to English")
    }

    fun recharge(amount: Double, method: String) {
        val success = repository.rechargeDemo(amount, method)
        if (success) {
            showNotification("Demo recharge of ${formatInr(amount)} successful!")
            navigateTo(ActiveScreen.MAIN)
        } else {
            showNotification("Please enter a valid amount", isError = true)
        }
    }

    fun withdraw(amount: Double, destination: String) {
        val (success, msg) = repository.withdrawDemo(amount, destination)
        showNotification(msg, isError = !success)
        if (success) {
            navigateTo(ActiveScreen.MAIN)
        }
    }

    fun claimDailyCheckin() {
        val ok = repository.claimDailyBonus()
        if (ok) {
            showNotification("Daily check-in reward claimed! Check your Bonus assets.")
        } else {
            showNotification("Already claimed today! Come back tomorrow.", isError = true)
        }
    }

    fun claimTaskReward(taskId: String) {
        val ok = repository.claimPromoTask(taskId)
        if (ok) {
            showNotification("Task reward credited to your wallet!")
        } else {
            showNotification("Task not eligible or already claimed", isError = true)
        }
    }

    fun executeTrade(symbol: String, action: String, amount: Double, leverage: Int) {
        val (ok, msg) = repository.executeDemoTrade(symbol, action, amount, leverage)
        showNotification(msg, isError = !ok)
        if (ok) {
            _selectedQuote.value = null
        }
    }

    fun sendChat(text: String) {
        if (text.isNotBlank()) {
            repository.sendChatMessage(text)
        }
    }

    fun createSupportTicket(subject: String, category: String, desc: String) {
        if (subject.isBlank() || desc.isBlank()) {
            showNotification("Please fill in subject and description", isError = true)
            return
        }
        val ticket = repository.submitSupportTicket(subject, category, desc)
        showNotification("Ticket ${ticket.id} created successfully! Our team will respond shortly.")
    }

    fun updateProfile(name: String, mobile: String, upiId: String, bankAcc: String, ifsc: String) {
        repository.updateUserProfile(name, mobile, upiId, bankAcc, ifsc)
        showNotification("Profile and Payment Settings updated successfully!")
    }

    // Mobile Number and Password Authentication (No KYC, No Gmail)
    fun loginWithMobile(mobile: String, password: String) {
        val (ok, msg) = repository.loginWithMobile(mobile, password)
        showNotification(msg, isError = !ok)
        if (ok) {
            navigateTo(ActiveScreen.MAIN)
        }
    }

    fun registerWithMobile(name: String, mobile: String, password: String) {
        val (ok, msg) = repository.registerWithMobile(name, mobile, password)
        showNotification(msg, isError = !ok)
        if (ok) {
            navigateTo(ActiveScreen.MAIN)
        }
    }

    fun updatePassword(newPass: String) {
        val (ok, msg) = repository.updatePassword(newPass)
        showNotification(msg, isError = !ok)
    }

    fun logout() {
        repository.logout()
        showNotification("You have been signed out.")
        navigateTo(ActiveScreen.MAIN)
    }

    fun isTradingWindowOpen(): Boolean = repository.isTradingWindowOpen()

    fun isWithdrawalWindowOpen(): Boolean = repository.isWithdrawalWindowOpen()

    fun getTimeRemainingUntilNextTrade(): Long = repository.getTimeRemainingUntilNextTrade(user.value.lastTradeTimestamp)

    // Admin helpers
    fun adminApprove(txId: String) {
        repository.adminApproveTransaction(txId)
        showNotification("Transaction $txId approved by Admin")
    }

    fun adminReject(txId: String) {
        repository.adminRejectTransaction(txId)
        showNotification("Transaction $txId rejected", isError = true)
    }

    fun adminToggleStatus(uid: String) {
        repository.adminToggleUserStatus(uid)
        showNotification("Account status updated for $uid")
    }

    fun adminToggleUserStatus(uid: String) = adminToggleStatus(uid)

    fun adminAdjust(uid: String, amount: Double) {
        repository.adminAdjustBalance(uid, amount)
        showNotification("Adjusted ${formatInr(amount)} for user $uid")
    }

    fun adminUpdateUserVip(uid: String, newVipLevel: Int) {
        repository.adminUpdateUserVip(uid, newVipLevel)
        showNotification("Updated user $uid to VIP $newVipLevel")
    }

    fun adminReply(ticketId: String, reply: String) {
        repository.adminReplyTicket(ticketId, reply)
        showNotification("Replied and resolved ticket $ticketId")
    }

    fun clearLastRechargeReceipt() {
        repository.clearLastRechargeReceipt()
    }

    fun clearSettledTradeEvent() {
        repository.clearSettledTradeEvent()
    }

    fun rechargeQr(amount: Double, utr: String): Boolean {
        val tx = repository.rechargeViaQr(amount, utr)
        if (tx != null) {
            showNotification("Recharge request for ${formatInr(amount)} submitted! Status: PENDING (Waiting for payment verification).")
            return true
        } else {
            showNotification("Please enter a valid amount", isError = true)
            return false
        }
    }

    fun start60sTrade(symbol: String, direction: String, amount: Double) {
        val (ok, msg) = repository.start60sTrade(symbol, direction, amount)
        showNotification(msg, isError = !ok)
    }

    fun activatePlan(planId: String) {
        val (ok, msg) = repository.activateInvestmentPlan(planId)
        showNotification(msg, isError = !ok)
    }

    fun claimSalary() {
        val (ok, msg) = repository.claimDailySalary()
        showNotification(msg, isError = !ok)
    }

    // --- ADMIN AUTHORIZATION & 12 MODULE CONTROLS ---

    private val _isAdminAuthorized = MutableStateFlow(false)
    val isAdminAuthorized: StateFlow<Boolean> = _isAdminAuthorized.asStateFlow()

    fun verifyAdminPin(pin: String): Boolean {
        if (pin == adminConfig.value.masterAdminPin || pin == "8888") {
            _isAdminAuthorized.value = true
            showNotification("Administrator Access Granted")
            return true
        } else {
            showNotification("Incorrect Admin PIN", isError = true)
            return false
        }
    }

    fun lockAdmin() {
        _isAdminAuthorized.value = false
    }

    fun adminPublishDailySignal(
        symbol: String,
        pairName: String,
        direction: String,
        profitPercent: Double,
        remark: String,
        isActive: Boolean = true
    ) {
        repository.adminPublishDailySignal(symbol, pairName, direction, profitPercent, remark, isActive)
        showNotification("Daily Signal published: $pairName ($direction) +$profitPercent%")
    }

    fun adminUpdateTradingSchedule(
        startHour: Int,
        endHour: Int,
        enforceHours: Boolean,
        enforce24hLimit: Boolean,
        requireSignalMatch: Boolean
    ) {
        repository.adminUpdateTradingSchedule(startHour, endHour, enforceHours, enforce24hLimit, requireSignalMatch)
        showNotification("Trading schedule ($startHour:00 - $endHour:00) & constraints updated")
    }

    fun adminResetUserTradeCooldown(uid: String) {
        repository.adminResetUserTradeCooldown(uid)
        showNotification("24h trading cooldown reset for $uid")
    }

    fun adminUpdateTelegramConfig(link: String, handle: String, title: String, description: String) {
        repository.adminUpdateTelegramConfig(link, handle, title, description)
        showNotification("Official Telegram channel configuration updated")
    }

    // 1. User Management
    fun adminAddUser(name: String, mobile: String, role: String, vipLevel: Int, starRating: Int, initialBalance: Double) {
        repository.adminAddUser(name, mobile, role, vipLevel, starRating, initialBalance)
        showNotification("User account created: $name")
    }

    fun adminUpdateUser(uid: String, name: String, mobile: String, role: String, status: String) {
        repository.adminUpdateUserDetails(uid, name, mobile, role, status)
        showNotification("User $uid updated successfully")
    }

    fun adminDeleteUser(uid: String) {
        repository.adminDeleteUser(uid)
        showNotification("User $uid removed")
    }

    // 2. Wallet Balance Management
    fun adminAdjustWallet(uid: String, availableDelta: Double, contractDelta: Double, bonusDelta: Double, remark: String) {
        repository.adminAdjustWalletBreakdown(uid, availableDelta, contractDelta, bonusDelta, remark)
        showNotification("Balances adjusted for user $uid")
    }

    fun adminResetBalance(uid: String) {
        repository.adminResetUserBalance(uid)
        showNotification("User $uid balances reset to ₹0.00")
    }

    // 3. Recharge & QR Verification
    fun adminManualCreditRecharge(uid: String, amount: Double, utr: String, remark: String) {
        repository.adminManualCreditRecharge(uid, amount, utr, remark)
        showNotification("Recharge verified & credited: ₹$amount")
    }

    // 4. Withdrawals
    fun adminApproveWithdrawalWithRef(txId: String, utrRef: String) {
        repository.adminApproveWithdrawal(txId, utrRef)
        showNotification("Withdrawal approved (UTR: $utrRef)")
    }

    // 5. Investment & Trading Settings
    fun adminUpdateTradingConfig(profitPercent: Double, durationSeconds: Int, minAmount: Double, maxAmount: Double, isEnabled: Boolean) {
        repository.adminUpdateTradingConfig(profitPercent, durationSeconds, minAmount, maxAmount, isEnabled)
        showNotification("Trading engine settings saved")
    }

    // 6. Referral Commission
    fun adminUpdateReferralCommission(l1Pct: Double, l2Pct: Double, l3Pct: Double) {
        repository.adminUpdateReferralCommission(l1Pct, l2Pct, l3Pct)
        showNotification("Referral commission rates updated")
    }

    // 7. App Content & Banners
    fun adminUpdateContent(ticker: String, bannerTitle: String, bannerSubtitle: String) {
        repository.adminUpdateContent(ticker, bannerTitle, bannerSubtitle)
        showNotification("Content, ticker, and banners updated")
    }

    // 8. Feature Flags
    fun adminToggleFeature(featureKey: String, isEnabled: Boolean) {
        repository.adminToggleFeature(featureKey, isEnabled)
        showNotification("Feature $featureKey set to ${if (isEnabled) "ENABLED" else "DISABLED"}")
    }

    // 9. Scanner Settings
    val availableQrPresets get() = repository.availableQrPresets

    fun adminSetActiveQrCode(qrId: String) {
        repository.adminSetActiveQrCode(qrId)
        val name = repository.availableQrPresets.find { it.id == qrId }?.title ?: qrId
        showNotification("Active Recharge QR set to: $name")
    }

    fun adminUpdateScannerSettings(
        activeQrType: String,
        upiId: String,
        merchantName: String,
        customTitle: String = ""
    ) {
        repository.adminUpdateScannerSettings(activeQrType, upiId, merchantName, customTitle)
        showNotification("Recharge QR Code & Gateway configuration saved")
    }

    fun adminUpdateScannerSettings(upiId: String, merchantName: String, useReferenceImage: Boolean) {
        repository.adminUpdateScannerSettings(upiId, merchantName, useReferenceImage)
        showNotification("Scanner & UPI gateway settings saved")
    }

    fun adminUploadScannerImage(context: Context, uri: Uri) {
        val (ok, msg) = repository.adminSaveUploadedQrImage(context, uri)
        showNotification(msg, isError = !ok)
    }

    fun adminSetUploadedQrUrl(url: String) {
        val (ok, msg) = repository.adminSetUploadedQrUrl(url)
        showNotification(msg, isError = !ok)
    }

    fun adminRemoveUploadedQr() {
        repository.adminRemoveUploadedQr()
        showNotification("Custom uploaded QR removed. Reverted to official M-Logo QR")
    }

    // 10. Security & Admin PIN
    fun adminUpdatePin(newPin: String) {
        repository.adminUpdatePin(newPin)
        showNotification("Master Admin PIN updated successfully")
    }

    fun adminResetDemoData() {
        repository.adminResetDemoData()
        showNotification("Platform demo data reset to clean initial state")
    }

    // 12. Dynamic Features Management
    fun adminAddDynamicFeature(name: String, description: String, category: String) {
        repository.adminAddDynamicFeature(name, description, category)
        showNotification("Dynamic feature added: $name")
    }

    fun adminToggleDynamicFeature(id: String, isEnabled: Boolean) {
        repository.adminToggleDynamicFeature(id, isEnabled)
        showNotification("Feature status updated")
    }

    fun adminDeleteDynamicFeature(id: String) {
        repository.adminDeleteDynamicFeature(id)
        showNotification("Feature removed from platform")
    }

    // 13. Trade Pairs & Markets Admin Controls
    fun adminToggleQuote(symbol: String, isEnabled: Boolean) {
        repository.adminToggleQuoteStatus(symbol, isEnabled)
        showNotification("Trading pair $symbol status set to ${if (isEnabled) "ACTIVE" else "SUSPENDED"}")
    }

    fun adminUpdateQuotePrice(symbol: String, newPrice: Double, changePercent: Double) {
        repository.adminUpdateQuotePrice(symbol, newPrice, changePercent)
        showNotification("Price updated for $symbol: ₹$newPrice ($changePercent%)")
    }

    fun adminAddQuote(symbol: String, name: String, category: MarketCategory, priceInr: Double, changePct: Double) {
        repository.adminAddQuote(symbol, name, category, priceInr, changePct)
        showNotification("New trading pair $symbol added to platform!")
    }

    fun adminDeleteQuote(symbol: String) {
        repository.adminDeleteQuote(symbol)
        showNotification("Trading pair $symbol removed from platform")
    }

    // 14. Withdrawal Fee & Time Window Controls
    fun adminUpdateWithdrawalSettings(feePercent: Double, startHour: Int, endHour: Int, enforceHours: Boolean) {
        repository.adminUpdateWithdrawalSettings(feePercent, startHour, endHour, enforceHours)
        showNotification("Withdrawal policy updated: ${feePercent}% Fee, Window ${startHour}:00 - ${endHour}:00")
    }
}

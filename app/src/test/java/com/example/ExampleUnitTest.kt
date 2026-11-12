package com.example

import com.example.data.model.AppLanguage
import com.example.data.model.AppStrings
import com.example.data.repository.AppRepository
import com.example.ui.viewmodel.AppViewModel
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ExampleUnitTest {

    private lateinit var repository: AppRepository
    private lateinit var viewModel: AppViewModel

    @Before
    fun setup() {
        repository = AppRepository()
        viewModel = AppViewModel(repository)
    }

    @Test
    fun testInitialZeroBalanceForNewUser() {
        val user = repository.user.value
        assertEquals(0.00, user.availableBalance, 0.001)
        assertEquals(0.00, user.totalAssets, 0.001)
        assertEquals(0.00, user.contractAmount, 0.001)
        assertEquals(0.00, user.totalRecharge, 0.001)
        assertEquals(0.00, user.totalWithdrawal, 0.001)
        assertEquals(0.00, user.tradingAssets, 0.001)
        assertEquals(0.00, user.todayIncome, 0.001)
        assertEquals(0.00, user.yesterdayIncome, 0.001)
        assertTrue(repository.transactions.value.isEmpty())
    }

    @Test
    fun testCurrencyFormatting() {
        val formatted = viewModel.formatInr(125840.50)
        assertTrue(formatted.startsWith("₹"))
        assertTrue(formatted.contains("1,25,840.50") || formatted.contains("125,840.50"))
    }

    @Test
    fun testQrRechargeAndExactWalletCredit() {
        assertEquals(0.00, repository.user.value.availableBalance, 0.001)
        val depositAmount = 5000.0
        val receipt = repository.rechargeViaQr(depositAmount, "430198274619")

        assertNotNull(receipt)
        assertEquals(depositAmount, receipt?.amount ?: 0.0, 0.001)
        assertEquals(depositAmount, repository.user.value.availableBalance, 0.001)
        assertEquals(depositAmount, repository.user.value.totalAssets, 0.001)
        assertEquals(depositAmount, repository.user.value.totalRecharge, 0.001)
        assertEquals(1, repository.transactions.value.size)
    }

    @Test
    fun testFixed6Percent60sTradeOrderPlacement() {
        // Recharge wallet first
        repository.rechargeViaQr(10000.0, "UTR123456789")
        assertEquals(10000.0, repository.user.value.availableBalance, 0.001)

        val tradeAmount = 2000.0
        val (success, msg) = repository.start60sTrade("BTC", "Buy up", tradeAmount)

        assertTrue(success)
        assertTrue(msg.contains("60-Second"))
        assertEquals(8000.0, repository.user.value.availableBalance, 0.001)
        assertNotNull(repository.activeTrade.value)
        assertEquals(60, repository.activeTrade.value?.durationSeconds)
        assertEquals(6.0, repository.activeTrade.value?.profitPercent ?: 0.0, 0.01)
    }

    @Test
    fun testWithdrawalValidationAfterRecharge() {
        // Initially zero balance -> cannot withdraw
        val (failInitial, _) = repository.withdrawDemo(500.0, "UPI")
        assertFalse(failInitial)

        // Recharge ₹5000
        repository.rechargeViaQr(5000.0, "UTR9988776655")
        assertEquals(5000.0, repository.user.value.availableBalance, 0.001)

        // Attempting to withdraw less than ₹500
        val (minResult, minMsg) = repository.withdrawDemo(200.0, "Bank Account")
        assertFalse(minResult)
        assertTrue(minMsg.contains("Minimum"))

        // Valid withdrawal
        val (validResult, _) = repository.withdrawDemo(1000.0, "UPI")
        assertTrue(validResult)
        assertEquals(4000.0, repository.user.value.availableBalance, 0.01)
    }

    @Test
    fun testBilingualStrings() {
        val enHome = AppStrings.get("home", AppLanguage.ENGLISH)
        val hiHome = AppStrings.get("home", AppLanguage.HINDI)

        assertEquals("Home", enHome)
        assertEquals("होम", hiHome)

        val enRecharge = AppStrings.get("recharge", AppLanguage.ENGLISH)
        val hiRecharge = AppStrings.get("recharge", AppLanguage.HINDI)
        assertEquals("Recharge", enRecharge)
        assertTrue(hiRecharge.contains("रिचार्ज"))
    }
}

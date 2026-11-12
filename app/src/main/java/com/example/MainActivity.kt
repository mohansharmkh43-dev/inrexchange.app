package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.InrBottomNavigation
import com.example.ui.screens.*
import com.example.ui.theme.BgDark
import com.example.ui.theme.GainGreen
import com.example.ui.theme.LossRed
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.ActiveScreen
import com.example.ui.viewmodel.AppViewModel
import com.example.ui.viewmodel.MainTab

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                InrExchangeApp()
            }
        }
    }
}

@Composable
fun InrExchangeApp(
    viewModel: AppViewModel = viewModel()
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val activeScreen by viewModel.activeScreen.collectAsState()
    val language by viewModel.language.collectAsState()
    val selectedQuote by viewModel.selectedQuote.collectAsState()
    val notification by viewModel.uiNotification.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(notification) {
        notification?.let {
            snackbarHostState.showSnackbar(
                message = it.message,
                duration = SnackbarDuration.Short
            )
            viewModel.clearNotification()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .windowInsetsPadding(WindowInsets.statusBars),
        containerColor = BgDark,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                val isError = notification?.isError == true
                Snackbar(
                    containerColor = if (isError) LossRed else GainGreen,
                    contentColor = if (isError) Color.White else Color.Black,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(text = data.visuals.message)
                }
            }
        },
        bottomBar = {
            if (activeScreen == ActiveScreen.MAIN) {
                InrBottomNavigation(
                    currentTab = currentTab,
                    onTabSelected = { viewModel.setTab(it) },
                    language = language
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = activeScreen,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "ScreenTransition"
            ) { screen ->
                when (screen) {
                    ActiveScreen.MAIN -> {
                        when (currentTab) {
                            MainTab.HOME -> HomeScreen(viewModel = viewModel)
                            MainTab.ASSETS -> AssetsScreen(viewModel = viewModel)
                            MainTab.TEAM -> TeamScreen(viewModel = viewModel)
                            MainTab.SIGNAL -> SignalsScreen(viewModel = viewModel)
                            MainTab.MINE -> AssetsScreen(viewModel = viewModel)
                        }
                    }
                    ActiveScreen.RECHARGE -> RechargeScreen(viewModel = viewModel)
                    ActiveScreen.WITHDRAWAL -> WithdrawalScreen(viewModel = viewModel)
                    ActiveScreen.SIGNALS -> SignalsScreen(viewModel = viewModel)
                    ActiveScreen.SUPPORT_CHAT -> SupportScreen(viewModel = viewModel)
                    ActiveScreen.GUIDE -> GuideScreen(viewModel = viewModel)
                    ActiveScreen.VIP_DETAILS -> VIPScreen(viewModel = viewModel)
                    ActiveScreen.ADMIN_DASHBOARD -> AdminScreen(viewModel = viewModel)
                    ActiveScreen.PROFILE_SETTINGS -> ProfileSettingsScreen(viewModel = viewModel)
                    ActiveScreen.AUTH_FLOW -> HomeScreen(viewModel = viewModel)
                    ActiveScreen.INVESTMENT_PLANS -> InvestmentPlansScreen(viewModel = viewModel)
                    ActiveScreen.SALARY_BONUS_DETAILS -> SalaryBonusScreen(viewModel = viewModel)
                }
            }

            // Trade Bottom Sheet
            selectedQuote?.let { quote ->
                TradeDetailDialog(
                    quote = quote,
                    viewModel = viewModel,
                    onDismiss = { viewModel.clearSelectedQuote() }
                )
            }
        }
    }
}

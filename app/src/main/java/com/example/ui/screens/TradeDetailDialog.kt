package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.model.ActiveTrade
import com.example.data.model.MarketQuote
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.ActiveScreen
import com.example.ui.viewmodel.AppViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TradeDetailDialog(
    quote: MarketQuote,
    viewModel: AppViewModel,
    onDismiss: () -> Unit
) {
    val user by viewModel.user.collectAsState()
    val activeTrade by viewModel.activeTrade.collectAsState()
    val settledTrade by viewModel.settledTradeEvent.collectAsState()
    val officialSignal by viewModel.dailyOfficialSignal.collectAsState()
    val adminConfig by viewModel.adminConfig.collectAsState()

    val isWindowOpen = viewModel.isTradingWindowOpen()
    val cooldownRemaining = viewModel.getTimeRemainingUntilNextTrade()
    val isSignalMatch = quote.symbol.equals(officialSignal.symbol, ignoreCase = true) ||
            quote.symbol.equals(officialSignal.pairDisplayName, ignoreCase = true) ||
            officialSignal.pairDisplayName.contains(quote.symbol, ignoreCase = true)
    val canTrade = isWindowOpen && cooldownRemaining <= 0L && (!adminConfig.requireSignalMatch || isSignalMatch)

    val tradeDirection = officialSignal.direction
    var amountInput by remember { mutableStateOf("") }
    var selectedTimeframe by remember { mutableStateOf("1m") }

    // Synchronize available wallet balance automatically into the custom amount field
    LaunchedEffect(user.availableBalance) {
        amountInput = if (user.availableBalance > 0) {
            if (user.availableBalance % 1.0 == 0.0) {
                String.format(Locale.US, "%.0f", user.availableBalance)
            } else {
                String.format(Locale.US, "%.2f", user.availableBalance)
            }
        } else {
            "0"
        }
    }

    val parsedAmount = amountInput.toDoubleOrNull() ?: 0.0
    val expectedProfit = parsedAmount * (adminConfig.tradingProfitPercent / 100.0)
    val expectedTotalReturn = parsedAmount + expectedProfit

    // If an active trade is in progress or completed, show the live status dialog
    settledTrade?.let { trade ->
        TradeSettledDialog(
            trade = trade,
            viewModel = viewModel,
            onDismiss = {
                viewModel.clearSettledTradeEvent()
            }
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0F1322),
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color(0xFF2E3650)) },
        shape = RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header: Quick Margin & Symbol
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${quote.symbol}/USDT",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (quote.change24hPercent >= 0) Color(0x3000E676) else Color(0x30FF3366)
                            ) {
                                Text(
                                    text = String.format(Locale.getDefault(), "%.2f%%", quote.change24hPercent),
                                    color = if (quote.change24hPercent >= 0) GainGreen else LossRed,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Quick Margin • 60S Fixed 6% Contract",
                            color = AccentCyan,
                            fontSize = 11.sp
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Price and 24h Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = String.format(Locale.getDefault(), "%,.2f", quote.priceInInr),
                            color = if (quote.change24hPercent >= 0) GainGreen else LossRed,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Low: ${String.format(Locale.getDefault(), "%,.2f", quote.low24h)}",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "High: ${String.format(Locale.getDefault(), "%,.2f", quote.high24h)}",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Timeframe Selectors
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("1m", "5m", "15m", "30m", "1h", "4h", "1D").forEach { tf ->
                        val isSel = tf == selectedTimeframe
                        Text(
                            text = tf,
                            color = if (isSel) Color.White else Color(0xFF6B7280),
                            fontSize = 12.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { selectedTimeframe = tf }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Candlestick Simulation Chart
                CandlestickChartBox(quote = quote)

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Daily Trading Schedule & Signal Notification Banners
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                    // Signal Badge
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSignalMatch) Color(0xFF102820) else Color(0xFF261D15),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSignalMatch) GainGreen.copy(alpha = 0.5f) else Color(0xFFFF9800).copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isSignalMatch) Icons.Default.CheckCircle else Icons.Default.Info,
                                contentDescription = null,
                                tint = if (isSignalMatch) GainGreen else Color(0xFFFFB74D),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (isSignalMatch) "Official Daily Signal: ${officialSignal.pairDisplayName} (${officialSignal.direction})" else "Official Signal: ${officialSignal.pairDisplayName} (${officialSignal.direction})",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isSignalMatch) "This pair qualifies for 6.00% daily promotional reward." else "Only trades placed on the official signal qualify for the 6.00% daily reward.",
                                    color = if (isSignalMatch) GainGreen else Color(0xFFFFB74D),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Window & 24h Quota info
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            color = if (isWindowOpen) Color(0xFF152238) else Color(0xFF2B161B),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isWindowOpen) AccentCyan.copy(alpha = 0.4f) else LossRed.copy(alpha = 0.4f))
                        ) {
                            Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = if (isWindowOpen) AccentCyan else LossRed,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isWindowOpen) "Hours: 3PM - 9PM (Open)" else "Trading Closed (3PM-9PM)",
                                    color = if (isWindowOpen) AccentCyan else LossRed,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            color = if (cooldownRemaining <= 0L) Color(0xFF152238) else Color(0xFF2B161B),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (cooldownRemaining <= 0L) GainGreen.copy(alpha = 0.4f) else LossRed.copy(alpha = 0.4f))
                        ) {
                            Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LockClock,
                                    contentDescription = null,
                                    tint = if (cooldownRemaining <= 0L) GainGreen else Color(0xFFFFB74D),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                val cdHours = cooldownRemaining / (3600 * 1000L)
                                val cdMins = (cooldownRemaining % (3600 * 1000L)) / (60 * 1000L)
                                Text(
                                    text = if (cooldownRemaining <= 0L) "Quota: 1/24h (Ready)" else "Wait: ${cdHours}h ${cdMins}m",
                                    color = if (cooldownRemaining <= 0L) GainGreen else Color(0xFFFFB74D),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Active Trade Running View (If a 60S trade is currently counting down)
            if (activeTrade != null) {
                item {
                    val trade = activeTrade!!
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp)),
                        color = Color(0xFF1B1A30),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Brush.horizontalGradient(listOf(AccentPink, AccentCyan)))
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "60-SECOND TRADE IN PROGRESS",
                                color = AccentCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            // Circular Timer & Remaining Seconds
                            Box(
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF0D0F1A))
                                    .border(3.dp, AccentPink, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${trade.remainingSeconds}s",
                                        color = Color.White,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    Text(text = "60S DURATION", color = TextMuted, fontSize = 8.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            LinearProgressIndicator(
                                progress = { (60 - trade.remainingSeconds) / 60f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = AccentPink,
                                trackColor = Color(0xFF282845),
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Direction: ${trade.direction}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text(text = "Amount: ${viewModel.formatInr(trade.amountInr)}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Fixed Profit Rate: +6.00%", color = GainGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    text = "Expected Profit: +${viewModel.formatInr(trade.amountInr * 0.06)}",
                                    color = GainGreen,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }



            // Selection Period (Exact Coral Card: 60 S / 6% matching screenshot)
            item {
                Text(
                    text = "Selection Period",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFFA3E5C), // Vibrant coral pink from screenshot
                    modifier = Modifier
                        .width(100.dp)
                        .clip(RoundedCornerShape(14.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "60 S",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "6%",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Custom amount (Matching Screenshot)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Custom amount",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF1E2845),
                        modifier = Modifier.clickable {
                            amountInput = if (user.availableBalance > 0) {
                                if (user.availableBalance % 1.0 == 0.0) {
                                    String.format(Locale.US, "%.0f", user.availableBalance)
                                } else {
                                    String.format(Locale.US, "%.2f", user.availableBalance)
                                }
                            } else {
                                "0"
                            }
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = "Sync Balance",
                                tint = AccentCyan,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Auto Sync (100%)",
                                color = AccentCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = amountInput,
                    onValueChange = { input ->
                        val filtered = input.filter { it.isDigit() || it == '.' }
                        amountInput = filtered
                    },
                    placeholder = { Text("Please enter amount", color = Color(0xFF555D75)) },
                    suffix = { Text("INR", color = Color.White, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF131726),
                        unfocusedContainerColor = Color(0xFF131726),
                        focusedBorderColor = Color(0xFF3861FB),
                        unfocusedBorderColor = Color(0xFF232A40),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Available & Investing display (Matching screenshot line: Available: 0 INR  Investing: 0 INR)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Available: ${viewModel.formatInr(user.availableBalance)}",
                        color = Color(0xFF8E9BAE),
                        fontSize = 12.sp
                    )
                    Text(
                        text = "Investing: ${if (parsedAmount > 0) viewModel.formatInr(parsedAmount) else "0 INR"}",
                        color = Color(0xFF8E9BAE),
                        fontSize = 12.sp
                    )
                }

                // If balance is zero, show Recharge Quick CTA
                if (user.availableBalance <= 0) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                onDismiss()
                                viewModel.navigateTo(ActiveScreen.RECHARGE)
                            },
                        color = Color(0xFF261D15),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x60FF9800))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Wallet balance is ₹0.00. Recharge via QR code to trade.",
                                color = Color(0xFFFFB74D),
                                fontSize = 11.sp
                            )
                            Text(
                                text = "RECHARGE",
                                color = AccentPink,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))
            }

            // Summary Row: Symbol | Type | Price | Money | Expected (Matching Screenshot)
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF131726)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Symbol", color = Color(0xFF6B7280), fontSize = 10.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = "${quote.symbol}/USDT", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text(text = "Type", color = Color(0xFF6B7280), fontSize = 10.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Scanner 60S",
                                color = AccentCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column {
                            Text(text = "Price", color = Color(0xFF6B7280), fontSize = 10.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = String.format(Locale.getDefault(), "%,.2f", quote.priceInInr),
                                color = LossRed,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column {
                            Text(text = "Money", color = Color(0xFF6B7280), fontSize = 10.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (parsedAmount > 0) "${parsedAmount.toInt()} INR" else "0 INR",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Expected (+6%)", color = Color(0xFF6B7280), fontSize = 10.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (parsedAmount > 0) String.format(Locale.getDefault(), "%.2f INR", expectedTotalReturn) else "0.00 INR",
                                color = GainGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Blue Button: "Confirm order" (Matching Screenshot)
            item {
                val buttonText = when {
                    activeTrade != null -> "Trade In Progress (${activeTrade!!.remainingSeconds}s)"
                    !isWindowOpen -> "Trading Closed (3:00 PM - 9:00 PM)"
                    cooldownRemaining > 0 -> {
                        val h = cooldownRemaining / (3600 * 1000L)
                        val m = (cooldownRemaining % (3600 * 1000L)) / (60 * 1000L)
                        "1 Trade Per 24h (Next in ${h}h ${m}m)"
                    }
                    adminConfig.requireSignalMatch && !isSignalMatch -> "Trade on ${officialSignal.pairDisplayName} for 6% Reward"
                    user.availableBalance <= 0 -> "Wallet Empty • Recharge via QR"
                    parsedAmount <= 0 -> "Enter Amount"
                    parsedAmount > user.availableBalance -> "Insufficient Balance"
                    else -> "Confirm 60S Order (+${adminConfig.tradingProfitPercent}% Reward)"
                }

                Button(
                    onClick = {
                        viewModel.start60sTrade(
                            symbol = quote.symbol,
                            direction = tradeDirection,
                            amount = parsedAmount
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2563EB),
                        disabledContainerColor = Color(0xFF1E2640)
                    ),
                    enabled = canTrade && parsedAmount > 0 && parsedAmount <= user.availableBalance && activeTrade == null
                ) {
                    Text(
                        text = buttonText,
                        color = if (canTrade && parsedAmount > 0 && parsedAmount <= user.availableBalance && activeTrade == null) Color.White else Color(0xFF8E9BAE),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Regulatory Disclosure
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0x15FFFFFF)
                ) {
                    Text(
                        text = "• Regulatory Notice: The 6.00% daily profit is a configured promotional reward rate settled upon 60-second result confirmation on the official signal during permitted hours (3:00 PM - 9:00 PM). Not a guaranteed market deposit return.",
                        color = Color(0xFF8E9BAE),
                        fontSize = 9.sp,
                        lineHeight = 13.sp,
                        modifier = Modifier.padding(8.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun CandlestickChartBox(quote: MarketQuote) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF0A0D18))
            .border(1.dp, Color(0xFF1E2640), RoundedCornerShape(14.dp))
            .padding(8.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Grid lines
            val gridColor = Color(0x20FFFFFF)
            drawLine(gridColor, Offset(0f, h * 0.25f), Offset(w, h * 0.25f), strokeWidth = 1f)
            drawLine(gridColor, Offset(0f, h * 0.5f), Offset(w, h * 0.5f), strokeWidth = 1f)
            drawLine(gridColor, Offset(0f, h * 0.75f), Offset(w, h * 0.75f), strokeWidth = 1f)

            // Simulated candlesticks
            val candles = listOf(
                Triple(0.3f, 0.5f, true),
                Triple(0.48f, 0.42f, false),
                Triple(0.42f, 0.65f, true),
                Triple(0.6f, 0.52f, false),
                Triple(0.52f, 0.58f, true),
                Triple(0.58f, 0.78f, true),
                Triple(0.75f, 0.62f, false),
                Triple(0.62f, 0.35f, false),
                Triple(0.35f, 0.45f, true),
                Triple(0.44f, 0.72f, true),
                Triple(0.71f, 0.68f, false)
            )

            val candleWidth = w / (candles.size * 2f)
            candles.forEachIndexed { i, (openRatio, closeRatio, isGreen) ->
                val x = (i * 2 + 0.5f) * candleWidth
                val yOpen = h * (1f - openRatio)
                val yClose = h * (1f - closeRatio)
                val candleColor = if (isGreen) Color(0xFF00E676) else Color(0xFFFF3366)

                // Wick
                val high = minOf(yOpen, yClose) - 10f
                val low = maxOf(yOpen, yClose) + 10f
                drawLine(candleColor, Offset(x + candleWidth / 2, high), Offset(x + candleWidth / 2, low), strokeWidth = 2f)

                // Body
                val top = minOf(yOpen, yClose)
                val bodyHeight = maxOf(4f, kotlin.math.abs(yClose - yOpen))
                drawRect(candleColor, Offset(x, top), Size(candleWidth, bodyHeight))
            }

            // MA5 curve (Yellow) & MA10 curve (Purple)
            val ma5Color = Color(0xFFFFD700)
            val ma10Color = Color(0xFFE040FB)

            var prev5: Offset? = null
            var prev10: Offset? = null
            candles.forEachIndexed { i, (openRatio, closeRatio, _) ->
                val x = (i * 2 + 0.5f) * candleWidth + candleWidth / 2
                val y5 = h * (1f - (openRatio + closeRatio) / 2f * 0.95f)
                val y10 = h * (1f - (openRatio + closeRatio) / 2f * 0.90f)

                if (prev5 != null) {
                    drawLine(ma5Color, prev5!!, Offset(x, y5), strokeWidth = 2f)
                }
                if (prev10 != null) {
                    drawLine(ma10Color, prev10!!, Offset(x, y10), strokeWidth = 2f)
                }
                prev5 = Offset(x, y5)
                prev10 = Offset(x, y10)
            }
        }

        // Overlay MA legends
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(4.dp)
        ) {
            Text(text = "MA5: 77573.14  ", color = Color(0xFFFFD700), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(text = "MA10: 77563.80", color = Color(0xFFE040FB), fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun TradeSettledDialog(
    trade: ActiveTrade,
    viewModel: AppViewModel,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(26.dp),
            color = Color(0xFF131726),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, GainGreen)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Trophy / Gain Icon
                Box(
                    modifier = Modifier
                        .size(66.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(GainGreen, AccentCyan))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = "Profit",
                        tint = Color.Black,
                        modifier = Modifier.size(38.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "60S Trade Settled!",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black
                )

                Text(
                    text = "6% Fixed Profit Contract has completed.",
                    color = TextMuted,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Breakdown Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = BgDark,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Contract Type", color = TextMuted, fontSize = 12.sp)
                            Text(text = "${trade.symbol} (${trade.direction})", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = BorderSubtle.copy(alpha = 0.5f))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Invested Margin", color = TextMuted, fontSize = 12.sp)
                            Text(text = viewModel.formatInr(trade.amountInr), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = BorderSubtle.copy(alpha = 0.5f))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Fixed Profit (+6.00%)", color = TextMuted, fontSize = 12.sp)
                            Text(text = "+${viewModel.formatInr(trade.profitEarned)}", color = GainGreen, fontSize = 14.sp, fontWeight = FontWeight.Black)
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = BorderSubtle.copy(alpha = 0.5f))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Total Credited to Wallet", color = TextMuted, fontSize = 12.sp)
                            Text(
                                text = viewModel.formatInr(trade.amountInr + trade.profitEarned),
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                GradientButton(
                    text = "Continue Trading",
                    onClick = onDismiss,
                    brush = PrimaryGradient
                )
            }
        }
    }
}

package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppViewModel

@Composable
fun InvestmentPlansScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler { viewModel.navigateBack() }

    val user by viewModel.user.collectAsState()
    val activePlans by viewModel.userActivePlans.collectAsState()
    val plans = viewModel.investmentPlans

    var selectedPlanId by remember { mutableStateOf("PLAN-1000") }
    val currentPlan = plans.find { it.id == selectedPlanId } ?: plans.first()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF07090E)),
        contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateBack() }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "INR EXCHANGE Investment Form",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "90-Day Compounding Schemes & Projected Benefits",
                        color = AccentGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Scheme Selector Chips (1,000, 5,000, 10,000, 20,000, 50,000 Rs)
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "Select Investment Scheme Tier:",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    plans.forEach { p ->
                        val isSelected = p.id == selectedPlanId
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { selectedPlanId = p.id },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) Color(0xFFC41C1C) else Color(0xFF141824),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) Color(0xFFFFD700) else Color(0xFF282F48)
                            )
                        ) {
                            Text(
                                text = "${p.baseAmountInr.toInt()} Rs",
                                color = if (isSelected) Color.White else Color(0xFFB0B7C3),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Scheme Banner (Gold/Red styled banner like reference images)
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFF161924),
                border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFFFFB800))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "INVESTMENT DEPOSIT",
                                color = Color(0xFFFFD700),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = viewModel.formatInr(currentPlan.baseAmountInr, includeDecimals = false),
                                color = Color.White,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFD32F2F)
                        ) {
                            Text(
                                text = "6% DAILY PROFIT",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Projected 90 Days Return", color = TextMuted, fontSize = 11.sp)
                            Text(
                                text = viewModel.formatInr(currentPlan.projected90DayReturnInr, includeDecimals = false),
                                color = Color(0xFF00E676),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Available Balance", color = TextMuted, fontSize = 11.sp)
                            Text(
                                text = viewModel.formatInr(user.availableBalance),
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    GradientButton(
                        text = "Activate ${currentPlan.baseAmountInr.toInt()} Rs Scheme",
                        onClick = { viewModel.activatePlan(currentPlan.id) },
                        brush = Brush.horizontalGradient(listOf(Color(0xFFE53935), Color(0xFFFF8F00))),
                        enabled = user.availableBalance >= currentPlan.baseAmountInr
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Claimed / Projected Benefits Banner (Matching reference image bottom panel)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF0D1B2A),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1B3A4B))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Claimed / Projected Benefits",
                            color = Color(0xFFFFD700),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "— 90 Days Compounding Scheme —",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // 5 Benefit Pillars
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            BenefitPillar(title = "6% Daily", sub = "Deposit Profit", icon = Icons.Default.Percent, color = Color(0xFFE53935))
                            BenefitPillar(title = "INR ₹", sub = "Withdrawal", icon = Icons.Default.CurrencyRupee, color = Color(0xFFFFB800))
                            BenefitPillar(title = "Salary", sub = "VIP Income", icon = Icons.Default.AccountBalanceWallet, color = Color(0xFF00E676))
                            BenefitPillar(title = "Referral", sub = "10% L1 Income", icon = Icons.Default.Groups, color = Color(0xFF29B6F6))
                            BenefitPillar(title = "Level", sub = "L1-L3 Income", icon = Icons.Default.ShowChart, color = Color(0xFFAB47BC))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Active Investment Tracking
        if (activePlans.isNotEmpty()) {
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "My Active Compounding Plans",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            items(activePlans) { act ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF141926),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x5000E676))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Plan: ${act.baseAmountInr.toInt()} Rs (Day ${act.currentDay} of 90)",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(shape = RoundedCornerShape(6.dp), color = Color(0x3000E676)) {
                                Text(
                                    text = "ACCRUING 6%",
                                    color = Color(0xFF00E676),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = "Compounded Balance", color = TextMuted, fontSize = 10.sp)
                                Text(text = viewModel.formatInr(act.currentBalanceInr), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text(text = "Today's Return", color = TextMuted, fontSize = 10.sp)
                                Text(text = "+${viewModel.formatInr(act.todayProfitInr)}", color = Color(0xFF00E676), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = "Total Profits Accrued", color = TextMuted, fontSize = 10.sp)
                                Text(text = "+${viewModel.formatInr(act.totalProfitsInr)}", color = Color(0xFFFFD700), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }

        // 90-Day Compounding Breakdown Table
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "90-Day Schedule Breakdown (${currentPlan.baseAmountInr.toInt()} Rs)",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Daily funds, profits, and compounded balance matching reference chart",
                    color = TextMuted,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Table Header
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
                    color = Color(0xFF1E2435)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Day", color = Color(0xFFFFD700), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.8f))
                        Text(text = "Funds (₹)", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.3f))
                        Text(text = "Profit (₹)", color = Color(0xFFE53935), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.2f))
                        Text(text = "Balance (₹)", color = Color(0xFF00E676), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.4f), textAlign = TextAlign.End)
                    }
                }

                // Table Rows (showing key days and scrollable)
                currentPlan.schedule.take(30).forEachIndexed { index, row ->
                    val isAlt = index % 2 == 1
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = if (isAlt) Color(0xFF111420) else Color(0xFF161B29)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 7.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Day ${row.day}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(0.8f))
                            Text(text = row.funds.toInt().toString(), color = TextSecondary, fontSize = 11.sp, modifier = Modifier.weight(1.3f))
                            Text(text = "+${row.profit.toInt()}", color = Color(0xFFE57373), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.2f))
                            Text(text = row.balance.toInt().toString(), color = Color(0xFF00E676), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.4f), textAlign = TextAlign.End)
                        }
                    }
                }

                // Summary Note of Day 60 and Day 90
                val day60 = currentPlan.schedule[59]
                val day90 = currentPlan.schedule[89]
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp),
                    color = Color(0xFF1E2435)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Day 60 Balance:", color = Color(0xFFFFD700), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(text = viewModel.formatInr(day60.balance, includeDecimals = false), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Day 90 Final Return:", color = Color(0xFF00E676), fontSize = 13.sp, fontWeight = FontWeight.Black)
                            Text(text = viewModel.formatInr(day90.balance, includeDecimals = false), color = Color(0xFF00E676), fontSize = 14.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BenefitPillar(
    title: String,
    sub: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = title, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Text(text = sub, color = TextMuted, fontSize = 9.sp)
    }
}

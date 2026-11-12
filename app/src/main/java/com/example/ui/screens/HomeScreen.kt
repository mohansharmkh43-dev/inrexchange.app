package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.AppStrings
import com.example.data.model.MarketQuote
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.ActiveScreen
import com.example.ui.viewmodel.AppViewModel
import com.example.ui.viewmodel.MainTab

data class ReferenceQuickAction(
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@Composable
fun HomeScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val quotes by viewModel.quotes.collectAsState()
    val language by viewModel.language.collectAsState()
    val adminConfig by viewModel.adminConfig.collectAsState()

    val quickActions = listOf(
        ReferenceQuickAction(
            title = AppStrings.get("recharge", language),
            icon = Icons.Default.CallMade, // Diagonal arrow like screenshot
            onClick = { viewModel.navigateTo(ActiveScreen.RECHARGE) }
        ),
        ReferenceQuickAction(
            title = AppStrings.get("withdrawal", language),
            icon = Icons.Default.Sync, // Circular refresh/sync like screenshot
            onClick = { viewModel.navigateTo(ActiveScreen.WITHDRAWAL) }
        ),
        ReferenceQuickAction(
            title = AppStrings.get("serve", language),
            icon = Icons.Default.ChatBubbleOutline, // Chat bubble like screenshot
            onClick = { viewModel.navigateTo(ActiveScreen.SUPPORT_CHAT) }
        ),
        ReferenceQuickAction(
            title = AppStrings.get("salary", language),
            icon = Icons.Default.SwapHoriz, // Horizontal transfer arrows like screenshot
            onClick = { viewModel.navigateTo(ActiveScreen.SALARY_BONUS_DETAILS) }
        ),
        ReferenceQuickAction(
            title = AppStrings.get("signal", language),
            icon = Icons.Default.Link, // Link icon like screenshot
            onClick = { viewModel.setTab(MainTab.SIGNAL) }
        ),
        ReferenceQuickAction(
            title = AppStrings.get("download", language),
            icon = Icons.Default.CloudDownload, // Cloud download like screenshot
            onClick = {
                viewModel.showNotification("INR EXCHANGE APK is ready! File: INR_EXCHANGE.apk")
                viewModel.navigateTo(ActiveScreen.GUIDE)
            }
        )
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF000000)), // Deep pure black matching screenshots
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Top Header: 3D Isometric logo on Left, Globe Language icon on Right
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 3D Isometric Box Logo with Arrow
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF1E2640), Color(0xFF121422))
                                )
                            )
                            .border(1.dp, Color(0x60FF9800), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShowChart,
                            contentDescription = "Logo",
                            tint = Color(0xFFFF9800),
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "INR",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "EXCHANGE",
                                color = AccentPink,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        }
                        Text(
                            text = "India's Premier Rupee Trading Hub",
                            color = Color(0xFF6B7280),
                            fontSize = 10.sp
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Admin Terminal Icon
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF22152E))
                            .border(1.dp, Color(0x60FF2E93), CircleShape)
                            .clickable { viewModel.navigateTo(ActiveScreen.ADMIN_DASHBOARD) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "Admin Back-Office",
                            tint = AccentPink,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Globe icon button for language switch
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1A1D28))
                            .clickable { viewModel.toggleLanguage() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = "Language",
                            tint = Color(0xFF8E9BAE),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Live Admin Announcement Ticker
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF111422),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF232A40))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        contentDescription = "Announcement",
                        tint = AccentCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = adminConfig.tickerAnnouncement,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
        }

        // Demo notice
        item {
            DemoWalletNotice(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp),
                language = language
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        // 2-Column x 3-Row Quick Action Cards (Matching Reference Screenshot)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Row 1: Recharge & Withdrawal
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ScreenshotActionCard(
                        action = quickActions[0],
                        modifier = Modifier.weight(1f)
                    )
                    ScreenshotActionCard(
                        action = quickActions[1],
                        modifier = Modifier.weight(1f)
                    )
                }

                // Row 2: Serve & Salary
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ScreenshotActionCard(
                        action = quickActions[2],
                        modifier = Modifier.weight(1f)
                    )
                    ScreenshotActionCard(
                        action = quickActions[3],
                        modifier = Modifier.weight(1f)
                    )
                }

                // Row 3: Signal & Download
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ScreenshotActionCard(
                        action = quickActions[4],
                        modifier = Modifier.weight(1f)
                    )
                    ScreenshotActionCard(
                        action = quickActions[5],
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Official Daily Signal Spotlight Banner (Requirement 2 & 3)
        item {
            val dailyOfficialSignal by viewModel.dailyOfficialSignal.collectAsState()
            val isWindowOpen = viewModel.isTradingWindowOpen()

            Spacer(modifier = Modifier.height(14.dp))
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .clickable {
                        val matchedQuote = quotes.find {
                            it.symbol.equals(dailyOfficialSignal.symbol, ignoreCase = true) ||
                            it.symbol.equals(dailyOfficialSignal.pairDisplayName, ignoreCase = true)
                        } ?: quotes.first()
                        viewModel.selectQuoteForTrade(matchedQuote)
                    },
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFF101C22),
                border = androidx.compose.foundation.BorderStroke(1.2.dp, Brush.horizontalGradient(listOf(GainGreen, AccentCyan)))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(GainGreen, AccentCyan))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "TODAY'S OFFICIAL SIGNAL",
                                    color = GainGreen,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(shape = RoundedCornerShape(4.dp), color = Color(0x3000E676)) {
                                    Text(
                                        text = "+${dailyOfficialSignal.profitPercent}%",
                                        color = GainGreen,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(
                                text = "${dailyOfficialSignal.pairDisplayName} • ${dailyOfficialSignal.direction}",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isWindowOpen) "Trading Open: 3:00 PM - 9:00 PM • 60S Duration" else "Trading Hours: 3:00 PM - 9:00 PM (Closed)",
                                color = if (isWindowOpen) AccentCyan else LossRed,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Trade Now",
                        tint = AccentCyan
                    )
                }
            }
        }

        // 90-Day Compounding Investment Plans Featured Card
        item {
            Spacer(modifier = Modifier.height(14.dp))
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .clickable { viewModel.navigateTo(ActiveScreen.INVESTMENT_PLANS) },
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFF151928),
                border = androidx.compose.foundation.BorderStroke(1.2.dp, Brush.horizontalGradient(listOf(Color(0xFFFF9800), AccentPink)))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(Color(0xFFFF9800), AccentPink))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "6%",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "90-Day Compounding Scheme",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFFF9800).copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "HOT",
                                        color = Color(0xFFFF9800),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Plans: ₹1,000 • ₹5,000 • ₹10,000 • ₹20,000 • ₹50,000",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "View Plans",
                        tint = AccentCyan
                    )
                }
            }
        }

        // Section Title: "Hot"
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = AppStrings.get("hot", language),
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Coin Rows (Matching Reference Screenshot)
        items(quotes) { quote ->
            ScreenshotCoinRow(
                quote = quote,
                viewModel = viewModel,
                onTradeClick = { viewModel.selectQuoteForTrade(quote) }
            )
        }
    }
}

@Composable
fun ScreenshotActionCard(
    action: ReferenceQuickAction,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(58.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = action.onClick),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF13151D) // Dark card background as in screenshot
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1C202E)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = action.icon,
                    contentDescription = action.title,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = action.title,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ScreenshotCoinRow(
    quote: MarketQuote,
    viewModel: AppViewModel,
    onTradeClick: () -> Unit
) {
    val isPositive = quote.change24hPercent >= 0
    val changeColor = if (isPositive) Color(0xFF00E676) else Color(0xFFFF3B30)
    val prefix = if (isPositive) "+" else ""

    val coinBgColor = when (quote.symbol) {
        "BTC" -> Color(0xFFF7931A)
        "ETH" -> Color(0xFF627EEA)
        "DOGE" -> Color(0xFFC2A633)
        "BCH" -> Color(0xFF0AC18E)
        "BSV" -> Color(0xFFEAB300)
        "ETC" -> Color(0xFF34FA99)
        "BNB" -> Color(0xFFF3BA2F)
        "LTC" -> Color(0xFF345D9D)
        "XRP" -> Color(0xFF23292F)
        "SHIB" -> Color(0xFFFFA409)
        else -> AccentPurple
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onTradeClick)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Coin Icon + Symbol
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(coinBgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = quote.symbol.take(1),
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = quote.symbol,
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Right: Vibrant Blue Pill "Trade" Button
            Button(
                onClick = onTradeClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F80ED)),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 22.dp, vertical = 6.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Text(
                    text = "Trade",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Three columns: Current | 24H highest | 24H minimum
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Current column
            Column(modifier = Modifier.weight(1.3f)) {
                Text(
                    text = "Current",
                    color = Color(0xFF6B7280),
                    fontSize = 11.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = viewModel.formatInr(quote.priceInInr),
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$prefix${String.format("%.2f", quote.change24hPercent)}%",
                        color = changeColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 24H highest column
            Column(
                modifier = Modifier.weight(1.1f),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "24H highest",
                    color = Color(0xFF6B7280),
                    fontSize = 11.sp
                )
                Text(
                    text = viewModel.formatInr(quote.high24h),
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // 24H minimum column
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "24H minimum",
                    color = Color(0xFF6B7280),
                    fontSize = 11.sp
                )
                Text(
                    text = viewModel.formatInr(quote.low24h),
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Divider(color = Color(0xFF141720), thickness = 0.8.dp)
    }
}

package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppViewModel

@Composable
fun SignalsScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler { viewModel.navigateBack() }

    val context = LocalContext.current
    val quotes by viewModel.quotes.collectAsState()
    val dailyOfficialSignal by viewModel.dailyOfficialSignal.collectAsState()
    val adminConfig by viewModel.adminConfig.collectAsState()
    val isWindowOpen = viewModel.isTradingWindowOpen()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BgDark)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateBack() }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Official Trading Signals",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(GainGreen))
                    }
                    Text(text = "Daily 6% Platform Signal & Official Telegram Broadcast", color = TextMuted, fontSize = 11.sp)
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // 1. OFFICIAL TELEGRAM SIGNALS CHANNEL CARD (NEW REQUEST)
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                color = Color(0xFF101C2B),
                border = androidx.compose.foundation.BorderStroke(
                    1.5.dp,
                    Brush.horizontalGradient(listOf(Color(0xFF0088CC), Color(0xFF229ED9), AccentCyan))
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF0088CC)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = "Telegram",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = adminConfig.telegramTitle,
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Verified",
                                        tint = Color(0xFF229ED9),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                Text(
                                    text = "Official Telegram ID: ${adminConfig.telegramHandle}",
                                    color = Color(0xFF229ED9),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF0088CC).copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "LIVE TELEGRAM",
                                color = Color(0xFF229ED9),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = adminConfig.telegramDescription,
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Open Telegram Channel Button
                        Button(
                            onClick = {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(adminConfig.telegramChannelLink))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    viewModel.showNotification("Opening link: ${adminConfig.telegramChannelLink}")
                                }
                            },
                            modifier = Modifier.weight(1.3f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0088CC)),
                            contentPadding = PaddingValues(vertical = 12.dp)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Join Telegram", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        // Copy Telegram ID Button
                        OutlinedButton(
                            onClick = {
                                val cb = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                cb.setPrimaryClip(ClipData.newPlainText("Telegram Handle", adminConfig.telegramHandle))
                                viewModel.showNotification("Telegram ID copied: ${adminConfig.telegramHandle}")
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF0088CC)),
                            contentPadding = PaddingValues(vertical = 12.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Color(0xFF229ED9), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copy ID", color = Color(0xFF229ED9), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 2. OFFICIAL DAILY TRADING SIGNAL (Controlled by Admin for 6% Trades)
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                color = Color(0xFF111D24),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Brush.horizontalGradient(listOf(GainGreen, AccentCyan)))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(shape = RoundedCornerShape(8.dp), color = GainGreen.copy(alpha = 0.2f)) {
                            Text(
                                text = "🎯 OFFICIAL DAILY PLATFORM SIGNAL",
                                color = GainGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Surface(shape = RoundedCornerShape(8.dp), color = AccentPink.copy(alpha = 0.2f)) {
                            Text(
                                text = "+${dailyOfficialSignal.profitPercent}% REWARD",
                                color = AccentPink,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = dailyOfficialSignal.pairDisplayName,
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Direction: ${dailyOfficialSignal.direction}",
                            color = if (dailyOfficialSignal.direction == "Buy up") GainGreen else LossRed,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(text = "•", color = TextMuted)
                        Text(text = "Duration: 60 Seconds", color = AccentCyan, fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Trading Window: ${dailyOfficialSignal.validFromHour}:00 - ${dailyOfficialSignal.validToHour}:00 Daily (3:00 PM - 9:00 PM). Only trades on this official signal qualify for platform promotional rewards (Quota: 1 trade / 24h).",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    GradientButton(
                        text = "Trade Official Signal (${dailyOfficialSignal.pairDisplayName})",
                        onClick = {
                            val matchedQuote = quotes.find {
                                it.symbol.equals(dailyOfficialSignal.symbol, ignoreCase = true) ||
                                it.symbol.equals(dailyOfficialSignal.pairDisplayName, ignoreCase = true)
                            } ?: quotes.first()
                            viewModel.selectQuoteForTrade(matchedQuote)
                        },
                        brush = PrimaryGradient
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 3. TELEGRAM SIGNAL BROADCAST BENEFITS CARD
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = BgCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Why Join Official Telegram?",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.padding(vertical = 3.dp)) {
                        Text(text = "• ", color = AccentCyan, fontWeight = FontWeight.Bold)
                        Text(text = "Daily 6% signal options broadcasted before the 3:00 PM trading window.", color = TextMuted, fontSize = 11.sp)
                    }
                    Row(modifier = Modifier.padding(vertical = 3.dp)) {
                        Text(text = "• ", color = AccentCyan, fontWeight = FontWeight.Bold)
                        Text(text = "Direct updates on trading schedule, countdown alerts, and wallet settlement.", color = TextMuted, fontSize = 11.sp)
                    }
                    Row(modifier = Modifier.padding(vertical = 3.dp)) {
                        Text(text = "• ", color = AccentCyan, fontWeight = FontWeight.Bold)
                        Text(text = "Instant 24/7 VIP assistance from administrators directly on Telegram.", color = TextMuted, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun GuideScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler { viewModel.navigateBack() }

    val sections = listOf(
        "Welcome to INR EXCHANGE" to "INR EXCHANGE is India's premier financial dashboard and trading simulation platform denominated natively in Indian Rupees (INR ₹). Explore real-time cryptocurrency pairs, benchmark indices (NIFTY 50, SENSEX), and forex markets with institutional tools.",
        "Recharging Your Demo Wallet" to "1. Go to Home > Recharge.\n2. Choose Instant UPI QR, PhonePe/GPay, or IMPS.\n3. Enter desired INR amount and submit. Your demo balance is updated instantly.",
        "Executing Contract Trades" to "1. Select any pair from Trending Markets or the Quotes tab.\n2. Tap 'BUY / LONG' or 'SELL / SHORT'.\n3. Adjust leverage from 1x to 20x and margin in ₹.\n4. Confirm trade execution.",
        "VIP Membership System" to "Upgrade through 6 prestigious tiers (VIP 0 to VIP 5). Higher tiers qualify for up to 85% reduced exchange fees, unlimited daily withdrawals, and personalized market insights.",
        "Compliance & Safety" to "This application serves as an educational and simulation hub. Real money is never solicited, ensuring a risk-free learning environment for mastering financial trading."
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BgDark)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateBack() }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "INR EXCHANGE User Guide",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // APK Download Card
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF131D2E),
                border = androidx.compose.foundation.BorderStroke(1.2.dp, Brush.horizontalGradient(listOf(AccentCyan, AccentPink)))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(AccentCyan.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudDownload,
                                    contentDescription = null,
                                    tint = AccentCyan,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Android APK Package",
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "INR_EXCHANGE.apk • Release Build",
                                    color = AccentCyan,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = GainGreen.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "READY",
                                color = GainGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "The standalone Android APK is built and stored at 'public/INR_EXCHANGE.apk' and root './INR_EXCHANGE.apk'. You can also download it from the top-right AI Studio settings menu > 'Download APK'.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            viewModel.showNotification("APK path copied! File: public/INR_EXCHANGE.apk")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentPink),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy APK Download Info", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        items(sections) { (title, content) ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(18.dp),
                color = BgCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0x20FF2E93)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.MenuBook, contentDescription = null, tint = AccentPink, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = content, color = TextSecondary, fontSize = 12.sp, lineHeight = 19.sp)
                }
            }
        }
    }
}

@Composable
fun ProfileSettingsScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler { viewModel.navigateBack() }

    val user by viewModel.user.collectAsState()
    var name by remember { mutableStateOf(user.name) }
    var mobile by remember { mutableStateOf(user.mobile) }
    var upiId by remember { mutableStateOf(user.upiId) }
    var bankAcc by remember { mutableStateOf(user.bankAccountNumber) }
    var ifsc by remember { mutableStateOf(user.bankIfsc) }

    var showAuthDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BgDark)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateBack() }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Profile & Account Settings",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Account Switch & Mobile Auth
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = BgCardElevated,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "CURRENT ACCOUNT", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(text = user.name, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text(text = user.mobile, color = AccentCyan, fontSize = 12.sp)
                    }
                    Button(
                        onClick = { showAuthDialog = true },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentPurple)
                    ) {
                        Text("Switch Mobile", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        item {
            GradientCard(
                shape = RoundedCornerShape(20.dp),
                backgroundBrush = Brush.verticalGradient(listOf(BgCardElevated, BgCard))
            ) {
                Text(text = "Personal Details", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = mobile,
                    onValueChange = { mobile = it },
                    label = { Text("Mobile Number (+91)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Payment Payout Accounts", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = upiId,
                    onValueChange = { upiId = it },
                    label = { Text("UPI VPA ID") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = bankAcc,
                    onValueChange = { bankAcc = it },
                    label = { Text("Bank Account Number") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = ifsc,
                    onValueChange = { ifsc = it },
                    label = { Text("Bank IFSC Code") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                GradientButton(
                    text = "Save Profile Changes",
                    onClick = {
                        viewModel.updateProfile(name, mobile, upiId, bankAcc, ifsc)
                        viewModel.navigateBack()
                    },
                    brush = PrimaryGradient
                )
            }
        }
    }

    if (showAuthDialog) {
        var authMobile by remember { mutableStateOf("") }
        var authPassword by remember { mutableStateOf("") }
        var isRegisterMode by remember { mutableStateOf(false) }
        var authName by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAuthDialog = false },
            containerColor = BgCardElevated,
            title = {
                Text(
                    text = if (isRegisterMode) "Register (Mobile & Password)" else "Login (Mobile & Password)",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Sign in or create account using your 10-digit mobile number and secure password.",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                    if (isRegisterMode) {
                        OutlinedTextField(
                            value = authName,
                            onValueChange = { authName = it },
                            label = { Text("Your Full Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    OutlinedTextField(
                        value = authMobile,
                        onValueChange = { if (it.length <= 10 && it.all { c -> c.isDigit() }) authMobile = it },
                        label = { Text("10-Digit Mobile Number") },
                        prefix = { Text("+91 ") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = authPassword,
                        onValueChange = { authPassword = it },
                        label = { Text("Password (Min 4 characters)") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    TextButton(
                        onClick = { isRegisterMode = !isRegisterMode },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(
                            text = if (isRegisterMode) "Already registered? Login" else "New trader? Register (+91)",
                            color = AccentCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (isRegisterMode) {
                            viewModel.registerWithMobile(authName, authMobile, authPassword)
                        } else {
                            viewModel.loginWithMobile(authMobile, authPassword)
                        }
                        showAuthDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentPink)
                ) {
                    Text(if (isRegisterMode) "Register" else "Login")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAuthDialog = false }) { Text("Cancel") }
            }
        )
    }
}

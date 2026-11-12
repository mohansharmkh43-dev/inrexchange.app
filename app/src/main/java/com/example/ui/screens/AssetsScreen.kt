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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.AppStrings
import com.example.data.model.Transaction
import com.example.data.model.TransactionStatus
import com.example.data.model.TransactionType
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.ActiveScreen
import com.example.ui.viewmodel.AppViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AssetsScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val user by viewModel.user.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val language by viewModel.language.collectAsState()

    var selectedHistoryFilter by remember { mutableStateOf<TransactionStatus?>(null) } // null = All
    var showBankDetailsDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }

    val filteredTransactions = remember(transactions, selectedHistoryFilter) {
        if (selectedHistoryFilter == null) transactions
        else transactions.filter { it.status == selectedHistoryFilter }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BgDark),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // User Profile Header Bar
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                color = BgCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(PrimaryGradient),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = user.name.take(2).uppercase(),
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = user.name,
                                    color = Color.White,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0x3000E676)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PhoneAndroid,
                                            contentDescription = null,
                                            tint = GainGreen,
                                            modifier = Modifier.size(11.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "Mobile Verified",
                                            color = GainGreen,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "UID: ${user.uid} • ${user.mobile}",
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                        }
                    }

                    IconButton(onClick = { viewModel.navigateTo(ActiveScreen.PROFILE_SETTINGS) }) {
                        Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings", tint = AccentPink)
                    }
                }
            }
        }

        // Compliance notice
        item {
            DemoWalletNotice(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                language = language
            )
        }

        // Total Assets Grand Card
        item {
            GradientCard(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(24.dp),
                backgroundBrush = Brush.linearGradient(
                    listOf(Color(0xFF271336), Color(0xFF131726), Color(0xFF16233B))
                )
            ) {
                Text(
                    text = AppStrings.get("total_assets", language),
                    color = TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = viewModel.formatInr(user.totalAssets),
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Income Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = AppStrings.get("available_balance", language), color = TextMuted, fontSize = 11.sp)
                        Text(text = viewModel.formatInr(user.availableBalance), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = AppStrings.get("contract_amount", language), color = TextMuted, fontSize = 11.sp)
                        Text(text = viewModel.formatInr(user.contractAmount), color = AccentCyan, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = AppStrings.get("today_income", language), color = TextMuted, fontSize = 11.sp)
                        Text(text = "+${viewModel.formatInr(user.todayIncome)}", color = GainGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Fast Action Buttons: Recharge & Withdrawal
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GradientButton(
                        text = AppStrings.get("recharge", language),
                        onClick = { viewModel.navigateTo(ActiveScreen.RECHARGE) },
                        modifier = Modifier.weight(1f),
                        brush = PrimaryGradient,
                        icon = Icons.Default.AddCircle
                    )
                    OutlinedActionButton(
                        text = AppStrings.get("withdrawal", language),
                        onClick = { viewModel.navigateTo(ActiveScreen.WITHDRAWAL) },
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.ArrowCircleUp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { viewModel.navigateTo(ActiveScreen.INVESTMENT_PLANS) },
                    color = Color(0xFF1E1730),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Brush.horizontalGradient(listOf(Color(0xFFFF9800), AccentPink)))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = Color(0xFFFF9800),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "90-Day Compounding Schemes (6% Daily)",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "₹1,000 to ₹50,000 plans with daily return schedules",
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = AccentCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Detailed Assets Breakdown Grid
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Asset Portfolio Breakdown",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AssetStatCard(
                        title = AppStrings.get("trading_assets", language),
                        value = viewModel.formatInr(user.tradingAssets),
                        color = AccentPink,
                        icon = Icons.Default.CandlestickChart,
                        modifier = Modifier.weight(1f)
                    )
                    AssetStatCard(
                        title = AppStrings.get("bonus_assets", language),
                        value = viewModel.formatInr(user.bonusAssets),
                        color = AccentGold,
                        icon = Icons.Default.CardGiftcard,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AssetStatCard(
                        title = AppStrings.get("total_recharge", language),
                        value = viewModel.formatInr(user.totalRecharge),
                        color = GainGreen,
                        icon = Icons.Default.AccountBalanceWallet,
                        modifier = Modifier.weight(1f)
                    )
                    AssetStatCard(
                        title = AppStrings.get("total_withdrawal", language),
                        value = viewModel.formatInr(user.totalWithdrawal),
                        color = AccentBlue,
                        icon = Icons.Default.CreditCard,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Quick Settings Shortcuts (Bank, PIN, Language, Password, VIP)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Account & Security Controls",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                SettingsItemRow(
                    title = "Bank Account & UPI Interface",
                    subtitle = "${user.bankName} (${user.bankAccountNumber})",
                    icon = Icons.Default.AccountBalance,
                    onClick = { showBankDetailsDialog = true }
                )
                SettingsItemRow(
                    title = "VIP Membership Status",
                    subtitle = "Level VIP ${user.vipLevel} Active",
                    icon = Icons.Default.WorkspacePremium,
                    onClick = { viewModel.navigateTo(ActiveScreen.VIP_DETAILS) }
                )
                SettingsItemRow(
                    title = "Security & PIN Management",
                    subtitle = "Security PIN is enabled for withdrawals",
                    icon = Icons.Default.Lock,
                    onClick = { showChangePasswordDialog = true }
                )
                SettingsItemRow(
                    title = "Language Selection",
                    subtitle = if (language == AppLanguage.ENGLISH) "English (Current)" else "हिन्दी (Current)",
                    icon = Icons.Default.Translate,
                    onClick = { viewModel.toggleLanguage() }
                )
                SettingsItemRow(
                    title = "Admin Back-Office Portal",
                    subtitle = "Central administrative governance & controls",
                    icon = Icons.Default.AdminPanelSettings,
                    onClick = { viewModel.navigateTo(ActiveScreen.ADMIN_DASHBOARD) }
                )
            }
        }

        // Transaction History Header & Filter Chips
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Transaction & Change Records",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = selectedHistoryFilter == null,
                        onClick = { selectedHistoryFilter = null },
                        label = { Text("All") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AccentPurple,
                            selectedLabelColor = Color.White,
                            containerColor = BgCardElevated,
                            labelColor = TextSecondary
                        )
                    )
                    FilterChip(
                        selected = selectedHistoryFilter == TransactionStatus.SUCCESS,
                        onClick = { selectedHistoryFilter = TransactionStatus.SUCCESS },
                        label = { Text("Success") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GainGreen,
                            selectedLabelColor = Color.Black,
                            containerColor = BgCardElevated,
                            labelColor = TextSecondary
                        )
                    )
                    FilterChip(
                        selected = selectedHistoryFilter == TransactionStatus.PENDING,
                        onClick = { selectedHistoryFilter = TransactionStatus.PENDING },
                        label = { Text("Pending") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PendingYellow,
                            selectedLabelColor = Color.Black,
                            containerColor = BgCardElevated,
                            labelColor = TextSecondary
                        )
                    )
                    FilterChip(
                        selected = selectedHistoryFilter == TransactionStatus.FAILED,
                        onClick = { selectedHistoryFilter = TransactionStatus.FAILED },
                        label = { Text("Failed") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = LossRed,
                            selectedLabelColor = Color.White,
                            containerColor = BgCardElevated,
                            labelColor = TextSecondary
                        )
                    )
                }
            }
        }

        // Transaction items
        if (filteredTransactions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "No records found in this category", color = TextMuted, fontSize = 13.sp)
                }
            }
        } else {
            items(filteredTransactions) { tx ->
                TransactionRowItem(tx = tx, viewModel = viewModel)
            }
        }

        // App Download & Sign out
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "INR EXCHANGE App v2.4.0 Pro Edition • Build 2026",
                    color = TextMuted,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(14.dp))
                OutlinedActionButton(
                    text = AppStrings.get("sign_out", language),
                    onClick = { viewModel.showNotification("Signed out. Switched to demo guest state.") },
                    borderColor = LossRed,
                    icon = Icons.Default.Logout
                )
            }
        }
    }

    // Bank details dialog
    if (showBankDetailsDialog) {
        BankDetailsDialog(
            user = user,
            viewModel = viewModel,
            onDismiss = { showBankDetailsDialog = false }
        )
    }

    // Change Password / PIN Dialog
    if (showChangePasswordDialog) {
        ChangePasswordDialog(
            viewModel = viewModel,
            onDismiss = { showChangePasswordDialog = false }
        )
    }
}

@Composable
fun AssetStatCard(
    title: String,
    value: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = BgCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, color = TextSecondary, fontSize = 11.sp)
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = value, color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
fun SettingsItemRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = BgCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0x209B51E0)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = AccentPink, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Text(text = subtitle, color = TextMuted, fontSize = 11.sp)
                }
            }

            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun TransactionRowItem(
    tx: Transaction,
    viewModel: AppViewModel
) {
    val isCredit = tx.type in listOf(TransactionType.RECHARGE, TransactionType.TRADING_PROFIT, TransactionType.BONUS_CLAIM, TransactionType.REFERRAL_COMMISSION)
    val amountColor = if (isCredit) GainGreen else Color.White
    val prefix = if (isCredit) "+" else "-"

    val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    val dateStr = sdf.format(Date(tx.timestamp))

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        color = BgCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            when (tx.type) {
                                TransactionType.RECHARGE -> Color(0x2000E676)
                                TransactionType.WITHDRAWAL -> Color(0x202F80ED)
                                TransactionType.TRADING_PROFIT -> Color(0x20FF2E93)
                                TransactionType.BONUS_CLAIM -> Color(0x20FFB800)
                                else -> Color(0x209B51E0)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (tx.type) {
                            TransactionType.RECHARGE -> Icons.Default.Add
                            TransactionType.WITHDRAWAL -> Icons.Default.ArrowOutward
                            TransactionType.TRADING_PROFIT -> Icons.Default.TrendingUp
                            TransactionType.BONUS_CLAIM -> Icons.Default.Celebration
                            else -> Icons.Default.SwapHoriz
                        },
                        contentDescription = null,
                        tint = when (tx.type) {
                            TransactionType.RECHARGE -> GainGreen
                            TransactionType.WITHDRAWAL -> AccentBlue
                            TransactionType.TRADING_PROFIT -> AccentPink
                            TransactionType.BONUS_CLAIM -> AccentGold
                            else -> AccentPurple
                        },
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = tx.type.name.replace("_", " "),
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${tx.method} • $dateStr",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$prefix${viewModel.formatInr(tx.amountInr)}",
                    color = amountColor,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(2.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (tx.status) {
                        TransactionStatus.SUCCESS -> Color(0x2000E676)
                        TransactionStatus.PENDING -> Color(0x20FFC107)
                        TransactionStatus.FAILED -> Color(0x20FF3366)
                    }
                ) {
                    Text(
                        text = tx.status.name,
                        color = when (tx.status) {
                            TransactionStatus.SUCCESS -> GainGreen
                            TransactionStatus.PENDING -> PendingYellow
                            TransactionStatus.FAILED -> LossRed
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun BankDetailsDialog(
    user: com.example.data.model.User,
    viewModel: AppViewModel,
    onDismiss: () -> Unit
) {
    var bankName by remember { mutableStateOf(user.bankName) }
    var accountNo by remember { mutableStateOf(user.bankAccountNumber) }
    var ifsc by remember { mutableStateOf(user.bankIfsc) }
    var upiId by remember { mutableStateOf(user.upiId) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BgCardElevated,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(text = "Bank & UPI Interface", color = Color.White, fontWeight = FontWeight.Bold)
        },
        text = {
            Column {
                Text(text = "Configure your payment accounts for simulated payouts.", color = TextMuted, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(14.dp))
                OutlinedTextField(
                    value = bankName,
                    onValueChange = { bankName = it },
                    label = { Text("Bank Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = accountNo,
                    onValueChange = { accountNo = it },
                    label = { Text("Account Number") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = ifsc,
                    onValueChange = { ifsc = it },
                    label = { Text("IFSC Code") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = upiId,
                    onValueChange = { upiId = it },
                    label = { Text("UPI VPA (e.g. name@upi)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.updateProfile(user.name, user.mobile, upiId, accountNo, ifsc)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = AccentPink)
            ) {
                Text("Save Accounts")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextMuted) }
        }
    )
}

@Composable
fun ChangePasswordDialog(
    viewModel: AppViewModel,
    onDismiss: () -> Unit
) {
    var oldPass by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BgCardElevated,
        shape = RoundedCornerShape(24.dp),
        title = { Text(text = "Security & PIN Setup", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                OutlinedTextField(
                    value = oldPass,
                    onValueChange = { oldPass = it },
                    label = { Text("Current Password") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = newPass,
                    onValueChange = { newPass = it },
                    label = { Text("New Login Password") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = pin,
                    onValueChange = { if (it.length <= 6) pin = it },
                    label = { Text("6-Digit Payment Security PIN") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (newPass.isNotBlank()) {
                        viewModel.updatePassword(newPass)
                    } else {
                        viewModel.showNotification("Security settings updated successfully!")
                    }
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = AccentPink)
            ) {
                Text("Update Password & Security")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextMuted) }
        }
    )
}

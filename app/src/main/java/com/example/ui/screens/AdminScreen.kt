package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import java.io.File
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler { viewModel.navigateBack() }

    val user by viewModel.user.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val tickets by viewModel.supportTickets.collectAsState()
    val auditLogs by viewModel.auditLogs.collectAsState()
    val adminConfig by viewModel.adminConfig.collectAsState()
    val dynamicFeatures by viewModel.dynamicFeatures.collectAsState()
    val isAdminAuthorized by viewModel.isAdminAuthorized.collectAsState()

    var activeAdminTab by remember { mutableStateOf("OVERVIEW") }
    // Tabs: "OVERVIEW", "USERS", "BALANCES", "RECHARGES", "WITHDRAWALS", "TRADING", "REFERRAL", "CONTENT", "FEATURES", "SCANNER", "SECURITY", "LOGS"

    // If not authorized and user is not role ADMIN, show Master Admin Login Gate
    if (!isAdminAuthorized && user.role != "ADMIN") {
        AdminLoginGate(
            viewModel = viewModel,
            onBack = { viewModel.navigateBack() }
        )
        return
    }

    val pendingRecharges = remember(transactions) {
        transactions.filter { it.type == TransactionType.RECHARGE && it.status == TransactionStatus.PENDING }
    }
    val pendingWithdrawals = remember(transactions) {
        transactions.filter { it.type == TransactionType.WITHDRAWAL && it.status == TransactionStatus.PENDING }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BgDark)
    ) {
        // Top Navigation Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.navigateBack() }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Admin Back-Office",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(shape = RoundedCornerShape(6.dp), color = AccentGold) {
                            Text(
                                text = "SUPER ADMIN",
                                color = Color.Black,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(text = "Centralized Platform Governance & Control", color = TextMuted, fontSize = 10.sp)
                }
            }

            Row {
                IconButton(onClick = {
                    viewModel.showNotification("Audit report snapshot exported to server storage.")
                }) {
                    Icon(imageVector = Icons.Default.Download, contentDescription = "Export Report", tint = AccentCyan)
                }
                IconButton(onClick = {
                    viewModel.lockAdmin()
                    viewModel.showNotification("Admin panel locked.")
                }) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = "Lock Admin", tint = LossRed)
                }
            }
        }

        // Horizontal Scrollable Category Tabs (Covering all 12 modules)
        val adminTabs = listOf(
            "OVERVIEW" to "Overview & Stats",
            "USERS" to "Users (${allUsers.size})",
            "BALANCES" to "Wallet Balances",
            "RECHARGES" to "Recharges (${pendingRecharges.size})",
            "WITHDRAWALS" to "Withdrawals (${pendingWithdrawals.size})",
            "TRADING" to "Trading & Plans",
            "REFERRAL" to "Referral & VIP",
            "CONTENT" to "Content & Banners",
            "FEATURES" to "Feature Flags",
            "SCANNER" to "Scanner & QR",
            "SECURITY" to "Security & PIN",
            "LOGS" to "Audit Logs (${auditLogs.size})"
        )

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(adminTabs) { (id, label) ->
                val isSel = activeAdminTab == id
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { activeAdminTab = id },
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSel) AccentPurple else BgCardElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) AccentPink else BorderSubtle)
                ) {
                    Text(
                        text = label,
                        color = if (isSel) Color.White else TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Content Area per Selected Tab
        Box(modifier = Modifier.fillMaxSize()) {
            when (activeAdminTab) {
                "OVERVIEW" -> AdminOverviewModule(allUsers, transactions, tickets, adminConfig, viewModel)
                "USERS" -> AdminUsersModule(allUsers, viewModel)
                "BALANCES" -> AdminBalancesModule(allUsers, viewModel)
                "RECHARGES" -> AdminRechargesModule(transactions, viewModel)
                "WITHDRAWALS" -> AdminWithdrawalsModule(adminConfig, transactions, viewModel)
                "TRADING" -> AdminTradingModule(adminConfig, viewModel)
                "REFERRAL" -> AdminReferralModule(adminConfig, viewModel)
                "CONTENT" -> AdminContentModule(adminConfig, viewModel)
                "FEATURES" -> AdminFeaturesModule(adminConfig, dynamicFeatures, viewModel)
                "SCANNER" -> AdminScannerModule(adminConfig, viewModel)
                "SECURITY" -> AdminSecurityModule(adminConfig, viewModel)
                "LOGS" -> AdminLogsView(auditLogs)
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// MODULE 0: ADMIN LOGIN GATE (Role-Based Access Control)
// -------------------------------------------------------------------------------------------------
@Composable
fun AdminLoginGate(
    viewModel: AppViewModel,
    onBack: () -> Unit
) {
    var pinInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(AccentPurple, AccentPink))),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = "Admin Shield",
                tint = Color.White,
                modifier = Modifier.size(42.dp)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "INR EXCHANGE Admin Portal",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Black
        )
        Text(
            text = "Role-based restricted administrative access",
            color = TextMuted,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = BgCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(text = "Enter Administrator PIN", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = pinInput,
                    onValueChange = { if (it.length <= 8) pinInput = it },
                    placeholder = { Text("Default Master PIN: 8888", color = Color(0xFF6B7280)) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                GradientButton(
                    text = "Authorize Terminal",
                    onClick = {
                        viewModel.verifyAdminPin(pinInput)
                    },
                    brush = PrimaryGradient
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedActionButton(
                    text = "Auto-Fill Demo Admin PIN (8888)",
                    onClick = {
                        pinInput = "8888"
                        viewModel.verifyAdminPin("8888")
                    },
                    icon = Icons.Default.Key
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        TextButton(onClick = onBack) {
            Text("Back to App", color = TextSecondary)
        }
    }
}

// -------------------------------------------------------------------------------------------------
// MODULE 10 & 0: OVERVIEW & ANALYTICS
// -------------------------------------------------------------------------------------------------
@Composable
fun AdminOverviewModule(
    allUsers: List<User>,
    transactions: List<Transaction>,
    tickets: List<SupportTicket>,
    adminConfig: AdminAppConfig,
    viewModel: AppViewModel
) {
    val totalPlatformDeposits = remember(transactions) {
        transactions.filter { it.type == TransactionType.RECHARGE && it.status == TransactionStatus.SUCCESS }.sumOf { it.amountInr }
    }
    val totalPlatformWithdrawals = remember(transactions) {
        transactions.filter { it.type == TransactionType.WITHDRAWAL && it.status == TransactionStatus.SUCCESS }.sumOf { it.amountInr }
    }
    val totalUserBalances = remember(allUsers) { allUsers.sumOf { it.availableBalance } }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Analytics Grand Banner
        item {
            GradientCard(
                shape = RoundedCornerShape(20.dp),
                backgroundBrush = Brush.linearGradient(listOf(Color(0xFF271336), Color(0xFF131726), Color(0xFF16233B)))
            ) {
                Text(text = "Total Platform Net Liquidity", color = TextSecondary, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = viewModel.formatInr(totalUserBalances + totalPlatformDeposits),
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(GainGreen))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Fixed 6% Engine Active • Indian UPI Network Synced", color = AccentCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Metrics Grid (4 tiles)
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AdminMetricTile("Total Registered", "${allUsers.size} Users", Icons.Default.People, AccentPink, Modifier.weight(1f))
                AdminMetricTile("Total Deposits", viewModel.formatInr(totalPlatformDeposits), Icons.Default.QrCodeScanner, GainGreen, Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AdminMetricTile("Total Withdrawals", viewModel.formatInr(totalPlatformWithdrawals), Icons.Default.AccountBalance, AccentCyan, Modifier.weight(1f))
                AdminMetricTile("Support Tickets", "${tickets.count { it.status == "OPEN" }} Open", Icons.Default.HeadsetMic, PendingYellow, Modifier.weight(1f))
            }
        }

        // Quick Controls
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Quick Platform Actions", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = BgCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "60S Trading Engine", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Enable or pause 60s trade placement", color = TextMuted, fontSize = 10.sp)
                        }
                        Switch(
                            checked = adminConfig.isTradingEnabled,
                            onCheckedChange = { viewModel.adminToggleFeature("TRADING", it) }
                        )
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = BorderSubtle.copy(alpha = 0.5f))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "UPI QR Recharge Gateway", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Accept instant UPI scanner deposits", color = TextMuted, fontSize = 10.sp)
                        }
                        Switch(
                            checked = adminConfig.isRechargeEnabled,
                            onCheckedChange = { viewModel.adminToggleFeature("RECHARGE", it) }
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// MODULE 1: USER MANAGEMENT
// -------------------------------------------------------------------------------------------------
@Composable
fun AdminUsersModule(
    allUsers: List<User>,
    viewModel: AppViewModel
) {
    var searchQuery by remember { mutableStateOf("") }
    var showAddUserDialog by remember { mutableStateOf(false) }
    var selectedUserForEdit by remember { mutableStateOf<User?>(null) }

    val filtered = remember(allUsers, searchQuery) {
        if (searchQuery.isBlank()) allUsers
        else allUsers.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.uid.contains(searchQuery, ignoreCase = true) ||
            it.mobile.contains(searchQuery, ignoreCase = true)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by UID, Name or Mobile", color = Color(0xFF6B7280), fontSize = 12.sp) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = TextMuted) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { showAddUserDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentPurple)
                ) {
                    Icon(imageVector = Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add", fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        items(filtered) { u ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(14.dp),
                color = BgCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = u.name, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(shape = RoundedCornerShape(4.dp), color = if (u.role == "ADMIN") AccentPink else Color(0xFF1E2845)) {
                                    Text(text = u.role, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                }
                            }
                            Text(text = "${u.uid} • ${u.mobile}", color = TextMuted, fontSize = 11.sp)
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (u.status == "Active") Color(0x3000E676) else Color(0x30FF3366)
                        ) {
                            Text(
                                text = u.status,
                                color = if (u.status == "Active") GainGreen else LossRed,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Available: ${viewModel.formatInr(u.availableBalance)}", color = AccentCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(text = "VIP ${u.vipLevel} • Star-${u.starRating}", color = TextSecondary, fontSize = 11.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedButton(
                            onClick = { selectedUserForEdit = u },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Edit", fontSize = 11.sp)
                        }
                        OutlinedButton(
                            onClick = { viewModel.adminToggleUserStatus(u.uid) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(if (u.status == "Active") "Freeze" else "Unfreeze", fontSize = 11.sp)
                        }
                        Button(
                            onClick = { viewModel.adminDeleteUser(u.uid) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = LossRed.copy(alpha = 0.8f))
                        ) {
                            Text("Delete", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }

    // Add User Dialog
    if (showAddUserDialog) {
        var newName by remember { mutableStateOf("") }
        var newMobile by remember { mutableStateOf("") }
        var newRole by remember { mutableStateOf("USER") }
        var newBalance by remember { mutableStateOf("0") }

        AlertDialog(
            onDismissRequest = { showAddUserDialog = false },
            containerColor = BgCardElevated,
            title = { Text("Register New User", color = Color.White) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = newName, onValueChange = { newName = it }, label = { Text("Full Name") }, singleLine = true)
                    OutlinedTextField(value = newMobile, onValueChange = { newMobile = it }, label = { Text("Mobile (+91)") }, singleLine = true)
                    OutlinedTextField(value = newBalance, onValueChange = { newBalance = it }, label = { Text("Initial Balance (₹)") }, singleLine = true)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val bal = newBalance.toDoubleOrNull() ?: 0.0
                        viewModel.adminAddUser(newName, newMobile, newRole, 1, 0, bal)
                        showAddUserDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentPink)
                ) {
                    Text("Create Account")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddUserDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Edit User Dialog
    if (selectedUserForEdit != null) {
        val u = selectedUserForEdit!!
        var editName by remember { mutableStateOf(u.name) }
        var editMobile by remember { mutableStateOf(u.mobile) }
        var editRole by remember { mutableStateOf(u.role) }
        var editStatus by remember { mutableStateOf(u.status) }

        AlertDialog(
            onDismissRequest = { selectedUserForEdit = null },
            containerColor = BgCardElevated,
            title = { Text("Edit ${u.name} (${u.uid})", color = Color.White) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = editName, onValueChange = { editName = it }, label = { Text("Name") })
                    OutlinedTextField(value = editMobile, onValueChange = { editMobile = it }, label = { Text("Mobile (+91)") })
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Status: ", color = TextSecondary, fontSize = 12.sp)
                        FilterChip(selected = editStatus == "Active", onClick = { editStatus = "Active" }, label = { Text("Active") })
                        Spacer(modifier = Modifier.width(6.dp))
                        FilterChip(selected = editStatus == "Frozen", onClick = { editStatus = "Frozen" }, label = { Text("Frozen") })
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Role: ", color = TextSecondary, fontSize = 12.sp)
                        FilterChip(selected = editRole == "USER", onClick = { editRole = "USER" }, label = { Text("USER") })
                        Spacer(modifier = Modifier.width(6.dp))
                        FilterChip(selected = editRole == "ADMIN", onClick = { editRole = "ADMIN" }, label = { Text("ADMIN") })
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.adminUpdateUser(u.uid, editName, editMobile, editRole, editStatus)
                        selectedUserForEdit = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentPink)
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedUserForEdit = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}

// -------------------------------------------------------------------------------------------------
// MODULE 2: WALLET BALANCES
// -------------------------------------------------------------------------------------------------
@Composable
fun AdminBalancesModule(
    allUsers: List<User>,
    viewModel: AppViewModel
) {
    var selectedUserForAdjust by remember { mutableStateOf<User?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            Text(text = "User Balances Management", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(text = "Direct balance crediting, contract allocation, and adjustments", color = TextMuted, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(10.dp))
        }

        items(allUsers) { u ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(14.dp),
                color = BgCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "${u.name} (${u.uid})", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(text = viewModel.formatInr(u.totalAssets), color = AccentPink, fontSize = 15.sp, fontWeight = FontWeight.Black)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Available: ${viewModel.formatInr(u.availableBalance)}", color = TextSecondary, fontSize = 11.sp)
                        Text(text = "Contract: ${viewModel.formatInr(u.contractAmount)}", color = TextSecondary, fontSize = 11.sp)
                        Text(text = "Bonus: ${viewModel.formatInr(u.bonusAssets)}", color = TextSecondary, fontSize = 11.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { selectedUserForAdjust = u },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Credit / Debit ₹", fontSize = 11.sp)
                        }
                        OutlinedButton(
                            onClick = { viewModel.adminResetBalance(u.uid) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Reset to ₹0", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }

    if (selectedUserForAdjust != null) {
        val u = selectedUserForAdjust!!
        var adjustAmount by remember { mutableStateOf("1000") }
        var isCredit by remember { mutableStateOf(true) }
        var targetWallet by remember { mutableStateOf("AVAILABLE") }
        var remark by remember { mutableStateOf("Administrative Adjustment") }

        AlertDialog(
            onDismissRequest = { selectedUserForAdjust = null },
            containerColor = BgCardElevated,
            title = { Text("Adjust Balances: ${u.name}", color = Color.White) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row {
                        FilterChip(selected = isCredit, onClick = { isCredit = true }, label = { Text("+ Add Funds") })
                        Spacer(modifier = Modifier.width(6.dp))
                        FilterChip(selected = !isCredit, onClick = { isCredit = false }, label = { Text("- Deduct Funds") })
                    }
                    OutlinedTextField(
                        value = adjustAmount,
                        onValueChange = { adjustAmount = it },
                        label = { Text("Amount (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = remark,
                        onValueChange = { remark = it },
                        label = { Text("Audit Remark") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val parsed = adjustAmount.toDoubleOrNull() ?: 0.0
                        val delta = if (isCredit) parsed else -parsed
                        viewModel.adminAdjustWallet(u.uid, availableDelta = delta, contractDelta = 0.0, bonusDelta = 0.0, remark = remark)
                        selectedUserForAdjust = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentPink)
                ) {
                    Text("Apply Adjustment")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedUserForAdjust = null }) { Text("Cancel") }
            }
        )
    }
}

// -------------------------------------------------------------------------------------------------
// MODULE 3: RECHARGE REQUESTS & QR VERIFICATION
// -------------------------------------------------------------------------------------------------
@Composable
fun AdminRechargesModule(
    transactions: List<Transaction>,
    viewModel: AppViewModel
) {
    var showManualCreditDialog by remember { mutableStateOf(false) }

    val recharges = remember(transactions) {
        transactions.filter { it.type == TransactionType.RECHARGE }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Recharge Requests & Payments", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Verify UTRs and approve deposits", color = TextMuted, fontSize = 11.sp)
                }
                Button(
                    onClick = { showManualCreditDialog = true },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GainGreen)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Manual Credit", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        if (recharges.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("No recharge records on platform.", color = TextMuted, fontSize = 13.sp)
                }
            }
        } else {
            items(recharges) { tx ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = BgCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Deposit • ${tx.id}", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(text = viewModel.formatInr(tx.amountInr), color = GainGreen, fontSize = 15.sp, fontWeight = FontWeight.Black)
                        }
                        Text(text = "Method: ${tx.method} • Ref/UTR: ${tx.referenceId}", color = TextMuted, fontSize = 11.sp)
                        Text(text = "Status: ${tx.status.name} • ${tx.remark}", color = TextSecondary, fontSize = 10.sp)

                        if (tx.status == TransactionStatus.PENDING) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { viewModel.adminApprove(tx.id) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = GainGreen),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Verify & Credit", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Button(
                                    onClick = { viewModel.adminReject(tx.id) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = LossRed),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Reject", color = Color.White, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showManualCreditDialog) {
        var userUid by remember { mutableStateOf("INR-829147") }
        var creditAmount by remember { mutableStateOf("5000") }
        var utrNumber by remember { mutableStateOf("") }
        var remark by remember { mutableStateOf("Manual QR Verification") }

        AlertDialog(
            onDismissRequest = { showManualCreditDialog = false },
            containerColor = BgCardElevated,
            title = { Text("Manual Recharge Credit", color = Color.White) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = userUid, onValueChange = { userUid = it }, label = { Text("User UID") })
                    OutlinedTextField(value = creditAmount, onValueChange = { creditAmount = it }, label = { Text("Amount (₹)") })
                    OutlinedTextField(value = utrNumber, onValueChange = { utrNumber = it }, label = { Text("Bank UTR (12 Digits)") })
                    OutlinedTextField(value = remark, onValueChange = { remark = it }, label = { Text("Verification Remark") })
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val parsed = creditAmount.toDoubleOrNull() ?: 0.0
                        viewModel.adminManualCreditRecharge(userUid, parsed, utrNumber, remark)
                        showManualCreditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GainGreen)
                ) {
                    Text("Credit Wallet", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showManualCreditDialog = false }) { Text("Cancel") }
            }
        )
    }
}

// -------------------------------------------------------------------------------------------------
// MODULE 4: WITHDRAWALS
// -------------------------------------------------------------------------------------------------
@Composable
fun AdminWithdrawalsModule(
    adminConfig: AdminAppConfig,
    transactions: List<Transaction>,
    viewModel: AppViewModel
) {
    val withdrawals = remember(transactions) {
        transactions.filter { it.type == TransactionType.WITHDRAWAL }
    }

    var feeInput by remember(adminConfig.withdrawalFeePercent) { mutableStateOf(adminConfig.withdrawalFeePercent.toString()) }
    var startHourInput by remember(adminConfig.withdrawalStartHour) { mutableStateOf(adminConfig.withdrawalStartHour.toString()) }
    var endHourInput by remember(adminConfig.withdrawalEndHour) { mutableStateOf(adminConfig.withdrawalEndHour.toString()) }
    var enforceHours by remember(adminConfig.enforceWithdrawalHours) { mutableStateOf(adminConfig.enforceWithdrawalHours) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Section: Withdrawal Policy & Time Schedule (20% Fee & 9:00 AM - 12:00 AM)
        item {
            Text(text = "Withdrawal Policy & Schedule (विड्रोल नियम व समय)", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Black)
            Text(text = "Configure the 20% deduction charge and daily payout processing window (9:00 AM to 12:00 AM).", color = TextMuted, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = BgCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, GainGreen.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Enforce 9:00 AM - 12:00 AM Schedule", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Users cannot place withdrawals outside this permitted window", color = TextMuted, fontSize = 10.sp)
                        }
                        Switch(checked = enforceHours, onCheckedChange = { enforceHours = it })
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = feeInput,
                            onValueChange = { feeInput = it },
                            label = { Text("Fee Percent (%)") },
                            suffix = { Text("%", color = AccentPink, fontWeight = FontWeight.Bold) },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = startHourInput,
                            onValueChange = { startHourInput = it },
                            label = { Text("Open (e.g. 9 = 9 AM)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = endHourInput,
                            onValueChange = { endHourInput = it },
                            label = { Text("Close (e.g. 24 = 12 AM)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF131828),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(text = "CURRENT ACTIVE POLICY:", color = AccentCyan, fontSize = 10.sp, fontWeight = FontWeight.Black)
                            Text(text = "• Platform Deduction Fee: ${adminConfig.withdrawalFeePercent}% on every withdrawal", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            Text(text = "• Processing Schedule: ${adminConfig.withdrawalStartHour}:00 AM to ${if (adminConfig.withdrawalEndHour >= 24) "12:00 AM (Midnight)" else "${adminConfig.withdrawalEndHour}:00"} Daily", color = GainGreen, fontSize = 11.sp)
                            Text(text = "• Enforcement Status: ${if (adminConfig.enforceWithdrawalHours) "ACTIVE (Blocked outside window)" else "INACTIVE (24/7 allowed)"}", color = TextMuted, fontSize = 10.sp)
                        }
                    }

                    GradientButton(
                        text = "Save Withdrawal Policy & Hours",
                        onClick = {
                            val fee = feeInput.toDoubleOrNull() ?: 20.0
                            val sh = startHourInput.toIntOrNull() ?: 9
                            val eh = endHourInput.toIntOrNull() ?: 24
                            viewModel.adminUpdateWithdrawalSettings(fee, sh, eh, enforceHours)
                        },
                        brush = PrimaryGradient
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
            Text(text = "Pending & Recent Withdrawal Requests", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(text = "Review, payout or reject user withdrawal requests with fee details", color = TextMuted, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(10.dp))
        }

        if (withdrawals.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("No withdrawal requests on platform.", color = TextMuted, fontSize = 13.sp)
                }
            }
        } else {
            items(withdrawals) { tx ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = BgCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Withdrawal • ${tx.id}", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(text = viewModel.formatInr(tx.amountInr), color = AccentPink, fontSize = 15.sp, fontWeight = FontWeight.Black)
                        }
                        Text(text = "Destination: ${tx.method} • Ref: ${tx.referenceId}", color = TextMuted, fontSize = 11.sp)
                        Text(text = "Status: ${tx.status.name} • ${tx.remark}", color = TextSecondary, fontSize = 10.sp)

                        if (tx.status == TransactionStatus.PENDING) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { viewModel.adminApproveWithdrawalWithRef(tx.id, "PAY-" + (100000..999999).random()) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = GainGreen),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Approve Payout", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Button(
                                    onClick = { viewModel.adminReject(tx.id) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = LossRed),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Reject & Refund", color = Color.White, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// MODULE 5: TRADING SETTINGS & PLANS
// -------------------------------------------------------------------------------------------------
@Composable
fun AdminTradingModule(
    adminConfig: AdminAppConfig,
    viewModel: AppViewModel
) {
    val dailySignal by viewModel.dailyOfficialSignal.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val quotes by viewModel.quotes.collectAsState()

    var pairSearchQuery by remember { mutableStateOf("") }
    var showAddPairDialog by remember { mutableStateOf(false) }
    var editingQuote by remember { mutableStateOf<MarketQuote?>(null) }

    var selectedSymbol by remember { mutableStateOf(dailySignal.symbol) }
    var selectedPairName by remember { mutableStateOf(dailySignal.pairDisplayName) }
    var selectedDirection by remember { mutableStateOf(dailySignal.direction) }
    var signalProfitPct by remember { mutableStateOf(dailySignal.profitPercent.toString()) }
    var signalRemark by remember { mutableStateOf(dailySignal.remark) }

    var startHourInput by remember { mutableStateOf(adminConfig.tradingWindowStartHour.toString()) }
    var endHourInput by remember { mutableStateOf(adminConfig.tradingWindowEndHour.toString()) }
    var enforceHours by remember { mutableStateOf(adminConfig.enforceTradingHours) }
    var enforce24h by remember { mutableStateOf(adminConfig.enforce24hTradingLimit) }
    var requireSignalMatch by remember { mutableStateOf(adminConfig.requireSignalMatch) }

    var selectedUidToReset by remember { mutableStateOf(allUsers.firstOrNull()?.uid ?: "INR-829147") }

    val presetSignals = listOf(
        Triple("DOGE", "DOGE/USDT", "Buy up"),
        Triple("BTC", "BTC/INR", "Buy up"),
        Triple("ETH", "ETH/INR", "Buy up"),
        Triple("NIFTY 50", "NIFTY 50", "Buy up"),
        Triple("BNB", "BNB/USDT", "Buy down"),
        Triple("XRP", "XRP/USDT", "Buy up")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Section 1: Daily Trading Signal Publishing
        item {
            Text(text = "1. Daily Official Trading Signal", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Black)
            Text(text = "Publish one daily trading option. All users see and trade only this active signal for the 6% reward.", color = TextMuted, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = BgCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, GainGreen.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Active Signal Status", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Surface(shape = RoundedCornerShape(6.dp), color = GainGreen.copy(alpha = 0.2f)) {
                            Text(text = "LIVE FOR ALL USERS", color = GainGreen, fontSize = 10.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                        }
                    }

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF0F1B18),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GainGreen.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = "CURRENT PUBLISHED SIGNAL: ${dailySignal.pairDisplayName}", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Black)
                            Text(text = "Direction: ${dailySignal.direction} • Reward: +${dailySignal.profitPercent}% • Duration: 60s", color = GainGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Trading Schedule: ${dailySignal.validFromHour}:00 to ${dailySignal.validToHour}:00 Daily", color = AccentCyan, fontSize = 10.sp)
                        }
                    }

                    Text(text = "Select Daily Trading Option to Publish:", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        presetSignals.take(3).forEach { (sym, pair, dir) ->
                            val isSel = selectedSymbol == sym
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        selectedSymbol = sym
                                        selectedPairName = pair
                                        selectedDirection = dir
                                    },
                                color = if (isSel) AccentPink.copy(alpha = 0.25f) else BgDark,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) AccentPink else BorderSubtle)
                            ) {
                                Column(modifier = Modifier.padding(vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = pair, color = if (isSel) AccentPink else Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Text(text = dir, color = if (dir == "Buy up") GainGreen else LossRed, fontSize = 9.sp)
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        presetSignals.takeLast(3).forEach { (sym, pair, dir) ->
                            val isSel = selectedSymbol == sym
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        selectedSymbol = sym
                                        selectedPairName = pair
                                        selectedDirection = dir
                                    },
                                color = if (isSel) AccentPink.copy(alpha = 0.25f) else BgDark,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) AccentPink else BorderSubtle)
                            ) {
                                Column(modifier = Modifier.padding(vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = pair, color = if (isSel) AccentPink else Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Text(text = dir, color = if (dir == "Buy up") GainGreen else LossRed, fontSize = 9.sp)
                                }
                            }
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = selectedPairName,
                            onValueChange = { selectedPairName = it },
                            label = { Text("Option Name") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = signalProfitPct,
                            onValueChange = { signalProfitPct = it },
                            label = { Text("Reward (%)") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = selectedDirection == "Buy up",
                            onClick = { selectedDirection = "Buy up" },
                            label = { Text("Direction: Buy up") },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = selectedDirection == "Buy down",
                            onClick = { selectedDirection = "Buy down" },
                            label = { Text("Direction: Buy down") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = signalRemark,
                        onValueChange = { signalRemark = it },
                        label = { Text("Signal Remark / Description") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    GradientButton(
                        text = "Publish Signal For All Users",
                        onClick = {
                            val profit = signalProfitPct.toDoubleOrNull() ?: 6.0
                            viewModel.adminPublishDailySignal(
                                symbol = selectedSymbol,
                                pairName = selectedPairName,
                                direction = selectedDirection,
                                profitPercent = profit,
                                remark = signalRemark,
                                isActive = true
                            )
                        },
                        brush = PrimaryGradient
                    )
                }
            }
        }

        // Section 2: Trading Schedule & 24h Restriction
        item {
            Spacer(modifier = Modifier.height(18.dp))
            Text(text = "2. Trading Schedule & Limits", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Black)
            Text(text = "Control daily trading window (3:00 PM to 9:00 PM) and once-per-24h restriction.", color = TextMuted, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = BgCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Enforce Trading Hours (3:00 PM - 9:00 PM)", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Trading will strictly close at 9:00 PM and reopen at 3:00 PM", color = TextMuted, fontSize = 10.sp)
                        }
                        Switch(checked = enforceHours, onCheckedChange = { enforceHours = it })
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = startHourInput,
                            onValueChange = { startHourInput = it },
                            label = { Text("Open Hour (24h, default 15 = 3PM)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = endHourInput,
                            onValueChange = { endHourInput = it },
                            label = { Text("Close Hour (24h, default 21 = 9PM)") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    HorizontalDivider(color = BorderSubtle.copy(alpha = 0.5f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "1 Trade per 24 Hours per User", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Restricts each user to only 1 trade in every 24-hour cycle", color = TextMuted, fontSize = 10.sp)
                        }
                        Switch(checked = enforce24h, onCheckedChange = { enforce24h = it })
                    }

                    HorizontalDivider(color = BorderSubtle.copy(alpha = 0.5f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Lock Rewards Exclusively to Daily Signal", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Other options cannot earn promotional trading rewards", color = TextMuted, fontSize = 10.sp)
                        }
                        Switch(checked = requireSignalMatch, onCheckedChange = { requireSignalMatch = it })
                    }

                    GradientButton(
                        text = "Save Schedule & Limits",
                        onClick = {
                            val sh = startHourInput.toIntOrNull() ?: 15
                            val eh = endHourInput.toIntOrNull() ?: 21
                            viewModel.adminUpdateTradingSchedule(
                                startHour = sh,
                                endHour = eh,
                                enforceHours = enforceHours,
                                enforce24hLimit = enforce24h,
                                requireSignalMatch = requireSignalMatch
                            )
                        },
                        brush = PrimaryGradient
                    )
                }
            }
        }

        // Section 3: Reset User 24h Trading Cooldown Tool
        item {
            Spacer(modifier = Modifier.height(18.dp))
            Text(text = "3. Reset User 24h Trading Quota", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Black)
            Text(text = "Manually clear a user's 24-hour limit so they can test/place another trade immediately.", color = TextMuted, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = BgCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(text = "Target User UID to Reset:", color = TextMuted, fontSize = 11.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = selectedUidToReset,
                            onValueChange = { selectedUidToReset = it },
                            label = { Text("User UID") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        Button(
                            onClick = {
                                viewModel.adminResetUserTradeCooldown(selectedUidToReset)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Reset 24h Quota", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Section 4: All Trading Pairs & Markets Control (ट्रेडिंग क्षेत्र के सभी ट्रेड्स का कंट्रोल)
        item {
            Spacer(modifier = Modifier.height(18.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "4. Trade Pairs & Markets Control", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Black)
                    Text(text = "Manage all trading pairs visible in the Trading area. Toggle active/suspended, edit prices, or promote to daily signal.", color = TextMuted, fontSize = 11.sp)
                }
                Button(
                    onClick = { showAddPairDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = GainGreen),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("+ Add Pair", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))

            // Search filter for pairs
            OutlinedTextField(
                value = pairSearchQuery,
                onValueChange = { pairSearchQuery = it },
                placeholder = { Text("Filter trade pairs (e.g. BTC, ETH, NIFTY)", color = TextMuted, fontSize = 12.sp) },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Total Pairs: ${quotes.size} (Active: ${quotes.count { it.isEnabled }}, Paused: ${quotes.count { !it.isEnabled }})",
                color = AccentCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
        }

        val filteredPairs = quotes.filter {
            pairSearchQuery.isBlank() || it.symbol.contains(pairSearchQuery, true) || it.name.contains(pairSearchQuery, true)
        }

        items(filteredPairs) { q ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(14.dp),
                color = BgCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, if (q.isEnabled) BorderSubtle else LossRed.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "${q.symbol}/USDT", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Black)
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (q.isEnabled) GainGreen.copy(alpha = 0.2f) else LossRed.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = if (q.isEnabled) "ACTIVE" else "SUSPENDED",
                                    color = if (q.isEnabled) GainGreen else LossRed,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = if (q.isEnabled) "Enabled" else "Paused", color = TextMuted, fontSize = 11.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Switch(
                                checked = q.isEnabled,
                                onCheckedChange = { viewModel.adminToggleQuote(q.symbol, it) }
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "${q.name} • ${q.category.name}", color = TextMuted, fontSize = 11.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "₹${String.format(Locale.getDefault(), "%,.2f", q.priceInInr)}",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${if (q.change24hPercent >= 0) "+" else ""}${String.format(Locale.getDefault(), "%.2f%%", q.change24hPercent)}",
                                color = if (q.change24hPercent >= 0) GainGreen else LossRed,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.adminPublishDailySignal(
                                    symbol = q.symbol,
                                    pairName = "${q.symbol}/USDT",
                                    direction = "Buy up",
                                    profitPercent = 6.00,
                                    remark = "Admin Daily Signal: ${q.symbol}/USDT (6.00% Promotional Reward)"
                                )
                            },
                            modifier = Modifier.weight(1.3f),
                            colors = ButtonDefaults.buttonColors(containerColor = AccentPink),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("★ Set as Daily Signal", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { editingQuote = q },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = BgCardElevated),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("Edit Price", color = Color.White, fontSize = 11.sp)
                        }

                        IconButton(
                            onClick = { viewModel.adminDeleteQuote(q.symbol) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = "Delete", tint = LossRed, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }

    if (showAddPairDialog) {
        var newSym by remember { mutableStateOf("") }
        var newName by remember { mutableStateOf("") }
        var newPrice by remember { mutableStateOf("") }
        var newChange by remember { mutableStateOf("0.0") }
        var selectedCat by remember { mutableStateOf(MarketCategory.CRYPTO) }

        AlertDialog(
            onDismissRequest = { showAddPairDialog = false },
            title = { Text("Add New Trading Pair", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = newSym, onValueChange = { newSym = it.uppercase() }, label = { Text("Symbol (e.g. SOL)") }, singleLine = true)
                    OutlinedTextField(value = newName, onValueChange = { newName = it }, label = { Text("Full Name (e.g. Solana)") }, singleLine = true)
                    OutlinedTextField(value = newPrice, onValueChange = { newPrice = it }, label = { Text("Price in INR (₹)") }, singleLine = true)
                    OutlinedTextField(value = newChange, onValueChange = { newChange = it }, label = { Text("24h Change % (e.g. 5.2)") }, singleLine = true)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val p = newPrice.toDoubleOrNull() ?: 100.0
                        val c = newChange.toDoubleOrNull() ?: 0.0
                        if (newSym.isNotBlank()) {
                            viewModel.adminAddQuote(newSym, newName, selectedCat, p, c)
                            showAddPairDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GainGreen)
                ) {
                    Text("Add Pair", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddPairDialog = false }) {
                    Text("Cancel", color = TextMuted)
                }
            },
            containerColor = BgCard
        )
    }

    editingQuote?.let { eq ->
        var editPrice by remember(eq) { mutableStateOf(eq.priceInInr.toString()) }
        var editChange by remember(eq) { mutableStateOf(eq.change24hPercent.toString()) }

        AlertDialog(
            onDismissRequest = { editingQuote = null },
            title = { Text("Edit ${eq.symbol}/USDT Price", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Configure live quote price in INR and 24h change for ${eq.name}", color = TextMuted, fontSize = 11.sp)
                    OutlinedTextField(value = editPrice, onValueChange = { editPrice = it }, label = { Text("Price (₹)") }, singleLine = true)
                    OutlinedTextField(value = editChange, onValueChange = { editChange = it }, label = { Text("24h Change %") }, singleLine = true)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val p = editPrice.toDoubleOrNull() ?: eq.priceInInr
                        val c = editChange.toDoubleOrNull() ?: eq.change24hPercent
                        viewModel.adminUpdateQuotePrice(eq.symbol, p, c)
                        editingQuote = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GainGreen)
                ) {
                    Text("Save Price", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingQuote = null }) {
                    Text("Cancel", color = TextMuted)
                }
            },
            containerColor = BgCard
        )
    }
}

// -------------------------------------------------------------------------------------------------
// MODULE 6: REFERRALS & VIP
// -------------------------------------------------------------------------------------------------
@Composable
fun AdminReferralModule(
    adminConfig: AdminAppConfig,
    viewModel: AppViewModel
) {
    var l1Pct by remember { mutableStateOf(adminConfig.l1CommissionPercent.toString()) }
    var l2Pct by remember { mutableStateOf(adminConfig.l2CommissionPercent.toString()) }
    var l3Pct by remember { mutableStateOf(adminConfig.l3CommissionPercent.toString()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            Text(text = "Referral & VIP Commission Settings", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(text = "Configure 3-Level fission commission rates and bonuses", color = TextMuted, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = BgCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(text = "3-Level Fission Commission Rates", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)

                    OutlinedTextField(
                        value = l1Pct,
                        onValueChange = { l1Pct = it },
                        label = { Text("Level 1 (Direct) Commission %") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = l2Pct,
                        onValueChange = { l2Pct = it },
                        label = { Text("Level 2 (Secondary) Commission %") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = l3Pct,
                        onValueChange = { l3Pct = it },
                        label = { Text("Level 3 (Tertiary) Commission %") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    GradientButton(
                        text = "Update Commission Rates",
                        onClick = {
                            val l1 = l1Pct.toDoubleOrNull() ?: 10.0
                            val l2 = l2Pct.toDoubleOrNull() ?: 3.0
                            val l3 = l3Pct.toDoubleOrNull() ?: 1.0
                            viewModel.adminUpdateReferralCommission(l1, l2, l3)
                        },
                        brush = PrimaryGradient
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// MODULE 7: CONTENT & BANNERS
// -------------------------------------------------------------------------------------------------
@Composable
fun AdminContentModule(
    adminConfig: AdminAppConfig,
    viewModel: AppViewModel
) {
    var ticker by remember { mutableStateOf(adminConfig.tickerAnnouncement) }
    var bannerTitle by remember { mutableStateOf(adminConfig.promoBannerTitle) }
    var bannerSubtitle by remember { mutableStateOf(adminConfig.promoBannerSubtitle) }
    var broadcastMsg by remember { mutableStateOf("") }

    var telegramLink by remember { mutableStateOf(adminConfig.telegramChannelLink) }
    var telegramHandle by remember { mutableStateOf(adminConfig.telegramHandle) }
    var telegramTitle by remember { mutableStateOf(adminConfig.telegramTitle) }
    var telegramDesc by remember { mutableStateOf(adminConfig.telegramDescription) }

    LaunchedEffect(adminConfig.telegramChannelLink, adminConfig.telegramHandle) {
        telegramLink = adminConfig.telegramChannelLink
        telegramHandle = adminConfig.telegramHandle
        telegramTitle = adminConfig.telegramTitle
        telegramDesc = adminConfig.telegramDescription
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            Text(text = "App Content, Banners & Notifications", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(text = "Edit homepage marquee, banner copy, Telegram community link, and push announcements", color = TextMuted, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(12.dp))

            // TELEGRAM CHANNEL & SIGNALS LINK CONFIGURATION (NEW REQUIREMENT)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF101D2E),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF0088CC))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0088CC)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Telegram", tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(text = "Official Telegram Signals Channel", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Displayed to users on the Signal tab with direct join link", color = TextMuted, fontSize = 10.sp)
                        }
                    }

                    OutlinedTextField(
                        value = telegramLink,
                        onValueChange = { telegramLink = it },
                        label = { Text("Telegram Channel Link (https://t.me/...)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = telegramHandle,
                        onValueChange = { telegramHandle = it },
                        label = { Text("Telegram Username / ID (@Handle)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = telegramTitle,
                        onValueChange = { telegramTitle = it },
                        label = { Text("Channel Display Title") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = telegramDesc,
                        onValueChange = { telegramDesc = it },
                        label = { Text("Description / Joining Instructions") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )

                    GradientButton(
                        text = "Save Telegram Settings",
                        onClick = {
                            viewModel.adminUpdateTelegramConfig(telegramLink, telegramHandle, telegramTitle, telegramDesc)
                        },
                        brush = Brush.horizontalGradient(listOf(Color(0xFF0088CC), AccentCyan))
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = BgCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(text = "Live Marquee Announcement Ticker", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = ticker,
                        onValueChange = { ticker = it },
                        label = { Text("Ticker Message") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )

                    Text(text = "Homepage Promo Banner", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = bannerTitle,
                        onValueChange = { bannerTitle = it },
                        label = { Text("Banner Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = bannerSubtitle,
                        onValueChange = { bannerSubtitle = it },
                        label = { Text("Banner Subtitle") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    GradientButton(
                        text = "Save Content & Banners",
                        onClick = { viewModel.adminUpdateContent(ticker, bannerTitle, bannerSubtitle) },
                        brush = PrimaryGradient
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = BgCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(text = "Broadcast Instant Push Notification", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = broadcastMsg,
                        onValueChange = { broadcastMsg = it },
                        placeholder = { Text("Enter message for all active users...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                    Button(
                        onClick = {
                            if (broadcastMsg.isNotBlank()) {
                                viewModel.showNotification("BROADCAST: $broadcastMsg")
                                broadcastMsg = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Send Broadcast to All Users")
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// MODULE 8 & 12: FEATURE FLAGS & DYNAMIC FEATURES
// -------------------------------------------------------------------------------------------------
@Composable
fun AdminFeaturesModule(
    adminConfig: AdminAppConfig,
    dynamicFeatures: List<DynamicFeature>,
    viewModel: AppViewModel
) {
    var showAddFeatureDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            Text(text = "Platform Feature Toggles", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(text = "Enable, disable, or provision modular system capabilities", color = TextMuted, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = BgCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    FeatureToggleRow("Trading Engine Active", adminConfig.isTradingEnabled) { viewModel.adminToggleFeature("TRADING", it) }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = BorderSubtle.copy(alpha = 0.5f))
                    FeatureToggleRow("Recharge Payment Gateway", adminConfig.isRechargeEnabled) { viewModel.adminToggleFeature("RECHARGE", it) }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = BorderSubtle.copy(alpha = 0.5f))
                    FeatureToggleRow("Withdrawal Processing", adminConfig.isWithdrawalEnabled) { viewModel.adminToggleFeature("WITHDRAWAL", it) }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = BorderSubtle.copy(alpha = 0.5f))
                    FeatureToggleRow("Referral Fission System", adminConfig.isReferralEnabled) { viewModel.adminToggleFeature("REFERRAL", it) }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = BorderSubtle.copy(alpha = 0.5f))
                    FeatureToggleRow("Strict Trading Hours (3PM - 9PM)", adminConfig.enforceTradingHours) { viewModel.adminToggleFeature("TRADING_HOURS", it) }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = BorderSubtle.copy(alpha = 0.5f))
                    FeatureToggleRow("24-Hour Trade Limit Per User", adminConfig.enforce24hTradingLimit) { viewModel.adminToggleFeature("24H_LIMIT", it) }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = BorderSubtle.copy(alpha = 0.5f))
                    FeatureToggleRow("Official Daily Signal Reward Lock", adminConfig.requireSignalMatch) { viewModel.adminToggleFeature("SIGNAL_LOCK", it) }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = BorderSubtle.copy(alpha = 0.5f))
                    FeatureToggleRow("Platform Maintenance Mode", adminConfig.isMaintenanceMode) { viewModel.adminToggleFeature("MAINTENANCE", it) }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Dynamic Feature Registry", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Button(
                    onClick = { showAddFeatureDialog = true },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentPurple)
                ) {
                    Text("+ Add Feature", fontSize = 11.sp)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(dynamicFeatures) { feat ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                color = BgCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = feat.name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFF1E2845)) {
                                Text(text = feat.category, color = AccentCyan, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                            }
                        }
                        Text(text = feat.description, color = TextMuted, fontSize = 11.sp)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Switch(
                            checked = feat.isEnabled,
                            onCheckedChange = { viewModel.adminToggleDynamicFeature(feat.id, it) }
                        )
                        IconButton(onClick = { viewModel.adminDeleteDynamicFeature(feat.id) }) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = LossRed, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }

    if (showAddFeatureDialog) {
        var featName by remember { mutableStateOf("") }
        var featDesc by remember { mutableStateOf("") }
        var featCat by remember { mutableStateOf("General") }

        AlertDialog(
            onDismissRequest = { showAddFeatureDialog = false },
            containerColor = BgCardElevated,
            title = { Text("Add Dynamic Feature", color = Color.White) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = featName, onValueChange = { featName = it }, label = { Text("Feature Name") })
                    OutlinedTextField(value = featDesc, onValueChange = { featDesc = it }, label = { Text("Description") })
                    OutlinedTextField(value = featCat, onValueChange = { featCat = it }, label = { Text("Category") })
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (featName.isNotBlank()) {
                            viewModel.adminAddDynamicFeature(featName, featDesc, featCat)
                            showAddFeatureDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentPink)
                ) {
                    Text("Register Feature")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddFeatureDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun FeatureToggleRow(title: String, isChecked: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        Switch(checked = isChecked, onCheckedChange = onToggle)
    }
}

// -------------------------------------------------------------------------------------------------
// MODULE 9: SCANNER & QR SETTINGS
// -------------------------------------------------------------------------------------------------
@Composable
fun AdminScannerModule(
    adminConfig: AdminAppConfig,
    viewModel: AppViewModel
) {
    val context = LocalContext.current
    var upiId by remember { mutableStateOf(adminConfig.upiId) }
    var merchantName by remember { mutableStateOf(adminConfig.merchantName) }
    var customTitle by remember { mutableStateOf(adminConfig.activeQrTitle) }
    var selectedQrId by remember { mutableStateOf(adminConfig.activeQrCodeType) }
    var customUrlInput by remember { mutableStateOf("") }

    // Android Zero-Permission Photo Picker for uploading scanner image
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.adminUploadScannerImage(context, uri)
        }
    }

    LaunchedEffect(adminConfig.activeQrCodeType) {
        selectedQrId = adminConfig.activeQrCodeType
        customTitle = adminConfig.activeQrTitle
        upiId = adminConfig.upiId
        merchantName = adminConfig.merchantName
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            Text(text = "Recharge QR Code & Gateway Manager", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black)
            Text(text = "Upload custom scanner images from your device, choose from presets, or customize UPI parameters", color = TextMuted, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(14.dp))

            // 1. CURRENTLY LIVE QR DISPLAY CARD
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = BgCard,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Brush.horizontalGradient(listOf(GainGreen, AccentCyan)))
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
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(GainGreen)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "LIVE ON RECHARGE SCREEN",
                                color = GainGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = AccentPink.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(0.8.dp, AccentPink)
                        ) {
                            Text(
                                text = adminConfig.activeQrCodeType,
                                color = AccentPink,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // QR Preview Thumbnail
                        Surface(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.5.dp, BorderSubtle, RoundedCornerShape(12.dp)),
                            color = Color.White
                        ) {
                            when (adminConfig.activeQrCodeType) {
                                "UPLOADED_CUSTOM" -> {
                                    val localPath = adminConfig.customUploadedQrPath
                                    val webUrl = adminConfig.customUploadedQrUrl
                                    if (!localPath.isNullOrBlank() && File(localPath).exists()) {
                                        AsyncImage(
                                            model = File(localPath),
                                            contentDescription = "Active Uploaded QR",
                                            modifier = Modifier.fillMaxSize().padding(4.dp),
                                            contentScale = ContentScale.Fit
                                        )
                                    } else if (!webUrl.isNullOrBlank()) {
                                        AsyncImage(
                                            model = webUrl,
                                            contentDescription = "Active Uploaded QR",
                                            modifier = Modifier.fillMaxSize().padding(4.dp),
                                            contentScale = ContentScale.Fit
                                        )
                                    } else {
                                        Image(
                                            painter = painterResource(id = R.drawable.scanner_qr_code),
                                            contentDescription = "Fallback QR",
                                            modifier = Modifier.fillMaxSize().padding(4.dp),
                                            contentScale = ContentScale.Fit
                                        )
                                    }
                                }
                                "M_LOGO" -> {
                                    Image(
                                        painter = painterResource(id = R.drawable.scanner_qr_code),
                                        contentDescription = "Active 'M' Logo QR Code",
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(4.dp),
                                        contentScale = ContentScale.Fit
                                    )
                                }
                                "FASTPAY" -> {
                                    Image(
                                        painter = painterResource(id = R.drawable.payment_qr_code),
                                        contentDescription = "Active FastPay QR Code",
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(4.dp),
                                        contentScale = ContentScale.Fit
                                    )
                                }
                                else -> {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.QrCodeScanner,
                                            contentDescription = null,
                                            tint = Color.Black,
                                            modifier = Modifier.size(44.dp)
                                        )
                                        Text(text = "DYNAMIC", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Black)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = adminConfig.activeQrTitle,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Merchant: ${adminConfig.merchantName}",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "UPI VPA: ${adminConfig.upiId}",
                                color = AccentCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 2. UPLOAD SCANNER SECTION (NEW FEATURE REQUEST)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFF141226),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Brush.horizontalGradient(listOf(AccentPink, AccentPurple)))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(PrimaryGradient),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudUpload,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Upload New Scanner QR Image",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "Upload photo/screenshot from gallery to set as the live recharge scanner",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Photo Picker Upload Button
                    Button(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentPink),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Select & Upload Image from Gallery", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // URL Input Option
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = customUrlInput,
                            onValueChange = { customUrlInput = it },
                            label = { Text("Or Paste Image URL (https://...)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        Button(
                            onClick = {
                                if (customUrlInput.isNotBlank()) {
                                    viewModel.adminSetUploadedQrUrl(customUrlInput)
                                    customUrlInput = ""
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AccentCyan),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 14.dp)
                        ) {
                            Text("Apply URL", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    // If an uploaded scanner image exists, show its card here
                    val hasUploaded = !adminConfig.customUploadedQrPath.isNullOrBlank() || !adminConfig.customUploadedQrUrl.isNullOrBlank()
                    if (hasUploaded) {
                        val isCustomActive = adminConfig.activeQrCodeType == "UPLOADED_CUSTOM"
                        Spacer(modifier = Modifier.height(14.dp))
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = if (isCustomActive) Color(0xFF1E1736) else BgDark,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isCustomActive) AccentPink else BorderSubtle)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    color = Color.White
                                ) {
                                    val path = adminConfig.customUploadedQrPath
                                    val url = adminConfig.customUploadedQrUrl
                                    if (!path.isNullOrBlank() && File(path).exists()) {
                                        AsyncImage(
                                            model = File(path),
                                            contentDescription = "Uploaded QR",
                                            modifier = Modifier.fillMaxSize().padding(2.dp),
                                            contentScale = ContentScale.Fit
                                        )
                                    } else if (!url.isNullOrBlank()) {
                                        AsyncImage(
                                            model = url,
                                            contentDescription = "Uploaded QR",
                                            modifier = Modifier.fillMaxSize().padding(2.dp),
                                            contentScale = ContentScale.Fit
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Your Uploaded Scanner",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (!adminConfig.customUploadedQrPath.isNullOrBlank()) "Stored in app internal memory" else "Web URL configured",
                                        color = TextMuted,
                                        fontSize = 10.sp
                                    )
                                }

                                if (isCustomActive) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = GainGreen.copy(alpha = 0.2f),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, GainGreen)
                                    ) {
                                        Text(
                                            text = "ACTIVE",
                                            color = GainGreen,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Black,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }
                                } else {
                                    Button(
                                        onClick = { viewModel.adminSetActiveQrCode("UPLOADED_CUSTOM") },
                                        shape = RoundedCornerShape(6.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = AccentPink),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                        modifier = Modifier.height(30.dp)
                                    ) {
                                        Text("Set Active", fontSize = 10.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.width(6.dp))

                                IconButton(
                                    onClick = { viewModel.adminRemoveUploadedQr() },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete Uploaded QR",
                                        tint = LossRed,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. CHOOSE FROM SYSTEM SCANNER PRESETS
            Text(
                text = "Preset Recharge QR Codes",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Tap 'Set Active' on any QR code below to immediately display it to users",
                color = TextMuted,
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            // OPTION A: Provided Reference QR with 'M' Logo (User requested this specifically)
            val isMLogoActive = adminConfig.activeQrCodeType == "M_LOGO"
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { viewModel.adminSetActiveQrCode("M_LOGO") },
                shape = RoundedCornerShape(14.dp),
                color = if (isMLogoActive) Color(0xFF1B1832) else BgCard,
                border = androidx.compose.foundation.BorderStroke(
                    if (isMLogoActive) 1.5.dp else 1.dp,
                    if (isMLogoActive) AccentPink else BorderSubtle
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        color = Color.White
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.scanner_qr_code),
                            contentDescription = "Provided 'M' Logo QR",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(2.dp),
                            contentScale = ContentScale.Fit
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "1. Official 'M' Logo QR",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFF332014)
                            ) {
                                Text(
                                    text = "USER PROVIDED",
                                    color = Color(0xFFFF9800),
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Uploaded primary merchant QR code with 'M' center logo",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                        Text(
                            text = "UPI: inrexchange.pay@hdfcbank",
                            color = AccentCyan,
                            fontSize = 10.sp
                        )
                    }

                    if (isMLogoActive) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = GainGreen.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GainGreen)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = GainGreen, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("ACTIVE", color = GainGreen, fontSize = 10.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    } else {
                        Button(
                            onClick = { viewModel.adminSetActiveQrCode("M_LOGO") },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AccentPink),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Set Active", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // OPTION B: Dynamic UPI Amount QR
            val isDynamicActive = adminConfig.activeQrCodeType == "DYNAMIC_UPI"
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { viewModel.adminSetActiveQrCode("DYNAMIC_UPI") },
                shape = RoundedCornerShape(14.dp),
                color = if (isDynamicActive) Color(0xFF1B1832) else BgCard,
                border = androidx.compose.foundation.BorderStroke(
                    if (isDynamicActive) 1.5.dp else 1.dp,
                    if (isDynamicActive) AccentCyan else BorderSubtle
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        color = Color.White
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.QrCode, contentDescription = null, tint = Color.Black, modifier = Modifier.size(32.dp))
                            Text("AUTO ₹", color = Color.Black, fontSize = 8.sp, fontWeight = FontWeight.Black)
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "2. Dynamic Amount QR",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Encodes the exact ₹ amount entered by user automatically",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                        Text(
                            text = "Auto UPI: inrexchange.pay@hdfcbank",
                            color = AccentCyan,
                            fontSize = 10.sp
                        )
                    }

                    if (isDynamicActive) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = GainGreen.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GainGreen)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = GainGreen, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("ACTIVE", color = GainGreen, fontSize = 10.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    } else {
                        Button(
                            onClick = { viewModel.adminSetActiveQrCode("DYNAMIC_UPI") },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AccentCyan),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Set Active", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // OPTION C: Secondary FastPay Merchant QR
            val isFastPayActive = adminConfig.activeQrCodeType == "FASTPAY"
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { viewModel.adminSetActiveQrCode("FASTPAY") },
                shape = RoundedCornerShape(14.dp),
                color = if (isFastPayActive) Color(0xFF1B1832) else BgCard,
                border = androidx.compose.foundation.BorderStroke(
                    if (isFastPayActive) 1.5.dp else 1.dp,
                    if (isFastPayActive) AccentPurple else BorderSubtle
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        color = Color.White
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.payment_qr_code),
                            contentDescription = "FastPay QR",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(2.dp),
                            contentScale = ContentScale.Fit
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "3. FastPay Secondary Merchant QR",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "High-speed business collection gateway QR",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                        Text(
                            text = "UPI: inrexchange.merchant@icici",
                            color = AccentCyan,
                            fontSize = 10.sp
                        )
                    }

                    if (isFastPayActive) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = GainGreen.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GainGreen)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = GainGreen, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("ACTIVE", color = GainGreen, fontSize = 10.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    } else {
                        Button(
                            onClick = { viewModel.adminSetActiveQrCode("FASTPAY") },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Set Active", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // OPTION D: Custom UPI VPA Dynamic QR
            val isCustomActive = adminConfig.activeQrCodeType == "CUSTOM_VPA"
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { viewModel.adminSetActiveQrCode("CUSTOM_VPA") },
                shape = RoundedCornerShape(14.dp),
                color = if (isCustomActive) Color(0xFF1B1832) else BgCard,
                border = androidx.compose.foundation.BorderStroke(
                    if (isCustomActive) 1.5.dp else 1.dp,
                    if (isCustomActive) AccentPink else BorderSubtle
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        color = Color.White
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.Tune, contentDescription = null, tint = Color.Black, modifier = Modifier.size(30.dp))
                            Text("CUSTOM", color = Color.Black, fontSize = 8.sp, fontWeight = FontWeight.Black)
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "4. Custom UPI VPA Dynamic QR",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Uses custom UPI VPA configured below in real time",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                        Text(
                            text = "UPI: $upiId",
                            color = AccentCyan,
                            fontSize = 10.sp
                        )
                    }

                    if (isCustomActive) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = GainGreen.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GainGreen)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = GainGreen, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("ACTIVE", color = GainGreen, fontSize = 10.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    } else {
                        Button(
                            onClick = { viewModel.adminSetActiveQrCode("CUSTOM_VPA") },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AccentPink),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Set Active", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. GATEWAY DETAILS CUSTOMIZATION
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = BgCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(text = "Gateway Identity & Parameters", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)

                    OutlinedTextField(
                        value = customTitle,
                        onValueChange = { customTitle = it },
                        label = { Text("Display QR Title (Shown to Users)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = upiId,
                        onValueChange = { upiId = it },
                        label = { Text("Official Recharge UPI ID / VPA") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = merchantName,
                        onValueChange = { merchantName = it },
                        label = { Text("Merchant Brand Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    GradientButton(
                        text = "Save Gateway Configuration",
                        onClick = {
                            viewModel.adminUpdateScannerSettings(
                                activeQrType = adminConfig.activeQrCodeType,
                                upiId = upiId,
                                merchantName = merchantName,
                                customTitle = customTitle
                            )
                        },
                        brush = PrimaryGradient
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// MODULE 11: SECURITY & ADMIN PIN
// -------------------------------------------------------------------------------------------------
@Composable
fun AdminSecurityModule(
    adminConfig: AdminAppConfig,
    viewModel: AppViewModel
) {
    var newPin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            Text(text = "Security, Access & System Maintenance", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(text = "Manage Master Admin PIN, authorization policies, and reset demo data", color = TextMuted, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = BgCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(text = "Change Master Administrator PIN", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)

                    OutlinedTextField(
                        value = newPin,
                        onValueChange = { newPin = it },
                        label = { Text("New PIN (4-8 Digits)") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = confirmPin,
                        onValueChange = { confirmPin = it },
                        label = { Text("Confirm New PIN") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        modifier = Modifier.fillMaxWidth()
                    )

                    GradientButton(
                        text = "Update Master PIN",
                        onClick = {
                            if (newPin.length >= 4 && newPin == confirmPin) {
                                viewModel.adminUpdatePin(newPin)
                                newPin = ""
                                confirmPin = ""
                            } else {
                                viewModel.showNotification("PINs do not match or are too short (min 4 digits)", isError = true)
                            }
                        },
                        brush = PrimaryGradient
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF261217),
                border = androidx.compose.foundation.BorderStroke(1.dp, LossRed.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(text = "Danger Zone: Reset Demo Data", color = LossRed, fontSize = 13.sp, fontWeight = FontWeight.Black)
                    Text(text = "Reset all client wallet balances to strictly ₹0.00 and clear history.", color = TextMuted, fontSize = 11.sp)

                    Button(
                        onClick = { viewModel.adminResetDemoData() },
                        colors = ButtonDefaults.buttonColors(containerColor = LossRed),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Reset All Balances to ₹0.00")
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// LOGS VIEW
// -------------------------------------------------------------------------------------------------
@Composable
fun AdminLogsView(logs: List<String>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            Text(text = "Server Security & Administrative Audit Log", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(text = "Immutable audit trail of all governance operations", color = TextMuted, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(10.dp))
        }

        items(logs) { log ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp),
                shape = RoundedCornerShape(8.dp),
                color = BgCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
            ) {
                Text(
                    text = log,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
    }
}

@Composable
fun AdminMetricTile(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = BgCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = title, color = TextMuted, fontSize = 10.sp)
            Text(text = value, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Black)
        }
    }
}

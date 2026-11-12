package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppViewModel
import java.util.Locale

@Composable
fun WithdrawalScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler { viewModel.navigateBack() }

    val user by viewModel.user.collectAsState()
    val adminConfig by viewModel.adminConfig.collectAsState()
    val isWindowOpen = viewModel.isWithdrawalWindowOpen()

    var amountInput by remember { mutableStateOf("5000") }
    var selectedDestination by remember { mutableStateOf("BANK") } // "BANK" or "UPI"
    var securityPin by remember { mutableStateOf("") }

    val parsedAmount = amountInput.toDoubleOrNull() ?: 0.0
    val feePercent = adminConfig.withdrawalFeePercent
    val feeAmount = parsedAmount * (feePercent / 100.0)
    val netAmount = (parsedAmount - feeAmount).coerceAtLeast(0.0)
    val isAmountValid = parsedAmount >= 500 && parsedAmount <= user.availableBalance
    val isPinValid = securityPin.length == 6

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BgDark)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp)
    ) {
        // Top Nav
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
                    text = "Withdrawal (INR ₹)",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Compliance notice
        item {
            Spacer(modifier = Modifier.height(10.dp))
            DemoWalletNotice()
            Spacer(modifier = Modifier.height(10.dp))

            // Withdrawal Window (9:00 AM to 12:00 AM)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = if (isWindowOpen) Color(0xFF0F1F19) else Color(0xFF261217),
                border = androidx.compose.foundation.BorderStroke(1.2.dp, if (isWindowOpen) GainGreen.copy(alpha = 0.5f) else LossRed.copy(alpha = 0.5f))
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
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(if (isWindowOpen) GainGreen.copy(alpha = 0.2f) else LossRed.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = if (isWindowOpen) GainGreen else LossRed,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Daily Withdrawal Window",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "9:00 AM to 12:00 AM Daily (सुबह 9:00 AM से रात 12:00 AM)",
                                color = if (isWindowOpen) Color(0xFFA5D6A7) else Color(0xFFFFAB91),
                                fontSize = 11.sp
                            )
                        }
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isWindowOpen) GainGreen.copy(alpha = 0.25f) else LossRed.copy(alpha = 0.25f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isWindowOpen) GainGreen else LossRed)
                    ) {
                        Text(
                            text = if (isWindowOpen) "● OPEN NOW" else "● CLOSED",
                            color = if (isWindowOpen) GainGreen else LossRed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Available Balance Card
        item {
            GradientCard(
                shape = RoundedCornerShape(20.dp),
                backgroundBrush = Brush.linearGradient(listOf(Color(0xFF1B233D), Color(0xFF131525)))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Available Withdrawable Balance", color = TextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = viewModel.formatInr(user.availableBalance),
                            color = Color.White,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Button(
                        onClick = { amountInput = user.availableBalance.toInt().toString() },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(text = "All Out", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Destination Selection
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Payout Channel Destination",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Bank Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { selectedDestination = "BANK" },
                shape = RoundedCornerShape(16.dp),
                color = if (selectedDestination == "BANK") Color(0xFF261D38) else BgCard,
                border = androidx.compose.foundation.BorderStroke(1.2.dp, if (selectedDestination == "BANK") AccentPink else BorderSubtle)
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
                                .background(Color(0x309B51E0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.AccountBalance, contentDescription = null, tint = AccentPink, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = "Linked Bank Account", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(text = "${user.bankName} • ${user.bankAccountNumber} (${user.bankIfsc})", color = TextMuted, fontSize = 11.sp)
                        }
                    }
                    RadioButton(
                        selected = selectedDestination == "BANK",
                        onClick = { selectedDestination = "BANK" },
                        colors = RadioButtonDefaults.colors(selectedColor = AccentPink)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // UPI Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { selectedDestination = "UPI" },
                shape = RoundedCornerShape(16.dp),
                color = if (selectedDestination == "UPI") Color(0xFF261D38) else BgCard,
                border = androidx.compose.foundation.BorderStroke(1.2.dp, if (selectedDestination == "UPI") AccentPink else BorderSubtle)
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
                                .background(Color(0x302F80ED)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.QrCode, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = "Direct UPI Transfer", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(text = user.upiId, color = TextMuted, fontSize = 11.sp)
                        }
                    }
                    RadioButton(
                        selected = selectedDestination == "UPI",
                        onClick = { selectedDestination = "UPI" },
                        colors = RadioButtonDefaults.colors(selectedColor = AccentPink)
                    )
                }
            }
        }

        // Amount & PIN inputs
        item {
            Spacer(modifier = Modifier.height(20.dp))
            GradientCard(
                shape = RoundedCornerShape(20.dp),
                backgroundBrush = Brush.verticalGradient(listOf(BgCardElevated, BgCard))
            ) {
                Text(text = "Withdrawal Amount (₹)", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = amountInput,
                    onValueChange = { amountInput = it.filter { c -> c.isDigit() } },
                    label = { Text("Amount to withdraw") },
                    prefix = { Text("₹ ", color = AccentPink, fontWeight = FontWeight.Black, fontSize = 20.sp) },
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Black, color = Color.White),
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = BgDark,
                        unfocusedContainerColor = BgDark,
                        focusedBorderColor = AccentPink,
                        unfocusedBorderColor = BorderSubtle,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(text = "Payment Security PIN", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = securityPin,
                    onValueChange = { if (it.length <= 6 && it.all { c -> c.isDigit() }) securityPin = it },
                    placeholder = { Text("Enter 6-digit PIN (e.g. 123456)", color = TextMuted, fontSize = 13.sp) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = BgDark,
                        unfocusedContainerColor = BgDark,
                        focusedBorderColor = AccentPink,
                        unfocusedBorderColor = BorderSubtle,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Breakdown with 20% Platform Fee
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(BgDark)
                        .padding(14.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Gross Withdrawal Amount", color = TextMuted, fontSize = 12.sp)
                        Text(text = viewModel.formatInr(parsedAmount), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Platform Processing Charge (${adminConfig.withdrawalFeePercent.toInt()}%)", color = Color(0xFFFFB74D), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Text(text = "-${viewModel.formatInr(feeAmount)}", color = Color(0xFFFF5252), fontSize = 12.sp, fontWeight = FontWeight.Black)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    HorizontalDivider(color = BorderSubtle.copy(alpha = 0.6f))
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Net Amount Credited to Bank/UPI", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Black)
                        Text(text = viewModel.formatInr(netAmount), color = GainGreen, fontSize = 15.sp, fontWeight = FontWeight.Black)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF261D15),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x60FF9800))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = Color(0xFFFFB74D), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "विड्रोल करने पर 20% चार्ज काटा जाएगा। समय: सुबह 9:00 AM से रात 12:00 AM।",
                                color = Color(0xFFFFB74D),
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }

        // Submit Button
        item {
            Spacer(modifier = Modifier.height(24.dp))
            val btnText = when {
                !isWindowOpen -> "Withdrawal Closed (Available 9:00 AM - 12:00 AM)"
                parsedAmount <= 0 -> "Enter Amount"
                parsedAmount < 500 -> "Minimum ₹500 Required"
                parsedAmount > user.availableBalance -> "Insufficient Balance"
                !isPinValid -> "Enter 6-Digit PIN"
                else -> "Withdraw ₹${String.format(Locale.getDefault(), "%,.2f", parsedAmount)} (Net: ₹${String.format(Locale.getDefault(), "%,.2f", netAmount)})"
            }
            val canSubmit = isAmountValid && isPinValid && isWindowOpen
            GradientButton(
                text = btnText,
                onClick = {
                    val dest = if (selectedDestination == "BANK") "${user.bankName} (${user.bankAccountNumber})" else "UPI: ${user.upiId}"
                    viewModel.withdraw(parsedAmount, dest)
                },
                brush = if (canSubmit) Brush.horizontalGradient(listOf(AccentBlue, AccentPurple, AccentPink)) else Brush.horizontalGradient(listOf(Color(0xFF374151), Color(0xFF1F2937))),
                enabled = canSubmit
            )
        }
    }
}

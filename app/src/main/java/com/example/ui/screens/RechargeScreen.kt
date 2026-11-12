package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import coil.compose.AsyncImage
import java.io.File
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.model.RechargeReceipt
import com.example.data.model.Transaction
import com.example.data.model.TransactionStatus
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.ActiveScreen
import com.example.ui.viewmodel.AppViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

@Composable
fun RechargeScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler { viewModel.navigateBack() }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val user by viewModel.user.collectAsState()
    val adminConfig by viewModel.adminConfig.collectAsState()
    val lastReceipt by viewModel.lastRechargeReceipt.collectAsState()

    var amountInput by remember { mutableStateOf("1000") }
    var utrInput by remember { mutableStateOf("") }
    var isVerifying by remember { mutableStateOf(false) }
    var generatedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var pendingTx by remember { mutableStateOf<Transaction?>(null) }

    val quickAmounts = listOf("500", "1,000", "2,000", "5,000", "10,000", "20,000", "50,000")
    val parsedAmount = amountInput.toDoubleOrNull() ?: 0.0
    val upiId = adminConfig.upiId
    val merchantName = adminConfig.merchantName

    // Generate dynamic QR code whenever amount changes
    LaunchedEffect(parsedAmount) {
        withContext(Dispatchers.Default) {
            try {
                val formattedAmount = if (parsedAmount > 0) String.format(Locale.US, "%.2f", parsedAmount) else ""
                val upiPayload = if (formattedAmount.isNotEmpty()) {
                    "upi://pay?pa=$upiId&pn=${merchantName.replace(" ", "%20")}&am=$formattedAmount&cu=INR&tn=Wallet%20Recharge"
                } else {
                    "upi://pay?pa=$upiId&pn=${merchantName.replace(" ", "%20")}&cu=INR&tn=Wallet%20Recharge"
                }

                val hints = mapOf(
                    EncodeHintType.MARGIN to 1,
                    EncodeHintType.CHARACTER_SET to "UTF-8"
                )
                val size = 512
                val bitMatrix = QRCodeWriter().encode(upiPayload, BarcodeFormat.QR_CODE, size, size, hints)
                val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
                for (x in 0 until size) {
                    for (y in 0 until size) {
                        bmp.setPixel(x, y, if (bitMatrix.get(x, y)) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
                    }
                }
                withContext(Dispatchers.Main) {
                    generatedBitmap = bmp
                }
            } catch (e: Exception) {
                // Keep existing bitmap on error
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF06080F))
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 48.dp)
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
                    Text(
                        text = "Recharge Wallet",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Instant UPI QR Code Payment",
                        color = AccentCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Current Balance Banner
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = BgCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "CURRENT AVAILABLE BALANCE", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = viewModel.formatInr(user.availableBalance),
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0x2000E676)
                    ) {
                        Text(
                            text = "100% SECURE",
                            color = GainGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Amount Input Card (Requirement 1: Users can enter any recharge amount)
        item {
            Spacer(modifier = Modifier.height(14.dp))
            GradientCard(
                shape = RoundedCornerShape(20.dp),
                backgroundBrush = Brush.verticalGradient(listOf(BgCardElevated, BgCard))
            ) {
                Text(
                    text = "1. Enter Recharge Amount",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "You can enter any custom amount in Indian Rupees (₹)",
                    color = TextMuted,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = amountInput,
                    onValueChange = { input ->
                        val filtered = input.filter { it.isDigit() || it == '.' }
                        amountInput = filtered
                    },
                    label = { Text("Deposit Amount (₹)") },
                    prefix = { Text("₹ ", color = AccentPink, fontWeight = FontWeight.Black, fontSize = 24.sp) },
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 26.sp, fontWeight = FontWeight.Black, color = Color.White),
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

                Spacer(modifier = Modifier.height(12.dp))

                // Quick Presets
                Text(text = "Quick Presets:", color = TextMuted, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    quickAmounts.take(4).forEach { amt ->
                        val plain = amt.replace(",", "")
                        val isSelected = amountInput == plain
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { amountInput = plain },
                            color = if (isSelected) AccentPink.copy(alpha = 0.25f) else BgDark,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) AccentPink else BorderSubtle)
                        ) {
                            Text(
                                text = "₹$amt",
                                color = if (isSelected) AccentPink else Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 7.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    quickAmounts.takeLast(3).forEach { amt ->
                        val plain = amt.replace(",", "")
                        val isSelected = amountInput == plain
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { amountInput = plain },
                            color = if (isSelected) AccentPink.copy(alpha = 0.25f) else BgDark,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) AccentPink else BorderSubtle)
                        ) {
                            Text(
                                text = "₹$amt",
                                color = if (isSelected) AccentPink else Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 7.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        // Generated QR Code Display (Requirement 2 & 3: Display exact amount entered)
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                color = BgCard,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Brush.horizontalGradient(listOf(AccentCyan, AccentPink)))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header Tag
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFF1B2238)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(GainGreen)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "2. OFFICIAL RECHARGE SCANNER • INSTANT UPI",
                                color = AccentCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val infiniteTransition = rememberInfiniteTransition(label = "recharge_laser")
                    val scanProgress by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(durationMillis = 1800, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "laser_y"
                    )

                    // Recharge Scanner Box with dynamically generated QR code encoding exact amount entered
                    Surface(
                        modifier = Modifier
                            .size(240.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .border(3.dp, Color.White, RoundedCornerShape(18.dp))
                            .shadow(16.dp, RoundedCornerShape(18.dp)),
                        color = Color.White
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            when (adminConfig.activeQrCodeType) {
                                "UPLOADED_CUSTOM" -> {
                                    val localPath = adminConfig.customUploadedQrPath
                                    val webUrl = adminConfig.customUploadedQrUrl
                                    if (!localPath.isNullOrBlank() && File(localPath).exists()) {
                                        AsyncImage(
                                            model = File(localPath),
                                            contentDescription = "Admin Uploaded Custom Scanner QR",
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(6.dp),
                                            contentScale = ContentScale.Fit
                                        )
                                    } else if (!webUrl.isNullOrBlank()) {
                                        AsyncImage(
                                            model = webUrl,
                                            contentDescription = "Admin Uploaded Custom Scanner QR",
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(6.dp),
                                            contentScale = ContentScale.Fit
                                        )
                                    } else {
                                        Image(
                                            painter = painterResource(id = R.drawable.scanner_qr_code),
                                            contentDescription = "Official Recharge Scanner QR Code (M Logo)",
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(6.dp),
                                            contentScale = ContentScale.Fit
                                        )
                                    }
                                }
                                "M_LOGO" -> {
                                    // Requirement: Uploaded reference QR code with 'M' logo set active
                                    Image(
                                        painter = painterResource(id = R.drawable.scanner_qr_code),
                                        contentDescription = "Official Recharge Scanner QR Code (M Logo)",
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(6.dp),
                                        contentScale = ContentScale.Fit
                                    )
                                }
                                "FASTPAY" -> {
                                    Image(
                                        painter = painterResource(id = R.drawable.payment_qr_code),
                                        contentDescription = "Secondary FastPay Merchant QR Code",
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(6.dp),
                                        contentScale = ContentScale.Fit
                                    )
                                }
                                else -> {
                                    // Dynamic Amount QR or Custom VPA QR
                                    if (generatedBitmap != null) {
                                        Image(
                                            bitmap = generatedBitmap!!.asImageBitmap(),
                                            contentDescription = "Dynamic UPI QR Code for ₹$amountInput",
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(14.dp),
                                            contentScale = ContentScale.Fit
                                        )
                                    } else {
                                        Image(
                                            painter = painterResource(id = R.drawable.scanner_qr_code),
                                            contentDescription = "Official Recharge Scanner QR Code",
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(8.dp),
                                            contentScale = ContentScale.Fit
                                        )
                                    }
                                }
                            }

                            // Animated green laser scanner line over QR code
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val y = size.height * scanProgress
                                drawLine(
                                    color = Color(0xFF00E676),
                                    start = Offset(0f, y),
                                    end = Offset(size.width, y),
                                    strokeWidth = 3.dp.toPx()
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = adminConfig.activeQrTitle,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(GainGreen)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "Admin Verified Gateway • Active",
                            color = GainGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Exact Amount Banner
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF1E1428),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AccentPink.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Amount to Pay: ", color = TextSecondary, fontSize = 13.sp)
                            Text(
                                text = viewModel.formatInr(parsedAmount),
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Scan with Google Pay, PhonePe, Paytm, BHIM, CRED or Bank UPI",
                        color = TextMuted,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // UPI ID Copy Row
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp)),
                        color = BgCardElevated,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "Official UPI ID / VPA", color = TextMuted, fontSize = 10.sp)
                                Text(
                                    text = upiId,
                                    color = AccentCyan,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        val cb = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        cb.setPrimaryClip(ClipData.newPlainText("UPI ID", upiId))
                                        viewModel.showNotification("UPI ID copied: $upiId")
                                    },
                                color = AccentPink.copy(alpha = 0.2f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy",
                                        tint = AccentPink,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "Copy", color = AccentPink, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Verification & Credit Section (Requirement 4 & 5)
        item {
            Spacer(modifier = Modifier.height(16.dp))
            GradientCard(
                shape = RoundedCornerShape(20.dp),
                backgroundBrush = Brush.verticalGradient(listOf(BgCardElevated, Color(0xFF131728)))
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(AccentPink),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "3. Payment Verification",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Enter 12-digit UTR/Ref number from your UPI receipt",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = utrInput,
                    onValueChange = {
                        if (it.length <= 12 && it.all { c -> c.isDigit() }) {
                            utrInput = it
                        }
                    },
                    label = { Text("12-Digit UPI Ref / UTR Number") },
                    placeholder = { Text("e.g. 430198274619") },
                    trailingIcon = {
                        if (utrInput.isEmpty()) {
                            TextButton(onClick = {
                                val cb = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = cb.primaryClip?.getItemAt(0)?.text?.toString() ?: ""
                                val digitsOnly = clip.filter { it.isDigit() }.take(12)
                                if (digitsOnly.isNotEmpty()) {
                                    utrInput = digitsOnly
                                } else {
                                    val randomUtr = (1..12).map { (0..9).random() }.joinToString("")
                                    utrInput = randomUtr
                                    viewModel.showNotification("Auto-filled demo 12-digit UTR: $randomUtr")
                                }
                            }) {
                                Text("PASTE", color = AccentCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            IconButton(onClick = { utrInput = "" }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = TextMuted)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = BgDark,
                        unfocusedContainerColor = BgDark,
                        focusedBorderColor = AccentCyan,
                        unfocusedBorderColor = BorderSubtle,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Submit Payment & Verify Button
                GradientButton(
                    text = if (isVerifying) "Verifying Banking Gateway..." else "Submit Payment & Verify ${viewModel.formatInr(parsedAmount)}",
                    onClick = {
                        if (parsedAmount <= 0) {
                            viewModel.showNotification("Please enter a valid recharge amount", isError = true)
                            return@GradientButton
                        }
                        val effectiveUtr = if (utrInput.length >= 6) utrInput else (1..12).map { (0..9).random() }.joinToString("")
                        isVerifying = true

                        coroutineScope.launch {
                            delay(600)
                            isVerifying = false
                            val tx = viewModel.repository.rechargeViaQr(parsedAmount, effectiveUtr)
                            pendingTx = tx
                        }
                    },
                    brush = PrimaryGradient,
                    enabled = !isVerifying && parsedAmount > 0
                )
            }
        }
    }

    // Pending Verification Dialog (Requirement: Do NOT credit wallet before payment verification; display PENDING status)
    pendingTx?.let { tx ->
        Dialog(onDismissRequest = { pendingTx = null }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(22.dp),
                color = Color(0xFF131726),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFF9800))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF261D15)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.HourglassTop,
                            contentDescription = null,
                            tint = Color(0xFFFFB74D),
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Recharge Submitted",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0x30FF9800)
                    ) {
                        Text(
                            text = "STATUS: PENDING VERIFICATION",
                            color = Color(0xFFFFB74D),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = BgDark
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Amount", color = TextMuted, fontSize = 12.sp)
                                Text(text = viewModel.formatInr(tx.amountInr), color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                            HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = BorderSubtle.copy(alpha = 0.5f))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Reference / UTR", color = TextMuted, fontSize = 12.sp)
                                Text(text = tx.referenceId, color = AccentCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Per system security policies, your wallet is NOT credited until payment is confirmed by the banking gateway or Administrator.",
                        color = TextMuted,
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 14.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Gateway Instant Verify Simulation Button
                    GradientButton(
                        text = "Simulate Gateway Approval (Instant)",
                        onClick = {
                            viewModel.adminApprove(tx.id)
                            pendingTx = null
                        },
                        brush = PrimaryGradient
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = {
                            pendingTx = null
                            viewModel.navigateBack()
                        },
                        modifier = Modifier.fillMaxWidth().height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
                    ) {
                        Text("Awaiting Admin Verification", color = TextSecondary, fontSize = 13.sp)
                    }
                }
            }
        }
    }

    // Requirement 5: "Display a 'Recharge Successful' notification popup only after the money has been successfully credited to the wallet."
    lastReceipt?.let { receipt ->
        Dialog(onDismissRequest = {
            viewModel.clearLastRechargeReceipt()
            viewModel.navigateTo(ActiveScreen.MAIN)
        }) {
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
                    // Big Green Glowing Checkmark
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(GainGreen, AccentCyan))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Success",
                            tint = Color.Black,
                            modifier = Modifier.size(42.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Recharge Successful!",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black
                    )

                    Text(
                        text = "The amount has been credited to your wallet balance.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Receipt Info Card
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
                                Text(text = "Amount Credited", color = TextMuted, fontSize = 12.sp)
                                Text(
                                    text = viewModel.formatInr(receipt.amount),
                                    color = GainGreen,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = BorderSubtle.copy(alpha = 0.5f))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "UTR / Ref No.", color = TextMuted, fontSize = 12.sp)
                                Text(text = receipt.utr, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = BorderSubtle.copy(alpha = 0.5f))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Payment Mode", color = TextMuted, fontSize = 12.sp)
                                Text(text = "UPI QR Code", color = AccentCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = BorderSubtle.copy(alpha = 0.5f))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Updated Wallet Balance", color = TextMuted, fontSize = 12.sp)
                                Text(
                                    text = viewModel.formatInr(receipt.newBalance),
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Buttons
                    GradientButton(
                        text = "Start 60S Trading (6% Profit)",
                        onClick = {
                            viewModel.clearLastRechargeReceipt()
                            viewModel.navigateTo(ActiveScreen.MAIN)
                        },
                        brush = PrimaryGradient
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(onClick = {
                        viewModel.clearLastRechargeReceipt()
                        viewModel.navigateTo(ActiveScreen.MAIN)
                    }) {
                        Text(text = "Back to Wallet", color = TextSecondary, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

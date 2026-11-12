package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.AppStrings
import com.example.data.model.TeamMember
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.ActiveScreen
import com.example.ui.viewmodel.AppViewModel

@Composable
fun TeamScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val user by viewModel.user.collectAsState()
    val teamMembers by viewModel.teamMembers.collectAsState()
    val language by viewModel.language.collectAsState()
    val context = LocalContext.current

    var selectedTierFilter by remember { mutableIntStateOf(0) } // 0 = All, 1 = T1, 2 = T2, 3 = T3

    val filteredMembers = remember(teamMembers, selectedTierFilter) {
        if (selectedTierFilter == 0) teamMembers
        else teamMembers.filter { it.tier == selectedTierFilter }
    }

    val totalCommission = remember(teamMembers) { teamMembers.sumOf { it.commissionEarnedInr } }
    val totalVolume = remember(teamMembers) { teamMembers.sumOf { it.tradingVolumeInr } }

    val inviteLink = "https://inrexchange.app/register?ref=${user.referralCode}"

    fun copyToClipboard(text: String, label: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        viewModel.showNotification("$label copied to clipboard!")
    }

    fun shareInvite() {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "Join INR EXCHANGE! Use my referral code: ${user.referralCode} to trade in Indian Rupees (₹). Register now: $inviteLink")
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share INR EXCHANGE Invite")
        context.startActivity(shareIntent)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BgDark),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Top Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Referral & Team",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Invite partners, track team performance and tiered rebates",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
        }

        // Referral Code Card
        item {
            GradientCard(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(24.dp),
                backgroundBrush = Brush.linearGradient(
                    listOf(Color(0xFF28183B), Color(0xFF141728), Color(0xFF16253B))
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = AppStrings.get("invitation_code", language),
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = user.referralCode,
                            color = AccentGold,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { copyToClipboard(user.referralCode, "Invitation code") },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = AppStrings.get("copy", language), fontSize = 12.sp)
                        }

                        Button(
                            onClick = { shareInvite() },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentPink),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = AppStrings.get("share", language), fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Link Row
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = BgDark,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = inviteLink,
                            color = TextSecondary,
                            fontSize = 11.sp,
                            maxLines = 1,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy link",
                            tint = AccentCyan,
                            modifier = Modifier
                                .size(18.dp)
                                .clickable { copyToClipboard(inviteLink, "Invitation link") }
                        )
                    }
                }
            }
        }

        // Star Rating & VIP Daily Salary Banner
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .clickable { viewModel.navigateTo(ActiveScreen.SALARY_BONUS_DETAILS) },
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFF1B182B),
                border = androidx.compose.foundation.BorderStroke(1.2.dp, Brush.horizontalGradient(listOf(AccentPurple, AccentPink)))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(AccentPurple, AccentPink))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stars,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Star Rating & VIP Daily Salary",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = AccentPink.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "VIP1-VIP7",
                                        color = AccentPink,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Star-0 to Star-7 (6% - 9%) • Daily salary up to ₹2,00,000",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "View Details",
                        tint = AccentCyan
                    )
                }
            }
        }

        // Team Metrics Grid
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TeamStatCard(
                        title = AppStrings.get("team_size", language),
                        value = "${teamMembers.size + 41}",
                        sub = "Members",
                        icon = Icons.Default.Groups,
                        color = AccentPink,
                        modifier = Modifier.weight(1f)
                    )
                    TeamStatCard(
                        title = AppStrings.get("valid_users", language),
                        value = "${teamMembers.size + 32}",
                        sub = "Active Traders",
                        icon = Icons.Default.VerifiedUser,
                        color = GainGreen,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TeamStatCard(
                        title = "Team Volume",
                        value = viewModel.formatInr(totalVolume + 1500000, includeDecimals = false),
                        sub = "24h Trading",
                        icon = Icons.Default.ShowChart,
                        color = AccentCyan,
                        modifier = Modifier.weight(1f)
                    )
                    TeamStatCard(
                        title = "Earned Rebates",
                        value = viewModel.formatInr(totalCommission + 12000),
                        sub = "Paid in ₹",
                        icon = Icons.Default.MonetizationOn,
                        color = AccentGold,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Tiers Explanation Tabs (T1, T2, T3)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Referral Commission Hierarchy",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TierBadge(title = "All Tiers", isSelected = selectedTierFilter == 0, onClick = { selectedTierFilter = 0 }, modifier = Modifier.weight(1f))
                    TierBadge(title = "T1 Direct (8%)", isSelected = selectedTierFilter == 1, onClick = { selectedTierFilter = 1 }, modifier = Modifier.weight(1f))
                    TierBadge(title = "T2 Sub (4%)", isSelected = selectedTierFilter == 2, onClick = { selectedTierFilter = 2 }, modifier = Modifier.weight(1f))
                    TierBadge(title = "T3 Third (2%)", isSelected = selectedTierFilter == 3, onClick = { selectedTierFilter = 3 }, modifier = Modifier.weight(1f))
                }
            }
        }

        // Member History Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "PARTNER / UID", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text(text = "VOLUME / REBATE (₹)", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Member Rows
        items(filteredMembers) { member ->
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
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(
                                    when (member.tier) {
                                        1 -> Brush.linearGradient(listOf(AccentPink, AccentPurple))
                                        2 -> Brush.linearGradient(listOf(AccentBlue, AccentCyan))
                                        else -> Brush.linearGradient(listOf(AccentOrange, AccentGold))
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "T${member.tier}",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = member.name,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${member.uid} • Joined ${member.joinedDate}",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "+${viewModel.formatInr(member.commissionEarnedInr)}",
                            color = GainGreen,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Vol: ${viewModel.formatInr(member.tradingVolumeInr, includeDecimals = false)}",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TeamStatCard(
    title: String,
    value: String,
    sub: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
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
            Text(text = value, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
            Text(text = sub, color = TextMuted, fontSize = 10.sp)
        }
    }
}

@Composable
fun TierBadge(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) AccentPurple else BgCardElevated,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) AccentPink else BorderSubtle)
    ) {
        Text(
            text = title,
            color = if (isSelected) Color.White else TextSecondary,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            modifier = Modifier.padding(vertical = 8.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

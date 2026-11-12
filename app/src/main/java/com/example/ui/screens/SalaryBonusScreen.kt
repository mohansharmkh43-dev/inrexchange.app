package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GradientButton
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppViewModel

@Composable
fun SalaryBonusScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler { viewModel.navigateBack() }

    val user by viewModel.user.collectAsState()
    val starTiers = viewModel.starRatingTiers
    val vipTiers = viewModel.vipSalaryTiers

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF06080E)),
        contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp)
    ) {
        // Nav Header
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
                        text = "Star Rating & VIP Daily Salary",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Invitation Rewards & Third-Level Fission System",
                        color = Color(0xFFFFD700),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Top Invitation Rewards Banner (Matching reference image)
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF0F1B38),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF2979FF))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "INVITATION REWARDS",
                            color = Color(0xFFFFD700),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(text = "L1: 10%", color = Color(0xFF00E676), fontSize = 15.sp, fontWeight = FontWeight.Black)
                            Text(text = "L2: 3%", color = Color(0xFF29B6F6), fontSize = 15.sp, fontWeight = FontWeight.Black)
                            Text(text = "L3: 1%", color = Color(0xFFFFB74D), fontSize = 15.sp, fontWeight = FontWeight.Black)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF1E88E5)
                    ) {
                        Text(
                            text = "Third-Level Fission",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Daily Salary Claim Card for User
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFF1A1526),
                border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFFFF7A00))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "YOUR VIP SALARY STATUS", color = Color(0xFFFFB800), fontSize = 11.sp, fontWeight = FontWeight.Black)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "VIP ${user.vipLevel} Leader • Star-${user.starRating}",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(Color(0xFFFF9800), Color(0xFFFF3D00)))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.MonetizationOn, contentDescription = null, tint = Color.White, modifier = Modifier.size(26.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Eligible Daily Salary: ₹1,000 / day", color = TextSecondary, fontSize = 12.sp)
                        Text(text = "Active Team: ${user.l1Count + user.l2Count + user.l3Count} users", color = Color(0xFF00E676), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    GradientButton(
                        text = if (user.dailySalaryClaimedToday) "Today's Salary Claimed (₹1,000)" else "Claim Today's Salary (₹1,000)",
                        onClick = { viewModel.claimSalary() },
                        brush = if (user.dailySalaryClaimedToday) Brush.linearGradient(listOf(Color(0xFF2C2F3D), Color(0xFF1E212B))) else Brush.horizontalGradient(listOf(Color(0xFFFF9800), Color(0xFFE91E63))),
                        enabled = !user.dailySalaryClaimedToday
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Star Rating Table (Left table in reference)
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF131722),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF28334E))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Star Rating", color = Color(0xFFFFD700), fontSize = 15.sp, fontWeight = FontWeight.Black)
                            Text(text = "Profit Margin", color = Color(0xFF00E676), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        // Table header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF1E2638))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(text = "Star rating", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            Text(text = "L1 quantity", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                            Text(text = "Daily profit", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                        }

                        // Table rows
                        starTiers.forEachIndexed { i, tier ->
                            val isUserStar = tier.rating == "Star-${user.starRating}"
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(if (isUserStar) Color(0x30FFD700) else if (i % 2 == 1) Color(0xFF10141E) else Color(0xFF151A27))
                                    .padding(horizontal = 10.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = tier.rating + if (isUserStar) " ★" else "", color = if (isUserStar) Color(0xFFFFD700) else Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                                Text(text = "${tier.l1Quantity}", color = TextSecondary, fontSize = 11.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                                Text(text = "${String.format("%.2f", tier.dailyProfitPercent)}%", color = Color(0xFF00E676), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "New users start as 0-star users. After inviting 3 users at level 1, they will automatically be promoted to 1-star users, and their trading profit margin will increase to 6.25%, with a maximum of 9%.",
                            color = Color(0xFFFFB74D),
                            fontSize = 10.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // VIP Daily Salary Table (Right table in reference)
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF18131E),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3E284A))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "VIP Daily Salary Tiers", color = Color(0xFFFF4081), fontSize = 15.sp, fontWeight = FontWeight.Black)
                            Text(text = "Daily Payout", color = Color(0xFFFFD700), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        // Table Header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF2E1C38))
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(text = "VIP Level", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            Text(text = "L1+L2+L3", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                            Text(text = "L1", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.7f), textAlign = TextAlign.Center)
                            Text(text = "Daily Salary", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.3f), textAlign = TextAlign.End)
                        }

                        // Table Rows
                        vipTiers.forEachIndexed { i, v ->
                            val isCurrentVip = v.vipLevel == "VIP${user.vipLevel}"
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(if (isCurrentVip) Color(0x30E91E63) else if (i % 2 == 1) Color(0xFF120E18) else Color(0xFF191322))
                                    .padding(horizontal = 8.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = v.vipLevel + if (isCurrentVip) " ✓" else "", color = if (isCurrentVip) Color(0xFFFF4081) else Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                Text(text = "${v.totalTeamRequired}", color = TextSecondary, fontSize = 11.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                                Text(text = "${v.l1Required}", color = TextSecondary, fontSize = 11.sp, modifier = Modifier.weight(0.7f), textAlign = TextAlign.Center)
                                Text(text = "₹${v.dailySalaryInr.toInt()}", color = Color(0xFFFFD700), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.3f), textAlign = TextAlign.End)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Once the team reaches the required number of Tier 1 users and team members, they can apply for VIP status and become eligible for daily salary payments.",
                            color = Color(0xFFE1BEE7),
                            fontSize = 10.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Third-Level Fission Tree Diagram Description
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF0F1626),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1B2B48))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(text = "Third-Level Fission Network", color = Color(0xFF29B6F6), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "• Level 1 (L1): Users who register via your invitation link belong to your L1 (10% rebate).\n• Level 2 (L2): Users who register via L1 invitation links belong to L2 (3% rebate).\n• Level 3 (L3): Users who register via L2 invitation links belong to L3 (1% rebate).\n• L1, L2, and L3 members all belong to your team.",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}

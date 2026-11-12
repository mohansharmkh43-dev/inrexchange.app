package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.data.model.DailyBonus
import com.example.data.model.PromoTask
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppViewModel

@Composable
fun BonusScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val user by viewModel.user.collectAsState()
    val dailyBonuses by viewModel.dailyBonuses.collectAsState()
    val promoTasks by viewModel.promoTasks.collectAsState()
    val language by viewModel.language.collectAsState()

    val todayBonus = dailyBonuses.find { it.isToday }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BgDark),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Bonus & Rewards Hub",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Complete daily activities and trading tasks to earn INR bonuses",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
        }

        // Overview Card
        item {
            GradientCard(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(24.dp),
                backgroundBrush = Brush.linearGradient(
                    listOf(Color(0xFF331538), Color(0xFF1B132C), Color(0xFF161F33))
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = AppStrings.get("bonus_assets", language),
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = viewModel.formatInr(user.bonusAssets),
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(AccentGold, AccentOrange))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stars,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = AppStrings.get("accumulated_bonus", language), color = TextMuted, fontSize = 11.sp)
                        Text(text = viewModel.formatInr(user.accumulatedBonus), color = AccentGold, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "Bonus Utility", color = TextMuted, fontSize = 11.sp)
                        Text(text = "Offset Trading Margin", color = AccentCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Daily Check-in Streak
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null, tint = AccentPink, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = AppStrings.get("daily_checkin", language),
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "Day 4 of 7",
                        color = AccentGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 7-day row
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(dailyBonuses) { item ->
                        DailyCheckinDayTile(item = item, viewModel = viewModel)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Action button for today
                val isTodayClaimed = todayBonus?.isClaimed == true
                GradientButton(
                    text = if (isTodayClaimed) AppStrings.get("checked_in", language) else AppStrings.get("checkin_btn", language),
                    onClick = { viewModel.claimDailyCheckin() },
                    brush = if (isTodayClaimed) Brush.linearGradient(listOf(Color(0xFF2A2D3C), Color(0xFF1E212E))) else PrimaryGradient,
                    textColor = if (isTodayClaimed) TextMuted else Color.White,
                    enabled = !isTodayClaimed,
                    icon = if (isTodayClaimed) Icons.Default.CheckCircle else Icons.Default.Celebration
                )
            }
        }

        // Tasks & Promotional Offers
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Mission & Promotional Tasks",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Clear trading milestones to receive instant simulated cash credits",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
        }

        // Promo Tasks List
        items(promoTasks) { task ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 5.dp),
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
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (task.isClaimed) Color(0x2000E676) else Color(0x20FF2E93)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (task.isClaimed) Icons.Default.Check else Icons.Default.CardGiftcard,
                                contentDescription = null,
                                tint = if (task.isClaimed) GainGreen else AccentPink,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = task.title,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = task.description,
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "+${viewModel.formatInr(task.rewardInr)}",
                                color = AccentGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Claim / Done button
                    if (task.isClaimed) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0x2000E676)
                        ) {
                            Text(
                                text = "Claimed",
                                color = GainGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    } else {
                        Button(
                            onClick = { viewModel.claimTaskReward(task.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentPink),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Claim",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DailyCheckinDayTile(
    item: DailyBonus,
    viewModel: AppViewModel
) {
    val isToday = item.isToday
    val isClaimed = item.isClaimed

    Surface(
        modifier = Modifier
            .width(62.dp)
            .clip(RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        color = when {
            isClaimed -> Color(0xFF172B20)
            isToday -> Color(0xFF331D38)
            else -> BgCardElevated
        },
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            when {
                isClaimed -> GainGreen
                isToday -> AccentPink
                else -> BorderSubtle
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Day ${item.day}",
                color = if (isToday) AccentPink else TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Icon(
                imageVector = if (isClaimed) Icons.Default.CheckCircle else Icons.Default.MonetizationOn,
                contentDescription = null,
                tint = if (isClaimed) GainGreen else AccentGold,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "₹${item.amountInr.toInt()}",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

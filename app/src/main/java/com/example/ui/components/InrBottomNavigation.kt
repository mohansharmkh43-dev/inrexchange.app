package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainTab

data class NavigationTabItem(
    val tab: MainTab,
    val titleKey: String,
    val icon: ImageVector
)

@Composable
fun InrBottomNavigation(
    currentTab: MainTab,
    onTabSelected: (MainTab) -> Unit,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        NavigationTabItem(MainTab.HOME, "home", Icons.Default.Home),
        NavigationTabItem(MainTab.ASSETS, "assets", Icons.Default.AccountBalanceWallet),
        NavigationTabItem(MainTab.TEAM, "team", Icons.Default.Groups),
        NavigationTabItem(MainTab.SIGNAL, "signal", Icons.Default.Radar),
        NavigationTabItem(MainTab.MINE, "mine", Icons.Default.AccountCircle)
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars),
        color = BgDark,
        tonalElevation = 10.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Brush.verticalGradient(listOf(BgCardElevated, BgCard)))
                .border(1.dp, Color(0x309B51E0), RoundedCornerShape(24.dp))
                .padding(vertical = 6.dp, horizontal = 4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    val isSelected = item.tab == currentTab
                    val iconTint by animateColorAsState(
                        targetValue = if (isSelected) AccentPink else TextMuted,
                        animationSpec = tween(250),
                        label = "iconTint"
                    )

                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onTabSelected(item.tab) }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .then(
                                    if (isSelected) {
                                        Modifier
                                            .clip(CircleShape)
                                            .background(
                                                Brush.radialGradient(
                                                    listOf(Color(0x40FF2E93), Color.Transparent)
                                                )
                                            )
                                    } else Modifier
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = AppStrings.get(item.titleKey, language),
                                tint = iconTint,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Text(
                            text = AppStrings.get(item.titleKey, language),
                            color = if (isSelected) Color.White else TextMuted,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )

                        // Active Indicator Dot
                        Box(
                            modifier = Modifier
                                .padding(top = 2.dp)
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) AccentPink else Color.Transparent)
                        )
                    }
                }
            }
        }
    }
}

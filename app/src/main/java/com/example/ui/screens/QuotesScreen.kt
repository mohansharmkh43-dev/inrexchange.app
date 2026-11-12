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
import com.example.data.model.MarketCategory
import com.example.data.model.MarketQuote
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuotesScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val quotes by viewModel.quotes.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(MarketCategory.ALL) }
    var showOnlyWatchlist by remember { mutableStateOf(false) }

    val filteredList = remember(quotes, searchQuery, selectedCategory, showOnlyWatchlist) {
        quotes.filter { q ->
            val matchesCategory = (selectedCategory == MarketCategory.ALL || q.category == selectedCategory)
            val matchesSearch = (q.symbol.contains(searchQuery, ignoreCase = true) || q.name.contains(searchQuery, ignoreCase = true))
            val matchesWatchlist = if (showOnlyWatchlist) q.isFavorite else true
            matchesCategory && matchesSearch && matchesWatchlist
        }
    }

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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Market Quotes",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Indian Rupee (₹) Live Trading Pairs",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }

                    // Watchlist filter button
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { showOnlyWatchlist = !showOnlyWatchlist },
                        shape = RoundedCornerShape(12.dp),
                        color = if (showOnlyWatchlist) AccentGold else BgCardElevated,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (showOnlyWatchlist) AccentGold else BorderSubtle)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (showOnlyWatchlist) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = "Watchlist",
                                tint = if (showOnlyWatchlist) Color.Black else AccentGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Watchlist",
                                color = if (showOnlyWatchlist) Color.Black else Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by symbol (e.g. BTC, NIFTY, GOLD)", color = TextMuted, fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = AccentPink
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = TextMuted
                                )
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = BgCardElevated,
                        unfocusedContainerColor = BgCard,
                        focusedBorderColor = AccentPink,
                        unfocusedBorderColor = BorderSubtle,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Categories
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(MarketCategory.values()) { cat ->
                        val isSel = cat == selectedCategory
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { selectedCategory = cat },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSel) AccentPurple else BgCardElevated,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) AccentPink else BorderSubtle)
                        ) {
                            Text(
                                text = cat.name.replace("_", " "),
                                color = if (isSel) Color.White else TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                            )
                        }
                    }
                }
            }
        }

        // List Header Columns
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "PAIR / NAME", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.3f))
                Text(text = "TREND (24H)", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text(text = "PRICE (₹) / 24H", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.3f))
            }
        }

        // Quote rows
        if (filteredList.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No matching trading pairs found",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        } else {
            items(filteredList) { quote ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { viewModel.selectQuoteForTrade(quote) },
                    shape = RoundedCornerShape(16.dp),
                    color = BgCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Watchlist star & Name
                        IconButton(
                            onClick = { viewModel.repository.toggleFavoriteQuote(quote.symbol) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = if (quote.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = "Favorite",
                                tint = if (quote.isFavorite) AccentGold else TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Column(modifier = Modifier.weight(1.2f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = quote.symbol,
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black
                                )
                                if (!quote.isEnabled) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Surface(shape = RoundedCornerShape(4.dp), color = Color(0x30FF3366)) {
                                        Text(
                                            text = "PAUSED",
                                            color = LossRed,
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = quote.name,
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }

                        // Sparkline Chart
                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            SparklineChart(
                                data = quote.sparkline,
                                isPositive = quote.change24hPercent >= 0
                            )
                        }

                        // Price & 24h
                        Column(
                            modifier = Modifier.weight(1.3f),
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = viewModel.formatInr(quote.priceInInr),
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            PriceChangeBadge(percent = quote.change24hPercent)
                        }
                    }
                }
            }
        }
    }
}

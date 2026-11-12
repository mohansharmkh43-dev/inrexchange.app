package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Primary Dark Backgrounds
val BgDark = Color(0xFF090A0F)
val BgCard = Color(0xFF12141F)
val BgCardElevated = Color(0xFF1A1D2E)
val BgCardGlass = Color(0xCC161928)
val SurfaceDark = Color(0xFF131522)
val SurfaceDarkVariant = Color(0xFF1E2238)

// Accent Colors
val AccentPink = Color(0xFFFF2E93)
val AccentPurple = Color(0xFF9B51E0)
val AccentBlue = Color(0xFF2F80ED)
val AccentCyan = Color(0xFF00E5FF)
val AccentOrange = Color(0xFFFF7A00)
val AccentGold = Color(0xFFFFB800)

// Financial Status Colors
val GainGreen = Color(0xFF00E676)
val LossRed = Color(0xFFFF3366)
val PendingYellow = Color(0xFFFFC107)

// Text Colors
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFF9E9EB8)
val TextMuted = Color(0xFF6B6E8A)

// Borders & Dividers
val BorderSubtle = Color(0xFF262A42)
val BorderGlow = Color(0x40FF2E93)

// Luxury Gradients
val PrimaryGradient = Brush.horizontalGradient(
    colors = listOf(AccentPink, AccentPurple, AccentBlue)
)

val AccentOrangeGradient = Brush.horizontalGradient(
    colors = listOf(AccentOrange, AccentPink)
)

val CardGlowGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF2A1B3D), Color(0xFF121422))
)

val GoldGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFFFFD700), Color(0xFFFF8C00))
)

val GreenGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFF00E676), Color(0xFF00B0FF))
)

val DarkSurfaceGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF1A1D2E), Color(0xFF0F101A))
)

package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Pure Luxury Light Mode System (iOS 18 Clean Light Frosted Glass & Dynamic Colors)

val LuxuryLightCanvas = Color(0xFFFFFFFF)
val LuxuryLightCanvasAlt = Color(0xFFF8FAFC)
val LuxuryLightSurface = Color(0xFFFFFFFF)
val LuxuryLightCardBg = Color(0xFFFFFFFF)

// Border & Glass lines
val LightGlassBorderStroke = Color(0xFFE2E8F0)
val LightGlassBorderGlow = Color(0xFFCBD5E1)
val LightGlassBorderActive = Color(0xFF10B981)

// Luxury Accents
val LuxuryGold = Color(0xFFD97706)
val LuxuryGoldBg = Color(0xFFFEF3C7)
val LuxuryGoldBorder = Color(0xFFFDE68A)

val LuxuryEmerald = Color(0xFF059669)
val LuxuryEmeraldBright = Color(0xFF10B981)
val LuxuryEmeraldContainer = Color(0xFFD1FAE5)

val LuxuryBlue = Color(0xFF2563EB)
val LuxuryBlueContainer = Color(0xFFDBEAFE)

val LuxuryPurple = Color(0xFF7C3AED)
val LuxuryPurpleContainer = Color(0xFFEDE9FE)

val LuxuryRose = Color(0xFFE11D48)
val LuxuryRoseContainer = Color(0xFFFFE4E6)

val WhatsAppGreen = Color(0xFF25D366)
val WhatsAppGreenContainer = Color(0xFFDCFCE7)

// Text Colors (High Contrast on White)
val LuxuryTextPrimary = Color(0xFF0F172A)     // Deep slate navy
val LuxuryTextSecondary = Color(0xFF475569)   // Medium slate
val LuxuryTextMuted = Color(0xFF94A3B8)       // Light slate

// Guilt status
val GuiltAmber = Color(0xFFD97706)
val GuiltAmberContainer = Color(0xFFFEF3C7)
val GuiltCrimson = Color(0xFFDC2626)
val GuiltCrimsonContainer = Color(0xFFFEE2E2)

// Card & Component Gradients
val LightCardSurfaceGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFFFFFFFF),
        Color(0xFFFBFDFF),
        Color(0xFFF8FAFC)
    )
)

val LightCardBorderGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFFE2E8F0),
        Color(0xFFF1F5F9)
    )
)

val EmeraldCallGradient = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFF059669),
        Color(0xFF10B981),
        Color(0xFF34D399)
    )
)

val GoldAccentGradient = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFFD97706),
        Color(0xFFF59E0B),
        Color(0xFFFBBF24)
    )
)

// Ambient Light Radial Background Glows
val LightAmbientGoldRadial = Color(0xFFF59E0B)
val LightAmbientEmeraldRadial = Color(0xFF10B981)
val LightAmbientBlueRadial = Color(0xFF3B82F6)

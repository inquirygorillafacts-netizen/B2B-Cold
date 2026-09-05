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

// Modern Clean Light Canvas & Elevated Card Tokens
val ModernCanvasBgStart = Color(0xFFF8FAFC)
val ModernCanvasBgMid = Color(0xFFF1F5F9)
val ModernCanvasBgEnd = Color(0xFFE2E8F0)

val ModernCardBorder = Brush.verticalGradient(
    colors = listOf(
        Color(0xFFE2E8F0),
        Color(0xFFCBD5E1)
    )
)

val ModernAvatarGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFF4F46E5), // Indigo
        Color(0xFF2563EB), // Electric Blue
        Color(0xFF06B6D4)  // Cyan
    )
)

// Ultra-Modern Executive Glass & Gradient Tokens
val ExecutiveCanvasBg = Color(0xFFF8FAFC)
val ExecutiveCanvasDark = Color(0xFF0F172A)
val ExecutiveCardSurface = Color(0xFFFFFFFF)
val ExecutiveGlassBorder = Color(0xFFE2E8F0)

// Modern Minimalist Card & Component Tokens
val RomanticChampagneBorder = Color(0xFFE2E8F0)
val RomanticCardSurfaceBg = Color(0xFFFFFFFF)
val RomanticAvatarGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFF4F46E5),
        Color(0xFF3B82F6),
        Color(0xFF06B6D4)
    )
)
val RomanticChampagnePill = Color(0xFFF1F5F9)
val RomanticChampagneText = Color(0xFF475569)
val RomanticTouchpointBg = Color(0xFFF8FAFC)
val RomanticTouchpointText = Color(0xFF0F172A)
val RomanticGoldAccent = Color(0xFFD97706)

val IridescentCardBorder = Brush.linearGradient(
    colors = listOf(
        Color(0xFF38BDF8).copy(alpha = 0.5f),
        Color(0xFF818CF8).copy(alpha = 0.35f),
        Color(0xFF34D399).copy(alpha = 0.45f)
    )
)

val GoldenShimmerGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFFF59E0B),
        Color(0xFFFBBF24),
        Color(0xFFD97706)
    )
)

val ExecutiveAvatarGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFF1E40AF),
        Color(0xFF3B82F6),
        Color(0xFF06B6D4)
    )
)

val HeroCallButtonGradient = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFF059669),
        Color(0xFF10B981),
        Color(0xFF0D9488)
    )
)

// RGB Glass & Futuristic Prismatic Palette
val RgbSpectrumColors = listOf(
    Color(0xFFFF0055), // Vibrant Red-Pink
    Color(0xFF8B5CF6), // Violet
    Color(0xFF3B82F6), // Electric Blue
    Color(0xFF06B6D4), // Cyan
    Color(0xFF10B981), // Emerald
    Color(0xFFF59E0B), // Amber
    Color(0xFFFF0055)  // Back to Red-Pink
)

val RgbGlassBorder = Brush.linearGradient(
    colors = listOf(
        Color(0xFFFF007A).copy(alpha = 0.8f),
        Color(0xFF7928CA).copy(alpha = 0.8f),
        Color(0xFF0070F3).copy(alpha = 0.8f),
        Color(0xFF00DFD8).copy(alpha = 0.8f),
        Color(0xFF10B981).copy(alpha = 0.8f)
    )
)

val RgbGlassCardBg = Brush.verticalGradient(
    colors = listOf(
        Color(0xFFFFFFFF).copy(alpha = 0.92f),
        Color(0xFFF8FAFC).copy(alpha = 0.85f),
        Color(0xFFF1F5F9).copy(alpha = 0.95f)
    )
)

val DarkRgbGlassCardBg = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF131826).copy(alpha = 0.94f),
        Color(0xFF0F172A).copy(alpha = 0.96f),
        Color(0xFF090D16).copy(alpha = 0.98f)
    )
)



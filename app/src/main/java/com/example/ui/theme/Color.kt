package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// ==========================================
// Brand Colors Extracted from Logo RT 04
// ==========================================
val DeepSlateNavy = Color(0xFF134B70)     // Primary Brand & Outlines
val MediumSlateBlue = Color(0xFF3B6790)   // Secondary Accents & Structure
val MintCyan = Color(0xFF86D3B6)          // Highlight & Active States
val SoftMintBackground = Color(0xFFEEF8F6)// Light Scaffold Background
val SoftMintContainer = Color(0xFFC8ECE6) // Soft Mint Tonal Container
val BannerPaleTint = Color(0xFFE8F4F8)    // Subtle Surface Tint

// Neutral & Baseline Light Palette
val OnPrimaryLight = Color(0xFFFFFFFF)
val PrimaryContainerLight = Color(0xFFD0E4FF)
val OnPrimaryContainerLight = Color(0xFF001D36)

val SecondaryLight = MediumSlateBlue
val OnSecondaryLight = Color(0xFFFFFFFF)
val SecondaryContainerLight = BannerPaleTint
val OnSecondaryContainerLight = Color(0xFF0C2033)

val TertiaryLight = Color(0xFF266E54)
val OnTertiaryLight = Color(0xFFFFFFFF)
val TertiaryContainerLight = SoftMintContainer
val OnTertiaryContainerLight = Color(0xFF002115)

val BackgroundLight = SoftMintBackground
val OnBackgroundLight = Color(0xFF131F24)
val SurfaceLight = Color(0xFFFFFFFF)
val OnSurfaceLight = Color(0xFF131F24)
val SurfaceVariantLight = BannerPaleTint
val OnSurfaceVariantLight = Color(0xFF414E53)
val OutlineLight = Color(0xFF717E83)
val OutlineVariantLight = Color(0xFFBFC9CD)

// Dark Theme Palette - Pure Pitch Black (Hitam Pekat #000000)
val PrimaryDark = MintCyan
val OnPrimaryDark = Color(0xFF003827)
val PrimaryContainerDark = Color(0xFF13384D)
val OnPrimaryContainerDark = Color(0xFFBBECE0)

val SecondaryDark = Color(0xFFA6C8FF)
val OnSecondaryDark = Color(0xFF00315B)
val SecondaryContainerDark = Color(0xFF1B3248)
val OnSecondaryContainerDark = Color(0xFFD4E3FF)

val TertiaryDark = Color(0xFF86D3B6)
val OnTertiaryDark = Color(0xFF003827)
val TertiaryContainerDark = Color(0xFF07523C)
val OnTertiaryContainerDark = Color(0xFFA3F2D2)

val BackgroundDark = Color(0xFF000000) // Hitam pekat OLED / Pure Black
val OnBackgroundDark = Color(0xFFF1F5F9)
val SurfaceDark = Color(0xFF0A0D10)     // Deep pitch black surface
val OnSurfaceDark = Color(0xFFF1F5F9)
val SurfaceVariantDark = Color(0xFF121820)
val OnSurfaceVariantDark = Color(0xFF94A3B8)
val OutlineDark = Color(0xFF263238)
val OutlineVariantDark = Color(0xFF161E26)

// Semantic Indicator Colors (Income & Expense)
val IncomeGreen = Color(0xFF16A34A)
val IncomeGreenLight = Color(0xFFDCFCE7)
val IncomeGreenContainer = Color(0xFFDCFCE7)
val ExpenseRed = Color(0xFFDC2626)
val ExpenseRedLight = Color(0xFFFEE2E2)
val ExpenseRedContainer = Color(0xFFFEE2E2)
val PendingAmber = Color(0xFFD97706)
val PendingAmberContainer = Color(0xFFFEF3C7)

// Backward Compatibility Aliases & References
val EmeraldPrimary = DeepSlateNavy
val EmeraldOnPrimary = OnPrimaryLight
val EmeraldPrimaryContainer = PrimaryContainerLight
val EmeraldOnPrimaryContainer = OnPrimaryContainerLight
val EmeraldPrimaryDark = PrimaryDark
val EmeraldOnPrimaryDark = OnPrimaryDark
val EmeraldPrimaryContainerDark = PrimaryContainerDark
val EmeraldOnPrimaryContainerDark = OnPrimaryContainerDark

val TealSecondary = MediumSlateBlue
val TealOnSecondary = OnSecondaryLight
val TealSecondaryContainer = SecondaryContainerLight
val TealOnSecondaryContainer = OnSecondaryContainerLight

val AmberTertiary = PendingAmber
val AmberOnTertiary = Color(0xFFFFFFFF)
val AmberTertiaryContainer = PendingAmberContainer
val AmberOnTertiaryContainer = Color(0xFF78350F)

val PendingYellow = PendingAmber
val PendingYellowLight = PendingAmberContainer

val TextPrimaryLight = OnBackgroundLight
val TextSecondaryLight = OnSurfaceVariantLight
val TextMutedLight = OutlineLight
val TextPrimaryDark = OnBackgroundDark
val TextSecondaryDark = OnSurfaceVariantDark

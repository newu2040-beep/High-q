package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Obsidian & Charcoal Surfaces (Dark Theme)
val HighQDarkBg = Color(0xFF0C0E12)
val HighQSurface = Color(0xFF14171E)
val HighQSurfaceElevated = Color(0xFF1C202B)
val HighQSurfaceCard = Color(0xFF161923)
val HighQBorderSubtle = Color(0xFF262C3A)
val HighQBorderFocused = Color(0xFF3D475C)

// Typography Colors
val HighQTextPrimary = Color(0xFFF7F8FA)
val HighQTextSecondary = Color(0xFF9CA3AF)
val HighQTextMuted = Color(0xFF6B7280)
val HighQTextDisabled = Color(0xFF4B5563)

// Brand Accents
val HighQMint = Color(0xFF65F3BD)
val HighQMintDark = Color(0xFF38D196)
val HighQAqua = Color(0xFF42D3D8)
val HighQPeach = Color(0xFFFFA978)
val HighQOrange = Color(0xFFFF884D)
val HighQGold = Color(0xFFFFC078)

// Gradients
val HighQEnhanceGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFF7EF5C6), Color(0xFFFFC096))
)

val HighQMintAquaGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFF65F3BD), Color(0xFF42D3D8))
)

val HighQDarkOverlayGradient = Brush.verticalGradient(
    colors = listOf(
        Color.Transparent,
        Color(0x660C0E12),
        Color(0xF00C0E12),
        Color(0xFF0C0E12)
    )
)

// Pastel Light Mode (Auth Screen Specifics)
val AuthLightBg = Color(0xFFF8F7F4)
val AuthCardBg = Color(0xFFFFFFFF)
val AuthInputBg = Color(0xFFF2F1EC)
val AuthInputBorder = Color(0xFFE5E3DC)
val AuthTextPrimary = Color(0xFF14161B)
val AuthTextSecondary = Color(0xFF757A88)
val AuthButtonDark = Color(0xFF12141A)

// Pastel Theme 1: Mint Pastel
val PastelMintBg = Color(0xFFF4F8F5)
val PastelMintSurface = Color(0xFFFFFFFF)
val PastelMintCard = Color(0xFFEAF4EE)
val PastelMintBorder = Color(0xFFD3E7DC)
val PastelMintPrimary = Color(0xFF269466)
val PastelMintText = Color(0xFF172B21)
val PastelMintTextSec = Color(0xFF5A7264)

// Pastel Theme 2: Peach Pastel
val PastelPeachBg = Color(0xFFFAF5F2)
val PastelPeachSurface = Color(0xFFFFFFFF)
val PastelPeachCard = Color(0xFFF9EAE1)
val PastelPeachBorder = Color(0xFFEED7C8)
val PastelPeachPrimary = Color(0xFFDE6B35)
val PastelPeachText = Color(0xFF2C1910)
val PastelPeachTextSec = Color(0xFF7A5C4F)

// Pastel Theme 3: Lavender Pastel
val PastelLavenderBg = Color(0xFFF6F4FA)
val PastelLavenderSurface = Color(0xFFFFFFFF)
val PastelLavenderCard = Color(0xFFECE7F6)
val PastelLavenderBorder = Color(0xFFDDD4EE)
val PastelLavenderPrimary = Color(0xFF6B52B5)
val PastelLavenderText = Color(0xFF201732)
val PastelLavenderTextSec = Color(0xFF605477)

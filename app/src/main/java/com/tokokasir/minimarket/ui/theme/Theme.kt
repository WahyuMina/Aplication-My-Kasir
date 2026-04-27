package com.tokokasir.minimarket.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// 1. KITA SETTING ROYAL ONYX SEBAGAI DARK MODE (Karena dasarnya hitam)
private val RoyalOnyxColorScheme = darkColorScheme(
    primary = AccentColor,          // Emas (Untuk tombol utama & aksen)
    onPrimary = Color.Black,        // Teks Hitam di atas tombol Emas (Kontras!)

    secondary = Color(0xFFD4AF37),  // Emas versi lebih kalem
    onSecondary = Color.Black,

    background = IvorySurface,      // Hitam Matte (#121212)
    onBackground = Color.White,     // Teks Putih di atas latar hitam

    surface = DeepPurpleBrand95,    // Hitam Matte sedikit lebih terang (#1E1E1E)
    onSurface = Color.White,

    surfaceVariant = Color(0xFF2C2C2C), // Untuk kartu yang butuh kedalaman
    onSurfaceVariant = Color.LightGray
)

@Composable
fun TokoKasirMiniMarketTheme(
    // Kita paksa menggunakan RoyalOnyxColorScheme agar tetap terlihat mewah
    // baik di mode sistem terang maupun gelap.
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = RoyalOnyxColorScheme,
        typography = Typography, // Menggunakan settingan huruf default
        content = content
    )
}
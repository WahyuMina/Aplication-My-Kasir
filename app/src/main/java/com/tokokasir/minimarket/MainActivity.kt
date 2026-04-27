package com.tokokasir.minimarket

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
// Pastikan import tema yang benar!
import com.tokokasir.minimarket.ui.theme.TokoKasirMiniMarketTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // GUNAKAN TEMA PROFESIONAL BARU KITA
            TokoKasirMiniMarketTheme {
                AppNavigationLayout()
            }
        }
    }
}

@Composable
fun AppNavigationLayout() {
    val context = LocalContext.current
    val database = remember { KasirDatabase.getDatabase(context) }
    val dao = database.kasirDao()

    val viewModel: KasirViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return KasirViewModel(dao) as T
            }
        }
    )

//    var currentScreen by remember { mutableStateOf(2) }

    // 1. Manajer Korsel (Mengatur 4 layar dan mengingat posisi saat ini)
    // Jika ada error merah, tekan Alt+Enter untuk import HorizontalPager
    val pagerState = rememberPagerState(
        initialPage = 2, // Mulai dari Dashboard (indeks ke-2)
        pageCount = { 5 } // Total ada 4 layar
    )

    // 2. Animator (Dibutuhkan untuk memutar animasi geser saat Bottom Nav diklik)
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxWidth(),
        bottomBar = {
            NavigationBar(
                modifier = Modifier.fillMaxWidth(),
                containerColor = Color(0xFF1E1E1E),
                tonalElevation = 8.dp
            ) {
                // ... (Seluruh isi NavigationBarItem TETAP SAMA seperti sebelumnya) ...
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Inventory, contentDescription = null) },
                    label = {Text("Katalog", fontSize = 11.sp,
                        maxLines = 1)},
                    // 1. KABEL LAMPU: Menyala jika halaman saat ini = 0
                    selected = pagerState.currentPage == 0,
                    // 2. KABEL ANIMATOR: Jika diklik, luncurkan layar ke indeks 0
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(0)
                        }
                    },
                    alwaysShowLabel = false, // Hanya teks aktif yang muncul

                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent, // Gelembung menjadi tembus pandang (menyatu dengan canvas)
                        selectedIconColor = MaterialTheme.colorScheme.primary, // Ikon yang dipilih menyala Emas
                        selectedTextColor = MaterialTheme.colorScheme.primary, // Teks yang dipilih menyala Emas
                        unselectedIconColor = Color.Gray, // Ikon yang tidak dipilih berwarna abu-abu redup
                        unselectedTextColor = Color.Gray
                    )
                )
                NavigationBarItem(
                    icon = { Icon(painter = painterResource(id = R.drawable.history_transaction), contentDescription = null) },
                    label = {Text("Riwayat", fontSize = 11.sp,
                        maxLines = 1)},
                    // 1. KABEL LAMPU: Menyala jika halaman saat ini = 0
                    selected = pagerState.currentPage == 1,
                    // 2. KABEL ANIMATOR: Jika diklik, luncurkan layar ke indeks 0
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    },
                    alwaysShowLabel = false,
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent, // Gelembung menjadi tembus pandang (menyatu dengan canvas)
                        selectedIconColor = MaterialTheme.colorScheme.primary, // Ikon yang dipilih menyala Emas
                        selectedTextColor = MaterialTheme.colorScheme.primary, // Teks yang dipilih menyala Emas
                        unselectedIconColor = Color.Gray, // Ikon yang tidak dipilih berwarna abu-abu redup
                        unselectedTextColor = Color.Gray
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = null) },
                    label = {Text("Dashboard", fontSize = 11.sp,
                        maxLines = 1)},
                    // 1. KABEL LAMPU: Menyala jika halaman saat ini = 0
                    selected = pagerState.currentPage == 2,
                    // 2. KABEL ANIMATOR: Jika diklik, luncurkan layar ke indeks 0
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(2)
                        }
                    },
                    alwaysShowLabel = false,
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent, // Gelembung menjadi tembus pandang (menyatu dengan canvas)
                        selectedIconColor = MaterialTheme.colorScheme.primary, // Ikon yang dipilih menyala Emas
                        selectedTextColor = MaterialTheme.colorScheme.primary, // Teks yang dipilih menyala Emas
                        unselectedIconColor = Color.Gray, // Ikon yang tidak dipilih berwarna abu-abu redup
                        unselectedTextColor = Color.Gray
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.PointOfSale, contentDescription = null) },
                    label = {Text("kasir", fontSize = 11.sp,
                        maxLines = 1)},
                    // 1. KABEL LAMPU: Menyala jika halaman saat ini = 0
                    selected = pagerState.currentPage == 3,
                    // 2. KABEL ANIMATOR: Jika diklik, luncurkan layar ke indeks 0
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(3)
                        }
                    },
                    alwaysShowLabel = false,
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent, // Gelembung menjadi tembus pandang (menyatu dengan canvas)
                        selectedIconColor = MaterialTheme.colorScheme.primary, // Ikon yang dipilih menyala Emas
                        selectedTextColor = MaterialTheme.colorScheme.primary, // Teks yang dipilih menyala Emas
                        unselectedIconColor = Color.Gray, // Ikon yang tidak dipilih berwarna abu-abu redup
                        unselectedTextColor = Color.Gray
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = {Text("Pengaturan", fontSize = 11.sp,
                        maxLines = 1)},
                    // 1. KABEL LAMPU: Menyala jika halaman saat ini = 0
                    selected = pagerState.currentPage == 4,
                    // 2. KABEL ANIMATOR: Jika diklik, luncurkan layar ke indeks 0
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(4)
                        }
                    },
                    alwaysShowLabel = false,
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent, // Gelembung menjadi tembus pandang (menyatu dengan canvas)
                        selectedIconColor = MaterialTheme.colorScheme.primary, // Ikon yang dipilih menyala Emas
                        selectedTextColor = MaterialTheme.colorScheme.primary, // Teks yang dipilih menyala Emas
                        unselectedIconColor = Color.Gray, // Ikon yang tidak dipilih berwarna abu-abu redup
                    )
                )
            }
        }
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.padding(innerPadding).fillMaxSize()
        ) { page ->
            // Menampilkan layar sesuai indeks halaman yang sedang dilihat
            when (page) {
                0 -> KatalogScreen(viewModel)
                1 -> RiwayatScreen(viewModel)
                2 -> DashboardScreen(viewModel)
                3 -> KasirScreen(viewModel)
                4 -> AkunScreen(viewModel)
            }
        }
    }
}

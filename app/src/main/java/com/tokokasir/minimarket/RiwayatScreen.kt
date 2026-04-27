package com.tokokasir.minimarket

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState // <-- IMPORT BARU INI WAJIB ADA
import androidx.compose.runtime.getValue     // <-- IMPORT BARU INI WAJIB ADA
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiwayatScreen(viewModel: KasirViewModel) {
    // 1. PERBAIKAN: Tangkap aliran data dari Database menggunakan collectAsState()
    val history by viewModel.transactionHistory.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            modifier = Modifier.height(80.dp),
            title = { Text("Riwayat Transaksi", color = Color(0xFFD4AF37), fontSize = 20.sp) },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background, // Akan otomatis jadi Hitam Pekat
                titleContentColor = MaterialTheme.colorScheme.primary // Akan otomatis jadi Emas
            )
        )

        if (history.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("Belum ada transaksi hari ini", color = Color.Gray)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(history) { trx ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Baris Atas: ID dan Tanggal
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(trx.id, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(trx.date, fontSize = 12.sp, color = Color.Gray)
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                            // Baris Tengah: Harga
                            Text("Total Belanja: Rp ${trx.totalAmount}", fontWeight = FontWeight.Bold, color = Color(0xFFFF8C00))
                            Text("Tunai: Rp ${trx.cashAmount} | Kembali: Rp ${trx.changeAmount}", fontSize = 12.sp)

                            Spacer(modifier = Modifier.height(8.dp))

                            // 2. PERBAIKAN: Langsung panggil itemsSummary (Kode joinToString dihapus)
                            Text("Barang: ${trx.itemsSummary}", fontSize = 12.sp, color = Color.DarkGray)
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(80.dp))
    }
}
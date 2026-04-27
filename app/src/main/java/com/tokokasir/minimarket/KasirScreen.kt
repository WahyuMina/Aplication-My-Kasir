package com.tokokasir.minimarket

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.ToneGenerator
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.ViewWeek
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KasirScreen(viewModel: KasirViewModel) {
    val context = LocalContext.current
    var barcodeInput by remember { mutableStateOf("") }
    var showScanner by remember { mutableStateOf(false) }

    // 2. KITA HAPUS DATA LIST MANUAL! Sekarang kita baca langsung dari Koki (ViewModel)
    val cartItems = viewModel.cartItems
    val totalHarga = viewModel.calculateTotal() // Hitung total langsung minta ke Koki

    var showPaymentDialog by remember { mutableStateOf(false) }
    var cashInput by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var changeAmount by remember { mutableStateOf(0) }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            showScanner = true
        } else {
            println("Izin Kamera Ditolak")
        }
    }

    // MENGGUNAKAN COLUMN UTAMA SEBAGAI PENGGANTI SCAFFOLD LAMA
    Column(modifier = Modifier.fillMaxSize()) {

        // 1. HEADER ATAS
        TopAppBar(
            modifier = Modifier.height(80.dp),
            title = { Text("Toko Kasir MiniMarket", color = Color(0xFFD4AF37), fontSize = 20.sp) },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background, // Akan otomatis jadi Hitam Pekat
                titleContentColor = MaterialTheme.colorScheme.primary // Akan otomatis jadi Emas
            )
        )

        // 2. AREA DAFTAR BARANG (Di Tengah)
        // weight(1f) membuat area ini mengambil sisa ruang antara Header dan area Checkout
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (cartItems.isEmpty()) {
                Text(
                    text = "Belum ada barang. Silahkan Scan!",
                    modifier = Modifier.align(Alignment.Center), color = Color.Gray
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = cartItems,
                        key = { it.timestampId }
                    ) { produk ->
                        var isDismissed by remember { mutableStateOf(false) }
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = { value ->
                                if (value == SwipeToDismissBoxValue.StartToEnd) {
                                    isDismissed = true
                                    true
                                } else {
                                    false
                                }
                            }
                        )

                        LaunchedEffect(isDismissed) {
                            if (isDismissed) {
                                kotlinx.coroutines.delay(300)
                                viewModel.removeFromCart(produk)
                            }
                        }

                        Column {
                            AnimatedVisibility(
                                visible = cartItems.contains(produk),
                                exit = fadeOut(animationSpec = tween(300)) + shrinkVertically()
                            ) {
                                SwipeToDismissBox(
                                    state = dismissState,
                                    backgroundContent = {
                                        val color =
                                            if (dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd)
                                                Color.Red else Color.Transparent

                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(color, shape = RoundedCornerShape(8.dp))
                                                .padding(horizontal = 20.dp),
                                            contentAlignment = Alignment.CenterStart
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Hapus",
                                                tint = Color.White
                                            )
                                        }
                                    },
                                    enableDismissFromStartToEnd = true,
                                    enableDismissFromEndToStart = false
                                ) {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(1f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    produk.name,
                                                    fontWeight = FontWeight.Bold,
                                                    maxLines = 1
                                                )
                                                Text(
                                                    "Barcode: ${produk.barcode}",
                                                    fontSize = 12.sp,
                                                    color = Color.Gray
                                                )
                                                Text(
                                                    "Rp ${produk.price}",
                                                    color = Color(0xFFD4AF37),
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(
                                                    text = "${produk.quantity}",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 22.sp,
                                                    modifier = Modifier.padding(bottom = 2.dp)
                                                )
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                                ) {
                                                    IconButton(
                                                        onClick = {
                                                            viewModel.decreaseCartItem(produk)
                                                        },
                                                        modifier = Modifier.size(48.dp)
                                                    ) {
                                                        Icon(
                                                            Icons.Default.Remove,
                                                            contentDescription = null,
                                                            tint = Color.Gray,
                                                            modifier = Modifier.size(24.dp)
                                                        )
                                                    }

                                                    IconButton(
                                                        onClick = {
                                                            viewModel.addToCart(produk)
                                                        },
                                                        modifier = Modifier.size(48.dp)
                                                    ) {
                                                        Icon(
                                                            Icons.Default.Add,
                                                            contentDescription = null,
                                                            tint = Color(0xFFD4AF37),
                                                            modifier = Modifier.size(24.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. AREA CHECKOUT BAWAH
        Surface(
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Input Barcode & Tambah
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = barcodeInput,
                        onValueChange = { barcodeInput = it },
                        label = { Text("Masukkan Barcode") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        leadingIcon = {
                            Icon(Icons.Default.ViewWeek, contentDescription = "Barcode Icon", tint = Color.Gray)
                        },
                        trailingIcon = {
                            IconButton(onClick = {
                                val permissionCheckResult = ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA)
                                if (permissionCheckResult == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                                    showScanner = true
                                } else {
                                    cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                                }
                            }) {
                                Icon(Icons.Default.PhotoCamera, contentDescription = "Scan", tint = Color(0xFFD4AF37))
                            }
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val foundProduct = viewModel.findProductByBarcode(barcodeInput)
                            if (foundProduct != null) {
                                viewModel.addToCart(foundProduct)
                                barcodeInput = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD4AF37)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(56.dp)
                    ) {
                        Text("TAMBAH")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Total Harga & Bayar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Total Bayar", fontSize = 14.sp)
                        AnimatedContent(targetState = totalHarga) { targetTotal ->
                            Text(
                                text = "Rp $targetTotal",
                                fontSize = 24.sp,
                                color = Color(0xFFD4AF37),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Button(
                        onClick = {
                            if (cartItems.isNotEmpty()){
                                showPaymentDialog = true
                            }
                        },
                        modifier = Modifier.height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD4AF37))
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null )
                        Spacer(Modifier.width(8.dp))
                        Text("BAYAR")
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }

    // LOGIKA POP-UP PEMBAYARAN
    if (showPaymentDialog) {
        val cash = cashInput.toIntOrNull() ?: 0
        val isUangKurang = cash < totalHarga && cashInput.isNotEmpty()

        AlertDialog(
            onDismissRequest = { showPaymentDialog = false },
            title = { Text("Proses Pembayaran", fontWeight = FontWeight.Bold)},
            text = {
                Column{
                    Text("Total Belanja: Rp. $totalHarga", fontSize = 18.sp,
                        color = if (isUangKurang) Color.Red else Color(0xFFD4AF37), fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = cashInput,
                        onValueChange = {cashInput = it},
                        label = { Text("Uang Tunai (Rp)")},
                        singleLine = true,
                        isError = isUangKurang,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                        ),
                        supportingText = {
                            if (isUangKurang) {
                                Text ("Uang Tidak Cukup", color = Color.Red)
                            }
                        }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (cash >= totalHarga) {
                            changeAmount = cash - totalHarga
                            showPaymentDialog = false
                            showSuccessDialog = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (cash >= totalHarga) Color(0xFFD4AF37) else Color.Gray
                    ),
                    enabled = cash >= totalHarga
                ) {
                    Text("Proses")
                }
            },
            dismissButton = {
                TextButton(onClick = {showPaymentDialog = false}) {
                    Text("Batal", color = Color.Gray)
                }
            }
        )
    }

    // LOGIKA POP-UP SUKSES & KEMBALIAN
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
            },
            confirmButton = {
                // Kita bungkus dengan Column agar bisa menaruh 2 tombol atas-bawah
                Column(modifier = Modifier.fillMaxWidth()) {

                    // --- TOMBOL 1: OPSI BAGIKAN STRUK (Hanya ditekan jika diminta pelanggan) ---
                    OutlinedButton(
                        onClick = {
                            bagikanStrukDigital(
                                context = context,
                                namaToko = viewModel.namaToko.value,
                                daftarBelanja = viewModel.cartItems,
                                totalHarga = totalHarga
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Saya tambahkan ikon Share agar terlihat profesional
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = Color(0xFFD4AF37))
                        Spacer(Modifier.width(8.dp))
                        Text("BAGIKAN STRUK", color = Color(0xFFD4AF37))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // --- TOMBOL 2: SELESAI TRANSAKSI (Tombol Utama) ---
                    Button(
                        onClick = {
                            // Di sini MURNI hanya untuk menyimpan dan mereset layar
                            val cash = cashInput.toIntOrNull() ?: 0
                            viewModel.saveTransaction(cash = cash, change = changeAmount)
                            showSuccessDialog = false
                            cashInput = ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD4AF37))
                    ) {
                        Text("SELESAI & ANTRIAN BARU", color = Color.Black)
                    }
                }
            },title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(viewModel.namaToko.value, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text(viewModel.alamatToko.value, fontSize = 12.sp, color = Color.Gray)
                    Text("---------------------------------", color = Color.LightGray)
                }
            },
            text = {
                Column {
                    // Daftar barang ringkas
                    viewModel.cartItems.forEach {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${it.name} x${it.quantity}", fontSize = 14.sp)
                            Text("Rp ${it.price * it.quantity}", fontSize = 14.sp)
                        }
                    }
                    Text(
                        "---------------------------------",
                        color = Color.LightGray,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("TOTAL", fontWeight = FontWeight.Bold)
                        Text("Rp $totalHarga", fontWeight = FontWeight.Bold)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("TUNAI", fontSize = 14.sp)
                        Text("Rp $cashInput", fontSize = 14.sp)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "KEMBALI",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD4AF37)
                        )
                        Text(
                            "Rp $changeAmount",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF8C00)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Kasir: ${viewModel.namaKasir.value}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Text(
                            viewModel.pesanStruk.value,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        )
    }

    // LOGIKA POP-UP KAMERA
    if (showScanner){
        AlertDialog(
            onDismissRequest = {showScanner = false},
            confirmButton = {
                TextButton(
                    onClick = { showScanner = false },
                    modifier = Modifier.padding(top = 0.dp)
                ) {
                    Text("TUTUP", fontWeight = FontWeight.Bold)
                }
            },
            title = {Text("Scan Barcode Produk")},
            text = {
                Box(modifier = Modifier.size(300.dp).clipToBounds()) {
                    BarcodeScannerView(
                        onBarcodeDetected = {code ->
                            val foundProduct = viewModel.findProductByBarcode(code)
                            val toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
                            toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 150)

                            if (foundProduct != null) {
                                viewModel.addToCart(foundProduct)
                                } else {
                                    barcodeInput = code
                                }
                            showScanner = false
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        )
    }
}

// Fungsi ini merakit data menjadi teks mirip struk, lalu membuka menu Share HP
fun bagikanStrukDigital(context: Context, namaToko: String, daftarBelanja: List<Product>, totalHarga: Int) {
    // 1. Merakit Teks Struk
    val tanggal = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("id", "ID")).format(Date())

    val teksStruk = java.lang.StringBuilder().apply {
        append("🛒 *STRUK PEMBELIAN* 🛒\n")
        append("🏢 *$namaToko*\n")
        append("📅 $tanggal\n")
        append("---------------------------------\n")

        // Looping semua barang yang dibeli
        daftarBelanja.forEach { barang ->
            append("▪️ ${barang.name}\n")
            append("   ${barang.quantity} x Rp ${barang.price} = Rp ${barang.price * barang.quantity}\n")
        }

        append("---------------------------------\n")
        append("💰 *TOTAL: Rp $totalHarga*\n")
        append("---------------------------------\n")
        append("🙏 Terima kasih telah berbelanja!\n")
        append("📝 _Dicetak otomatis menggunakan TokoKasir App_")
    }.toString()

    // 2. Memanggil Sistem Android untuk Share (Membuka WhatsApp/Telegram)
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, teksStruk)
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, "Bagikan Struk ke Pelanggan:")
    context.startActivity(shareIntent)
}
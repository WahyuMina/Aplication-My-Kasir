package com.tokokasir.minimarket

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation.Companion.keyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay // Penting untuk animasi jeda

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KatalogScreen(viewModel: KasirViewModel) {
    val productList by viewModel.productList.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.height(80.dp),
                title = {
                    Text(
                        text = "Katalog Barang",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary, // Emas
                contentColor = Color.Black,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp),
                modifier = Modifier.offset(x = -10.dp, y = 20.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Barang")
            }
        },
        // Tombol tetap di posisi bawah tengah yang kamu sukai
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            if (productList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Gudang masih kosong", color = Color.Gray)
                }
            } else {
                // LAZYCOLUMN: Otomatis bisa di-scroll ke bawah jika produk banyak
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 100.dp, start = 16.dp, end = 16.dp, top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = productList,
                        key = { it.barcode }
                    ) { product ->

                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = { dismissValue ->
                                dismissValue == SwipeToDismissBoxValue.StartToEnd
                            }
                        )

                        // --- LOGIKA ANIMASI JEDA (THE PAUSE) ---
                        // Efek ini akan berjalan saat status kartu berubah menjadi 'Dismissed'
                        LaunchedEffect(dismissState.currentValue) {
                            if (dismissState.currentValue == SwipeToDismissBoxValue.StartToEnd) {
                                delay(500) // Jeda 0.5 detik agar animasi swipe selesai sempurna
                                viewModel.deleteProductFromDatabase(product)
                            }
                        }

                        val iconScale by animateFloatAsState(
                            targetValue = if (dismissState.targetValue == SwipeToDismissBoxValue.StartToEnd) 1.3f else 0.8f,
                            label = "SkalaIkon"
                        )

                        SwipeToDismissBox(
                            state = dismissState,
                            enableDismissFromEndToStart = false,
                            enableDismissFromStartToEnd = true,
                            modifier = Modifier.animateItem(),
                            backgroundContent = {
                                val color by animateColorAsState(
                                    targetValue = if (dismissState.targetValue == SwipeToDismissBoxValue.StartToEnd) {
                                        Color(0xFFB71C1C) // Merah Gelap
                                    } else {
                                        Color.Transparent
                                    }, label = "WarnaHapus"
                                )

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(color, RoundedCornerShape(12.dp))
                                        .padding(start = 24.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Hapus",
                                        tint = Color.White,
                                        modifier = Modifier.scale(iconScale)
                                    )
                                }
                            },
                            content = {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    shape = RoundedCornerShape(12.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(product.name, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp)
                                            Text("Barcode: ${product.barcode}", color = Color.Gray, fontSize = 12.sp)
                                            Text("Rp ${product.price}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // --- DIALOG TAMBAH BARANG (Tetap Sama) ---
    if (showAddDialog) {
        var newBarcode by remember { mutableStateOf("") }
        var newName by remember { mutableStateOf("") }
        var newPrice by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        if (newBarcode.isNotBlank() && newName.isNotBlank() && newPrice.isNotBlank()) {
                            viewModel.addNewProduct(newBarcode, newName, newPrice.toIntOrNull() ?: 0)
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) { Text("SIMPAN", color = Color.Black) }
            },
            title = { Text("Tambah Barang Baru", color = MaterialTheme.colorScheme.onSurface) },
            containerColor = MaterialTheme.colorScheme.surface,
            text = {
                Column {
                    OutlinedTextField(value = newName, onValueChange = { newName = it }, label = { Text("Nama Barang") })
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = newBarcode, onValueChange = { newBarcode = it }, label = { Text("Barcode") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = newPrice, onValueChange = { newPrice = it }, label = { Text("Harga")
                    }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }
        )
    }
}
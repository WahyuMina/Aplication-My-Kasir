package com.tokokasir.minimarket

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AkunScreen(viewModel: KasirViewModel) {
    val context = LocalContext.current

    var inputNamaToko by remember { mutableStateOf(viewModel.namaToko.value) }
    var inputAlamatToko by remember { mutableStateOf(viewModel.alamatToko.value) }
    var inputNamaKasir by remember { mutableStateOf(viewModel.namaKasir.value) }
    var inputPesanStruk by remember { mutableStateOf(viewModel.pesanStruk.value) }

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        TopAppBar(
            modifier = Modifier.height(80.dp),
            title = { Text("Pengaturan Toko & Struk", color = Color(0xFFD4AF37), fontSize = 20.sp) },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.primary
            )
        )

        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF121212)),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Identitas Struk", fontWeight = FontWeight.Bold, fontSize = 18.sp)

                    OutlinedTextField(
                        value = inputNamaToko,
                        onValueChange = { inputNamaToko = it },
                        label = { Text("Nama Toko") },
                        leadingIcon = { Icon(Icons.Default.Storefront, null) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = inputAlamatToko,
                        onValueChange = { inputAlamatToko = it },
                        label = { Text("Alamat Toko") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, null) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = inputNamaKasir,
                        onValueChange = { inputNamaKasir = it },
                        label = { Text("Nama Kasir") },
                        leadingIcon = { Icon(Icons.Default.Person, null) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = inputPesanStruk,
                        onValueChange = { inputPesanStruk = it },
                        label = { Text("Pesan Bawah Struk") },
                        leadingIcon = { Icon(Icons.Default.ChatBubbleOutline, null) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            viewModel.namaToko.value = inputNamaToko
                            viewModel.alamatToko.value = inputAlamatToko
                            viewModel.namaKasir.value = inputNamaKasir
                            viewModel.pesanStruk.value = inputPesanStruk
                            Toast.makeText(context, "Data Struk Diperbarui!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD4AF37))
                    ) {
                        Text("SIMPAN PERUBAHAN")
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(80.dp))
    }
}
package com.tokokasir.minimarket

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "katalog_barang")
// Ini adalah "Cetakan" (Data Class) untuk barang daganganmu
data class Product(
    @PrimaryKey
    val barcode: String,
    val name: String,
    val price: Int,
    val quantity: Int = 1,
    val timestampId: Long = 0L // Untuk membedakan barang di keranjang
)

// Ini adalah cetakan untuk 1 Struk Belanja
@Entity(tableName = "riwayat_transaksi")
data class Transaction(
    @PrimaryKey
    val id: String,
    val date: String,
    val itemsSummary: String, // Kita ubah List menjadi String ringkasan barang
    val totalAmount: Int,
    val cashAmount: Int,
    val changeAmount: Int
)
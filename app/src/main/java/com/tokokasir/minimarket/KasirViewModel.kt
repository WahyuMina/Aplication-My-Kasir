package com.tokokasir.minimarket

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// 1. Koki sekarang butuh Kunci Brankas (KasirDao)
class KasirViewModel(private val dao: KasirDao) : ViewModel() {

    // --- DATA GUDANG (Langsung terhubung ke Database secara Real-time) ---
    // stateIn akan mengubah aliran data dari database menjadi State yang bisa dibaca oleh UI
    val productList = dao.getAllProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- DATA TRANSAKSI & PENGATURAN (Sama seperti sebelumnya) ---
    val cartItems = mutableStateListOf<Product>()
    val transactionHistory = dao.getAllTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var namaToko = mutableStateOf("")
    var alamatToko = mutableStateOf("")
    var namaKasir = mutableStateOf("")
    var pesanStruk = mutableStateOf("")

    // --- LOGIKA BISNIS ---

    // Menyimpan barang baru ke Database
    fun addNewProduct(barcode: String, name: String, price: Int) {
        viewModelScope.launch { // Membuka jalur khusus (Coroutine) agar aplikasi tidak nge-lag
            val newProduct = Product(barcode, name, price)
            dao.insertProduct(newProduct) // Simpan ke brankas!
        }
    }

    // Menghapus barang dari Database secara permanen
    fun deleteProductFromDatabase(product: Product) {
        viewModelScope.launch {
            dao.deleteProduct(product)
        }
    }

    // Mencari barang (Sekarang mencarinya dari value database)
    fun findProductByBarcode(barcode: String): Product? {
        return productList.value.find { it.barcode == barcode }
    }

    // Fungsi keranjang dan transaksi (TIDAK ADA YANG BERUBAH)
    fun addToCart(product: Product) {
        val existingItem = cartItems.find { it.barcode == product.barcode }
        if (existingItem != null) {
            val index = cartItems.indexOf(existingItem)
            cartItems[index] = existingItem.copy(quantity = existingItem.quantity + 1)
        } else {
            cartItems.add(product.copy(timestampId = System.currentTimeMillis()))
        }
    }

    fun decreaseCartItem(product: Product) {
        if (product.quantity > 1) {
            val index = cartItems.indexOf(product)
            cartItems[index] = product.copy(quantity = product.quantity - 1)
        }
    }

    fun removeFromCart(product: Product) {
        cartItems.remove(product)
    }

    fun clearCart() {
        cartItems.clear()
    }

    fun calculateTotal(): Int {
        return cartItems.sumOf { it.price * it.quantity }
    }

    fun saveTransaction(cash: Int, change: Int) {
        if (cartItems.isNotEmpty()) {
            val total = calculateTotal()
            // Gabungkan nama-nama barang menjadi satu teks panjang
            val ringkasanBarang = cartItems.joinToString(", ") { "${it.name} (x${it.quantity})" }

            val newTransaction = Transaction(
                id = "TRX-${System.currentTimeMillis()}",
                date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date()),
                itemsSummary = ringkasanBarang,
                totalAmount = total,
                cashAmount = cash,
                changeAmount = change
            )
            viewModelScope.launch {
                dao.insertTransaction(newTransaction) // SIMPAN PERMANEN!
                clearCart()
            }
        }
    }
}


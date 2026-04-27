package com.tokokasir.minimarket

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// @Dao = Data Access Object
@Dao
interface KasirDao {
    // MENGAMBIL semua data barang
    // 'Flow' agar kalau ada barang baru, layarnya otomatis *update* (Real-time!)
    @Query("SELECT * FROM katalog_barang")
    fun getAllProducts(): Flow<List<Product>>

    // Perintah MENYIMPAN barang baru
    // OnConflictStrategy.REPLACE = Kalau barcodenya sudah ada, timpa dengan data yang baru
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product)

    // --- PERINTAH BARU UNTUK TRANSAKSI ---
    @Query("SELECT * FROM riwayat_transaksi ORDER BY id DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    // Perintah MENGHAPUS barang (opsional untuk nanti)
    @Delete
    suspend fun deleteProduct(product: Product)

    @Insert
    suspend fun insertTransaction(transaction: Transaction)
}

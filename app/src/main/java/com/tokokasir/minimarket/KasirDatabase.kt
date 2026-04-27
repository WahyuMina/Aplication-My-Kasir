package com.tokokasir.minimarket

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Memberitahu Room bahwa database ini berisi tabel Product, versi 1
@Database(entities = [Product::class, Transaction::class], version = 1, exportSchema = false)
abstract class KasirDatabase : RoomDatabase() {

    // Mendaftarkan si Satpam (DAO) ke dalam Brankas
    abstract fun kasirDao(): KasirDao

    // Kode 'Companion Object' ini adalah standar baku dari Google.
    // Fungsinya memastikan aplikasi kita hanya punya 1 brankas utama, tidak berlipat ganda.
    companion object {
        @Volatile
        private var INSTANCE: KasirDatabase? = null

        fun getDatabase(context: Context): KasirDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KasirDatabase::class.java,
                    "toko_kasir_db" // Ini nama file databasemu yang tersimpan di HP nanti
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
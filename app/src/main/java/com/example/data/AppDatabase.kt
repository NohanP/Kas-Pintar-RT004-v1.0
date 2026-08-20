package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.model.AnnouncementEntity
import com.example.model.CitizenEntity
import com.example.model.TransactionEntity

@Database(
    entities = [CitizenEntity::class, TransactionEntity::class, AnnouncementEntity::class],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun citizenDao(): CitizenDao
    abstract fun transactionDao(): TransactionDao
    abstract fun announcementDao(): AnnouncementDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                try {
                    db.execSQL("ALTER TABLE transactions ADD COLUMN proofPhotoUri TEXT DEFAULT NULL")
                } catch (_: Exception) {}
                try {
                    db.execSQL("ALTER TABLE transactions ADD COLUMN proofPhotoCloudUrl TEXT DEFAULT NULL")
                } catch (_: Exception) {}
                try {
                    db.execSQL("ALTER TABLE transactions ADD COLUMN proofPhotoDescription TEXT DEFAULT NULL")
                } catch (_: Exception) {}
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                try {
                    db.execSQL("ALTER TABLE transactions ADD COLUMN isPettyCash INTEGER NOT NULL DEFAULT 0")
                } catch (_: Exception) {}
                try {
                    db.execSQL("ALTER TABLE transactions ADD COLUMN bpkkNumber TEXT NOT NULL DEFAULT ''")
                } catch (_: Exception) {}
                try {
                    db.execSQL("ALTER TABLE transactions ADD COLUMN recipientPerson TEXT DEFAULT NULL")
                } catch (_: Exception) {}
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                try {
                    db.execSQL("ALTER TABLE transactions ADD COLUMN address TEXT DEFAULT NULL")
                } catch (_: Exception) {}
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "kas_rt_mandiri_app.db"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    .fallbackToDestructiveMigration(true)
                    .fallbackToDestructiveMigrationOnDowngrade(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

package com.study.prepshala.lakshyaDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.study.prepshala.secretDiaryDatabase.SecretDiaryDatabase

@Database (
    entities = [Lakshya :: class],
    version = 1,
    exportSchema = false
)
abstract class LakshyaDatabase: RoomDatabase() {
    abstract fun getLakshyaDao() : LakshyaDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: LakshyaDatabase? = null

        fun getDatabase(context: Context): LakshyaDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LakshyaDatabase::class.java,
                    "LakshyaDatabase"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}
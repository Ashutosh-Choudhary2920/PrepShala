package com.study.prepshala.secretDiaryDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(
        entities = [SecretDiary :: class],
        version = 1,
        exportSchema = false
    )
    abstract class SecretDiaryDatabase : RoomDatabase() {
        abstract fun getSecretDiaryDao() : SecretDiaryDao

        companion object {
            // Singleton prevents multiple instances of database opening at the
            // same time.
            @Volatile
            private var INSTANCE: SecretDiaryDatabase? = null

            fun getDatabase(context: Context): SecretDiaryDatabase {
                // if the INSTANCE is not null, then return it,
                // if it is, then create the database
                return INSTANCE ?: synchronized(this) {
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        SecretDiaryDatabase::class.java,
                        "SecretDiaryDatabase"
                    ).build()
                    INSTANCE = instance
                    // return instance
                    instance
                }
            }
        }
    }

package com.study.prepshala.lakshyaDatabase

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface LakshyaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(lakshya: Lakshya)

    @Delete
    suspend fun delete(lakshya: Lakshya)

    @Query("SELECT * FROM Lakshya")
    fun getAllEntries() : LiveData<List<Lakshya>>
}
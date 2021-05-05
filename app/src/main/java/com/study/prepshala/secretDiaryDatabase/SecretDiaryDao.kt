package com.study.prepshala.secretDiaryDatabase

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface SecretDiaryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(secretDiary : SecretDiary)


    @Delete
    suspend fun delete(secretDiary : SecretDiary)

    @Query("SELECT * FROM SecretDiary ORDER by date and time ASC")
    fun getAllEntries() : LiveData<List<SecretDiary>>

}
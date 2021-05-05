package com.study.prepshala.secretDiaryDatabase
import androidx.lifecycle.LiveData

class SecretDiaryRepository(private val secretDiaryDao: SecretDiaryDao) {

    val allEntries: LiveData<List<SecretDiary>> = secretDiaryDao.getAllEntries()

    suspend fun insert(secretDiary: SecretDiary){
        secretDiaryDao.insert(secretDiary)
    }

    suspend fun delete(secretDiary: SecretDiary) {
        secretDiaryDao.delete(secretDiary)
    }

}
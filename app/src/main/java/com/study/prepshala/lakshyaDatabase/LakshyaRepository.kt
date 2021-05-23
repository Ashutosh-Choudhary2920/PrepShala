package com.study.prepshala.lakshyaDatabase

import androidx.lifecycle.LiveData
import com.study.prepshala.secretDiaryDatabase.SecretDiary


class LakshyaRepository (private val lakshyaDao: LakshyaDao) {
    val allEntries: LiveData<List<Lakshya>> = lakshyaDao.getAllEntries()

    suspend fun insert(lakshya: Lakshya){
        lakshyaDao.insert(lakshya)
    }

    suspend fun delete(lakshya: Lakshya) {
        lakshyaDao.delete(lakshya)
    }
}
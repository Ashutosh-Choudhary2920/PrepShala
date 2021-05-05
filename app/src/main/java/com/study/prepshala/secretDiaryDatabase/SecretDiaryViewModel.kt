package com.study.prepshala.secretDiaryDatabase

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SecretDiaryViewModel(application: Application): AndroidViewModel(application) {
    val allEntries: LiveData<List<SecretDiary>>


    private val repository: SecretDiaryRepository

    init {
        val dao = SecretDiaryDatabase.getDatabase(application).getSecretDiaryDao()
        repository = SecretDiaryRepository(dao)
        allEntries = repository.allEntries

    }

    fun deleteEntries(secretDiary: SecretDiary) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(secretDiary)
    }

    fun addEntries(secretDiary: SecretDiary) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(secretDiary)
    }

}

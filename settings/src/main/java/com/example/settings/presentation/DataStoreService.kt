package com.example.settings.presentation

import com.example.settings.data.SettingContainer
import kotlinx.coroutines.flow.StateFlow

interface DataStoreService {
    val settingData: StateFlow<SettingContainer>
    suspend fun saveSetting(periodic: Long, delayed: Long)
    suspend fun readSetting()
}
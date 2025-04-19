package com.example.settings.di

import com.example.settings.data.DataStoreServiceImpl
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import com.example.settings.presentation.DataStoreService
import com.example.settings.presentation.SettingsViewModel

val settingsModule = module {
    single<DataStoreService> { DataStoreServiceImpl(androidApplication()) }
    viewModel{ SettingsViewModel(get()) }
}
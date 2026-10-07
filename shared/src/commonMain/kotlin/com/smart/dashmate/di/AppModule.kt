package com.smart.dashmate.di

import com.smart.dashmate.data.remote.ApiService
import com.smart.dashmate.data.remote.httpClient
import com.smart.dashmate.data.repository.RouterRepository
import com.smart.dashmate.presentation.viewModel.RouteViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single {
        httpClient
    }

    single {
        ApiService(
            client = get()
        )
    }

    single {
        RouterRepository(
            apiService = get()
        )
    }

    single {
        RouteViewModel(
            routerRepository = get()
        )
    }
}
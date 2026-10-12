package com.smart.dashmate.di

import com.smart.dashmate.data.remote.ApiService
import com.smart.dashmate.data.remote.WebSocketService
import com.smart.dashmate.data.remote.httpClient
import com.smart.dashmate.data.remote.webSocketClient
import com.smart.dashmate.data.repository.RouterRepository
import com.smart.dashmate.presentation.viewModel.RouteViewModel
import org.koin.dsl.module

val mapModule = module {

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

val webSocketModule = module {

    single {
        WebSocketService(
            client = webSocketClient
        )
    }

}

val appModule = module {
    includes(
        mapModule,
        webSocketModule
    )
}
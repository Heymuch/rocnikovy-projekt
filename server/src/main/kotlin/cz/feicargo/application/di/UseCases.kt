package cz.feicargo.application.di

import cz.feicargo.station.usecase.SearchForStationsUseCase
import io.ktor.server.plugins.di.DependencyRegistry

fun DependencyRegistry.useCases() {
    provide<SearchForStationsUseCase> {
        SearchForStationsUseCase(
            stationRepository = resolve(),
            transactionManager = resolve(),
        )
    }
}

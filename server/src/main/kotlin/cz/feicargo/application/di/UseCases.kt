package cz.feicargo.application.di

import cz.feicargo.cargotype.usecase.GetCargoTypesUseCase
import cz.feicargo.station.usecase.SearchForStationsUseCase
import io.ktor.server.plugins.di.DependencyRegistry

fun DependencyRegistry.useCases() {
    provide<SearchForStationsUseCase> {
        SearchForStationsUseCase(
            stationRepository = resolve(),
            transactionManager = resolve(),
        )
    }
    provide<GetCargoTypesUseCase> {
        GetCargoTypesUseCase(
            repository = resolve(),
            transactionManager = resolve(),
        )
    }
}

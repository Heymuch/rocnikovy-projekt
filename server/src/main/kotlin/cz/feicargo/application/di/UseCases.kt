package cz.feicargo.application.di

import cz.feicargo.cargotype.usecase.GetCargoTypesUseCase
import cz.feicargo.order.usecase.SearchOrdersUseCase
import cz.feicargo.station.usecase.SearchForStationsUseCase
import cz.feicargo.trainset.usecase.GetTrainSetDetailUseCase
import cz.feicargo.trainset.usecase.SearchTrainSetsUseCase
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

    provide< SearchTrainSetsUseCase> {
        SearchTrainSetsUseCase(
            repository = resolve(),
            stationRepository = resolve(),
            transactionManager = resolve(),
        )
    }

    provide<SearchOrdersUseCase> {
        SearchOrdersUseCase(
            orderRepository = resolve(),
            trainSetRepository = resolve(),
            cargoTypeRepository = resolve(),
            stationRepository = resolve(),
            transactionManager = resolve(),
        )
    }

    provide<GetTrainSetDetailUseCase> {
        GetTrainSetDetailUseCase(
            trainSetRepository = resolve(),
            orderRepository = resolve(),
            userRepository = resolve(),
            stationRepository = resolve(),
            locomotiveRepository = resolve(),
            wagonRepository = resolve(),
            transactionManager = resolve(),
            )
    }
}

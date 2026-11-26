package cz.feicargo.application.di

import cz.feicargo.cargotype.usecase.GetCargoTypesUseCase
import cz.feicargo.order.usecase.CalculateOrderUseCase
import cz.feicargo.order.usecase.CreateOrderUseCase
import cz.feicargo.order.usecase.GetOrderDetailUseCase
import cz.feicargo.order.usecase.SearchOrdersUseCase
import cz.feicargo.order.usecase.UpdateOrderUseCase
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

    provide<SearchTrainSetsUseCase> {
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
            userRepository = resolve(),
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
            routeService = resolve(),
            railwayNodeRepository = resolve(),
            cargoTypeRepository = resolve(),
            transactionManager = resolve(),
        )
    }

    provide<GetOrderDetailUseCase> {
        GetOrderDetailUseCase(
            orderRepository = resolve(),
            trainSetRepository = resolve(),
            cargoTypeRepository = resolve(),
            stationRepository = resolve(),
            wagonRepository = resolve(),
            userRepository = resolve(),
            railwayNodeRepository = resolve(),
            routeService = resolve(),
            transactionManager = resolve(),
        )
    }

    provide<CreateOrderUseCase> {
        CreateOrderUseCase(
            orderRepository = resolve(),
            orderCalculationService = resolve(),
            transactionManager = resolve(),
        )
    }

    provide<CalculateOrderUseCase> {
        CalculateOrderUseCase(
            orderCalculationService = resolve(),
            railwayNodeRepository = resolve(),
            transactionManager = resolve(),
        )
    }

    provide<UpdateOrderUseCase> {
        UpdateOrderUseCase(
            orderRepository = resolve(),
            orderCalculationService = resolve(),
            transactionManager = resolve(),
        )
    }
}

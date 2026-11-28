package cz.feicargo.application.di

import cz.feicargo.order.service.OrderCalculationService
import cz.feicargo.order.service.implementation.OrderCalculationServiceImpl
import cz.feicargo.route.service.RouteService
import cz.feicargo.route.service.implementation.RoutePostgresService
import io.ktor.server.plugins.di.DependencyRegistry

fun DependencyRegistry.services() {
    provide<RouteService> {
        RoutePostgresService(
            railwayGraphRepository = resolve(),
        )
    }

    provide<OrderCalculationService> {
        OrderCalculationServiceImpl(
            routeService = resolve(),
            cargoTypeRepository = resolve(),
            wagonRepository = resolve(),
            locomotiveRepository = resolve(),
            transactionManager = resolve(),
        )
    }
}

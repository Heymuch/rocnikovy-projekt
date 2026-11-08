package cz.feicargo.application.di

import cz.feicargo.cargotype.api.rest.controller.GetCargoTypesRestController
import cz.feicargo.order.api.rest.controller.SearchOrdersRestController
import cz.feicargo.station.api.rest.controller.SearchForStationsRestController
import cz.feicargo.trainset.api.rest.controller.SearchTrainSetsRestController
import io.ktor.server.plugins.di.DependencyRegistry

fun DependencyRegistry.controllers() {
    provide<SearchForStationsRestController> {
        SearchForStationsRestController(
            useCase = resolve(),
        )
    }

    provide<GetCargoTypesRestController> {
        GetCargoTypesRestController(
            useCase = resolve(),
        )
    }

    provide<SearchTrainSetsRestController> {
        SearchTrainSetsRestController(
            useCase = resolve(),
        )
    }

    provide<SearchOrdersRestController> {
        SearchOrdersRestController(
            useCase = resolve(),
        )
    }
}

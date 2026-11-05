package cz.feicargo.application.di

import cz.feicargo.cargotype.api.rest.controller.GetCargoTypesRestController
import cz.feicargo.station.api.rest.controller.SearchForStationsRestController
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
}

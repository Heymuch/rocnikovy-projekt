package cz.feicargo.station.api.rest.route

import cz.feicargo.station.api.rest.Stations
import cz.feicargo.station.api.rest.controller.SearchForStationsRestController
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.resources.get
import io.ktor.server.routing.Route
import io.ktor.server.routing.application
import io.ktor.server.routing.openapi.describe
import io.ktor.utils.io.ExperimentalKtorApi

@OptIn(ExperimentalKtorApi::class)
object StationsRoutes {
    object Search {
        fun Route.get() =
            get<Stations.Search> {
                with(application.dependencies.resolve<SearchForStationsRestController>()) {
                    SearchForStationsRestController.Context(
                        call,
                        it.query,
                    ).use()
                }
            }.describe {
                summary = "Search for stations"
                tag("Stations")
            }
    }

    object Get {
        fun Route.get() = get<Stations.Get> {}
    }
}

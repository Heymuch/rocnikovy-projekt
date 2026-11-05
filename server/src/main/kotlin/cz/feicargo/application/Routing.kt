package cz.feicargo.application

import cz.feicargo.cargotype.api.rest.route.CargoTypeRoutes
import cz.feicargo.station.api.rest.route.StationsRoutes
import io.ktor.http.ContentType
import io.ktor.openapi.OpenApiInfo
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.resources.Resources
import io.ktor.server.routing.openapi.OpenApiDocSource
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.routingModule() {
    install(Resources)

    routing {
        swaggerUI("/docs/swagger") {
            info = OpenApiInfo(title = "FEICargo", version = "1.0.0")
            source =
                OpenApiDocSource.Routing(
                    contentType = ContentType.Application.Yaml,
                )
        }
        route("/api") {
            with(StationsRoutes.Search) { get() }
            with(StationsRoutes.Get) { get() }
            with(CargoTypeRoutes.GetAll) { get() }
        }
    }
}

package cz.feicargo.cargotype.api.rest.route

import cz.feicargo.cargotype.api.rest.CargoTypes
import cz.feicargo.cargotype.api.rest.controller.GetCargoTypesRestController
import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.resources.get
import io.ktor.server.routing.Route
import io.ktor.server.routing.application
import io.ktor.server.routing.openapi.describe
import io.ktor.utils.io.ExperimentalKtorApi

@OptIn(ExperimentalKtorApi::class)
object CargoTypeRoutes {
    object GetAll {
        fun Route.get() =
            get<CargoTypes.GetAll> {
                with(application.dependencies.resolve<GetCargoTypesRestController>()) {
                    GetCargoTypesRestController.Context(call).use()
                }
            }.describe {
                summary = "Get all cargo types"
                tag("CargoTypes")
                responses {
                    HttpStatusCode.OK {
                        description = "List of all cargo types"
                    }
                }
            }
    }
}

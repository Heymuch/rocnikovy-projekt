package cz.feicargo.order.api.rest.route

import cz.feicargo.order.api.rest.Orders
import cz.feicargo.order.api.rest.controller.SearchOrdersRestController
import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.resources.get
import io.ktor.server.routing.Route
import io.ktor.server.routing.application
import io.ktor.server.routing.openapi.describe
import io.ktor.utils.io.ExperimentalKtorApi

@OptIn(ExperimentalKtorApi::class)
object OrderRoutes {
    object Search {
        fun Route.get() =
            get<Orders.Search> { request ->
                with(application.dependencies.resolve<SearchOrdersRestController>()) {
                    val xInclude = call.request.headers["X-Include"]
                    val includes = xInclude?.split(",")?.map { it.trim() } ?: emptyList()
                    SearchOrdersRestController.Context(call, request.page, request.size, request.userId, includes).use()
                }
            }.describe {
                summary = "Overview for orders with pagination and optional user filter"
                tag("Orders")

                parameters {
                    header("X-Include") {
                        description = "For example: trainSet,cargoType,station"
                    }
                }

                responses {
                    HttpStatusCode.OK {
                        description = "Paginated list of orders"
                    }
                }
            }
    }
}

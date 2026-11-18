package cz.feicargo.order.api.rest.route

import cz.feicargo.order.api.rest.Orders
import cz.feicargo.order.api.rest.controller.SearchOrdersRestController
import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.resources.get
import io.ktor.server.routing.Route
import io.ktor.server.routing.application
import io.ktor.server.routing.openapi.describe
import cz.feicargo.order.api.rest.controller.GetOrderDetailRestController
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
                        description = "For example: trainSet,cargoType,station,user"
                    }
                }

                responses {
                    HttpStatusCode.OK {
                        description = "Paginated list of orders"
                    }
                }
            }
    }

    object GetDetail {
        fun Route.get() =
            get<Orders.Detail> { request ->
                with(application.dependencies.resolve<GetOrderDetailRestController>()) {
                    val uuid = kotlin.uuid.Uuid.parse(request.id)
                    val xInclude = call.request.headers["X-Include"]
                    val includes = xInclude?.split(",")?.map { it.trim() } ?: emptyList()

                    this.invoke(GetOrderDetailRestController.Context(call, uuid, includes))
                }
            }.describe {
                summary = "Detail of an order"
                tag("Orders")

                parameters {
                    header("X-Include") {
                        description = "For example: trainSet,cargoType,station,wagon,user"
                    }
                }

                responses {
                    HttpStatusCode.OK { description = "Detail of an order with optional extended data" }
                    HttpStatusCode.NotFound { description = "Order not found" }
                }
            }
    }
}

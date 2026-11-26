package cz.feicargo.order.api.rest.route

import cz.feicargo.order.api.rest.Orders
import cz.feicargo.order.api.rest.controller.CalculateOrderRestController
import cz.feicargo.order.api.rest.controller.SearchOrdersRestController
import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.routing.Route
import io.ktor.server.routing.application
import io.ktor.server.routing.openapi.describe
import cz.feicargo.order.api.rest.controller.GetOrderDetailRestController
import cz.feicargo.order.api.rest.controller.CreateOrderRestController
import cz.feicargo.order.api.rest.dto.request.OrderCalculateRequestDTO
import cz.feicargo.order.api.rest.dto.request.OrderCreateRequestDTO
import cz.feicargo.order.api.rest.controller.UpdateOrderRestController
import cz.feicargo.order.api.rest.dto.request.OrderUpdateRequestDTO
import cz.feicargo.order.api.rest.controller.DeleteOrderRestController
import io.ktor.server.resources.delete
import io.ktor.server.resources.put
import io.ktor.openapi.jsonSchema
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

                    GetOrderDetailRestController.Context(call, uuid, includes).use()
                }
            }.describe {
                summary = "Detail of an order"
                tag("Orders")

                parameters {
                    header("X-Include") {
                        description = "For example: trainSet,cargoType,station,wagon,user,railwayNode"
                    }
                }

                responses {
                    HttpStatusCode.OK { description = "Detail of an order with optional extended data" }
                    HttpStatusCode.NotFound { description = "Order not found" }
                }
            }
    }

    object Create {
        fun Route.post() =
            post<Orders> {
                with(application.dependencies.resolve<CreateOrderRestController>()) {
                    // Dočasně tu zafixujeme ID testovacího uživatele z našeho SQL skriptu,
                    // později toto ID budeme číst např. z JWTPrincipal hlaviček po autorizaci.
                    // TODO opravit
                    val dummyUserId = kotlin.uuid.Uuid.parse("11111111-1111-1111-1111-111111111111")
                    CreateOrderRestController.Context(call, dummyUserId).use()
                }
            }.describe {
                summary = "Create new order"
                tag("Orders")
                requestBody {
                    description = "New order data"
                    schema = jsonSchema<OrderCreateRequestDTO>()
                }
                responses {
                    HttpStatusCode.Created { description = "Order was created and is in the queue" }
                    HttpStatusCode.BadRequest { description = "Bad data" }
                }
            }
    }

    object Calculate {
        fun Route.post() =
            post<Orders.Calculate> {
                with(application.dependencies.resolve<CalculateOrderRestController>()) {
                    val xInclude = call.request.headers["X-Include"]
                    val includes = xInclude?.split(",")?.map { it.trim() } ?: emptyList()

                    CalculateOrderRestController.Context(call, includes).use()
                }
            }.describe {
                summary = "Calculate estimated order price, wagons and route"
                tag("Orders")
                parameters {
                    header("X-Include") {
                        description = "For example: railwayNode"
                    }
                }
                requestBody {
                    description = "Order parameters for price and route calculation"
                    schema = jsonSchema<OrderCalculateRequestDTO>()
                }
                responses {
                    HttpStatusCode.OK { description = "Order calculation result." }
                    HttpStatusCode.BadRequest { description = "Invalid request parameters or no route found." }
                }
            }
    }

    object Update {
        fun Route.put() =
            put<Orders.Update> { request ->
                with(application.dependencies.resolve<UpdateOrderRestController>()) {
                    // Dočasně zafixované ID testovacího uživatele (Firma A), později z JWT tokenu
                    // TODO opravit
                    val dummyUserId = kotlin.uuid.Uuid.parse("11111111-1111-1111-1111-111111111111")

                    UpdateOrderRestController.Context(call, request.id, dummyUserId).use()
                }
            }.describe {
                summary = "Update an existing order (only allowed in IN_QUEUE status)"
                tag("Orders")
                requestBody {
                    description = "Updated order data"
                    schema = jsonSchema<OrderUpdateRequestDTO>()
                }
                responses {
                    HttpStatusCode.OK { description = "Order was successfully updated." }
                    HttpStatusCode.BadRequest { description = "Invalid request data or no route found." }
                    HttpStatusCode.Forbidden { description = "User is not allowed to update this order." }
                    HttpStatusCode.NotFound { description = "Order not found." }
                    HttpStatusCode.Conflict { description = "Order is not in IN_QUEUE status." }
                }
            }
    }

    object Delete {
        fun Route.delete() =
            delete<Orders.Delete> { request ->
                with(application.dependencies.resolve<DeleteOrderRestController>()) {
                    // Dočasně zafixované ID testovacího uživatele (Firma A), později z JWT tokenu
                    val dummyUserId = kotlin.uuid.Uuid.parse("11111111-1111-1111-1111-111111111111")

                    DeleteOrderRestController.Context(call, request.id, dummyUserId).use()
                }
            }.describe {
                summary = "Delete an existing order (only allowed in IN_QUEUE status)"
                tag("Orders")
                responses {
                    HttpStatusCode.NoContent { description = "Order was successfully deleted." }
                    HttpStatusCode.Forbidden { description = "User is not allowed to delete this order." }
                    HttpStatusCode.NotFound { description = "Order not found." }
                    HttpStatusCode.Conflict { description = "Order is not in IN_QUEUE status." }
                }
            }
    }
}

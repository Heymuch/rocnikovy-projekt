package cz.feicargo.order.api.rest.route

import cz.feicargo.application.authenticatedUser
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
import io.ktor.server.response.respond
import io.ktor.server.resources.put

import io.ktor.openapi.jsonSchema
import io.ktor.utils.io.ExperimentalKtorApi

@OptIn(ExperimentalKtorApi::class)
object OrderRoutes {
    object Search {
        fun Route.get() =
            get<Orders.Search> { request ->
                val user = call.authenticatedUser()
                if (!user.isAdmin && !request.userId.isNullOrBlank() && !request.userId.equals(user.id.toString(), ignoreCase = true)) {
                    call.respond(HttpStatusCode.Forbidden, "You are not allowed to view orders of another user.")
                    return@get
                }

                with(application.dependencies.resolve<SearchOrdersRestController>()) {
                    val effectiveUserId = if (user.isAdmin) request.userId else user.id.toString()
                    val xInclude = call.request.headers["X-Include"]
                    val includes = xInclude?.split(",")?.map { it.trim() } ?: emptyList()
                    SearchOrdersRestController.Context(call, request.page, request.size, effectiveUserId, includes).use()
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
                    HttpStatusCode.OK { description = "Paginated list of orders" }
                    HttpStatusCode.Forbidden { description = "User is not allowed to view orders of another user" }
                }
            }
    }

    object GetDetail {
        fun Route.get() =
            get<Orders.Detail> { request ->
                with(application.dependencies.resolve<GetOrderDetailRestController>()) {
                    val user = call.authenticatedUser()
                    val uuid = kotlin.uuid.Uuid.parse(request.id)
                    val xInclude = call.request.headers["X-Include"]
                    val includes = xInclude?.split(",")?.map { it.trim() } ?: emptyList()

                    GetOrderDetailRestController.Context(call, uuid, user.id, user.isAdmin, includes).use()
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
                    HttpStatusCode.Forbidden { description = "User is not allowed to view this order" }
                    HttpStatusCode.NotFound { description = "Order not found" }
                }
            }
    }

    object Create {
        fun Route.post() =
            post<Orders> {
                val user = call.authenticatedUser()
                if (user.isAdmin) {
                    call.respond(HttpStatusCode.Forbidden, "Admins are not allowed to create orders.")
                    return@post
                }
                with(application.dependencies.resolve<CreateOrderRestController>()) {
                    val user = call.authenticatedUser()
                    CreateOrderRestController.Context(call, user.id).use()
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
                    val user = call.authenticatedUser()
                    UpdateOrderRestController.Context(call, request.id, user.id, user.isAdmin).use()
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
                    val user = call.authenticatedUser()
                    DeleteOrderRestController.Context(call, request.id, user.id, user.isAdmin).use()
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

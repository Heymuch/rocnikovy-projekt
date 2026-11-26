package cz.feicargo.order.api.rest.controller

import cz.feicargo.commons.controller.rest.RestController
import cz.feicargo.order.api.rest.dto.request.OrderUpdateRequestDTO
import cz.feicargo.order.usecase.UpdateOrderUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import kotlin.uuid.Uuid

class UpdateOrderRestController(
    private val useCase: UpdateOrderUseCase,
) : RestController<UpdateOrderRestController.Context> {

    data class Context(
        override val call: ApplicationCall,
        val orderIdRaw: String,
        val userId: Uuid,
    ) : RestController.Context

    override suspend fun invoke(ctx: Context) {
        val orderId = Uuid.parse(ctx.orderIdRaw)
        val requestDTO = ctx.call.receive<OrderUpdateRequestDTO>()

        when (val result = useCase.invoke(UpdateOrderUseCase.Context(orderId, ctx.userId, requestDTO))) {
            is UpdateOrderUseCase.Result.Success ->
                ctx.call.respond(HttpStatusCode.OK, result.detail)

            is UpdateOrderUseCase.Result.NotFound ->
                ctx.call.respond(HttpStatusCode.NotFound, "Order not found.")

            is UpdateOrderUseCase.Result.Forbidden ->
                ctx.call.respond(HttpStatusCode.Forbidden, "You do not have permission to update this order.")

            is UpdateOrderUseCase.Result.InvalidStatus ->
                ctx.call.respond(
                    HttpStatusCode.Conflict,
                    "Only orders in IN_QUEUE status can be updated (current status: ${result.currentStatus}).",
                )
        }
    }
}

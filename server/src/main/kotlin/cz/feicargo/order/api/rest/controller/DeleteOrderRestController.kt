package cz.feicargo.order.api.rest.controller

import cz.feicargo.commons.controller.rest.RestController
import cz.feicargo.order.usecase.DeleteOrderUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import kotlin.uuid.Uuid

class DeleteOrderRestController(
    private val useCase: DeleteOrderUseCase,
) : RestController<DeleteOrderRestController.Context> {

    data class Context(
        override val call: ApplicationCall,
        val orderIdRaw: String,
        val userId: Uuid,
        val isAdmin: Boolean,
    ) : RestController.Context

    override suspend fun invoke(ctx: Context) {
        val orderId = Uuid.parse(ctx.orderIdRaw)

        when (val result = useCase.invoke(DeleteOrderUseCase.Context(orderId, ctx.userId, ctx.isAdmin))) {
            is DeleteOrderUseCase.Result.Success ->
                ctx.call.respond(HttpStatusCode.NoContent)

            is DeleteOrderUseCase.Result.NotFound ->
                ctx.call.respond(HttpStatusCode.NotFound, "Order not found.")

            is DeleteOrderUseCase.Result.Forbidden ->
                ctx.call.respond(HttpStatusCode.Forbidden, "You do not have permission to delete this order.")

            is DeleteOrderUseCase.Result.InvalidStatus ->
                ctx.call.respond(
                    HttpStatusCode.Conflict,
                    "Only orders in IN_QUEUE status can be deleted (current status: ${result.currentStatus}).",
                )
        }
    }
}

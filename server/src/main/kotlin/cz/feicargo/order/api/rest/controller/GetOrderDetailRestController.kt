package cz.feicargo.order.api.rest.controller

import cz.feicargo.commons.controller.rest.RestController
import cz.feicargo.order.usecase.GetOrderDetailUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import kotlin.uuid.Uuid

class GetOrderDetailRestController(
    private val useCase: GetOrderDetailUseCase,
) : RestController<GetOrderDetailRestController.Context> {

    data class Context(
        override val call: ApplicationCall,
        val orderId: Uuid,
        val userId: Uuid,
        val isAdmin: Boolean,
        val includes: List<String>,
    ) : RestController.Context

    override suspend fun invoke(ctx: Context) {
        when (val result = useCase.invoke(GetOrderDetailUseCase.Context(ctx.orderId, ctx.userId, ctx.isAdmin, ctx.includes))) {
            is GetOrderDetailUseCase.Result.Success ->
                ctx.call.respond(HttpStatusCode.OK, result.detail)

            is GetOrderDetailUseCase.Result.NotFound ->
                ctx.call.respond(HttpStatusCode.NotFound, "Order not found.")

            is GetOrderDetailUseCase.Result.Forbidden ->
                ctx.call.respond(HttpStatusCode.Forbidden, "You do not have permission to view this order.")
        }
    }
}

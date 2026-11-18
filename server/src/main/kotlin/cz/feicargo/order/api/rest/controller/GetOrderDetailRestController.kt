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
        val id: Uuid,
        val includes: List<String>
    ) : RestController.Context

    override suspend fun invoke(ctx: Context) {
        val result = useCase.invoke(GetOrderDetailUseCase.Context(ctx.id, ctx.includes))

        if (result.detail == null) {
            ctx.call.respond(HttpStatusCode.NotFound, "Order was not found.")
            return
        }
        ctx.call.respond(HttpStatusCode.OK, result.detail)
    }
}

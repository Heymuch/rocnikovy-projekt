package cz.feicargo.order.api.rest.controller

import cz.feicargo.commons.controller.rest.RestController
import cz.feicargo.order.api.rest.dto.request.OrderCreateRequestDTO
import cz.feicargo.order.usecase.CreateOrderUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import kotlin.uuid.Uuid

class CreateOrderRestController(
    private val useCase: CreateOrderUseCase,
) : RestController<CreateOrderRestController.Context> {

    data class Context(
        override val call: ApplicationCall,
        val userId: Uuid
    ) : RestController.Context

    override suspend fun invoke(ctx: Context) {
        val requestDTO = ctx.call.receive<OrderCreateRequestDTO>()
        val result = useCase.invoke(CreateOrderUseCase.Context(ctx.userId, requestDTO))
        ctx.call.respond(HttpStatusCode.Created, result.detail)
    }
}

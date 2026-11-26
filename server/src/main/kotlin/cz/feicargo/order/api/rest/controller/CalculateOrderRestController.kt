package cz.feicargo.order.api.rest.controller

import cz.feicargo.commons.controller.rest.RestController
import cz.feicargo.order.api.rest.dto.request.OrderCalculateRequestDTO
import cz.feicargo.order.usecase.CalculateOrderUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond

class CalculateOrderRestController(
    private val useCase: CalculateOrderUseCase,
) : RestController<CalculateOrderRestController.Context> {

    data class Context(
        override val call: ApplicationCall,
        val includes: List<String>,
    ) : RestController.Context

    override suspend fun invoke(ctx: Context) {
        val requestDTO = ctx.call.receive<OrderCalculateRequestDTO>()

        val result = useCase.invoke(CalculateOrderUseCase.Context(requestDTO, ctx.includes))

        ctx.call.respond(HttpStatusCode.OK, result.calculation)
    }
}

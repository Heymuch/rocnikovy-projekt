package cz.feicargo.order.api.rest.controller

import cz.feicargo.commons.controller.rest.RestController
import cz.feicargo.order.api.rest.dto.response.PaginatedOrderResponseDTO
import cz.feicargo.order.api.rest.dto.toDTO
import cz.feicargo.order.usecase.SearchOrdersUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import kotlin.math.ceil
import kotlin.uuid.Uuid

class SearchOrdersRestController(
    private val useCase: SearchOrdersUseCase,
) : RestController<SearchOrdersRestController.Context> {

    data class Context(
        override val call: ApplicationCall,
        val page: Int,
        val size: Int,
        val userIdRaw: String?,
        val includes: List<String>
    ) : RestController.Context

    override suspend fun invoke(ctx: Context) {
        val parsedUserId = ctx.userIdRaw?.let { Uuid.parse(it) }
        val result = useCase.invoke(SearchOrdersUseCase.Context(ctx.page, ctx.size, parsedUserId, ctx.includes))
        val totalPages = ceil(result.totalCount.toDouble() / ctx.size).toInt()

        val responseDTO = PaginatedOrderResponseDTO(
            items = result.orders.map { it.toDTO() },
            extend = result.extend,
            page = ctx.page,
            size = ctx.size,
            totalCount = result.totalCount,
            totalPages = totalPages
        )

        ctx.call.respond(HttpStatusCode.OK, responseDTO)
    }
}

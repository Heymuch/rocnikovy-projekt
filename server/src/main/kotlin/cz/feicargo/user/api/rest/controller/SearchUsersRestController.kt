package cz.feicargo.user.api.rest.controller

import cz.feicargo.commons.controller.rest.RestController
import cz.feicargo.user.api.rest.dto.response.PaginatedUserResponseDTO
import cz.feicargo.user.usecase.SearchUsersUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import kotlin.math.ceil
import kotlin.uuid.Uuid

class SearchUsersRestController(
    private val useCase: SearchUsersUseCase,
) : RestController<SearchUsersRestController.Context> {

    data class Context(
        override val call: ApplicationCall,
        val page: Int,
        val size: Int,
        val currentUserId: Uuid,
    ) : RestController.Context

    override suspend fun invoke(ctx: Context) {
        val result = useCase.invoke(SearchUsersUseCase.Context(ctx.page, ctx.size, ctx.currentUserId))
        val totalPages = ceil(result.totalCount.toDouble() / ctx.size).toInt()

        val responseDTO = PaginatedUserResponseDTO(
            items = result.users,
            page = ctx.page,
            size = ctx.size,
            totalCount = result.totalCount,
            totalPages = totalPages,
        )

        ctx.call.respond(HttpStatusCode.OK, responseDTO)
    }
}

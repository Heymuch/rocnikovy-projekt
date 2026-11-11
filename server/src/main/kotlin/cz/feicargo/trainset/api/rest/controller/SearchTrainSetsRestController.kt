package cz.feicargo.trainset.api.rest.controller

import cz.feicargo.commons.controller.rest.RestController
import cz.feicargo.trainset.api.rest.dto.response.PaginatedTrainSetResponseDTO
import cz.feicargo.trainset.api.rest.dto.toDTO
import cz.feicargo.trainset.usecase.SearchTrainSetsUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import kotlin.math.ceil

class SearchTrainSetsRestController(
    private val useCase: SearchTrainSetsUseCase,
) : RestController<SearchTrainSetsRestController.Context> {

    data class Context(
        override val call: ApplicationCall,
        val page: Int,
        val size: Int,
        val includes: List<String>
    ) : RestController.Context

    override suspend fun invoke(ctx: Context) {
        val result = useCase.invoke(SearchTrainSetsUseCase.Context(ctx.page, ctx.size, ctx.includes))
        val totalPages = ceil(result.totalCount.toDouble() / ctx.size).toInt()
        val responseDTO = PaginatedTrainSetResponseDTO(
            items = result.trainSets.map { it.toDTO() },
            extend = result.extend,
            page = ctx.page,
            size = ctx.size,
            totalCount = result.totalCount,
            totalPages = totalPages
        )
        ctx.call.respond(HttpStatusCode.OK, responseDTO)
    }
}

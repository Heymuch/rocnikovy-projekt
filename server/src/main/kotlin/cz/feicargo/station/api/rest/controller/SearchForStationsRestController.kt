package cz.feicargo.station.api.rest.controller

import cz.feicargo.commons.controller.rest.RestController
import cz.feicargo.station.api.rest.dto.response.PaginatedStationResponseDTO
import cz.feicargo.station.api.rest.dto.toDTO
import cz.feicargo.station.usecase.SearchForStationsUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import kotlin.math.ceil

class SearchForStationsRestController(
    private val useCase: SearchForStationsUseCase,
) : RestController<SearchForStationsRestController.Context> {
    data class Context(
        override val call: ApplicationCall,
        val query: String?,
        val page: Int,
        val size: Int,
    ) : RestController.Context

    override suspend fun invoke(ctx: Context) {
        require(ctx.page >= 0) { "Page must be greater than or equal to zero." }
        require(ctx.size in 1..100) { "Page size must be between 1 and 100." }

        val result = useCase.invoke(
            SearchForStationsUseCase.Context(
                fulltextSearchValue = ctx.query,
                page = ctx.page,
                size = ctx.size,
            ),
        )

        val totalPages = ceil(result.totalCount.toDouble() / ctx.size).toInt()

        val responseDTO = PaginatedStationResponseDTO(
            items = result.stations.map { it.toDTO() },
            page = ctx.page,
            size = ctx.size,
            totalCount = result.totalCount,
            totalPages = totalPages,
        )

        ctx.call.respond(HttpStatusCode.OK, responseDTO)
    }
}

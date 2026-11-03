package cz.feicargo.station.api.rest.controller

import cz.feicargo.commons.controller.rest.RestController
import cz.feicargo.station.usecase.SearchForStationsUseCase
import io.ktor.server.application.ApplicationCall

class SearchForStationsRestController(
    private val controller: SearchForStationsUseCase,
) : RestController<SearchForStationsRestController.Context> {
    data class Context(
        override val call: ApplicationCall,
        val query: String,
    ) : RestController.Context

    override suspend fun invoke(ctx: Context) {
        TODO("Not yet implemented")
    }
}

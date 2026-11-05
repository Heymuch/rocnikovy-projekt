package cz.feicargo.cargotype.api.rest.controller

import cz.feicargo.commons.controller.rest.RestController
import cz.feicargo.cargotype.api.rest.dto.response.CargoTypeResponseDTO
import cz.feicargo.cargotype.api.rest.dto.toDTO
import cz.feicargo.cargotype.usecase.GetCargoTypesUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond

class GetCargoTypesRestController(
    private val useCase: GetCargoTypesUseCase,
) : RestController<GetCargoTypesRestController.Context> {

    data class Context(override val call: ApplicationCall) : RestController.Context

    override suspend fun invoke(ctx: Context) {
        val result = useCase.invoke(GetCargoTypesUseCase.Context())

        val responseDTO = CargoTypeResponseDTO(
            items = result.cargoTypes.map { it.toDTO() }
        )

        ctx.call.respond(HttpStatusCode.OK, responseDTO)
    }
}

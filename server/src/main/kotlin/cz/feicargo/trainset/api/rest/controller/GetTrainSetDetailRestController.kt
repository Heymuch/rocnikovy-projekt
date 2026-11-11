package cz.feicargo.trainset.api.rest.controller

import cz.feicargo.commons.controller.rest.RestController
import cz.feicargo.trainset.usecase.GetTrainSetDetailUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import kotlin.uuid.Uuid

class GetTrainSetDetailRestController(
    private val useCase: GetTrainSetDetailUseCase,
) : RestController<GetTrainSetDetailRestController.Context> {

    data class Context(
        override val call: ApplicationCall,
        val id: Uuid,
        val includes: List<String>
    ) : RestController.Context

    override suspend fun invoke(ctx: Context) {
        val result = useCase.invoke(GetTrainSetDetailUseCase.Context(ctx.id, ctx.includes))
        if (result.detail == null) {
            ctx.call.respond(HttpStatusCode.NotFound, "The train set with ID ${ctx.id} was not found.")
            return
        }
        ctx.call.respond(HttpStatusCode.OK, result.detail)
    }
}

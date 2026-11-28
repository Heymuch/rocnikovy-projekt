package cz.feicargo.user.api.rest.controller

import cz.feicargo.commons.controller.rest.RestController
import cz.feicargo.user.api.rest.dto.request.BlockUserRequestDTO
import cz.feicargo.user.api.rest.dto.request.BlockUserResponseDTO
import cz.feicargo.user.usecase.BlockUserUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import kotlin.uuid.Uuid

class BlockUserRestController(
    private val useCase: BlockUserUseCase,
) : RestController<BlockUserRestController.Context> {

    data class Context(
        override val call: ApplicationCall,
        val targetUserIdRaw: String,
        val currentUserId: Uuid,
    ) : RestController.Context

    override suspend fun invoke(ctx: Context) {
        val targetUserId = Uuid.parse(ctx.targetUserIdRaw)
        val requestDTO = ctx.call.receive<BlockUserRequestDTO>()

        when (
            val result = useCase.invoke(
                BlockUserUseCase.Context(targetUserId, ctx.currentUserId, requestDTO.isBlocked),
            )
        ) {
            is BlockUserUseCase.Result.Success ->
                ctx.call.respond(
                    HttpStatusCode.OK,
                    BlockUserResponseDTO(
                        id = result.user.id.value.toString(),
                        isBlocked = result.user.isBlocked,
                    ),
                )

            is BlockUserUseCase.Result.NotFound ->
                ctx.call.respond(HttpStatusCode.NotFound, "User not found.")
            is BlockUserUseCase.Result.SelfBlockNotAllowed ->
                ctx.call.respond(HttpStatusCode.BadRequest, "You cannot block or unblock your own account.")
        }
    }
}

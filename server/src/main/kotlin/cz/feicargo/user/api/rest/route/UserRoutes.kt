package cz.feicargo.user.api.rest.route

import cz.feicargo.application.authenticatedUser
import cz.feicargo.user.api.rest.Users
import cz.feicargo.user.api.rest.controller.BlockUserRestController
import cz.feicargo.user.api.rest.controller.SearchUsersRestController
import cz.feicargo.user.api.rest.dto.request.BlockUserRequestDTO
import io.ktor.http.HttpStatusCode
import io.ktor.openapi.jsonSchema
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.resources.get
import io.ktor.server.resources.patch
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.application
import io.ktor.server.routing.openapi.describe
import io.ktor.utils.io.ExperimentalKtorApi

@OptIn(ExperimentalKtorApi::class)
object UserRoutes {
    object Search {
        fun Route.get() =
            get<Users.Search> { request ->
                val user = call.authenticatedUser()
                if (!user.isAdmin) {
                    call.respond(HttpStatusCode.Forbidden, "Admin access required.")
                    return@get
                }

                with(application.dependencies.resolve<SearchUsersRestController>()) {
                    SearchUsersRestController.Context(call, request.page, request.size, user.id).use()
                }
            }.describe {
                summary = "Overview of users with order statistics (for Admin)"
                tag("Users")
                responses {
                    HttpStatusCode.OK { description = "Paginated list of users" }
                    HttpStatusCode.Forbidden { description = "Admin access required" }
                }
            }
    }

    object Block {
        fun Route.patch() =
            patch<Users.Block> { request ->
                val user = call.authenticatedUser()
                if (!user.isAdmin) {
                    call.respond(HttpStatusCode.Forbidden, "Admin access required.")
                    return@patch
                }

                with(application.dependencies.resolve<BlockUserRestController>()) {
                    BlockUserRestController.Context(call, request.id, user.id).use()
                }
            }.describe {
                summary = "Block or unblock a user (for Admin)"
                tag("Users")
                requestBody {
                    description = "Desired block state"
                    schema = jsonSchema<BlockUserRequestDTO>()
                }
                responses {
                    HttpStatusCode.OK { description = "User block state updated" }
                    HttpStatusCode.BadRequest { description = "Invalid user ID or self-blocking attempted" }
                    HttpStatusCode.Forbidden { description = "Admin access required" }
                    HttpStatusCode.NotFound { description = "User not found" }
                }
            }
    }
}

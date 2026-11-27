package cz.feicargo.application

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.application.log
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import kotlinx.serialization.Serializable
import java.time.format.DateTimeParseException

@Serializable
data class ErrorResponseDTO(
    val message: String,
)

fun Application.statusPagesModule() {
    install(StatusPages) {
        exception<IllegalArgumentException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponseDTO(cause.message ?: "Invalid request parameters."),
            )
        }

        exception<DateTimeParseException> { call, _ ->
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponseDTO("Invalid date/time format. Expected ISO-8601 (e.g. 2026-12-10T08:00:00Z)."),
            )
        }

        exception<BadRequestException> { call, _ ->
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponseDTO("Malformed request body."),
            )
        }

        exception<Throwable> { call, cause ->
            call.application.log.error("Unhandled exception", cause)
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponseDTO("An unexpected internal server error occurred."),
            )
        }
    }
}

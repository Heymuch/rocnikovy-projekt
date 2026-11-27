package cz.feicargo.application

import com.auth0.jwk.JwkProviderBuilder
import cz.feicargo.commons.persistence.TransactionManager
import cz.feicargo.user.entity.User
import cz.feicargo.user.persistence.UserRepository
import io.ktor.server.application.createRouteScopedPlugin
import io.ktor.server.auth.AuthenticationChecked
import io.ktor.server.plugins.di.dependencies
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import java.net.URI
import java.util.concurrent.TimeUnit
import kotlin.uuid.Uuid

const val AUTH_JWT = "auth-jwt"

data class AuthenticatedUser(
    val id: Uuid,
    val email: String,
    val name: String,
    val roles: Set<String>,
) {
    val isAdmin: Boolean get() = "admin" in roles
}

fun ApplicationCall.authenticatedUser(): AuthenticatedUser {
    val principal = principal<JWTPrincipal>()
        ?: throw IllegalStateException("Missing authenticated JWT principal.")
    val payload = principal.payload

    return AuthenticatedUser(
        id = Uuid.parse(payload.subject),
        email = payload.getClaim("email").asString().orEmpty(),
        name = payload.getClaim("name").asString().orEmpty(),
        roles = payload.getClaim("roles").asList(String::class.java)?.toSet().orEmpty(),
    )
}

fun Application.authenticationModule() {
    val issuer = environment.config.property("keycloak.issuer").getString()
    val jwksUrl = environment.config.property("keycloak.jwksUrl").getString()
    val audience = environment.config.property("keycloak.audience").getString()

    val jwkProvider = JwkProviderBuilder(URI(jwksUrl).toURL())
        .cached(10, 24, TimeUnit.HOURS)
        .rateLimited(10, 1, TimeUnit.MINUTES)
        .build()

    install(Authentication) {
        jwt(AUTH_JWT) {
            realm = "feicargo"
            verifier(jwkProvider, issuer) {
                acceptLeeway(3)
                withAudience(audience)
            }
            validate { credential ->
                val subject = credential.payload.subject ?: return@validate null
                // Ověříme, že sub je platné UUID
                if (runCatching { Uuid.parse(subject) }.isFailure) return@validate null
                JWTPrincipal(credential.payload)
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired.")
            }
        }
    }
}

val BlockedUserPlugin = createRouteScopedPlugin(name = "BlockedUserPlugin") {
    on(AuthenticationChecked) { call ->
        val principal = call.principal<JWTPrincipal>() ?: return@on
        val userId = runCatching { Uuid.parse(principal.payload.subject) }.getOrNull() ?: return@on

        val userRepository = call.application.dependencies.resolve<UserRepository>()
        val transactionManager = call.application.dependencies.resolve<TransactionManager>()

        val user = transactionManager.newTransaction {
            userRepository.select(listOf(User.Id(userId))).firstOrNull()
        }

        if (user?.isBlocked == true) {
            call.respond(HttpStatusCode.Forbidden, "Your account has been blocked.")
        }
    }
}


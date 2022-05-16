package com.bashkir.plugins

import com.bashkir.services.UserService
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import org.koin.core.parameter.parametersOf
import org.koin.ktor.ext.inject

fun Application.configureGoogleAuth() {
    val verifier: GoogleIdTokenVerifier by inject { parametersOf(environment) }
    val userService: UserService by inject()

    fun isExist(token: GoogleIdToken?): Boolean =
        token != null && userService.getUser(token.payload.subject) != null

    fun isVerified(token: String): Boolean = isExist(verifier.verify(token))

    install(Authentication) {
        session<UserSession> {
            validate { session ->
                if (isVerified(session.accessToken))
                    session
                else
                    null
            }

            challenge {
                call.respond(HttpStatusCode.Unauthorized)
            }
        }
    }

    routing {
        post("login") {
            val token: String = call.receive()
            val verifiedToken = verifier.verify(token)
            if (isExist(verifiedToken)) {
                call.sessions.set(UserSession(token, verifiedToken.payload.subject))
                call.respond(HttpStatusCode.OK)
            } else
                call.respond(HttpStatusCode.BadRequest)
        }
    }
}
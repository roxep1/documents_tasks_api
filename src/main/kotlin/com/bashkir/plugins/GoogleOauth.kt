package com.bashkir.plugins

import com.bashkir.services.UserService
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

    install(Authentication) {
        session<UserSession> {
            validate { session ->
                session
            }

            challenge {
                call.respond(HttpStatusCode.Forbidden)
                println("Нет данных авторизации. ${it.toString()}")
            }
        }
    }

    routing {
        post("login") {
            val params = call.receiveParameters()
            val token: String = params["idToken"].toString()
            val verifiedToken = verifier.verify(token)

            when {
                verifiedToken == null -> call.respond(HttpStatusCode.BadRequest)
                userService.getUser(verifiedToken.payload.subject) == null -> {
                    call.respond(HttpStatusCode.Forbidden)
                    println("Пользователя нет в базе данных.")
                }
                else -> {
                    val userId = verifiedToken.payload.subject
                    call.sessions.getOrSet{ UserSession(token, userId) }
                    call.respond(userService.getUser(userId)!!)
                }
            }
        }
    }

}
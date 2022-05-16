package com.bashkir.plugins

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.sessions.*

fun Application.configureSessions() =
    install(Sessions) {
        cookie<UserSession>("user_session")
    }

data class UserSession(val accessToken: String, val userId: String) : Principal
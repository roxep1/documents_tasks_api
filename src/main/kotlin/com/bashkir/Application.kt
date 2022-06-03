package com.bashkir

import com.bashkir.extensions.configureKoin
import com.bashkir.extensions.connectDatabase
import com.bashkir.extensions.createTables
import com.bashkir.extensions.scheduleUpdateUsers
import com.bashkir.plugins.configureGoogleAuth
import com.bashkir.plugins.configureRouting
import com.bashkir.plugins.configureSerialization
import com.bashkir.plugins.configureSessions
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.ContentDisposition.Companion.File
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.launch
import java.io.File


fun main(args: Array<String>): Unit =
    io.ktor.server.tomcat.EngineMain.main(args)


@Suppress("unused")
fun Application.module() {
    connectDatabase()
    createTables()
    configureSessions()
    configureGoogleAuth()
    configureRouting()
    configureSerialization()
    configureKoin()

    launch{
        scheduleUpdateUsers()
    }
}

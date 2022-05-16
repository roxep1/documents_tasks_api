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
import kotlinx.coroutines.launch


fun main(args: Array<String>): Unit =
    io.ktor.server.tomcat.EngineMain.main(args)


@Suppress("unused")
fun Application.module() {
    connectDatabase(user = "task123", password = "b1rt4-3", database = "task123")
    createTables()
    configureRouting()
    configureSerialization()
    configureSessions()
    configureGoogleAuth()
    configureKoin()

    launch{
        scheduleUpdateUsers()
    }
}

package com.bashkir

import com.bashkir.extensions.configureKoin
import com.bashkir.extensions.connectDatabase
import com.bashkir.extensions.createTables
import com.bashkir.extensions.deleteTables
import com.bashkir.plugins.configureRouting
import com.bashkir.plugins.configureSerialization
import io.ktor.application.*


fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    connectDatabase()
    createTables()
    configureRouting()
    configureSerialization()
    configureKoin()
}

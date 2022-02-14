package com.bashkir

import com.bashkir.extensions.configureKoin
import com.bashkir.extensions.connectDatabase
import com.bashkir.extensions.createTables
import com.bashkir.plugins.configureRouting
import com.bashkir.plugins.configureSerialization
import io.ktor.application.*


fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    connectDatabase(user = "", password = "") /* База должна называться documents_tasks */
    createTables()
    configureRouting()
    configureSerialization()
    configureKoin()
}

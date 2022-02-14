package com.bashkir.plugins

import com.bashkir.routings.documentRoute
import com.bashkir.routings.taskRoute
import com.bashkir.routings.templateRoute
import com.bashkir.routings.userRoute
import io.ktor.application.*
import io.ktor.routing.*


fun Application.configureRouting() {
    routing {
        userRoute()
        documentRoute()
        taskRoute()
        templateRoute()
    }
}

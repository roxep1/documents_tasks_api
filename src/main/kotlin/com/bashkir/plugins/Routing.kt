package com.bashkir.plugins

import com.bashkir.routings.documentRoute
import com.bashkir.routings.taskRoute
import com.bashkir.routings.templateRoute
import com.bashkir.routings.userRoute
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.routing.*


fun Application.configureRouting() {
    routing {
        authenticate {
            userRoute()
            documentRoute()
            taskRoute()
            templateRoute()
        }
    }
}

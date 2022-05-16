package com.bashkir.routings

import com.bashkir.extensions.withStringId
import com.bashkir.services.TemplateService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Route.templateRoute(){
    val service: TemplateService by inject()

    route("template") {
        post {
            service.addTemplate(call.receive())
            call.respond(HttpStatusCode.OK)
        }

        get("{id}"){
            withStringId {
                call.respond(service.getTemplate(it))
            }
        }

        get{
            call.respond(service.getAllTemplates())
        }

        put{
            service.updateTemplate(call.receive())
            call.respond(HttpStatusCode.OK)
        }
    }
}
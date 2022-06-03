package com.bashkir.routings

import com.bashkir.extensions.withId
import com.bashkir.models.Task
import com.bashkir.models.TaskWithFiles
import com.bashkir.services.TaskService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Route.taskRoute() {
    val service: TaskService by inject()

    route("task") {

        /* created не нужен.
        * в перформерах:
        * status, comment, statusChanged и documents не нужны */
        post {
            val model: TaskWithFiles = call.receive()
            if (model.task.performs.isEmpty())
                call.respond(HttpStatusCode.BadRequest)
            else {
                service.addTask(model)
                call.respond(HttpStatusCode.OK)
            }
        }

        put("status") {
            service.inProgressAllPerforms(call.receive())
            call.respond(HttpStatusCode.OK)
        }

        route("{id}") {

            put("comment") {
                withId {
                    service.addCommentToPerform(it, call.receive())
                    call.respond(HttpStatusCode.OK)
                }
            }

            put("status") {
                withId {
                    service.changePerformStatus(it, call.receive())
                    call.respond(HttpStatusCode.OK)
                }
            }

            delete {
                withId {
                    service.deleteTask(it)
                    call.respond(HttpStatusCode.OK)
                }
            }
        }
    }
}
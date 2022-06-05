package com.bashkir.routings

import com.bashkir.extensions.withStringId
import com.bashkir.extensions.withUserId
import com.bashkir.plugins.UserSession
import com.bashkir.services.UserService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import org.koin.ktor.ext.inject

fun Route.userRoute() {
    val service: UserService by inject()

    get("users") {
        call.respond(service.getAllUsers())
    }

    get("logout") {
        call.sessions.clear<UserSession>()
        call.respond(OK)
    }

    route("user") {

        /* Принимает только List<String> в теле запроса и возвращает List<User> */
        get {
            call.respond(service.getAllUsers(call.receive()))
        }

        get("familiarizes") {
            withUserId {
                call.respond(service.getFamiliarizes(it))
            }
        }

        get("agreements") {
            withUserId {
                call.respond(service.getAgreements(it))
            }
        }

        route("documents") {
            get {
                withUserId {
                    call.respond(service.getAllMyDocuments(it))
                }
            }

            get("created"){
                withUserId {
                    call.respond(service.getCreatedDocuments(it))
                }
            }
        }

        route("tasks") {
            /* Возвращает все задания связанные с пользователем.
            И где пользователь автор, и где он исполнитель */
            get {
                withUserId {
                    call.respond(service.getAllUserTasks(it))
                }
            }

            /* Возвращает задания, которые должен выполнить данный пользователь */
            get("todo") {
                withUserId {
                    call.respond(service.getTasksToDo(it))
                }
            }

            get("given") {
                withUserId {
                    call.respond(service.getGivenTasks(it))
                }
            }
        }

        route("{id}") {
            put {
                withStringId {
                    service.setRole(it, call.receive())
                    call.respond(OK)
                }
            }

            /* Принимает id пользователя и возвращает пользователя */
            get {
                withStringId {
                    val user = service.getUser(it)
                    if (user != null)
                        call.respond(user)
                    else
                        call.respond(HttpStatusCode.NotFound)
                }
            }
        }
    }
}
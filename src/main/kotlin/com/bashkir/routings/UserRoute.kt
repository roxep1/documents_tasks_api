package com.bashkir.routings

import com.bashkir.extensions.withStringId
import com.bashkir.services.UserService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Routing.userRoute() {
    val service: UserService by inject()

    get("users") {
        call.respond(service.getAllUsers())
    }

    route("user") {

        /* Принимает только List<String> в теле запроса и возвращает List<User> */
        get {
            call.respond(service.getAllUsers(call.receive()))
        }

        route("{id}") {
            put {
                withStringId {
                    service.setRole(it, call.receive())
                    call.respond(HttpStatusCode.OK)
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

            get("familiarizes") {
                withStringId {
                    call.respond(service.getFamiliarizes(it))
                }
            }

            get("agreements") {
                withStringId {
                    call.respond(service.getAgreements(it))
                }
            }

            route("tasks") {
                /* Возвращает все задания связанные с пользователем.
                И где пользователь автор, и где он исполнитель */
                get {
                    withStringId {
                        call.respond(service.getAllUserTasks(it))
                    }
                }

                /* Возвращает задания, которые должен выполнить данный пользователь */
                get("todo") {
                    withStringId {
                        call.respond(service.getTasksToDo(it))
                    }
                }

                get("given") {
                    withStringId {
                        call.respond(service.getGivenTasks(it))
                    }
                }
            }
        }
    }
}
package com.bashkir.routings

import com.bashkir.extensions.withId
import com.bashkir.services.DocumentService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Routing.documentRoute() {
    val service: DocumentService by inject()

    route("document") {

        /* В моделе документа не нужно заполнять created, desc нулабельный.
        В моделях familiarize нужно заполнить только id юзера и документа.
        В моделях agreement не нужно заполнять status, comment, created, statusChanged */
        post {
            service.addDocument(call.receive())
            call.respond(HttpStatusCode.OK)
        }

        /* Принимает List<Int> с айдишниками документов. Возвращает List<Document> */
        get {
            call.respond(service.getAllDocuments(call.receive()))
        }

        get("{id}") {
            withId {
                call.respond(service.getDocument(it))
            }
        }

        route("familiarize") {

            /* Меняет статус ознакомления по айдишнику familiarize */
            put("{id}") {
                withId {
                    call.respond(service.familiarize(it))
                }
            }

            /* Меняет статус ознакомления сразу множеству ознакомлений.
            * Принимает List<Int> через тело запроса */
            put {
                service.familiarizeAll(call.receive())
                call.respond(HttpStatusCode.OK)
            }
        }

        /* Тут нужно в теле запроса передать AgreementStatus.
        Еще можно передать как параметр комментарий к изменению статуса
         согласования вот так: agreement/12334?comment=Комментарий
        Это опционально, можно его не добавлять.*/
        put("agreement/{id}") {
            withId {
                val comment = call.request.queryParameters["comment"]
                if (comment.isNullOrBlank())
                    service.changeAgreementStatus(it, call.receive())
                else
                    service.changeAgreementStatus(it, call.receive(), comment)
                call.respond(HttpStatusCode.OK)
            }
        }

        post("perform/{id}") {
            withId {
                service.addDocumentToPerform(it, call.receive())
                call.respond(HttpStatusCode.OK)
            }
        }

    }

}
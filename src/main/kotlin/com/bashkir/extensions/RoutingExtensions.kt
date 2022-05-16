package com.bashkir.extensions

import com.bashkir.plugins.UserSession
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.util.pipeline.*

inline fun PipelineContext<Unit, ApplicationCall>.withStringId(
    action: (String) -> Unit
) = call.parameters["id"]?.let(action)

inline fun PipelineContext<Unit, ApplicationCall>.withId(
    action: (Int) -> Unit
) = call.parameters["id"]?.let {
    action(it.toInt())
}

inline fun PipelineContext<Unit, ApplicationCall>.withUserId(
    action: (String) -> Unit
) = call.principal<UserSession>()?.userId?.let(action)
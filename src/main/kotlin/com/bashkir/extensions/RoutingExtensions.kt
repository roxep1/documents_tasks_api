package com.bashkir.extensions

import io.ktor.application.*
import io.ktor.util.pipeline.*

inline fun PipelineContext<Unit, ApplicationCall>.withStringId(
    action: (String) -> Unit
) = call.parameters["id"]?.let(action)

inline fun PipelineContext<Unit, ApplicationCall>.withId(
    action: (Int) -> Unit
) = call.parameters["id"]?.let {
    action(it.toInt())
}
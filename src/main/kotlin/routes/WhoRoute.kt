package com.bitrabbit.routes

import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.who(){

    route("/who") {
        handle {
            val principal = call.authentication.principal<JWTPrincipal>()
            val subjectString = principal!!.payload.subject.removePrefix("auth0|")
            call.respondText("Success, $subjectString")
        }
    }
}
package com.bitrabbit

import com.bitrabbit.routes.login
import com.bitrabbit.routes.root
import com.bitrabbit.routes.sendMail
import com.bitrabbit.routes.who
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*

fun Application.configureRouting(apiKey: String, issuer: String, audience: String) {
    routing {
        root()
        sendMail(apiKey)

        // Static feature. Try to access `/static/ktor_logo.svg`
        staticResources("/static", "static")

        //Basic Auth
        authenticate("myBasicAuth") {
            login(issuer, audience)
        }

        //JWT AUth
        authenticate("myJWTAuth") {
            who()
        }
    }
}

package com.bitrabbit

import com.bitrabbit.db.DatabaseFactory
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    // Initialize DB
    DatabaseFactory.init(environment.config)

    val jwtConfig = environment.config.config("ktor.jwt")
    val issuer = jwtConfig.property("domain").getString()
    val audience = jwtConfig.property("audience").getString()
    val realm = jwtConfig.property("realm").getString()

    val resendConfig = environment.config.config("ktor.resend")
    val apiKey = resendConfig.property("apiKey").getString()

    installPlugins(realm, issuer, audience)
    configureRouting(apiKey, issuer, audience)
}

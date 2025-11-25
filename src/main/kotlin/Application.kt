package com.bitrabbit

import com.bitrabbit.db.DatabaseFactory
import com.bitrabbit.db.dao.IResumeDao
import com.bitrabbit.db.dao.IUserDao
import com.bitrabbit.db.dao.ResumeDao
import com.bitrabbit.db.dao.UserDao
import com.bitrabbit.routes.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*

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

    val userDao: IUserDao = UserDao()
    val resumeDao: IResumeDao = ResumeDao()

    routing {
        root()
        resumeRoutes(resumeDao)
        sendMail(apiKey)

        // Static feature. Try to access `/static/ktor_logo.svg`
        staticResources("/static", "static")

        //Basic Auth
        authenticate("myBasicAuth") {
            login(issuer, audience, userDao)
        }

        //JWT AUth
        authenticate("myJWTAuth") {
            who()
        }
    }
}

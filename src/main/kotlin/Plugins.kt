package com.bitrabbit

import com.bitrabbit.db.models.Users
import com.bitrabbit.helper.AuthHelper.makeJwtVerifier
import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.event.Level

fun Application.installPlugins(
    realm: String,
    issuer: String,
    audience: String
) {
    install(Authentication) {
        //BasicAuth
        basic("myBasicAuth") {
            this.realm = realm
            validate { auth ->
                val users = withContext(Dispatchers.IO) {
                    transaction {
                        Users
                            .selectAll()
                            .where { (Users.username eq auth.name) and (Users.password eq auth.password) }
                            .map { resultRow ->
                                resultRow[Users.username]
                            }
                    }
                }
                if (users.isNotEmpty()) UserIdPrincipal(auth.name) else null
            }
        }

        //JWT Auth
        jwt("myJWTAuth") {
            verifier(makeJwtVerifier(issuer, audience))
            this.realm = realm
            validate { credential ->
                if (credential.payload.audience.contains(audience))
                    JWTPrincipal(credential.payload)
                else
                    null
            }
        }
    }


    install(StatusPages) {
        exception<Throwable> { call, e ->
            call.respondText(e.localizedMessage, ContentType.Text.Plain, HttpStatusCode.InternalServerError)
        }
    }

    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
            serializeNulls()
        }
    }

    install(DefaultHeaders) {
        header("X-Engine", "Ktor") // will send this header with each response
    }

    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    install(CORS) {
        // ✅ Allow production frontend
        allowHost("salomirc.linkpc.net", schemes = listOf("https"))

        // ✅ Allow localhost for development
        allowHost("localhost:8889", schemes = listOf("http"))

        // ✅ Allow typical frontend headers
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.Accept)


        // ✅ Allow common HTTP methods
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Options)

        // ✅ Permite trimiterea cookie/token (dacă e cazul)
        allowCredentials = true

        // ✅ (opțional) expune headere adiționale către frontend
        exposeHeader(HttpHeaders.ContentType)
    }

//    install(HttpsRedirect) {
//        // The port to redirect to. By default, 443, the default HTTPS port.
//        sslPort = 443
//        // 301 Moved Permanently, or 302 Found redirect.
//        permanentRedirect = true
//    }
}
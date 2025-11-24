package com.bitrabbit.routes

import com.bitrabbit.db.models.User
import com.bitrabbit.db.models.Users
import com.bitrabbit.helper.AuthHelper.makeToken
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.ktor.util.reflect.*
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.login(issuer: String, audience: String){
    get("/login") {
        val principal = call.principal<UserIdPrincipal>()!!
        val users = transaction {
            Users
                .selectAll()
                .where { Users.username eq principal.name }
                .map {
                    User(
                        it[Users.username],
                        it[Users.password],
                        it[Users.firstName],
                        it[Users.lastName],
                        it[Users.email],
                        it[Users.isAdmin],
                        it[Users.token]
                    )
                }
        }
        call.respond(
            users[0].apply {
                this.token = makeToken(issuer, audience, principal.name)
            },
            typeInfo<User>()
        )
    }
}
package com.bitrabbit.routes

import com.bitrabbit.db.dao.IUserDao
import com.bitrabbit.db.models.User
import com.bitrabbit.helper.AuthHelper.makeToken
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.login(issuer: String, audience: String, userDao: IUserDao){
    get("/login") {
        val userIdPrincipal = call.principal<UserIdPrincipal>()
        val user: User? = userIdPrincipal?.let { principal ->
            withContext(Dispatchers.IO) {
                transaction {
                    val token = makeToken(issuer, audience, principal.name)
                    val existingUser = userDao.getByUsername(principal.name) ?: return@transaction null
                    val updatedUser = existingUser.copy(token = token)
                    val success = userDao.update(updatedUser.id, updatedUser)
                    if (success) updatedUser else null
                }
            }
        }
        if (user == null) {
            call.respondText("Resume not found", status = HttpStatusCode.NotFound)
        } else {
            call.respond(user)
        }
    }
}
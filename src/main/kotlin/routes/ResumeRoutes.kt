package com.bitrabbit.routes

import com.bitrabbit.db.dao.IResumeDao
import com.bitrabbit.db.models.ResumeDataModel
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.resumeRoutes(resumeDao: IResumeDao) {

    route("/resume") {

        // ---------------------------
        // CREATE
        // ---------------------------
        post {
            val dto = call.receive<ResumeDataModel>()
            val id = withContext(Dispatchers.IO) {
                transaction {
                    resumeDao.add(dto)
                }
            }
            call.respond(mapOf("id" to id))
        }

        // ---------------------------
        // READ BY ID
        // ---------------------------
        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respondText("Invalid ID.", status = io.ktor.http.HttpStatusCode.BadRequest)
                return@get
            }

            val resume = withContext(Dispatchers.IO) {
                transaction {
                    resumeDao.getById(id)
                }
            }

            if (resume == null) {
                call.respondText("Resume not found", status = io.ktor.http.HttpStatusCode.NotFound)
            } else {
                call.respond(resume)
            }
        }

        // ---------------------------
        // UPDATE
        // ---------------------------
        put("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respondText("Invalid ID.", status = io.ktor.http.HttpStatusCode.BadRequest)
                return@put
            }

            val dto = call.receive<ResumeDataModel>()

            val updated = withContext(Dispatchers.IO) {
                transaction {
                    resumeDao.update(id, dto)
                }
            }

            if (updated) {
                call.respondText("Resume updated successfully")
            } else {
                call.respondText("Resume not found", status = io.ktor.http.HttpStatusCode.NotFound)
            }
        }

        // ---------------------------
        // DELETE
        // ---------------------------
        delete("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respondText("Invalid ID.", status = io.ktor.http.HttpStatusCode.BadRequest)
                return@delete
            }

            val deleted = withContext(Dispatchers.IO) {
                transaction {
                    resumeDao.delete(id)
                }
            }

            if (deleted) {
                call.respondText("Resume deleted successfully")
            } else {
                call.respondText("Resume not found", status = io.ktor.http.HttpStatusCode.NotFound)
            }
        }
    }
}

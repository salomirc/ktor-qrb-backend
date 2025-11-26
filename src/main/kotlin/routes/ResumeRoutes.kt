package com.bitrabbit.routes

import com.bitrabbit.db.dao.IResumeDao
import com.bitrabbit.db.models.ResumeDataModel
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.resumeRoutes(resumeDao: IResumeDao) {

    route("/resume") {

        // -----------------------------------------
        // CREATE new resume for a user
        // -----------------------------------------
        post {
            val dto = call.receive<ResumeDataModel>()

            if (dto.userId <= 0) {
                call.respondText(
                    "userId must be provided and > 0",
                    status = HttpStatusCode.BadRequest
                )
                return@post
            }

            val id = withContext(Dispatchers.IO) {
                transaction { resumeDao.add(dto) }
            }

            call.respond(mapOf("id" to id))
        }

        // -----------------------------------------
        // GET a single resume by resumeId
        // -----------------------------------------
        get("/{resumeId}") {
            val resumeId = call.parameters["resumeId"]?.toIntOrNull()
            if (resumeId == null) {
                call.respondText("Invalid resumeId", status = HttpStatusCode.BadRequest)
                return@get
            }

            val resume = withContext(Dispatchers.IO) {
                transaction { resumeDao.getById(resumeId) }
            }

            if (resume == null) {
                call.respondText("Resume not found", status = HttpStatusCode.NotFound)
            } else {
                call.respond(resume)
            }
        }

        // -----------------------------------------
        // GET all resumes for a user
        // -----------------------------------------
        get("/user/{userId}") {
            val userId = call.parameters["userId"]?.toIntOrNull()
            if (userId == null) {
                call.respondText("Invalid userId", status = HttpStatusCode.BadRequest)
                return@get
            }

            val resumes = withContext(Dispatchers.IO) {
                transaction { resumeDao.getAllByUserId(userId) }
            }

            call.respond(resumes)
        }

        // -----------------------------------------
        // UPDATE an existing resume by resumeId
        // -----------------------------------------
        put("/{resumeId}") {
            val resumeId = call.parameters["resumeId"]?.toIntOrNull()
            if (resumeId == null) {
                call.respondText("Invalid resumeId", status = HttpStatusCode.BadRequest)
                return@put
            }

            val dto = call.receive<ResumeDataModel>()

            val updated = withContext(Dispatchers.IO) {
                transaction { resumeDao.update(resumeId, dto) }
            }

            if (updated) {
                call.respondText("Resume updated successfully")
            } else {
                call.respondText("Resume not found", status = HttpStatusCode.NotFound)
            }
        }

        // -----------------------------------------
        // DELETE a resume by resumeId
        // -----------------------------------------
        delete("/{resumeId}") {
            val resumeId = call.parameters["resumeId"]?.toIntOrNull()
            if (resumeId == null) {
                call.respondText("Invalid resumeId", status = HttpStatusCode.BadRequest)
                return@delete
            }

            val deleted = withContext(Dispatchers.IO) {
                transaction { resumeDao.delete(resumeId) }
            }

            if (deleted) {
                call.respondText("Resume deleted successfully")
            } else {
                call.respondText("Resume not found", status = HttpStatusCode.NotFound)
            }
        }
    }
}


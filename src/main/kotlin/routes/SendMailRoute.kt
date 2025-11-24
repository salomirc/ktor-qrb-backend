package com.bitrabbit.routes

import com.resend.Resend
import com.resend.core.exception.ResendException
import com.resend.services.emails.model.CreateEmailOptions
import com.resend.services.emails.model.CreateEmailResponse
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.util.reflect.*

data class ContactRequest(
    val name: String,
    val email: String,
    val message: String
)

data class ContactResponse(
    val isSuccess: Boolean,
    val emailId: String,
    val errorMessage: String
)

fun Routing.sendMail(apiKey: String){
    post("/sendMail"){
        val data = call.receive<ContactRequest>()
        val result = sendEmail(apiKey, data)
        call.respond(result, typeInfo<ContactResponse>())
    }
}

fun sendEmail(apiKey: String, data: ContactRequest): ContactResponse {
    val resend = Resend(apiKey)
    val params: CreateEmailOptions? = CreateEmailOptions.builder()
        .from("Acme <onboarding@resend.dev>")
        .to("ciprian.salomir@gmail.com")
        .subject("Website contact form message")
        .html("<strong>${data.message}</strong>")
        .build()

    var isSuccess = false
    var emailId = ""
    var errorMessage = ""

    try {
        val data: CreateEmailResponse = resend.emails().send(params)
        println(data.id)
        isSuccess = true
        emailId = data.id
    } catch (e: ResendException) {
        e.printStackTrace()
        errorMessage = "${e.message} ${e.localizedMessage}"
    }

    return ContactResponse(isSuccess, emailId, errorMessage)
}

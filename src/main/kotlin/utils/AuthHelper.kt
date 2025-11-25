package com.bitrabbit.helper

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm

object AuthHelper {
    private val algorithm = Algorithm.HMAC256("maca")

    fun makeToken(issuer: String, audience: String, subject: String): String = JWT.create()
        .withSubject(subject)
        .withAudience(audience)
        .withIssuer(issuer)
        .sign(algorithm)

    fun makeJwtVerifier(issuer: String, audience: String): JWTVerifier = JWT
        .require(algorithm)
        .withAudience(audience)
        .withIssuer(issuer)
        .build()
}
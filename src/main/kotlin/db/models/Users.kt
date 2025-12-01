package com.bitrabbit.db.models

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object Users: Table() {
    val id = integer("id").autoIncrement()
    val username: Column<String> = varchar("username", 50).uniqueIndex()
    val password: Column<String> = varchar("password", 50)
    val firstName: Column<String> = varchar("first_name", 50)
    val lastName: Column<String> = varchar("last_name", 50)
    val email: Column<String> = varchar("email", 50)
    val isAdmin: Column<Boolean> = bool("is_admin")
    val token: Column<String?> = varchar("token", 500).nullable()

    override val primaryKey = PrimaryKey(id)
}

data class User(
    val id: Int = 0,
    val username: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val isAdmin: Boolean,
    var token: String? = null
)
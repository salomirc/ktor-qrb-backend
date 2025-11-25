package com.bitrabbit.db.dao

import com.bitrabbit.db.models.User
import com.bitrabbit.db.models.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

interface IUserDao {
    fun getAll(): List<User>
    fun getById(id: Int): User?
    fun getByUsername(username: String): User?
    fun add(user: User): Int
    fun update(id: Int, user: User): Boolean
    fun delete(id: Int): Boolean
}

class UserDao : IUserDao {

    override fun getAll(): List<User> {
        return Users.selectAll().map { toUser(it) }
    }

    override fun getById(id: Int): User? {
        return Users.selectAll().where { Users.id eq id }
            .map { toUser(it) }
            .singleOrNull()
    }

    override fun getByUsername(username: String): User? {
        return Users.selectAll().where { Users.username eq username }
            .map { toUser(it) }
            .singleOrNull()
    }

    override fun add(user: User): Int {
        return Users.insert {
            it[username] = user.username
            it[password] = user.password
            it[firstName] = user.firstName
            it[lastName] = user.lastName
            it[email] = user.email
            it[isAdmin] = user.isAdmin
            it[token] = user.token
        } get Users.id
    }

    override fun update(id: Int, user: User): Boolean {
        return Users.update({ Users.id eq id }) {
            it[username] = user.username
            it[password] = user.password
            it[firstName] = user.firstName
            it[lastName] = user.lastName
            it[email] = user.email
            it[isAdmin] = user.isAdmin
            it[token] = user.token
        } > 0
    }

    override fun delete(id: Int): Boolean {
        return Users.deleteWhere { Users.id eq id } > 0
    }

    private fun toUser(row: ResultRow): User =
        User(
            id = row[Users.id],
            username = row[Users.username],
            password = row[Users.password],
            firstName = row[Users.firstName],
            lastName = row[Users.lastName],
            email = row[Users.email],
            isAdmin = row[Users.isAdmin],
            token = row[Users.token]
        )
}


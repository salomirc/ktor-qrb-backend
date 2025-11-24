package com.bitrabbit.db.initDSL

import com.bitrabbit.db.models.Users
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Transaction

fun Transaction.initDSLQuickResumeBuilderDb() {
//    SchemaUtils.drop(Users)
    SchemaUtils.create(Users)

    //CREATE
//    val id = Users.insert {
//        it[username] = "salox"
//        it[password] = "redhatslx"
//        it[firstName] = "Ciprian"
//        it[lastName] = "Salomir"
//        it[email] = "ciprian.salomir@gmail.com"
//        it[isAdmin] = false
//        it[token] = null
//    } get(Users.id)

    //READ
//    val allUsers = Users.
//        selectAll().map {
//        User(
//            it[Users.username],
//            it[Users.password],
//            it[Users.firstName],
//            it[Users.lastName],
//            it[Users.email],
//            it[Users.isAdmin],
//            it[Users.token]
//        )
//        }
//    println("All users : $allUsers")
}
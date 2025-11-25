package com.bitrabbit.db.initDSL

import com.bitrabbit.db.models.Users
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Transaction

fun Transaction.initQuickResumeBuilderDb() {

//    SchemaUtils.drop(Users)
    SchemaUtils.create(Users)

//    val userDao: IUserDao = UserDao()

    //CREATE
//    userDao.add(
//        User(
//            username = "salox",
//            password = "redhatslx",
//            firstName = "Ciprian",
//            lastName = "Salomir",
//            email = "ciprian.salomir@gmail.com",
//            isAdmin = false
//        )
//    )

    //READ
//    val allUsers = userDao.getAll()
//    println("All users : $allUsers")
}
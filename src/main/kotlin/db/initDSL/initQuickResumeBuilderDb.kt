package com.bitrabbit.db.initDSL

import com.bitrabbit.db.models.*
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Transaction

fun Transaction.initQuickResumeBuilderDb() {
    SchemaUtils.create(Users)
    SchemaUtils.create(ResumeData)
    SchemaUtils.create(ResumeWorkHistory)
    SchemaUtils.create(ResumeEducation)
    SchemaUtils.create(ResumeLanguage)

//        val userDao: IUserDao = UserDao()
//        val resumeDao: IResumeDao = ResumeDao()
//
//    //CREATE
//    val id = userDao.add(
//        User(
//            username = "bill.gates",
//            password = "pass",
//            firstName = "Bill",
//            lastName = "Gates",
//            email = "bill.gates@gmail.com",
//            isAdmin = false
//        )
//    )
//
//    resumeDao.add(
//        ResumeDataModel(
//            userId = id,
//            personalInfo = null,
//            summary = null,
//            workHistory = emptyList(),
//            education = emptyList(),
//            languages = emptyList()
//        )
//    )

    //READ
//    val allUsers = userDao.getAll()
//    println("All users : $allUsers")
}
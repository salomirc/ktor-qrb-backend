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
}
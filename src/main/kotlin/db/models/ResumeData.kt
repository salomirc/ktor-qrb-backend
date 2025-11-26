package com.bitrabbit.db.models

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object ResumeData : Table() {
    val id: Column<Int> = integer("id").autoIncrement()

    // Foreign key to USERS
    val userId: Column<Int> =
        integer("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)

    // Personal info
    val fullName: Column<String?> = varchar("full_name", 200).nullable()
    val title: Column<String?> = varchar("title", 200).nullable()
    val address: Column<String?> = varchar("address", 300).nullable()
    val phone: Column<String?> = varchar("phone", 100).nullable()
    val email: Column<String?> = varchar("email", 200).nullable()
    val linkedIn: Column<String?> = varchar("linkedin", 300).nullable()

    val summary: Column<String?> = text("summary").nullable()

    override val primaryKey = PrimaryKey(id)
}

object ResumeWorkHistory : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val resumeId: Column<Int> =
        integer("resume_id").references(ResumeData.id, onDelete = ReferenceOption.CASCADE)

    val position: Column<String?> = varchar("position", 200).nullable()
    val company: Column<String?> = varchar("company", 200).nullable()
    val location: Column<String?> = varchar("location", 200).nullable()
    val description: Column<String?> = text("description").nullable()
    val startDate: Column<String?> = varchar("start_date", 50).nullable()
    val endDate: Column<String?> = varchar("end_date", 50).nullable()

    override val primaryKey = PrimaryKey(id)
}

object ResumeEducation : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val resumeId: Column<Int> =
        integer("resume_id").references(ResumeData.id, onDelete = ReferenceOption.CASCADE)

    val institution: Column<String?> = varchar("institution", 200).nullable()
    val title: Column<String?> = varchar("title", 200).nullable()
    val description: Column<String?> = text("description").nullable()
    val startDate: Column<String?> = varchar("start_date", 50).nullable()
    val endDate: Column<String?> = varchar("end_date", 50).nullable()

    override val primaryKey = PrimaryKey(id)
}

object ResumeLanguage : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val resumeId: Column<Int> =
        integer("resume_id").references(ResumeData.id, onDelete = ReferenceOption.CASCADE)

    val name: Column<String?> = varchar("name", 100).nullable()
    val proficiency: Column<String?> = varchar("proficiency", 100).nullable()

    override val primaryKey = PrimaryKey(id)
}

data class ResumeDataModel(
    val id: Int = 0,
    val userId: Int,
    val personalInfo: PersonalInfo?,
    val summary: String?,
    val workHistory: List<WorkHistory>,
    val education: List<Education>,
    val languages: List<Language>
) {
    data class PersonalInfo(
        val fullName: String?,
        val title: String?,
        val address: String?,
        val phone: String?,
        val email: String?,
        val linkedIn: String?
    )
    data class WorkHistory(
        val position: String?,
        val company: String?,
        val location: String?,
        val description: String?,
        val startDate: String?,
        val endDate: String?
    )
    data class Education(
        val institution: String?,
        val title: String?,
        val description: String?,
        val startDate: String?,
        val endDate: String?
    )
    data class Language(
        val name: String?,
        val proficiency: String?
    )
}


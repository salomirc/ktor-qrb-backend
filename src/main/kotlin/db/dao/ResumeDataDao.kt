package com.bitrabbit.db.dao

import com.bitrabbit.db.models.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

interface IResumeDao {

    // CREATE
    fun add(resume: ResumeDataModel): Int

    // READ single resume by resumeId
    fun getById(resumeId: Int): ResumeDataModel?

    // READ all resumes for a user
    fun getAllByUserId(userId: Int): List<ResumeDataModel>

    // UPDATE by resumeId
    fun update(resumeId: Int, resume: ResumeDataModel): Boolean

    // DELETE by resumeId
    fun delete(resumeId: Int): Boolean
}


class ResumeDao : IResumeDao {

    // --------------------------------------------------
    // CREATE
    // --------------------------------------------------
    override fun add(resume: ResumeDataModel): Int {
        val resumeId = ResumeData.insert {
            it[userId] = resume.userId
            it[fullName] = resume.personalInfo?.fullName
            it[title] = resume.personalInfo?.title
            it[address] = resume.personalInfo?.address
            it[phone] = resume.personalInfo?.phone
            it[email] = resume.personalInfo?.email
            it[linkedIn] = resume.personalInfo?.linkedIn
            it[summary] = resume.summary
        } get ResumeData.id

        // Insert Work History
        resume.workHistory.forEach { wh ->
            ResumeWorkHistory.insert {
                it[ResumeWorkHistory.resumeId] = resumeId
                it[position] = wh.position
                it[company] = wh.company
                it[location] = wh.location
                it[description] = wh.description
                it[startDate] = wh.startDate
                it[endDate] = wh.endDate
            }
        }

        // Insert Education
        resume.education.forEach { ed ->
            ResumeEducation.insert {
                it[ResumeEducation.resumeId] = resumeId
                it[institution] = ed.institution
                it[title] = ed.title
                it[description] = ed.description
                it[startDate] = ed.startDate
                it[endDate] = ed.endDate
            }
        }

        // Insert Languages
        resume.languages.forEach { lg ->
            ResumeLanguage.insert {
                it[ResumeLanguage.resumeId] = resumeId
                it[name] = lg.name
                it[proficiency] = lg.proficiency
            }
        }

        return resumeId
    }

    // --------------------------------------------------
    // READ SINGLE resume
    // --------------------------------------------------
    override fun getById(resumeId: Int): ResumeDataModel? {
        val row = ResumeData
            .selectAll()
            .where { ResumeData.id eq resumeId }
            .singleOrNull() ?: return null

        val personalInfo = ResumeDataModel.PersonalInfo(
            fullName = row[ResumeData.fullName],
            title = row[ResumeData.title],
            address = row[ResumeData.address],
            phone = row[ResumeData.phone],
            email = row[ResumeData.email],
            linkedIn = row[ResumeData.linkedIn],
        )

        val summary = row[ResumeData.summary]
        val userId = row[ResumeData.userId]

        val workHistory = ResumeWorkHistory
            .selectAll()
            .where { ResumeWorkHistory.resumeId eq resumeId }
            .map {
                ResumeDataModel.WorkHistory(
                    position = it[ResumeWorkHistory.position],
                    company = it[ResumeWorkHistory.company],
                    location = it[ResumeWorkHistory.location],
                    description = it[ResumeWorkHistory.description],
                    startDate = it[ResumeWorkHistory.startDate],
                    endDate = it[ResumeWorkHistory.endDate],
                )
            }

        val education = ResumeEducation
            .selectAll()
            .where { ResumeEducation.resumeId eq resumeId }
            .map {
                ResumeDataModel.Education(
                    institution = it[ResumeEducation.institution],
                    title = it[ResumeEducation.title],
                    description = it[ResumeEducation.description],
                    startDate = it[ResumeEducation.startDate],
                    endDate = it[ResumeEducation.endDate],
                )
            }

        val languages = ResumeLanguage
            .selectAll()
            .where { ResumeLanguage.resumeId eq resumeId }
            .map {
                ResumeDataModel.Language(
                    name = it[ResumeLanguage.name],
                    proficiency = it[ResumeLanguage.proficiency],
                )
            }

        return ResumeDataModel(
            id = resumeId,
            userId = userId,
            personalInfo = personalInfo,
            summary = summary,
            workHistory = workHistory,
            education = education,
            languages = languages
        )
    }

    // --------------------------------------------------
    // GET ALL resumes for a user
    // --------------------------------------------------
    override fun getAllByUserId(userId: Int): List<ResumeDataModel> =
        ResumeData
            .selectAll()
            .where { ResumeData.userId eq userId }
            .mapNotNull { row -> getById(row[ResumeData.id]) }


    // --------------------------------------------------
    // UPDATE resume
    // --------------------------------------------------
    override fun update(resumeId: Int, resume: ResumeDataModel): Boolean {

        val updated = ResumeData.update({ ResumeData.id eq resumeId }) {
            it[fullName] = resume.personalInfo?.fullName
            it[title] = resume.personalInfo?.title
            it[address] = resume.personalInfo?.address
            it[phone] = resume.personalInfo?.phone
            it[email] = resume.personalInfo?.email
            it[linkedIn] = resume.personalInfo?.linkedIn
            it[summary] = resume.summary
        } > 0

        if (!updated) return false

        // delete old lists
        ResumeWorkHistory.deleteWhere { ResumeWorkHistory.resumeId eq resumeId }
        ResumeEducation.deleteWhere { ResumeEducation.resumeId eq resumeId }
        ResumeLanguage.deleteWhere { ResumeLanguage.resumeId eq resumeId }

        // Insert new WorkHistory
        resume.workHistory.forEach { wh ->
            ResumeWorkHistory.insert {
                it[ResumeWorkHistory.resumeId] = resumeId
                it[position] = wh.position
                it[company] = wh.company
                it[location] = wh.location
                it[description] = wh.description
                it[startDate] = wh.startDate
                it[endDate] = wh.endDate
            }
        }

        // Insert new Education
        resume.education.forEach { ed ->
            ResumeEducation.insert {
                it[ResumeEducation.resumeId] = resumeId
                it[institution] = ed.institution
                it[title] = ed.title
                it[description] = ed.description
                it[startDate] = ed.startDate
                it[endDate] = ed.endDate
            }
        }

        // Insert new Languages
        resume.languages.forEach { lg ->
            ResumeLanguage.insert {
                it[ResumeLanguage.resumeId] = resumeId
                it[name] = lg.name
                it[proficiency] = lg.proficiency
            }
        }

        return true
    }

    // --------------------------------------------------
    // DELETE
    // --------------------------------------------------
    override fun delete(resumeId: Int): Boolean {
        ResumeWorkHistory.deleteWhere { ResumeWorkHistory.resumeId eq resumeId }
        ResumeEducation.deleteWhere { ResumeEducation.resumeId eq resumeId }
        ResumeLanguage.deleteWhere { ResumeLanguage.resumeId eq resumeId }
        return ResumeData.deleteWhere { ResumeData.id eq resumeId } > 0
    }
}



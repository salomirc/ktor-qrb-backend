package com.bitrabbit.db.dao

import com.bitrabbit.db.models.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

interface IResumeDao {
    fun add(resume: ResumeDataModel): Int
    fun getById(id: Int): ResumeDataModel?
    fun update(id: Int, resume: ResumeDataModel): Boolean
    fun delete(id: Int): Boolean
}

class ResumeDao : IResumeDao {

    override fun add(resume: ResumeDataModel): Int {
        val resumeId = ResumeData.insert {
            it[fullName] = resume.personalInfo.fullName
            it[title] = resume.personalInfo.title
            it[address] = resume.personalInfo.address
            it[phone] = resume.personalInfo.phone
            it[email] = resume.personalInfo.email
            it[linkedIn] = resume.personalInfo.linkedIn
            it[summary] = resume.summary
        } get ResumeData.id

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

        resume.languages.forEach { lang ->
            ResumeLanguage.insert {
                it[ResumeLanguage.resumeId] = resumeId
                it[name] = lang.name
                it[proficiency] = lang.proficiency
            }
        }

        return resumeId
    }

    override fun getById(id: Int): ResumeDataModel? {
        val row = ResumeData.selectAll().where { ResumeData.id eq id }.singleOrNull() ?: return null

        val personalInfo = ResumeDataModel.PersonalInfo(
            fullName = row[ResumeData.fullName],
            title = row[ResumeData.title],
            address = row[ResumeData.address],
            phone = row[ResumeData.phone],
            email = row[ResumeData.email],
            linkedIn = row[ResumeData.linkedIn],
        )

        val summary = row[ResumeData.summary]

        val workHistory = ResumeWorkHistory
            .selectAll().where { ResumeWorkHistory.resumeId eq id }
            .map { wh ->
                ResumeDataModel.WorkHistory(
                    position = wh[ResumeWorkHistory.position],
                    company = wh[ResumeWorkHistory.company],
                    location = wh[ResumeWorkHistory.location],
                    description = wh[ResumeWorkHistory.description],
                    startDate = wh[ResumeWorkHistory.startDate],
                    endDate = wh[ResumeWorkHistory.endDate],
                )
            }

        val education = ResumeEducation
            .selectAll().where { ResumeEducation.resumeId eq id }
            .map { ed ->
                ResumeDataModel.Education(
                    institution = ed[ResumeEducation.institution],
                    title = ed[ResumeEducation.title],
                    description = ed[ResumeEducation.description],
                    startDate = ed[ResumeEducation.startDate],
                    endDate = ed[ResumeEducation.endDate],
                )
            }

        val languages = ResumeLanguage
            .selectAll().where { ResumeLanguage.resumeId eq id }
            .map { lg ->
                ResumeDataModel.Language(
                    name = lg[ResumeLanguage.name],
                    proficiency = lg[ResumeLanguage.proficiency],
                )
            }

        return ResumeDataModel(
            personalInfo = personalInfo,
            summary = summary,
            workHistory = workHistory,
            education = education,
            languages = languages
        )
    }

    override fun update(id: Int, resume: ResumeDataModel): Boolean {
        val updated = ResumeData.update({ ResumeData.id eq id }) {
            it[fullName] = resume.personalInfo.fullName
            it[title] = resume.personalInfo.title
            it[address] = resume.personalInfo.address
            it[phone] = resume.personalInfo.phone
            it[email] = resume.personalInfo.email
            it[linkedIn] = resume.personalInfo.linkedIn
            it[summary] = resume.summary
        } > 0

        // remove old lists
        ResumeWorkHistory.deleteWhere { resumeId eq id }
        ResumeEducation.deleteWhere { resumeId eq id }
        ResumeLanguage.deleteWhere { resumeId eq id }

        // insert new lists
        resume.workHistory.forEach { wh ->
            ResumeWorkHistory.insert {
                it[resumeId] = id
                it[position] = wh.position
                it[company] = wh.company
                it[location] = wh.location
                it[description] = wh.description
                it[startDate] = wh.startDate
                it[endDate] = wh.endDate
            }
        }

        resume.education.forEach { ed ->
            ResumeEducation.insert {
                it[resumeId] = id
                it[institution] = ed.institution
                it[title] = ed.title
                it[description] = ed.description
                it[startDate] = ed.startDate
                it[endDate] = ed.endDate
            }
        }

        resume.languages.forEach { lg ->
            ResumeLanguage.insert {
                it[resumeId] = id
                it[name] = lg.name
                it[proficiency] = lg.proficiency
            }
        }

        return updated
    }

    override fun delete(id: Int): Boolean {
        ResumeWorkHistory.deleteWhere { resumeId eq id }
        ResumeEducation.deleteWhere { resumeId eq id }
        ResumeLanguage.deleteWhere { resumeId eq id }
        return ResumeData.deleteWhere { ResumeData.id eq id } > 0
    }
}


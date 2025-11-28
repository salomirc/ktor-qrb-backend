package com.bitrabbit.db

import MigrationUtils
import com.bitrabbit.db.initDSL.initQuickResumeBuilderDb
import com.bitrabbit.db.models.ResumeData
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.config.*
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ExperimentalDatabaseMigrationApi
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init(config: ApplicationConfig) {
        val dbConfig = config.config("ktor.database")
        val url = dbConfig.property("url").getString()
        val driver = dbConfig.property("driver").getString()
        val user = dbConfig.property("user").getString()
        val password = dbConfig.property("password").getString()
        val migrationDir = dbConfig.property("migration").getString()
        val hikariConfig = HikariConfig().apply {
            this.jdbcUrl = url
            this.driverClassName = driver
            this.username = user
            this.password = password

            // ⭐ Tuned for 1 GB RAM servers (best settings)
            maximumPoolSize = 3            // limit to 3 connections total
            minimumIdle = 1                // only one idle connection
            idleTimeout = 30_000           // keep idle connections for 30s

            // ⭐ Stability settings
            connectionTimeout = 10_000     // fail fast if pool is exhausted
            maxLifetime = 1_200_000        // 20 min (below MySQL's 28 min death)
            validationTimeout = 5_000      // quick validation timeout

            // ⭐ Detect real leaks without false positives
            leakDetectionThreshold = 10_000 // warn if held >10s

            // ⭐ Required for Exposed ORM
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        }

        val dataSource = HikariDataSource(hikariConfig)
        Database.connect(dataSource)
        println("✅ Connected to MySQL database: $url")
        preventMetadataLeak()

        val flyway = Flyway.configure()
            .dataSource(url, user, password)
            .locations("filesystem:$migrationDir")
            .baselineOnMigrate(true) // Used when migrating an existing database for the first time
            .load()

//        transaction {
//            generateMigrationScript(migrationDir)
//        }

//        transaction {
//            flyway.migrate()
//        }

        initDbTables()
    }

    private fun preventMetadataLeak() {
        // ⭐ IMPORTANT: Prevent Exposed lazy metadata leak
        transaction { }
    }

    /**
     * Initial DB schema creation (blocking, called only at startup)
     */
    private fun initDbTables() {
        // Create tables if not exist
        transaction {
            addLogger(StdOutSqlLogger)
            initQuickResumeBuilderDb()
        }
    }

    @OptIn(ExperimentalDatabaseMigrationApi::class)
    private fun generateMigrationScript(migrationsDirectory: String) {
        // Generate a migration script in the specified path
        MigrationUtils.generateMigrationScript(
            ResumeData,
            scriptDirectory = migrationsDirectory,
            scriptName = "V3__ResumeData_add_Users_id_foreign_key",
        )
    }
}
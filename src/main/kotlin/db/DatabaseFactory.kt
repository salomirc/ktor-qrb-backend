package com.bitrabbit.db

import MigrationUtils
import com.bitrabbit.db.initDSL.initQuickResumeBuilderDb
import com.bitrabbit.db.models.Users
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

            maximumPoolSize = 4
            minimumIdle = 2               // keep a few connections ready
            idleTimeout = 60_000          // 60s before idle connections are closed
            connectionTimeout = 30_000    // 30s max wait for a connection
            maxLifetime = 30 * 60_000     // recycle connections every 30 min
            leakDetectionThreshold = 20_000  // warn if connection not returned in 20s
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        }

        val dataSource = HikariDataSource(hikariConfig)
        Database.connect(dataSource)
        println("✅ Connected to MySQL database: $url")

        val flyway = Flyway.configure()
            .dataSource(url, user, password)
            .locations("filesystem:$migrationDir")
            .baselineOnMigrate(true) // Used when migrating an existing database for the first time
            .load()

//        transaction {
//            generateMigrationScript(migration)
//        }

//        transaction {
//            flyway.migrate()
//        }

        initDbTables()
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
            Users,
            scriptDirectory = migrationsDirectory,
            scriptName = "V2__drop_password_unique_index",
        )
    }
}
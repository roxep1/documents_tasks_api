package com.bashkir.extensions

import com.bashkir.models.*
import com.bashkir.serviceModule
import io.ktor.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.logger.Level
import org.koin.ktor.ext.Koin
import org.koin.logger.slf4jLogger
import java.sql.Connection
import java.sql.DriverManager

fun Application.configureKoin() =
    install(Koin) {
        slf4jLogger(level = Level.ERROR)
        modules(serviceModule)
    }

fun connectDatabase(user: String, password: String) {
    Database.connect(
        "jdbc:postgresql://localhost:5432/documents_tasks", driver = "org.postgresql.Driver",
        user = user, password = password
    )
}

fun connectDatabase() =
    Database.connect(::getConnection)

private fun getConnection(): Connection {
    val dbUrl = System.getenv("JDBC_DATABASE_URL")
    return DriverManager.getConnection(dbUrl)
}

fun createTables(firstDeploy: Boolean = false) = transaction {
    if(firstDeploy){
        exec("CREATE TYPE AgreementStatus AS ENUM ('Sent', 'Agreed', 'Declined');")
        exec("CREATE TYPE PerformStatus AS ENUM ('Waiting', 'InProgress', 'Completed');")
    }

    SchemaUtils.createMissingTablesAndColumns(
        AgreementTable,
        DocumentTable,
        FamiliarizeTable,
        PerformTable,
        RoleTable,
        TaskTable,
        TemplateTable,
        UserTable
    )
}

fun deleteTables() = transaction {
    SchemaUtils.drop(
        AgreementTable,
        DocumentTable,
        FamiliarizeTable,
        PerformTable,
        RoleTable,
        TaskTable,
        TemplateTable,
        UserTable
    )
    exec("DROP TYPE IF EXISTS AgreementStatus CASCADE")
    exec("DROP TYPE IF EXISTS PerformStatus CASCADE")
}
package com.bashkir.extensions

import com.bashkir.models.*
import com.bashkir.serviceModule
import io.ktor.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.ktor.ext.Koin
import org.koin.logger.slf4jLogger

fun Application.configureKoin() {
    install(Koin) {
        slf4jLogger()
        modules(serviceModule)
    }
}

fun connectDatabase(user: String, password: String) {
    Database.connect(
        "jdbc:postgresql://localhost:5432/documents_tasks", driver = "org.postgresql.Driver",
        user = user, password = password
    )
}

fun createTables() = transaction{
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
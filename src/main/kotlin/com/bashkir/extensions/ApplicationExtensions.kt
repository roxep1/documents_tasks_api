package com.bashkir.extensions

import com.bashkir.models.*
import com.bashkir.modulesDI
import com.bashkir.retrofit.Authorizer
import com.bashkir.retrofit.ServiceAccount
import com.bashkir.retrofit.models.GoogleAccountInfo
import com.bashkir.services.UserService
import io.ktor.application.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.logger.Level
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.inject
import org.koin.logger.slf4jLogger
import java.sql.Connection
import java.sql.DriverManager
import kotlin.time.Duration.Companion.days
import kotlin.time.ExperimentalTime

fun Application.configureKoin() =
    install(Koin) {
        slf4jLogger(level = Level.ERROR)
        modules(modulesDI)
    }

fun connectDatabase(user: String, password: String, database: String = "documents_tasks") {
    Database.connect(
        "jdbc:postgresql://localhost:5432/$database", driver = "org.postgresql.Driver",
        user = user, password = password
    )
}

fun connectDatabase() =
    Database.connect(::getConnection)

private fun getConnection(): Connection {
    val dbUrl = System.getenv("JDBC_DATABASE_URL")
    return DriverManager.getConnection(dbUrl)
}

fun createTables() = transaction {
    exec(
        "DO \$\$ BEGIN\n" +
                "    CREATE TYPE AgreementStatus AS ENUM ('Sent', 'Agreed', 'Declined');\n" +
                "    CREATE TYPE PerformStatus AS ENUM ('Waiting', 'InProgress', 'Completed');" +
                "EXCEPTION\n" +
                "    WHEN duplicate_object THEN null;\n" +
                "END \$\$;"
    )

    SchemaUtils.createMissingTablesAndColumns(
        AgreementTable,
        FileTable,
        DocumentTable,
        FamiliarizeTable,
        PerformTable,
        RoleTable,
        TaskTable,
        TemplateTable,
        UserTable
    )
    if (Role.findById("Admin") == null)
        Role.new("Admin") {}

    if (Role.findById("Employee") == null)
        Role.new("Employee") {}
}

private suspend fun Application.authorizeService(): String {
    val retrofitAuth: Authorizer by inject()
    val token: String by inject(qualifier = named("jwtToken")) { parametersOf(environment) }
    return retrofitAuth.getToken(token).accessToken
}

suspend fun Application.getUsers(): List<GoogleAccountInfo> {
    val serviceAccount: ServiceAccount by inject()
    val token = authorizeService()
    val users = arrayListOf<GoogleAccountInfo>()
    var pageToken: String? = null
    do {
        val response = serviceAccount.getUsers(token, pageToken ?: "")
        users.addAll(response.googleAccountInfos.filter { user -> !user.email.startsWith("st_") })
        pageToken = response.nextPageToken
    } while (pageToken != null)
    return users
}

suspend fun Application.writeUsers() = coroutineScope {
    val service: UserService by inject()
    val users = getUsers()
    println("Получено ${users.count()} пользователей...")
    users.forEach { user -> launch { service.add(user) } }
}


@Suppress("EXPERIMENTAL_IS_NOT_ENABLED")
@OptIn(ExperimentalTime::class)
tailrec suspend fun Application.scheduleUpdateUsers() {
    coroutineScope {
        launch {
            writeUsers()
            println("Запись пользователей завершена.")
        }
        delay(1.days)
    }
    scheduleUpdateUsers()
}
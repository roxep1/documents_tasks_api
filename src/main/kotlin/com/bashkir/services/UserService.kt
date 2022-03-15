package com.bashkir.services

import com.bashkir.models.*
import org.jetbrains.exposed.sql.transactions.transaction

class UserService {

    fun getUser(id: String): User.Model? = transaction { User.findById(id)?.toModel() }

    fun getAllUsers(ids: List<String>): List<User.Model> = ids.map { getUser(it)!! }

    fun getAllUsers(): List<User.Model> = transaction { User.all().map { it.toModel() } }

    fun getTasksToDo(id: String): List<Task.Model> = transaction {
        User[id].tasksToDo.map { it.task.toModel() }
    }

    fun getGivenTasks(id: String): List<Task.Model> =
        transaction {
            User[id].givenTasks.map { it.toModel() }
        }

    fun getAllUserTasks(id: String): List<Task.Model> = getGivenTasks(id)
        .plus(getTasksToDo(id))

    fun getFamiliarizes(id: String): List<Familiarize.Model> =
        transaction { User[id].familiarizes.map { it.toModel() } }

    fun getAgreements(id: String): List<Agreement.Model> =
        transaction { User[id].agreements.map { it.toModel() } }

    fun getCreatedDocuments(id: String): List<Document.Model> =
        transaction { User[id].createdDocuments.map { it.toModel() } }

    fun setRole(id: String, role: Role.Model) = transaction { User[id].role = Role[role.name!!] }
}
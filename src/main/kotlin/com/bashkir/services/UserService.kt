package com.bashkir.services

import com.bashkir.models.Agreement
import com.bashkir.models.Familiarize
import com.bashkir.models.Task
import com.bashkir.models.User
import org.jetbrains.exposed.sql.transactions.transaction

class UserService {

    fun getUser(id: String): User.Model? = transaction { User.findById(id)?.toModel() }

    fun getAllUsers(ids: List<String>): List<User.Model> = ids.map { getUser(it)!! }

    fun getAllUsers(): List<User.Model> = transaction { User.all().map { it.toModel() } }

    fun getTasksToDo(id: String): List<*> = transaction {
        val user = User[id]
        user.tasksToDo.toList().plus(user.agreements).plus(user.familiarizes)
    }

    fun getGivenTasks(id: String): List<Task.Model> =
        transaction {
            User[id].givenTasks.map { it.toModel() }
        }

    fun getAllUserTasks(id: String): List<Task.Model> = getGivenTasks(id)
//        .plus(getTasksToDo(id))

    fun getFamiliarizes(id: String): List<Familiarize.Model> =
        transaction { User[id].familiarizes.map { it.toModel() } }

    fun getAgreements(id: String): List<Agreement.Model> =
        transaction { User[id].agreements.map { it.toModel() } }
}
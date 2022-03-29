package com.bashkir.services

import com.bashkir.models.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class TaskService {
    fun addTask(model: Task.Model) = transaction {
        val task = Task.new {
            title = model.title!!
            desc = model.desc!!
            created = LocalDateTime.now()
            deadline = LocalDateTime.parse(model.deadline)
            author = User[model.author!!.id!!]
        }

        model.performs.forEach { performer ->
            Perform.new {
                user = User[performer.user!!.id!!]
                this.task = task
                status = PerformStatus.Waiting
                comment = null
                statusChanged = null
            }
        }
    }

    fun addCommentToPerform(performId: Int, comment: String) = transaction {
        Perform[performId].comment = comment
    }

    fun changePerformStatus(performId: Int, status: PerformStatus) = transaction {
        Perform[performId].run {
            this.status = status
            statusChanged = LocalDateTime.now()
        }
    }

    fun inProgressAllPerforms(ids: List<Int>) = ids.forEach { changePerformStatus(it, PerformStatus.InProgress) }

    fun deleteTask(id: Int) = transaction {
        Task[id].run {
            Document.find { DocumentTable.perform inList performs.map { it.id } }.forEach { it.delete() }
            performs.forEach { it.delete() }
            delete()
        }
    }
}
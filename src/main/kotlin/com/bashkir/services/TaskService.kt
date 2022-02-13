package com.bashkir.services

import com.bashkir.models.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class TaskService {
    fun addTask(model: Task.Model) = transaction {
        Task.new {
            title = model.title
            desc = model.desc
            created = LocalDateTime.now()
            deadline = LocalDateTime.parse(model.deadline)
            author = User[model.authorId]
        }

        model.performs.forEach { performer ->
            Perform.new {
                user = User[performer.userId]
                task = Task[performer.taskId]
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
        Perform[performId].run{
            this.status = status
            statusChanged = LocalDateTime.now()
        }
    }
}
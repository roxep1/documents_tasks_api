package com.bashkir.services

import com.bashkir.models.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.java.KoinJavaComponent.inject
import java.time.LocalDateTime

class TaskService {
    private val documentService: DocumentService by inject(DocumentService::class.java)

    fun addTask(model: TaskWithFiles) = transaction {
        val task = Task.new {
            title = model.task.title!!
            desc = model.task.desc!!
            created = LocalDateTime.now()
            deadline = LocalDateTime.parse(model.task.deadline)
            author = User[model.task.author!!.id!!]
        }

        model.task.performs.forEach { performer ->
            Perform.new {
                user = User[performer.user!!.id!!]
                this.task = task
                status = PerformStatus.Waiting
                comment = null
                statusChanged = null
            }
        }

        model.task.documents.forEachIndexed { index, document ->
            documentService.addDocument(DocumentWithFile(document, model.files[index]))
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
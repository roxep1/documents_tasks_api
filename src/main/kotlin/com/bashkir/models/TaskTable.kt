package com.bashkir.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.transactions.transaction

object TaskTable : IntIdTable("task") {
    val title = varchar("title", 100)
    val desc = varchar("description", 300)
    val created = datetime("created")
    val deadline = datetime("deadline")
    val author = reference("author", UserTable)
}

class Task(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Task>(TaskTable)

    var title by TaskTable.title
    var desc by TaskTable.desc
    var created by TaskTable.created
    var deadline by TaskTable.deadline
    var author by User referencedOn TaskTable.author
    val performs by Perform referrersOn PerformTable.task

    @Serializable
    data class Model(@Transient val model: Task? = null) {
        val id = model?.id?.value
        val title = model?.title
        val desc = model?.desc
        val created = model?.created?.toString()
        val deadline = model?.deadline?.toString()
        val author = model?.author?.toModel()
        val performs = transaction { model?.performs?.map { it.toModel() } }
    }

    fun toModel(): Model = Model(this)
}
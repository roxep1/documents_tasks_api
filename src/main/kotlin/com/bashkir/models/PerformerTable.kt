package com.bashkir.models

import com.bashkir.PGEnum
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ColumnSet
import org.jetbrains.exposed.sql.ResultRow

object PerformerTable : IntIdTable("performer") {
    val user = reference("user", UserTable)
    val task = reference("task", TaskTable)
    val status = customEnumeration(
        "status",
        "status",
        { value -> TaskStatus.valueOf(value as String) },
        { PGEnum("TaskStatus", it) })
    val comment = varchar("comment", 300)
}

class Performer(id: EntityID<Int>): IntEntity(id){
    companion object : IntEntityClass<Agreement>(AgreementTable) {
        override val dependsOnTables: ColumnSet = UserTable.innerJoin(AgreementTable).innerJoin(DocumentTable)
        override fun createInstance(entityId: EntityID<Int>, row: ResultRow?): Agreement {

            row?.getOrNull(UserTable.id)?.let {
                User.wrap(it, row)
            }
            row?.getOrNull(DocumentTable.id)?.let {
                Document.wrap(it, row)
            }

            return super.createInstance(entityId, row)
        }
    }

    var user by User referencedOn PerformerTable.user
    var task by Task referencedOn PerformerTable.task
    var status by PerformerTable.status
    var comment by PerformerTable.comment

    @Serializable
    data class Model(@Transient val model: Performer? = null){
        val id = model!!.id.value
        val userId = model!!.user.id.value
        val taskId = model!!.task.id.value
        val status = model!!.status
        val comment = model!!.comment
    }
}
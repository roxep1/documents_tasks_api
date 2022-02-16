package com.bashkir.models

import com.bashkir.extensions.PGEnum
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ColumnSet
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.javatime.datetime

object PerformTable : IntIdTable("performer") {
    val user = reference("user", UserTable)
    val task = reference("task", TaskTable)
    val status = customEnumeration(
        "status",
        "PerformStatus",
        { value -> PerformStatus.valueOf(value as String) },
        { PGEnum("PerformStatus", it) })
    val comment = varchar("comment", 300).nullable()
    val statusChanged = datetime("statusChanged").nullable()
}

class Perform(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Perform>(PerformTable) {
        override val dependsOnTables: ColumnSet = UserTable.innerJoin(TaskTable).innerJoin(PerformTable)
        override fun createInstance(entityId: EntityID<Int>, row: ResultRow?): Perform {

            row?.getOrNull(UserTable.id)?.let {
                User.wrap(it, row)
            }
            row?.getOrNull(TaskTable.id)?.let {
                Task.wrap(it, row)
            }

            return super.createInstance(entityId, row)
        }
    }

    var user by User referencedOn PerformTable.user
    var task by Task referencedOn PerformTable.task
    var status by PerformTable.status
    var comment by PerformTable.comment
    var statusChanged by PerformTable.statusChanged
    val documents by Document optionalReferrersOn DocumentTable.perform

    @Serializable
    data class Model(@Transient val model: Perform? = null) {
        val id = model?.id?.value
        val user = model?.user?.toModel()
        val taskId = model?.task?.id?.value
        val status = model?.status
        val comment = model?.comment
        val statusChanged = model?.statusChanged?.toString()
        val documents = model?.documents?.map { it.toModel() } ?: listOf()
    }

    fun toModel(): Model = Model(this)
}
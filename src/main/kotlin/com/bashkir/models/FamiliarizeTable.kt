package com.bashkir.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.javatime.datetime

object FamiliarizeTable : IntIdTable("familiarize") {

    val user = reference("user", UserTable)
    val document = reference("document", DocumentTable)
    val checked = bool("checked")
    val created = datetime("created")
}

class Familiarize(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Familiarize>(FamiliarizeTable) {

        override fun createInstance(entityId: EntityID<Int>, row: ResultRow?): Familiarize {

            row?.getOrNull(UserTable.id)?.let {
                User.wrap(it, row)
            }
            row?.getOrNull(DocumentTable.id)?.let {
                Document.wrap(it, row)
            }

            return super.createInstance(entityId, row)
        }
    }

    var user by User referencedOn FamiliarizeTable.user
    var document by Document referencedOn FamiliarizeTable.document
    var checked by FamiliarizeTable.checked
    var created by FamiliarizeTable.created

    fun toModel(): Model = Model(this)

    @Serializable
    data class Model(@Transient val model: Familiarize? = null) {
        val id = model?.id?.value
        val userId = model?.user?.id?.value
        val documentId = model?.document?.id?.value
        val familiarized = model?.checked
        val created = model?.created.toString()
    }
}
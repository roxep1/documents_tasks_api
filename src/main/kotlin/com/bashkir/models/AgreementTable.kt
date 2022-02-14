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

object AgreementTable : IntIdTable("agreement") {

    val user = reference("user", UserTable)
    val document = reference("document", DocumentTable)
    val deadline = datetime("deadline")
    val status = customEnumeration(
        "status",
        null,
        { value -> AgreementStatus.valueOf(value as String) },
        { PGEnum("AgreementStatus", it) })
    val comment = varchar("comment", 300).nullable()
    val created = datetime("created")
    val statusChanged = datetime("status_changed").nullable()
}

class Agreement(id: EntityID<Int>) : IntEntity(id) {
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

    var user by User referencedOn AgreementTable.user
    var document by Document referencedOn AgreementTable.document
    var deadline by AgreementTable.deadline
    var status by AgreementTable.status
    var comment by AgreementTable.comment
    var created by AgreementTable.created
    var statusChanged by AgreementTable.statusChanged

    @Serializable
    data class Model(@Transient val model: Agreement? = null) {
        val id = model!!.id.value
        val userId = model!!.user.id.value
        val documentId = model!!.document.id.value
        val deadline = model!!.deadline.toString()
        val status = model!!.status
        val comment = model!!.comment
        val created = model!!.created.toString()
        val statusChanged = model!!.statusChanged?.toString()
    }

    fun toModel(): Model = Model(this)
}
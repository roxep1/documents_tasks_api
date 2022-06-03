package com.bashkir.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime

object DocumentTable : IntIdTable("documents", "documents_id") {
    val template = reference("template_id", TemplateTable).nullable()
    val author = reference("author", UserTable)
    val title = varchar("title", 200)
    val desc = varchar("description", 300).nullable()
    val created = datetime("created")
    val file = reference("file_id", FileTable)
    val perform = reference("perform_id", PerformTable).nullable()
    val task = reference("task_id", TaskTable).nullable()
}


class Document(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Document>(DocumentTable)

    var template by Template optionalReferencedOn DocumentTable.template
    var author by User referencedOn DocumentTable.author
    var title by DocumentTable.title
    var desc by DocumentTable.desc
    var file by File referencedOn DocumentTable.file
    var created by DocumentTable.created
    var perform by Perform optionalReferencedOn DocumentTable.perform
    var task by Task optionalReferencedOn DocumentTable.task
    val familiarize by Familiarize referrersOn FamiliarizeTable.document
    val agreement by Agreement referrersOn AgreementTable.document

    @Serializable
    data class Model(
        @Transient val model: Document? = null,
        @Transient val withFamiliarizesAndAgreements: Boolean = true
    ) {
        val id = model?.id?.value ?: -1
        val taskId = model?.task?.id?.value
        val templateId = model?.template?.id?.value
        val author = model?.author?.toModel()
        val title = model?.title
        val desc = model?.desc
        val created = model?.created.toString()
        val familiarize =
            if (withFamiliarizesAndAgreements) model?.familiarize?.map { it.toModel() } ?: listOf() else listOf()
        val agreement =
            if (withFamiliarizesAndAgreements) model?.agreement?.map { it.toModel() } ?: listOf() else listOf()
    }

    fun toModel(withFamiliarizesAndAgreements: Boolean = true): Model = Model(this, withFamiliarizesAndAgreements)
}
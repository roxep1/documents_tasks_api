package com.bashkir.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object FileTable : IntIdTable("files", "file_id") {
    val name = varchar("name", 100)
    val size = float("mb_size").nullable()
    val file = binary("file")
    val extension = varchar("extension", 6)
}

class File(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<File>(FileTable)

    var name by FileTable.name
    var size by FileTable.size
    var file by FileTable.file
    var extension by FileTable.extension

    @Serializable
    data class Model(
        @Transient val model: File? = null
    ) {
        val id = model?.id?.value
        val name = model?.name
        val size = model?.size
        val file = model?.file
        val extension = model?.extension
    }

    fun toModel(): Model = Model(this)
}
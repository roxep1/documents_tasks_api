package com.bashkir.models

import com.bashkir.extensions.StringEntityClass
import com.bashkir.extensions.StringIdTable
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.id.EntityID

object TemplateTable : StringIdTable("template", "name", 50) {
    val file = text("file")
}

class Template(id: EntityID<String>): Entity<String>(id){
    companion object: StringEntityClass<Template>(TemplateTable)

    var file by TemplateTable.file

    @Serializable
    data class Model(@Transient val model: Template? = null){
        val name = model!!.id.value
        val file = model!!.file
    }

    fun toModel(): Model = Model(this)
}
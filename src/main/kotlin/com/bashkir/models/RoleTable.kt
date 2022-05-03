package com.bashkir.models

import com.bashkir.StringEntityClass
import com.bashkir.StringIdTable
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.id.EntityID

object RoleTable : StringIdTable("role", "role_name", 70)

class Role(id: EntityID<String>) : Entity<String>(id) {
    companion object : StringEntityClass<Role>(RoleTable)

    @Serializable
    data class Model(@Transient val model: Role? = null) {
        val name = model?.id?.value
    }

    fun toModel(): Model = Model(this)
}
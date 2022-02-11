package com.bashkir.models

import com.bashkir.extensions.StringEntityClass
import com.bashkir.extensions.StringIdTable
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.id.EntityID

object UserTable: StringIdTable("user", "user_id", 30) {
    val email = varchar("email", 100)
    val firstName = varchar("first_name", 100)
    val secondName = varchar("second_name", 100)
    val middleName = varchar("middle_name", 100).nullable()
    val password = varchar("password", 50).nullable()
    val role = reference("role", RoleTable).nullable()
}

class User(id: EntityID<String>) : Entity<String>(id) {
    companion object : StringEntityClass<User>(UserTable)

    var firstName by UserTable.firstName
    var secondName by UserTable.secondName
    var middleName by UserTable.middleName
    var email by UserTable.email
    var password by UserTable.password
    var role by Role optionalReferencedOn UserTable.role
    val familiarize by Familiarize.referrersOn(FamiliarizeTable.user)
    val agreement by Agreement.referrersOn(AgreementTable.user)
    val tasks by Performer.referrersOn(PerformerTable.user)

    @Serializable
    data class Model(@Transient val model: User? = null) {
        val id = model!!.id.value
        val firstName = model!!.firstName
        val secondName = model!!.secondName
        val middleName = model?.middleName
        val email = model!!.email
        val role = model!!.role?.id?.value
        val familiarize = model!!.familiarize.map{ it.toModel()}
        val agreement = model!!.agreement.map{ it.toModel()}
        val tasks = model!!.tasks.map{ it.toModel()}
    }

    fun toModel(): Model = Model(this)
}
package com.bashkir.services

import com.bashkir.models.Template
import org.jetbrains.exposed.sql.transactions.transaction

class TemplateService {
    fun addTemplate(model: Template.Model) = transaction {
        Template.new(model.name) {
            file = model.file!!
        }
    }

    fun getTemplate(id: String): Template.Model = transaction {
        Template[id].toModel()
    }

    fun getAllTemplates(): List<Template.Model> = transaction {
        Template.all().map { it.toModel() }
    }

    fun updateTemplate(model: Template.Model) = transaction {
        Template[model.name!!].file = model.file!!
    }
}
package com.bashkir.services

import com.bashkir.models.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class DocumentService {
    fun addDocument(model: Document.Model): Document = transaction {
        val doc = Document.new {
            model.templateId?.let {
                template = Template[model.templateId]
            }
            author = User[model.author!!.id!!]
            title = model.title!!
            file = model.file!!
            desc = model.desc
            created = LocalDateTime.now()
            ext = model.extension!!
        }

        model.familiarize.forEach {
            newFamiliarizeFromModel(it, doc)
        }

        model.agreement.forEach {
            newAgreementFromModel(it, doc)
        }
        doc
    }


    fun getAllDocuments(): List<Document.Model> = transaction { Document.all().map { it.toModel() } }

    fun getDocument(id: Int): Document.Model = transaction { Document[id].toModel() }

    fun getAllDocuments(ids: List<Int>): List<Document.Model?> = ids.map { getDocument(it) }

    fun familiarize(familiarizeId: Int) =
        transaction {
            Familiarize[familiarizeId].checked = true
        }

    fun familiarizeAll(ids: List<Int>) = ids.forEach { familiarize(it) }

    fun changeAgreementStatus(agreementId: Int, status: AgreementStatus): Agreement =
        transaction {
            Agreement[agreementId].apply {
                this.status = status
                statusChanged = LocalDateTime.now()
            }
        }

    fun changeAgreementStatus(agreementId: Int, status: AgreementStatus, comment: String) =
        transaction {
            changeAgreementStatus(agreementId, status).comment = comment
        }

    fun addDocumentToPerform(performId: Int, document: Document.Model) = transaction {
        addDocument(document).perform = Perform[performId]
    }

    fun updateDocument(model: Document.Model) = transaction {
        Document[model.id].run {
            model.templateId?.let {
                template = Template[model.templateId]
            }
            title = model.title!!
            file = model.file!!
            desc = model.desc
            ext = model.extension!!

            familiarize.map { it.user.id.value }.let { ids ->
                model.familiarize.forEach {
                    if (!ids.contains(it.user!!.id!!))
                        newFamiliarizeFromModel(it, this)
                }
            }
            agreement.map { it.user.id.value }.let { ids ->
                model.agreement.forEach {
                    if (!ids.contains(it.user!!.id!!))
                        newAgreementFromModel(it, this)
                }
            }
            familiarize.forEach {
                it.checked = false
            }
            agreement.forEach {
                it.status = AgreementStatus.Sent
            }
        }
    }

    private fun newFamiliarizeFromModel(model: Familiarize.Model, doc: Document) = transaction {
        Familiarize.new {
            user = User[model.user!!.id!!]
            document = doc
            checked = false
            created = LocalDateTime.now()
        }
    }

    private fun newAgreementFromModel(model: Agreement.Model, doc: Document) = transaction {
        Agreement.new {
            user = User[model.user!!.id!!]
            document = doc
            deadline = LocalDateTime.parse(model.deadline)
            status = AgreementStatus.Sent
            comment = null
            created = LocalDateTime.now()
            statusChanged = null
        }
    }
}
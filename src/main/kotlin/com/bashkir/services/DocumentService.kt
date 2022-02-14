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
            author = User[model.author.id]
            title = model.title
            file = model.file
            desc = model.desc
            created = LocalDateTime.now()
        }

        model.familiarize.forEach {
            Familiarize.new {
                user = User[it.userId]
                document = Document[it.documentId]
                checked = false
                created = LocalDateTime.now()
            }
        }

        model.agreement.forEach {
            Agreement.new {
                user = User[it.userId]
                document = Document[it.documentId]
                deadline = LocalDateTime.parse(it.deadline)
                status = AgreementStatus.Sent
                comment = null
                created = LocalDateTime.now()
                statusChanged = null
            }
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

}
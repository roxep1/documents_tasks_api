package com.bashkir.services

import com.bashkir.models.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class DocumentService {
    fun addDocument(model: DocumentWithFile): Document {
        val file = transaction {
            File.new {
                name = model.file.name!!
                size = model.file.size
                file = model.file.file!!
                extension = model.file.extension!!
            }
        }

        return transaction {
            val doc = Document.new {
                model.document.templateId?.let {
                    template = Template[model.document.templateId]
                }
                this.file = file
                author = User[model.document.author!!.id!!]
                title = model.document.title!!
                desc = model.document.desc
                created = LocalDateTime.now()
            }

            model.document.familiarize.forEach {
                newFamiliarizeFromModel(it, doc)
            }

            model.document.agreement.forEach {
                newAgreementFromModel(it, doc)
            }
            doc
        }
    }


    fun getAllDocuments(): List<Document.Model> = transaction { Document.all().map { it.toModel() } }

    fun getDocument(id: Int): Document.Model = transaction { Document[id].toModel() }

    fun getFile(id: Int): File.Model = transaction { Document[id].file.toModel() }

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

    fun addDocumentToPerform(performId: Int, model: DocumentWithFile) = transaction {
        addDocument(model).perform = Perform[performId]
    }

    fun updateDocument(id: Int, fileModel: File.Model) = transaction {
        Document[id].file.run {
            name = fileModel.name!!
            size = fileModel.size
            file = fileModel.file!!
            extension = fileModel.extension!!
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

    fun deleteDocument(id: Int) = transaction {
        Document[id].run{
            val fileId = file.id
            agreement.forEach{ it.delete()}
            familiarize.forEach { it.delete() }
            delete()
            File[fileId].delete()
        }
    }
}
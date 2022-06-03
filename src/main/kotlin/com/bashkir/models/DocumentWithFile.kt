package com.bashkir.models

import kotlinx.serialization.Serializable

@Serializable
data class DocumentWithFile(val document: Document.Model, val file: File.Model)

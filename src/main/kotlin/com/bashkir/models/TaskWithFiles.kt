package com.bashkir.models

import kotlinx.serialization.Serializable

@Serializable
data class TaskWithFiles(val task: Task.Model, val files: List<File.Model>)

package com.bashkir.retrofit.models

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class GoogleAccountInfo(
    val id : String,

    @SerializedName("primaryEmail")
    val email : String,

    val name : UserName
)

package com.bashkir.retrofit.models

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class UserName(
    val fullName: String,

    @SerializedName("familyName")
    val lastName: String,

    @SerializedName("givenName")
    val firstName: String
)

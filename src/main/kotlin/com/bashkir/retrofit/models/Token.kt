package com.bashkir.retrofit.models

import com.google.gson.annotations.SerializedName

@kotlinx.serialization.Serializable
data class Token(
    @SerializedName("access_token")
    val accessToken: String
)

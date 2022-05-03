package com.bashkir.retrofit.models

import com.google.gson.annotations.SerializedName


data class GoogleAccountListResponse(
    @SerializedName("users") val googleAccountInfos: List<GoogleAccountInfo>,
    val nextPageToken: String?
)

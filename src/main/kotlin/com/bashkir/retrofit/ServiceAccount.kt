package com.bashkir.retrofit

import com.bashkir.retrofit.models.GoogleAccountListResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ServiceAccount {
    @GET("users")
    suspend fun getUsers(
        @Query("access_token") token: String,
        @Query("pageToken") pageToken : String = "",
        @Query("maxResults") max: Int = 500,
        @Query("domain") domain : String = "ok654.ru"
    ): GoogleAccountListResponse
}
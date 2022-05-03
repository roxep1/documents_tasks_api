package com.bashkir.retrofit

import com.bashkir.retrofit.models.Token
import retrofit2.http.POST
import retrofit2.http.Query

interface Authorizer {

    @POST("token")
    suspend fun getToken(
        @Query("assertion") jwtToken: String,
        @Query("grant_type") grantType: String = "urn:ietf:params:oauth:grant-type:jwt-bearer"
    ): Token
}
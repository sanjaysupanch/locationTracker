package com.example.tracking.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("09575b0b-7cd4-4c79-ba7f-568cf9785dce/")
    suspend fun syncLocation(@Body location: SyncLocationRequest): Response<Void>
}
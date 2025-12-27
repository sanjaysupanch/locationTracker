package com.example.tracking.data.remote

import com.google.gson.annotations.SerializedName

data class SyncLocationRequest(
    @SerializedName("employeeId")
    val employeeId: String,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("accuracy")
    val accuracy: Float,
    @SerializedName("timestamp")
    val timestamp: Long,
    @SerializedName("speed")
    val speed: Float
)


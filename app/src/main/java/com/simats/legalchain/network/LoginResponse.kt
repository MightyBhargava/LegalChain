package com.simats.legalchain.network

import com.google.gson.annotations.SerializedName

data class LoginResponse(

    @SerializedName("status")
    val status: String,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("role")
    val role: String? = null,

    @SerializedName("user")
    val user: User? = null
)

data class User(

    @SerializedName("id")
    val id: String? = null,

    @SerializedName("full_name")
    val fullName: String? = null,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("phone")
    val phone: String? = null,

    // Lawyer-only
    @SerializedName("bar_id")
    val barId: String? = null,

    @SerializedName("document")
    val document: String? = null
)

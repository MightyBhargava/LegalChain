package com.simats.legalchain.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    /* ================= REGISTER ================= */
    @Multipart
    @POST("legalchain/api/register.php")
    fun register(
        @Part("role") role: RequestBody,
        @Part("full_name") fullName: RequestBody,
        @Part("email") email: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part("password") password: RequestBody,

        // Lawyer-only fields
        @Part("bar_id") barId: RequestBody?,
        @Part("country") country: RequestBody?,
        @Part("state") state: RequestBody?,
        @Part("district") district: RequestBody?,
        @Part("address") address: RequestBody?,

        @Part document: MultipartBody.Part?
    ): Call<RegisterResponse>

    /* ================= LOGIN ================= */
    @FormUrlEncoded
    @POST("legalchain/api/login.php")
    fun login(
        @Field("identifier") identifier: String,
        @Field("password") password: String,
        @Field("role") role: String
    ): Call<LoginResponse>

    /* ================= FORGOT PASSWORD ================= */
    @FormUrlEncoded
    @POST("legalchain/api/forgot_password.php")
    fun forgotPassword(
        @Field("email") email: String
    ): Call<BasicResponse>

    /* ================= RESET PASSWORD ================= */
    @FormUrlEncoded
    @POST("legalchain/api/reset_password.php")
    fun resetPassword(
        @Field("email") email: String,
        @Field("otp") otp: String,
        @Field("password") password: String
    ): Call<BasicResponse>

    /* ================= AI ================= */
    @POST("legalchain/api/get_ai.php")
    fun askAI(
        @Body body: Map<String, String>
    ): Call<AIResponse>
}

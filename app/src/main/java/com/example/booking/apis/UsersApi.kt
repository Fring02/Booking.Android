package com.example.booking.apis

import com.example.booking.models.LoginUser
import com.example.booking.models.RegistrationUser
import com.example.booking.models.UpdateUser
import com.example.booking.models.User
import retrofit2.Call
import retrofit2.http.*

interface UsersApi {
    @POST("users/register")
    fun register(@Body user: RegistrationUser) : Call<String>
    @POST("users/login")
    fun login(@Body user: LoginUser) : Call<String>
    @GET("users/{id}")
    fun getUserById(@Path("id") id: String, @Header("Authorization") token: String) : Call<User>
    @PUT("users/{id}")
    fun updateUserById(@Path("id") id: String, @Header("Authorization") token: String, @Body user: UpdateUser) : Call<String>
}
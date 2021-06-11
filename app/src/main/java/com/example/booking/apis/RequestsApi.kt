package com.example.booking.apis

import com.example.booking.models.BookingRequest
import com.example.booking.models.UpdateUser
import retrofit2.Call
import retrofit2.http.*

interface RequestsApi {
    @POST("requests")
    fun createRequest(@Body request: BookingRequest, @Header("Authorization") token: String) : Call<String>
    @GET("requests/check/userId={userId}&serviceId={serviceId}")
    fun checkRequest(@Path("userId") userId: String, @Path("serviceId") serviceId: String,
    @Header("Authorization") token: String) : Call<Boolean>
    @GET("requests/userId={userId}")
    fun getRequestsByUserId(@Path("userId") userId: String, @Header("Authorization") token: String) : Call<List<BookingRequest>>

    @DELETE("requests/{id}")
    fun deleteRequestById(@Path("id") id:String, @Header("Authorization") token: String) : Call<String>
}
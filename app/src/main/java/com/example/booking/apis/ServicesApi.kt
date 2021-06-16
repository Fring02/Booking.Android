package com.example.booking.apis

import com.example.booking.models.LeisureService
import com.example.booking.models.LeisureServiceElement
import com.example.booking.models.UpdateService
import retrofit2.Call
import retrofit2.http.*

interface ServicesApi {
    @GET("services")
    fun getServices() : Call<ArrayList<LeisureServiceElement>>
    @GET("services")
    fun filterServices(@Query("workingTime") workingTime: String, @Query("rating") rating: Int,
                       @Query("categoryName") categoryName: String) : Call<ArrayList<LeisureServiceElement>>
    @GET("services/{id}")
    fun getServiceById(@Path("id") id: String) : Call<LeisureService>
    @PUT("services/{id}")
    fun updateServiceRating(@Path("id") id: String, @Body service: UpdateService, @Header("Authorization") token: String) : Call<String>
}
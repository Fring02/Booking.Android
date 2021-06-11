package com.example.booking.apis

import com.example.booking.models.Category
import retrofit2.Call
import retrofit2.http.GET

interface CategoriesApi  {
    @GET("categories")
    fun getCategories() : Call<ArrayList<Category>>
}
package com.example.booking.apis

import com.example.booking.models.Category
import com.example.booking.models.Comment
import com.example.booking.models.CreateComment
import retrofit2.Call
import retrofit2.http.*

interface CommentsApi {
    @GET("comments")
    fun getComments(@Query("serviceId") serviceId: String) : Call<List<Comment>>
    @POST("comments")
    fun createComment(@Body comment: CreateComment, @Header("Authorization") token: String) : Call<String>
}
package com.example.booking.utils

import com.example.booking.config.ApiSettings
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object BookingApi {
    private var retrofit: Retrofit? = null
    fun getInstance(): Retrofit? {
        if(retrofit == null) retrofit = Retrofit.Builder().baseUrl(ApiSettings.API_URL).
        addConverterFactory(ScalarsConverterFactory.create()).
        addConverterFactory(GsonConverterFactory.create()).
        build()
        return retrofit
    }
}
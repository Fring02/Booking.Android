package com.example.booking.models

data class BookingRequest(var id: String? = null, var serviceId: String,
var bookingTime: String, var userId: String, var info: String, var service: LeisureServiceElement?)
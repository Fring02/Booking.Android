package com.example.booking.models

data class User(var firstName: String, var lastName: String, var email: String,
var mobilePhone: String, var requests: List<BookingRequest>)
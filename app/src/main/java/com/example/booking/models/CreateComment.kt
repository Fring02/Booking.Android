package com.example.booking.models

data class CreateComment(var userId: String, var serviceId: String,
                         var content: String)
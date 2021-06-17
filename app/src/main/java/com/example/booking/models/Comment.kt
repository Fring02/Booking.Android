package com.example.booking.models

import java.util.*

data class Comment(var id: String, var user: User, var userId: String, var serviceId: String, var leftAt: String,
var content: String)

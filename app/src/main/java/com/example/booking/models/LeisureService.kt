package com.example.booking.models

data class LeisureService(var id: String,
var name: String, var location: String, var website: String, var rating: Int,
var description: String, var workingTime: String, var category: Category,
var images: List<ServiceImage>, var latitude: Double, var longitude: Double)
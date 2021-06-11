package com.example.booking.models

data class LeisureServiceElement(var id: String,
                                 var name: String, var location: String, var rating: Int,
                                 var category: Category,
                                 var images: List<ServiceImage>)
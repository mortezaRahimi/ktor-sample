package com.example.model

import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
data class Customer(
    val id: Int ,
    val name:String,
    val phoneNumber:String
)
val customers = mutableListOf(Customer(id = 10, name = "John" , phoneNumber = "987456332"))

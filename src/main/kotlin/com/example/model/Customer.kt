package com.example.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*

@Serializable
data class Customer(
    val userName:String,
    val pass:String,
    val id: Int ,
    val name:String,
    val phoneNumber:String
)
data class Article(val id: Int, val title: String, val body: String)

object Articles : Table() {
    val id = integer("id").autoIncrement()
    val title = varchar("title", 128)
    val body = varchar("body", 1024)

    override val primaryKey = PrimaryKey(id)

}
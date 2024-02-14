package com.example.plugins

import com.example.model.Customer
import com.example.model.customers
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*

fun Application.configureRouting() {
    routing {
        get("/customers") {
            if (customers.isNotEmpty())
                call.respond(customers)
            else
                call.respondText("No customer registered", status = HttpStatusCode.OK)
        }

        get("/customers/{id}") {
            val id = call.parameters["id"] ?: call.respondText("wrong parameters", status = HttpStatusCode.BadRequest)

            val customer = customers.find { cu -> cu.id.toString() == id } ?:return@get call.respondText( "No customer found with id: $id" , status = HttpStatusCode.OK)
            call.respond(customer)
        }

        post("/customer") {
            val customer = call.receive<Customer>()
            customers.add(customer)
            call.respondText("Customer stored correctly", status = HttpStatusCode.Created)
        }
    }
}

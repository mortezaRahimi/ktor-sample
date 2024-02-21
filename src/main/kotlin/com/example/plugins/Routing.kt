package com.example.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.local.dao
import com.example.model.Customer
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.freemarker.*
import io.ktor.server.util.*
import java.util.*

fun Application.configureRouting() {
    install(ContentNegotiation) {
        json()
    }
    val secret = environment.config.property("ktor.jwt.secret").getString()
    val myRealm = environment.config.property("ktor.jwt.realm").getString()
    val issuer = environment.config.property("ktor.jwt.issuer").getString()
    val audience = environment.config.property("ktor.jwt.audience").getString()

    install(Authentication) {

        jwt("auth-jwt") {
            realm = myRealm
            verifier(
                JWT.require(Algorithm.HMAC256(secret))
                    .withAudience(audience)
                    .withIssuer(issuer)
                    .build()
            )
            challenge { defaultScheme, realm ->
                call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
            }
        }
    }

    routing {

        get("{id}") {
            val id = call.parameters.getOrFail<Int>("id").toInt()
            call.respond(FreeMarkerContent("show.ftl", mapOf("article" to dao.article(id))))
        }
        get("{id}/edit") {
            val id = call.parameters.getOrFail<Int>("id").toInt()
            call.respond(FreeMarkerContent("edit.ftl", mapOf("article" to dao.article(id))))
        }

        post {
            val formParameters = call.receiveParameters()
            val title = formParameters.getOrFail("title")
            val body = formParameters.getOrFail("body")
            val article = dao.addNewArticle(title, body)
            call.respondRedirect("/articles/${article?.id}")
        }

        post("{id}") {
            val id = call.parameters.getOrFail<Int>("id").toInt()
            val formParameters = call.receiveParameters()
            when (formParameters.getOrFail("_action")) {
                "update" -> {
                    val title = formParameters.getOrFail("title")
                    val body = formParameters.getOrFail("body")
                    dao.editArticle(id, title, body)
                    call.respondRedirect("/articles/$id")
                }
                "delete" -> {
                    dao.deleteArticle(id)
                    call.respondRedirect("/articles")
                }
            }
        }

        post("/login") {
            val user = call.receive<Customer>()

            with(user) {
                if (userName.length < 3 || pass.length < 4) {
                    call.respond(HttpStatusCode.BadRequest, "User name or password is not valid")
                    return@post
                } else {
                    val token = JWT.create()
                        .withAudience(audience)
                        .withIssuer(issuer)
                        .withClaim("username", user.userName)
                        .withExpiresAt(Date(System.currentTimeMillis() + 60000))
                        .sign(Algorithm.HMAC256(secret))
                    call.respond(hashMapOf("token" to token))
                }
            }

        }
//        get("/customers") {
//            if (customers.isNotEmpty())
//                call.respond(customers)
//            else
//                call.respondText("No customer registered", status = HttpStatusCode.OK)
//        }
//
//        get("/customers/{id}") {
//            val id = call.parameters["id"] ?: call.respondText("wrong parameters", status = HttpStatusCode.BadRequest)
//
//            val customer = customers.find { cu -> cu.id.toString() == id } ?:return@get call.respondText( "No customer found with id: $id" , status = HttpStatusCode.OK)
//            call.respond(customer)
//        }

//        post("/customer") {
//            val customer = call.receive<Customer>()
//            customers.add(customer)
//            call.respondText("Customer stored correctly", status = HttpStatusCode.Created)
//        }
    }
}

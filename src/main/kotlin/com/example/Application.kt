package com.example

import com.example.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.websocket.*
import java.time.Duration

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    configureRouting()
    configureSockets()
}

//fun main(args: Array<String>) {
//    embeddedServer(Netty, port = 8080) {
//        install(WebSockets){
//            pingPeriod = Duration.ofSeconds(15)
//            timeout = Duration.ofSeconds(15)
//            maxFrameSize = Long.MAX_VALUE
//            masking = false
//        }
//
//    }.start(wait = true)
////    io.ktor.server.netty.EngineMain.main(args)
//}
//
//fun Application.module() {
//    configureSerialization()
//    configureRouting()
//}

//embeddedServer(Netty, port = 8080, module = Application::module).start(wait = true)
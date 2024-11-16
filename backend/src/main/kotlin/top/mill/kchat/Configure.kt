package top.mill.kchat

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import top.mill.kchat.network.chatRoute
import top.mill.kchat.network.groupsRoute
import top.mill.kchat.network.usersRoute
import kotlin.time.Duration.Companion.seconds

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json()
    }
}

fun Application.configureDatabases() {
    routing {
        usersRoute()
        groupsRoute()
    }
}

fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = 15.seconds
        timeout = 15.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    routing {
        chatRoute()
    }
}
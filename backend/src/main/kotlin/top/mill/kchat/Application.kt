package top.mill.kchat

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.coroutines.*
import top.mill.kchat.network.Client
import top.mill.kchat.network.broadcastDiscoveryMessage
import top.mill.kchat.network.listenForResponses
import top.mill.kchat.network.route.chatRoute
import top.mill.kchat.network.route.groupsRoute
import top.mill.kchat.network.route.usersRoute
import kotlin.time.Duration.Companion.seconds

@OptIn(DelicateCoroutinesApi::class)
fun main(args: Array<String>) = runBlocking {

    logger().info { "Application started." }

    val ktorJob = GlobalScope.launch { EngineMain.main(args) }

    val broadcastJob = launch(Dispatchers.Default) {
        while (true) {
            broadcastDiscoveryMessage()
            delay(BROADCAST_DELAY)
        }
    }

    val listenJob = launch(Dispatchers.Default) {
        listenForResponses { userAddress ->
            logger().info { "Discovered user at $userAddress" }
            Client.addNewAddress(userAddress)
        }
    }

    broadcastJob.join()
    listenJob.join()
    ktorJob.join()

    ConfigManager.saveConfig()

    logger().info { "Application stopped." }
}

fun Application.module() {
    configureSerialization()
    configureSockets()
}

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json()
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
        usersRoute()
        groupsRoute()
    }
}
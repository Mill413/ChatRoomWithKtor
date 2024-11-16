package top.mill.kchat

import io.ktor.server.application.*
import io.ktor.server.netty.*
import kotlinx.coroutines.*
import top.mill.kchat.network.Client
import top.mill.kchat.network.broadcastDiscoveryMessage
import top.mill.kchat.network.listenForResponses

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
    configureDatabases()
    configureSockets()
}

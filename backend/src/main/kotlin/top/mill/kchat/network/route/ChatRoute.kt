package top.mill.kchat.network.route

import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.delay
import top.mill.kchat.logger
import top.mill.kchat.network.Client
import top.mill.kchat.network.DeviceSession

fun Route.chatRoute() {
    webSocket("/chat") {
        val id = call.parameters["uuid"] ?: return@webSocket close(
            CloseReason(
                CloseReason.Codes.CANNOT_ACCEPT,
                "Missing ID"
            )
        )
        val host = call.request.local.remoteAddress
        logger("ChatRoute").info { "Routing: Received WebSocket request /ws?uuid=${id} from $host" }
        Client.addSession(DeviceSession(id, this))

        // Keep Session Alive
        while (true) {
            delay(100)
        }
    }
}
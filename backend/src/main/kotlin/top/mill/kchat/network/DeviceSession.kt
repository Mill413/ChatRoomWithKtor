package top.mill.kchat.network

import io.ktor.websocket.*
import kotlinx.serialization.json.Json
import top.mill.kchat.UUIDManager
import top.mill.kchat.messages.MessageList
import top.mill.kchat.messages.TextMessage
import java.time.LocalDateTime
import java.time.ZoneOffset

class DeviceSession(val id: String, private val session: WebSocketSession) {
    suspend fun send(message: String) {
        session.outgoing.send(Frame.Text(message))
    }

    // TODO(better handle message)
    suspend fun receive(): MessageList {
        return session.incoming.receive().let { frame ->
            return if (frame is Frame.Text) {
                val msg = frame.readText()
                Json.Default.decodeFromString<MessageList>(msg)
            } else MessageList(
                mutableListOf(
                    TextMessage(
                        frame.toString(),
                        id,
                        UUIDManager.getUUIDString(),
                        LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
                    )
                ),
                id,
                UUIDManager.getUUIDString(),
                LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
            )
        }
    }
}
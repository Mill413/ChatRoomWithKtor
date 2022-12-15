package top.harumill.message.commandMessage

import io.ktor.client.plugins.websocket.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import top.harumill.contact.server.Client
import top.harumill.message.Message
import top.harumill.message.objectToByte
import top.harumill.utils.Logger

interface CommandMessage : Message {

    val type: CommandTYPE

    /**
     * 客户端向服务器发送指令
     */
    suspend fun request(server: DefaultClientWebSocketSession) {
        Logger.verbose("Server <- $this")
        server.send(objectToByte(this)!!)
    }

    /**
     * 服务器向客户端发送指令
     */
    suspend fun response(client: DefaultWebSocketServerSession) {
        Logger.verbose("Server -> $this")
        client.send(objectToByte(this)!!)
    }

    suspend fun response(client: Client) {
        response(client.session)
    }


    override fun contentToString(): String {
        return "[getto:command: $type]"
    }

    override fun toString(): String


}

/**
 * 指令类型
 *
 */
enum class CommandTYPE {
    LOGIN,
    SYNC
}
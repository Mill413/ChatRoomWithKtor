package top.harumill.contact.server

import io.ktor.http.cio.websocket.*
import io.ktor.websocket.*
import top.harumill.top.harumill.contact.User
import top.harumill.top.harumill.message.Message
import top.harumill.top.harumill.message.MessageChain
import top.harumill.top.harumill.message.objectToByte
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.concurrent.atomic.AtomicInteger

typealias Session = DefaultWebSocketServerSession

class Client(private val session:Session): User() {
    companion object { var lastId = AtomicInteger(0) }
    override val id: Long = lastId.getAndIncrement().toLong()
    override var name: String = "user-$id"

    override suspend fun sendMessage(message: Message) {
        session.send(objectToByte(message)!!)
    }

    override suspend fun sendMessage(messageChain: MessageChain) {
        session.send(objectToByte(messageChain)!!)
    }

    suspend fun sendMessage(message: String){
        session.send(message)
    }

    override fun toString(): String {
        return "${name}(${id})"
    }
}


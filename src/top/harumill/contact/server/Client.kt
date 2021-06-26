package top.harumill.contact.server

import io.ktor.http.cio.websocket.*
import io.ktor.websocket.*
import top.harumill.contact.Contact
import top.harumill.message.*
import top.harumill.top.harumill.contact.UserInfo
import top.harumill.top.harumill.message.singleMessage.PlainText
import top.harumill.utils.Logger
import java.util.concurrent.atomic.AtomicInteger

typealias Session = DefaultWebSocketServerSession

/**
 * 服务器上的客户端类，一个Client实例表示一个已经连接的客户端
 * 不应通过[ForwardMessage]发送
 */
class Client(private val session:Session): Contact {

    companion object {
        var lastId = AtomicInteger(0)
        const val serialVersionUID:Long = 11
    }
    override val id: Long = lastId.getAndIncrement().toLong()
    override var name: String = "user-$id"

    override suspend fun sendMessage(message: Message) {
        sendMessage(ForwardMessage(message,null, UserInfo(this)))
    }

    override suspend fun sendMessage(messageChain: MessageChain) {
        sendMessage(messageChain)
    }

    override suspend fun sendMessage(forwardMessage: ForwardMessage) {
        Logger.verbose(forwardMessage.toString())
        session.send(objectToByte(forwardMessage)!!)
    }

    suspend fun sendMessage(message: String){
        sendMessage(PlainText(message))
    }

    override fun toString(): String {
        return "${name}(${id})"
    }
}


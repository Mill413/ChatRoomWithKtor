package top.harumill.contact.server

import io.ktor.http.cio.websocket.*
import io.ktor.websocket.*
import top.harumill.top.harumill.contact.User
import top.harumill.top.harumill.message.Message
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

    suspend fun sendMessage(message: String){
        session.send(message)
    }

    override fun toString(): String {
        return "${name}(${id})"
    }
}

fun objectToByte(obj: Any?): ByteArray? {
    var bytes: ByteArray? = null
    try {
        // object to bytearray
        val bo = ByteArrayOutputStream()
        val oo = ObjectOutputStream(bo)
        oo.writeObject(obj)
        bytes = bo.toByteArray()
        bo.close()
        oo.close()
    } catch (e: Exception) {
        println("translation" + e.message)
        e.printStackTrace()
    }
    return bytes
}

fun byteToObject(bytes: ByteArray?): Any? {
    var obj: Any? = null
    try {
        // bytearray to object
        val bi = ByteArrayInputStream(bytes)
        val oi = ObjectInputStream(bi)
        obj = oi.readObject()
        bi.close()
        oi.close()
    } catch (e: java.lang.Exception) {
        println("translation" + e.message)
        e.printStackTrace()
    }
    return obj
}
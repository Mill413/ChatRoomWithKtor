package top.harumill.message

import top.harumill.contact.Contact
import top.harumill.message.Message.Companion.sendTime
import java.io.*
import java.time.LocalDateTime
import java.time.ZoneOffset

/**
 * 消息接口
 * @property sendTime 消息发送的时间戳，初始值为构造消息时的时间戳，应在发送消息时更改
 * @property sourceID 为消息在数据库内的索引，初始化为消息 构造时的时间戳
 *
 */
interface Message : Serializable {
    val sourceID: Long
        get() = sendTime

    companion object {
        var sendTime: Long = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli()
    }

    /**
     * 消息发送
     * @param target 发送对象
     */
    suspend fun sendTo(target: Contact) {
        target.sendMessage(this)
        sendTime = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli()
    }


    /**
     * 用于将消息转化为客户端显示
     * 示例:
     * PlainText: Hello
     * FileMessage: `[文件`]fileName.type
     * Command: `[指令`] commandName
     * ...
     */
    fun contentToString(): String

    /**
     * 用于将消息转化为日志显示
     * 示例:
     * PlainText:
     */
    override fun toString(): String
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

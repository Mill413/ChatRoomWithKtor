package top.harumill.top.harumill.message.singleMessage

import top.harumill.message.Message
import top.harumill.message.MessageType
import top.harumill.message.singleMessage.SingleMessage

/**
 * 文本消息
 * @property type 文本消息
 * @property content 文本内容
 */
class PlainText(text: String): SingleMessage {
    companion object{
        const val serialVersionUID:Long = 21
    }

    override val type: MessageType = MessageType.PLAINTEXT
    val content:String = text

    override fun contentToString(): String {
        return content
    }

    override fun toString(): String {
        return content
    }
}
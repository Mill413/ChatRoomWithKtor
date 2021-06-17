package top.harumill.top.harumill.message

import top.harumill.top.harumill.contact.Contact

/**
 * 文本消息
 * @property type 文本消息
 * @property content 文本内容
 * @property target 消息的接收者
 */
class PlainText(text:String,receiver:Contact? = null): Message() {
    override val type: MessageType = MessageType.PLAINTEXT

    val content:String = text

    val target = receiver

    override fun toString(): String {
        return content
    }
}
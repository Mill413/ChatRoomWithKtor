package top.harumill.top.harumill.message

import top.harumill.top.harumill.contact.Contact

/**
 * 文本消息
 * @property type 文本消息
 * @property content 文本内容
 */
class PlainText(text: String): Message {
    override val type: MessageType = MessageType.PLAINTEXT
    val content:String = text

    override fun contentToString(): String {
        return content
    }

    override fun toString(): String {
        return content
    }
}
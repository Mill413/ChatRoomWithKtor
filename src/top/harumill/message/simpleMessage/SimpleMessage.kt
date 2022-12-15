package top.harumill.message.simpleMessage

import top.harumill.contact.UserInfo
import top.harumill.message.Message

class SimpleMessage(message: Message, from: UserInfo, to: UserInfo) : Message {
    companion object {
        const val serialVersionUID: Long = 20
    }

    private var content = message
    private var sender = from
    val target = to

    constructor(message: String, from: UserInfo, to: UserInfo) : this(PlainText(message), from, to)
    /**
     * @return 返回[content]的客户端显示
     */
    override fun contentToString(): String {
        return content.contentToString()
    }
    override fun toString(): String {
        val senderStr = sender.toString()
        val targetStr = target.toString()

        return "$senderStr -> $targetStr: ${content.contentToString()}"
    }
}
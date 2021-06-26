package top.harumill.message

import top.harumill.contact.Contact
import top.harumill.top.harumill.contact.UserInfo
import top.harumill.top.harumill.message.singleMessage.PlainText

class ForwardMessage(message: Message, from: UserInfo? = null, to: UserInfo? = null):Message {
    companion object{
        const val serialVersionUID:Long = 20
    }

    val content = message
    val sender = from
    val target = to

    override val type: MessageType = MessageType.FORWARD

    constructor(message:String,from: UserInfo?=null,to: UserInfo?=null) : this(PlainText(message),from,to)
    constructor(message: Message,from:Contact?=null,to:Contact?=null):this(message,
        UserInfo(from), UserInfo(to)
    )
    constructor(message: String,from:Contact?=null,to:Contact?=null):this(PlainText(message),
        UserInfo(from),UserInfo(to))

    override fun contentToString(): String {
        val senderStr = sender?.toString() ?: "Server"
        val targetStr = target?.toString() ?: "Server"

        return "$senderStr -> $targetStr: ${content.contentToString()}"    }

    override fun toString(): String {
        return contentToString()
    }
}
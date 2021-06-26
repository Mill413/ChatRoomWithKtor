package top.harumill.message.singleMessage

import top.harumill.message.Message
import top.harumill.message.MessageChain
import top.harumill.message.MessageType

/**
 * 单一类型消息接口
 */
interface SingleMessage:Message {
    override val type: MessageType

    /**
     * 将一个单一类型消息转化为消息链
     */
    fun toMessageChain(): MessageChain {
        return MessageChain(this)
    }

    operator fun plus(message: Message): MessageChain {
        val chain = MessageChain(this)
        return chain.add(message)
    }
}
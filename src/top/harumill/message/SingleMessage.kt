package top.harumill.message

/**
 * 单一类型消息接口，提供将单一类型消息转化为[MessageChain]的方法
 *
 * [toMessageChain] 将一个单一类型消息转化为消息链
 *
 */
interface SingleMessage : Message {

    fun toMessageChain(): MessageChain {
        return MessageChain(this)
    }

    operator fun plus(message: Message): MessageChain {
        val chain = MessageChain(this)
        return chain.add(message)
    }
}
package top.harumill.top.harumill.message

import top.harumill.top.harumill.contact.Contact

/**
 * 消息抽象类,所有具体类型的消息应继承此类
 * @property type 类型属性
 * @property sourceID 为消息在数据库内的索引，初始化为消息 构造时的时间戳
 *
 * @see MessageType 消息类型
 * @see MetaMessage 元消息接口
 */
interface Message:MetaMessage {
    /**
     * 消息发送
     * @param target 发送对象
     */
    suspend fun sendTo(target:Contact){
        target.sendMessage(this)
    }

    fun toMessageChain():MessageChain{
        return MessageChain(this)
    }

    operator fun plus(message: Message):MessageChain{
        val chain = MessageChain(this)
        return chain.add(message)
    }
}
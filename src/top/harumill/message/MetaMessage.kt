package top.harumill.top.harumill.message

import java.io.Serializable

/**
 * 元消息接口，只包含最原始的消息内容,任何具体的消息类型不应直接实现此接口，而应继承[Message]类
 * @property type 消息类型
 * @property sourceID 消息在数据库内的索引
 *
 * @see Message 抽象消息类
 * @see MessageType 消息类型
 */
interface MetaMessage:Serializable {
    val type:MessageType
    val sourceID:Long
}

/**
 * 数据类型
 * @property PLAINTEXT 文本消息，如 ”Hello“
 * @property COMMAND 指令消息，客户端向服务器发送的特定指令
 */
enum class MessageType {
    PLAINTEXT,
    COMMAND
}
package top.harumill.contact

import top.harumill.message.ForwardMessage
import top.harumill.message.Message
import top.harumill.message.MessageChain

/**
 * 用户或群组的公共接口
 * @property info 用户信息
 */
interface Contact {
    var info: UserInfo

    /**
     * 发送消息方法
     */
    suspend fun sendMessage(message: Message)

    suspend fun sendMessage(messageChain: MessageChain)

    suspend fun sendMessage(forwardMessage: ForwardMessage)
}
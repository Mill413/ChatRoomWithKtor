package top.harumill.message.singleMessage.command

import top.harumill.message.MessageType
import top.harumill.message.singleMessage.SingleMessage

interface Command: SingleMessage {
    override val type: MessageType

    override fun contentToString(): String {
        return "[getto:command: $type]"
    }

    override fun toString(): String
}

/**
 * 指令类型
 *
 */
enum class CommandTYPE{
    LOGIN
}
package top.harumill.top.harumill.message.command

import top.harumill.top.harumill.message.Message
import top.harumill.top.harumill.message.MessageType

interface Command: Message {
    override val type: MessageType
        get() = MessageType.COMMAND
    val commandName: CommandTYPE

    override fun contentToString(): String {
        return "[getto:command: $commandName]"
    }

}

/**
 * 指令类型
 *
 */
enum class CommandTYPE{

}
package top.harumill.message.singleMessage.command

import top.harumill.contact.Contact
import top.harumill.contact.server.Client
import top.harumill.message.MessageType
import top.harumill.message.singleMessage.command.Command
import top.harumill.message.singleMessage.command.CommandTYPE
import top.harumill.top.harumill.contact.UserInfo

/**
 * 服务器发送给客户端的包含登录信息的指令消息
 */
class LoginCmd(
    user:UserInfo
) :Command {
    companion object{
        const val serialVersionUID:Long = 24
    }

    val id = user.id
    val name = user.name


    override val type: MessageType = MessageType.LOGIN_CMD

    override fun toString(): String {
        return contentToString()
    }


}
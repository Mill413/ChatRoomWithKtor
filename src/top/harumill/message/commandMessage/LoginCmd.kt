package top.harumill.message.commandMessage

import top.harumill.contact.UserInfo

/**
 * 登录指令
 *
 * 客户端应向服务器发送自己的UID当接收到服务器返回的登录指令后完成登录
 *
 * 服务器应在接收到客户端的登录指令后，才能将客户端保存进[ClientPool]，并返回包含用户信息的登录指令
 */
class LoginCmd(
    user: UserInfo
) : CommandMessage {
    companion object {
        const val serialVersionUID: Long = 24
    }

    override val type: CommandTYPE
        get() = CommandTYPE.LOGIN

    val info = user

    var list = listOf<UserInfo>()

    override fun toString(): String {
        return "[getto:command: $type: {\"info\": $info,\"list:\"[$list]}]"
    }


}
package top.harumill.contact.server

import top.harumill.contact.Contact
import top.harumill.contact.UserInfo
import top.harumill.message.simpleMessage.SimpleMessage
import top.harumill.message.Message
import top.harumill.message.MessageChain

class Group : Contact {
    companion object {
        const val serialVersionUID: Long = 12
    }

    override var info: UserInfo = UserInfo(
        id = 0
    )

    override suspend fun sendMessage(message: Message) {
        memberList.forEach {
            it.sendMessage(message)
        }
    }

    override suspend fun sendMessage(messageChain: MessageChain) {
        memberList.forEach {
            it.sendMessage(messageChain)
        }
    }

    override suspend fun sendMessage(simpleMessage: SimpleMessage) {
        memberList.forEach {
            it.sendMessage(simpleMessage)
        }
    }

    val memberList: MutableList<Client>
        get() {
            return mutableListOf()
        }

}
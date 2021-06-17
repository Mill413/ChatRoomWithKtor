package top.harumill.top.harumill.contact.server

import top.harumill.contact.server.Client
import top.harumill.top.harumill.contact.Contact
import top.harumill.top.harumill.message.Message

class Group:Contact {
    override val id: Long
        get() = TODO("Not yet implemented")
    override var name: String
        get() = TODO("Not yet implemented")
        set(value) {}

    override suspend fun sendMessage(message: Message) {
        memberList.forEach {
            it.sendMessage(message)
        }
    }

    val memberList:MutableList<Client>
        get() {
            return mutableListOf()
        }

}
package top.harumill.top.harumill.contact.client

import top.harumill.top.harumill.contact.User
import top.harumill.top.harumill.message.Message

class Friend:User() {
    override val id: Long
        get() = TODO("Not yet implemented")
    override var name: String
        get() = TODO("Not yet implemented")
        set(value) {}

    override suspend fun sendMessage(message: Message) {

    }
}
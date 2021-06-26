package top.harumill.contact.client

import top.harumill.contact.Contact
import top.harumill.message.ForwardMessage
import top.harumill.message.Message
import top.harumill.message.MessageChain

class Friend:Contact {
    override val id: Long
        get() = TODO("Not yet implemented")
    override var name: String
        get() = TODO("Not yet implemented")
        set(value) {}

    override suspend fun sendMessage(message: Message) {

    }

    override suspend fun sendMessage(messageChain: MessageChain) {
        TODO("Not yet implemented")
    }

    override suspend fun sendMessage(forwardMessage: ForwardMessage) {
        TODO("Not yet implemented")
    }
}
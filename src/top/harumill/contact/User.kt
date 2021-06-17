package top.harumill.top.harumill.contact

import top.harumill.top.harumill.message.Message

abstract class User:Contact {

    abstract override val id: Long

    abstract override var name: String

    abstract override suspend fun sendMessage(message: Message)
}
package top.mill.kchat.service

import top.mill.kchat.contacts.Contact
import top.mill.kchat.contacts.Group
import top.mill.kchat.contacts.User
import top.mill.kchat.messages.Message
import top.mill.kchat.network.Client

class ChatService {

    suspend fun sendMessage(from: User, to: Contact, message: Message) {
        when (to) {
            is User -> {
                Client.sendMessageOnWebSocket(message.toString(), to.id)
            }

            is Group -> {
                Client.broadcastMessageOnWebSocket(message.toString(), to.members.map { user -> user.id })
            }
        }
    }

    suspend fun receiveMessage(from: Contact, onReceive: (Message) -> Unit) {
        Client.receiveMessageOnWebSocket(from.id) { msg -> onReceive(msg) }
    }
}
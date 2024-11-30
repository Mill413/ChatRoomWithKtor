package top.mill.kchat.service

import top.mill.kchat.contacts.Contact
import top.mill.kchat.contacts.Group
import top.mill.kchat.contacts.User
import top.mill.kchat.messages.MessageList
import top.mill.kchat.network.Client

class ChatService {

    suspend fun sendMessage(from: User, to: Contact, messageList: MessageList) {
        when (to) {
            is User -> {
                Client.sendMessageOnWebSocket(messageList.toString(), to.id)
            }

            is Group -> {
                Client.broadcastMessageOnWebSocket(messageList.toString(), to.members.map { user -> user.id })
            }
        }
    }

    suspend fun receiveMessage(from: Contact, onReceive: (MessageList) -> Unit) {
        Client.receiveMessageOnWebSocket(from.id) { msg -> onReceive(msg) }
    }
}
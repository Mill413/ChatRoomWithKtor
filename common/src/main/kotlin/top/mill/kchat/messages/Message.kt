package top.mill.kchat.messages

import kotlinx.serialization.Serializable

@Serializable
sealed class Message {
    abstract val sender: String
    abstract val receiver: String
    abstract val sendTime: Long
    abstract val type: MessageType
}
package top.mill.kchat.messages

import kotlinx.serialization.Serializable

@Serializable
sealed class MetaMessage {
    abstract val sender: String
    abstract val receiver: String
    abstract val sendTime: Long
    abstract val type: MessageType
}
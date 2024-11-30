package top.mill.kchat.messages

import kotlinx.serialization.Serializable

@Serializable
data class TextMessage(
    val text: String,
    override val sender: String,
    override val receiver: String,
    override val sendTime: Long,
    override val type: MessageType = MessageType.TEXT
) : MetaMessage()
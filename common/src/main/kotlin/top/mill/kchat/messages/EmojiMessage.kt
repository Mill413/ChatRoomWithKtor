package top.mill.kchat.messages

@kotlinx.serialization.Serializable
data class EmojiMessage(
    val emoji: String,
    override val sender: String,
    override val receiver: String,
    override val sendTime: Long,
    override val type: MessageType = MessageType.EMOJI
) : MetaMessage()
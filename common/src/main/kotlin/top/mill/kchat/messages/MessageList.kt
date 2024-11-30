package top.mill.kchat.messages

import kotlinx.serialization.Serializable

@Serializable
data class MessageList(
    val messages: MutableList<Message> = mutableListOf(),
    override val sender: String,
    override val receiver: String,
    override val sendTime: Long,
    override val type: MessageType = MessageType.MIXED
) : Message(), Iterable<Message> {

    init {
        if (messages.any { it is FileMessage }) throw IllegalArgumentException("FileMessage must be sent individually!")
    }

    fun add(message: Message) {
        if (message is FileMessage) throw IllegalArgumentException("FileMessage must be sent individually!")
        messages.add(message)
    }

    override fun iterator() = messages.iterator()
}
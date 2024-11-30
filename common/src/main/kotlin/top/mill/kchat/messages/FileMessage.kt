package top.mill.kchat.messages

import kotlinx.serialization.Serializable

@Serializable
data class FileMessage(
    val file: ByteArray,
    override val sender: String,
    override val receiver: String,
    override val sendTime: Long,
    override val type: MessageType = MessageType.FILE
) : Message() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FileMessage

        if (sendTime != other.sendTime) return false
        if (!file.contentEquals(other.file)) return false
        if (sender != other.sender) return false
        if (receiver != other.receiver) return false

        return true
    }

    override fun hashCode(): Int {
        var result = sendTime.hashCode()
        result = 31 * result + file.contentHashCode()
        result = 31 * result + sender.hashCode()
        result = 31 * result + receiver.hashCode()
        return result
    }
}

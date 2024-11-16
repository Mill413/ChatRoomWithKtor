package top.mill.kchat.messages

import kotlinx.serialization.Serializable

@Serializable
data class ImageMessage(
    val image: ByteArray,
    val imageName: String,
    override val sender: String,
    override val receiver: String,
    override val sendTime: Long,
    override val type: MessageType = MessageType.IMAGE
) : MetaMessage() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ImageMessage

        if (sendTime != other.sendTime) return false
        if (!image.contentEquals(other.image)) return false
        if (imageName != other.imageName) return false
        if (sender != other.sender) return false
        if (receiver != other.receiver) return false

        return true
    }

    override fun hashCode(): Int {
        var result = sendTime.hashCode()
        result = 31 * result + image.contentHashCode()
        result = 31 * result + imageName.hashCode()
        result = 31 * result + sender.hashCode()
        result = 31 * result + receiver.hashCode()
        return result
    }
}

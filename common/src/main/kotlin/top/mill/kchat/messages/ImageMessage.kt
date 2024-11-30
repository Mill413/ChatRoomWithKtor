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
) : Message() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        val otherImage = other as ImageMessage

        if (sendTime != otherImage.sendTime) return false
        if (!image.contentEquals(otherImage.image)) return false
        if (imageName != otherImage.imageName) return false
        if (sender != otherImage.sender) return false
        if (receiver != otherImage.receiver) return false

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

package top.mill.kchat.contacts

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val status: UserStatus,
    @SerialName("userUUID")
    override val id: String,
    @SerialName("userName")
    override val name: String
) : Contact()

enum class UserStatus {
    ONLINE,
    OFFLINE,
}
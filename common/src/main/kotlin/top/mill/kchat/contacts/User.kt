package top.mill.kchat.contacts

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val status: UserStatus,
    @SerialName("userID")
    override val id: String,
    @SerialName("userName")
    override val name: String
) : Contact(
    id = id,
    name = name,
)

enum class UserStatus {
    ONLINE,
    OFFLINE,
}
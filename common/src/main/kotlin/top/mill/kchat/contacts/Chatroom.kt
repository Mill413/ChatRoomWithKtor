package top.mill.kchat.contacts

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Chatroom(
    var members: MutableList<User> = mutableListOf(),
    val creator: String,
    @SerialName("roomID")
    override val id: String,
    @SerialName("roomName")
    override val name: String
) : Contact(
    id = id,
    name = name,
)

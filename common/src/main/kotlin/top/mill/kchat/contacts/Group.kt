package top.mill.kchat.contacts

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Group(
    var members: MutableList<User> = mutableListOf(),
    val creator: User,
    @SerialName("groupUUID")
    override val id: String,
    @SerialName("groupName")
    override val name: String
) : Contact()

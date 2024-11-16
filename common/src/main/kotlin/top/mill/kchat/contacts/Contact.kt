package top.mill.kchat.contacts

import kotlinx.serialization.Serializable

@Serializable
sealed class Contact {
    abstract val id: String
    abstract val name: String
}
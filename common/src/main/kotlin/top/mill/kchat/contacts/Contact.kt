package top.mill.kchat.contacts

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
abstract class Contact(
    @Transient open val id: String = "",
    @Transient open val name: String = "",
)
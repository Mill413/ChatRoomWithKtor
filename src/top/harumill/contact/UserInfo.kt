package top.harumill.top.harumill.contact

import top.harumill.contact.Contact
import java.io.Serializable
import java.util.concurrent.atomic.AtomicInteger

/**
 * 发送给客户端所需的用户信息
 */
data class UserInfo(
    val id:Long?,
    val name:String = "Server"
):Serializable{
    companion object {
        const val serialVersionUID:Long = 10
    }

    constructor(contact:Contact?):this(
        id = contact?.id,
        name = contact?.name?:"Server"
    )

    override fun toString(): String {
        return "$name${if (id != null) "($id)" else ""}"
    }
}

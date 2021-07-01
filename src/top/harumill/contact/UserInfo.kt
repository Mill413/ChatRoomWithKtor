package top.harumill.contact

import java.io.Serializable

/**
 * 用户信息
 *
 * 可用于客户端与服务器收发用户信息
 */
data class UserInfo(
    var id: Long = 0L,
    val name: String = if (id != 0L) "user-$id" else "Server"
) : Serializable {
    companion object {
        const val serialVersionUID: Long = 10
    }

    override fun toString(): String {
        return "$name${"($id)"}"
    }
}

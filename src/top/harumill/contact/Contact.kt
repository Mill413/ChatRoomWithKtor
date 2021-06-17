package top.harumill.top.harumill.contact

import top.harumill.top.harumill.message.Message
import java.io.Serializable

/**
 * 服务器端用户或者群组的公共接口
 * @property id 用户或者群组的id,用于在数据库中的索引及唯一标识符
 * @property name 用户或者群组的名称
 */
interface Contact:Serializable {
    val id:Long
    var name:String

    /**
     * 发送消息方法
     */
    suspend fun sendMessage(message:Message)
}
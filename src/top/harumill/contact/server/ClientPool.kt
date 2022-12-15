package top.harumill.contact.server

import top.harumill.contact.UserInfo
import top.harumill.message.Message
import java.util.concurrent.ConcurrentHashMap

typealias UserID = Long

object ClientPool {
    private val clientMap = ConcurrentHashMap<UserID?, Client>()

    fun queryClient(id: UserID): Client? {
        return clientMap[id]
    }

    fun joinClient(newClient: Client) {
        clientMap[newClient.info.id] = newClient
    }

    fun deleteClient(client: Client) {
        clientMap.remove(client.info.id)
    }

    //TODO-协程
    suspend fun broadcast(message: Message) {
        clientMap.forEach {
            it.value.sendMessage(message)
        }
    }

    fun onlineList(): List<UserInfo> {
        val list = mutableListOf<UserInfo>()
        clientMap.forEach {
            list.add(it.value.info)
        }
        return list.toList()
    }
}
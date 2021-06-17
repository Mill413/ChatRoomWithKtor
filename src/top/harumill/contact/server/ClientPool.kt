package top.harumill.top.harumill.contact.server

import top.harumill.contact.server.Client
import top.harumill.contact.server.Session
import top.harumill.top.harumill.message.Message
import java.util.concurrent.ConcurrentHashMap

typealias UserID = Long
object ClientPool {
    private val clientMap = ConcurrentHashMap<UserID,Client>()

    fun joinClinet(newClient:Client){
        clientMap[newClient.id] = newClient
    }

    fun deleteClient(client: Client){
        clientMap.remove(client.id)
    }

    suspend fun broadcast(message:Message){
        clientMap.forEach{
            it.value.sendMessage(message)
        }
    }
}
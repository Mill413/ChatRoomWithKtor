package top.mill.kchat.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.websocket.*
import kotlinx.serialization.json.Json
import top.mill.kchat.exceptions.KChatException
import top.mill.kchat.logger
import java.net.InetAddress

object Client {
    private val logger = logger("Client")

    private val client = HttpClient(CIO) {
        install(WebSockets)
    }

    private val deviceInNet = mutableListOf<InetAddress>()
    private val uuidToAddress = mutableMapOf<String, InetAddress>()
    private val uuidToSession = mutableMapOf<String, WebSocketSession>()

    init {
        logger.info { "LocalHost Address: ${InetAddress.getLocalHost().hostAddress}" }
    }

    fun onlineCount() = uuidToSession.size

    fun addNewAddress(ip: InetAddress) {
        deviceInNet.add(ip)
    }

    fun deleteAddress(ip: InetAddress) {
        deviceInNet.remove(ip)
    }

    fun updateUUID(uuid: String, ip: InetAddress) {
        uuidToAddress[uuid] = ip
    }

    suspend fun createSession(uuid: String, ip: InetAddress) {
        uuidToSession.getOrPut(uuid) {
            client.webSocketSession(
                host = ip.hostAddress,
                port = 8080,
                path = "/chat"
            )
        }
    }

    suspend fun sendMessageOnWebSocket(message: String, uuid: String) {
        val session = uuidToSession[uuid]
        if (session == null) {
            throw KChatException("No session found for uuid: $uuid", logger)
        }
        session.send(message)
    }

    suspend fun broadcastMessageOnWebSocket(message: String, uuidList: List<String> = uuidToAddress.keys.toList()) =
        uuidList.forEach { uuid ->
            if (uuid in uuidToAddress) sendMessageOnWebSocket(message, uuid)
        }

    internal suspend inline fun <reified T> getRequest(
        uuid: String, port: Int = 8080, path: String, params: Map<String, String>
    ): T = client.get("${uuidToAddress[uuid]}:$port/$path") {
        url {
            params.forEach { k, v -> parameters.append(k, v) }
        }
    }.let { response -> Json.decodeFromString(response.body()) }

    suspend fun <T> postRequest(uuid: String, port: Int = 8080, path: String, body: T) =
        client.post("${uuidToAddress[uuid]}:$port/$path") { body }

    suspend fun <T> putRequest(uuid: String, port: Int = 8080, path: String, body: T) =
        client.put("${uuidToAddress[uuid]}:$port/$path") { body }

    suspend fun <T> deleteRequest(uuid: String, port: Int = 8080, path: String, body: T) =
        client.delete("${uuidToAddress[uuid]}:$port/$path") { body }

    internal suspend inline fun <reified T> broadcastGetRequest(
        uuidList: List<String> = uuidToAddress.keys.toList(),
        port: Int = 8080,
        path: String,
        params: Map<String, String>
    ): List<T> = uuidList.mapNotNull { uuid ->
        uuid.takeIf { it in uuidToAddress }?.let { getRequest(it, port, path, params) }
    }

    suspend fun <T> broadcastPostRequest(
        uuidList: List<String> = uuidToAddress.keys.toList(), port: Int = 8080, path: String, body: T
    ) = uuidList.forEach { uuid ->
        if (uuid in uuidToAddress) {
            postRequest(uuid, port, path, body)
        }
    }

    suspend fun <T> broadcastPutRequest(
        uuidList: List<String> = uuidToAddress.keys.toList(), port: Int = 8080, path: String, body: T
    ) = uuidList.forEach { uuid ->
        if (uuid in uuidToAddress) {
            putRequest(uuid, port, path, body)
        }
    }

    suspend fun <T> broadcastDeleteRequest(
        uuidList: List<String> = uuidToAddress.keys.toList(), port: Int = 8080, path: String, body: T
    ) = uuidList.forEach { uuid ->
        if (uuid in uuidToAddress) {
            deleteRequest(uuid, port, path, body)
        }
    }
}




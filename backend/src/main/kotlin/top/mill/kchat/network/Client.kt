package top.mill.kchat.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.request.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.json.Json
import top.mill.kchat.logger
import java.net.InetAddress
import kotlin.time.Duration.Companion.seconds

object Client {
    private val logger = logger("Client")

    private val client = HttpClient(CIO) {
        install(WebSockets)
    }

    private val onlineAddressList = mutableListOf<InetAddress>()

    private val uuidToAddressMap = mutableMapOf<String, InetAddress>()

    init {
        logger.info { "LocalHost Address: ${InetAddress.getLocalHost().hostAddress}" }
    }

    fun onlineCount(): Int = onlineAddressList.size

    fun addNewAddress(ip: InetAddress) {
        onlineAddressList.add(ip)
    }

    fun updateUUID(uuid: String, ip: InetAddress) {
        uuidToAddressMap[uuid] = ip
    }

    fun deleteAddress(ip: InetAddress) {
        onlineAddressList.remove(ip)
    }

    fun getAddressByUUID(uuid: String): InetAddress? = uuidToAddressMap[uuid]

    fun sendMessageOnWebSocket(message: String, address: InetAddress) {
        TODO("Send Text Message to certain IP via WebSocket")
    }

    fun broadcastMessageOnWebSocket(message: String, addressList: List<InetAddress> = onlineAddressList) {
        if (addressList != onlineAddressList && addressList.any { address -> address !in onlineAddressList }) {
            logger.error { "Invalid address in List" }
        }
        addressList.forEach { address -> sendMessageOnWebSocket(message, address) }
    }

    suspend inline fun <reified T> getRequest(ip: InetAddress, port: Int = 8080, path: String): T {
        val response = getClient().get("$ip:$port/$path")
        return Json.decodeFromString(response.body())
    }

    suspend fun <T> postRequest(ip: InetAddress, port: Int = 8080, path: String, body: T) =
        client.post("$ip:$port/$path") { body }

    suspend fun <T> putRequest(ip: InetAddress, port: Int = 8080, path: String, body: T) =
        client.put("$ip:$port/$path") { body }

    suspend fun deleteRequest(ip: InetAddress, port: Int = 8080, path: String) = getClient().delete("$ip:$port/$path")

    internal suspend inline fun <reified T> broadcastGetRequest(
        addressList: List<InetAddress> = onlineAddressList,
        port: Int = 8080,
        path: String
    ): List<T> = addressList.map { address -> getRequest(address, port, path) }

    suspend fun <T> broadcastPostRequest(
        addressList: List<InetAddress> = onlineAddressList,
        port: Int = 8080,
        path: String,
        body: T
    ) = addressList.forEach { address -> postRequest(address, port, path, body) }

    suspend fun <T> broadcastPutRequest(
        addressList: List<InetAddress> = onlineAddressList,
        port: Int = 8080,
        path: String,
        body: T
    ) = addressList.forEach { address -> putRequest(address, port, path, body) }

    fun getClient() = client
}

fun Application.configureSockets() {
    install(io.ktor.server.websocket.WebSockets) {
        pingPeriod = 15.seconds
        timeout = 15.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {
        webSocket("/chat") {
            TODO("Complete route in WebSocket")
            for (frame in incoming) {
                if (frame is Frame.Text) {
                    val text = frame.readText()
                    outgoing.send(Frame.Text("YOU SAID: $text"))
                    if (text.equals("bye", ignoreCase = true)) {
                        close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
                    }
                }
            }
        }
    }
}


package top.mill.kchat.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import kotlinx.serialization.json.Json
import top.mill.kchat.logger
import java.net.InetAddress

object Client {
    private val logger = logger("Client")

    private val client = HttpClient(CIO) {
        install(WebSockets)
    }

    private val deviceAddressList = mutableListOf<InetAddress>()

    private val uuidToAddressMap = mutableMapOf<String, InetAddress>()

    init {
        logger.info { "LocalHost Address: ${InetAddress.getLocalHost().hostAddress}" }
    }

    fun onlineCount(): Int = deviceAddressList.size

    fun addNewAddress(ip: InetAddress) {
        deviceAddressList.add(ip)
    }

    fun updateUUID(uuid: String, ip: InetAddress) {
        uuidToAddressMap[uuid] = ip
    }

    fun deleteAddress(ip: InetAddress) {
        deviceAddressList.remove(ip)
    }

    fun getAddressByUUID(uuid: String): InetAddress? = uuidToAddressMap[uuid]

    fun sendMessageOnWebSocket(message: String, address: InetAddress) {
        TODO("Send Text Message to certain IP via WebSocket")
    }

    fun broadcastMessageOnWebSocket(message: String, addressList: List<InetAddress> = deviceAddressList) {
        if (addressList != deviceAddressList && addressList.any { address -> address !in deviceAddressList }) {
            logger.error { "Invalid address in List" }
        }
        addressList.forEach { address -> sendMessageOnWebSocket(message, address) }
    }

    internal suspend inline fun <reified T> getRequest(
        ip: InetAddress,
        port: Int = 8080,
        path: String,
        params: Map<String, String>
    ): T {
        val response = client.get("$ip:$port/$path") {
            url {
                params.forEach { k, v -> parameters.append(k, v) }
            }
        }
        return Json.decodeFromString(response.body())
    }

    suspend fun <T> postRequest(ip: InetAddress, port: Int = 8080, path: String, body: T) =
        client.post("$ip:$port/$path") { body }

    suspend fun <T> putRequest(ip: InetAddress, port: Int = 8080, path: String, body: T) =
        client.put("$ip:$port/$path") { body }

    suspend fun deleteRequest(ip: InetAddress, port: Int = 8080, path: String) = client.delete("$ip:$port/$path")

    internal suspend inline fun <reified T> broadcastGetRequest(
        addressList: List<InetAddress> = deviceAddressList,
        port: Int = 8080,
        path: String,
        params: Map<String, String>
    ): List<T> = addressList.map { address -> getRequest(address, port, path, params) }

    suspend fun <T> broadcastPostRequest(
        addressList: List<InetAddress> = deviceAddressList,
        port: Int = 8080,
        path: String,
        body: T
    ) = addressList.forEach { address -> postRequest(address, port, path, body) }

    suspend fun <T> broadcastPutRequest(
        addressList: List<InetAddress> = deviceAddressList,
        port: Int = 8080,
        path: String,
        body: T
    ) = addressList.forEach { address -> putRequest(address, port, path, body) }
}




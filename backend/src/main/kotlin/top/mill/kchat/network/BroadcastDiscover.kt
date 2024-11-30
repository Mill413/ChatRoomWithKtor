package top.mill.kchat.network

import top.mill.kchat.*
import top.mill.kchat.contacts.UserStatus
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

private val logger = logger("Network.Discover")

// TODO(Check ip is Active in Net)
fun broadcastDiscoveryMessage() {
    if (localStatus == UserStatus.ONLINE) {
        DatagramSocket().use { socket ->
            socket.broadcast = true
            val data = BROADCAST_MESSAGE.toByteArray()
            val packet = DatagramPacket(data, data.size, InetAddress.getByName("255.255.255.255"), BROADCAST_PORT)
            socket.send(packet)
            logger.trace { "Send Broadcast message: $BROADCAST_MESSAGE To Port: $BROADCAST_PORT" }
        }
    }
}

fun listenForResponses(onUserDiscovered: (InetAddress) -> Unit) {
    val socket = DatagramSocket(BROADCAST_PORT)
    val buffer = ByteArray(256)

    while (true) {
        val packet = DatagramPacket(buffer, buffer.size)
        socket.receive(packet)
        val receivedMessage = String(packet.data, 0, packet.length)

        if (packet.address.hostAddress == InetAddress.getLocalHost().hostAddress) continue
        logger.trace { "Received: $receivedMessage From Address: ${packet.address.hostAddress}" }
        if (localStatus == UserStatus.ONLINE) {
            if (receivedMessage == BROADCAST_MESSAGE) {
                respondToDiscovery(packet.address)
            } else if (receivedMessage == RESPONSE_MESSAGE) {
                onUserDiscovered(packet.address)
            }
        }
    }
}

fun respondToDiscovery(address: InetAddress) {
    logger.debug { "Respond to discovery: $address" }
    DatagramSocket().use { socket ->
        val responseData = RESPONSE_MESSAGE.toByteArray()
        val responsePacket = DatagramPacket(responseData, responseData.size, address, BROADCAST_PORT)
        socket.send(responsePacket)
    }
}

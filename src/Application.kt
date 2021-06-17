package top.harumill

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*
import io.ktor.util.collections.*
import io.ktor.websocket.*
import top.harumill.contact.server.Client
import top.harumill.contact.server.byteToObject
import top.harumill.top.harumill.contact.server.ClientPool
import top.harumill.top.harumill.message.Message
import top.harumill.top.harumill.message.MessageType
import top.harumill.top.harumill.message.PlainText
import top.harumill.top.harumill.utils.Logger
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

typealias UserID = Long

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)




@OptIn(InternalAPI::class, kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(io.ktor.websocket.WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {
//        val clients = ConcurrentHashMap<UserID,Member>()
//        val clientsSet = ConcurrentSet<Member>()


        get("/") {
            call.respondText("Hello!This is a testing Chatroom with WebSocket!", contentType = ContentType.Text.Plain)
        }

        webSocket("/chat"){

        }
        webSocket("/echo") {
            val newClient = Client(this)
            ClientPool.joinClinet(newClient)
            Logger.verbose("$newClient 上线了")
            newClient.sendMessage(PlainText("Hello!Welcome to Getto ChatRoom!"))
            try {
                while (true) {
                    when (val frame = incoming.receiveCatching().getOrNull()) {
                        null -> {
                            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session"))
                            return@webSocket
                        }
                        is Frame.Text -> {
                            val rawMessage = frame.readText()
                            Logger.verbose(rawMessage)
                            newClient.sendMessage(rawMessage)
                        }
                        is Frame.Binary -> {
                            val rawMessage = byteToObject(frame.readBytes()) as Message
                            when(rawMessage.type){
                                MessageType.PLAINTEXT -> {
                                    newClient.sendMessage(rawMessage as PlainText)
                                }
                                else -> {}
                            }

                        }
                    }
                }
            }catch (e:Exception){
                Logger.err(e.toString())
                e.printStackTrace()
            } finally {
                ClientPool.deleteClient(newClient)
                Logger.verbose("$newClient 下线了")
            }
        }
        webSocket("/public") {
//            val newClient = Member(this)
//            clientsSet += newClient
//            Logger.verbose("$newClient 上线了")
//            clientsSet.forEach {
//                it.sendMessage("$newClient 上线了")
//            }
//
//            newClient.sendMessage(PlainText("Hello!Welcome to Getto ChatRoom!"))
//            try {
//                while (true) {
//                    val frame = incoming.receiveCatching().getOrNull()
//                    if (frame == null){
//                        println("现在没有session")
//                        close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session"))
//                        return@webSocket
//                    }
//                    when (frame) {
//                        is Frame.Text -> {
//                            val rawMessage = frame.readText()
//                            Logger.verbose(rawMessage)
//                            // TODO- Parse RawMessage And Send Message To Target
//                            clientsSet.forEach {
//                                it.sendMessage(rawMessage)
//                            }
//                        }
//                        else -> {}
//                    }
//                }
//            }catch (e:Exception){
//                Logger.err(e.toString())
//                e.printStackTrace()
//            } finally {
//                clientsSet-=newClient
//                Logger.verbose("$newClient 下线了")
//                clientsSet.forEach {
//                    it.sendMessage("$newClient 下线了")
//                }
//            }
        }
    }
}


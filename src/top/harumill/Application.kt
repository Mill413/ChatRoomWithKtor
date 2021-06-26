package top.harumill

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*
import io.ktor.websocket.*
import top.harumill.contact.server.Client
import top.harumill.contact.server.ClientPool
import top.harumill.message.*
import top.harumill.top.harumill.contact.UserInfo
import top.harumill.top.harumill.message.singleMessage.PlainText
import top.harumill.message.singleMessage.command.LoginCmd
import top.harumill.utils.Logger
import java.time.Duration

typealias UserID = Long

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)




@OptIn(InternalAPI::class, kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@Suppress("unused") // Referenced in application.conf
fun Application.module() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {


        get("/") {
            call.respondText("Hello!This is a testing Chatroom with WebSocket!", contentType = ContentType.Text.Plain)
        }

        webSocket("/chat"){

        }
        webSocket("/echo") {
            val newClient = Client(this)
            ClientPool.joinClinet(newClient)
            Logger.verbose("$newClient 上线了")

            val loginMsg = ForwardMessage("Hello!Welcome to Getto ChatRoom!",null,newClient)
            newClient.sendMessage(loginMsg)

            val loginCmd = LoginCmd(UserInfo(newClient))
            newClient.sendMessage(loginCmd)

            try {
                while (true) {
                    when (val frame = incoming.receiveCatching().getOrNull()) {
                        null -> {
                            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session"))
                            return@webSocket
                        }
                        is Frame.Text -> {
                            val rawMessage = frame.readText()
                            Logger.verbose("Get message from $newClient: $rawMessage")
                            newClient.sendMessage(PlainText(rawMessage))
                        }
                        is Frame.Binary -> {
                            val rawMessage = byteToObject(frame.readBytes()) as ForwardMessage
                            Logger.verbose(rawMessage.toString())
                            newClient.sendMessage(
                                ForwardMessage(
                                    message = rawMessage.content,
                                    from = rawMessage.target,
                                    to = rawMessage.sender
                            ))

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

        }
    }
}


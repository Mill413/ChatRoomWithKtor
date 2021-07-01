package top.harumill

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*
import io.ktor.websocket.*
import top.harumill.contact.UserInfo
import top.harumill.contact.server.Client
import top.harumill.contact.server.ClientPool
import top.harumill.message.ForwardMessage
import top.harumill.message.Message
import top.harumill.message.byteToObject
import top.harumill.message.commandMessage.CommandMessage
import top.harumill.message.commandMessage.LoginCmd
import top.harumill.message.commandMessage.UpdateCmd
import top.harumill.message.singleMessage.PlainText
import top.harumill.utils.Logger
import top.harumill.utils.UIDPool
import java.time.Duration

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
        webSocket("/echo") {
            val newUID = UIDPool.generateUID()
            val newClient = Client(this, newUID)

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
                            val rawMessage = byteToObject(frame.readBytes()) as Message
                            Logger.verbose(rawMessage.toString())

                            when (rawMessage) {
                                is ForwardMessage -> {
                                    val target = rawMessage.target.id
                                    if (target == 0L) {
                                        newClient.sendMessage(
                                            ForwardMessage(
                                                message = rawMessage.content,
                                                from = rawMessage.target,
                                                to = rawMessage.sender
                                            )
                                        )
                                    } else {
                                        ClientPool.queryClient(target)?.sendMessage(rawMessage)
                                    }

                                }
                                is CommandMessage -> {
                                    when (rawMessage) {
                                        is LoginCmd -> {
                                            if (rawMessage.info.id == -1L) {
                                                rawMessage.info.id = newUID
                                            }

                                            ClientPool.joinClinet(newClient)
                                            Logger.verbose("$newClient 上线了")

                                            val loginMsg = ForwardMessage(
                                                "Hello ${newClient.info.name}!Welcome to Getto ChatRoom!",
                                                UserInfo(0L),
                                                newClient.info
                                            )
                                            newClient.sendMessage(loginMsg)

                                            val loginCmd = LoginCmd(newClient.info)
                                            loginCmd.list = ClientPool.onlineList()
                                            loginCmd.response(this)

                                        }
                                        is UpdateCmd -> {

                                        }
                                    }
                                }
                            }


                        }
                    }
                }
            } catch (e: Exception) {
                Logger.err(e.toString())
                e.printStackTrace()
            } finally {
                ClientPool.deleteClient(newClient)
                UIDPool.returnUID(newUID)
                Logger.verbose("$newClient 下线了")
            }
        }
    }
}


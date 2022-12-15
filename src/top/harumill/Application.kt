package top.harumill

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import top.harumill.contact.UserInfo
import top.harumill.contact.server.Client
import top.harumill.contact.server.ClientPool
import top.harumill.message.Message
import top.harumill.message.byteToObject
import top.harumill.message.commandMessage.Command
import top.harumill.message.commandMessage.LoginCmd
import top.harumill.message.commandMessage.UpdateCmd
import top.harumill.message.simpleMessage.SimpleMessage
import top.harumill.utils.Logger
import top.harumill.utils.UIDPool
import java.time.Duration

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {
        get("/") {
            call.respondText("Hello!This is a testing Chatroom with WebSocket!\n" +
                    "If you can read this text,please get out :)", contentType = ContentType.Text.Plain)
        }

        webSocket("/echo") {
            val newUID = UIDPool.generateUID()
            val newClient = Client(this, newUID)
            Logger.verbose("$newClient 上线了")
            send("Hello!This is a testing Chatroom with WebSocket!Your id is $newUID")

            try {
                while (true) {
                    when (val frame = incoming.receiveCatching().getOrNull()) {
                        null -> {
                            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session"))
                            return@webSocket
                        }

                        is Frame.Text -> {
                            val rawMessage = frame.readText()
                            Logger.verbose("Recv from $newClient: $rawMessage")
                            send(rawMessage)
                        }

                        else -> {}
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

        //私有协议消息以二进制形式发送，用于自写的客户端
        webSocket("/app"){
            val newUID = UIDPool.generateUID()
            val newClient = Client(this, newUID)
            Logger.verbose("$newClient 上线了")
            newClient.sendMessage("Welcome to chatroom!Your id is $newUID.")

            try {
                while (true) {
                    when (val frame = incoming.receiveCatching().getOrNull()) {
                        is Frame.Binary -> {
                            val rawMessage = byteToObject(frame.readBytes()) as Message
                            Logger.verbose(rawMessage.toString())

                            when (rawMessage) {
                                is SimpleMessage -> {
                                    ClientPool.queryClient(rawMessage.target.id)?.sendMessage(rawMessage)
                                }
                                //TODO-重写
                                is Command -> {
                                    when (rawMessage) {
                                        is LoginCmd -> {
                                            if (rawMessage.info.id == -1L) {
                                                rawMessage.info.id = newUID
                                            }

                                            ClientPool.joinClient(newClient)
                                            Logger.verbose("$newClient 上线了")

                                            val loginMsg = SimpleMessage(
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

                        null -> {
                            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session"))
                            return@webSocket
                        }
                        else -> {}
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


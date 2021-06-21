import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.websocket.*
import io.ktor.http.cio.websocket.*
import java.time.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.websocket.*
import io.ktor.client.features.websocket.WebSockets
import io.ktor.http.cio.websocket.Frame
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import top.harumill.top.harumill.message.*
import top.harumill.top.harumill.message.command.Command
import top.harumill.top.harumill.utils.Logger
import java.io.File
import java.util.*

val scanner = Scanner(System.`in`)

object WsClientApp {
    @JvmStatic
    fun main(args: Array<String>) {

        runBlocking {
            val client = HttpClient(CIO).config {
                install(WebSockets)
            }

            client.ws(
                method = HttpMethod.Get,
                host = "127.0.0.1",
                port = 8080,
                path = "/echo"
            ) {
                val session = this
                launch {
                    sendMessage(session)
                }
                receiveMessage(session)

            }
        }
    }
}

suspend fun sendMessage(session: DefaultClientWebSocketSession) {
    while (true) {
        var msg: Message
        println("""
            请选择你要发送的消息类型:
            1.文本消息
            2.文件消息
        """.trimIndent())
        val type = (1..2).random()
        println("接下来请输入消息内容:")
        when (type) {
            1 -> {
                print("请输入文本消息:")
                val text = scanner.nextLine()
                msg = PlainText(text)
                session.send(objectToByte(msg)!!)
            }
            2 -> {
                print("请输入文件路径:")
                val path = scanner.nextLine()
                msg = FileMessage(File(path))
                session.send(objectToByte(msg)!!)
            }
            else -> {

            }
        }
    }
}

suspend fun receiveMessage(session: DefaultClientWebSocketSession) {
    while (true) {
        when (val message = session.incoming.receive()) {
            is Frame.Text -> {
                Logger.verbose("Get message from server: ${message.readText()}")
            }
            is Frame.Binary -> {
                val msg = top.harumill.top.harumill.message.byteToObject(message.data) as Message
                when (msg.type) {
                    MessageType.PLAINTEXT -> {
                        Logger.verbose((msg as PlainText).contentToString())
                    }
                    MessageType.FILE -> {
                        Logger.verbose((msg as FileMessage).contentToString())
                    }
                    MessageType.MESSAGECHAIN -> {
                        Logger.verbose((msg as MessageChain).contentToString())
                    }
                    MessageType.COMMAND -> {
                        Logger.verbose((msg as Command).contentToString())
                    }
                }
            }
        }
    }
}

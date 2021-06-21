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
import java.io.File
import java.util.*

object WsClientApp {
    @JvmStatic
    fun main(args: Array<String>) {
        val scanner = Scanner(System.`in`)
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
                launch {
//                    while (true){
//                        val text = scanner.nextLine()
//                        val text2 = scanner.nextLine()
//                        val msg = PlainText(text)+PlainText(text2)
//                        val frameSend = objectToByte(msg)
//                        send(frameSend!!)
//                    }
                    val file  = File("data/a.txt")
                    val msg = FileMessage(file)
                    val frame = objectToByte(msg)
                    send(frame!!)
                }
                while (true){
                    when(val message = incoming.receive()){
                        is Frame.Text -> {
                            println("Get message from server: ${message.readText()}")
                        }
                        is Frame.Binary -> {
                            val msg = byteToObject(message.data) as Message
                            when(msg.type){
                                MessageType.PLAINTEXT -> {
                                    println((msg as PlainText).content)
                                }
                            }
                        }
                    }
                }

            }
        }
    }
}

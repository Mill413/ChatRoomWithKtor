import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import org.jline.reader.EndOfFileException
import org.jline.reader.LineReader
import org.jline.reader.LineReaderBuilder
import org.jline.reader.impl.history.DefaultHistory
import org.jline.terminal.TerminalBuilder
import java.util.concurrent.Executors
import kotlin.coroutines.resumeWithException

fun main(): Unit = runBlocking{
    val terminal = TerminalBuilder.builder().system(true).build()
    val lineReader = LineReaderBuilder
        .builder()
        .appName("Getto Chatroom")
        .terminal(terminal)
        .history(DefaultHistory())
        .build()

    val prompt = "Getto>"

    val lineList = mutableListOf<String>()
    var line = ""

    launch {
        repeat(1000){i ->
            println(i.toString())
        }
    }

    while (true){
        Input.requestInput("Getto",lineReader)
    }

}

object Input{
    val thread = Executors.newSingleThreadExecutor { task ->
        Thread(task, "Mirai Console Input Thread").also {
            it.isDaemon = false
        }
    }

    var executingCoroutine: CancellableContinuation<String>? = null

    suspend fun requestInput(hint: String,lineReader:LineReader): String {
        return suspendCancellableCoroutine { coroutine ->
            if (thread.isShutdown || thread.isTerminated) {
                coroutine.resumeWithException(EndOfFileException())
                return@suspendCancellableCoroutine
            }
            executingCoroutine = coroutine
            runCatching {
                thread.submit {
                    runCatching {
                        lineReader.readLine(
                            if (hint.isNotEmpty()) {
                                "$hint > "
                            } else "> "
                        )
                    }.let { result ->
                        executingCoroutine = null
                        coroutine.resumeWith(result)
                    }
                }
            }.onFailure { error ->
                executingCoroutine = null
                runCatching { coroutine.resumeWithException(EndOfFileException(error)) }
            }
        }
    }
}

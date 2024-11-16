package top.mill.kchat.exceptions

import io.github.oshai.kotlinlogging.KLogger

class KChatException(message: String, logger: KLogger) : Exception(message) {
    init {
        logger.error { message }
    }
}
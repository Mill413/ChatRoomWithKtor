package top.mill.kchat

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging

private object LoggerManager {
    private val loggerMap = mutableMapOf<String, KLogger>()

    fun getLogger(name: String) =
        loggerMap[name] ?: run {
            val logger = KotlinLogging.logger(name)
            loggerMap.put(name, logger)
            logger
        }
}

fun logger(loggerName: String = "Application") = LoggerManager.getLogger(loggerName)
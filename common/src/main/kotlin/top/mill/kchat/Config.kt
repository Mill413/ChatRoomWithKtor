package top.mill.kchat

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

const val BROADCAST_MESSAGE = "DISCOVER_USER"
const val RESPONSE_MESSAGE = "USER_HERE"
const val BROADCAST_DELAY = 500L
const val BROADCAST_PORT = 8888

object ConfigManager {
    val jsonFormatter = Json { prettyPrint = true }

    private var config = Config(
        broadcastMessage = "DISCOVER_USER",
        responseMessage = "USER_HERE",
        broadcastDelayMilliSecond = 500
    )
    private const val CONFIG_FILE = "data/config.json"
    private val logger = logger("ConfigManager")

    init {
        readConfigFile()
        logger.info { "Load config in $CONFIG_FILE" }
        logger.info { jsonFormatter.encodeToString(config) }
    }

    fun saveConfig() {
        val configFile = File(CONFIG_FILE)
        if (!configFile.exists()) {
            configFile.createNewFile()
        }
        configFile.writeText(Json.encodeToString(config))
        logger.info { "Save config to $CONFIG_FILE" }
        logger.info { jsonFormatter.encodeToString(config) }
    }

    fun readConfigFile() {
        val configFile = File(CONFIG_FILE)
        if (!configFile.exists()) {
            logger.error { "Config file not found in $CONFIG_FILE" }
            throw RuntimeException("Config file not found in $CONFIG_FILE")
        }
        try {
            config = Json.decodeFromString(configFile.readText().trim())
        } catch (_: Exception) {
            logger.error { "Parse config error in $CONFIG_FILE" }
        }
    }

    fun getConfig() = config
}

@Serializable
data class Config(
    val broadcastMessage: String,
    val responseMessage: String,
    val broadcastDelayMilliSecond: Long
)
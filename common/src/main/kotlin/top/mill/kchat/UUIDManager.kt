package top.mill.kchat

import top.mill.kchat.exceptions.KChatException
import java.io.File
import java.io.FileNotFoundException
import java.util.*


object UUIDManager {
    private val logger = logger("UUIDManager")
    private const val UUID_FILE_PATH = "kchat-uuid"
    private var uuid: UUID? = null

    init {
        val uuidFile = File(UUID_FILE_PATH)
        if (uuidFile.exists()) {
            try {
                val uuidString = uuidFile.readText().trim()
                uuid = UUID.fromString(uuidString)
                logger.info { "UUID loaded from file: $uuid" }
            } catch (e: FileNotFoundException) {
                logger.error { "UUID file not found: $e" }
            } catch (e: IllegalArgumentException) {
                logger.error { "Invalid UUID format in file: $e" }
            }
        } else {
            uuid = UUID.randomUUID()
            uuidFile.writeText(uuid.toString())
            logger.info { "New UUID generated and saved to file: $uuid" }
        }
    }

    fun getUUIDString(): String {
        return uuid?.toString() ?: throw KChatException("UUID has not been loaded yet", logger)
    }
}

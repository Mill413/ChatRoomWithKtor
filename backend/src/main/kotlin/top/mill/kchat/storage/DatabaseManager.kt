package top.mill.kchat.storage

import org.jetbrains.exposed.sql.Database
import top.mill.kchat.logger

object DatabaseManager {
    private const val DB_PATH = "data/kchat.db"

    private lateinit var database: Database

    init {
        try {
            database = Database.connect(
                url = "jdbc:sqlite:$DB_PATH",
                driver = "org.sqlite.JDBC"
            )
            logger("Database").info { "Connected to database $DB_PATH" }
        } catch (e: Exception) {
            logger("Database").error(e) { "Error database connection" }
        }
    }

    fun getDatabase(): Database = database
}
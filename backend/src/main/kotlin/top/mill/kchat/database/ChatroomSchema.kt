package top.mill.kchat.database

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import top.mill.kchat.contacts.Chatroom
import java.time.LocalDateTime
import java.time.ZoneOffset

class ChatroomService(database: Database) {
    object Chatrooms : Table("Chatrooms") {
        val index = integer("id").autoIncrement()
        val name = text("name")
        val uuid = text("uuid")
        val createTime = long("create_time")
        val creatorId = text("creator_id")

        override val primaryKey = PrimaryKey(index)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Chatrooms)
        }
    }

    suspend fun create(room: Chatroom, userId: String): Int = dbQuery {
        Chatrooms.insert {
            it[name] = room.name
            it[uuid] = room.id
            it[creatorId] = userId
            it[createTime] = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
        }[Chatrooms.index]
    }

    suspend fun getById(id: Int): Chatroom? {
        return dbQuery {
            Chatrooms.selectAll().where { Chatrooms.index eq id }
                .map {
                    Chatroom(
                        id = it[Chatrooms.uuid],
                        name = it[Chatrooms.name],
                        creator = it[Chatrooms.creatorId]
                    )
                }
                .singleOrNull()
        }
    }

    suspend fun getByName(name: String): List<Chatroom>? {
        return dbQuery {
            Chatrooms.selectAll().where { Chatrooms.name eq name }
                .map {
                    Chatroom(
                        id = it[Chatrooms.uuid],
                        name = it[Chatrooms.name],
                        creator = it[Chatrooms.creatorId]
                    )
                }
        }
    }

    suspend fun updateName(id: Int, room: Chatroom) {
        dbQuery {
            Chatrooms.update({ Chatrooms.index eq id }) {
                it[name] = room.name
            }
        }
    }

    suspend fun delete(uuid: String) {
        dbQuery {
            Chatrooms.deleteWhere { Chatrooms.uuid eq uuid }
        }
    }

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}


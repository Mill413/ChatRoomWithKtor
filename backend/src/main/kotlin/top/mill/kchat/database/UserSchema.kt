package top.mill.kchat.database

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import top.mill.kchat.contacts.User
import top.mill.kchat.contacts.UserStatus
import java.time.LocalDateTime
import java.time.ZoneOffset

class UserSchema(database: Database) {
    object Users : Table("Users") {
        val name = text("name")
        val uuid = text("uuid")
        val createTime = long("create_time")
        val loginTime = long("login_time")

        override val primaryKey = PrimaryKey(uuid)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Users)
        }
    }

    suspend fun create(user: User): String = dbQuery {
        Users.insert {
            it[name] = user.name
            it[uuid] = user.id
            it[createTime] = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
            it[loginTime] = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
        }[Users.uuid]
    }

    suspend fun getByUUID(uuid: String): User? {
        return dbQuery {
            Users.selectAll().where { Users.uuid eq uuid }
                .map {
                    User(
                        name = it[Users.name],
                        id = it[Users.uuid],
                        status = UserStatus.OFFLINE
                    )
                }
                .singleOrNull()
        }
    }

    suspend fun getByName(name: String): List<User>? {
        return dbQuery {
            Users.selectAll().where { Users.name eq name }
                .map {
                    User(
                        name = it[Users.name],
                        id = it[Users.uuid],
                        status = UserStatus.OFFLINE
                    )
                }
        }
    }

    suspend fun updateUserName(uuid: String, user: User) {
        dbQuery {
            Users.update({ Users.uuid eq uuid }) {
                it[name] = user.name
            }
        }
    }

    suspend fun updateUserLoginTime(uuid: String, login: Long) {
        dbQuery {
            Users.update({ Users.uuid eq uuid }) {
                it[loginTime] = login
            }
        }
    }

    suspend fun delete(uuid: String) {
        dbQuery {
            Users.deleteWhere { Users.uuid eq uuid }
        }
    }

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}


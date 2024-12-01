package top.mill.kchat.storage

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import top.mill.kchat.contacts.User
import top.mill.kchat.contacts.UserStatus
import java.time.LocalDateTime
import java.time.ZoneOffset

// TODO(Redesign methods)
class UserSchema(database: Database) {
    object Users : Table("users") {
        val userUUID = text("user_id")
        val userName = text("user_name")
        val commentName = text("comment_name").default("")
        val userCreateTime = long("user_create_time").default(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))
        val lastLoginTime = long("last_login_time")
        val userStatus = text("user_status")

        override val primaryKey = PrimaryKey(userUUID)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Users)
        }
    }

    suspend fun addUser(user: User): String = dbQuery {
        Users.insert {
            it[userName] = user.name
            it[userUUID] = user.id
            it[userCreateTime] = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
            it[lastLoginTime] = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
            it[userStatus] = user.status.toString()
            it[commentName] = user.name
        }[Users.userUUID]
    }

    suspend fun getUserByUUID(uuid: String): User? = dbQuery {
        Users.selectAll().where { Users.userUUID eq uuid }
            .map {
                User(
                    name = it[Users.userName],
                    id = it[Users.userUUID],
                    status = UserStatus.OFFLINE
                )
            }.singleOrNull()
    }

    suspend fun getUserByName(name: String): List<User> = dbQuery {
        Users.selectAll().where { Users.userName eq name }
            .map {
                User(
                    name = it[Users.userName],
                    id = it[Users.userUUID],
                    status = UserStatus.OFFLINE
                )
            }
    }

    suspend fun updateUserName(uuid: String, user: User) = dbQuery {
        Users.update({ Users.userUUID eq uuid }) {
            it[userName] = user.name
        }
    }

    suspend fun updateUserLoginTime(uuid: String, loginTime: Long = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)) =
        dbQuery {
            Users.update({ Users.userUUID eq uuid }) {
                it[lastLoginTime] = loginTime
            }
        }

    suspend fun updateUserStatus(uuid: String, status: UserStatus) = dbQuery {
        Users.update({ Users.userUUID eq uuid }) {
            it[userStatus] = status.toString()
        }
    }

    suspend fun deleteUser(uuid: String) = dbQuery {
        Users.deleteWhere { userUUID eq uuid }
    }

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}


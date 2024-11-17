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
    object Users : Table("users") {
        val userName = text("user_name")
        val userUUID = text("user_uuid")
        val userCreateTime = long("user_create_time")
        val userLoginTime = long("user_login_time")
        val userStatus = text("user_status")

        override val primaryKey = PrimaryKey(userUUID)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Users)
        }
    }

    suspend fun createUser(user: User): String = dbQuery {
        Users.insert {
            it[userName] = user.name
            it[userUUID] = user.id
            it[userCreateTime] = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
            it[userLoginTime] = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
            it[userStatus] = user.status.toString()
        }[Users.userUUID]
    }

    suspend fun getUserByUUID(uuid: String): User? {
        return dbQuery {
            Users.selectAll().where { Users.userUUID eq uuid }
                .map {
                    User(
                        name = it[Users.userName],
                        id = it[Users.userUUID],
                        status = UserStatus.OFFLINE
                    )
                }
                .singleOrNull()
        }
    }

    suspend fun getUserByName(name: String): List<User> {
        return dbQuery {
            Users.selectAll().where { Users.userName eq name }
                .map {
                    User(
                        name = it[Users.userName],
                        id = it[Users.userUUID],
                        status = UserStatus.OFFLINE
                    )
                }
        }
    }

    suspend fun updateUserName(uuid: String, user: User) {
        dbQuery {
            Users.update({ Users.userUUID eq uuid }) {
                it[userName] = user.name
            }
        }
    }

    suspend fun updateUserLoginTime(uuid: String, loginTime: Long = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)) {
        dbQuery {
            Users.update({ Users.userUUID eq uuid }) {
                it[userLoginTime] = loginTime
            }
        }
    }

    suspend fun updateUserStatus(uuid: String, status: UserStatus) {
        dbQuery {
            Users.update({ Users.userUUID eq uuid }) {
                it[userStatus] = status.toString()
            }
        }
    }

    suspend fun delete(uuid: String) {
        dbQuery {
            Users.deleteWhere { userUUID eq uuid }
        }
    }

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}


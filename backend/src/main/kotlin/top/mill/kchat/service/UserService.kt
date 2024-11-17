package top.mill.kchat.service

import top.mill.kchat.UUIDManager
import top.mill.kchat.contacts.Contact
import top.mill.kchat.contacts.User
import top.mill.kchat.contacts.UserStatus
import top.mill.kchat.database.DatabaseManager
import top.mill.kchat.database.UserSchema
import top.mill.kchat.exceptions.KChatException
import top.mill.kchat.localStatus
import top.mill.kchat.logger
import top.mill.kchat.messages.Message
import top.mill.kchat.network.Client

// Local invoke -> Request from client to broadcast & Storage data
// Network invoke -> Storage data
class UserService {
    private val logger = logger("Application")

    private val localUUID = UUIDManager.getUUIDString()

    suspend fun create(user: User): String {
        logger.info { "User ${user.name} created." }

        onLocalUser(user.id) {
            Client.broadcastPostRequest(path = "user", body = user)
        }
        val userSchema = UserSchema(DatabaseManager.getDatabase())
        if (userSchema.getUserByUUID(user.id) == null) {
            return userSchema.createUser(user)
        } else throw KChatException("User ${user.name} already exists.", logger)
    }

    suspend fun login(user: User): Int {
        logger.info { "User ${user.name} logged in." }
        onLocalUser(user.id) {
            Client.broadcastPutRequest(path = "user/login", body = user)
            localStatus = UserStatus.ONLINE
        }
        val userSchema = UserSchema(DatabaseManager.getDatabase())
        if (userSchema.getUserByUUID(user.id) != null) {
            return userSchema.updateUserLoginTime(user.id)
        } else throw KChatException("User ${user.name} does not exist.", logger)
    }

    suspend fun logout(user: User): Int {
        logger.info { "User ${user.name} logged out." }
        onLocalUser(user.id) {
            Client.broadcastPostRequest(path = "user/logout", body = user)
            localStatus = UserStatus.OFFLINE
        }
        val userSchema = UserSchema(DatabaseManager.getDatabase())
        if (userSchema.getUserByUUID(user.id) != null) {
            return userSchema.updateUserStatus(user.id, UserStatus.OFFLINE)
        } else throw KChatException("User ${user.name} does not exist.", logger)
    }

    suspend fun updateInfo(newUser: User): Int {
        logger.info { "User ${newUser.id} updated to new name: ${newUser.name}." }
        onLocalUser(newUser.id) {
            Client.broadcastPutRequest(
                path = "user/name",
                body = newUser
            )
        }
        val userSchema = UserSchema(DatabaseManager.getDatabase())
        if (userSchema.getUserByUUID(newUser.id) != null) {
            return userSchema.updateUserName(newUser.id, newUser)
        } else throw KChatException("User ${newUser.name} does not exist.", logger)
    }

    suspend fun queryUserByUUID(uuid: String) = UserSchema(DatabaseManager.getDatabase()).getUserByUUID(uuid)

    suspend fun queryUserByName(name: String) = UserSchema(DatabaseManager.getDatabase()).getUserByName(name)

    suspend fun deleteUser(uuid: String): Int {
        logger.info { "User $uuid deleted." }
        onLocalUser(uuid) {
            Client.broadcastDeleteRequest(path = "user/${uuid}", body = uuid)
        }
        val userSchema = UserSchema(DatabaseManager.getDatabase())
        if (userSchema.getUserByUUID(uuid) != null) {
            return userSchema.delete(uuid)
        } else throw KChatException("User $uuid does not exist.", logger)
    }

    fun sendMessage(from: User, to: Contact, message: Message) {
        TODO("Send Message from a user to other user or a Chatroom")
    }

    private inline fun onLocalUser(uuid: String, block: () -> Unit) {
        if (localUUID == uuid) block()
    }
}


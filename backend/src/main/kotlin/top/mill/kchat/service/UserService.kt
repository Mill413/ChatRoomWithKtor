package top.mill.kchat.service

import top.mill.kchat.UUIDManager
import top.mill.kchat.contacts.User
import top.mill.kchat.contacts.UserStatus
import top.mill.kchat.exceptions.KChatException
import top.mill.kchat.localStatus
import top.mill.kchat.logger
import top.mill.kchat.network.Client
import top.mill.kchat.storage.DatabaseManager
import top.mill.kchat.storage.UserSchema

// TODO(Redesign methods)
class UserService {
    private val logger = logger("UserService")
    private val localUUID = UUIDManager.getUUIDString()

    suspend fun createUser(user: User): String {
        logger.info { "User ${user.name} created." }

        onLocalUser(user.id) {
            Client.broadcastPostRequest(path = "user", body = user)
        }

        val userSchema = UserSchema(DatabaseManager.getDatabase())
        if (userSchema.getUserByUUID(user.id) == null) {
            return userSchema.addUser(user)
        } else throw KChatException("User ${user.name} already exists.", logger)
    }

    suspend fun login(user: User): Int {
        logger.info { "User ${user.name} logged in." }
        val userSchema = UserSchema(DatabaseManager.getDatabase())
        onLocalUser(user.id) {
            Client.broadcastPutRequest(path = "user/login", body = user)
            localStatus = UserStatus.ONLINE
//            userSchema.createUser(user)
        }
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
            return userSchema.deleteUser(uuid)
        } else throw KChatException("User $uuid does not exist.", logger)
    }

    private inline fun onLocalUser(uuid: String, block: () -> Unit) {
        if (localUUID == uuid) block()
    }
}


package top.mill.kchat.service

import top.mill.kchat.UUIDManager
import top.mill.kchat.contacts.Contact
import top.mill.kchat.contacts.User
import top.mill.kchat.database.DatabaseManager
import top.mill.kchat.database.UserSchema
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
        } else throw Exception("User ${user.name} already exists.")
    }

    suspend fun login(user: User) {
        logger.info { "User ${user.name} logged in." }
        onLocalUser(user.id) {
            Client.broadcastPutRequest(path = "user/login", body = user)
        }

    }

    fun logout() {
        TODO("Logout")
    }

    fun updateInfo() {
        TODO("Update information of user")
    }

    fun deleteUser() {
        TODO("Delete a user")
    }

    fun sendMessage(from: User, to: Contact, message: Message) {
        TODO("Send Message from a user to other user or a Chatroom")
    }

    private inline fun onLocalUser(uuid: String, block: () -> Unit) {
        if (localUUID == uuid) block()
    }
}


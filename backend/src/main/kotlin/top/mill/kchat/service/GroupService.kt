package top.mill.kchat.service

import top.mill.kchat.UUIDManager
import top.mill.kchat.contacts.Group
import top.mill.kchat.contacts.User
import top.mill.kchat.database.DatabaseManager
import top.mill.kchat.database.GroupSchema
import top.mill.kchat.exceptions.KChatException
import top.mill.kchat.logger
import top.mill.kchat.network.Client

class GroupService {
    private val logger = logger("Application")
    private val localUUID = UUIDManager.getUUIDString()

    suspend fun createGroup(group: Group): String {
        logger.info { "Creating Group ${group.name}" }

        onLocalUser(group.creator) {
            Client.broadcastPostRequest(path = "group", body = group)
        }

        val groupSchema = GroupSchema(DatabaseManager.getDatabase())
        if (groupSchema.getGroupByUUID(group.id) == null) {
            return groupSchema.createGroup(group)
        } else throw KChatException("Group ${group.id} already exists", logger)
    }

    suspend fun queryGroupByUUID(uuid: String) = GroupSchema(DatabaseManager.getDatabase()).getGroupByUUID(uuid)

    suspend fun queryGroupByName(groupName: String) =
        GroupSchema(DatabaseManager.getDatabase()).getGroupsByName(groupName)

    suspend fun joinGroup(group: Group, user: User): String {
        logger.info { "User ${user.name} joining Group ${group.name}" }

        onLocalUser(user.id) {
            Client.broadcastPutRequest(
                uuidList = group.members.map { user -> user.id },
                path = "group/join/${group.id}",
                body = user
            )
        }

        val groupSchema = GroupSchema(DatabaseManager.getDatabase())
        if (groupSchema.getGroupByUUID(group.id) != null) {
            return groupSchema.addUserGroup(user, group)
        } else throw KChatException("Group ${group.id} does not exist.", logger)
    }

    suspend fun leaveGroup(group: Group, user: User): Int {
        logger.info { "User ${user.name} leaving Group ${group.name}" }

        onLocalUser(user.id) {
            Client.broadcastPutRequest(
                uuidList = group.members.map { user -> user.id },
                path = "group/leave/${group.id}",
                body = user
            )
        }

        val groupSchema = GroupSchema(DatabaseManager.getDatabase())
        if (groupSchema.getGroupByUUID(group.id) != null) {
            return groupSchema.deleteUserGroup(user, group)
        } else throw KChatException("Group ${group.id} does not exist.", logger)
    }

    suspend fun deleteGroup(group: Group): Int {
        logger.info { "Deleting Group ${group.name}" }

        onLocalUser(group.creator) {
            Client.broadcastDeleteRequest(
                uuidList = group.members.map { user -> user.id },
                path = "group/${group.id}",
                body = group
            )
        }

        val groupSchema = GroupSchema(DatabaseManager.getDatabase())
        if (groupSchema.getGroupByUUID(group.id) != null) {
            return groupSchema.deleteGroupByUUID(group.id)
        } else throw KChatException("Group ${group.id} does not exist.", logger)
    }

    private inline fun onLocalUser(uuid: String, block: () -> Unit) {
        if (localUUID == uuid) block()
    }
}


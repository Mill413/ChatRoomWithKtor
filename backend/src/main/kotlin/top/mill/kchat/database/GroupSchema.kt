package top.mill.kchat.database

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import top.mill.kchat.contacts.Group
import top.mill.kchat.contacts.User
import java.time.LocalDateTime
import java.time.ZoneOffset

class GroupSchema(database: Database) {
    object Groups : Table("groups") {
        val groupName = text("group_name")
        val groupUUID = text("group_uuid")
        val groupCreateTime = long("group_create_time")
        val groupCreatorUUID = text("group_creator_uuid")

        override val primaryKey = PrimaryKey(groupUUID)
    }

    object UsersGroups : Table("users_groups") {
        val userUUID = text("user_uuid")
        val groupUUID = text("group_uuid")
    }

    init {
        transaction(database) {
            SchemaUtils.create(Groups)
        }
    }

    suspend fun createGroup(group: Group, creator: User): String = dbQuery {
        Groups.insert {
            it[groupName] = group.name
            it[groupUUID] = group.id
            it[groupCreatorUUID] = creator.id
            it[groupCreateTime] = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
        }[Groups.groupUUID]
    }

    suspend fun getGroupByUUID(uuid: String): Group? {
        TODO("Get a group from group table and user_group table by group_uuid")
    }

    suspend fun getGroupsByName(name: String): List<Group>? {
        TODO("Get a group from group table and user_group table by group_name")
    }

    suspend fun updateGroupName(id: Int, room: Group) {
        TODO("Update group name")
    }

    suspend fun delete(uuid: String) {
        dbQuery {
            Groups.deleteWhere { groupUUID eq uuid }
        }
    }

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}


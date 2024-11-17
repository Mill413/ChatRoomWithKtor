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

    object UserGroup : Table("user_group") {
        val userUUID = text("user_uuid")
        val groupUUID = text("group_uuid")
    }

    init {
        transaction(database) {
            SchemaUtils.create(Groups)
            SchemaUtils.create(UserGroup)
        }
    }

    suspend fun createGroup(group: Group, creator: User): String = dbQuery {
        group.members.forEach { user ->
            UserGroup.insert {
                it[userUUID] = user.id
                it[groupUUID] = group.id
            }
        }
        Groups.insert {
            it[groupName] = group.name
            it[groupUUID] = group.id
            it[groupCreatorUUID] = creator.id
            it[groupCreateTime] = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
        }[Groups.groupUUID]
    }

    suspend fun getGroupByUUID(uuid: String): Group? = dbQuery {
        TODO("Get a group from group table and user_group table by group_uuid")
    }

    suspend fun getGroupsByName(name: String): List<Group>? = dbQuery {
        TODO("Get a group from group table and user_group table by group_name")
    }

    suspend fun updateGroupName(id: Int, room: Group) = dbQuery {
        TODO("Update group name")
    }

    suspend fun addUserGroup(user: User, group: Group) = dbQuery {
        UserGroup.insert {
            it[userUUID] = user.id
            it[groupUUID] = group.id
        }
    }

    suspend fun deleteUserGroup(user: User, group: Group) = dbQuery {
        UserGroup.deleteWhere {
            userUUID eq user.id
            groupUUID eq group.id
        }
    }


    suspend fun deleteGroupByUUID(uuid: String) = dbQuery {
        Groups.deleteWhere { groupUUID eq uuid }
    }


    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}


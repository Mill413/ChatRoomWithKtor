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

    suspend fun createGroup(group: Group) = dbQuery {
        group.members.forEach { user ->
            UserGroup.insert {
                it[userUUID] = user.id
                it[groupUUID] = group.id
            }
        }
        Groups.insert {
            it[groupName] = group.name
            it[groupUUID] = group.id
            it[groupCreatorUUID] = group.creator
            it[groupCreateTime] = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
        }[Groups.groupUUID]
    }

    suspend fun getGroupByUUID(uuid: String) = dbQuery {
        Groups.selectAll().where {
            Groups.groupUUID eq uuid
        }.map {
            Group(
                id = it[Groups.groupUUID],
                name = it[Groups.groupName],
                creator = it[Groups.groupCreatorUUID]
            )
        }.singleOrNull()
    }

    suspend fun getGroupsByName(name: String) = dbQuery {
        Groups.selectAll().where {
            Groups.groupName eq name
        }.map {
            Group(
                id = it[Groups.groupUUID],
                name = it[Groups.groupName],
                creator = it[Groups.groupCreatorUUID]
            )
        }
    }

    suspend fun getUsersByGroup(group: Group) = dbQuery {
        (Groups innerJoin UserGroup)
            .select(
                Groups.groupUUID,
                Groups.groupName,
                Groups.groupCreatorUUID
            )
            .where { UserGroup.groupUUID eq group.id }
            .map {
                Group(
                    name = it[Groups.groupName],
                    id = it[Groups.groupUUID],
                    creator = it[Groups.groupCreatorUUID]
                )
            }
    }

    suspend fun getGroupsByUser(user: User) = dbQuery {
        (Groups innerJoin UserGroup)
            .select(
                Groups.groupUUID,
                Groups.groupName,
                Groups.groupCreatorUUID
            )
            .where { UserGroup.userUUID eq user.id }
            .map {
                Group(
                    name = it[Groups.groupName],
                    id = it[Groups.groupUUID],
                    creator = it[Groups.groupCreatorUUID]
                )
            }
    }

    suspend fun deleteGroupByUUID(uuid: String) = dbQuery {
        Groups.deleteWhere { groupUUID eq uuid }
    }

    suspend fun updateGroupName(uuid: String, newGroup: Group) = dbQuery {
        Groups.update({ Groups.groupUUID eq uuid }) {
            it[groupName] = newGroup.name
        }
    }

    suspend fun addUserGroup(user: User, group: Group) = dbQuery {
        UserGroup.insert {
            it[userUUID] = user.id
            it[groupUUID] = group.id
        }[Groups.groupUUID]
    }

    suspend fun deleteUserGroup(user: User, group: Group) = dbQuery {
        UserGroup.deleteWhere {
            userUUID eq user.id
            groupUUID eq group.id
        }
    }

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}


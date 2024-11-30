package top.mill.kchat.network.route

import io.ktor.http.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.request.ContentTransformationException
import io.ktor.server.response.*
import io.ktor.server.routing.*
import top.mill.kchat.contacts.Group
import top.mill.kchat.exceptions.KChatException
import top.mill.kchat.logger
import top.mill.kchat.service.GroupService

fun Route.groupRoute() {
    val service = GroupService()
    val logger = logger("Service")
    route("/group") {
        get("/query") {
            val uuid = call.parameters["uuid"]
            val name = call.parameters["name"]
            when {
                uuid != null -> {
                    val group = service.queryGroupByUUID(uuid)
                    if (group != null) {
                        call.respond(HttpStatusCode.OK, group)
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Group UUID: $uuid not found")
                    }
                }

                name != null -> {
                    val groups = service.queryGroupByName(name)
                    if (groups.isNotEmpty()) {
                        call.respond(HttpStatusCode.OK, groups)
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Group Name: $name not found")
                    }
                }

                else -> {
                    call.respondText(text = "Parameter id or name is required", status = HttpStatusCode.BadRequest)
                }
            }
        }

        post {
            try {
                val group = call.receive<Group>()
                val remoteAddress = call.request.origin.remoteHost
                logger.info { "Received from $remoteAddress: $group" }

                val uuid = service.createGroup(group)
                call.respondText(
                    text = "Group $uuid from $remoteAddress created",
                    status = HttpStatusCode.Companion.Created
                )
            } catch (e: ContentTransformationException) {
                logger.error { "${e.message}" }
                call.respondText(text = "Failed to parse Group", status = HttpStatusCode.Companion.BadRequest)
            } catch (e: Exception) {
                logger.error { "${e.message}" }
                call.respondText(text = "Failed to create Group", status = HttpStatusCode.Companion.InternalServerError)
            }
        }

        put("/join/{userID}") {
            val group = call.receive<Group>()
            val userUUID = call.parameters["userID"]
            if (userUUID != null) {
                service.joinGroup(user = userUUID, group = group)
                call.respond(HttpStatusCode.OK, "User $userUUID joined Group ${group.name}")
            } else {
                call.respond(HttpStatusCode.NotFound, "User UUID: $userUUID not found")
            }
        }
        put("/leave/{groupID}") {
            val group = call.receive<Group>()
            val userUUID = call.parameters["userID"]
            if (userUUID != null) {
                service.leaveGroup(user = userUUID, group = group)
                call.respond(HttpStatusCode.OK, "User $userUUID joined Group ${group.name}")
            } else {
                call.respond(HttpStatusCode.NotFound, "User UUID: $userUUID not found")
            }
        }
        put("/update/{groupID}") {
            val group = call.receive<Group>()
            try {
                service.updateGroupName(group)
                call.respond(HttpStatusCode.OK, "Group ${group.id} Updated")
            } catch (e: Exception) {
                if (e !is KChatException) {
                    logger.error { "${e.message}" }
                }
                call.respond(HttpStatusCode.InternalServerError, "Group ${group.name} update failed")
            }
        }

        delete {
            try {
                val group = call.receive<Group>()
                service.deleteGroup(group)
                call.respond(HttpStatusCode.OK, "Group: ${group.id} deleted")
            } catch (e: Exception) {
                if (e !is KChatException) {
                    logger.error { "${e.message}" }
                }
                call.respond(HttpStatusCode.InternalServerError)
            }
        }
    }
}
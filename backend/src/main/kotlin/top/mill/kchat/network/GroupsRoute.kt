package top.mill.kchat.network

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import top.mill.kchat.contacts.Group
import top.mill.kchat.database.DatabaseManager
import top.mill.kchat.database.GroupSchema

fun Route.groupsRoute() {
    val service = GroupSchema(DatabaseManager.getDatabase())
    route("/room") {
        get("/id/{id}") {
            TODO("Get chatroom by ID")
        }

        get("/name/{name}") {
            TODO("Get chatroom by name")
        }

        post {
            try {
                val room = call.receive<Group>()
                val uuid = service.createGroup(room, room.creator)
                call.respondText(text = "Chatroom $uuid created", status = HttpStatusCode.Companion.Created)
            } catch (_: ContentTransformationException) {
                call.respondText(text = "Failed to parse Chatroom", status = HttpStatusCode.Companion.BadRequest)
            } catch (_: Exception) {
                call.respondText(
                    text = "Failed to create Chatroom",
                    status = HttpStatusCode.Companion.InternalServerError
                )
            }
        }

        put("/{id}") {
            TODO("Update chatroom by ID")
        }

        delete("/{id}") {
            TODO("Delete chatroom by ID")
        }
    }
}
package top.mill.kchat.service

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.Database
import top.mill.kchat.contacts.Chatroom
import top.mill.kchat.database.ChatroomService

// TODO("Complete implementation of ChatroomService")
class ChatroomService {
    fun createRoom() {}

    fun joinRoom() {}

    fun leaveRoom() {}

    fun deleteRoom() {}
}

fun Route.chatroomsRoute(database: Database) {
    val service = ChatroomService(database = database)
    route("/room") {
        get("/id/{id}") {
            TODO("Get chatroom by ID")
        }

        get("/name/{name}") {
            TODO("Get chatroom by name")
        }

        post {
            try {
                val room = call.receive<Chatroom>()
                val uuid = service.create(room, room.creator)
                call.respondText(text = "Chatroom $uuid created", status = HttpStatusCode.Created)
            } catch (_: ContentTransformationException) {
                call.respondText(text = "Failed to parse Chatroom", status = HttpStatusCode.BadRequest)
            } catch (_: Exception) {
                call.respondText(text = "Failed to create Chatroom", status = HttpStatusCode.InternalServerError)
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
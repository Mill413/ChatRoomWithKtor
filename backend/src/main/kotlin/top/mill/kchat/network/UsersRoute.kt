package top.mill.kchat.network

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.mill.kchat.contacts.User
import top.mill.kchat.service.UserService
import java.net.InetAddress

fun Route.usersRoute() {
    val service = UserService()
    route("/user") {
        get("/query") {
            val uuid = call.parameters["id"]
            val name = call.parameters["name"]
            when {
                uuid != null -> {
                    call.respondText(text = Json.encodeToString(service.queryUserByUUID(uuid)))
                }

                name != null -> {
                    call.respondText(text = Json.encodeToString(service.queryUserByName(name)))
                }

                else         -> {
                    call.respondText(text = "Parameter id or name is required", status = HttpStatusCode.BadRequest)
                }
            }
        }

        post {
            try {
                val user = call.receive<User>()
                val uuid = service.create(user)
                val remoteAddress = call.receive<InetAddress>()

                Client.updateUUID(uuid, remoteAddress)
                call.respondText(
                    text = "User $uuid from $remoteAddress created",
                    status = HttpStatusCode.Companion.Created
                )
            } catch (_: ContentTransformationException) {
                call.respondText(text = "Failed to parse User", status = HttpStatusCode.Companion.BadRequest)
            } catch (_: Exception) {
                call.respondText(text = "Failed to create User", status = HttpStatusCode.Companion.InternalServerError)
            }
        }

        put("/name") {
            TODO("Update User Name by ID")
        }

        put("/login") {
            val user = call.receive<User>()
            service.login(user)
        }

        delete("/{id}") {
            TODO("Delete user by ID")
        }
    }
}
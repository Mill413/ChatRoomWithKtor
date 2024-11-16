package top.mill.kchat.network

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import top.mill.kchat.contacts.User
import top.mill.kchat.service.UserService
import java.net.InetAddress

fun Route.usersRoute() {
    val service = UserService()
    route("/user") {
        get("/query") {
            val uuid = call.parameters["id"]
            val name = call.parameters["name"]
            TODO("Return user by id or name")
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
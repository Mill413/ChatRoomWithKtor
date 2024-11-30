package top.mill.kchat.network.route

import io.ktor.http.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.request.ContentTransformationException
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.delay
import top.mill.kchat.contacts.User
import top.mill.kchat.exceptions.KChatException
import top.mill.kchat.logger
import top.mill.kchat.network.Client
import top.mill.kchat.service.UserService
import java.net.InetAddress

// TODO(Redesign API)
fun Route.userRoute() {
    val service = UserService()
    val logger = logger("UserRoute")
    logger.info { "Listening to UserRoute" }
    route("/user") {
        get("/query") {
            val uuid = call.parameters["id"]
            val name = call.parameters["name"]
            when {
                uuid != null -> {
                    val user = service.queryUserByUUID(uuid)
                    if (user != null) {
                        call.respond(HttpStatusCode.OK, user)
                    } else {
                        call.respond(HttpStatusCode.NotFound, "User UUID: $uuid not found")
                    }
                }

                name != null -> {
                    val users = service.queryUserByName(name)
                    if (users.isNotEmpty()) {
                        call.respond(HttpStatusCode.OK, users)
                    } else {
                        call.respond(HttpStatusCode.NotFound, "User Name: $name not found")
                    }
                }

                else -> {
                    call.respondText(text = "Parameter id or name is required", status = HttpStatusCode.BadRequest)
                }
            }
        }

        post {
            logger.info { "here" }
            try {
                val user = call.receive<User>()
                val remoteAddress = call.request.origin.remoteHost
                logger.info { "Received from $remoteAddress: $user" }

                val uuid = service.createUser(user)
                Client.updateAddressByUUID(uuid, InetAddress.getByName(remoteAddress))
                call.respondText(
                    text = "User $uuid from $remoteAddress created",
                    status = HttpStatusCode.Companion.Created
                )
            } catch (e: ContentTransformationException) {
                logger.error { "${e.message}" }
                call.respondText(text = "Failed to parse User", status = HttpStatusCode.Companion.BadRequest)
            } catch (e: Exception) {
                logger.error { "${e.message}" }
                call.respondText(text = "Failed to create User", status = HttpStatusCode.Companion.InternalServerError)
            }
        }

        put("/name") {
            val user = call.receive<User>()
            try {
                service.updateInfo(user)
                call.respond(HttpStatusCode.OK, "User ${user.id} updated")
            } catch (e: Exception) {
                if (e !is KChatException) {
                    logger.error { "${e.message}" }
                }
                call.respond(HttpStatusCode.InternalServerError, "User ${user.name} update failed")
            }
        }

        put("/login") {
            val user = call.receive<User>()
            try {
                service.login(user)
                val host = call.request.local.remoteHost
                delay(500)
                Client.createDeviceSessionFromClient(user.id, host)
                call.respond(HttpStatusCode.OK, "User ${user.name} logged in")
            } catch (e: Exception) {
                if (e !is KChatException) {
                    logger.error { "${e.message}" }
                }
                call.respondText(text = "User: ${user.name} login failed", status = HttpStatusCode.InternalServerError)
            }
        }

        delete("/{id}") {
            try {
                val id = call.parameters["id"]
                if (id != null) {
                    service.deleteUser(id)
                    call.respond(HttpStatusCode.OK, "User: $id deleted")
                } else {
                    logger.error { "Parameter id is required" }
                    call.respond(HttpStatusCode.BadRequest, "Parameter id is required")
                }
            } catch (e: Exception) {
                if (e !is KChatException) {
                    logger.error { "${e.message}" }
                }
                call.respond(HttpStatusCode.InternalServerError)
            }
        }
    }
}
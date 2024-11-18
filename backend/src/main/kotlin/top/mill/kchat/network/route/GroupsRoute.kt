package top.mill.kchat.network.route

import io.ktor.server.routing.*
import top.mill.kchat.service.GroupService

fun Route.groupsRoute() {
    val service = GroupService()
    route("/group") {
        get("/query") {
            TODO("Get group by parameters id and name")
        }

        post {
            TODO("Create a Group")
        }

        put {
            route("/join/{groupID}") {
                TODO("Join a group with body of User")
            }
            route("/leave/{groupID}") {
                TODO("Leave a group with body of user")
            }
            route("/update/{groupID}") {
                TODO("Update information of group by ID")
            }
        }

        delete("/{id}") {
            TODO("Delete group by ID")
        }
    }
}
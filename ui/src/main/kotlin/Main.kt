package top.mill

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import top.mill.kchat.Backend
import top.mill.kchat.UUIDManager
import top.mill.kchat.contacts.User
import top.mill.kchat.contacts.UserStatus
import top.mill.kchat.service.UserService

fun main(args: Array<String>): Unit = runBlocking {
    val local = User(
        UserStatus.ONLINE,
        UUIDManager.getUUIDString(),
        args[0]
    )

    launch { Backend().start(args) }

    UserService().createUser(local)
}
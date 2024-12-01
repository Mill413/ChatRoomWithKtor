package top.mill

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import top.mill.kchat.Backend
import top.mill.kchat.UUIDManager
import top.mill.kchat.contacts.User
import top.mill.kchat.contacts.UserStatus

fun main(args: Array<String>): Unit = runBlocking {
    val local = User(
        UserStatus.ONLINE,
        UUIDManager.getUUIDString(),
        args[0]
    )

    launch { Backend().start(args) }

    // TODO(User login)

    // TODO(Send Message to a if local user is not a)

    // TODO(Send Message to non-exist c)

    // TODO(Create Group test_group if local user is a)

    // TODO(Join Group a if local user is not a)

    // TODO(Send Message to test_group)

    // TODO(Leave Group test_group)

    // TODO(User logout)
}
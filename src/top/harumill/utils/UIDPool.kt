package top.harumill.utils

object UIDPool {
    private val uidPool = mutableSetOf<Long>()

    private const val MAX_SIZE = 1000
    init {
        for (i in 1..MAX_SIZE) {
            uidPool.add(i.toLong())
        }
    }

    fun generateUID(): Long {
        val uid = uidPool.random()
        uidPool.remove(uid)
        return uid
    }

    fun returnUID(uid: Long) {
        uidPool.add(uid)
    }
}
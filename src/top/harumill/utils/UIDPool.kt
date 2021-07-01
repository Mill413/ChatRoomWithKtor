package top.harumill.utils

object UIDPool {
    private val uidPool = mutableListOf<Long>()

    init {
        for (i in 1..1000) {
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
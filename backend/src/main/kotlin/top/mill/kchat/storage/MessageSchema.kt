package top.mill.kchat.storage

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction

class MessageSchema(database: Database) {
    object Messages : Table("messages") {
        val msg_id = text("msg_id")
        val senderUUID = text("sender_uuid")
        val receiverType = text("receiver_type")
        val receiverUUID = text("receiver_uuid")
        val content = text("content")
        val sendTime = long("send_time")
        val status = text("status")
        val msgType = text("msg_type")

        override val primaryKey = PrimaryKey(msg_id)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Messages)
        }
    }

    // TODO(Complete functions)
}
package top.harumill.message.commandMessage

// TODO-同步信息类
class UpdateCmd() : CommandMessage {

    override val type: CommandTYPE
        get() = CommandTYPE.SYNC

    override fun toString(): String {
        return contentToString()
    }


}

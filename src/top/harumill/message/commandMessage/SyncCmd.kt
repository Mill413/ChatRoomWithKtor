package top.harumill.message.commandMessage

// TODO-同步信息类
class UpdateCmd() : Command {

    override val type: CommandTYPE
        get() = CommandTYPE.SYNC

    override fun toString(): String {
        return contentToString()
    }


}

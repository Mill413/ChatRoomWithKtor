package top.harumill.top.harumill.message.singleMessage

import top.harumill.message.Message
import top.harumill.message.MessageType
import top.harumill.message.singleMessage.SingleMessage
import java.io.File

class FileMessage(resource: File?=null): SingleMessage {
    companion object{
        const val serialVersionUID:Long = 22
    }
    override val type: MessageType = MessageType.FILE


    private var file = resource

    var fileType = if (file == null) "" else file!!.extension

    var fileSize = if (file == null) 0 else file!!.length()

    fun saveFileTo(path:String){
        if (file != null) file!!.copyTo(File(path),true)
    }

    override fun toString(): String {
        return "[getto:file: ${file?.name}]"
    }

    override fun contentToString(): String {
        return "[文件]${file?.name}"
    }
}
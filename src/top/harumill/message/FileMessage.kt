package top.harumill.top.harumill.message

import java.io.File

class FileMessage(resource: File?=null):Message {
    override val type: MessageType = MessageType.FILE

    private var file = resource



    var fileType = if (file == null) "" else file!!.extension

    var fileSize = if (file == null) 0 else file!!.length()

    fun saveFileTo(path:String){
        if (file != null) file!!.copyTo(File(path))
    }
}
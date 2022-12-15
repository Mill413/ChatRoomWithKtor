package top.harumill.message.simpleMessage

import java.io.File

class FileMessage(resource: File? = null) : SingleMessage {
    companion object {
        const val serialVersionUID: Long = 22
    }


    private val file = resource

    var fileType = file?.extension ?: ""

    var fileSize = file?.length() ?: 0

    fun saveFileTo(path: String): File? {
        return file?.copyTo(File(path + file.name), true)
    }

    override fun toString(): String {
        return "[getto:file: ${file?.name}]"
    }

    override fun contentToString(): String {
        return "[文件]${file?.name}"
    }
}
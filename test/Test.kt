import top.harumill.top.harumill.message.FileMessage
import java.io.*

enum class Type{
    PLAIN,
    IMAGE,
    CHAIN
}
open class A(num:Int, type:Type):Serializable{
    val i = num
    val mytype = type
}
class B(num: Int,type: Type):A(num,type){
    val extra = "hi"
}
class C(num: Int,type: Type):A(num,type){
    val list = mutableListOf<A>()
}

fun main(){
//    val c = C(2,Type.CHAIN)
//    c.list.add(B(1,Type.IMAGE))
//    c.list.add(A(2,Type.PLAIN))
//
//    val byte = objectToByte(c)
//    val obj = byteToObject(byte) as A
//    if (obj.mytype == Type.CHAIN){
//        (obj as C).list.forEach {
//            println(it.i)
//        }
//    }

    val file  = File("data/a.txt")
    val msg = FileMessage(file)
    println(msg.fileSize)
}
fun objectToByte(obj: Any?): ByteArray? {
    var bytes: ByteArray? = null
    try {
        // object to bytearray
        val bo = ByteArrayOutputStream()
        val oo = ObjectOutputStream(bo)
        oo.writeObject(obj)
        bytes = bo.toByteArray()
        bo.close()
        oo.close()
    } catch (e: Exception) {
        println("translation" + e.message)
        e.printStackTrace()
    }
    return bytes
}

fun byteToObject(bytes: ByteArray?): Any? {
    var obj: Any? = null
    try {
        // bytearray to object
        val bi = ByteArrayInputStream(bytes)
        val oi = ObjectInputStream(bi)
        obj = oi.readObject()
        bi.close()
        oi.close()
    } catch (e: java.lang.Exception) {
        println("translation" + e.message)
        e.printStackTrace()
    }
    return obj
}
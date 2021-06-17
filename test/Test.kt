import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

enum class Type{
    PLAIN,
    IMAGE
}
open class A(num:Int, type:Type):Serializable{
    val i = num
    val mytype = type
}
class B(num: Int,type: Type):A(num,type){
    val extra = "hi"
}
fun main(){
    val b = B(3,Type.PLAIN)

    val bToByte = objectToByte(b)
    val byteToObj = byteToObject(bToByte) as A
    if (byteToObj.mytype == Type.PLAIN){
        println((byteToObj as B).extra)
    }
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
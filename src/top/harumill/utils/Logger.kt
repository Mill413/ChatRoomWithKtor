package top.harumill.top.harumill.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Logger {

    private fun ouput(identifier:String,msg:String){
        println("${formatter.format(LocalDateTime.now())} Getto/${identifier}: $msg")
    }

    fun verbose(ver:String){
        ouput("V",ver)
    }
    fun info(info:String){
        ouput("I",info)
    }

    fun warn(warning:String){
        ouput("W",warning)
    }

    fun err(error:String){
        ouput("E",error)
    }
}

val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS")

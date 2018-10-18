package com.artear.processor

import javax.annotation.processing.Messager
import javax.tools.Diagnostic


fun Messager.log(message : String){
    printMessage(Diagnostic.Kind.NOTE, message)
}

fun Messager.error(message : String){
    printMessage(Diagnostic.Kind.ERROR, message)
}
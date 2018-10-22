package com.artear.processor.util

import com.artear.annotations.JsInterface
import com.artear.processor.process.Process
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.TypeElement

fun <T> RoundEnvironment.executeProcess(process: Process<T>) {
    getElementsAnnotatedWith(JsInterface::class.java)
            .filterIsInstance<TypeElement>()
            .filter { Utils.isValidClass(it, process.messager) }
            .map { process.buildAnnotationClass(it) }
            .forEach { process.createAnnotationFile(it) }

}
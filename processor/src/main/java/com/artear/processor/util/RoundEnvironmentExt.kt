package com.artear.processor.util

import com.artear.processor.process.Process
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.TypeElement

fun <T> RoundEnvironment.executeProcess(process: Process<T>) {
    getElementsAnnotatedWith(process.annotation)
            .filterIsInstance<TypeElement>()
            .filter { Utils.isValidClass(it, process.messager) }
            .map { process.buildAnnotationClass(it) }
            .forEach { process.createAnnotationFile(it) }

}
package com.artear.processor.process

import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types


abstract class Process<T>(val processingEnv: ProcessingEnvironment) {

    val messager: Messager = processingEnv.messager
    val elements: Elements = processingEnv.elementUtils
    val typeUtils: Types = processingEnv.typeUtils

    abstract val annotation: Class<out Annotation>

    abstract fun buildAnnotationClass(typeElement: TypeElement): T

    abstract fun createAnnotationFile(annotationClass: T)
}
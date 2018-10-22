package com.artear.processor.process

import javax.lang.model.element.TypeElement


interface Process<T> {

    val annotation: Class<out Annotation>

    fun buildAnnotationClass(typeElement: TypeElement): T

    fun createAnnotationFile(annotationClass: T, pathname: String)
}
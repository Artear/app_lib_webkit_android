package com.artear.processor.process

import com.artear.annotations.JsEventManager
import com.artear.processor.process.model.JsEventManagerClass
import com.artear.processor.util.Utils
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.TypeElement


class JsEventManagerProcess(processingEnv: ProcessingEnvironment) : Process<JsEventManagerClass>(processingEnv) {

    override val annotation: Class<out Annotation> = JsEventManager::class.java

    override fun buildAnnotationClass(typeElement: TypeElement): JsEventManagerClass {
        val packageName = Utils.packageName(elements, typeElement)
        val className = typeElement.asType().toString().split(".").last()
        return JsEventManagerClass(packageName, className)
    }

    override fun createAnnotationFile(annotationClass: JsEventManagerClass) {

    }
}
package com.artear.processor.process

import com.artear.annotations.JsEventManager
import com.artear.processor.ArtearGenerator
import com.artear.processor.process.model.JsEventManagerClass
import com.artear.processor.util.KotlinFiler
import com.artear.processor.util.Utils
import com.squareup.kotlinpoet.FileSpec
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.TypeElement


class JsEventManagerProcess(processingEnv: ProcessingEnvironment,
                            private val jsInterfaceProcess: JsInterfaceProcess) :
        Process<JsEventManagerClass>(processingEnv) {

    override val annotation: Class<out Annotation> = JsEventManager::class.java

    override fun buildAnnotationClass(typeElement: TypeElement): JsEventManagerClass {
        val packageName = Utils.packageName(elements, typeElement)
        val className = typeElement.asType().toString().split(".").last()
        return JsEventManagerClass(packageName, className)
    }

    override fun createAnnotationFile(annotationClass: JsEventManagerClass) {
        val jsEventManagerFuncSpec = ArtearGenerator.generateJsEventManagerTypeSpec(
                annotationClass, jsInterfaceProcess.jsInterfaceClassList)
        var file = FileSpec.builder(annotationClass.packageName, "${annotationClass.className}Ext")
                .addFunction(jsEventManagerFuncSpec)
                .build()
        file.writeTo(KotlinFiler.getInstance(processingEnv).newFile())

        val webWrapperFuncSpec = ArtearGenerator.generateWebWrapperTypeSpec(annotationClass)
        file = FileSpec.builder("com.artear.webwrap", "WebWrapperExt")
                .addFunction(webWrapperFuncSpec)
                .build()
        file.writeTo(KotlinFiler.getInstance(processingEnv).newFile())
    }

}

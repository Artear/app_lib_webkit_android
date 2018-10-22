package com.artear.processor

import com.artear.annotations.JsEventManager
import com.artear.annotations.JsInterface
import com.artear.processor.process.JsEventManagerClass
import com.artear.processor.process.JsInterfaceClass
import com.artear.processor.util.KotlinFiler
import com.artear.processor.util.Utils.findAnnotationValue
import com.artear.processor.util.Utils.isValidClass
import com.artear.processor.util.Utils.packageName
import com.artear.processor.util.log
import com.squareup.kotlinpoet.FileSpec
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Elements
import javax.lang.model.util.Types


class ArtearProcessor : AbstractProcessor() {

    private lateinit var messager: Messager
    private lateinit var elements: Elements
    private lateinit var typeUtils: Types

    @Synchronized
    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        elements = processingEnv.elementUtils
        messager = processingEnv.messager
        typeUtils = processingEnv.typeUtils
    }

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

    override fun getSupportedOptions(): MutableSet<String> {
        return mutableSetOf(Config.KAPT_KOTLIN_GENERATED_OPTION_NAME)
    }

    override fun getSupportedAnnotationTypes(): Set<String> {
        return setOf(JsInterface::class.java.canonicalName,
                JsEventManager::class.java.canonicalName)
    }

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {

        KotlinFiler.getInstance(processingEnv)

        roundEnv.getElementsAnnotatedWith(JsInterface::class.java)
                .filterIsInstance<TypeElement>()
                .filter { isValidClass(it, messager) }
                .map { buildJsInterfaceClass(it) }
                .forEach { createJsInterfaceFile(it) }

        roundEnv.getElementsAnnotatedWith(JsEventManager::class.java)
                .filterIsInstance<TypeElement>()
                .filter { isValidClass(it, messager) }
                .map { buildJsEventInterfaceClass(it) }
                .forEach { createJsEventManagerFileExtension(it) }

        return true
    }

    private fun getPairType(it: TypeMirror): Pair<String, String> {
        val element = typeUtils.asElement(it) as TypeElement
        var typeInterface: String? = null
        if (it is DeclaredType) {
            typeInterface = it.typeArguments.first().toString()
        }
        checkNotNull(typeInterface) {
            "Can not be null the type interface"
        }
        messager.log("typeInterface $typeInterface")

        return Pair(element.qualifiedName.toString().substringAfterLast("."), typeInterface!!)
    }

    private fun buildJsInterfaceClass(typeElement: TypeElement): JsInterfaceClass {

        val packageName = packageName(elements, typeElement)
        val className = typeElement.asType().toString().split(".").last()
        val unsafeKey = findAnnotationValue(typeElement, JsInterface::class.qualifiedName,
                "key", String::class.java)
        val key = checkNotNull(unsafeKey) { "The key can not be null on JsInterface annotation" }
        val interfacePairType = typeElement.interfaces
                .map { return@map getPairType(it) }
                .first()

        return JsInterfaceClass(packageName, className, key, interfacePairType)
    }


    private fun createJsInterfaceFile(jsInterfaceClass: JsInterfaceClass) {
        val jsInterfaceTypeSpec = ArtearGenerator.generateJsInterfaceTypeSpec(jsInterfaceClass)
        val file = FileSpec.builder(jsInterfaceClass.packageName, jsInterfaceTypeSpec.name!!)
                .addType(jsInterfaceTypeSpec)
                .build()
        file.writeTo(KotlinFiler.getInstance(processingEnv).newFile())
    }


    private fun buildJsEventInterfaceClass(typeElement: TypeElement): JsEventManagerClass {
        val packageName = packageName(elements, typeElement)
        val className = typeElement.asType().toString().split(".").last()
        return JsEventManagerClass(packageName, className)
    }

    private fun createJsEventManagerFileExtension(jsEventManagerClass: JsEventManagerClass) {

    }

}

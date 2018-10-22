package com.artear.processor

import com.artear.annotations.JsInterface
import com.artear.processor.Utils.findAnnotationValue
import com.artear.processor.Utils.packageName
import com.squareup.kotlinpoet.FileSpec
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.tools.Diagnostic.Kind.ERROR


class ArtearProcessor : AbstractProcessor() {

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

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
        return mutableSetOf(ArtearProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME)
    }

    override fun getSupportedAnnotationTypes(): Set<String> {
        return setOf(JsInterface::class.java.canonicalName)
    }

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {

        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
                ?: run {
                    processingEnv.messager.printMessage(ERROR, "Can't find the target directory for generated Kotlin files.")
                    return false
                }


        roundEnv.getElementsAnnotatedWith(JsInterface::class.java)
                .filterIsInstance<TypeElement>()
                .filter { isValidClass(it) }
                .map { buildAnnotatedClass(it) }
                .forEach { createJsInterfaceFile(it, kaptKotlinGeneratedDir) }

        return true
    }

    private fun createJsInterfaceFile(jsInterfaceClass: JSInterfaceClass, pathname: String) {
        val jsInterfaceTypeSpec = ArtearGenerator.generateJsInterfaceTypeSpec(jsInterfaceClass)
        val file = FileSpec.builder(jsInterfaceClass.packageName, jsInterfaceTypeSpec.name!!)
                .addType(jsInterfaceTypeSpec)
                .build()
        file.writeTo(File(pathname))
    }


    private fun isValidClass(typeElement: TypeElement): Boolean {
        if (typeElement.kind != ElementKind.CLASS) {
            messager.printMessage(ERROR, "Can only be applied to classes,  element: $typeElement ")
            return false
        }
        return true
    }

    private fun buildAnnotatedClass(typeElement: TypeElement): JSInterfaceClass {

        val packageName = packageName(elements, typeElement)
        val className = typeElement.asType().toString().split(".").last()
        val unsafeKey = findAnnotationValue(typeElement, JsInterface::class.qualifiedName,
                "key", String::class.java)
        val key = checkNotNull(unsafeKey) { "The key can not be null on JsInterface annotation" }
        val interfacePairType = typeElement.interfaces
                .map { return@map getPairType(it) }
                .first()

        return JSInterfaceClass(packageName, className, key, interfacePairType)
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


}

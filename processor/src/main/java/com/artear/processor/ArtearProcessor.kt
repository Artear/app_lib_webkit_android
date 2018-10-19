package com.artear.processor

import com.artear.annotations.JsInterface
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.Element
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
                .forEach {
                    //Create file and export to a function

                    val typeSpec = ArtearGenerator.generateClass(messager, it)

                    val typeSpec2 = TypeSpec.classBuilder("Greeter")
                            .primaryConstructor(FunSpec.constructorBuilder()
                                    .addParameter("name", String::class)
                                    .build())
                            .addProperty(PropertySpec.builder("name", String::class)
                                    .initializer("name")
                                    .build())
                            .addFunction(FunSpec.builder("greet")
                                    .addStatement("println(%S)", "Hello, \$name")
                                    .build())
                            .build()

                    val file = FileSpec.builder(it.packageName, typeSpec.name!!)
                            .addType(typeSpec)
                            .build()

                    file.writeTo(File(kaptKotlinGeneratedDir))
                }

        return true
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

    fun findEnclosingTypeElement(e: Element?): TypeElement? {
        var e = e

        while (e != null && e !is TypeElement) {

            e = e.enclosingElement

        }

        return TypeElement::class.java.cast(e)

    }

    private fun <T> findAnnotationValue(element: Element, annotationClass: String?,
                                        valueName: String, expectedType: Class<T>): T? {
        var ret: T? = null
        for (annotationMirror in element.annotationMirrors) {
            val annotationType = annotationMirror.annotationType
            val annotationElement = annotationType.asElement() as TypeElement
            if (annotationElement.qualifiedName.contentEquals(annotationClass)) {
                ret = extractValue(annotationMirror, valueName, expectedType)
                break
            }
        }
        return ret
    }

    private fun <T> extractValue(annotationMirror: AnnotationMirror,
                                 valueName: String, expectedType: Class<T>): T? {
        val elementValues = HashMap(annotationMirror.elementValues)
        for ((key, value1) in elementValues) {
            if (key.simpleName.contentEquals(valueName)) {
                val value = value1.value
                return expectedType.cast(value)
            }
        }
        return null
    }

//    @Throws(IOException::class)
//    private fun generateJSInterfaceClass(jsInterfacesClasses: List<JSInterfaceClass>) {
//        if (jsInterfacesClasses.isEmpty()) {
//            return
//        }
//
//        for (easyJSONClass in jsInterfacesClasses) {
//            val packageName = Utils.getPackageName(elements, easyJSONClass.typeElement)
//            val generatedClass = ArtearGenerator.generateClass(easyJSONClass)
//
//            val fileSpec = FileSpec.get(packageName, generatedClass)
//            fileSpec.writeTo(System.out)
//        }
//    }

    private fun packageName(elementUtils: Elements, typeElement: Element): String {
        val pkg = elementUtils.getPackageOf(typeElement)
        if (pkg.isUnnamed) {
            throw GeneratorClassExceptions("The package of ${typeElement.simpleName} class has no name")
        }
        return pkg.qualifiedName.toString()
    }
}

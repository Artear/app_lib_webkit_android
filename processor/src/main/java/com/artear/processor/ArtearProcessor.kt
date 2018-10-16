package com.artear.processor

import com.artear.annotations.JsInterface
import com.squareup.kotlinpoet.*
import java.io.File
import java.io.IOException
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.tools.Diagnostic.Kind.ERROR

//@SupportedSourceVersion(SourceVersion.RELEASE_8)
//@SupportedAnnotationTypes("com.artear.annotations.JSInterface")
//@SupportedOptions(ArtearProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME)
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

                    val greeterClass = ClassName("test.generate.artear", "Greeter")
                    val file = FileSpec.builder("test.generate.artear", "HelloWorld")
                            .addType(TypeSpec.classBuilder("Greeter")
                                    .primaryConstructor(FunSpec.constructorBuilder()
                                            .addParameter("name", String::class)
                                            .build())
                                    .addProperty(PropertySpec.builder("name", String::class)
                                            .initializer("name")
                                            .build())
                                    .addFunction(FunSpec.builder("greet")
                                            .addStatement("println(%S)", "Hello, \$name")
                                            .build())
                                    .build())
                            .addFunction(FunSpec.builder("main")
                                    .addParameter("args", String::class, KModifier.VARARG)
                                    .addStatement("%T(args[0]).greet()", greeterClass)
                                    .build())
                            .build()

                    file.writeTo(File(kaptKotlinGeneratedDir))
                }

        return true
    }


//    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
//
//        val typeElements = roundEnv.getElementsAnnotatedWith(JsInterface::class.java).asSequence()
//                .filterIsInstance<TypeElement>()
//                .filter { isValidClass(it) }
//                .map { buildAnnotatedClass(it) }
//                .toList()
//
//        val generatedSourcesRoot: String = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME].orEmpty()
//        if (generatedSourcesRoot.isEmpty()) {
//            processingEnv.messager.printMessage(Diagnostic.Kind.ERROR,
//                    "Can't find the target directory for generated Kotlin files.")
//            return false
//        }
//
//        generateJSInterfaceClass(typeElements)
//        return true
//    }


    private fun isValidClass(typeElement: TypeElement): Boolean {
        if (typeElement.kind != ElementKind.CLASS) {
            messager.printMessage(ERROR, "Can only be applied to classes,  element: $typeElement ")
            return false
        }
        return true
    }

    private fun buildAnnotatedClass(typeElement: TypeElement): JSInterfaceClass {

        val variableNames = typeElement.enclosedElements
                .filterIsInstance<VariableElement>()
                .map { it.simpleName.toString() }
                .toList()

        val packageName = packageName(elements, typeElement)
//        val generatedClass = ArtearGenerator.generateClass(it)

        return JSInterfaceClass(typeElement, variableNames)
    }

    @Throws(IOException::class)
    private fun generateJSInterfaceClass(jsInterfacesClasses: List<JSInterfaceClass>) {
        if (jsInterfacesClasses.isEmpty()) {
            return
        }

        for (easyJSONClass in jsInterfacesClasses) {
            val packageName = Utils.getPackageName(elements, easyJSONClass.typeElement)
            val generatedClass = ArtearGenerator.generateClass(easyJSONClass)

            val fileSpec = FileSpec.get(packageName, generatedClass)
            fileSpec.writeTo(System.out)
        }
    }

    private fun packageName(elementUtils: Elements, typeElement: Element): String {
        val pkg = elementUtils.getPackageOf(typeElement)
        if (pkg.isUnnamed) {
            throw GeneratorClassExceptions("The package of ${typeElement.simpleName} class has no name")
        }
        return pkg.qualifiedName.toString()
    }
}

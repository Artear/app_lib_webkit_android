package com.artear.processor

import com.artear.processor.process.model.JsEventManagerClass
import com.artear.processor.process.model.JsInterfaceClass
import com.squareup.kotlinpoet.*


internal object ArtearGenerator {

    private const val CLASS_NAME_JS_SUFFIX = "Js"

    fun generateJsInterfaceTypeSpec(jsInterfaceClass: JsInterfaceClass): TypeSpec {

        val builder = TypeSpec.classBuilder(jsInterfaceClass.className + CLASS_NAME_JS_SUFFIX)
                .addModifiers(KModifier.PUBLIC, KModifier.FINAL)

        val upPackageName = jsInterfaceClass.packageName.substringBeforeLast(".")
        val commandClassName = ClassName(upPackageName, "CommandJs")

        builder.addSuperinterface(commandClassName)

        val contextClassName = ClassName("android.content", "Context").asNullable()
        val contextNameParam = "context"
        val contextParam = ParameterSpec.builder(contextNameParam, contextClassName,
                KModifier.OVERRIDE).build()
        val contextProperty = PropertySpec.builder(contextNameParam, contextClassName)
                .initializer(contextNameParam)
                .mutable(true)
                .build()

        val targetClassName = ClassName(jsInterfaceClass.packageName, jsInterfaceClass.className)
        val targetNameParam = targetClassName.simpleName.toLowerCase()
        val targetParam = ParameterSpec.builder(targetNameParam,
                targetClassName, KModifier.PRIVATE)
                .build()
        val targetProperty = PropertySpec.builder(targetNameParam, targetClassName)
                .initializer(targetNameParam)
                .build()

        val delegateClassName = ClassName(upPackageName, "WebJsDispatcher").asNullable()
        val delegateNameParam = "delegate"
        val delegateParam = ParameterSpec.builder(delegateNameParam, delegateClassName,
                KModifier.OVERRIDE)
                .build()
        val delegateProperty = PropertySpec.builder(delegateNameParam, delegateClassName)
                .initializer(delegateNameParam)
                .mutable(true)
                .build()

        val keyProperty = PropertySpec.builder("key", String::class)
                .addModifiers(KModifier.OVERRIDE)
                .initializer("%S", jsInterfaceClass.key)
                .build()

        builder.primaryConstructor(FunSpec.constructorBuilder()
                .addParameter(contextParam)
                .addParameter(targetParam)
                .addParameter(delegateParam)
                .build())
                .addProperty(contextProperty)
                .addProperty(targetProperty)
                .addProperty(delegateProperty)
                .addProperty(keyProperty)

        val indexNameParam = "index"
        val indexParam = ParameterSpec.builder(indexNameParam, Int::class).build()
        val dataJsonParam = ParameterSpec.builder("dataJson", String::class).build()

        val javaScriptClassName = ClassName("android.webkit", "JavascriptInterface")
        val suppressClassName = ClassName("android.annotation", "SuppressLint")
        val suppressAnnotation = AnnotationSpec.builder(suppressClassName)
                .addMember("%S", "CheckResult")
                .build()

        val nameInterfaceGenericType = jsInterfaceClass.interfaceType.second.substringAfterLast(".")

        val classNameJsonAdapter = ClassName("$upPackageName.data", "${nameInterfaceGenericType}JsonAdapter")
        val classNameMoshi = ClassName("com.squareup.moshi", "Moshi")

        val classNameDispatch = ClassName(upPackageName, "dispatch")
        val classNameError = ClassName(upPackageName, "error")


        val codeBuilder = CodeBlock.builder()
                .beginControlFlow("try")
                .addStatement("val adapter = %T(%T.Builder().build())", classNameJsonAdapter,
                        classNameMoshi)
                .addStatement("val data = adapter.fromJson(dataJson)")

        when (jsInterfaceClass.interfaceType.first) {
            "SyncEventJs" -> {
                codeBuilder.addStatement("val event = $targetNameParam.event(" +
                        "$contextNameParam!!,$indexNameParam, data!!)")
                codeBuilder.addStatement("$delegateNameParam!!.%T(event)", classNameDispatch)
            }
            "DeferEventJs" -> {
                codeBuilder.addStatement("$targetNameParam.event(" +
                        "$contextNameParam!!, $delegateNameParam!!, $indexNameParam, data!!)")
            }
        }

        val executeCode = codeBuilder.nextControlFlow("catch (ex: %T)", Exception::class)
                .addStatement("$delegateNameParam?.%T($indexNameParam, ex.message)", classNameError)
                .endControlFlow()
                .build()

        val executeFunction = FunSpec.builder("execute")
                .addAnnotation(suppressAnnotation)
                .addAnnotation(javaScriptClassName)
                .addModifiers(KModifier.OVERRIDE)
                .addParameter(indexParam)
                .addParameter(dataJsonParam)
                .addCode(executeCode)
                .build()

        builder.addFunction(executeFunction)

        return builder.build()
    }

    fun generateJsEventManagerTypeSpec(annotationClass: JsEventManagerClass) : FunSpec{

        val className = ClassName(annotationClass.packageName, annotationClass.className)
        val builder = FunSpec.builder("initialize")
                .receiver(className)
                .addModifiers(KModifier.PUBLIC)
                .addStatement("")

        return builder.build()
    }

}
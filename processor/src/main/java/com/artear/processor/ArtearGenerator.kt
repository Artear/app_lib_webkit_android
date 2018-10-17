package com.artear.processor

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import javax.annotation.processing.Messager
import javax.tools.Diagnostic


internal object ArtearGenerator {

    private const val CLASS_NAME_JS_SUFFIX = "Js"

    fun generateClass(messager : Messager, jsInterfaceClass: JSInterfaceClass): TypeSpec {

        val builder = TypeSpec.classBuilder(jsInterfaceClass.className + CLASS_NAME_JS_SUFFIX)
                .addModifiers(KModifier.PUBLIC, KModifier.FINAL)

        val className = ClassName(jsInterfaceClass.packageName.substringBeforeLast("."), "CommandJs")

        messager.printMessage(Diagnostic.Kind.WARNING, "ArtearGenerator - " +
                "jsInterfaceClass key = ${jsInterfaceClass.key} ")

        builder.addSuperinterface(className)


        val contextClassName = ClassName("android.content", "Context")

        builder.primaryConstructor(FunSpec.constructorBuilder()
                //TODO var!!!!
                        .addParameter("context", contextClassName, KModifier.OVERRIDE)
                        .build())
        jsInterfaceClass.variableNames.forEach {

//            builder.addFunction(generateAsJSONMethod(it, jsInterfaceClass))

        }

        return builder.build()
    }

    /**
     * @return a asJSON() method for an easyJSONClass.
     */
    private fun generateAsJSONMethod(variableName : String, jsInterfaceClass: JSInterfaceClass): FunSpec {

//        val paramName = jsInterfaceClass.type.toString().split(".").last().toLowerCase()

        val methodBuilder = FunSpec.builder(variableName)
                .addModifiers(KModifier.PUBLIC)
                .addParameter("index", Int::class)
//        if (easyJSONClass.variableNames.isNotEmpty()) {
//            methodBuilder.addCode("try {\n")
//        }
//
//        for (variableName in easyJSONClass.variableNames) {
//            methodBuilder.addStatement(String.format("  jsonObject.put(\"%s\", $paramName.%s)",
//                    variableName, variableName))
//
//        }
//        if (easyJSONClass.variableNames.isNotEmpty()) {
//            methodBuilder.addCode("} catch (\$T e) {\n" +
//                    "   e.printStackTrace();\n" +
//                    "}\n", JSONException)
//        }
//
//        methodBuilder.addStatement("return jsonObject")

        return methodBuilder.build()
    }
}
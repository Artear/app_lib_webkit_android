package com.artear.processor

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec


internal object CodeGenerator {

    private const val CLASS_NAME_PREFIX = "ArtearJS"

    fun generateClass(jsInterfaceClass: JSInterfaceClass): TypeSpec {

        val className = jsInterfaceClass.type.toString().split(".").last()

        val builder = TypeSpec.classBuilder(CLASS_NAME_PREFIX + className)
                .addModifiers(KModifier.PUBLIC, KModifier.FINAL)

        jsInterfaceClass.variableNames.forEach {

            builder.addFunction(generateAsJSONMethod(it, jsInterfaceClass))

        }

        return builder.build()
    }

    /**
     * @return a asJSON() method for an easyJSONClass.
     */
    private fun generateAsJSONMethod(variableName : String, jsInterfaceClass: JSInterfaceClass): FunSpec {

        val paramName = jsInterfaceClass.type.toString().split(".").last().toLowerCase()

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
package com.artear.processor.util

import com.artear.processor.Config
import java.io.File
import javax.annotation.processing.ProcessingEnvironment
import javax.tools.Diagnostic


class KotlinFiler(processingEnv: ProcessingEnvironment) {

    companion object : SingletonHolder<KotlinFiler, ProcessingEnvironment>(::KotlinFiler)

        private val kaptKotlinGeneratedDir: String =
            processingEnv.options[Config.KAPT_KOTLIN_GENERATED_OPTION_NAME] ?: run {
                processingEnv.messager.printMessage(Diagnostic.Kind.ERROR,
                        "Can't find the target directory for generated Kotlin files.")
                throw KotlinFilerException("Can't generate Kotlin files.")
            }

    fun newFile(): File {
        return File(kaptKotlinGeneratedDir)
    }

}
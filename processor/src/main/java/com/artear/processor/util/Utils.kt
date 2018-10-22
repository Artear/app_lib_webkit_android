package com.artear.processor.util

import javax.annotation.processing.Messager
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.tools.Diagnostic


internal object Utils {

    fun <T> findAnnotationValue(element: Element, annotationClass: String?,
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

    fun <T> extractValue(annotationMirror: AnnotationMirror,
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

    fun packageName(elementUtils: Elements, typeElement: Element): String {
        val pkg = elementUtils.getPackageOf(typeElement)
        if (pkg.isUnnamed) {
            throw GeneratorClassException("The package of ${typeElement.simpleName} class has no name")
        }
        return pkg.qualifiedName.toString()
    }

    fun isValidClass(typeElement: TypeElement, messager: Messager): Boolean {
        if (typeElement.kind != ElementKind.CLASS) {
            messager.printMessage(Diagnostic.Kind.ERROR, "Can only be applied to classes,  element: $typeElement ")
            return false
        }
        return true
    }


}
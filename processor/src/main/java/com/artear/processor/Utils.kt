package com.artear.processor

import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements

internal object Utils {

    fun getPackageName(elementUtils: Elements, typeElement: TypeElement): String {
        val pkg = elementUtils.getPackageOf(typeElement)
        if (pkg.isUnnamed) {
            throw IllegalStateException(typeElement.simpleName.toString())
        }
        return pkg.qualifiedName.toString()
    }
}
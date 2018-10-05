package com.artear.processor

import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror


internal class JSInterfaceClass(val typeElement: TypeElement, val variableNames: List<String>) {
    val type: TypeMirror
        get() = typeElement.asType()
}
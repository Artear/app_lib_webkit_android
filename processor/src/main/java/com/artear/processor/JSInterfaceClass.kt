package com.artear.processor

/**
 * @param packageName The original package name of the class that have [com.artear.annotations.JsInterface]
 * @param className The name of the class
 * @param key The key word of annotation
 * @param interfaceType Its a pair, first is the type interface, second is the
 * generic type of that interface type. For example: List<Int> -> first is List, and second
 * is Int
 */
internal class JSInterfaceClass(val packageName: String, val className: String, val key: String,
                                val interfaceType: Pair<String, String>)
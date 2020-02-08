package org.m0skit0.koin.generation

import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement

internal data class KoinModuleFunction(
    val name: String,
    val `class`: String
)

internal fun Element.toKoinModuleFunction(): KoinModuleFunction = KoinModuleFunction(
    simpleName.toString(),
    (enclosingElement as TypeElement).qualifiedName.toString()
)

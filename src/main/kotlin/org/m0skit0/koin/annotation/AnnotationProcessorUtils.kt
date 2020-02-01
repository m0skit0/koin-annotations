package org.m0skit0.koin.annotation

import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement

internal fun Set<Element>.forEachExecutableElement(block: (ExecutableElement) -> Unit) {
    forEach { element ->
        block(element as ExecutableElement)
    }
}
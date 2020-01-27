package org.m0skit0.koin.annotations

import com.google.auto.service.AutoService
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

private const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions(KAPT_KOTLIN_GENERATED_OPTION_NAME)
internal class KoinModuleProcessor : AbstractProcessor() {

    override fun process(set: MutableSet<out TypeElement>, roundEnvironment: RoundEnvironment): Boolean {
        roundEnvironment.getElementsAnnotatedWith(KoinModule::class.java).forEach { element ->
            processingEnv.messager.printMessage(Diagnostic.Kind.NOTE, "Element: $element")
        }
        return false
    }
}
package org.m0skit0.koin.annotation

import com.google.auto.service.AutoService
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

private const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions(KAPT_KOTLIN_GENERATED_OPTION_NAME)
@SupportedAnnotationTypes("org.m0skit0.koin.annotation.KoinModule")
internal class KoinModuleAnnotationProcessor : AbstractProcessor() {

    override fun process(set: MutableSet<out TypeElement>, roundEnvironment: RoundEnvironment): Boolean {

        if (set.isEmpty()) return false

        // TODO Check if annotated functions has no parameters and returns org.koin.core.module.Module
        // TODO Check function access is at least internal
        // TODO Process top-level functions correctly
        // TODO Process functions in instance classes correctly

        roundEnvironment.annotationsToKoinModuleFunctions().let { koinModuleFunctions ->
            KoinModuleHelperGenerator(processingEnv.filer, koinModuleFunctions).generate()
        }

        return true
    }

    private fun RoundEnvironment.annotationsToKoinModuleFunctions(): Iterable<KoinModuleFunction> =
        getElementsAnnotatedWith(KoinModule::class.java).map { element ->
            KoinModuleFunction(
                element.simpleName.toString(),
                (element.enclosingElement as TypeElement).qualifiedName.toString()
            )
        }
}

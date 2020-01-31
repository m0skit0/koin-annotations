package org.m0skit0.koin.annotations

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.asClassName
import org.koin.core.module.Module
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

private const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions(KAPT_KOTLIN_GENERATED_OPTION_NAME)
@SupportedAnnotationTypes("org.m0skit0.koin.annotations.KoinModule")
internal class KoinModuleProcessor : AbstractProcessor() {

    override fun process(set: MutableSet<out TypeElement>, roundEnvironment: RoundEnvironment): Boolean {
        processingEnv.messager.printMessage(Diagnostic.Kind.WARNING, "KoinModuleProcessor >> Process")

        // TODO Check if annotated function has no parameters and returns org.koin.core.module.Module
        // TODO Check function access is at least internal
        // TODO Process top-level functions correctly
        roundEnvironment.getElementsAnnotatedWith(KoinModule::class.java).associate { element ->
            "${element.simpleName}" to "${(element.enclosingElement as TypeElement).qualifiedName}"
        }.run {
            if (isNotEmpty()) {
                val initializeKoinModulesFunSpec = initializeKoinModulesFunSpec(this)
                FileSpec.builder("org.m0skit0.koin.annotations", "KoinAnnotations")
                    .addImport("org.koin.core.context", "loadKoinModules")
                    .addImport("org.koin.dsl", "module")
                    .addFunction(initializeKoinModulesFunSpec)
                    .build()
                    .writeTo(processingEnv.filer)
            }
        }
        return true
    }

    private fun initializeKoinModulesFunSpec(modules: Map<String, String>): FunSpec {
        val modulesList = modules.map { (key, value) -> "$value.$key()"}.joinToString(", ")
        return FunSpec.builder("listOfKoinModules")
            .addModifiers(KModifier.INTERNAL)
            .returns(List::class.asClassName().parameterizedBy(Module::class.asClassName()))
            .addCode("""
                |return listOf(
                |   $modulesList
                |)
                """.trimMargin())
            .build()
    }
}
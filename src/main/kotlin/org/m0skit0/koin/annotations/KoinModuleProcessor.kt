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

private const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions(KAPT_KOTLIN_GENERATED_OPTION_NAME)
@SupportedAnnotationTypes("org.m0skit0.koin.annotations.KoinModule")
internal class KoinModuleProcessor : AbstractProcessor() {

    override fun process(set: MutableSet<out TypeElement>, roundEnvironment: RoundEnvironment): Boolean {

        if (set.isEmpty()) return false

        // TODO Check if annotated function has no parameters and returns org.koin.core.module.Module
        // TODO Check function access is at least internal
        // TODO Process top-level functions correctly

        roundEnvironment.annotatedFunctions().let { annotatedFunctions ->
            annotatedFunctions.toListOfKoinModulesFunSpec().let { listOfKoinModulesFunSpec ->
                listOfKoinModulesFileSpec(listOfKoinModulesFunSpec)
                    .writeTo(processingEnv.filer)
            }
        }

        return true
    }

    private fun Map<String, String>.toListOfKoinModulesFunSpec(): FunSpec =
        FunSpec.builder("listOfKoinModules")
            .addModifiers(KModifier.INTERNAL)
            .returns(List::class.asClassName().parameterizedBy(Module::class.asClassName()))
            .addCode("""
                |return listOf(
                |   ${map { it.toFunctionQualifiedName() }.joinToString(", ")}
                |)
                """.trimMargin())
            .build()

    private fun RoundEnvironment.annotatedFunctions(): Map<String, String> =
        getElementsAnnotatedWith(KoinModule::class.java).associate { element ->
            "${element.simpleName}" to "${(element.enclosingElement as TypeElement).qualifiedName}"
        }

    private fun Map.Entry<String, String>.toFunctionQualifiedName(): String = "$value.$key()"

    private fun listOfKoinModulesFileSpec(koinModules: FunSpec): FileSpec =
        FileSpec.builder("org.m0skit0.koin.annotations", "KoinAnnotations")
            .addImport("org.koin.core.context", "loadKoinModules")
            .addImport("org.koin.dsl", "module")
            .addFunction(koinModules)
            .build()
}
package org.m0skit0.koin.generation

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.asClassName
import org.koin.core.module.Module
import javax.annotation.processing.Filer

internal class KoinModuleHelperGeneratorImpl(
    private val filer: Filer,
    private val koinModuleFunctions: Iterable<KoinModuleFunction>
) : KoinModuleHelperGenerator {

    override fun generate() {
        koinModuleFunctions.toListOfKoinModulesFunSpec().let { funSpec ->
            listOfKoinModulesToFileSpec(funSpec).writeTo(filer)
        }
    }

    private fun Iterable<KoinModuleFunction>.toListOfKoinModulesFunSpec(): FunSpec =
        FunSpec.builder("listOfKoinModules")
            .addModifiers(KModifier.INTERNAL)
            .returns(List::class.asClassName().parameterizedBy(Module::class.asClassName()))
            .addCode("""
                |return listOf(
                |    ${joinToString(", ") { it.toFunctionQualifiedName() }}
                |)
                """.trimMargin())
            .build()

    private fun KoinModuleFunction.toFunctionQualifiedName(): String = "$`class`.$name()"

    private fun listOfKoinModulesToFileSpec(koinModules: FunSpec): FileSpec =
        FileSpec.builder("org.m0skit0.koin.annotation", "KoinModuleHelper")
            .addFunction(koinModules)
            .build()
}

package org.m0skit0.koin.annotation

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.asTypeName
import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.module.Module
import org.m0skit0.koin.di.NAMED_KOIN_MODULE_HELPER_GENERATOR
import org.m0skit0.koin.di.initializeKoin
import org.m0skit0.koin.generation.KoinModuleFunction
import org.m0skit0.koin.generation.KoinModuleHelperGenerator
import org.m0skit0.koin.generation.toKoinModuleFunction
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

private const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions(KAPT_KOTLIN_GENERATED_OPTION_NAME)
@SupportedAnnotationTypes("org.m0skit0.koin.annotation.KoinModule")
internal class KoinModuleAnnotationProcessor : AbstractProcessor(), KoinComponent {

    override fun init(processingEnvironment: ProcessingEnvironment) {
        super.init(processingEnvironment)
        initializeKoin()
    }

    override fun process(set: MutableSet<out TypeElement>, roundEnvironment: RoundEnvironment): Boolean {

        if (set.isEmpty()) return false

        roundEnvironment.getElementsAnnotatedWith(KoinModule::class.java).run {

            checkElementKind()

            checkFunction()

            // TODO Check function access is at least internal
            // TODO Process top-level functions correctly
            // TODO Process functions in instance classes correctly

            map { it.toKoinModuleFunction() }.let { koinModuleFunctions ->
                get<(Filer, Iterable<KoinModuleFunction>) -> KoinModuleHelperGenerator>(NAMED_KOIN_MODULE_HELPER_GENERATOR)
                    .invoke(processingEnv.filer, koinModuleFunctions).generate()
            }
        }

        return true
    }

    private fun Set<Element>.checkElementKind() {
        forEach { element ->
            if (element.kind != ElementKind.METHOD) {
                error("Annotated element must be a function", element)
            }
        }
    }

    private fun Set<Element>.checkFunction() {
        checkFunctionVisibility()
        checkFunctionParameters()
        checkFunctionReturnType()
    }

    private fun Set<Element>.checkFunctionParameters() {
        forEachExecutableElement { element ->
            if (element.parameters.isNotEmpty()) {
                error("Function cannot have parameters", element)
            }
        }
    }

    private fun Set<Element>.checkFunctionReturnType() {
        forEachExecutableElement { element ->
            val koinModuleTypeName = Module::class.asTypeName()
            if (element.returnType.asTypeName() != koinModuleTypeName) {
                error("Function must return a Koin Module", element)
            }
        }
    }

    private fun Set<Element>.checkFunctionVisibility() {
        forEachExecutableElement { element ->
            if (!element.modifiers.contains(Modifier.PUBLIC)) {
                error("Function must be public", element)
            }
        }
    }

    private fun error(message: String, element: Element) {
        processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, message, element)
    }
}

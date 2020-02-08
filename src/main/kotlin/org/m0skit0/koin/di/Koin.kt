package org.m0skit0.koin.di

import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.m0skit0.koin.generation.KoinModuleFunction
import org.m0skit0.koin.generation.KoinModuleHelperGenerator
import org.m0skit0.koin.generation.KoinModuleHelperGeneratorImpl
import javax.annotation.processing.Filer

internal val NAMED_KOIN_MODULE_HELPER_GENERATOR = named("NAMED_KOIN_MODULE_HELPER_GENERATOR")

private val module = module {
    single<(Filer, Iterable<KoinModuleFunction>) -> KoinModuleHelperGenerator>(NAMED_KOIN_MODULE_HELPER_GENERATOR) {
        { filer, koinModuleFunctions -> KoinModuleHelperGeneratorImpl(filer, koinModuleFunctions) }
    }
}

internal fun initializeKoin() {
    startKoin {
        modules(module)
    }
}
package org.m0skit0.koin.di

import org.m0skit0.koin.generation.KoinModuleFunction
import org.m0skit0.koin.generation.KoinModuleHelperGenerator
import org.m0skit0.koin.generation.KoinModuleHelperGeneratorImpl
import javax.annotation.processing.Filer

internal fun getKoinModuleHelperGenerator(filer: Filer, koinModuleFunctions: Iterable<KoinModuleFunction>): KoinModuleHelperGenerator =
    KoinModuleHelperGeneratorImpl(filer, koinModuleFunctions)

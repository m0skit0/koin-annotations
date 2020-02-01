package org.m0skit0.koin.provider

import org.koin.core.module.Module

interface KoinModuleProvider {
    fun module(): Module
}

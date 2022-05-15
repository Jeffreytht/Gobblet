package com.jeffreytht.gobblet.util

import kotlin.reflect.KClass

interface DependencyProvider {
    fun <T: Any> extractDependency(cls: KClass<T>): T?
}
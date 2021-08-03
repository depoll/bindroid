package com.bindroid.utils

import java.lang.ref.WeakReference
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0

class CompiledProperty<T>(prop: () -> KProperty0<T>, cls: Class<T>) : Property<T>() {
    companion object {
        inline operator fun <reified T> invoke(noinline prop: () -> KProperty0<T>): CompiledProperty<T> {
            return CompiledProperty(prop, T::class.java)
        }
    }

    init {
        this.getter = Function {
            try {
                val resolved = prop()
                resolved.get()
            } catch (e: NullPointerException) {
                null
            }
        }
        this.setter = Action {
            val kprop = prop()
            if (kprop is KMutableProperty0<T>) {
                kprop.set(it)
            } else {
                throw UnsupportedOperationException("Property ${kprop?.name} has no setter")
            }
        }
        this.propertyType = cls
    }
}

inline infix fun <T, TResult> T.weakBind(crossinline operation: T.() -> TResult): () -> TResult {
    val weakThis = WeakReference(this)
    return { weakThis.get()!!.operation() }
}

inline infix fun <T, reified TResult> T.weakProp(crossinline operation: T.() -> KProperty0<TResult>):
        CompiledProperty<TResult> {
    return CompiledProperty(this weakBind operation)
}

inline fun <reified T> compiledProp(noinline prop: () -> KProperty0<T>): CompiledProperty<T> {
    return CompiledProperty(prop)
}
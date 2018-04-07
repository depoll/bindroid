package com.bindroid.utils

import java.lang.ref.WeakReference
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty

class CompiledProperty<T>(prop: () -> KProperty<T>, cls: Class<T>) : Property<T>() {
    companion object {
        inline operator fun <reified T> invoke(noinline prop: () -> KProperty<T>): CompiledProperty<T> {
            return CompiledProperty(prop, T::class.java)
        }
    }

    init {
        this.getter = Function { prop().getter.call() }
        this.setter = Action {
            val kprop = prop()
            if (kprop is KMutableProperty<*>) {
                kprop.setter.call(it)
            } else {
                throw UnsupportedOperationException("Property ${kprop.name} has no setter")
            }
        }
        this.propertyType = cls
    }
}

infix fun <T, TResult> T.weakBind(operation: T.() -> TResult): () -> TResult {
    val weakThis = WeakReference(this)
    return { weakThis.get()!!.operation() }
}
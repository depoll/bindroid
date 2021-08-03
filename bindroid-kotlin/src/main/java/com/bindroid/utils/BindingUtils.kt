package com.bindroid.utils

import android.app.Activity
import android.view.View
import com.bindroid.Binding
import com.bindroid.BindingMode
import com.bindroid.ValueConverter
import com.bindroid.converters.AdapterConverter
import com.bindroid.ui.UiBinder
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0

/**
 * Creates a binding between any two properties specified in any form.
 */
fun bind(
    targetProperty: Property<*>,
    sourceProperty: Property<*>,
    mode: BindingMode = BindingMode.ONE_WAY,
    valueConverter: ValueConverter = ValueConverter.getDefaultConverter()
): Binding {
    return Binding(targetProperty, sourceProperty, mode, valueConverter)
}

/**
 * Creates a one-way binding of the target property to an observable lambda.
 */
inline infix fun <reified TProp> (() -> KMutableProperty0<TProp>).bind(
    crossinline source: () -> TProp
): Binding {
    return bind(CompiledProperty(this), Property<TProp>({ source() }, null), BindingMode.ONE_WAY)
}

/**
 * Creates a binding between two properties, each specified as lambdas like
 * {@code { (parent.child::childProperty) }}
 */
inline fun <reified TResult1, reified TResult2> bind(
    noinline target: () -> KProperty0<TResult1>,
    noinline source: () -> KProperty0<TResult2>,
    mode: BindingMode = BindingMode.ONE_WAY,
    valueConverter: ValueConverter = ValueConverter.getDefaultConverter()
): Binding {
    return Binding(CompiledProperty(target), CompiledProperty(source), mode, valueConverter)
}

/**
 * Creates a ValueConverter using lambdas for each conversion.
 */
fun converter(
    toSource: ((targetValue: Any?, sourceType: Class<*>?) -> Any?)? = null,
    toTarget: ((sourceValue: Any?, targetType: Class<*>?) -> Any?)? = null
)
        : ValueConverter {
    return object : ValueConverter() {
        override fun convertToSource(targetValue: Any?, sourceType: Class<*>?): Any? {
            return toSource?.invoke(targetValue, sourceType)
        }

        override fun convertToTarget(sourceValue: Any?, targetType: Class<*>?): Any? {
            return toTarget?.invoke(sourceValue, targetType)
        }
    }
}

fun View.uibind(
    targetId: Int,
    targetProperty: String?,
    sourceProperty: Property<*>,
    mode: BindingMode = BindingMode.ONE_WAY,
    converter: ValueConverter = ValueConverter.getDefaultConverter()
): Binding {
    return UiBinder.bind(
        WeakReflectedProperty(this.findViewById(targetId), targetProperty),
        sourceProperty, mode, converter
    )
}

inline fun <reified T> View.uibind(
    targetId: Int,
    targetProperty: String?,
    noinline sourceProperty: () -> KProperty0<T>,
    mode: BindingMode = BindingMode.ONE_WAY,
    converter: ValueConverter = ValueConverter.getDefaultConverter()
): Binding {
    return UiBinder.bind(
        WeakReflectedProperty(this.findViewById(targetId), targetProperty),
        CompiledProperty(sourceProperty), mode, converter
    )
}

inline fun <reified T> View.bindTo(
    targetId: Int,
    targetProperty: String?,
    noinline sourceGetter: () -> T,
    converter: ValueConverter = ValueConverter.getDefaultConverter()
): Binding {
    return UiBinder.bind(
        WeakReflectedProperty(this.findViewById(targetId), targetProperty),
        Property(sourceGetter, null, T::class.java), BindingMode.ONE_WAY, converter
    )
}

fun Activity.uibind(
    targetId: Int,
    targetProperty: String?,
    sourceProperty: Property<*>,
    mode: BindingMode? = BindingMode.ONE_WAY,
    converter: ValueConverter = ValueConverter.getDefaultConverter()
): Binding {
    return UiBinder.bind(
        WeakReflectedProperty(this.findViewById(targetId), targetProperty),
        sourceProperty, mode, converter
    )
}

inline fun <reified T> Activity.uibind(
    targetId: Int,
    targetProperty: String?,
    noinline sourceProperty: () -> KProperty0<T>,
    mode: BindingMode = BindingMode.ONE_WAY,
    converter: ValueConverter = ValueConverter.getDefaultConverter()
): Binding {
    return UiBinder.bind(
        WeakReflectedProperty(this.findViewById(targetId), targetProperty),
        CompiledProperty(sourceProperty), mode, converter
    )
}

inline fun <reified T> Activity.bindTo(
    targetId: Int,
    targetProperty: String?,
    noinline sourceGetter: () -> T,
    converter: ValueConverter = ValueConverter.getDefaultConverter()
): Binding {
    return UiBinder.bind(
        WeakReflectedProperty(this.findViewById(targetId), targetProperty),
        Property(sourceGetter, null, T::class.java), BindingMode.ONE_WAY, converter
    )
}

inline fun <reified T : View> AdapterConverter(): AdapterConverter = AdapterConverter(T::class.java)
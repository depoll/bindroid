/**
 * Created by depoll on 2/2/18.
 */
package com.bindroid.trackable

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

interface TrackableProperty<T>: ReadWriteProperty<Any?, T> {}

inline fun <reified T: Any?> trackable(initialValue: T, crossinline set: (newValue: T) -> Unit = {}):
        TrackableProperty<T> {
    return when(T::class) {
        Boolean::class -> {
            val storage = TrackableBoolean(initialValue as Boolean)
            object : TrackableProperty<T> {
                override fun getValue(thisRef: Any?, property: KProperty<*>): T {
                    return storage.getValue(thisRef, property) as T
                }

                override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
                    storage.setValue(thisRef, property, value as Boolean)
                    set(value)
                }
            }
        }
        Byte::class -> {
            val storage = TrackableByte(initialValue as Byte)
            object : TrackableProperty<T> {
                override fun getValue(thisRef: Any?, property: KProperty<*>): T {
                    return storage.getValue(thisRef, property) as T
                }

                override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
                    storage.setValue(thisRef, property, value as Byte)
                    set(value)
                }
            }
        }
        Char::class -> {
            val storage = TrackableChar(initialValue as Char)
            object : TrackableProperty<T> {
                override fun getValue(thisRef: Any?, property: KProperty<*>): T {
                    return storage.getValue(thisRef, property) as T
                }

                override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
                    storage.setValue(thisRef, property, value as Char)
                    set(value)
                }
            }
        }
        Double::class -> {
            val storage = TrackableDouble(initialValue as Double)
            object : TrackableProperty<T> {
                override fun getValue(thisRef: Any?, property: KProperty<*>): T {
                    return storage.getValue(thisRef, property) as T
                }

                override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
                    storage.setValue(thisRef, property, value as Double)
                    set(value)
                }
            }
        }
        Float::class -> {
            val storage = TrackableFloat(initialValue as Float)
            object : TrackableProperty<T> {
                override fun getValue(thisRef: Any?, property: KProperty<*>): T {
                    return storage.getValue(thisRef, property) as T
                }

                override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
                    storage.setValue(thisRef, property, value as Float)
                    set(value)
                }
            }
        }
        Int::class -> {
            val storage = TrackableInt(initialValue as Int)
            object : TrackableProperty<T> {
                override fun getValue(thisRef: Any?, property: KProperty<*>): T {
                    return storage.getValue(thisRef, property) as T
                }

                override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
                    storage.setValue(thisRef, property, value as Int)
                    set(value)
                }
            }
        }
        Long::class -> {
            val storage = TrackableLong(initialValue as Long)
            object : TrackableProperty<T> {
                override fun getValue(thisRef: Any?, property: KProperty<*>): T {
                    return storage.getValue(thisRef, property) as T
                }

                override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
                    storage.setValue(thisRef, property, value as Long)
                    set(value)
                }
            }
        }
        Short::class -> {
            val storage = TrackableShort(initialValue as Short)
            object : TrackableProperty<T> {
                override fun getValue(thisRef: Any?, property: KProperty<*>): T {
                    return storage.getValue(thisRef, property) as T
                }

                override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
                    storage.setValue(thisRef, property, value as Short)
                    set(value)
                }
            }
        }
        else -> {
            val storage = TrackableField<T>(initialValue)
            object : TrackableProperty<T> {
                override fun getValue(thisRef: Any?, property: KProperty<*>): T {
                    return storage.getValue(thisRef, property)
                }

                override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
                    storage.setValue(thisRef, property, value)
                    set(value)
                }
            }
        }
    }
}

@JvmName("trackableNullable")
inline fun <reified T: Any?> trackable(crossinline set: (newValue: T?) -> Unit = {}):
        TrackableProperty<T?> {
    return object : TrackableProperty<T?> {
        private val storage = TrackableField<T?>(null)
        override fun getValue(thisRef: Any?, property: KProperty<*>): T? {
            return storage.getValue(thisRef, property)
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
            storage.setValue(thisRef, property, value)
            set(value)
        }
    }
}

operator fun <T> TrackableField<T>.getValue(
        thisRef: Any?,
        property: KProperty<*>
): T {
    return this.get()
}

operator fun <T> TrackableField<T>.setValue(
        thisRef: Any?,
        property: KProperty<*>,
        newValue: T) {
    this.set(newValue)
}

operator fun TrackableInt.getValue(
        thisRef: Any?,
        property: KProperty<*>
): Int {
    return this.get()
}

operator fun TrackableInt.setValue(
        thisRef: Any?,
        property: KProperty<*>,
        newValue: Int) {
    this.set(newValue)
}

operator fun TrackableBoolean.getValue(
        thisRef: Any?,
        property: KProperty<*>
): Boolean {
    return this.get()
}

operator fun TrackableBoolean.setValue(
        thisRef: Any?,
        property: KProperty<*>,
        newValue: Boolean) {
    this.set(newValue)
}

operator fun TrackableByte.getValue(
        thisRef: Any?,
        property: KProperty<*>
): Byte {
    return this.get()
}

operator fun TrackableByte.setValue(
        thisRef: Any?,
        property: KProperty<*>,
        newValue: Byte) {
    this.set(newValue)
}

operator fun TrackableChar.getValue(
        thisRef: Any?,
        property: KProperty<*>
): Char {
    return this.get()
}

operator fun TrackableChar.setValue(
        thisRef: Any?,
        property: KProperty<*>,
        newValue: Char) {
    this.set(newValue)
}

operator fun TrackableDouble.getValue(
        thisRef: Any?,
        property: KProperty<*>
): Double {
    return this.get()
}

operator fun TrackableDouble.setValue(
        thisRef: Any?,
        property: KProperty<*>,
        newValue: Double) {
    this.set(newValue)
}

operator fun TrackableFloat.getValue(
        thisRef: Any?,
        property: KProperty<*>
): Float {
    return this.get()
}

operator fun TrackableFloat.setValue(
        thisRef: Any?,
        property: KProperty<*>,
        newValue: Float) {
    this.set(newValue)
}

operator fun TrackableLong.getValue(
        thisRef: Any?,
        property: KProperty<*>
): Long {
    return this.get()
}

operator fun TrackableLong.setValue(
        thisRef: Any?,
        property: KProperty<*>,
        newValue: Long) {
    this.set(newValue)
}

operator fun TrackableShort.getValue(
        thisRef: Any?,
        property: KProperty<*>
): Short {
    return this.get()
}

operator fun TrackableShort.setValue(
        thisRef: Any?,
        property: KProperty<*>,
        newValue: Short) {
    this.set(newValue)
}

class TrackingScope<T> {
    val keepTracking: Unit
        get() {
            shouldKeepTracking = true
        }
    val stopTracking: Unit
        get() {
            shouldKeepTracking = false
        }
    internal var shouldKeepTracking: Boolean = false
    internal var wasCalled: Boolean = false
    internal var memoized: T? = null
}

fun <T> track(toTrack: () -> T, action: TrackingScope<T>.(() -> T) -> Unit) {
    val scope = TrackingScope<T>()
    val innerTrack = fun(): T {
        if (scope.wasCalled) {
            @Suppress("UNCHECKED_CAST")
            return scope.memoized as T
        }
        try {
            scope.memoized = Trackable.track({
                if (scope.shouldKeepTracking) {
                    track(toTrack, action)
                }
            }, toTrack)
            @Suppress("UNCHECKED_CAST")
            return scope.memoized as T
        } finally {
            scope.wasCalled = true
        }
    }
    scope.action(innerTrack)
    if (!scope.wasCalled) {
        innerTrack()
    }
}

inline fun <T> TrackableCollection<T>.transaction(operation: TrackableCollection<T>.() -> Unit) {
    try {
        this.isTracking = false
        this.operation()
    } finally {
        this.isTracking = true
        this.updateTrackers()
    }
}
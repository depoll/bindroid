/**
 * Created by depoll on 2/2/18.
 */
package com.bindroid.trackable

import kotlin.reflect.KProperty

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
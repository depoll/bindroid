package com.bindroid.test.kotlin

import com.bindroid.trackable.*
import com.bindroid.utils.EqualityComparer
import junit.framework.TestCase
import org.junit.Assert
import kotlin.math.min

class TrackableUtilsTest : TestCase() {
    fun testTrackSyntax() {
        val obj = object {
            var field: Int by TrackableInt(0)
        }
        var toSet = 0
        track({ obj.field }) {
            keepTracking
            toSet = it()
            if (toSet > 5) {
                stopTracking
            }
        }
        for (i in 1..10) {
            obj.field = i
            Assert.assertEquals(i, obj.field)
            Assert.assertEquals(min(i, 6), toSet)
        }
    }

    fun testTrackSyntaxWithThrownExceptions() {
        val obj = object {
            var field: Int by TrackableInt(0)
        }
        var toSet = 0
        track({
            val result = obj.field
            if (result == 3) throw UnsupportedOperationException()
            result
        }) {
            keepTracking
            toSet = try {
                it()
            } catch (e: Exception) {
                3
            }
            if (toSet > 5) {
                stopTracking
            }
        }
        for (i in 1..10) {
            obj.field = i
            Assert.assertEquals(i, obj.field)
            Assert.assertEquals(min(i, 6), toSet)
        }
    }

    fun testTrackSyntaxWithoutCallingIt() {
        val obj = object {
            var field: Int by TrackableInt(0)
        }
        var toSet = 0
        track({ obj.field }) {
            keepTracking
            toSet++
            if (toSet > 5) {
                stopTracking
            }
        }
        Assert.assertEquals(1, toSet)
        // Reset toSet to 0 since it will have been called the first time.
        toSet = 0
        for (i in 1..10) {
            obj.field = i
            Assert.assertEquals(i, obj.field)
            Assert.assertEquals(min(i, 6), toSet)
        }
    }

    fun testPersistentTrackableField() {
        val obj = object {
            var field: Int? by TrackableField<Int?>(Int.MIN_VALUE)
        }
        var toSet: Int? = null
        track({ obj.field }) {
            keepTracking
            toSet = it()
        }
        Assert.assertEquals(Int.MIN_VALUE, obj.field)
        Assert.assertEquals(obj.field, toSet)
        obj.field = 150
        Assert.assertEquals(150, obj.field!!)
        Assert.assertEquals(150, toSet!!)
        obj.field = 300
        Assert.assertEquals(300, obj.field!!)
        Assert.assertEquals(300, toSet!!)
    }

    fun testSetSameValue() {
        val obj = object {
            var field: Int? by TrackableField<Int?>(Int.MIN_VALUE)
        }
        var toSet: Int? = null
        track({ obj.field }) {
            keepTracking
            toSet = it()
        }
        Assert.assertEquals(Int.MIN_VALUE, obj.field)
        Assert.assertEquals(obj.field, toSet)
        obj.field = 150
        Assert.assertEquals(150, obj.field!!)
        Assert.assertEquals(150, toSet!!)
        obj.field = 300
        Assert.assertEquals(300, obj.field!!)
        Assert.assertEquals(300, toSet!!)
        toSet = Int.MIN_VALUE
        obj.field = 300
        Assert.assertEquals(300, obj.field!!)
        Assert.assertEquals(Int.MIN_VALUE, toSet!!)
        obj.field = 600
        Assert.assertEquals(600, obj.field!!)
        Assert.assertEquals(600, toSet!!)
    }

    fun testComparingTrackableField() {
        val obj = object {
            var field: Int? by ComparingTrackableField<Int?>(
                    150,
                    EqualityComparer { obj1, obj2 ->
                        // Returns true if both values are odd or even.
                        obj1!! % 2 == obj2!! % 2
                    }
            )
        }
        var toSet: Int? = null
        track({ obj.field }) {
            keepTracking
            toSet = it()
        }
        Assert.assertEquals(150, obj.field!!)
        Assert.assertEquals(150, toSet!!)
        obj.field = 300
        Assert.assertEquals(150, obj.field!!)
        Assert.assertEquals(150, toSet!!)
        obj.field = 151
        Assert.assertEquals(151, obj.field!!)
        Assert.assertEquals(151, toSet!!)
        obj.field = 601
        Assert.assertEquals(151, obj.field!!)
        Assert.assertEquals(151, toSet!!)
    }

    fun testSimpleTrackableField() {
        val obj = object {
            var field: Int? by TrackableField<Int?>(Int.MIN_VALUE)
        }
        var toSet: Int? = null
        Assert.assertEquals(Integer.MIN_VALUE, obj.field!!)
        track({ obj.field }) {
            if (toSet == null) {
                keepTracking
            }
            toSet = it()
        }
        Assert.assertEquals(obj.field, toSet)
        obj.field = 150
        Assert.assertEquals(150, obj.field!!)
        Assert.assertEquals(150, toSet!!)
        obj.field = 300
        Assert.assertEquals(300, obj.field!!)
        Assert.assertEquals(150, toSet!!)
    }

    fun testPersistentTrackableInt() {
        var field: Int by TrackableInt(Int.MIN_VALUE)
        var toSet = 0
        Assert.assertEquals(Int.MIN_VALUE, field)
        track({ field }) {
            keepTracking
            toSet = it()
        }
        Assert.assertEquals(field, toSet)
        field = 150
        Assert.assertEquals(150, field)
        Assert.assertEquals(150, toSet)
        field = 300
        Assert.assertEquals(300, field)
        Assert.assertEquals(300, toSet)
    }

    fun testSetSameValueInt() {
        var field: Int by TrackableInt(Int.MIN_VALUE)
        var toSet = 0
        Assert.assertEquals(Int.MIN_VALUE, field)
        track({ field }) {
            keepTracking
            toSet = it()
        }
        Assert.assertEquals(field, toSet)
        field = 150
        Assert.assertEquals(150, field)
        Assert.assertEquals(150, toSet)
        field = 300
        Assert.assertEquals(300, field)
        Assert.assertEquals(300, toSet)
        toSet = 0
        field = 300
        Assert.assertEquals(300, field)
        Assert.assertEquals(0, toSet)
        field = 600
        Assert.assertEquals(600, field)
        Assert.assertEquals(600, toSet)
    }

    fun testSimpleTrackableInt() {
        val obj = object {
            var field: Int by TrackableInt(Int.MIN_VALUE)
        }
        var toSet: Int = Int.MIN_VALUE
        Assert.assertEquals(Int.MIN_VALUE, obj.field)
        track({ obj.field }) {
            if (toSet == Int.MIN_VALUE) {
                keepTracking
            }
            toSet = it()
        }
        Assert.assertEquals(obj.field, toSet)
        obj.field = 150
        Assert.assertEquals(150, obj.field)
        Assert.assertEquals(150, toSet)
        obj.field = 300
        Assert.assertEquals(300, obj.field)
        Assert.assertEquals(150, toSet)
    }

    /*

    private fun beginTracking(field: TrackableBoolean, toSet: AtomicReference<Boolean>) {
        val t = object : Tracker {
            override fun update() {
                toSet.set(field.get())
                Trackable.track(this, Action { field.get() })
            }
        }
        Trackable.track(t, Action { toSet.set(field.get()) })
    }

    fun testPersistentTrackableBoolean() {
        val field = TrackableBoolean(false)
        val toSet = AtomicReference<Boolean>()
        Assert.assertEquals(false, field.get())
        beginTracking(field, toSet)
        Assert.assertEquals(field.get(), toSet.get())
        field.set(true)
        Assert.assertEquals(true, field.get())
        Assert.assertEquals(true, toSet.get())
        field.set(false)
        Assert.assertEquals(false, field.get())
        Assert.assertEquals(false, toSet.get())
    }

    fun testSetSameValueBoolean() {
        val field = TrackableBoolean(false)
        val toSet = AtomicReference<Boolean>()
        Assert.assertEquals(false, field.get())
        beginTracking(field, toSet)
        Assert.assertEquals(field.get(), toSet.get())
        field.set(true)
        Assert.assertEquals(true, field.get())
        Assert.assertEquals(true, toSet.get())
        toSet.set(false)
        field.set(true)
        Assert.assertEquals(true, field.get())
        Assert.assertEquals(false, toSet.get())
        field.set(false)
        Assert.assertEquals(false, field.get())
        Assert.assertEquals(false, toSet.get())
    }

    fun testSimpleTrackableBoolean() {
        val field = TrackableBoolean(false)
        val toSet = AtomicReference<Boolean>()
        Assert.assertEquals(false, field.get())
        val t = Tracker { toSet.set(field.get()) }
        Trackable.track(t, Action { toSet.set(field.get()) })
        Assert.assertEquals(field.get(), toSet.get())
        field.set(true)
        Assert.assertEquals(true, field.get())
        Assert.assertEquals(true, toSet.get())
        field.set(false)
        Assert.assertEquals(false, field.get())
        Assert.assertEquals(true, toSet.get())
    }

    private fun beginTracking(field: TrackableByte, toSet: AtomicReference<Byte>) {
        val t = object : Tracker {
            override fun update() {
                toSet.set(field.get())
                Trackable.track(this, Action { field.get() })
            }
        }
        Trackable.track(t, Action { toSet.set(field.get()) })
    }

    fun testPersistentTrackableByte() {
        val field = TrackableByte(java.lang.Byte.MIN_VALUE)
        val toSet = AtomicReference<Byte>()
        Assert.assertEquals(java.lang.Byte.MIN_VALUE, field.get())
        beginTracking(field, toSet)
        Assert.assertEquals(field.get(), toSet.get().toByte())
        field.set(150.toByte())
        Assert.assertEquals(150.toByte(), field.get())
        Assert.assertEquals(150.toByte(), toSet.get().toByte())
        field.set(300.toByte())
        Assert.assertEquals(300.toByte(), field.get())
        Assert.assertEquals(300.toByte(), toSet.get().toByte())
    }

    fun testSetSameValueByte() {
        val field = TrackableByte(java.lang.Byte.MIN_VALUE)
        val toSet = AtomicReference<Byte>()
        Assert.assertEquals(java.lang.Byte.MIN_VALUE, field.get())
        beginTracking(field, toSet)
        Assert.assertEquals(field.get(), toSet.get().toByte())
        field.set(150.toByte())
        Assert.assertEquals(150.toByte(), field.get())
        Assert.assertEquals(150.toByte(), toSet.get().toByte())
        field.set(300.toByte())
        Assert.assertEquals(300.toByte(), field.get())
        Assert.assertEquals(300.toByte(), toSet.get().toByte())
        toSet.set(java.lang.Byte.MIN_VALUE)
        field.set(300.toByte())
        Assert.assertEquals(300.toByte(), field.get())
        Assert.assertEquals(java.lang.Byte.MIN_VALUE, toSet.get().toByte())
        field.set(600.toByte())
        Assert.assertEquals(600.toByte(), field.get())
        Assert.assertEquals(600.toByte(), toSet.get().toByte())
    }

    fun testSimpleTrackableByte() {
        val field = TrackableByte(java.lang.Byte.MIN_VALUE)
        val toSet = AtomicReference<Byte>()
        Assert.assertEquals(java.lang.Byte.MIN_VALUE, field.get())
        val t = Tracker { toSet.set(field.get()) }
        Trackable.track(t, Action { toSet.set(field.get()) })
        Assert.assertEquals(field.get(), toSet.get().toByte())
        field.set(150.toByte())
        Assert.assertEquals(150.toByte(), field.get())
        Assert.assertEquals(150.toByte(), toSet.get().toByte())
        field.set(300.toByte())
        Assert.assertEquals(300.toByte(), field.get())
        Assert.assertEquals(150.toByte(), toSet.get().toByte())
    }

    private fun beginTracking(field: TrackableChar, toSet: AtomicReference<Char>) {
        val t = object : Tracker {
            override fun update() {
                toSet.set(field.get())
                Trackable.track(this, Action { field.get() })
            }
        }
        Trackable.track(t, Action { toSet.set(field.get()) })
    }

    fun testPersistentTrackableChar() {
        val field = TrackableChar(Character.MIN_VALUE)
        val toSet = AtomicReference<Char>()
        Assert.assertEquals(Character.MIN_VALUE, field.get())
        beginTracking(field, toSet)
        Assert.assertEquals(field.get(), toSet.get())
        field.set(150.toChar())
        Assert.assertEquals(150.toChar(), field.get())
        Assert.assertEquals(150.toChar(), toSet.get())
        field.set(300.toChar())
        Assert.assertEquals(300.toChar(), field.get())
        Assert.assertEquals(300.toChar(), toSet.get())
    }

    fun testSetSameValueChar() {
        val field = TrackableChar(Character.MIN_VALUE)
        val toSet = AtomicReference<Char>()
        Assert.assertEquals(Character.MIN_VALUE, field.get())
        beginTracking(field, toSet)
        Assert.assertEquals(field.get(), toSet.get())
        field.set(150.toChar())
        Assert.assertEquals(150.toChar(), field.get())
        Assert.assertEquals(150.toChar(), toSet.get())
        field.set(300.toChar())
        Assert.assertEquals(300.toChar(), field.get())
        Assert.assertEquals(300.toChar(), toSet.get())
        toSet.set(Character.MIN_VALUE)
        field.set(300.toChar())
        Assert.assertEquals(300.toChar(), field.get())
        Assert.assertEquals(Character.MIN_VALUE, toSet.get())
        field.set(600.toChar())
        Assert.assertEquals(600.toChar(), field.get())
        Assert.assertEquals(600.toChar(), toSet.get())
    }

    fun testSimpleTrackableChar() {
        val field = TrackableChar(Character.MIN_VALUE)
        val toSet = AtomicReference<Char>()
        Assert.assertEquals(Character.MIN_VALUE, field.get())
        val t = Tracker { toSet.set(field.get()) }
        Trackable.track(t, Action { toSet.set(field.get()) })
        Assert.assertEquals(field.get(), toSet.get())
        field.set(150.toChar())
        Assert.assertEquals(150.toChar(), field.get())
        Assert.assertEquals(150.toChar(), toSet.get())
        field.set(300.toChar())
        Assert.assertEquals(300.toChar(), field.get())
        Assert.assertEquals(150.toChar(), toSet.get())
    }

    private fun beginTracking(field: TrackableDouble, toSet: AtomicReference<Double>) {
        val t = object : Tracker {
            override fun update() {
                toSet.set(field.get())
                Trackable.track(this, Action { field.get() })
            }
        }
        Trackable.track(t, Action { toSet.set(field.get()) })
    }

    fun testPersistentTrackableDouble() {
        val field = TrackableDouble(java.lang.Double.MIN_VALUE)
        val toSet = AtomicReference<Double>()
        Assert.assertEquals(java.lang.Double.MIN_VALUE, field.get())
        beginTracking(field, toSet)
        Assert.assertEquals(field.get(), toSet.get().toDouble())
        field.set(150.0)
        Assert.assertEquals(150.0, field.get(), java.lang.Double.MIN_VALUE)
        Assert.assertEquals(150.0, toSet.get().toDouble(), java.lang.Double.MIN_VALUE)
        field.set(300.0)
        Assert.assertEquals(300.0, field.get(), java.lang.Double.MIN_VALUE)
        Assert.assertEquals(300.0, toSet.get().toDouble(), java.lang.Double.MIN_VALUE)
    }

    fun testSetSameValueDouble() {
        val field = TrackableDouble(java.lang.Double.MIN_VALUE)
        val toSet = AtomicReference<Double>()
        Assert.assertEquals(java.lang.Double.MIN_VALUE, field.get(), java.lang.Double.MIN_VALUE)
        beginTracking(field, toSet)
        Assert.assertEquals(field.get(), toSet.get().toDouble(), java.lang.Double.MIN_VALUE)
        field.set(150.0)
        Assert.assertEquals(150.0, field.get(), java.lang.Double.MIN_VALUE)
        Assert.assertEquals(150.0, toSet.get().toDouble(), java.lang.Double.MIN_VALUE)
        field.set(300.0)
        Assert.assertEquals(300.0, field.get(), java.lang.Double.MIN_VALUE)
        Assert.assertEquals(300.0, toSet.get().toDouble(), java.lang.Double.MIN_VALUE)
        toSet.set(java.lang.Double.MIN_VALUE)
        field.set(300.0)
        Assert.assertEquals(300.0, field.get(), java.lang.Double.MIN_VALUE)
        Assert.assertEquals(java.lang.Double.MIN_VALUE, toSet.get().toDouble(), java.lang.Double.MIN_VALUE)
        field.set(600.0)
        Assert.assertEquals(600.0, field.get(), java.lang.Double.MIN_VALUE)
        Assert.assertEquals(600.0, toSet.get().toDouble(), java.lang.Double.MIN_VALUE)
    }

    fun testSimpleTrackableDouble() {
        val field = TrackableDouble(java.lang.Double.MIN_VALUE)
        val toSet = AtomicReference<Double>()
        Assert.assertEquals(java.lang.Double.MIN_VALUE, field.get())
        val t = Tracker { toSet.set(field.get()) }
        Trackable.track(t, Action { toSet.set(field.get()) })
        Assert.assertEquals(field.get(), toSet.get().toDouble(), java.lang.Double.MIN_VALUE)
        field.set(150.0)
        Assert.assertEquals(150.0, field.get(), java.lang.Double.MIN_VALUE)
        Assert.assertEquals(150.0, toSet.get().toDouble(), java.lang.Double.MIN_VALUE)
        field.set(300.0)
        Assert.assertEquals(300.0, field.get(), java.lang.Double.MIN_VALUE)
        Assert.assertEquals(150.0, toSet.get().toDouble(), java.lang.Double.MIN_VALUE)
    }

    private fun beginTracking(field: TrackableFloat, toSet: AtomicReference<Float>) {
        val t = object : Tracker {
            override fun update() {
                toSet.set(field.get())
                Trackable.track(this, Action { field.get() })
            }
        }
        Trackable.track(t, Action { toSet.set(field.get()) })
    }

    fun testPersistentTrackableFloat() {
        val field = TrackableFloat(java.lang.Float.MIN_VALUE)
        val toSet = AtomicReference<Float>()
        Assert.assertEquals(java.lang.Float.MIN_VALUE, field.get())
        beginTracking(field, toSet)
        Assert.assertEquals(field.get(), toSet.get().toFloat())
        field.set(150f)
        Assert.assertEquals(150f, field.get(), java.lang.Float.MIN_VALUE)
        Assert.assertEquals(150f, toSet.get().toFloat(), java.lang.Float.MIN_VALUE)
        field.set(300f)
        Assert.assertEquals(300f, field.get(), java.lang.Float.MIN_VALUE)
        Assert.assertEquals(300f, toSet.get().toFloat(), java.lang.Float.MIN_VALUE)
    }

    fun testSetSameValueFloat() {
        val field = TrackableFloat(java.lang.Float.MIN_VALUE)
        val toSet = AtomicReference<Float>()
        Assert.assertEquals(java.lang.Float.MIN_VALUE, field.get(), java.lang.Float.MIN_VALUE)
        beginTracking(field, toSet)
        Assert.assertEquals(field.get(), toSet.get().toFloat(), java.lang.Float.MIN_VALUE)
        field.set(150f)
        Assert.assertEquals(150f, field.get(), java.lang.Float.MIN_VALUE)
        Assert.assertEquals(150f, toSet.get().toFloat(), java.lang.Float.MIN_VALUE)
        field.set(300f)
        Assert.assertEquals(300f, field.get(), java.lang.Float.MIN_VALUE)
        Assert.assertEquals(300f, toSet.get().toFloat(), java.lang.Float.MIN_VALUE)
        toSet.set(java.lang.Float.MIN_VALUE)
        field.set(300f)
        Assert.assertEquals(300f, field.get(), java.lang.Float.MIN_VALUE)
        Assert.assertEquals(java.lang.Float.MIN_VALUE, toSet.get().toFloat(), java.lang.Float.MIN_VALUE)
        field.set(600f)
        Assert.assertEquals(600f, field.get(), java.lang.Float.MIN_VALUE)
        Assert.assertEquals(600f, toSet.get().toFloat(), java.lang.Float.MIN_VALUE)
    }

    fun testSimpleTrackableFloat() {
        val field = TrackableFloat(java.lang.Float.MIN_VALUE)
        val toSet = AtomicReference<Float>()
        Assert.assertEquals(java.lang.Float.MIN_VALUE, field.get())
        val t = Tracker { toSet.set(field.get()) }
        Trackable.track(t, Action { toSet.set(field.get()) })
        Assert.assertEquals(field.get(), toSet.get().toFloat(), java.lang.Float.MIN_VALUE)
        field.set(150f)
        Assert.assertEquals(150f, field.get(), java.lang.Float.MIN_VALUE)
        Assert.assertEquals(150f, toSet.get().toFloat(), java.lang.Float.MIN_VALUE)
        field.set(300f)
        Assert.assertEquals(300f, field.get(), java.lang.Float.MIN_VALUE)
        Assert.assertEquals(150f, toSet.get().toFloat(), java.lang.Float.MIN_VALUE)
    }

    private fun beginTracking(field: TrackableLong, toSet: AtomicReference<Long>) {
        val t = object : Tracker {
            override fun update() {
                toSet.set(field.get())
                Trackable.track(this, Action { field.get() })
            }
        }
        Trackable.track(t, Action { toSet.set(field.get()) })
    }

    fun testPersistentTrackableLong() {
        val field = TrackableLong(java.lang.Long.MIN_VALUE)
        val toSet = AtomicReference<Long>()
        Assert.assertEquals(java.lang.Long.MIN_VALUE, field.get())
        beginTracking(field, toSet)
        Assert.assertEquals(field.get(), toSet.get().toLong())
        field.set(150)
        Assert.assertEquals(150, field.get())
        Assert.assertEquals(150, toSet.get().toLong())
        field.set(300)
        Assert.assertEquals(300, field.get())
        Assert.assertEquals(300, toSet.get().toLong())
    }

    fun testSetSameValueLong() {
        val field = TrackableLong(java.lang.Long.MIN_VALUE)
        val toSet = AtomicReference<Long>()
        Assert.assertEquals(java.lang.Long.MIN_VALUE, field.get())
        beginTracking(field, toSet)
        Assert.assertEquals(field.get(), toSet.get().toLong())
        field.set(150)
        Assert.assertEquals(150, field.get())
        Assert.assertEquals(150, toSet.get().toLong())
        field.set(300)
        Assert.assertEquals(300, field.get())
        Assert.assertEquals(300, toSet.get().toLong())
        toSet.set(java.lang.Long.MIN_VALUE)
        field.set(300)
        Assert.assertEquals(300, field.get())
        Assert.assertEquals(java.lang.Long.MIN_VALUE, toSet.get().toLong())
        field.set(600)
        Assert.assertEquals(600, field.get())
        Assert.assertEquals(600, toSet.get().toLong())
    }

    fun testSimpleTrackableLong() {
        val field = TrackableLong(java.lang.Long.MIN_VALUE)
        val toSet = AtomicReference<Long>()
        Assert.assertEquals(java.lang.Long.MIN_VALUE, field.get())
        val t = Tracker { toSet.set(field.get()) }
        Trackable.track(t, Action { toSet.set(field.get()) })
        Assert.assertEquals(field.get(), toSet.get().toLong())
        field.set(150)
        Assert.assertEquals(150, field.get())
        Assert.assertEquals(150, toSet.get().toLong())
        field.set(300)
        Assert.assertEquals(300, field.get())
        Assert.assertEquals(150, toSet.get().toLong())
    }

    private fun beginTracking(field: TrackableShort, toSet: AtomicReference<Short>) {
        val t = object : Tracker {
            override fun update() {
                toSet.set(field.get())
                Trackable.track(this, Action { field.get() })
            }
        }
        Trackable.track(t, Action { toSet.set(field.get()) })
    }

    fun testPersistentTrackableShort() {
        val field = TrackableShort(java.lang.Short.MIN_VALUE)
        val toSet = AtomicReference<Short>()
        Assert.assertEquals(java.lang.Short.MIN_VALUE, field.get())
        beginTracking(field, toSet)
        Assert.assertEquals(field.get(), toSet.get().toShort())
        field.set(150.toShort())
        Assert.assertEquals(150.toShort(), field.get())
        Assert.assertEquals(150.toShort(), toSet.get().toShort())
        field.set(300.toShort())
        Assert.assertEquals(300.toShort(), field.get())
        Assert.assertEquals(300.toShort(), toSet.get().toShort())
    }

    fun testSetSameValueShort() {
        val field = TrackableShort(java.lang.Short.MIN_VALUE)
        val toSet = AtomicReference<Short>()
        Assert.assertEquals(java.lang.Short.MIN_VALUE, field.get())
        beginTracking(field, toSet)
        Assert.assertEquals(field.get(), toSet.get().toShort())
        field.set(150.toShort())
        Assert.assertEquals(150.toShort(), field.get())
        Assert.assertEquals(150.toShort(), toSet.get().toShort())
        field.set(300.toShort())
        Assert.assertEquals(300.toShort(), field.get())
        Assert.assertEquals(300.toShort(), toSet.get().toShort())
        toSet.set(java.lang.Short.MIN_VALUE)
        field.set(300.toShort())
        Assert.assertEquals(300.toShort(), field.get())
        Assert.assertEquals(java.lang.Short.MIN_VALUE, toSet.get().toShort())
        field.set(600.toShort())
        Assert.assertEquals(600.toShort(), field.get())
        Assert.assertEquals(600.toShort(), toSet.get().toShort())
    }

    fun testSimpleTrackableShort() {
        val field = TrackableShort(java.lang.Short.MIN_VALUE)
        val toSet = AtomicReference<Short>()
        Assert.assertEquals(java.lang.Short.MIN_VALUE, field.get())
        val t = Tracker { toSet.set(field.get()) }
        Trackable.track(t, Action { toSet.set(field.get()) })
        Assert.assertEquals(field.get(), toSet.get().toShort())
        field.set(150.toShort())
        Assert.assertEquals(150.toShort(), field.get())
        Assert.assertEquals(150.toShort(), toSet.get().toShort())
        field.set(300.toShort())
        Assert.assertEquals(300.toShort(), field.get())
        Assert.assertEquals(150.toShort(), toSet.get().toShort())
    }*/
}
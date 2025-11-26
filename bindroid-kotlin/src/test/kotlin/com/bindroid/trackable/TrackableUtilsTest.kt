package com.bindroid.trackable

import org.junit.Assert.*
import org.junit.Test

class TrackableUtilsTest {
    class Holder {
        var value by trackable(0)
    }

    @Test
    fun trackable_delegate_reads_and_writes() {
        val h = Holder()
        assertEquals(0, h.value)
        h.value = 5
        assertEquals(5, h.value)
    }

    // Test all primitive types
    class AllTypesHolder {
        var boolValue by trackable(false)
        var byteValue by trackable(0.toByte())
        var charValue by trackable('a')
        var doubleValue by trackable(0.0)
        var floatValue by trackable(0.0f)
        var intValue by trackable(0)
        var longValue by trackable(0L)
        var shortValue by trackable(0.toShort())
        var stringValue by trackable("initial")
        var nullableValue by trackable<String?>(null)
    }

    @Test
    fun trackable_boolean_property() {
        val holder = AllTypesHolder()
        assertFalse(holder.boolValue)
        holder.boolValue = true
        assertTrue(holder.boolValue)
    }

    @Test
    fun trackable_byte_property() {
        val holder = AllTypesHolder()
        assertEquals(0.toByte(), holder.byteValue)
        holder.byteValue = 127.toByte()
        assertEquals(127.toByte(), holder.byteValue)
    }

    @Test
    fun trackable_char_property() {
        val holder = AllTypesHolder()
        assertEquals('a', holder.charValue)
        holder.charValue = 'z'
        assertEquals('z', holder.charValue)
    }

    @Test
    fun trackable_double_property() {
        val holder = AllTypesHolder()
        assertEquals(0.0, holder.doubleValue, 0.001)
        holder.doubleValue = 3.14159
        assertEquals(3.14159, holder.doubleValue, 0.001)
    }

    @Test
    fun trackable_float_property() {
        val holder = AllTypesHolder()
        assertEquals(0.0f, holder.floatValue, 0.001f)
        holder.floatValue = 2.718f
        assertEquals(2.718f, holder.floatValue, 0.001f)
    }

    @Test
    fun trackable_int_property() {
        val holder = AllTypesHolder()
        assertEquals(0, holder.intValue)
        holder.intValue = 42
        assertEquals(42, holder.intValue)
    }

    @Test
    fun trackable_long_property() {
        val holder = AllTypesHolder()
        assertEquals(0L, holder.longValue)
        holder.longValue = 9999999999L
        assertEquals(9999999999L, holder.longValue)
    }

    @Test
    fun trackable_short_property() {
        val holder = AllTypesHolder()
        assertEquals(0.toShort(), holder.shortValue)
        holder.shortValue = 1000.toShort()
        assertEquals(1000.toShort(), holder.shortValue)
    }

    @Test
    fun trackable_string_property() {
        val holder = AllTypesHolder()
        assertEquals("initial", holder.stringValue)
        holder.stringValue = "changed"
        assertEquals("changed", holder.stringValue)
    }

    @Test
    fun trackable_nullable_property() {
        val holder = AllTypesHolder()
        assertNull(holder.nullableValue)
        holder.nullableValue = "not null"
        assertEquals("not null", holder.nullableValue)
        holder.nullableValue = null
        assertNull(holder.nullableValue)
    }

    @Test
    fun trackable_with_setter_callback() {
        var callbackInvoked = false
        var newValue: Int? = null

        class CallbackHolder {
            var value by trackable(0) { v ->
                callbackInvoked = true
                newValue = v
            }
        }

        val holder = CallbackHolder()
        assertFalse(callbackInvoked)
        holder.value = 123
        assertTrue(callbackInvoked)
        assertEquals(123, newValue)
    }

    @Test
    fun trackable_all_types_with_callbacks() {
        var boolCalled = false
        var byteCalled = false
        var charCalled = false
        var doubleCalled = false
        var floatCalled = false
        var intCalled = false
        var longCalled = false
        var shortCalled = false

        class CallbacksHolder {
            var boolVal by trackable(false) { boolCalled = true }
            var byteVal by trackable(0.toByte()) { byteCalled = true }
            var charVal by trackable('a') { charCalled = true }
            var doubleVal by trackable(0.0) { doubleCalled = true }
            var floatVal by trackable(0.0f) { floatCalled = true }
            var intVal by trackable(0) { intCalled = true }
            var longVal by trackable(0L) { longCalled = true }
            var shortVal by trackable(0.toShort()) { shortCalled = true }
        }

        val holder = CallbacksHolder()

        holder.boolVal = true
        assertTrue(boolCalled)

        holder.byteVal = 1
        assertTrue(byteCalled)

        holder.charVal = 'z'
        assertTrue(charCalled)

        holder.doubleVal = 1.0
        assertTrue(doubleCalled)

        holder.floatVal = 1.0f
        assertTrue(floatCalled)

        holder.intVal = 1
        assertTrue(intCalled)

        holder.longVal = 1L
        assertTrue(longCalled)

        holder.shortVal = 1
        assertTrue(shortCalled)
    }

    @Test
    fun trackable_nullable_with_setter_callback() {
        var callbackInvoked = false
        var capturedValue: String? = null

        class NullableCallbackHolder {
            var value by trackable<String?> { v ->
                callbackInvoked = true
                capturedValue = v
            }
        }

        val holder = NullableCallbackHolder()
        holder.value = "test"
        assertTrue(callbackInvoked)
        assertEquals("test", capturedValue)

        callbackInvoked = false
        holder.value = null
        assertTrue(callbackInvoked)
        assertNull(capturedValue)
    }

    @Test
    fun trackableField_getValue_operator() {
        val field = TrackableField("test")
        class FieldHolder {
            var value by field
        }
        val holder = FieldHolder()
        assertEquals("test", holder.value)
    }

    @Test
    fun trackableField_setValue_operator() {
        val field = TrackableField("initial")
        class FieldHolder {
            var value by field
        }
        val holder = FieldHolder()
        holder.value = "updated"
        assertEquals("updated", holder.value)
        assertEquals("updated", field.get())
    }

    @Test
    fun trackableInt_getValue_operator() {
        val field = TrackableInt(10)
        class IntHolder {
            var value by field
        }
        val holder = IntHolder()
        assertEquals(10, holder.value)
    }

    @Test
    fun trackableInt_setValue_operator() {
        val field = TrackableInt(10)
        class IntHolder {
            var value by field
        }
        val holder = IntHolder()
        holder.value = 20
        assertEquals(20, holder.value)
        assertEquals(20, field.get())
    }

    @Test
    fun trackableBoolean_operators() {
        val field = TrackableBoolean(false)
        class BoolHolder {
            var value by field
        }
        val holder = BoolHolder()
        assertFalse(holder.value)
        holder.value = true
        assertTrue(holder.value)
    }

    @Test
    fun trackableByte_operators() {
        val field = TrackableByte(1)
        class ByteHolder {
            var value by field
        }
        val holder = ByteHolder()
        assertEquals(1.toByte(), holder.value)
        holder.value = 127
        assertEquals(127.toByte(), holder.value)
    }

    @Test
    fun trackableChar_operators() {
        val field = TrackableChar('x')
        class CharHolder {
            var value by field
        }
        val holder = CharHolder()
        assertEquals('x', holder.value)
        holder.value = 'y'
        assertEquals('y', holder.value)
    }

    @Test
    fun trackableDouble_operators() {
        val field = TrackableDouble(1.5)
        class DoubleHolder {
            var value by field
        }
        val holder = DoubleHolder()
        assertEquals(1.5, holder.value, 0.001)
        holder.value = 2.5
        assertEquals(2.5, holder.value, 0.001)
    }

    @Test
    fun trackableFloat_operators() {
        val field = TrackableFloat(1.5f)
        class FloatHolder {
            var value by field
        }
        val holder = FloatHolder()
        assertEquals(1.5f, holder.value, 0.001f)
        holder.value = 2.5f
        assertEquals(2.5f, holder.value, 0.001f)
    }

    @Test
    fun trackableLong_operators() {
        val field = TrackableLong(100L)
        class LongHolder {
            var value by field
        }
        val holder = LongHolder()
        assertEquals(100L, holder.value)
        holder.value = 200L
        assertEquals(200L, holder.value)
    }

    @Test
    fun trackableShort_operators() {
        val field = TrackableShort(50)
        class ShortHolder {
            var value by field
        }
        val holder = ShortHolder()
        assertEquals(50.toShort(), holder.value)
        holder.value = 100
        assertEquals(100.toShort(), holder.value)
    }

    @Test
    fun trackingScope_keepTracking() {
        val scope = TrackingScope<Int>()
        assertFalse(scope.shouldKeepTracking)
        scope.keepTracking
        assertTrue(scope.shouldKeepTracking)
    }

    @Test
    fun trackingScope_stopTracking() {
        val scope = TrackingScope<Int>()
        scope.shouldKeepTracking = true
        scope.stopTracking
        assertFalse(scope.shouldKeepTracking)
    }

    @Test
    fun track_function_basic() {
        var trackableCalled = 0
        val field = TrackableInt(5)

        track({ field.get().also { trackableCalled++ } }) { innerTrack ->
            val result = innerTrack()
            assertEquals(5, result)
            assertEquals(1, trackableCalled)
        }
    }

    @Test
    fun track_function_keeps_tracking() {
        val field = TrackableInt(10)
        var invocationCount = 0

        track({ field.get() }) { innerTrack ->
            keepTracking
            val result = innerTrack()
            assertEquals(10, result)
            invocationCount++
        }

        // Should have been called at least once
        assertTrue(invocationCount > 0)
    }

    @Test
    fun track_function_stops_tracking() {
        val field = TrackableInt(20)
        var invocationCount = 0

        track({ field.get() }) { innerTrack ->
            stopTracking
            val result = innerTrack()
            assertEquals(20, result)
            invocationCount++
        }

        assertEquals(1, invocationCount)
    }

    @Test
    fun track_function_re_tracks_on_change() {
        val field = TrackableInt(100)
        var invocationCount = 0
        var maxInvocations = 3

        track({ field.get() }) { innerTrack ->
            invocationCount++
            if (invocationCount < maxInvocations) {
                keepTracking
            }
            val result = innerTrack()
            // Modify value to trigger re-track if still tracking
            if (invocationCount < maxInvocations) {
                field.set(field.get() + 1)
            }
        }

        // Should be called multiple times due to re-tracking
        assertEquals(maxInvocations, invocationCount)
    }

    @Test
    fun track_function_with_multiple_calls() {
        val field = TrackableInt(15)

        track({ field.get() }) { innerTrack ->
            val first = innerTrack()
            assertEquals(15, first)

            // Second call should return memoized value
            val second = innerTrack()
            assertEquals(15, second)
        }
    }

    @Test
    fun track_function_auto_invokes_if_not_called() {
        val field = TrackableInt(25)
        var actionRan = false

        track({ field.get() }) { innerTrack ->
            // Don't call innerTrack - it should auto-invoke
            actionRan = true
        }

        assertTrue(actionRan)
    }

    @Test
    fun trackableCollection_transaction() {
        val collection = TrackableCollection<String>()
        collection.add("a")
        collection.add("b")

        collection.transaction {
            add("c")
            add("d")
        }

        assertEquals(4, collection.size)
        assertEquals("a", collection[0])
        assertEquals("b", collection[1])
        assertEquals("c", collection[2])
        assertEquals("d", collection[3])
    }

    @Test
    fun trackableCollection_become_simple() {
        val collection = TrackableCollection<String>()
        collection.add("a")
        collection.add("b")

        collection.become(listOf("x", "y", "z"))

        assertEquals(3, collection.size)
        assertEquals("x", collection[0])
        assertEquals("y", collection[1])
        assertEquals("z", collection[2])
    }

    @Test
    fun trackableCollection_become_with_equals() {
        data class Item(val id: Int, val name: String)

        val collection = TrackableCollection<Item>()
        val item1 = Item(1, "first")
        val item2 = Item(2, "second")
        collection.add(item1)
        collection.add(item2)

        // Update with new items that have same IDs but different names
        val updatedItem1 = Item(1, "updated first")
        val updatedItem2 = Item(2, "updated second")
        collection.become(listOf(updatedItem1, updatedItem2)) { left, right ->
            left.id == right.id
        }

        assertEquals(2, collection.size)
        assertEquals("updated first", collection[0].name)
        assertEquals("updated second", collection[1].name)
    }

    @Test
    fun trackableCollection_become_removes_extra_items() {
        val collection = TrackableCollection<String>()
        collection.add("a")
        collection.add("b")
        collection.add("c")
        collection.add("d")

        collection.become(listOf("a", "b"))

        assertEquals(2, collection.size)
        assertEquals("a", collection[0])
        assertEquals("b", collection[1])
    }

    @Test
    fun trackableCollection_become_adds_new_items() {
        val collection = TrackableCollection<String>()
        collection.add("a")

        collection.become(listOf("a", "b", "c"))

        assertEquals(3, collection.size)
        assertEquals("a", collection[0])
        assertEquals("b", collection[1])
        assertEquals("c", collection[2])
    }

    @Test
    fun trackableCollection_become_reorders_items() {
        val collection = TrackableCollection<String>()
        collection.add("a")
        collection.add("b")
        collection.add("c")

        collection.become(listOf("c", "a", "b"))

        assertEquals(3, collection.size)
        assertEquals("c", collection[0])
        assertEquals("a", collection[1])
        assertEquals("b", collection[2])
    }
}


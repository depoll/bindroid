package com.bindroid.utils

import org.junit.Assert.*
import org.junit.Test
import java.lang.ref.WeakReference

class CompiledPropertyTest {
    class Box { var x: Int = 0 }
    class StringBox { var value: String = "initial" }
    class ReadOnlyBox { val immutable: String = "readonly" }

    @Test
    fun compiledProperty_reads_and_writes() {
        val b = Box()
        val p = CompiledProperty { b::x }
        assertEquals(0, p.value)
        p.setValue(7)
        assertEquals(7, b.x)
        assertEquals(7, p.value)
    }

    @Test
    fun compiledProperty_with_string() {
        val box = StringBox()
        val prop = CompiledProperty { box::value }
        assertEquals("initial", prop.value)
        prop.setValue("updated")
        assertEquals("updated", box.value)
        assertEquals("updated", prop.value)
    }

    @Test
    fun compiledProperty_handles_null_reference() {
        var box: Box? = Box()
        box!!.x = 42
        val prop = CompiledProperty { box!!::x }
        assertEquals(42, prop.value)

        // Null out the box - getter should handle NPE
        box = null
        val result = prop.value
        assertNull(result)
    }

    @Test
    fun compiledProperty_read_only_throws_on_set() {
        val box = ReadOnlyBox()
        val prop = CompiledProperty { box::immutable }
        assertEquals("readonly", prop.value)

        try {
            prop.setValue("attempt")
            fail("Should have thrown UnsupportedOperationException")
        } catch (e: UnsupportedOperationException) {
            assertTrue(e.message?.contains("has no setter") ?: false)
        }
    }

    @Test
    fun compiledProperty_has_correct_type() {
        val box = Box()
        val prop = CompiledProperty { box::x }
        assertEquals(Int::class.javaObjectType, prop.type)
    }

    @Test
    fun compiledProp_helper_function() {
        val box = StringBox()
        val prop = compiledProp { box::value }
        assertEquals("initial", prop.value)
        prop.setValue("changed")
        assertEquals("changed", prop.value)
    }

    @Test
    fun weakBind_creates_weak_reference_operation() {
        val box = Box()
        box.x = 100
        val weakOp = box weakBind { x }
        assertEquals(100, weakOp())

        box.x = 200
        assertEquals(200, weakOp())
    }

    @Test
    fun weakProp_creates_compiled_property_with_weak_ref() {
        val box = Box()
        box.x = 50
        val prop = box weakProp { this::x }
        assertEquals(50, prop.value)

        box.x = 75
        assertEquals(75, prop.value)

        prop.setValue(100)
        assertEquals(100, box.x)
    }

    @Test
    fun weakProp_with_string_property() {
        val box = StringBox()
        val prop = box weakProp { this::value }
        assertEquals("initial", prop.value)

        prop.setValue("modified")
        assertEquals("modified", box.value)
        assertEquals("modified", prop.value)
    }

    @Test
    fun compiledProperty_getter_and_setter_accessible() {
        val box = Box()
        val prop = CompiledProperty { box::x }

        assertNotNull(prop.getter)
        assertNotNull(prop.setter)

        val getterResult = prop.getter.evaluate()
        assertEquals(0, getterResult)

        prop.setter.invoke(123)
        assertEquals(123, box.x)
    }
}


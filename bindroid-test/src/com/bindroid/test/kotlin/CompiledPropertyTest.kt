package com.bindroid.test.kotlin

import com.bindroid.utils.CompiledProperty
import com.bindroid.utils.weakBind
import junit.framework.TestCase
import org.junit.Assert

class CompiledPropertyTest : TestCase() {
    var prop: String? = null
    val readOnlyProp: String?
        get() = prop

    fun testReadWriteCompiledProperty() {
        prop = "Hello"
        val property = CompiledProperty({ ::prop })
        Assert.assertEquals("Hello", property.value)
        property.value = "Goodbye"
        Assert.assertEquals("Goodbye", prop)
        Assert.assertEquals("Goodbye", property.value)
    }

    fun testReadOnlyCompiledProperty() {
        prop = "Hello"
        val property = CompiledProperty({ ::readOnlyProp })
        Assert.assertEquals("Hello", property.value)
        var threw = true
        try {
            property.value = "Goodbye"
            threw = false
        } catch (e: Exception) {
        }
        Assert.assertTrue(threw)
    }

    private class TestObj {
        var myProp: String? = null
    }

    fun testWeakCompiledProperty() {
        var obj = TestObj()
        obj.myProp = "Hello"
        val property = CompiledProperty(obj weakBind { ::myProp })
        Assert.assertEquals("Hello", property.value)
        obj = TestObj()
        Runtime.getRuntime().gc()
        var threw = true
        try {
            property.value
            threw = false
        } catch (e: Exception) {
            Assert.assertTrue(true)
        }
        Assert.assertTrue(threw)
    }
}

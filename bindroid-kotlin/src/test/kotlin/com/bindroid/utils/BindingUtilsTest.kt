package com.bindroid.utils

import org.junit.Assert.*
import org.junit.Test

class BindingUtilsTest {

    @Test
    fun converter_creates_valueConverter() {
        val conv = converter(
            toTarget = { value, _ -> (value as? Int)?.toString() },
            toSource = { value, _ -> (value as? String)?.toIntOrNull() }
        )

        val targetResult = conv.convertToTarget(42, String::class.java)
        assertEquals("42", targetResult)

        val sourceResult = conv.convertToSource("123", Int::class.java)
        assertEquals(123, sourceResult)
    }

    @Test
    fun converter_with_only_toTarget() {
        val conv = converter(
            toTarget = { value, _ -> (value as? Int)?.times(2) }
        )

        val result = conv.convertToTarget(21, Int::class.java)
        assertEquals(42, result)

        // toSource should return null when not provided
        val sourceResult = conv.convertToSource("test", String::class.java)
        assertNull(sourceResult)
    }

    @Test
    fun converter_with_only_toSource() {
        val conv = converter(
            toSource = { value, _ -> (value as? String)?.length }
        )

        val result = conv.convertToSource("hello", Int::class.java)
        assertEquals(5, result)

        // toTarget should return null when not provided
        val targetResult = conv.convertToTarget(42, String::class.java)
        assertNull(targetResult)
    }

    @Test
    fun converter_with_null_handlers() {
        val conv = converter()

        val targetResult = conv.convertToTarget("test", String::class.java)
        assertNull(targetResult)

        val sourceResult = conv.convertToSource("test", String::class.java)
        assertNull(sourceResult)
    }

    @Test
    fun converter_handles_null_values() {
        val conv = converter(
            toTarget = { value, _ -> value?.toString()?.uppercase() }
        )

        val result = conv.convertToTarget(null, String::class.java)
        assertNull(result)
    }

    @Test
    fun converter_handles_type_conversion() {
        val conv = converter(
            toTarget = { value, _ ->
                when (value) {
                    is Int -> value.toString()
                    is String -> value
                    else -> null
                }
            }
        )

        assertEquals("42", conv.convertToTarget(42, String::class.java))
        assertEquals("hello", conv.convertToTarget("hello", String::class.java))
        assertNull(conv.convertToTarget(3.14, String::class.java))
    }

    @Test
    fun converter_receives_type_parameter() {
        var capturedType: Class<*>? = null

        val conv = converter(
            toTarget = { value, targetType ->
                capturedType = targetType
                value
            }
        )

        conv.convertToTarget("test", String::class.java)
        assertEquals(String::class.java, capturedType)

        conv.convertToTarget(42, Int::class.java)
        assertEquals(Int::class.java, capturedType)
    }

    @Test
    fun converter_chains_transformations() {
        val conv = converter(
            toTarget = { value, _ ->
                (value as? Int)
                    ?.let { it * 2 }
                    ?.let { it + 10 }
                    ?.let { "Result: $it" }
            }
        )

        val result = conv.convertToTarget(5, String::class.java)
        assertEquals("Result: 20", result)
    }

    @Test
    fun converter_handles_complex_types() {
        data class Person(val name: String, val age: Int)

        val conv = converter(
            toTarget = { value, _ ->
                (value as? Person)?.let { "${it.name} (${it.age})" }
            },
            toSource = { value, _ ->
                (value as? String)?.split(" (")?.let { parts ->
                    if (parts.size == 2) {
                        val name = parts[0]
                        val age = parts[1].removeSuffix(")").toIntOrNull()
                        if (age != null) Person(name, age) else null
                    } else null
                }
            }
        )

        val person = Person("John", 30)
        val asString = conv.convertToTarget(person, String::class.java)
        assertEquals("John (30)", asString)

        val backToPerson = conv.convertToSource("Jane (25)", Person::class.java) as? Person
        assertNotNull(backToPerson)
        assertEquals("Jane", backToPerson?.name)
        assertEquals(25, backToPerson?.age)
    }
}

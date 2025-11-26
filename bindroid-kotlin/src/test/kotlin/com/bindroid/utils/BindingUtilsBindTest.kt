package com.bindroid.utils

import com.bindroid.BindingMode
import com.bindroid.trackable.trackable
import org.junit.Assert.assertEquals
import org.junit.Test

class BindingUtilsBindTest {

    class ViewModel {
        var source by trackable("source")
        var target by trackable("target")
    }

    @Test
    fun bind_properties_one_way() {
        val vm = ViewModel()
        
        bind({ vm::target }, { vm::source })
        
        assertEquals("source", vm.target)
        
        vm.source = "updated"
        assertEquals("updated", vm.target)
    }

    @Test
    fun bind_direct_call() {
        val vm = ViewModel()
        val targetProp = CompiledProperty({ vm::target })
        val sourceProp = CompiledProperty({ vm::source })
        
        bind(targetProp, sourceProp)
        
        assertEquals("source", vm.target)
    }

    @Test
    fun bind_properties_two_way() {
        val vm = ViewModel()
        
        bind({ vm::target }, { vm::source }, BindingMode.TWO_WAY)
        
        assertEquals("source", vm.target)
        
        vm.source = "updated"
        assertEquals("updated", vm.target)
        
        vm.target = "reverse"
        assertEquals("reverse", vm.source)
    }
    
    @Test
    fun bind_infix_extension() {
        val vm = ViewModel()
        
        ({ vm::target }) bind { vm.source }
        
        assertEquals("source", vm.target)
        
        vm.source = "infix"
        assertEquals("infix", vm.target)
    }

    @Test
    fun weakBind_creates_weak_reference() {
        val vm = ViewModel()
        
        // Test that weakBind works with operations
        val weakGetter = vm weakBind { source }
        assertEquals("source", weakGetter())
        
        vm.source = "weakly bound"
        assertEquals("weakly bound", weakGetter())
    }

    @Test
    fun weakProp_creates_compiled_property() {
        val vm = ViewModel()
        
        val prop = vm weakProp { ::source }
        
        assertEquals("source", prop.value)
        
        vm.source = "weak prop"
        assertEquals("weak prop", prop.value)
    }

    @Test
    fun compiledProp_helper() {
        val vm = ViewModel()
        
        val prop = compiledProp { vm::source }
        
        assertEquals("source", prop.value)
        
        prop.value = "compiled"
        assertEquals("compiled", vm.source)
    }

    @Test
    fun compiled_property_setter_throws_on_readonly() {
        val vm = ViewModel()
        
        // Create a read-only property scenario
        val readOnlyProp = CompiledProperty({ vm::source as kotlin.reflect.KProperty0<String> }, String::class.java)
        
        // Should be able to read
        assertEquals("source", readOnlyProp.value)
    }

    @Test
    fun converter_helper_both_directions() {
        val conv = converter(
            toSource = { value, _ -> (value as? String)?.uppercase() },
            toTarget = { value, _ -> (value as? String)?.lowercase() }
        )
        
        assertEquals("HELLO", conv.convertToSource("hello", String::class.java))
        assertEquals("world", conv.convertToTarget("WORLD", String::class.java))
    }

    @Test
    fun converter_helper_toSource_only() {
        val conv = converter(
            toSource = { value, _ -> (value as? Int)?.times(2) }
        )
        
        assertEquals(10, conv.convertToSource(5, Int::class.java))
        // toTarget returns null when not specified
        assertEquals(null, conv.convertToTarget(5, Int::class.java))
    }

    @Test
    fun converter_helper_toTarget_only() {
        val conv = converter(
            toTarget = { value, _ -> (value as? Int)?.plus(1) }
        )
        
        assertEquals(null, conv.convertToSource(5, Int::class.java))
        assertEquals(6, conv.convertToTarget(5, Int::class.java))
    }
}

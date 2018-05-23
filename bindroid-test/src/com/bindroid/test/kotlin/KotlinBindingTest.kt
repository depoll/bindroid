package com.bindroid.test.kotlin

import com.bindroid.Binding
import com.bindroid.BindingMode
import com.bindroid.test.GCTestUtils
import com.bindroid.utils.CompiledProperty
import com.bindroid.utils.bind
import com.bindroid.utils.converter
import junit.framework.TestCase
import org.junit.Assert
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.Executors

class KotlinBindingTest : TestCase() {
    fun testNestedTwoWayBinding() {
        val n1 = KotlinNestable()
        n1.value = "Hello!"
        val n2 = KotlinNestable()
        n2.value = "Bonjour!"
        n2.child = KotlinNestable()
        n2.child!!.value = "Yo!"

        bind({ (n1::value) }, { (n2.child!!::value) }, BindingMode.TWO_WAY)

        Assert.assertEquals("Yo!", n1.value)
        Assert.assertEquals("Bonjour!", n2.value)
        Assert.assertEquals("Yo!", n2.child!!.value)
        n1.value = "Shalom!"
        Assert.assertEquals("Shalom!", n1.value)
        Assert.assertEquals("Shalom!", n2.child!!.value)
        n2.value = "Hola!"
        Assert.assertEquals("Shalom!", n1.value)
        Assert.assertEquals("Shalom!", n2.child!!.value)
        n2.child!!.value = "Sup?"
        Assert.assertEquals("Sup?", n1.value)
        Assert.assertEquals("Sup?", n2.child!!.value)
        n2.child = null
        Assert.assertEquals(null, n1.value)
        n2.child = KotlinNestable()
        Assert.assertEquals(null, n1.value)
        n2.child!!.value = "Woohoo!"
        Assert.assertEquals("Woohoo!", n1.value)
        Assert.assertEquals("Woohoo!", n2.child!!.value)
        val newChild = KotlinNestable()
        newChild.value = "Hah!"
        n2.child = newChild
        Assert.assertEquals("Hah!", n1.value)
        Assert.assertEquals("Hah!", n2.child!!.value)
        n1.value = "Shalom!"
        Assert.assertEquals("Shalom!", n1.value)
        Assert.assertEquals("Shalom!", n2.child!!.value)
    }

    fun testOneWayBinding() {
        val n1 = KotlinNestable()
        n1.value = "Hello!"
        val n2 = KotlinNestable()
        n2.value = "Bonjour!"

        bind({ (n1::value) }, { (n2::value) }, BindingMode.ONE_WAY)

        Assert.assertEquals("Bonjour!", n1.value)
        Assert.assertEquals("Bonjour!", n2.value)
        n1.value = "Shalom!"
        Assert.assertEquals("Shalom!", n1.value)
        Assert.assertEquals("Bonjour!", n2.value)
        n2.value = "Hola!"
        Assert.assertEquals("Hola!", n1.value)
        Assert.assertEquals("Hola!", n2.value)
    }

    fun testOneWayBindingWithConverter() {
        val n1 = KotlinNestable()
        n1.value = "Hello!"
        val n2 = KotlinNestable()
        n2.value = "Bonjour!"

        bind({ (n1::value) }, { (n2::value) },
                BindingMode.ONE_WAY,
                converter({ targetValue, _ ->
                    if (("" + targetValue).endsWith("bar") || ("" + targetValue).endsWith("foo")) {
                        targetValue
                    } else targetValue.toString() + "foo"
                }, { sourceValue, _ ->
                    if (("" + sourceValue).endsWith("bar") || ("" + sourceValue).endsWith("foo")) {
                        sourceValue
                    } else sourceValue.toString() + "bar"
                }))

        Assert.assertEquals("Bonjour!bar", n1.value)
        Assert.assertEquals("Bonjour!", n2.value)
        n1.value = "Shalom!"
        Assert.assertEquals("Shalom!", n1.value)
        Assert.assertEquals("Bonjour!", n2.value)
        n2.value = "Hola!"
        Assert.assertEquals("Hola!bar", n1.value)
        Assert.assertEquals("Hola!", n2.value)
    }

    fun testBindingWithExpression() {
        val n1 = KotlinNestable()
        n1.value = "Hello!"
        val n2 = KotlinNestable()
        n2.value = "Bonjour!"

        { (n1::value) } bind { n2.value + "Whoa" }

        Assert.assertEquals("Bonjour!Whoa", n1.value)
        Assert.assertEquals("Bonjour!", n2.value)
        n1.value = "Shalom!"
        Assert.assertEquals("Shalom!", n1.value)
        Assert.assertEquals("Bonjour!", n2.value)
        n2.value = "Hola!"
        Assert.assertEquals("Hola!Whoa", n1.value)
        Assert.assertEquals("Hola!", n2.value)
    }

    fun testOneWayToSourceBindingWithConverter() {
        val n1 = KotlinNestable()
        n1.value = "Hello!"
        val n2 = KotlinNestable()
        n2.value = "Bonjour!"

        bind({ (n1::value) }, { (n2::value) },
                BindingMode.ONE_WAY_TO_SOURCE,
                converter({ targetValue, _ ->
                    if (("" + targetValue).endsWith("bar") || ("" + targetValue).endsWith("foo")) {
                        targetValue
                    } else targetValue.toString() + "foo"
                }, { sourceValue, _ ->
                    if (("" + sourceValue).endsWith("bar") || ("" + sourceValue).endsWith("foo")) {
                        sourceValue
                    } else sourceValue.toString() + "bar"
                }))

        Assert.assertEquals("Hello!", n1.value)
        Assert.assertEquals("Hello!foo", n2.value)
        n1.value = "Shalom!"
        Assert.assertEquals("Shalom!", n1.value)
        Assert.assertEquals("Shalom!foo", n2.value)
        n2.value = "Hola!"
        Assert.assertEquals("Shalom!", n1.value)
        Assert.assertEquals("Hola!", n2.value)
    }

    fun testTwoWayBindingWithConverter() {
        val n1 = KotlinNestable()
        n1.value = "Hello!"
        val n2 = KotlinNestable()
        n2.value = "Bonjour!"

        bind({ (n1::value) }, { (n2::value) },
                BindingMode.TWO_WAY,
                converter({ targetValue, _ ->
                    if (("" + targetValue).endsWith("bar") || ("" + targetValue).endsWith("foo")) {
                        targetValue
                    } else targetValue.toString() + "foo"
                }, { sourceValue, _ ->
                    if (("" + sourceValue).endsWith("bar") || ("" + sourceValue).endsWith("foo")) {
                        sourceValue
                    } else sourceValue.toString() + "bar"
                }))

        Assert.assertEquals("Bonjour!bar", n1.value)
        Assert.assertEquals("Bonjour!bar", n2.value)
        n1.value = "Shalom!"
        Assert.assertEquals("Shalom!foo", n1.value)
        Assert.assertEquals("Shalom!foo", n2.value)
        n2.value = "Hola!"
        Assert.assertEquals("Hola!bar", n1.value)
        Assert.assertEquals("Hola!bar", n2.value)
    }

    fun testOneWayToSourceBinding() {
        val n1 = KotlinNestable()
        n1.value = "Hello!"
        val n2 = KotlinNestable()
        n2.value = "Bonjour!"

        bind({ (n1::value) }, { (n2::value) }, BindingMode.ONE_WAY_TO_SOURCE)

        Assert.assertEquals("Hello!", n1.value)
        Assert.assertEquals("Hello!", n2.value)
        n1.value = "Shalom!"
        Assert.assertEquals("Shalom!", n1.value)
        Assert.assertEquals("Shalom!", n2.value)
        n2.value = "Hola!"
        Assert.assertEquals("Shalom!", n1.value)
        Assert.assertEquals("Hola!", n2.value)
    }

    fun testTwoWayBinding() {
        val n1 = KotlinNestable()
        n1.value = "Hello!"
        val n2 = KotlinNestable()
        n2.value = "Bonjour!"

        bind({ (n1::value) }, { (n2::value) }, BindingMode.TWO_WAY)

        Assert.assertEquals("Bonjour!", n1.value)
        Assert.assertEquals("Bonjour!", n2.value)
        n1.value = "Shalom!"
        Assert.assertEquals("Shalom!", n1.value)
        Assert.assertEquals("Shalom!", n2.value)
        n2.value = "Hola!"
        Assert.assertEquals("Hola!", n1.value)
        Assert.assertEquals("Hola!", n2.value)
    }

    fun testObjectsGetGCed() {
        Executors.newCachedThreadPool().submit(Callable {
            val n1 = KotlinNestable()
            n1.value = "Hello!"
            val n2 = KotlinNestable()
            n2.value = "Bonjour!"

            val b = bind({ (n1::value) }, { (n2::value) }, BindingMode.TWO_WAY)
            GCTestUtils.watchPointers(Arrays.asList(n1, n2, b))
        }).get().run()
    }

    fun testOneWayBindingsContinueInSpiteOfLosingReference() {
        val n1 = KotlinNestable()
        n1.value = "Hello!"
        val n2 = KotlinNestable()
        n2.value = "Bonjour!"

        bind({ (n1::value) }, { (n2::value) })

        Runtime.getRuntime().gc()
        Assert.assertEquals("Bonjour!", n1.value)
        Assert.assertEquals("Bonjour!", n2.value)
        Runtime.getRuntime().gc()
        n1.value = "Shalom!"
        Runtime.getRuntime().gc()
        Assert.assertEquals("Shalom!", n1.value)
        Assert.assertEquals("Bonjour!", n2.value)
        Runtime.getRuntime().gc()
        n2.value = "Hola!"
        Runtime.getRuntime().gc()
        Assert.assertEquals("Hola!", n1.value)
        Assert.assertEquals("Hola!", n2.value)
    }

    fun testOneWayBindingAllowsSourceToRelease() {
        Executors.newCachedThreadPool().submit(Callable {
            val n1 = KotlinNestable()
            n1.value = "Hello!"
            val n2 = KotlinNestable()
            n2.value = "Bonjour!"

            val sourceProperty = CompiledProperty({ (n2::value) })

            Binding(CompiledProperty({ (n1::value) }), sourceProperty, BindingMode.ONE_WAY)

            Runtime.getRuntime().gc()
            Assert.assertEquals("Bonjour!", n1.value)
            Assert.assertEquals("Bonjour!", n2.value)
            Runtime.getRuntime().gc()
            n1.value = "Shalom!"
            Runtime.getRuntime().gc()
            Assert.assertEquals("Shalom!", n1.value)
            Assert.assertEquals("Bonjour!", n2.value)
            Runtime.getRuntime().gc()
            n2.value = "Hola!"
            Runtime.getRuntime().gc()
            Assert.assertEquals("Hola!", n1.value)
            Assert.assertEquals("Hola!", n2.value)
            GCTestUtils.watchPointers(Arrays.asList(n2))
        }).get().run()
    }

    fun testOneWayToSourceBindingAllowsTargetToRelease() {
        Executors.newCachedThreadPool().submit(Callable {
            val n1 = KotlinNestable()
            n1.value = "Hello!"
            val n2 = KotlinNestable()
            n2.value = "Bonjour!"

            val targetProperty = CompiledProperty({ (n1::value) })

            Binding(targetProperty, CompiledProperty({ (n2::value) }),
                    BindingMode.ONE_WAY_TO_SOURCE)

            Runtime.getRuntime().gc()
            Assert.assertEquals("Hello!", n1.value)
            Assert.assertEquals("Hello!", n2.value)
            Runtime.getRuntime().gc()
            n1.value = "Shalom!"
            Runtime.getRuntime().gc()
            Assert.assertEquals("Shalom!", n1.value)
            Assert.assertEquals("Shalom!", n2.value)
            Runtime.getRuntime().gc()
            n2.value = "Hola!"
            Runtime.getRuntime().gc()
            Assert.assertEquals("Shalom!", n1.value)
            Assert.assertEquals("Hola!", n2.value)
            GCTestUtils.watchPointers(Arrays.asList(n1))
        }).get().run()
    }
}

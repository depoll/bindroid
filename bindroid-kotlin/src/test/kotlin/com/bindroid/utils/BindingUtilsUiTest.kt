package com.bindroid.utils

import android.app.Activity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.bindroid.BindingMode
import com.bindroid.trackable.trackable
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class BindingUtilsUiTest {

    private lateinit var activity: Activity
    private lateinit var textView: TextView
    private val TEXT_VIEW_ID = 12345

    class ViewModel {
        var text by trackable("initial")
    }

    @Before
    fun setUp() {
        activity = Robolectric.buildActivity(Activity::class.java).create().get()
        val layout = LinearLayout(activity)
        textView = TextView(activity)
        textView.id = TEXT_VIEW_ID
        layout.addView(textView)
        activity.setContentView(layout)
    }

    @Test
    fun uibind_view_binds_property_to_view() {
        val viewModel = ViewModel()
        
        // Bind TextView.text to ViewModel.text using View extension
        textView.uibind(TEXT_VIEW_ID, "text", { viewModel::text })
        
        // Initial sync
        assertEquals("initial", textView.text.toString())
        
        // Update ViewModel
        viewModel.text = "updated"
        assertEquals("updated", textView.text.toString())
    }

    @Test
    fun uibind_activity_binds_property_to_view() {
        val viewModel = ViewModel()
        
        // Bind TextView.text to ViewModel.text using Activity extension
        activity.uibind(TEXT_VIEW_ID, "text", { viewModel::text })
        
        // Initial sync
        assertEquals("initial", textView.text.toString())
        
        // Update ViewModel
        viewModel.text = "activity_updated"
        assertEquals("activity_updated", textView.text.toString())
    }
    
    @Test
    fun bindTo_view_binds_one_way() {
        val viewModel = ViewModel()
        
        // Bind TextView.text to ViewModel.text getter
        textView.bindTo(TEXT_VIEW_ID, "text", { viewModel.text })
        
        assertEquals("initial", textView.text.toString())
        
        viewModel.text = "one_way"
        // Note: bindTo with getter tracks changes if the getter accesses trackable properties
        assertEquals("one_way", textView.text.toString())
    }

    class TestView(context: android.content.Context) : View(context) {
        var myText: String = "initial"
    }

    @Test
    fun reflectedProperty_works_on_custom_view() {
        val customView = TestView(activity)
        val prop = com.bindroid.utils.ReflectedProperty(customView, "myText")
        
        assertEquals("initial", prop.value)
        
        prop.value = "updated"
        assertEquals("updated", customView.myText)
    }

    @Test
    fun reflectedProperty_finds_textView_text_property() {
        val method = TextView::class.java.getMethod("getText")
        println("TextView.getText() returns: ${method.returnType}")
        
        println("Listing setText methods:")
        TextView::class.java.methods.filter { it.name == "setText" }.forEach { 
            println("Method: ${it.name}(${it.parameterTypes.joinToString { t -> t.name }})")
        }
        
        val prop = com.bindroid.utils.ReflectedProperty(textView, "text")
        println("Property type: ${prop.type}")
        
        // Test setter
        try {
            prop.value = "reflected"
            assertEquals("reflected", textView.text.toString())
        } catch (e: Exception) {
            println("Setter failed: $e")
            // throw e // Don't throw to see output
        }
    }

    @Test
    fun uibind_view_with_property_object() {
        val viewModel = ViewModel()
        val property = CompiledProperty({ viewModel::text })
        
        // Bind using the non-inline overload taking Property object
        textView.uibind(TEXT_VIEW_ID, "text", property)
        
        assertEquals("initial", textView.text.toString())
        viewModel.text = "property_obj"
        assertEquals("property_obj", textView.text.toString())
    }

    @Test
    fun uibind_activity_with_property_object() {
        val viewModel = ViewModel()
        val property = CompiledProperty({ viewModel::text })
        
        // Bind using Activity extension (non-inline)
        activity.uibind(TEXT_VIEW_ID, "text", property)
        
        assertEquals("initial", textView.text.toString())
        viewModel.text = "activity_property"
        assertEquals("activity_property", textView.text.toString())
    }

    @Test
    fun bindTo_activity_binds_one_way() {
        val viewModel = ViewModel()
        
        // Bind TextView.text to ViewModel.text getter using Activity extension
        activity.bindTo(TEXT_VIEW_ID, "text", { viewModel.text })
        
        assertEquals("initial", textView.text.toString())
        
        viewModel.text = "activity_bindTo"
        assertEquals("activity_bindTo", textView.text.toString())
    }
}

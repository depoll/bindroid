package com.bindroid.test;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

import junit.framework.TestCase;

import com.bindroid.Binding;
import com.bindroid.BindingMode;
import com.bindroid.ValueConverter;
import com.bindroid.utils.Action;
import com.bindroid.utils.Function;
import com.bindroid.utils.Property;
import com.bindroid.utils.ReflectedProperty;

public class BindingTest extends TestCase {
  public void testNestedTwoWayBinding() {
    final Nestable n1 = new Nestable();
    n1.setValue("Hello!");
    final Nestable n2 = new Nestable();
    n2.setValue("Bonjour!");
    n2.setChild(new Nestable());
    n2.getChild().setValue("Yo!");

    new Binding(new ReflectedProperty(n1, "Value"), new ReflectedProperty(n2, "Child.Value"),
        BindingMode.TwoWay);

    assertEquals("Yo!", n1.getValue());
    assertEquals("Bonjour!", n2.getValue());
    assertEquals("Yo!", n2.getChild().getValue());
    n1.setValue("Shalom!");
    assertEquals("Shalom!", n1.getValue());
    assertEquals("Shalom!", n2.getChild().getValue());
    n2.setValue("Hola!");
    assertEquals("Shalom!", n1.getValue());
    assertEquals("Shalom!", n2.getChild().getValue());
    n2.getChild().setValue("Sup?");
    assertEquals("Sup?", n1.getValue());
    assertEquals("Sup?", n2.getChild().getValue());
    n2.setChild(null);
    assertEquals(null, n1.getValue());
    n2.setChild(new Nestable());
    assertEquals(null, n1.getValue());
    n2.getChild().setValue("Woohoo!");
    assertEquals("Woohoo!", n1.getValue());
    assertEquals("Woohoo!", n2.getChild().getValue());
    Nestable newChild = new Nestable();
    newChild.setValue("Hah!");
    n2.setChild(newChild);
    assertEquals("Hah!", n1.getValue());
    assertEquals("Hah!", n2.getChild().getValue());
    n1.setValue("Shalom!");
    assertEquals("Shalom!", n1.getValue());
    assertEquals("Shalom!", n2.getChild().getValue());
  }

  public void testOneWayBinding() {
    final Nestable n1 = new Nestable();
    n1.setValue("Hello!");
    final Nestable n2 = new Nestable();
    n2.setValue("Bonjour!");

    new Binding(new Property<String>(new Function<String>() {
      @Override
      public String evaluate() {
        return n1.getValue();
      }
    }, new Action<String>() {

      @Override
      public void invoke(String parameter) {
        n1.setValue(parameter);
      }
    }, String.class), new Property<String>(new Function<String>() {

      @Override
      public String evaluate() {
        return n2.getValue();
      }
    }, new Action<String>() {

      @Override
      public void invoke(String parameter) {
        n2.setValue(parameter);
      }
    }, String.class), BindingMode.OneWay);

    assertEquals("Bonjour!", n1.getValue());
    assertEquals("Bonjour!", n2.getValue());
    n1.setValue("Shalom!");
    assertEquals("Shalom!", n1.getValue());
    assertEquals("Bonjour!", n2.getValue());
    n2.setValue("Hola!");
    assertEquals("Hola!", n1.getValue());
    assertEquals("Hola!", n2.getValue());
  }

  public void testOneWayBindingWithConverter() {
    final Nestable n1 = new Nestable();
    n1.setValue("Hello!");
    final Nestable n2 = new Nestable();
    n2.setValue("Bonjour!");

    new Binding(new Property<String>(new Function<String>() {
      @Override
      public String evaluate() {
        return n1.getValue();
      }
    }, new Action<String>() {
      @Override
      public void invoke(String parameter) {
        n1.setValue(parameter);
      }
    }, String.class), new Property<String>(new Function<String>() {
      @Override
      public String evaluate() {
        return n2.getValue();
      }
    }, new Action<String>() {
      @Override
      public void invoke(String parameter) {
        n2.setValue(parameter);
      }
    }, String.class), BindingMode.OneWay, new ValueConverter() {
      @Override
      public Object convertToSource(Object targetValue, Class<?> sourceType) {
        if (("" + targetValue).endsWith("bar") || ("" + targetValue).endsWith("foo")) {
          return targetValue;
        }
        return targetValue + "foo";
      }

      @Override
      public Object convertToTarget(Object sourceValue, Class<?> targetType) {
        if (("" + sourceValue).endsWith("bar") || ("" + sourceValue).endsWith("foo")) {
          return sourceValue;
        }
        return sourceValue + "bar";
      }
    });

    assertEquals("Bonjour!bar", n1.getValue());
    assertEquals("Bonjour!", n2.getValue());
    n1.setValue("Shalom!");
    assertEquals("Shalom!", n1.getValue());
    assertEquals("Bonjour!", n2.getValue());
    n2.setValue("Hola!");
    assertEquals("Hola!bar", n1.getValue());
    assertEquals("Hola!", n2.getValue());
  }

  public void testOneWayToSourceBindingWithConverter() {
    final Nestable n1 = new Nestable();
    n1.setValue("Hello!");
    final Nestable n2 = new Nestable();
    n2.setValue("Bonjour!");

    new Binding(new Property<String>(new Function<String>() {
      @Override
      public String evaluate() {
        return n1.getValue();
      }
    }, new Action<String>() {
      @Override
      public void invoke(String parameter) {
        n1.setValue(parameter);
      }
    }, String.class), new Property<String>(new Function<String>() {
      @Override
      public String evaluate() {
        return n2.getValue();
      }
    }, new Action<String>() {
      @Override
      public void invoke(String parameter) {
        n2.setValue(parameter);
      }
    }, String.class), BindingMode.OneWayToSource, new ValueConverter() {
      @Override
      public Object convertToSource(Object targetValue, Class<?> sourceType) {
        if (("" + targetValue).endsWith("bar") || ("" + targetValue).endsWith("foo")) {
          return targetValue;
        }
        return targetValue + "foo";
      }

      @Override
      public Object convertToTarget(Object sourceValue, Class<?> targetType) {
        if (("" + sourceValue).endsWith("bar") || ("" + sourceValue).endsWith("foo")) {
          return sourceValue;
        }
        return sourceValue + "bar";
      }
    });

    assertEquals("Hello!", n1.getValue());
    assertEquals("Hello!foo", n2.getValue());
    n1.setValue("Shalom!");
    assertEquals("Shalom!", n1.getValue());
    assertEquals("Shalom!foo", n2.getValue());
    n2.setValue("Hola!");
    assertEquals("Shalom!", n1.getValue());
    assertEquals("Hola!", n2.getValue());
  }

  public void testTwoWayBindingWithConverter() {
    final Nestable n1 = new Nestable();
    n1.setValue("Hello!");
    final Nestable n2 = new Nestable();
    n2.setValue("Bonjour!");

    new Binding(new Property<String>(new Function<String>() {
      @Override
      public String evaluate() {
        return n1.getValue();
      }
    }, new Action<String>() {
      @Override
      public void invoke(String parameter) {
        n1.setValue(parameter);
      }
    }, String.class), new Property<String>(new Function<String>() {
      @Override
      public String evaluate() {
        return n2.getValue();
      }
    }, new Action<String>() {
      @Override
      public void invoke(String parameter) {
        n2.setValue(parameter);
      }
    }, String.class), BindingMode.TwoWay, new ValueConverter() {
      @Override
      public Object convertToSource(Object targetValue, Class<?> sourceType) {
        if (("" + targetValue).endsWith("bar") || ("" + targetValue).endsWith("foo")) {
          return targetValue;
        }
        return targetValue + "foo";
      }

      @Override
      public Object convertToTarget(Object sourceValue, Class<?> targetType) {
        if (("" + sourceValue).endsWith("bar") || ("" + sourceValue).endsWith("foo")) {
          return sourceValue;
        }
        return sourceValue + "bar";
      }
    });

    assertEquals("Bonjour!bar", n1.getValue());
    assertEquals("Bonjour!bar", n2.getValue());
    n1.setValue("Shalom!");
    assertEquals("Shalom!foo", n1.getValue());
    assertEquals("Shalom!foo", n2.getValue());
    n2.setValue("Hola!");
    assertEquals("Hola!bar", n1.getValue());
    assertEquals("Hola!bar", n2.getValue());
  }

  public void testOneWayBindingReflected() {
    final Nestable n1 = new Nestable();
    n1.setValue("Hello!");
    final Nestable n2 = new Nestable();
    n2.setValue("Bonjour!");

    new Binding(new ReflectedProperty(n1, "Value"), new ReflectedProperty(n2, "Value"),
        BindingMode.OneWay);

    assertEquals("Bonjour!", n1.getValue());
    assertEquals("Bonjour!", n2.getValue());
    n1.setValue("Shalom!");
    assertEquals("Shalom!", n1.getValue());
    assertEquals("Bonjour!", n2.getValue());
    n2.setValue("Hola!");
    assertEquals("Hola!", n1.getValue());
    assertEquals("Hola!", n2.getValue());
  }

  public void testOneWayToSourceBinding() {
    final Nestable n1 = new Nestable();
    n1.setValue("Hello!");
    final Nestable n2 = new Nestable();
    n2.setValue("Bonjour!");

    new Binding(new Property<String>(new Function<String>() {
      @Override
      public String evaluate() {
        return n1.getValue();
      }
    }, new Action<String>() {
      @Override
      public void invoke(String parameter) {
        n1.setValue(parameter);
      }
    }, String.class), new Property<String>(new Function<String>() {
      @Override
      public String evaluate() {
        return n2.getValue();
      }
    }, new Action<String>() {
      @Override
      public void invoke(String parameter) {
        n2.setValue(parameter);
      }
    }, String.class), BindingMode.OneWayToSource);

    assertEquals("Hello!", n1.getValue());
    assertEquals("Hello!", n2.getValue());
    n1.setValue("Shalom!");
    assertEquals("Shalom!", n1.getValue());
    assertEquals("Shalom!", n2.getValue());
    n2.setValue("Hola!");
    assertEquals("Shalom!", n1.getValue());
    assertEquals("Hola!", n2.getValue());
  }

  public void testOneWayToSourceBindingReflected() {
    final Nestable n1 = new Nestable();
    n1.setValue("Hello!");
    final Nestable n2 = new Nestable();
    n2.setValue("Bonjour!");

    new Binding(new ReflectedProperty(n1, "Value"), new ReflectedProperty(n2, "Value"),
        BindingMode.OneWayToSource);

    assertEquals("Hello!", n1.getValue());
    assertEquals("Hello!", n2.getValue());
    n1.setValue("Shalom!");
    assertEquals("Shalom!", n1.getValue());
    assertEquals("Shalom!", n2.getValue());
    n2.setValue("Hola!");
    assertEquals("Shalom!", n1.getValue());
    assertEquals("Hola!", n2.getValue());
  }

  public void testTwoWayBinding() {
    final Nestable n1 = new Nestable();
    n1.setValue("Hello!");
    final Nestable n2 = new Nestable();
    n2.setValue("Bonjour!");

    new Binding(new Property<String>(new Function<String>() {
      @Override
      public String evaluate() {
        return n1.getValue();
      }
    }, new Action<String>() {
      @Override
      public void invoke(String parameter) {
        n1.setValue(parameter);
      }
    }, String.class), new Property<String>(new Function<String>() {
      @Override
      public String evaluate() {
        return n2.getValue();
      }
    }, new Action<String>() {
      @Override
      public void invoke(String parameter) {
        n2.setValue(parameter);
      }
    }, String.class), BindingMode.TwoWay);

    assertEquals("Bonjour!", n1.getValue());
    assertEquals("Bonjour!", n2.getValue());
    n1.setValue("Shalom!");
    assertEquals("Shalom!", n1.getValue());
    assertEquals("Shalom!", n2.getValue());
    n2.setValue("Hola!");
    assertEquals("Hola!", n1.getValue());
    assertEquals("Hola!", n2.getValue());
  }

  public void testTwoWayBindingReflected() {
    final Nestable n1 = new Nestable();
    n1.setValue("Hello!");
    final Nestable n2 = new Nestable();
    n2.setValue("Bonjour!");

    new Binding(new ReflectedProperty(n1, "Value"), new ReflectedProperty(n2, "Value"),
        BindingMode.TwoWay);

    assertEquals("Bonjour!", n1.getValue());
    assertEquals("Bonjour!", n2.getValue());
    n1.setValue("Shalom!");
    assertEquals("Shalom!", n1.getValue());
    assertEquals("Shalom!", n2.getValue());
    n2.setValue("Hola!");
    assertEquals("Hola!", n1.getValue());
    assertEquals("Hola!", n2.getValue());
  }

  public void testObjectsGetGCed() throws Exception {
    Nestable n1 = new Nestable();
    n1.setValue("Hello!");
    Nestable n2 = new Nestable();
    n2.setValue("Bonjour!");

    Binding b = new Binding(new ReflectedProperty(n1, "Value"), new ReflectedProperty(n2, "Value"),
        BindingMode.TwoWay);

    ReferenceQueue<Object> refs = new ReferenceQueue<Object>();

    WeakReference<Nestable> weakN1 = new WeakReference<Nestable>(n1, refs);
    WeakReference<Nestable> weakN2 = new WeakReference<Nestable>(n2, refs);
    WeakReference<Binding> weakBinding = new WeakReference<Binding>(b, refs);

    n1 = n2 = null;
    b = null;
    int count = 0;
    while (count < 3) {
      Runtime.getRuntime().gc();
      if (refs.remove(100) != null) {
        count++;
      }
    }
    assertNull(weakN1.get());
    assertNull(weakN2.get());
    assertNull(weakBinding.get());
  }

  public void testOneWayBindingsContinueInSpiteOfLosingReference() throws Exception {
    Nestable n1 = new Nestable();
    n1.setValue("Hello!");
    Nestable n2 = new Nestable();
    n2.setValue("Bonjour!");

    new Binding(new ReflectedProperty(n1, "Value"), new ReflectedProperty(n2, "Value"),
        BindingMode.OneWay);

    Runtime.getRuntime().gc();
    assertEquals("Bonjour!", n1.getValue());
    assertEquals("Bonjour!", n2.getValue());
    Runtime.getRuntime().gc();
    n1.setValue("Shalom!");
    Runtime.getRuntime().gc();
    assertEquals("Shalom!", n1.getValue());
    assertEquals("Bonjour!", n2.getValue());
    Runtime.getRuntime().gc();
    n2.setValue("Hola!");
    Runtime.getRuntime().gc();
    assertEquals("Hola!", n1.getValue());
    assertEquals("Hola!", n2.getValue());
  }

  public void testOneWayBindingAllowsSourceToRelease() throws Exception {
    Nestable n1 = new Nestable();
    n1.setValue("Hello!");
    Nestable n2 = new Nestable();
    n2.setValue("Bonjour!");

    ReflectedProperty sourceProperty = new ReflectedProperty(n2, "Value");

    new Binding(new ReflectedProperty(n1, "Value"), sourceProperty, BindingMode.OneWay);

    Runtime.getRuntime().gc();
    assertEquals("Bonjour!", n1.getValue());
    assertEquals("Bonjour!", n2.getValue());
    Runtime.getRuntime().gc();
    n1.setValue("Shalom!");
    Runtime.getRuntime().gc();
    assertEquals("Shalom!", n1.getValue());
    assertEquals("Bonjour!", n2.getValue());
    Runtime.getRuntime().gc();
    n2.setValue("Hola!");
    Runtime.getRuntime().gc();
    assertEquals("Hola!", n1.getValue());
    assertEquals("Hola!", n2.getValue());

    ReferenceQueue<Object> refs = new ReferenceQueue<Object>();
    WeakReference<Nestable> weakN2 = new WeakReference<Nestable>(n2, refs);

    n2 = null;
    sourceProperty = null;
    int count = 0;
    int iterations = 0;
    while (count < 1 && iterations < 100) {
      Runtime.getRuntime().gc();
      if (refs.remove(100) != null) {
        count++;
      }
      iterations++;
    }
    assertNull(weakN2.get());
  }

  public void testOneWayToSourceBindingAllowsTargetToRelease() throws Exception {
    Nestable n1 = new Nestable();
    n1.setValue("Hello!");
    Nestable n2 = new Nestable();
    n2.setValue("Bonjour!");

    ReflectedProperty targetProperty = new ReflectedProperty(n1, "Value");

    new Binding(targetProperty, new ReflectedProperty(n2, "Value"), BindingMode.OneWayToSource);

    Runtime.getRuntime().gc();
    assertEquals("Hello!", n1.getValue());
    assertEquals("Hello!", n2.getValue());
    Runtime.getRuntime().gc();
    n1.setValue("Shalom!");
    Runtime.getRuntime().gc();
    assertEquals("Shalom!", n1.getValue());
    assertEquals("Shalom!", n2.getValue());
    Runtime.getRuntime().gc();
    n2.setValue("Hola!");
    Runtime.getRuntime().gc();
    assertEquals("Shalom!", n1.getValue());
    assertEquals("Hola!", n2.getValue());

    ReferenceQueue<Object> refs = new ReferenceQueue<Object>();
    WeakReference<Nestable> weakN1 = new WeakReference<Nestable>(n1, refs);

    n1 = null;
    targetProperty = null;
    int count = 0;
    int iterations = 0;
    while (count < 1 && iterations < 100) {
      Runtime.getRuntime().gc();
      if (refs.remove(100) != null) {
        count++;
      }
      iterations++;
    }
    assertNull(weakN1.get());
  }
}

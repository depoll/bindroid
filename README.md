Bindroid - Databinding for Android
==================================

Bindroid is an open-source utility library for Android apps whose primary goal is to simplify binding UI to data.  It introduces an observability pattern for model objects and a number of simple methods for quickly binding these objects to your user interfaces.  The result is a responsive, always-consistent and up-to-date user experience without having to write all of the glue to ensure that your UI is updated whenever the data it presents changes.

Bindroid dramatically simplifies implementing the [MVVM pattern](http://en.wikipedia.org/wiki/Model_View_ViewModel) when building Android applications, though it can be just as useful even if you're not following this pattern.

Javadocs
--------
You can find the Javadocs for Bindroid [here](http://depoll.github.com/bindroid/docs/).

Getting Started
---------------

Clone the repository, import it into Eclipse alongside your Android project, and add a reference to Bindroid to your project. Nothing more to it!

Building Your Model
-------------------

At the heart of the binding framework is the concept of notifications when changes occur in your model.  The classes in the `com.bindroid.trackable` package provide the tools you need to make your models notify observers of changes.

The most common class you'll use is `TrackableField<T>` and its trackable siblings, `TrackableBoolean`, `TrackableByte`, `TrackableChar`, `TrackableDouble`, `TrackableFloat`, `TrackableInt`, `TrackableLong`, and `TrackableShort`.  These objects can replace the private fields in your models.  Once you've backed your models with trackable fields, any changes to their values will be able to be tracked.  This enables bindings to observe changes to models as they happen and update their targets.

A simple ViewModel might look like this:

```java
public class FooModel {
  // An int property named "Bar"
  private TrackableInt bar = new TrackableInt(0);
  public int getBar() {
    return bar.get();
  }
  public void setBar(int value) {
    bar.set(value);
  }

  // A String property named "Baz"
  private TrackableField<String> baz = new TrackableField<String>();
  public String getBaz() {
    return baz.get();
  }
  public void setBaz(String value) {
    baz.set(value);
  }
}
```

`Trackable`s are infectious -- any property based upon trackable objects is automatically trackable, including complex calculated properties.  For example, if you added a property called `BarBaz` to the model above, calculated based upon the values of `Bar` and `Baz`, it would still be tracked and bindings to it would be updated whenever either `Bar` or `Baz` change:

```java
public String getBarBaz() {
  int strLength = (getBaz() == null ? 0 : getBaz().length());
  if ((strLength + getBar()) % 2 == 0) {
    return "Baz's length + Bar was even.";
  }
  return String.format("Bar: %d, Baz: %s", getBar(), getBaz());
}
```

Bindroid also provides a trackable list implementation called `TrackableCollection<T>`, allowing properties to be based upon values in the list.  Anything tracking these values will be notified whenever the collection changes.

Building Your UI
----------------

You will begin by building your Android UI as you would normally, with a variety of layout XML files.  Once you've placed all of your `View`s in the layout, it's time to create the object that you'll bind to and establish the bindings.  Bindroid takes care of the rest!

Under the covers, Bindroid uses the `Binding` class to establish a binding between two properties.  Most of the time, however, you will not use this class directly.  Instead, you will create your bindings using the `UiBinder` class.  `UiBinder` contains a number of overloads for a `bind()` method that aid in looking up UI elements based upon their resource IDs and binding to properties using reflection so that you don't have to build anonymous classes for every property that you bind to.

In your `Activity`'s `OnCreate()` override or `View`'s constructor, call `bind()` for each pair of properties you wish to track each other:

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  setContentView(R.layout.activity_main);

  ViewModel model = new ViewModel();

  UiBinder.bind(new EditTextTextProperty((EditText) this.findViewById(R.id.TextField)), model,
      "StringValue", BindingMode.TWO_WAY);
  UiBinder.bind(this, R.id.TextView, "Text", model, "StringValue", BindingMode.ONE_WAY);
}
```

The string-based property names in the example above indicate the property path to bind.  Any methods named "get<Name>" are considered a property called "Name", and if a corresponding setter is defined, it must be named "set<Name>".  The property path also supports dot-notation to access nested properties and index notation to access lists and maps.  For example, the string `"Foo.Bar[baz].Bat[1]"` would map to a binding to `model.getFoo().getBar().get("baz").getBat().get(1)`.

Since the built-in Android views don't support Bindroid's property change notifications, two-way bindings require some sort of adapter to proffer the changes back to the model object.  In the example above, we've used the `EditTextTextProperty` to bind the `Text` property on an `EditText` view to the `StringValue` property on our model.  Bindroid provides a few of these property adapters for you.  In most cases, however, a one-way binding can be accomplished simply using reflection (e.g. the `Text` property of a `TextView`, as shown above), since the binding framework doesn't need to be notified when the view's property value changes.

Conversions
-----------

Sometimes, the values on your model will not map directly to the types of values that the views in your UI expect.  For example, you may have an `int` property on your model that will be displayed as a `String` in a `TextView`.  Bindroid solves this problem using converters, which can be passed to a `Binding` or to `UiBinder.bind()`:

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  setContentView(R.layout.activity_main);

  ViewModel model = new ViewModel();

  UiBinder.bind(new EditTextTextProperty((EditText) this.findViewById(R.id.TextField)), model,
      "StringValue", BindingMode.TWO_WAY);
  UiBinder.bind(this, R.id.TextView, "Text", model, "StringValue", BindingMode.ONE_WAY);
  UiBinder.bind(this, R.id.ListView, "Adapter", model, "Dates", BindingMode.ONE_WAY,
      new AdapterConverter(DateView.class));

  UiBinder.bind(this, R.id.CountTextView, "Text", model, "Count", BindingMode.ONE_WAY,
      new ToStringConverter("Count: %d"));
  UiBinder.bind(this, R.id.TextLengthView, "Text", model, "TextLength", BindingMode.ONE_WAY,
      new ToStringConverter("Text length: %d"));
  UiBinder.bind(this, R.id.SumView, "Text", model, "CountPlusTextLength", BindingMode.ONE_WAY,
      new ToStringConverter("Sum: %d"));
  UiBinder.bind(this, R.id.EvenSpinner, "Visibility", model, "CountIsEven", BindingMode.ONE_WAY,
      new BoolConverter());
  UiBinder.bind(this, R.id.OddSpinner, "Visibility", model, "CountIsEven", BindingMode.ONE_WAY,
      new BoolConverter(true));
}
```

Bindroid provides a number of built-in converters for the most commonly-used cases required by UI bindings.  For example, any value can easily be converted to a `String` using the `ToStringConverter`.  `List`s and `TrackableCollection<T>`s can be easily converted into `Adapter`s so that `ListView`s can display them properly.  Nearly any value can be passed into a `BoolConverter` in order to transform it into a visibility or a boolean value for toggling a button.

If the built-in converters are insufficient, you can write your own by extending the `ValueConverter` class.  This class has two methods you can override (`convertToSource()` and `convertToTarget()`), one for each direction of the conversion.  In the case of UI bindings, the "target" will always be the piece of UI being bound, and the "source" will be your model object.  The remainder of the conversion is entirely up to you.


Adapting Existing Classes for Trackability
------------------------------------------

You may have existing classes that you want to expose as trackable objects that can be bound to using Bindroid.  This process requires a little instrumentation, but is relatively straightforward.  To set up your objects for trackability, you will use the `Trackable` class.  Any time an accessor to your object is called, you must call `trackable.track()`.  Whenever the value of the thing being accessed changes, you must call `trackable.notifyTrackers()`.  Once you've done that, these values can easily be bound using Bindroid.

For example, let's suppose we have an existing model object (`Foo`) using a legacy change notification mechanism.  Your ViewModel that wraps this model would then look like this:

```java
public class FooViewModel {
  private FooModel model;
  private Trackable trackable = new Trackable();
  public FooViewModel(FooModel model) {
    this.model = model;
    model.addChangeListener(new ChangeListener() {
      public void propertyChanged() {
        trackable.notifyTrackers();
      }
    });
  }

  // Each wrapped property would need to call track()
  public String getBar() {
    trackable.track();
    return model.getBar();
  }
  public void setBar(String value) {
    model.setBar(value);
  }

  // Alternatively, you can directly expose the model, but call track()
  // on the way in so that any listeners will know to listen for change
  // notifications
  public FooModel getFoo() {
    trackable.track();
    return model;
  }
}
```

If you view the source for `EditTextTextProperty`, you will see a similar adaptation of an existing property change notification mechanism (in this case the built-in `EditText` listener) to a `Trackable` so that it can be used by `Binding`s.

Memory Management and Threading
-------------------------------

One of the biggest potential pitfalls of of using observation patterns between UI and your model objects is that it's extremely easy to inadvertently leak your entire UI hierarchy.  Bindroid takes out most of the guess-work here.  If you use `UiBinder` to set up your bindings, they will hold only weak references to your UI, allowing the UI to be garbage collected even while it awaits change notifications from your model.  On the other hand, `UiBinder` will ensure that you model object stays alive as long as any UI is listening for its changes.

Another common nuisance when working with UI is that changes that occur on background threads must notify the UI on the main thread.  If you use `UiBinder` to set up your bindings, `Bindroid` will take care of this for you, dispatching change notifications to the main thread (only when necessary) to keep your UI up-to-date.

Other Uses
----------

While Bindroid's main goal is to ease UI development, its components are built to provide general binding of any two properties on any two objects.  It can be safely used as an adapter between objects of all sorts, setting them up to track and notify each other whenever changes occur.  Please don't hesitate to explore the `Binding` class if you're curious about how to make use of this functionality.

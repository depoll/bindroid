package com.bindroid.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.EditText;

import com.bindroid.BindingMode;
import com.bindroid.converters.AdapterConverter;
import com.bindroid.ui.EditTextTextProperty;
import com.bindroid.ui.UiBinder;

public class MainActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    ViewModel model = new ViewModel();

    UiBinder.bind(new EditTextTextProperty((EditText) this.findViewById(R.id.TextField)), model,
        "StringValue", BindingMode.TwoWay);
    UiBinder.bind(this, R.id.TextView, "Text", model, "StringValue", BindingMode.OneWay);
    UiBinder.bind(this, R.id.ListView, "Adapter", model, "Dates", BindingMode.OneWay,
        new AdapterConverter(DateView.class));
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.activity_main, menu);
    return true;
  }

}

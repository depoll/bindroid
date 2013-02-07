package com.bindroid.sample;

import java.util.Date;

import android.content.Context;
import android.widget.TextView;

import com.bindroid.BindingMode;
import com.bindroid.TrackableField;
import com.bindroid.converters.ToStringConverter;
import com.bindroid.ui.BoundUi;
import com.bindroid.ui.UiBinder;
import com.bindroid.ui.UiProperty;
import com.bindroid.utils.ReflectedProperty;

public class DateView extends TextView implements BoundUi<Date> {

  private TrackableField<Date> data = new TrackableField<Date>();

  public DateView(Context context) {
    super(context);
    UiBinder.bind(UiProperty.make(new ReflectedProperty(this, "Text")), this, "Date",
        BindingMode.ONE_WAY, new ToStringConverter("Added at: %s"));
  }

  @Override
  public void bind(Date dataSource) {
    data.setValue(dataSource);
  }

  public Date getDate() {
    return data.getValue();
  }
}

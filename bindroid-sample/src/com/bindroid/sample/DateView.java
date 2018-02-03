package com.bindroid.sample;

import android.content.Context;
import android.widget.TextView;

import com.bindroid.BindingMode;
import com.bindroid.converters.ToStringConverter;
import com.bindroid.trackable.TrackableField;
import com.bindroid.ui.BoundUi;
import com.bindroid.ui.UiBinder;
import com.bindroid.ui.UiProperty;
import com.bindroid.utils.ReflectedProperty;

import java.util.Date;

public class DateView extends TextView implements BoundUi<Date> {

    private TrackableField<Date> data = new TrackableField<Date>();

    public DateView(Context context) {
        super(context);
        UiBinder.bind(UiProperty.make(new ReflectedProperty(this, "Text")), this, "Date",
                BindingMode.ONE_WAY, new ToStringConverter("Added at: %s"));
    }

    @Override
    public void bind(Date dataSource) {
        data.set(dataSource);
    }

    public Date getDate() {
        return data.get();
    }
}

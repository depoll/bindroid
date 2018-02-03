package com.bindroid.sample;

import android.os.Handler;
import android.os.Looper;

import com.bindroid.trackable.TrackableCollection;
import com.bindroid.trackable.TrackableField;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class ViewModel {
    private TrackableField<String> stringValue = new TrackableField<String>("Hello, world!");
    private TrackableField<TrackableCollection<Date>> dates = new TrackableField<TrackableCollection<Date>>();

    public ViewModel() {
        dates.set(new TrackableCollection<Date>());
        final Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        getDates().add(0, new Date());
                        if (getCount() > 20) {
                            getDates().remove(getCount() - 1);
                        }
                    }
                });
            }
        }, new Date(), 2000);
    }

    public String getStringValue() {
        return stringValue.get();
    }

    public void setStringValue(String value) {
        stringValue.set(value);
    }

    public TrackableCollection<Date> getDates() {
        return dates.get();
    }

    public int getCount() {
        return getDates().size();
    }

    public int getTextLength() {
        return getStringValue().length();
    }

    public int getCountPlusTextLength() {
        return getCount() + getTextLength();
    }

    public boolean getCountIsEven() {
        return getCount() % 2 == 0;
    }
}

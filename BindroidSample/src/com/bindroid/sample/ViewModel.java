package com.bindroid.sample;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.bindroid.ObservableCollection;
import com.bindroid.TrackableField;

public class ViewModel {
  private TrackableField<String> stringValue = new TrackableField<String>("Hello, world!");
  private TrackableField<ObservableCollection<Date>> dates = new TrackableField<ObservableCollection<Date>>();

  public ViewModel() {
    dates.setValue(new ObservableCollection<Date>());
    final Timer timer = new Timer();
    timer.scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run() {
        getDates().add(0, new Date());
      }
    }, new Date(), 1000);
  }

  public String getStringValue() {
    return stringValue.getValue();
  }

  public void setStringValue(String value) {
    stringValue.setValue(value);
  }

  public ObservableCollection<Date> getDates() {
    return dates.getValue();
  }
}

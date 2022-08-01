package com.example.MyHealth;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MyXAxisValueFormatter extends ValueFormatter implements IAxisValueFormatter {


    // private String[] mValues;

    //  SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

    //   public void MyXAxisValueFormatter(String[] values) {
    //      this.mValues = values; }

    //   @Override
//    public String getFormattedValue(float value, AxisBase axis) {
    //       return sdf.format(new Date((long) value));
    //  }


    /*  @Override
      public String getFormattedValue(float value) {

          // Convert float value to date string
          // Convert from seconds back to milliseconds to format time  to show to the user
          long emissionsMilliSince1970Time = TimeUnit.DAYS.toMillis((long)value);

          // Show time in local version
          Date timeMilliseconds = new Date(emissionsMilliSince1970Time);
          SimpleDateFormat dateTimeFormat = new SimpleDateFormat("MM-dd");

          return dateTimeFormat.format(timeMilliseconds);
      }
  }*/

    class DateAxisValueFormatter implements IAxisValueFormatter {
        private String[] mValues;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd:hh:mm:ss");

        public DateAxisValueFormatter(String[] values) {
            this.mValues = values; }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            Date d = new Date(Float.valueOf(value).longValue());
            String date = new SimpleDateFormat("dd-MM", Locale.ENGLISH).format(d);

            return date.format(String.valueOf(new Date((long) value)));
        }
    }
}
//}
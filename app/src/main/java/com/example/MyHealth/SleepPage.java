package com.example.MyHealth;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;

import java.util.ArrayList;

public class SleepPage extends AppCompatActivity {
    BarChart sleepBarChart;

    DBHelper DB = new DBHelper(this);
    ArrayList<String> sleeplistitem;
    SimpleCursorAdapter sleepadapter;

    final  String[] sleepfrom = new String[]{DB.SLEEPGRAPHDATE,DB.SLEEPTOTAL,DB.SLEEPSCORE,};
    final  int[] sleepto = new int[]{R.id.txtSleepdate,R.id.txtSleepdur,R.id.txtsleepscorepage};

    ListView lvSleepdata;
    TextView scoretext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_page);

        sleeplistitem = new ArrayList<>();
        Cursor cursor = DB.getsleepdata();
        lvSleepdata = findViewById(R.id.listvsleepdata);
        sleepadapter = new SimpleCursorAdapter(this,R.layout.sleepdata_item,cursor,sleepfrom,sleepto,0);
        scoretext = findViewById(R.id.txtPageSleepScore);
        lvSleepdata.setAdapter(sleepadapter);
        scoretext.setText(DB.getsleepscore());
    }
}
package com.example.MyHealth;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.github.mikephil.charting.charts.BarChart;

import java.util.ArrayList;

public class SPO2page extends AppCompatActivity {
    BarChart spo2chart;
    DBHelper DB = new DBHelper(this);

    ArrayList<String> spo2listitem;
    SimpleCursorAdapter spo2adapter;

    ListView lvspo2data;

    final  String[] spo2from = new String[]{DB.SPODATE ,DB.SPOTIME,DB.BLOODOX};
    final  int[] spoto = new int[]{R.id.txtSPO2Date,R.id.txtSPO2Day,R.id.txtspoval};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spo2page);

        spo2listitem = new ArrayList<>();
        Cursor spocursor = DB.getbloodox();
        lvspo2data = findViewById(R.id.listvSPO2data);
        spo2adapter = new SimpleCursorAdapter(this, R.layout.spo2data_item,spocursor,spo2from, spoto, 0);

        lvspo2data.setAdapter(spo2adapter);
    }
}
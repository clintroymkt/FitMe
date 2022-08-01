package com.example.MyHealth;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class BPpage extends AppCompatActivity {
 DBHelper DB = new DBHelper(this);

 ArrayList<String> BPlistitem;
         CursorAdapter BPadpter;

 ListView lvBPdata;

 final String[] BPfrom = new String[]{DB.BPDATE,DB.BPTIME,DB.SYSDIAL};
 final int[] BPto = new int[]{R.id.txtBPDate,R.id.txtBPtime,R.id.txtsysdial1};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bppage);

        BPlistitem = new ArrayList<>();
        Cursor bpcursor = DB.getBP();
        lvBPdata = findViewById(R.id.listvBPdata);
        BPadpter = new SimpleCursorAdapter(this,R.layout.bpdata_item,bpcursor,BPfrom,BPto, 0);

        lvBPdata.setAdapter(BPadpter);
    }
}
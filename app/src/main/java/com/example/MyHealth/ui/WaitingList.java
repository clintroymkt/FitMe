package com.example.MyHealth.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.MyHealth.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class WaitingList extends AppCompatActivity {

    ListView listView;
    public ExtendedFloatingActionButton fullListBtn;


    String[] patientName = {"Patient 1", "Patient 2","Patient 3", "Patient 4","Patient 5"};
    int[] patientImage = {R.drawable.ic_patient,R.drawable.ic_patient,R.drawable.ic_patient,R.drawable.ic_patient,R.drawable.ic_patient};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_list);

        fullListBtn = findViewById(R.id.viewpatientflt);

        listView = findViewById(R.id.patientList);

        WaitingList.CustomAdapter customAdapter = new WaitingList.CustomAdapter();

        listView.setAdapter(customAdapter);

        fullListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WaitingList.this, PatientList.class);
                startActivity(intent);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), PatientDetails.class);
                startActivity(intent);
            }
        });
    }

    private class CustomAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return patientImage.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view1 = getLayoutInflater().inflate(R.layout.patrow,null);

            TextView name = view1.findViewById(R.id.patientname);
            ImageView image = view1.findViewById(R.id.patienticon);

            name.setText(patientName[position]);
            image.setImageResource(patientImage[position]);

            return view1;
        }
    }
}
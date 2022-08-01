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

public class hospitalList extends AppCompatActivity {
    ListView listView;


    String[] hospitalName = {"Hospital 1", "Hospital 2","Hospital 3", "Hospital 4","Hospital 5"};
    String[] hospitalAdd = {"Address 1", "Address 2","Address 3", "Address 4","Address 5"};
    int[] hospitalImage = {R.drawable.ic_patient,R.drawable.ic_patient,R.drawable.ic_patient,R.drawable.ic_patient,R.drawable.ic_patient};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_list);

        listView = findViewById(R.id.hospitalList);

        hospitalList.CustomAdapter customAdapter = new hospitalList.CustomAdapter();

        listView.setAdapter(customAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), HospComment.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private class CustomAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return hospitalImage.length;
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
            View view1 = getLayoutInflater().inflate(R.layout.hosprow,null);

            TextView name = view1.findViewById(R.id.hospitalname);
            TextView address = view1.findViewById(R.id.hospAddress);
            ImageView image = view1.findViewById(R.id.patienticon);

            name.setText(hospitalName[position]);
            address.setText(hospitalAdd[position]);
            image.setImageResource(hospitalImage[position]);

            return view1;
        }
    }
}
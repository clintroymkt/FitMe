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

import com.example.MyHealth.CallCenterActivity;
import com.example.MyHealth.R;

public class DocDirectory extends AppCompatActivity {
    ListView doclistView;
    String[] doctorName = {"MyHealth", "Doctor 1","Service 1", "Doctor 2","Doctor 6"};
    String[] docspecialty = {"Call Center", "Specialty","Specialty", "Specialty","Specialty"};
    int[] doctorImage = {R.drawable.myhealthmini,R.drawable.ic_patient,R.drawable.ic_patient,R.drawable.ic_patient,R.drawable.ic_patient};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_directory);

        doclistView = findViewById(R.id.referList);

        DocDirectory.CustomAdapter customAdapter = new DocDirectory.CustomAdapter();

        doclistView.setAdapter(customAdapter);

        doclistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position==0){
                    Intent intent = new Intent(getApplicationContext(), CallCenterActivity.class);
                    startActivity(intent);

                }else if (position==1){
                    Intent intent = new Intent(getApplicationContext(), ReferList.class);
                    startActivity(intent);

                }else if (position==2){

                }else if (position==3){

                }else if (position==4){

                }
            }
        });
    }
    private class CustomAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return doctorImage.length;
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
            View view1 = getLayoutInflater().inflate(R.layout.docrow,null);

            TextView name = view1.findViewById(R.id.doctorname);
            TextView specialty = view1.findViewById(R.id.txtspecialty);
            ImageView image = view1.findViewById(R.id.patienticon);

            name.setText(doctorName[position]);
            specialty.setText(docspecialty[position]);
            image.setImageResource(doctorImage[position]);

            return view1;
        }
    }
}
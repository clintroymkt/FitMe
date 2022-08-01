package com.example.MyHealth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.MyHealth.ui.DocDirectory;

public class CallCenterActivity extends AppCompatActivity {
    Button whatsappBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_center);


        whatsappBtn = (Button) findViewById(R.id.callWABtn);

        whatsappBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { openWhatsapp();

            }
        });
    }

    private void openWhatsapp() {
        String wpurl= "https://wa.me/+263772716947?text=Hello, \n I require assistance";
        Intent intent= new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(wpurl));
        startActivity(intent);
    }
}
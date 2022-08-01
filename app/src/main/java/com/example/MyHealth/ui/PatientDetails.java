package com.example.MyHealth.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.MyHealth.R;
import com.google.android.material.navigation.NavigationView;

public class PatientDetails extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

 //private Button consultBtn;
 private Button newConsult;
 private Button vitalsArch;
 private DrawerLayout patientdrawer;
 NavigationView navigationView;
 Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_details);

        vitalsArch = (Button) findViewById(R.id.btnVitalsArch);

        newConsult = (Button) findViewById(R.id.newConsult);

        patientdrawer = findViewById(R.id.patient_drawer);

        navigationView = findViewById(R.id.nav_view);

        toolbar = findViewById(R.id.toolbarx);
        
        
        setSupportActionBar(toolbar);

        Menu menu = navigationView.getMenu();


        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,patientdrawer, toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        patientdrawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(R.id.nav_dashboard);
        
        vitalsArch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openVitalsarch();
            }
        });


        newConsult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openConsult();
            }
        });



    }

    private void openVitalsarch() {
        Intent intent = new Intent(this, VitalsArch.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {

        if (patientdrawer.isDrawerOpen(GravityCompat.START)){
            patientdrawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }

    }





    private void openConsult() {
        Intent intent = new Intent(this, ConsultancyAdmin.class);
        startActivity(intent);
    }




    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.nav_dashboard:
                break;
            case R.id.nav_sysrev:
                Intent intent = new Intent(PatientDetails.this,SystemicRev.class);
                startActivity(intent);

                break;

            case R.id.nav_medhist:
                Intent intent1 = new Intent(PatientDetails.this,PatientHistory.class);
                startActivity(intent1);

                break;

            case R.id.nav_complaints:
                Intent intent6 = new Intent(PatientDetails.this, Complaints.class);
                startActivity(intent6);
                break;

            case R.id.nav_physex:
                Intent intent2 = new Intent(PatientDetails.this,PhysExam.class);
                startActivity(intent2);

                break;
            case R.id.nav_summary:
                Intent intent7 = new Intent(PatientDetails.this, SummaryActivity.class);
                startActivity(intent7);

                break;

            case R.id.nav_diagnosis:
                Intent intent3 = new Intent(PatientDetails.this,Diagnosis.class);
                startActivity(intent3);

                break;

            case R.id.nav_management:
                Intent intent4 = new Intent(PatientDetails.this,Management.class);
                startActivity(intent4);

                break;
            case R.id.nav_billing:
                Intent intent5 = new Intent(PatientDetails.this, Billing.class);
                startActivity(intent5);
                
                break;
        }

        patientdrawer.closeDrawer(GravityCompat.START);

        return true;
    }
}
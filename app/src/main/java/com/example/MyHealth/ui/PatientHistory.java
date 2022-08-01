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

public class PatientHistory extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Button famhistbtn;
    private Button pasthistbtn;
    private DrawerLayout patientdrawer;
    NavigationView navigationView;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_history);

        patientdrawer = findViewById(R.id.patient_drawer);

        famhistbtn = (Button)findViewById(R.id.btnfamHist);

        pasthistbtn = (Button)findViewById(R.id.btnpastHist);

        navigationView = findViewById(R.id.nav_view);

        toolbar = findViewById(R.id.toolbarx);

        setSupportActionBar(toolbar);

        Menu menu = navigationView.getMenu();


        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,patientdrawer, toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        patientdrawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(R.id.nav_medhist);

        famhistbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openfamhist();
            }
        });

        pasthistbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openpasthist();
            }
        });

    }

    private void openpasthist() {
        Intent intent = new Intent(this, PastHistory.class);
        startActivity(intent);
    }

    private void openfamhist() {
        Intent intent = new Intent(this, FamHistory.class);
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


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.nav_dashboard:
                Intent intent = new Intent(PatientHistory.this,PatientDetails.class);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_sysrev:
                Intent intent2 = new Intent(PatientHistory.this,SystemicRev.class);
                startActivity(intent2);
                finish();
                break;

            case R.id.nav_medhist:
                break;

            case R.id.nav_summary:
                Intent intent7 = new Intent(PatientHistory.this, SummaryActivity.class);
                startActivity(intent7);
                finish();
                break;

            case R.id.nav_physex:
                Intent intent5 = new Intent(PatientHistory.this,PhysExam.class);
                startActivity(intent5);
                finish();
                break;

            case R.id.nav_diagnosis:
                Intent intent3 = new Intent(PatientHistory.this,Diagnosis.class);
                startActivity(intent3);
                finish();
                break;

            case R.id.nav_management:
                Intent intent4 = new Intent(PatientHistory.this,Management.class);
                startActivity(intent4);
                finish();
                break;
            case R.id.nav_billing:
                Intent intent6 = new Intent(PatientHistory.this,Billing.class);
                startActivity(intent6);
                finish();
                break;
        }

        patientdrawer.closeDrawer(GravityCompat.START);

        return true;
    }
}
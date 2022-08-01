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

import com.example.MyHealth.R;
import com.google.android.material.navigation.NavigationView;

public class PhysExam extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout patientdrawer;
    NavigationView navigationView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phys_exam);

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

        navigationView.setCheckedItem(R.id.nav_physex);

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
                Intent intent = new Intent(PhysExam.this,PatientDetails.class);
                startActivity(intent);
                finish();
                break;

            case R.id.nav_complaints:
                Intent intent6 = new Intent(PhysExam.this, Complaints.class);
                startActivity(intent6);
                finish();
                break;

            case R.id.nav_sysrev:
                Intent intent2 = new Intent(PhysExam.this,SystemicRev.class);
                startActivity(intent2);
                finish();
                break;

            case R.id.nav_medhist:
                Intent intent1 = new Intent(PhysExam.this,PatientHistory.class);
                startActivity(intent1);
                finish();
                break;

            case R.id.nav_summary:
                Intent intent7 = new Intent(PhysExam.this, SummaryActivity.class);
                startActivity(intent7);
                finish();
                break;

            case R.id.nav_physex:
                break;

            case R.id.nav_diagnosis:
                Intent intent3 = new Intent(PhysExam.this,Diagnosis.class);
                startActivity(intent3);
                finish();
                break;

            case R.id.nav_management:
                Intent intent4 = new Intent(PhysExam.this,Management.class);
                startActivity(intent4);
                finish();
                break;
        }

        patientdrawer.closeDrawer(GravityCompat.START);

        return true;
    }
}
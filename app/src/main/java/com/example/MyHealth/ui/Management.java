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

public class Management extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout patientdrawer;
    private Button reqConsultbtn;
   // private Button mngmntplan;
    private Button revButton;
    private Button admitButton;
    private Button referBtn;

    NavigationView navigationView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management);

      //  mngmntplan = (Button) findViewById(R.id.btncreatePlan);

        referBtn = (Button) findViewById(R.id.referPatient);

        admitButton = (Button) findViewById(R.id.admitPatient);

        revButton = (Button) findViewById(R.id.btn_revFile);

        reqConsultbtn= (Button) findViewById(R.id.reqConsult);

        patientdrawer = findViewById(R.id.patient_drawer);

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

        navigationView.setCheckedItem(R.id.nav_management);

        referBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openReferList();
            }
        });

        admitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHospital();
            }
        });

        revButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openReview();
            }
        });

        reqConsultbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openOneTime();
            }
        });

      //  mngmntplan.setOnClickListener(new View.OnClickListener() {
     //       @Override
     //       public void onClick(View v) {
     //           openMngmntPlan();
     //       }
    //    });
    }

    private void openReferList() {
        Intent intent = new Intent(this, ReferList.class);
        startActivity(intent);
    }

    private void openHospital() {
        Intent intent = new Intent(this, hospitalList.class);
        startActivity(intent);
    }

    private void openReview() {
        Intent intent = new Intent(this, Review.class);
        startActivity(intent);
    }

    private void openMngmntPlan() {
        Intent intent = new Intent(this, ManagementPlan.class);
        startActivity(intent);
    }

    private void openOneTime() {
        Intent intent = new Intent(this, OneTimeActivity.class);
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
                Intent intent = new Intent(Management.this, PatientDetails.class);
                startActivity(intent);
                finish();
                break;

            case R.id.nav_complaints:
                Intent intent6 = new Intent(Management.this, Complaints.class);
                startActivity(intent6);
                finish();
                break;

            case R.id.nav_sysrev:
                Intent intent2 = new Intent(Management.this,SystemicRev.class);
                startActivity(intent2);
                finish();
                break;

            case R.id.nav_medhist:
                Intent intent1 = new Intent(Management.this,PatientHistory.class);
                startActivity(intent1);
                finish();
                break;

            case R.id.nav_summary:
                Intent intent7 = new Intent(Management.this, SummaryActivity.class);
                startActivity(intent7);
                finish();
                break;

            case R.id.nav_physex:
                Intent intent4 = new Intent(Management.this,PhysExam.class);
                startActivity(intent4);
                finish();
                break;

            case R.id.nav_diagnosis:
                Intent intent3 = new Intent(Management.this,Diagnosis.class);
                startActivity(intent3);
                finish();
                break;

            case R.id.nav_management:
                break;

            case R.id.nav_billing:
                Intent intent5 = new Intent(Management.this,Billing.class);
                startActivity(intent5);
                finish();
                break;
        }

        patientdrawer.closeDrawer(GravityCompat.START);

        return true;
    }
}
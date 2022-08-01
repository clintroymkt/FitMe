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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.MyHealth.R;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class SystemicRev extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout patientdrawer;
    private Spinner systemSpinner;
    private Button button1;



    private Button button2;
    private Button button3;
    private Button button4;
    private Button button5;
    private Button button6;
    private Button button7;
    private Button button8;
    private Button button9;
    private Button button10;
    private Button button11;
    private Button button12;

    NavigationView navigationView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_systemic_rev);



        button1 = (Button)findViewById(R.id.assesBtn1);

       button2 = (Button)findViewById(R.id.assesBtn2);

        button3 = (Button)findViewById(R.id.assesBtn3);

        button4 = (Button)findViewById(R.id.assesBtn4);

        button5 = (Button)findViewById(R.id.assesBtn5);

       button6 = (Button)findViewById(R.id.assesBtn6);

        button7 = (Button)findViewById(R.id.assesBtn7);

        button8 = (Button)findViewById(R.id.assesBtn8);

        button9 = (Button)findViewById(R.id.assesBtn9);

        button10 = (Button)findViewById(R.id.assesBtn10);

        button11 = (Button)findViewById(R.id.assesBtn11);

        button12 = (Button)findViewById(R.id.assesBtn12);


        systemSpinner = findViewById(R.id.systemSpinner);

        patientdrawer = findViewById(R.id.patient_drawer);

        navigationView = findViewById(R.id.nav_view);

        toolbar = findViewById(R.id.toolbarx);

        setSupportActionBar(toolbar);

        Menu menu = navigationView.getMenu();

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAssement();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAssement();
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAssement();
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAssement();
            }
        });

        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAssement();
            }
        });

        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAssement();
            }
        });

        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAssement();
            }
        });

        button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAssement();
            }
        });

        button9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAssement();
            }
        });

        button10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAssement();
            }
        });

        button11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAssement();
            }
        });

        button12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAssement();
            }
        });



        List<String> systems = new ArrayList<>();
        systems.add(0, "System");
        systems.add("General");
        systems.add("GIT");
        systems.add("Cardio-Pulmonary");
        systems.add("CNS");
        systems.add("Genito-Urinary");
        systems.add("Musculo-Skeletal");
        systems.add("Skin");


        ArrayAdapter<String> dataAdapter;
        dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,systems );

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        systemSpinner.setAdapter(dataAdapter);

        systemSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).equals("System"))
                {

                }
                else
                {
                    String item = parent.getItemAtPosition(position).toString();

                    if (parent.getItemAtPosition(position).equals("General"))
                    {
                        String feverstring = "Fever";
                        String weightstring = "Weight Change";
                        String fatiguestring = "Fatigue";
                        String blankstring = "         ";

                        TextView feverView = (TextView) findViewById(R.id.subsys1);
                        feverView.setText(feverstring);

                        TextView weightView = (TextView) findViewById(R.id.subsys2);
                        weightView.setText(weightstring);

                        TextView fatigueView = (TextView) findViewById(R.id.subsys3);
                        fatigueView.setText(fatiguestring);

                        TextView blankView = (TextView) findViewById(R.id.subsys4);
                        blankView.setText(blankstring);

                        TextView blankView1 = (TextView) findViewById(R.id.subsys5);
                        blankView1.setText(blankstring);

                        TextView blankView2 = (TextView) findViewById(R.id.subsys6);
                        blankView2.setText(blankstring);

                        TextView blankView3 = (TextView) findViewById(R.id.subsys7);
                        blankView3.setText(blankstring);

                        TextView blankView4 = (TextView) findViewById(R.id.subsys8);
                        blankView4.setText(blankstring);

                        TextView blankView5 = (TextView) findViewById(R.id.subsys9);
                        blankView5.setText(blankstring);

                        TextView blankView6 = (TextView) findViewById(R.id.subsys10);
                        blankView6.setText(blankstring);

                        TextView blankView7 = (TextView) findViewById(R.id.subsys11);
                        blankView7.setText(blankstring);
                    }

                    else if (parent.getItemAtPosition(position).equals("GIT"))
                    {
                        String dysstring = "Dsyphagia";
                        String regurgstring = "Weight Change";
                        String appetitestring = "Fatigue";
                        String nauseastring = "Nausea";
                        String vomitstring = "Vomiting";
                        String abdpainstring = "Abdominal Pain";
                        String abdswellstring = "Abdominal Swelling";
                        String diarrheastring = "Diarrhea";
                        String constipstring = "Constipation";
                        String jaundstring = "Jaundice";
                        String bleedstring = "Bleeding";

                        TextView dysView = (TextView) findViewById(R.id.subsys1);
                        dysView.setText(dysstring);

                        TextView regurgView = (TextView) findViewById(R.id.subsys2);
                        regurgView.setText(regurgstring);

                        TextView appetiteView = (TextView) findViewById(R.id.subsys3);
                        appetiteView.setText(appetitestring);

                        TextView nauseaView = (TextView) findViewById(R.id.subsys4);
                        nauseaView.setText(nauseastring);

                        TextView vomitView = (TextView) findViewById(R.id.subsys5);
                        vomitView.setText(vomitstring);

                        TextView abdpainView = (TextView) findViewById(R.id.subsys6);
                        abdpainView.setText(abdpainstring);

                        TextView abdswellView = (TextView) findViewById(R.id.subsys7);
                        abdswellView.setText(abdswellstring);

                        TextView diarrheaView = (TextView) findViewById(R.id.subsys8);
                        diarrheaView.setText(diarrheastring);

                        TextView constipView = (TextView) findViewById(R.id.subsys9);
                        constipView.setText(constipstring);

                        TextView jaundView = (TextView) findViewById(R.id.subsys10);
                        jaundView.setText(jaundstring);

                        TextView bleedView = (TextView) findViewById(R.id.subsys11);
                        bleedView.setText(bleedstring);
                    }

                    else if (parent.getItemAtPosition(position).equals("Cardio-Pulmonary"))
                    {
                        String coughstring = "Cough";
                        String sputumstring = "Sputum";
                        String sobstring = "SOB";
                        String palpstring = "Palpitation";
                        String syncopestring = "Syncope";
                        String wheezestring = "Wheeze";
                        String cyanosisstring = "Cyanosis";
                        String chestpstring = "Chest Pain";
                        String llsstring = "Lower Limb Swelling";
                        String blankstring = "         ";


                        TextView coughView = (TextView) findViewById(R.id.subsys1);
                        coughView.setText(coughstring);

                        TextView sputumView = (TextView) findViewById(R.id.subsys2);
                        sputumView.setText(sputumstring);

                        TextView sobView = (TextView) findViewById(R.id.subsys3);
                        sobView.setText(sobstring);

                        TextView palpView = (TextView) findViewById(R.id.subsys4);
                        palpView.setText(palpstring);

                        TextView syncopeView = (TextView) findViewById(R.id.subsys5);
                        syncopeView.setText(syncopestring);

                        TextView wheezeView = (TextView) findViewById(R.id.subsys6);
                        wheezeView.setText(wheezestring);

                        TextView cyanosisView = (TextView) findViewById(R.id.subsys7);
                        cyanosisView.setText(cyanosisstring);

                        TextView chestpView = (TextView) findViewById(R.id.subsys8);
                        chestpView.setText(chestpstring);

                        TextView llsView = (TextView) findViewById(R.id.subsys9);
                        llsView.setText(llsstring);

                        TextView blankView = (TextView) findViewById(R.id.subsys10);
                        blankView.setText(blankstring);

                        TextView blankView1 = (TextView) findViewById(R.id.subsys11);
                        blankView1.setText(blankstring);


                    }
                    else if (parent.getItemAtPosition(position).equals("CNS"))
                    {
                        String higherfstring = "Higher Functions";
                        String cranialnstring = "Cranial Nerves";
                        String weaknessstring = "Weakness";
                        String abnormalmstring = "Abnormal Movement";
                        String headachestring = "Headache";
                        String blankstring = "         ";



                        TextView higherfView = (TextView) findViewById(R.id.subsys1);
                        higherfView.setText(higherfstring);

                        TextView cranialnView = (TextView) findViewById(R.id.subsys2);
                        cranialnView.setText(cranialnstring);

                        TextView weaknessView = (TextView) findViewById(R.id.subsys3);
                        weaknessView.setText(weaknessstring);

                        TextView abnormalmView = (TextView) findViewById(R.id.subsys4);
                        abnormalmView.setText(abnormalmstring);

                        TextView headacheView = (TextView) findViewById(R.id.subsys5);
                        headacheView.setText(headachestring);

                        TextView blankView2 = (TextView) findViewById(R.id.subsys6);
                        blankView2.setText(blankstring);

                        TextView blankView3 = (TextView) findViewById(R.id.subsys7);
                        blankView3.setText(blankstring);

                        TextView blankView4 = (TextView) findViewById(R.id.subsys8);
                        blankView4.setText(blankstring);

                        TextView blankView5 = (TextView) findViewById(R.id.subsys9);
                        blankView5.setText(blankstring);

                        TextView blankView6 = (TextView) findViewById(R.id.subsys10);
                        blankView6.setText(blankstring);

                        TextView blankView7 = (TextView) findViewById(R.id.subsys11);
                        blankView7.setText(blankstring);
                    }

                    else if (parent.getItemAtPosition(position).equals("Genito-Urinary"))
                    {
                        String painstring = "Pain";
                        String polyuriastring = "Polyuria";
                        String dysuriastring = "Dsyuria";
                        String hurstring = "Hesitancy/Urgency/Retention";
                        String incontinencestring = "Incontinence";
                        String dischargestring = "Incontinence";
                        String blankstring = "         ";



                        TextView painView = (TextView) findViewById(R.id.subsys1);
                        painView.setText(painstring);

                        TextView polyuriaView = (TextView) findViewById(R.id.subsys2);
                        polyuriaView.setText(polyuriastring);

                        TextView dysuriaView = (TextView) findViewById(R.id.subsys3);
                        dysuriaView.setText(dysuriastring);

                        TextView hurView = (TextView) findViewById(R.id.subsys4);
                        hurView.setText(hurstring);

                        TextView incontinenceView = (TextView) findViewById(R.id.subsys5);
                        incontinenceView.setText(incontinencestring);

                        TextView dischargeView = (TextView) findViewById(R.id.subsys6);
                        dischargeView.setText(dischargestring);



                        TextView blankView3 = (TextView) findViewById(R.id.subsys7);
                        blankView3.setText(blankstring);

                        TextView blankView4 = (TextView) findViewById(R.id.subsys8);
                        blankView4.setText(blankstring);

                        TextView blankView5 = (TextView) findViewById(R.id.subsys9);
                        blankView5.setText(blankstring);

                        TextView blankView6 = (TextView) findViewById(R.id.subsys10);
                        blankView6.setText(blankstring);

                        TextView blankView7 = (TextView) findViewById(R.id.subsys11);
                        blankView7.setText(blankstring);
                    }
                    else if (parent.getItemAtPosition(position).equals("Musculo-Skeletal"))
                    {
                        String bjmpainstring = "Bone/Joint/Muscle Pain";
                        String jmswellingstring = "Joing/Muscle";
                        String blankstring = "         ";




                        TextView bjmpainView = (TextView) findViewById(R.id.subsys1);
                        bjmpainView.setText(bjmpainstring);

                        TextView jmswellView = (TextView) findViewById(R.id.subsys2);
                        jmswellView.setText(jmswellingstring);

                        TextView blankView0 = (TextView) findViewById(R.id.subsys3);
                        blankView0.setText(blankstring);

                        TextView blankView = (TextView) findViewById(R.id.subsys4);
                        blankView.setText(blankstring);

                        TextView blankView1 = (TextView) findViewById(R.id.subsys5);
                        blankView1.setText(blankstring);

                        TextView blankView2 = (TextView) findViewById(R.id.subsys6);
                        blankView2.setText(blankstring);

                        TextView blankView3 = (TextView) findViewById(R.id.subsys7);
                        blankView3.setText(blankstring);

                        TextView blankView4 = (TextView) findViewById(R.id.subsys8);
                        blankView4.setText(blankstring);

                        TextView blankView5 = (TextView) findViewById(R.id.subsys9);
                        blankView5.setText(blankstring);

                        TextView blankView6 = (TextView) findViewById(R.id.subsys10);
                        blankView6.setText(blankstring);

                        TextView blankView7 = (TextView) findViewById(R.id.subsys11);
                        blankView7.setText(blankstring);

                    }
                    else if (parent.getItemAtPosition(position).equals("Skin"))
                    {
                        String skinrstring = "Skin Rash";
                        String itchingstring = "Itching";
                        String hyperpigstring = "Hyper/Hypo Pigmentation";
                        String hirsutismstring = "Hirsutism/Alopecia";
                        String ulcerstring = "Ulcer";
                        String blankstring = "         ";




                        TextView skinrView = (TextView) findViewById(R.id.subsys1);
                        skinrView.setText(skinrstring);

                        TextView itchingView = (TextView) findViewById(R.id.subsys2);
                        itchingView.setText(itchingstring);

                        TextView hyperpigView = (TextView) findViewById(R.id.subsys3);
                        hyperpigView.setText(hyperpigstring);

                        TextView hirsutismView = (TextView) findViewById(R.id.subsys4);
                        hirsutismView.setText(hirsutismstring);

                        TextView ulcerView = (TextView) findViewById(R.id.subsys5);
                        ulcerView.setText(ulcerstring);

                        TextView blankView2 = (TextView) findViewById(R.id.subsys6);
                        blankView2.setText(blankstring);

                        TextView blankView3 = (TextView) findViewById(R.id.subsys7);
                        blankView3.setText(blankstring);

                        TextView blankView4 = (TextView) findViewById(R.id.subsys8);
                        blankView4.setText(blankstring);

                        TextView blankView5 = (TextView) findViewById(R.id.subsys9);
                        blankView5.setText(blankstring);

                        TextView blankView6 = (TextView) findViewById(R.id.subsys10);
                        blankView6.setText(blankstring);

                        TextView blankView7 = (TextView) findViewById(R.id.subsys11);
                        blankView7.setText(blankstring);

                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,patientdrawer, toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        patientdrawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(R.id.nav_sysrev);
    }

    private void openAssement() {
        Intent intent = new Intent(this, AssesmentActivity.class);
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
                Intent intent = new Intent(SystemicRev.this, PatientDetails.class);
                startActivity(intent);
                finish();
                break;

            case R.id.nav_complaints:
                Intent intent6 = new Intent(SystemicRev.this, Complaints.class);
                startActivity(intent6);
                finish();
                break;

            case R.id.nav_sysrev:
                break;

            case R.id.nav_summary:
                Intent intent7 = new Intent(SystemicRev.this, SummaryActivity.class);
                startActivity(intent7);
                finish();
                break;

            case R.id.nav_medhist:
                Intent intent1 = new Intent(SystemicRev.this,PatientHistory.class);
                startActivity(intent1);
                finish();
                break;

            case R.id.nav_physex:
                Intent intent4 = new Intent(SystemicRev.this,PhysExam.class);
                startActivity(intent4);
                finish();
                break;

            case R.id.nav_diagnosis:
                Intent intent2 = new Intent(SystemicRev.this,Diagnosis.class);
                startActivity(intent2);
                finish();
                break;

            case R.id.nav_management:
                Intent intent3 = new Intent(SystemicRev.this,Management.class);
                startActivity(intent3);
                finish();
                break;

            case R.id.nav_billing:
                Intent intent5 = new Intent(SystemicRev.this,Billing.class);
                startActivity(intent5);
                finish();
                break;
        }

        patientdrawer.closeDrawer(GravityCompat.START);

        return true;
    }

}
package com.example.MyHealth.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.MyHealth.CallCenterActivity;
import com.example.MyHealth.FindScreen;
import com.example.MyHealth.PhysWellness;
import com.example.MyHealth.R;
import com.example.MyHealth.fitnessbands.scan.ScanActivity;

import java.util.ArrayList;
import java.util.List;

//patient home screen

public class MainActivity extends AppCompatActivity {
    private RelativeLayout statuscard;
    private RelativeLayout servicescard;
    private ImageButton imageButton;
    private ImageButton logoutButton;
    private CardView doclistbtn;
    private CardView devices;
    private CardView wellnesscard;
    private ImageSlider imageSlider;
    private ImageSlider wellnessSlider;
    private RelativeLayout sliderlayout;
    private EditText searchbox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sliderlayout = findViewById(R.id.sliderlayout);

         imageSlider = findViewById(R.id.findslider);
         searchbox = findViewById(R.id.searchbox);



        List<SlideModel> slideModels= new ArrayList<>();

        slideModels.add(new SlideModel(R.drawable.doctor150));
        slideModels.add(new SlideModel(R.drawable.clinic150));
        slideModels.add(new SlideModel(R.drawable.pharma150));
        slideModels.add(new SlideModel(R.drawable.lab150));


        wellnessSlider = findViewById(R.id.wellnessslider);


        List<SlideModel> wellnessModel = new ArrayList<>();

        wellnessModel.add(new SlideModel(R.drawable.howareuemonew));

        wellnessModel.add(new SlideModel(R.drawable.physhealtnew));
        wellnessModel.add(new SlideModel(R.drawable.creatornew));
        wellnessModel.add(new SlideModel(R.drawable.physicalhealthnew));
        wellnessModel.add(new SlideModel(R.drawable.financialhealthnew));
        wellnessModel.add(new SlideModel(R.drawable.mentalhealthnew));
        wellnessModel.add(new SlideModel(R.drawable.explorenew));

        wellnessSlider.setImageList(wellnessModel,true);


                imageSlider.setImageList(slideModels, true);
      //  wellnesscard = findViewById(R.id.wellnesscv);
       // imageButton = (ImageButton) findViewById(R.id.virtual_Consult);
        statuscard = findViewById(R.id.statuscard);
       // servicescard = findViewById(R.id.servicescard);
        logoutButton = (ImageButton) findViewById(R.id.imageMenu);
        doclistbtn = findViewById(R.id.virtdoccard);
        devices =  findViewById(R.id.devicescard);

      //  imageButton.setOnClickListener(new View.OnClickListener() {
       //     @Override
      //      public void onClick(View v) {
     //           openConsultancy();
     //       }
     //   });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { openLogin();

            }
        });

        doclistbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openConsultancy();
            }
        });

       

        devices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLifecare();
           }
        });

        statuscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLifecare();
            }
        });

        imageSlider.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemSelected(int i) {
                openFind();
            }
        });

        wellnessSlider.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemSelected(int i) {
                openWellness();
            }
        });

//        servicescard.setOnClickListener(new View.OnClickListener() {
 //           @Override
 //           public void onClick(View v) {
  //              openDoclist();
  //          }
  //      });
    }

    private void openFind() {
        Intent intent = new Intent(this, FindScreen.class);
        startActivity(intent);
    }

    private void openWellness() {
        Intent intent = new Intent(this, PhysWellness.class);
        startActivity(intent);
    }

    private void openLifecare() {
        Intent intent = new Intent(this, ScanActivity.class);
        startActivity(intent);
    }


    private void openDoclist() {
        Intent intent = new Intent(this, DocDirectory.class);
        startActivity(intent);
    }

    public void openConsultancy() {
        Intent intent = new Intent(this, CallCenterActivity.class);
        startActivity(intent);
    }

   public void openLogin() {
       Intent intent = new Intent(this, LoginActivity.class);
       startActivity(intent);
   }



    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
package com.example.MyHealth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class BmiActivity extends AppCompatActivity {
    private CardView calculateBtn;
    private TextView bmiScore, bmiScoreRemark;
    EditText weight, height;
    String calculation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi);

        weight = findViewById(R.id.txtWeight);
        height = findViewById(R.id.txtHeight);

        bmiScore = findViewById(R.id.txtBmi);
        bmiScoreRemark = findViewById(R.id.txtBmiRemark);

        calculateBtn = findViewById(R.id.calculateBmiBtn);

        calculateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String S1 = weight.getText().toString();
                String S2 = height.getText().toString();

                float weightValue = Float.parseFloat(S1);
                float heightValue = Float.parseFloat(S2) / 100;

                float bmi = weightValue / (heightValue * heightValue);

                if (bmi < 16) {
                    bmiScoreRemark.setText("Severely UnderWeight");
                } else if (bmi < 18.5) {
                    bmiScoreRemark.setText("Under Weight");
                } else if (bmi >= 18.5 && bmi <= 24.9){
                    bmiScoreRemark.setText("Normal Weight");
                }else if (bmi >=25  && bmi <=29.9 ){
                    bmiScoreRemark.setText("Overweight");
                }else{
                    bmiScoreRemark.setText("Obese");
                }

                bmiScore.setText(String.valueOf(bmi));
            }
        });


    }
}
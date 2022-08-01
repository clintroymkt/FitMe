package com.example.MyHealth.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.MyHealth.FitDash;
import com.example.MyHealth.SignUpActivity;

import com.example.MyHealth.databinding.ActivityLoginBinding;
import com.example.MyHealth.fitnessbands.scan.ScanActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

//login screen, rn since haisi connected to db I added local variables but obviously it should load doc or patient view based on whether it's a doc entering or patient entering.


public class LoginActivity extends AppCompatActivity {


    private ActivityLoginBinding binding;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;

    private String email = "", password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Logging In");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        binding.lgsignupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });

        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });

        binding.skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                skipmethod();
            }
        });
    }

    private void skipmethod() {
        startActivity(new Intent(LoginActivity.this, ScanActivity.class ));
        finish();
    }

    private void checkUser() {
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            if(firebaseUser !=null){
                startActivity(new Intent(LoginActivity.this, ScanActivity.class ));
                finish();
            }
        }

    private void validateData() {
        email = binding.txtlgusername.getText().toString().trim();
        password = binding.txtlgpassword.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.txtlgusername.setError("Invalid email format");
        }
        else if (TextUtils.isEmpty(password)){
            binding.txtlgpassword.setError("Please Enter Password");
        }



        else {
            firebaseLogin();
        }
    }

    private void firebaseLogin() {
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        String email = firebaseUser.getEmail();
                        Toast.makeText(LoginActivity.this, "Logged in", Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(LoginActivity.this, ScanActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

  //  private static String token;



    //private void login() {
    //    Login login = new Login("teenzmcwamsie@gmail.com", "password");
   //       Call<User> call = userClient.login(login);

   //       call.enqueue(new Callback<User>() {
    //         @Override
   //          public void onResponse(Call<User> call, Response<User> response) {
   //               if (response.isSuccessful()) {
    //                  Toast.makeText(LoginActivity.this, response.body().getToken(), Toast.LENGTH_SHORT).show();
  //                    token = response.body().getToken();

    //                  Intent intent = new Intent(LoginActivity.this, PatientList.class);
   //                   startActivity(intent);
   //             } else {
   //                 Toast.makeText(LoginActivity.this, "login not correct if", Toast.LENGTH_SHORT).show();
    //            }
    //        }

    //        @Override
    //        public void onFailure(Call<User> call, Throwable t) {
            //    Toast.makeText(LoginActivity.this, "error if", Toast.LENGTH_SHORT).show();

      //          if (lgUsername.getText().toString().equals("DrChirisa") && lgPassword.getText().toString().equals("123456")) {
       //                               Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
     //                               startActivity(intent);
     //                            } else if (lgUsername.getText().toString().equals("clintroymkt@gmail.com") && lgPassword.getText().toString().equals("123456")) {
    //                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
     //                                 startActivity(intent);
    //                             } else if (lgUsername.getText().toString().equals("dmukute@gmail.com") && lgPassword.getText().toString().equals("123456")) {
    //                                  Intent intent = new Intent(LoginActivity.this, NurseActivity.class);
    //                                  startActivity(intent);
    //                              } else {
    //                                  Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
   //                               }


    //        }
   //     });
  //  }



    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}


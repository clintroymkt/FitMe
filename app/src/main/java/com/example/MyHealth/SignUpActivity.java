package com.example.MyHealth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;


import com.example.MyHealth.databinding.ActivitySignUpBinding;
import com.example.MyHealth.fitmodel.User;
import com.example.MyHealth.ui.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpActivity extends AppCompatActivity {

    private String email = "", password = "", fname= "", lname="";

    private ActivitySignUpBinding binding;

    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Creating your account..... ");
        progressDialog.setCanceledOnTouchOutside(false);

        binding.submitdets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });



    }

    private void validateData() {
        email = binding.txtEmails.getText().toString().trim();
        password = binding.txtPasswords.getText().toString().trim();
        fname = binding.txtFname.getText().toString().trim();
        lname = binding.txtLname.getText().toString().trim();
       // idNumber = binding.txtIdNumber.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.txtEmails.setError("Invalid email format");
        }
        else if (TextUtils.isEmpty(password)){
            binding.txtPasswords.setError("Please Enter Password");
        }
        else if (password.length()<6){
            binding.txtPasswords.setError("Password must at least be 6 characters long");
        }
        //   else if (TextUtils.isEmpty(fname)){
        //       binding.txtFname.setError("Please enter your First Name");
        //   }
        //   else if (TextUtils.isEmpty(lname)){
        //         binding.txtLname.setError("Please Last Name");
        //     }

        else {
            firebaseSignUp(email, password, fname, lname);
        }

    }

    private void firebaseSignUp(String email, String password, String fname, String lname) {

        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){


                            User user = new User(fname,lname, email);
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(SignUpActivity.this, "User has been registered", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                        progressDialog.dismiss();
                                    }else {
                                        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                        Toast.makeText(SignUpActivity.this, "Failed to register user", Toast.LENGTH_SHORT).show();

                                        progressDialog.dismiss();
                                    }
                                }
                            });


                        }else {
                            Toast.makeText(SignUpActivity.this, "Second fail", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

}
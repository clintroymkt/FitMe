package com.example.MyHealth;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class Backupactivity extends AppCompatActivity {

    Button uploadbtn;

    StorageReference storageReference;
    DatabaseReference databaseReference;
    DBHelper DB = new DBHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backupactivity);
        uploadbtn = findViewById(R.id.uploadbckpbtn);



        storageReference = FirebaseStorage.getInstance().getReference().child("backups");

        uploadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputStream inputStream = null;
                try{
                    inputStream = new FileInputStream(new File(String.valueOf(getBaseContext().getDatabasePath("Watchdata.db"))));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                UploadTask uploadTask = storageReference.child("Watchdata.db").putStream(inputStream);

                InputStream finalInputStream = inputStream;
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        if (finalInputStream !=null){
                            try {
                                finalInputStream.close();
                            }catch (IOException e){
                                e.printStackTrace();
                            }
                        }
                        Toast.makeText(Backupactivity.this, "Data backed Up", Toast.LENGTH_LONG);
                    }
                });
            }
        });
    }


}
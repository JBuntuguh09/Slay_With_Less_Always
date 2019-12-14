package com.lonewolf.slay_with_less_always;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Register extends AppCompatActivity {

    private EditText fname, lname, email, phone, password, confirm;
    private Button register;
    private ProgressBar progressBar;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        fname = findViewById(R.id.edtFName);
        lname = findViewById(R.id.edtLName);
        email = findViewById(R.id.edtEmail);
        phone = findViewById(R.id.edtPhone);
        password = findViewById(R.id.edtPassword);
        confirm = findViewById(R.id.edtConfirmPass);
        register = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar);
       

        getButtons();
    }

    private void getButtons() {
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fname.getText().toString().isEmpty()){
                    Toast.makeText(Register.this, "Enter first name", Toast.LENGTH_LONG).show();
                }else if(lname.getText().toString().isEmpty()){
                    Toast.makeText(Register.this, "Enter last name", Toast.LENGTH_LONG).show();
                }else if(email.getText().toString().isEmpty()){
                    Toast.makeText(Register.this, "Enter email", Toast.LENGTH_LONG).show();
                }else if(!email.getText().toString().contains("@")){
                    Toast.makeText(Register.this, "Enter a valid email address", Toast.LENGTH_LONG).show();
                }else if(password.getText().toString().isEmpty()){
                    Toast.makeText(Register.this, "Enter password", Toast.LENGTH_LONG).show();
                }else if(password.getText().toString().length()<7){
                    Toast.makeText(Register.this, "Password should be more than 6 characters", Toast.LENGTH_LONG).show();
                }else if(!password.getText().toString().equals(confirm.getText().toString())){
                    Toast.makeText(Register.this, "Passwords do not match", Toast.LENGTH_LONG).show();
                }else {
                    registerMe();
                }
            }
        });
    }

    private void registerMe() {
        progressBar.setVisibility(View.VISIBLE);
        auth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                String userId = auth.getCurrentUser().getUid();
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("First_Name", fname.getText().toString());
                hashMap.put("Last_Name", lname.getText().toString());
                hashMap.put("Email", email.getText().toString());
                hashMap.put("Phone", phone.getText().toString());
                hashMap.put("Password", password.getText().toString());
                hashMap.put("Role", "Standard");

                Log.d("hhhh", userId);
                databaseReference.child("Users").child(userId).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(Register.this, "Successfully Registered", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(Register.this, Main_Page.class);
                        startActivity(intent);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(Register.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(Register.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}

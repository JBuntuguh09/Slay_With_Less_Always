 package com.lonewolf.slay_with_less_always;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lonewolf.slay_with_less_always.Resources.Settings;

 public class MainActivity extends AppCompatActivity {

    private EditText username, pword;
    private Button login;
    private TextView register;
    private CheckBox loggedIn;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private ProgressBar progressBar;
    private Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Intent intent = new Intent(MainActivity.this, Main_Page.class);
//        startActivity(intent);
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        settings = new Settings(this);

        register = findViewById(R.id.txtRegister);
        login = findViewById(R.id.btnLogin);
        username = findViewById(R.id.edtUsername);
        pword = findViewById(R.id.edtPassword);
        loggedIn = findViewById(R.id.ckKeepLoggedIn);
        progressBar = findViewById(R.id.progressBar);

        if(auth.getUid()!=null){
            Intent intent = new Intent(MainActivity.this, Main_Page.class);
            startActivity(intent);
        }

        getButtons();


    }

     private void getButtons() {
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Register.class);
                startActivity(intent);

            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(username.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this, "Enter your email", Toast.LENGTH_SHORT).show();
                }else if(pword.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this, "Enter your password", Toast.LENGTH_SHORT).show();
                }else{
                    progressBar.setVisibility(View.VISIBLE);
                    auth.signInWithEmailAndPassword(username.getText().toString(), pword.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            if(loggedIn.isChecked()){
                                settings.setLoggedIn(true);
                            }else {
                                settings.setLoggedIn(false);
                            }
                            settings.setRole("Standard");
                            settings.setUserName("");
                            settings.setEmail(username.getText().toString());
                            Toast.makeText(MainActivity.this, "Successfully logged in", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, Main_Page.class);
                            startActivity(intent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });


        if(auth.getUid()!=null){
            Intent intent = new Intent(MainActivity.this, Main_Page.class);
            startActivity(intent);
        }
     }
 }

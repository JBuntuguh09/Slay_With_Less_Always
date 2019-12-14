package com.lonewolf.slay_with_less_always;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lonewolf.slay_with_less_always.Resources.PageAdapterMain;
import com.lonewolf.slay_with_less_always.Resources.Settings;

public class Main_Page extends AppCompatActivity {

    private PageAdapterMain pageAdapterMain;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Toolbar toolbar;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private Settings settings;
    private TextView title;
    private ImageView addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__page);

        viewPager = findViewById(R.id.vpDashBoardMain);
        tabLayout = findViewById(R.id.tabDashboard);
        toolbar = findViewById(R.id.toolbarDashBoard);
        settings = new Settings(this);
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        title = findViewById(R.id.txtTitle);
        addButton = findViewById(R.id.imgAdd);

        //auth = FirebaseAuth.getInstance();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Welcome");


        addButton.setVisibility(View.GONE);

        pageAdapterMain = new PageAdapterMain(getSupportFragmentManager());

        viewPager.setAdapter(pageAdapterMain);


        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabTextColors(Color.WHITE, Color.WHITE);

        getButtons();
    }

    private void getButtons() {

        if(auth.getUid()!=null){
            databaseReference.child("Users").child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        String name = dataSnapshot.child("First_Name").getValue().toString()+" "+dataSnapshot.child("Last_Name").getValue().toString();
                        getSupportActionBar().setTitle("Welcome "+name);
                        title.setText("Welcome "+name);
                        settings.setUserName(name);
                        settings.setEmail(dataSnapshot.child("Email").getValue().toString());
                        settings.setRole(dataSnapshot.child("Role").getValue().toString());
                        if(settings.getRole().equals("Admin")){
                            addButton.setVisibility(View.VISIBLE);
                        }else {
                            addButton.setVisibility(View.GONE);
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main_Page.this, Add_Items.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.menuAddNew){
            Intent intent = new Intent(Main_Page.this, Add_Items.class);
            startActivity(intent);

        } else if(item.getItemId()==R.id.menyLogOut){
            if(auth.getUid()!=null){

                auth.signOut();
            }else {

                Intent intent = new Intent(Main_Page.this, MainActivity.class);
                startActivity(intent);
            }
        }

        if(!settings.getRole().equals("Admin")){
            if(item.getItemId()==R.id.menuAddNew){
                item.setVisible(false);

            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if(menu!=null) {
            for (int b = 0; b < menu.size(); b++) {
                if(menu.getItem(b).getItemId() == R.id.menyLogOut) {
                    if (auth.getUid()!=null) {
                        menu.getItem(b).setTitle("Log Out");

                    } else {

                        menu.getItem(b).setTitle("Log In");
                    }
                }
            }

        }
        return super.onMenuOpened(featureId, menu);
    }



}

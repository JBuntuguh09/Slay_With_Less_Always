package com.lonewolf.slay_with_less_always;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        // The thread to wait for splash screen events
        // Wait given period of time or exit on touch
        // Run next activity
        Thread mSplashThread = new Thread() {
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        // Wait given period of time or exit on touch
                        wait(4000);
                    }
                } catch (InterruptedException ex) {
                }

                finish();

                // Run next activity
                Intent intent = new Intent(Splash.this, Main_Page.class);

                startActivity(intent);
                finish();
            }
        };

        mSplashThread.start();
    }

    /**
     * Processes splash screen touch events
     */

}
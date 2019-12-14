package com.lonewolf.slay_with_less_always.Fragments;


import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lonewolf.slay_with_less_always.R;
import com.lonewolf.slay_with_less_always.Resources.Settings;
import com.lonewolf.slay_with_less_always.Resources.SliderAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class Upcoming extends Fragment {

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private Settings settings;
    private ViewPager viewPager;
    private TabLayout indicator;
    private LinearLayout linMain1, linMain2;
    private List<String> paths = new ArrayList<>();
    private List<String> pathDownloaded = new ArrayList<>();


    List<Integer> color;
    List<String> itemName = new ArrayList<>();
    List<String> featureId = new ArrayList<>();
    List<Integer> drawables;

    List<Class> intents = new ArrayList<>();
    private Toolbar toolbar;
    private StorageReference storageReference;

    public Upcoming() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_upcoming, container, false);

        settings = new Settings(getActivity());
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        settings = new Settings(getActivity());
        viewPager=view.findViewById(R.id.viewPager);
        indicator=view.findViewById(R.id.indicator);

        getBacground();





        drawables = new ArrayList<>();



        return view;
    }

    private void getBacground() {

        databaseReference.child("Featured").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                pathDownloaded.clear();
                itemName.clear();
                featureId.clear();

                for(final DataSnapshot father : dataSnapshot.getChildren()){
                    String mUrl = father.child("Base64_Image").getValue().toString();
                    String name = father.child("Item").getValue().toString();
                    String itemId = father.child("Item_Id").getValue().toString();

                    pathDownloaded.add(mUrl);
                    itemName.add(name);
                    featureId.add(itemId);
                }
                if(pathDownloaded.size()>0){

                    viewPager.setAdapter(new SliderAdapter(getActivity(),pathDownloaded
                            , drawables, itemName, featureId));
                    indicator.setupWithViewPager(viewPager, true);


                    Timer timer = new Timer();
                    timer.scheduleAtFixedRate(new SliderTimer(), 4000, 6000);
                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    private void downloadPDF(Context context, String filename, String fileExtension, String destination, String url){

        DownloadManager downloadManager = (DownloadManager)context.getSystemService(context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

       // request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destination, filename+fileExtension);

        pathDownloaded.add(Environment.getExternalStorageDirectory().getAbsoluteFile()+"/Android/data/com.lonewolf.slay_with_less_always/files/Slay_Pics"+filename+fileExtension);
        Log.d("lllll", Environment.getExternalStorageDirectory().getAbsoluteFile()+"/Android/data/com.lonewolf.slay_with_less_always/files/Slay_Pics"+filename+fileExtension);
        downloadManager.enqueue(request);


    }





    private class SliderTimer extends TimerTask {

        @Override
        public void run() {
            if(getActivity()!=null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (viewPager.getCurrentItem() < pathDownloaded.size() - 1) {
                            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                        } else {
                            viewPager.setCurrentItem(0);
                        }
                    }
                });
            }
        }
    }




}

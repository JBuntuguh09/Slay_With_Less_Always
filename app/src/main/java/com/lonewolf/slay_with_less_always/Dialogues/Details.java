package com.lonewolf.slay_with_less_always.Dialogues;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lonewolf.slay_with_less_always.Add_Items;
import com.lonewolf.slay_with_less_always.R;
import com.lonewolf.slay_with_less_always.Resources.ShortCut_To;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Details extends Fragment {

    private static AlertDialog.Builder alertDialog;
    private static AlertDialog dialog;
    private static StorageReference storageReference;
    private static DatabaseReference databaseReference;
    private static String checkedLabel;

    public static void showDetail(final Activity activity, LinearLayout linearLayout, String name, String price, String desc, String currency, String available, String date, final String url){
        alertDialog = new AlertDialog.Builder(activity);
        dialog = alertDialog.create();
        storageReference = FirebaseStorage.getInstance().getReference();

        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View view = layoutInflater.inflate(R.layout.layout_cloth_details, linearLayout, false);
        TextView tName = view.findViewById(R.id.txtName);
        TextView tPrice = view.findViewById(R.id.txtPrice);
        TextView tDesc = view.findViewById(R.id.txtDescription);
        final ImageView pic = view.findViewById(R.id.imgPic);
        ImageView close = view.findViewById(R.id.imgCloseTab);
        ImageView whatsapp = view.findViewById(R.id.imgWhatsapp);
        ImageView phone = view.findViewById(R.id.imgPhone);
        ProgressBar progressBar = view.findViewById(R.id.progressBar);


        ShortCut_To.setPic(storageReference, activity, pic, url, progressBar);
        //Log.d("fg", df);
        //Glide.with(activity).load(storageReference.child("Dress_Pics/4,jpg").getDownloadUrl()).into(pic);

        tName.setText(name);
        tPrice.setText(price);
        tDesc.setText(desc);

        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE)== PackageManager.PERMISSION_GRANTED){
                    String phone = "233248801916";
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.fromParts("tel", phone, null));
                    activity.startActivity(intent);

                }else if(ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE)== PackageManager.PERMISSION_DENIED){
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE}, 9);

                }


            }
        });



        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse("https://api.whatsapp.com/send?phone=233248801916");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                activity.startActivity(intent);

            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.setView(view);
        dialog.show();

    }

    public static void upDateDetail(final Activity activity, LinearLayout linearLayout, final String name, final String price, final String desc, String currency, String available, String date, final String url,
                                    final String itemId){

        alertDialog = new AlertDialog.Builder(activity);
        dialog = alertDialog.create();


        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        final View view = layoutInflater.inflate(R.layout.activity_add__items, linearLayout, false);

        final EditText tName = view.findViewById(R.id.edtName);
        final EditText tPrice = view.findViewById(R.id.edtPrice);
        final EditText tDesc = view.findViewById(R.id.edtDesc);
        ImageView pic = view.findViewById(R.id.imgPic);
        final Spinner spinCurr = view.findViewById(R.id.spinCurrency);
        final RadioGroup radioType = view.findViewById(R.id.radionMain);
        Button update = view.findViewById(R.id.btnSubmit);
        checkedLabel = available;
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        final ProgressBar progressBar = view.findViewById(R.id.progressBar);
        ImageView close = view.findViewById(R.id.imgCloseTab);
        ProgressBar progressBar1 = view.findViewById(R.id.progressBar2);


        close.setVisibility(View.VISIBLE);
        update.setText(activity.getString(R.string.update));
        ShortCut_To.setPic(storageReference, activity, pic, url, progressBar1);
        tName.setText(name);
        tPrice.setText(price);
        tDesc.setText(desc);

        List list = Arrays.asList(ShortCut_To.getCurrenciesLabel);
        ArrayAdapter adapter = new ArrayAdapter(activity, R.layout.spiner_layout, list);
        adapter.setDropDownViewResource(R.layout.dropdown_layout);
        spinCurr.setAdapter(adapter);

        switch (currency) {
            case "Ghana Cedis":
                spinCurr.setSelection(0);
                break;
            case "US Dollars":
                spinCurr.setSelection(1);
                break;
            case "Euro":
                spinCurr.setSelection(2);
                break;
            case "British Pound":
                spinCurr.setSelection(3);
                break;
            case "Nigerian Naira":
                spinCurr.setSelection(4);
                break;
            case "CFA Franc":
                spinCurr.setSelection(5);
                break;
        }


        switch (available) {
            case "Available":
                radioType.check(R.id.radioAvailable);
                break;
            case "Reserved":
                radioType.check(R.id.radioReserved);
                break;
            case "Sold":
                radioType.check(R.id.radioSold);
                break;
        }

        radioType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checked = view.findViewById(checkedId);
                checkedLabel = checked.getText().toString();

            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                HashMap<String, Object>hashMap = new HashMap<>();

                hashMap.put("Name", tName.getText().toString());
                hashMap.put("Price", tPrice.getText().toString());
                hashMap.put("Datetime_Updated", ShortCut_To.getCurrentDatewithTime());
                hashMap.put("Status", checkedLabel);
                //hashMap.put("Picture_Link", url);
                hashMap.put("Description", tDesc.getText().toString());
                hashMap.put("Currency", spinCurr.getSelectedItem().toString());
                //hashMap.put("Pic_Id", picId);

                databaseReference.child("In_Stock").child(itemId).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(activity, "Successfully updated", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });



        dialog.setView(view);
        dialog.show();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==9 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
//            selectFiles();
            Toast.makeText(getActivity(), "hhh", Toast.LENGTH_SHORT).show();
            String phone = "233248801916";
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.fromParts("tel", phone, null));
            getActivity().startActivity(intent);
        }else {
            String phone = "233248801916";
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
            startActivity(intent);
            Toast.makeText(getActivity(), "Please allow permissions to access this", Toast.LENGTH_SHORT).show();
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}

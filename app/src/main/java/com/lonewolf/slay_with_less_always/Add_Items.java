package com.lonewolf.slay_with_less_always;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lonewolf.slay_with_less_always.Resources.ShortCut_To;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import id.zelory.compressor.Compressor;

public class Add_Items extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private StorageReference storage;
    private EditText name, price, desc;
    private Button submit;
    private ProgressBar progressBar;
    private RadioGroup radioMain;
    private ImageView itemPic;
    private Uri uri;
    private String checkedLabel = "Available";
    private Spinner spinCurrency;
    private String links;
    private CheckBox feature;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__items);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance().getReference();

        name = findViewById(R.id.edtName);
        price = findViewById(R.id.edtPrice);
        desc = findViewById(R.id.edtDesc);
        submit = findViewById(R.id.btnSubmit);
        radioMain = findViewById(R.id.radionMain);
        progressBar = findViewById(R.id.progressBar);
        itemPic = findViewById(R.id.imgPic);
        spinCurrency = findViewById(R.id.spinCurrency);
        feature = findViewById(R.id.chkfeatured);

        getButtons();
    }

    private void getButtons() {
        itemPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadButtons();
            }
        });

        radioMain.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checked = findViewById(checkedId);
                checkedLabel = checked.getText().toString();

            }
        });

        List list = Arrays.asList(ShortCut_To.getCurrenciesLabel);
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.spiner_layout, list);
        adapter.setDropDownViewResource(R.layout.dropdown_layout);
        spinCurrency.setAdapter(adapter);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name.getText().toString().isEmpty()){
                    Toast.makeText(Add_Items.this, "Enter Name", Toast.LENGTH_LONG).show();
                }else if(name.getText().toString().isEmpty()){
                    Toast.makeText(Add_Items.this, "Enter price", Toast.LENGTH_LONG).show();
                }else{
                    if(uri!=null){
                        savePic();
                    }else {
                        sendData("", "");
                    }
                }
            }
        });



    }
    private void savePic(){
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        name.setEnabled(false);
        price.setEnabled(false);
        desc.setEnabled(false);
        databaseReference.child("Identifiers").child("Pic_Id").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String numId = String.valueOf(Integer.parseInt(dataSnapshot.getValue().toString())+1);
                storage.child("Dress_Pics").child(numId).putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        String url = storage.child("Dress_Pics").child(numId).getPath();
                        databaseReference.child("Identifiers").child("Pic_Id").setValue(numId);
                        sendData("", numId);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Add_Items.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                        name.setEnabled(true);
                        price.setEnabled(true);
                        desc.setEnabled(true);
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double load = (100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                      //  progressBar.setProgress((int) load);
                        Log.d("lllmm", String.valueOf(load));
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Add_Items.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                name.setEnabled(true);
                price.setEnabled(true);
                desc.setEnabled(true);
            }
        });

    }

    private void sendData( String url, final String picId) {
        if(url.equals("")) {
            progressBar.setVisibility(View.VISIBLE);
            name.setEnabled(false);
            price.setEnabled(false);
            desc.setEnabled(false);
        }
        databaseReference.child("Identifiers").child("Item_Id").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                final String numId = String.valueOf(Integer.parseInt(dataSnapshot.getValue().toString())+1);
                String newUrl = "";
                if(picId.equals("")){
                    newUrl = "";
                }else {
                     newUrl = storage.child("Dress_Pics").child(picId).getPath();
                }
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("Item_Id", numId);
                hashMap.put("Name", name.getText().toString());
                hashMap.put("Price", price.getText().toString());
                hashMap.put("Datetime_Posted", ShortCut_To.getCurrentDatewithTime());
                hashMap.put("Status", checkedLabel);
                hashMap.put("Picture_Link", newUrl);
                hashMap.put("Description", desc.getText().toString());
                hashMap.put("Currency", spinCurrency.getSelectedItem().toString());
                hashMap.put("Pic_Id", picId);

                final String nameTxt = name.getText().toString();


                final String finalNewUrl = newUrl;
                databaseReference.child("In_Stock").child(numId).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        if(feature.isChecked()){
                            databaseReference.child("Identifiers").child("Feature_Id").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    final String featureId = String.valueOf(Integer.parseInt(dataSnapshot.getValue().toString())+1);
                                    HashMap<String, Object> hash = new HashMap<>();
                                    hash.put("Item", nameTxt);
                                    hash.put("Pic_link", finalNewUrl);
                                    hash.put("date_posted", ShortCut_To.getCurrentDatewithTime());
                                    hash.put("Item_Id", numId);
                                    hash.put("Feature_Id", featureId);
                                    hash.put("Base64_Image", links);

                                    if(featureId.equals("11")){
                                        databaseReference.child("Featured").child("1").updateChildren(hash).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                databaseReference.child("Identifiers").child("Feature_Id").setValue("1");
                                            }
                                        });
                                    }else {
                                        databaseReference.child("Featured").child(featureId).updateChildren(hash).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                databaseReference.child("Identifiers").child("Feature_Id").setValue(featureId);
                                            }
                                        });
                                    }


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                        progressBar.setVisibility(View.GONE);
                        name.setEnabled(true);
                        price.setEnabled(true);
                        desc.setEnabled(true);

                        name.setText("");
                        price.setText("");
                        desc.setText("");

                        itemPic.setImageResource(R.drawable.add_pic);
                        radioMain.check(R.id.radioAvailable);
                        spinCurrency.setSelection(0);
                        databaseReference.child("Identifiers").child("Item_Id").setValue(numId);
                        Toast.makeText(Add_Items.this, "Successfully Posted", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Add_Items.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                        name.setEnabled(true);
                        price.setEnabled(true);
                        desc.setEnabled(true);
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Add_Items.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                name.setEnabled(true);
                price.setEnabled(true);
                desc.setEnabled(true);
            }
        });
    }


    private void uploadButtons(){
        if(ContextCompat.checkSelfPermission(Add_Items.this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
            selectFiles();

        }else if(ContextCompat.checkSelfPermission(Add_Items.this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(Add_Items.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 9);

        }
    }

    private void selectFiles() {


        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                //.setAspectRatio(1,1)
                //.setMinCropWindowSize(500,500)
                .start(Add_Items.this);


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==9 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            selectFiles();
        }else {
            Toast.makeText(Add_Items.this, "Please allow permissions to access this", Toast.LENGTH_SHORT).show();
        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                uri = result.getUri();





                File actualImageFile = new File(uri.getPath());
                Bitmap compressedImageBitmap = null;
                //itemPic.setImageBitmap(compressedImageBitmap);
                try {
                    compressedImageBitmap = new Compressor(this)
//                            .setMaxWidth(500)
//                            .setMaxHeight(500)
//                            .setQuality(50)
                            .compressToBitmap(actualImageFile);
                    itemPic.setImageBitmap(compressedImageBitmap);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    compressedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                    byte[] thumb_byte = baos.toByteArray();
                     links = Base64.encodeToString(thumb_byte, Base64.DEFAULT);
                } catch (IOException e) {
                    e.printStackTrace();
                }






            }
        }
    }

}

package com.lonewolf.slay_with_less_always.Fragments;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lonewolf.slay_with_less_always.Add_Items;
import com.lonewolf.slay_with_less_always.Dialogues.Details;
import com.lonewolf.slay_with_less_always.R;
import com.lonewolf.slay_with_less_always.Resources.Settings;
import com.lonewolf.slay_with_less_always.Resources.ShortCut_To;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import id.zelory.compressor.Compressor;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class View_In_Stock extends Fragment {

    private FirebaseAuth auth;
    private DatabaseReference databaseReference, currentRef;
    private StorageReference storageReference;
    private Settings settings;
    private LinearLayout linearLayout;
    private TextView welcome;
    private ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
    private ProgressBar progressBar;
    private AlertDialog.Builder alert;
    private AlertDialog dialog;
    private  String checkedLabel;
    private String links;
    private Uri uri;
    private ImageView itemPic;

    public View_In_Stock() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view__in__stock, container, false);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        currentRef = FirebaseDatabase.getInstance().getReference().child("In_Stock");
        storageReference = FirebaseStorage.getInstance().getReference();

        settings = new Settings(getActivity());

        linearLayout = view.findViewById(R.id.linStockMain);
        welcome = view.findViewById(R.id.txtWelcome);
        progressBar = view.findViewById(R.id.progressBar);

       // getButtons();

        getCurrent();

        currentRef.limitToFirst(100).keepSynced(true);
        return view;
    }

    private void getCurrent() {
        progressBar.setVisibility(View.VISIBLE);
        currentRef.limitToFirst(100).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();
                linearLayout.removeAllViews();
                for (DataSnapshot child : dataSnapshot.getChildren()){
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("name", child.child("Name").getValue().toString());
                    hashMap.put("price", child.child("Price").getValue().toString());
                    hashMap.put("datetime", child.child("Datetime_Posted").getValue().toString());
                    hashMap.put("status", child.child("Status").getValue().toString());
                    hashMap.put("picLink", child.child("Picture_Link").getValue().toString());
                    hashMap.put("desc", child.child("Description").getValue().toString());
                    hashMap.put("currency", child.child("Currency").getValue().toString());
                    hashMap.put("item_id", child.child("Item_Id").getValue().toString());

                    arrayList.add(hashMap);
                }

                if(arrayList.size()>0 && getActivity()!=null){
                    ShortCut_To.setAutoFilter(arrayList, "item_id");
                    setCurrent();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setCurrent() {

        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        for(int a=0; a<arrayList.size(); a++){
            View view = layoutInflater.inflate(R.layout.layout_items, linearLayout, false);

            TextView name = view.findViewById(R.id.txtName);
            TextView price = view.findViewById(R.id.txtPrice);
            ImageView pic = view.findViewById(R.id.imgCloth);
            ImageView sold = view.findViewById(R.id.imgSold);
            ImageView reserved = view.findViewById(R.id.imgReserved);
            ProgressBar progress = view.findViewById(R.id.progressBar);

            final HashMap<String, String> hashMap = arrayList.get(a);
            name.setText(hashMap.get("name"));
            price.setText(hashMap.get("price"));

            if(hashMap.get("status").equals("Sold")){
                sold.setVisibility(View.VISIBLE);
            }else if(hashMap.get("status").equals("Reserved")){
                reserved.setVisibility(View.VISIBLE);
            }

            if(!hashMap.get("picLink").equals("")){
                ShortCut_To.setPic(storageReference, getActivity(), pic, hashMap.get("picLink"), progress);

            }else {
                progress.setVisibility(View.GONE);
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDetail( linearLayout, hashMap.get("name"), hashMap.get("price"), hashMap.get("desc"), hashMap.get("currency"), hashMap.get("status"),
                            hashMap.get("datetime"), hashMap.get("picLink"));
                }
            });

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(settings.getRole().equals("Admin")) {
                        upDateDetail(linearLayout, hashMap.get("name"), hashMap.get("price"), hashMap.get("desc"), hashMap.get("currency"), hashMap.get("status"),
                                hashMap.get("datetime"), hashMap.get("picLink"), hashMap.get("item_id"));
                    }
                    return false;

                }
            });


            linearLayout.addView(view);
        }
        progressBar.setVisibility(View.GONE);
    }

    private void getButtons() {
//        if(auth.getUid()!=null){
//            databaseReference.child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    String name = dataSnapshot.child("First_Name").getValue().toString()+" "+dataSnapshot.child("Last_Name").getValue().toString();
//                    String welcomeMess = "Welcome "+name;
//                    welcome.setText(welcomeMess);
//
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
//        }


    }

    private  void showDetail(LinearLayout linearLayout, String name, String price, String desc, String currency, String available, String date, final String url){
        alert = new AlertDialog.Builder(getActivity());
        dialog = alert.create();
        storageReference = FirebaseStorage.getInstance().getReference();

        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View view = layoutInflater.inflate(R.layout.layout_cloth_details, linearLayout, false);
        TextView tName = view.findViewById(R.id.txtName);
        TextView tPrice = view.findViewById(R.id.txtPrice);
        TextView tDesc = view.findViewById(R.id.txtDescription);
        final ImageView pic = view.findViewById(R.id.imgPic);
        ImageView close = view.findViewById(R.id.imgCloseTab);
        ImageView whatsapp = view.findViewById(R.id.imgWhatsapp);
        ImageView phone = view.findViewById(R.id.imgPhone);
        ImageView instagram = view.findViewById(R.id.imgIstagram);
        ProgressBar prog = view.findViewById(R.id.progressBar);


        if(!url.equals("")) {
            ShortCut_To.setPic(storageReference, getActivity(), pic, url, prog);
        }else {
            prog.setVisibility(View.GONE);
        }
        //Log.d("fg", df);
        //Glide.with(activity).load(storageReference.child("Dress_Pics/4,jpg").getDownloadUrl()).into(pic);
        String thePrice = ShortCut_To.getCurrency(currency)+price;
        tName.setText(name);
        tPrice.setText(thePrice);
        tDesc.setText(desc);

        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE)== PackageManager.PERMISSION_GRANTED){
                    String phone = "233248801916";
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.fromParts("tel", phone, null));
                    getActivity().startActivity(intent);

                }else if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE)== PackageManager.PERMISSION_DENIED){
                   // ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, 8);
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 8);

                }

//https://www.instagram.com/slay_with_less_always/
            }
        });



        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse("https://api.whatsapp.com/send?phone=233248801916");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                getActivity().startActivity(intent);

            }
        });

        instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse("https://www.instagram.com/slay_with_less_always");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                getActivity().startActivity(intent);

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

    private   void upDateDetail(LinearLayout linearLayout, final String name, final String price, final String desc, String currency, String available, String date, final String url,
                                    final String itemId){
        uri = null;
        alert = new AlertDialog.Builder(getActivity());
        dialog = alert.create();


        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        final View view = layoutInflater.inflate(R.layout.activity_add__items, linearLayout, false);

        final EditText tName = view.findViewById(R.id.edtName);
        final EditText tPrice = view.findViewById(R.id.edtPrice);
        final EditText tDesc = view.findViewById(R.id.edtDesc);
         itemPic = view.findViewById(R.id.imgPic);
        final Spinner spinCurr = view.findViewById(R.id.spinCurrency);
        final RadioGroup radioType = view.findViewById(R.id.radionMain);
        Button update = view.findViewById(R.id.btnSubmit);
        Button delete = view.findViewById(R.id.btnDelete);
        checkedLabel = available;
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        final ProgressBar progressBar = view.findViewById(R.id.progressBar);
        ImageView close = view.findViewById(R.id.imgCloseTab);
        ProgressBar prog = view.findViewById(R.id.progressBar2);


        close.setVisibility(View.VISIBLE);
        update.setText(getActivity().getString(R.string.update));
        if(!url.equals("")) {
            ShortCut_To.setPic(storageReference, getActivity(), itemPic, url, prog);
        }else {
            prog.setVisibility(View.GONE);
        }
        tName.setText(name);
        tPrice.setText(price);
        tDesc.setText(desc);

        List list = Arrays.asList(ShortCut_To.getCurrenciesLabel);
        ArrayAdapter adapter = new ArrayAdapter(getActivity(), R.layout.spiner_layout, list);
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


        delete.setVisibility(View.VISIBLE);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                if (uri == null) {

                    HashMap<String, Object> hashMap = new HashMap<>();

                    hashMap.put("Name", tName.getText().toString());
                    hashMap.put("Price", tPrice.getText().toString());
                    hashMap.put("Datetime_Updated", ShortCut_To.getCurrentDatewithTime());
                    hashMap.put("Status", checkedLabel);
                    //hashMap.put("Picture_Link", url);
                    hashMap.put("Description", tDesc.getText().toString());
                    hashMap.put("Currency", spinCurr.getSelectedItem().toString());


                    databaseReference.child("In_Stock").child(itemId).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getActivity(), "Successfully updated", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            dialog.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

                } else {

                    storageReference.child(url).putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            HashMap<String, Object> hashMap = new HashMap<>();

                            hashMap.put("Name", tName.getText().toString());
                            hashMap.put("Price", tPrice.getText().toString());
                            hashMap.put("Datetime_Updated", ShortCut_To.getCurrentDatewithTime());
                            hashMap.put("Status", checkedLabel);
                            //hashMap.put("Picture_Link", url);
                            hashMap.put("Description", tDesc.getText().toString());
                            hashMap.put("Currency", spinCurr.getSelectedItem().toString());


                            databaseReference.child("In_Stock").child(itemId).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getActivity(), "Successfully updated", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                    dialog.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    });

                }
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE
                );
                if(url.equals("")){

                    databaseReference.child("In_Stock").child(itemId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getActivity(), "Successfully Delted", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                            dialog.dismiss();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE
                            );
                        }
                    });

                }else{
                    storageReference.child(url).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            databaseReference.child("In_Stock").child(itemId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getActivity(), "Successfully Deleted", Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.GONE
                                    );
                                    dialog.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });

        itemPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePic();
            }
        });



        dialog.setView(view);
        dialog.show();


    }

    private void takePic(){
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    //.setAspectRatio(1,1)
                    //.setMinCropWindowSize(500,500)
                    .start(getContext(), this);

        }else if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED){
            //ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 9);
            requestPermissions( new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 9);

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 9) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        //.setAspectRatio(1,1)
                        //.setMinCropWindowSize(500,500)
                        .start(getContext(), this);
            } else {
                Toast.makeText(getActivity(), "Please allow permissions to access this", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == 8) {
            if ( grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                String phone = "233248801916";
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.fromParts("tel", phone, null));
                startActivity(intent);
            } else {
                String phone = "233248801916";
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                startActivity(intent);
            }

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
                    compressedImageBitmap = new Compressor(getActivity())
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

package com.lonewolf.slay_with_less_always.Resources;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lonewolf.slay_with_less_always.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;



public class SliderAdapter extends PagerAdapter {

    private Activity context;

    private List<String> path;
    private List<String> itemName;
    private List<String> fId;

    private List<Integer> myBackground;
    private List<Class> myClasses;
    private Settings saved_data;
    private int check = 0;
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private AlertDialog.Builder alert;
    private AlertDialog dialog;


    public SliderAdapter(Activity context, List<String> path, List<Integer> backgrounds ,  List<String> ItemName, List<String> featureId) {
        this.context = context;
        this.path = path;
        this.myBackground = backgrounds;
        this.itemName = ItemName;
        this.fId = featureId;

    }

    @Override
    public int getCount() {
        return path.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_slider, null);

        TextView textView = view.findViewById(R.id.textView);
        final RelativeLayout linearLayout =   view.findViewById(R.id.linLayout);
        ImageView imageView =   view.findViewById(R.id.imgSlide);
        ProgressBar progressBar = view.findViewById(R.id.progressBar);

        saved_data = new Settings(context);

        //textView.setText(colorName.get(position));
        //linearLayout.setBackgroundColor(color.get(position));
        //linearLayout.setBackgroundResource(myBackground.get(position));
        final Bitmap bmp = ShortCut_To.decodeBase64(path.get(position));
        if (bmp != null) {

            imageView.setImageBitmap(bmp);
            textView.setText(itemName.get(position));
        }

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetail(linearLayout, itemName.get(position), fId.get(position),bmp, context);


            }
        });

        ViewPager viewPager = (ViewPager) container;
        viewPager.addView(view, 0);

        return view;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        ViewPager viewPager = (ViewPager) container;
        View view = (View) object;
        viewPager.removeView(view);
    }


    private  void showDetail(RelativeLayout linearLayout, final String name,   String id, final Bitmap url, final Activity activity){
        alert = new AlertDialog.Builder(activity);
        dialog = alert.create();
        storageReference = FirebaseStorage.getInstance().getReference();

        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View view = layoutInflater.inflate(R.layout.layout_cloth_details, linearLayout, false);
        final TextView tName = view.findViewById(R.id.txtName);
        final TextView tPrice = view.findViewById(R.id.txtPrice);
        final TextView tDesc = view.findViewById(R.id.txtDescription);
        final ImageView pic = view.findViewById(R.id.imgPic);
        ImageView close = view.findViewById(R.id.imgCloseTab);
        ImageView whatsapp = view.findViewById(R.id.imgWhatsapp);
        ImageView phone = view.findViewById(R.id.imgPhone);
        ImageView instagram = view.findViewById(R.id.imgIstagram);
        final ProgressBar prog = view.findViewById(R.id.progressBar);


        DatabaseReference featured = FirebaseDatabase.getInstance().getReference().child("In_Stock").child(id);
        featured.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String price = dataSnapshot.child("Price").getValue().toString();
                String currency = dataSnapshot.child("Currency").getValue().toString();
                String desc = dataSnapshot.child("Description").getValue().toString();

                if(!url.equals("")) {
                    pic.setImageBitmap(url);
                }
                prog.setVisibility(View.GONE);

                String thePrice = ShortCut_To.getCurrency(currency)+price;
                tName.setText(name);
                tPrice.setText(thePrice);
                tDesc.setText(desc);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE)== PackageManager.PERMISSION_GRANTED){
                    String phone = "233248801916";
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.fromParts("tel", phone, null));
                    activity.startActivity(intent);

                }else if(ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE)== PackageManager.PERMISSION_DENIED){
                    // ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, 8);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        activity.requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 8);
                    }

                }

//https://www.instagram.com/slay_with_less_always/
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

        instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse("https://www.instagram.com/slay_with_less_always");
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


}
package com.lakshaysinghla.imaginiserdev1;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Lakshay Singhla on 07-Jul-17.
 */

public class CropActivity extends AppCompatActivity implements View.OnClickListener{

    CropImageView cropImageView;
    Button crop_btn;
    ImageView finalImg;
    RelativeLayout loading;
    Bitmap.CompressFormat format;
    Bitmap cropped;
    boolean PositiveButtonPressed=false;
    Uri uri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        cropImageView = (CropImageView) findViewById(R.id.cropImageView);
        finalImg = (ImageView) findViewById(R.id.final_img);
        crop_btn = (Button) findViewById(R.id.crop_btn);
        crop_btn.setOnClickListener(this);
        loading = (RelativeLayout) findViewById(R.id.loading);

        Bundle extras = getIntent().getExtras();
        uri = Uri.parse(extras.getString("Uri"));

        //setting the loaded image in view to crop it
        if (HomeFragment.camSelected) {
            cropImageView.setImageUriAsync(Uri.fromFile(new File(uri.getPath())));
        } else {
            cropImageView.setImageUriAsync(uri);
        }
    }

    @Override
    public void onBackPressed() {
        if (HomeFragment.camSelected) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(null);
            builder.setMessage("Are you sure, you want to delete this captured image?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    File f = new File(uri.getPath());
                    f.delete();
                    HomeFragment.camSelected = false;
                    CropActivity.super.onBackPressed();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    HomeFragment.camSelected = false;
                    CropActivity.super.onBackPressed();
                }
            });
            builder.create().show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {

        //getting the cropped image in bitmap
        cropped = cropImageView.getCroppedImage();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Format");
        CharSequence[] ch = {"Jpeg","Png","Webp"};
        format = Bitmap.CompressFormat.JPEG;
        builder.setSingleChoiceItems(ch, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0){
                    format = Bitmap.CompressFormat.JPEG;
                } else if(which == 1){
                    format = Bitmap.CompressFormat.PNG;
                } else if(which == 2){
                    format = Bitmap.CompressFormat.WEBP;
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CropAsyncTask task = new CropAsyncTask();
                task.execute();
            }
        });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog.show();

        //cropImageView.setImageBitmap(cropped);
    }

    public class CropAsyncTask extends AsyncTask<Void,Void,String>{
        @Override
        protected void onPreExecute() {
            cropImageView.setVisibility(View.GONE);
            crop_btn.setVisibility(View.GONE);
            finalImg.setVisibility(View.VISIBLE);
            finalImg.setImageBitmap(cropped);
            loading.setVisibility(View.VISIBLE);
        }
        @Override
        protected String doInBackground(Void... params) {
            int compression_rate ;
            String path = Environment.getExternalStorageDirectory() + "/Image Solution";
            File file = new File(path);
            file.mkdirs();
            if(format == Bitmap.CompressFormat.JPEG) {
                path = path + "/Pic_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".jpeg";
                compression_rate = 95;
            } else if(format == Bitmap.CompressFormat.PNG){
                path = path + "/Pic_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".png";
                compression_rate = 70;
            } else{
                path = path + "/Pic_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".webp";
                compression_rate = 95;
            }
            file = new File(path);
            try{
                file.createNewFile();
                FileOutputStream fos;
                fos = new FileOutputStream(file);
                cropped.compress(format , compression_rate , fos);
            }catch (FileNotFoundException e){
                return "Some error occurred. Sorry about that";
            }catch (IOException e){
                return "Some error occurred. Sorry about that";
            }
            return "Image Saved";
        }
        @Override
        protected void onPostExecute(String string) {
            loading.setVisibility(View.GONE);
            Answers.getInstance().logCustom(new CustomEvent("Lakshay")
                    .putCustomAttribute("Crop Image","Ended"));
            if(HomeFragment.camSelected) {
                File f = new File(uri.getPath());
                f.delete();
                HomeFragment.camSelected = false;
            }
            Toast.makeText(CropActivity.this, string, Toast.LENGTH_SHORT).show();
        }
    }
}

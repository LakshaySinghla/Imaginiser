package com.lakshaysinghla.imaginiserdev1;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Lakshay Singhla on 28-Jun-17.
 */

public class HomeFragment extends Fragment {

    int Request_Compress = 10;
    int Request_Compress_cam =110;
    int Request_Resize = 20;
    int Request_Resize_cam =210;
    int Request_Crop = 30;
    int Request_Crop_cam =310;
    CropImageView cropImageView;
    int compression_rate;
    Bitmap.CompressFormat format ;
    MainActivity mainActivity;
    RelativeLayout progressBar;
    RadioButton cam , fm;
    int prev_pos_comp = 1 , prev_pos_format = 0;
    String photoPath;
    static boolean camSelected = false;

    public static HomeFragment newFragment(){
        return new HomeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.home_fragment, container, false);

        cropImageView = (CropImageView) rootview.findViewById(R.id.cropImageView);
        progressBar = (RelativeLayout) rootview.findViewById(R.id.compress_progress_bar);
        cam = (RadioButton) rootview.findViewById(R.id.camera);
        fm = (RadioButton) rootview.findViewById(R.id.fm);

        Button com_img = (Button) rootview.findViewById(R.id.com_img);
        com_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Answers.getInstance().logCustom(new CustomEvent("Lakshay")
                        .putCustomAttribute("Compress Image","Started"));
                MainActivity.refresh = false;
                if(fm.isChecked()) {
                    camSelected = false;
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), Request_Compress);
                    //MainActivity.navigation.setSelectedItemId(R.id.navigation_dashboard);
                }else if(cam.isChecked()){
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    // Ensure that there's a camera activity to handle the intent
                    if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                        // Create the File where the photo should go
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ex) {
                            Toast.makeText(getContext(), "Some Error Occurred. Try Again",Toast.LENGTH_SHORT).show();
                        }
                        // Continue only if the File was successfully created
                        if (photoFile != null) {
                            camSelected = true;
                            Uri photoURI = FileProvider.getUriForFile(getContext(), "com.lakshaysinghla.imaginiserdev1.fileprovider", photoFile);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            startActivityForResult(takePictureIntent, Request_Compress_cam);
                        }
                    }
                }
            }
        });

        Button resize = (Button) rootview.findViewById(R.id.re_img);
        resize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Answers.getInstance().logCustom(new CustomEvent("Lakshay")
                        .putCustomAttribute("Resize Image","Started"));
                MainActivity.refresh = false;
                if(fm.isChecked()) {
                    camSelected = false;
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), Request_Resize);
                }else if(cam.isChecked()){
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    // Ensure that there's a camera activity to handle the intent
                    if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                        // Create the File where the photo should go
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ex) {
                            Toast.makeText(getContext(), "Some Error Occurred. Try Again",Toast.LENGTH_SHORT).show();
                        }
                        // Continue only if the File was successfully created
                        if (photoFile != null) {
                            camSelected = true;
                            Uri photoURI = FileProvider.getUriForFile(getContext(), "com.lakshaysinghla.imaginiserdev1.fileprovider", photoFile);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            startActivityForResult(takePictureIntent, Request_Resize_cam);
                        }
                    }
                }
            }
        });

        Button crop = (Button) rootview.findViewById(R.id.crop_img);
        crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Answers.getInstance().logCustom(new CustomEvent("Lakshay")
                        .putCustomAttribute("Crop Image","Started"));
                MainActivity.refresh = false;
                if(fm.isChecked()) {
                    camSelected = false;
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), Request_Crop);
                }else if (cam.isChecked()){
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    // Ensure that there's a camera activity to handle the intent
                    if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                        // Create the File where the photo should go
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ex) {
                            Toast.makeText(getContext(), "Some Error Occurred. Try Again",Toast.LENGTH_SHORT).show();
                        }
                        // Continue only if the File was successfully created
                        if (photoFile != null) {
                            camSelected = true;
                            Uri photoURI = FileProvider.getUriForFile(getContext(), "com.lakshaysinghla.imaginiserdev1.fileprovider", photoFile);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            startActivityForResult(takePictureIntent, Request_Crop_cam);
                        }
                    }
                }
            }
        });

        //ImageView animate = (ImageView) rootview.findViewById(R.id.animation);
        //animate.setBackgroundResource(R.drawable.animation);
        //AnimationDrawable frameAnimation = (AnimationDrawable) animate.getBackground();
        //frameAnimation.start();

        return rootview;
    }

    public void getMainActivity(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "CAM_" + timeStamp + ".jpg";
        //File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        photoPath = Environment.getExternalStorageDirectory() + "/Image Solution";
        File storageDir = new File(photoPath);
        storageDir.mkdir();
        File image = new File(photoPath+"/"+imageFileName);

        // Save a file: path for use with ACTION_VIEW intents
        photoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Request_Compress){
            if(resultCode == RESULT_OK){
                final Uri selected_uri = data.getData();
                performCompress(selected_uri);
            }
        }
        else if(requestCode == Request_Compress_cam){
            if (resultCode == RESULT_OK) {
                Uri uri = Uri.parse(photoPath);
                performCompress(uri);
            }else {
                File f = new File(photoPath);
                f.delete();
                camSelected = false;
            }
        }
        else if(requestCode == Request_Resize){
            if(resultCode == RESULT_OK){
                Uri selected_uri = data.getData();
                Intent i = new Intent(getActivity() , ResizeActivity.class);
                i.putExtra("uri" , selected_uri.toString());
                startActivity(i);
            }
        }
        else if(requestCode == Request_Resize_cam){
            if(resultCode == RESULT_OK){
                Uri uri = Uri.parse(photoPath);
                Intent i = new Intent(getActivity() , ResizeActivity.class);
                i.putExtra("uri" , uri.toString());
                startActivity(i);
            }else{
                File f = new File(photoPath);
                f.delete();
                camSelected = false;
            }
        }
        else if(requestCode == Request_Crop){
            if(resultCode == RESULT_OK) {
                Uri selected_uri = data.getData();
                /*CropImage.activity(uri)
                        .start(getContext(), this);
                */
                //Sending intent to CropActivity with Extra uri which user has picked
                Intent newIntent = new Intent(getActivity() , CropActivity.class );
                newIntent.putExtra("Uri" , selected_uri.toString());
                startActivity(newIntent);
            }
        }
        else if(requestCode == Request_Crop_cam){
            if (resultCode == RESULT_OK){
                Uri uri = Uri.parse(photoPath);
                Intent newIntent = new Intent(getActivity() , CropActivity.class );
                newIntent.putExtra("Uri" , uri.toString());
                startActivity(newIntent);
            }else{
                File f = new File(photoPath);
                f.delete();
                camSelected = false;
            }
        }
        /*
        //When using default crop Activity
        else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Log.v("LAK" , "URI: " + getRealPathFromURI(resultUri));
                Bitmap bitmap = BitmapFactory.decodeFile(getRealPathFromURI(resultUri));
                Log.v("LAK" , "Width: "+bitmap.getWidth() +" Height: "+bitmap.getHeight());
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
        */
    }

    private View createDialog(){
        LayoutInflater li = getActivity().getLayoutInflater();
        View v = li.inflate(R.layout.dialog_compress,null);
        LinearLayout low = (LinearLayout) v.findViewById(R.id.low);
        final LinearLayout medium = (LinearLayout) v.findViewById(R.id.medium);
        final LinearLayout high = (LinearLayout) v.findViewById(R.id.high);
        LinearLayout ultaHigh = (LinearLayout) v.findViewById(R.id.ultra_high);
        LinearLayout jpeg = (LinearLayout) v.findViewById(R.id.jpeg);
        LinearLayout png = (LinearLayout) v.findViewById(R.id.png);
        LinearLayout webp = (LinearLayout) v.findViewById(R.id.webp);

        final RadioButton lowRb = (RadioButton) v.findViewById(R.id.low_rb);
        final RadioButton mediumRb = (RadioButton) v.findViewById(R.id.medium_rb);
        final RadioButton highRb = (RadioButton) v.findViewById(R.id.high_rb);
        final RadioButton ultraHighRb = (RadioButton) v.findViewById(R.id.ultra_high_rb);
        final RadioButton jpegRb = (RadioButton) v.findViewById(R.id.jpeg_rb);
        final RadioButton pngRb = (RadioButton) v.findViewById(R.id.png_rb);
        final RadioButton webpRb = (RadioButton) v.findViewById(R.id.webp_rb);

        low.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lowRb.setChecked(true);
                mediumRb.setChecked(false);
                highRb.setChecked(false);
                ultraHighRb.setChecked(false);
                compression_rate = 90;
            }
        });
        medium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediumRb.setChecked(true);
                lowRb.setChecked(false);
                highRb.setChecked(false);
                ultraHighRb.setChecked(false);
                compression_rate = 80;
            }
        });
        high.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                highRb.setChecked(true);
                mediumRb.setChecked(false);
                lowRb.setChecked(false);
                ultraHighRb.setChecked(false);
                compression_rate = 50;
            }
        });
        ultaHigh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ultraHighRb.setChecked(true);
                mediumRb.setChecked(false);
                highRb.setChecked(false);
                lowRb.setChecked(false);
                compression_rate = 15;
            }
        });

        jpeg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jpegRb.setChecked(true);
                pngRb.setChecked(false);
                webpRb.setChecked(false);
                format = Bitmap.CompressFormat.JPEG;
            }
        });
        png.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pngRb.setChecked(true);
                jpegRb.setChecked(false);
                webpRb.setChecked(false);
                format = Bitmap.CompressFormat.PNG;
            }
        });
        webp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webpRb.setChecked(true);
                pngRb.setChecked(false);
                jpegRb.setChecked(false);
                format = Bitmap.CompressFormat.WEBP;
            }
        });

        return v;
    }

    private void performCompress( final Uri selectedUri){
        compression_rate = 80;
        format = Bitmap.CompressFormat.JPEG;

        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle(null);

        alertDialogBuilder.setView(createDialog());
                /*CharSequence[] temp = {"Low","Medium","High","Ultra High"};
                alertDialogBuilder.setSingleChoiceItems(temp, 1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0){
                            compression_rate = 90;
                        } else if (which == 1){
                            compression_rate = 80;
                        } else if (which == 2){
                            compression_rate = 50;
                        } else if (which == 3){
                            compression_rate = 15;
                        }
                    }
                });
                */
        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //dialog.cancel();
                        //performCompress(selected_uri);
                        CompressAsynctask task = new CompressAsynctask();
                        task.execute(selectedUri);
                    }
                });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        File f = new File(photoPath);
                        f.delete();
                        camSelected = false;
                    }
                });
        alertDialogBuilder.show();
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        try {
            ContentResolver cr = getContext().getContentResolver();
            Cursor cursor = cr.query(contentURI, null, null, null, null);
            if (cursor == null) { // Source is Dropbox or other similar local file path
                result = contentURI.getPath();
            } else {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                result = cursor.getString(idx);
                cursor.close();
            }
        }catch (RuntimeException e){
            //Log.v("LAK","RuntimeException: "+e.getStackTrace());
            result = null;
        }
        return result;
    }

    public static long getFolderSize(File f) {
        long size = 0;
        if (f.isDirectory()) {
            for (File file : f.listFiles()) {
                size += getFolderSize(file);
            }
        } else {
            size=f.length();
        }
        return size;
    }

    public class CompressAsynctask extends AsyncTask<Uri,Void,String>{
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Uri... uri) {
            String outpath, size;
            Bitmap bmp=null;
            //setting the path of file where image will  be stored
            outpath = Environment.getExternalStorageDirectory() + "/Image Solution";
            File outfile = new File(outpath);
            outfile.mkdirs();
            if(format == Bitmap.CompressFormat.JPEG) {
                outpath = outpath + "/Pic_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".jpeg" ;
            }else if(format == Bitmap.CompressFormat.PNG) {
                outpath = outpath + "/Pic_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".png" ;
            }else if(format == Bitmap.CompressFormat.WEBP) {
                outpath = outpath + "/Pic_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".webp" ;
            }

            outfile = new File(outpath);
            try {
                outfile.createNewFile();
                InputStream is;
                if(camSelected){
                    is = new FileInputStream(uri[0].getPath());
                }else {
                    is = getContext().getContentResolver().openInputStream(uri[0]);
                }
                bmp = BitmapFactory.decodeStream(is);
                is.close();
                FileOutputStream fo;
                fo = new FileOutputStream(outfile);
                bmp.compress(format , compression_rate ,fo);
                fo.close();
            } catch (FileNotFoundException e) {
                return "Something went wrong . Try Again ";
            } catch (IOException e) {
                return "Something went wrong . Try Again ";
            }

            //getting the size of the output file
            long Filesize = getFolderSize(outfile)/1024;    //call function and convert bytes into Kb
            if(Filesize>=1024)
                size = String.format("%.2f", (float) Filesize / 1024) + " Mb";
            else size = Filesize+" Kb";

            return "File is successfully compressed of Size " + size;
        }

        @Override
        protected void onPostExecute(String s) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getContext() , s , Toast.LENGTH_LONG ).show();
            Answers.getInstance().logCustom(new CustomEvent("Lakshay")
                    .putCustomAttribute("Compress Image","Ended"));
            if(HomeFragment.camSelected) {
                File f = new File(photoPath);
                f.delete();
                HomeFragment.camSelected = false;
            }
            mainActivity.refreshList();
        }
    }
}

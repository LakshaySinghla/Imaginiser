package com.lakshaysinghla.imaginiserdev1;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Lakshay Singhla on 12-Jul-17.
 */

public class ResizeActivity extends AppCompatActivity implements View.OnClickListener{
    ImageView original_img;
    //TextView original_name, original_size;
    TextView original_dimen;
    TextView tv1, tv2, tv3, tv4, tv5;
    EditText et_heigth , et_width;
    Button done ;
    RelativeLayout progressBar;
    Bitmap.CompressFormat format;
    Uri uri;

    int ori_height, ori_width, new_height, new_width,
            height1, height2, height3, height4, height5, width1, width2, width3, width4, width5;
    float ratio;

    boolean heightHasFocus=false , widthHasFocus = false;
    String outpath;
    InputStream is;
    FileOutputStream fo;
    Bitmap bmp, new_bmp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resize);

        progressBar = (RelativeLayout) findViewById(R.id.resize_progress_bar);
        original_img = (ImageView) findViewById(R.id.photo_original);
        //original_name = (TextView) findViewById(R.id.photo_original_name);
        //original_size = (TextView) findViewById(R.id.photo_original_size);
        original_dimen = (TextView) findViewById(R.id.photo_original_dimen);
        et_heigth = (EditText) findViewById(R.id.enter_height);
        et_width = (EditText) findViewById(R.id.enter_width);
        done = (Button) findViewById(R.id.done);
        tv1 = (TextView) findViewById(R.id.twentyfive);
        tv2 = (TextView) findViewById(R.id.thirty);
        tv3 = (TextView) findViewById(R.id.fifty);
        tv4 = (TextView) findViewById(R.id.sixty);
        tv5 = (TextView) findViewById(R.id.seventyfive);
        tv1.setOnClickListener(this);
        tv2.setOnClickListener(this);
        tv3.setOnClickListener(this);
        tv4.setOnClickListener(this);
        tv5.setOnClickListener(this);

        Bundle extras = getIntent().getExtras();
        uri = Uri.parse(extras.getString("uri"));
        if(HomeFragment.camSelected){
            Glide.with(this)
                    .load(new File(uri.getPath()))
                    .into(original_img);
        }else {
            Glide.with(this)
                    .load(uri)
                    .into(original_img);
        }
        //String size = "";
        //File f;
        try {
            if(HomeFragment.camSelected){
                is = new FileInputStream(uri.getPath());
            }else {
                is = getContentResolver().openInputStream(uri);
            }
            //f = new File(uri.getPath());
            bmp = BitmapFactory.decodeStream(is);
            ori_height = bmp.getHeight();
            ori_width = bmp.getWidth();
            is.close();
            //size = null;
            //long Filesize = getFolderSize(f)/1024;    //call function and convert bytes into Kb
            //long Filesize = is.available();
            //if(Filesize>=1024) size=(float) Filesize/1024 + " Mb";
            //else size = Filesize+" Kb";
            //original_name.setText(f.getName());
            //original_size.setText(size);
            original_dimen.setText( ori_height+" x "+ori_width );
        }catch (FileNotFoundException e){
            Toast.makeText(this, "Something went wrong.Try Again", Toast.LENGTH_LONG).show();
            //Log.v("LAK","FileNotFound:"+e.toString());
        }catch (IOException e){
            Toast.makeText(this, "Something went wrong.Try Again", Toast.LENGTH_LONG).show();
            //Log.v("LAK","IOException:"+e.toString());
        }
        height1 = (int)(ori_height*0.25);
        width1 = (int)(ori_width*0.25);
        height2 = (int)(ori_height*0.3);
        width2 = (int)(ori_width*0.3);
        height3 = (int)(ori_height*0.5);
        width3 = (int)(ori_width*0.5);
        height4 = (int)(ori_height*0.6);
        width4 = (int)(ori_width*0.6);
        height5 = (int)(ori_height*0.75);
        width5 = (int)(ori_width*0.75);

        tv1.setText( height1 + " X " + width1 + "  (25%)" );
        tv2.setText( height2 + " X " + width2 + "  (30%)" );
        tv3.setText( height3 + " X " + width3 + "  (50%)" );
        tv4.setText( height4 + " X " + width4 + "  (60%)" );
        tv5.setText( height5 + " X " + width5 + "  (75%)" );

        et_heigth.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    heightHasFocus = true;
                    et_heigth.addTextChangedListener(myTextWatcher);
                }else{
                    heightHasFocus= false;
                    et_heigth.removeTextChangedListener(myTextWatcher);
                }
            }
        });
        et_width.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    widthHasFocus = true;
                    et_width.addTextChangedListener(myTextWatcher);
                }else{
                    widthHasFocus = false;
                    et_width.removeTextChangedListener(myTextWatcher);
                }
            }
        });

        done.setOnClickListener(this);

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
                    ResizeActivity.super.onBackPressed();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    HomeFragment.camSelected = false;
                    ResizeActivity.super.onBackPressed();
                }
            });
            builder.create().show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.twentyfive){
            if(heightHasFocus){
                et_heigth.setText(height1+"");
            }else if(widthHasFocus){
                et_width.setText(width1+"");
            }
        }else if(v.getId() == R.id.thirty){
            if(heightHasFocus){
                et_heigth.setText(height2+"");
            }else if(widthHasFocus){
                et_width.setText(width2+"");
            }
        }else if(v.getId() == R.id.fifty){
            if(heightHasFocus){
                et_heigth.setText(height3+"");
            }else if(widthHasFocus){
                et_width.setText(width3+"");
            }
        }else if(v.getId() == R.id.sixty){
            if(heightHasFocus){
                et_heigth.setText(height4+"");
            }else if(widthHasFocus){
                et_width.setText(width4+"");
            }
        }else if(v.getId() == R.id.seventyfive){
            if(heightHasFocus){
                et_heigth.setText(height5+"");
            }else if(widthHasFocus){
                et_width.setText(width5+"");
            }
        }else if (v.getId() == R.id.done) {

            if (new_height <= ori_height && new_width <= ori_width) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Select Extension");
                CharSequence[] ext = {"Jpeg","Png","Webp"};
                format = Bitmap.CompressFormat.JPEG;
                builder.setSingleChoiceItems(ext, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0){
                            format = Bitmap.CompressFormat.JPEG;
                        }else if (which == 1){
                            format = Bitmap.CompressFormat.PNG;
                        }else if (which == 2){
                            format = Bitmap.CompressFormat.WEBP;
                        }
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ResizeAsyncTask task = new ResizeAsyncTask();
                        task.execute();
                    }
                });
                alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertDialog.show();
            }
            else{
                if(new_height > ori_height){
                    et_heigth.setError("Height is too large");
                    et_heigth.requestFocus();
                }
                else if(new_width > ori_width){
                    et_width.setError("Width is too large");
                    et_width.requestFocus();
                }
            }

        }

    }

    private final TextWatcher myTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() != 0) {
                if (heightHasFocus) {
                    new_height = Integer.parseInt(s.toString());
                    ratio = (float)ori_height / (float)new_height;
                    float temp = (float)ori_width / ratio;
                    new_width = (int)temp;
                    et_width.setText(new_width + "");
                } else if (widthHasFocus) {
                    new_width = Integer.parseInt(s.toString());
                    ratio = (float)ori_width / (float)new_width;
                    float temp = (float) ori_height / ratio;
                    new_height = (int)temp;
                    et_heigth.setText(new_height + "");
                }
            }
            else{
                if(heightHasFocus){
                    et_width.setText("");
                }
                else if(widthHasFocus){
                    et_heigth.setText("");
                }
            }
        }
    };

    public class ResizeAsyncTask extends AsyncTask<Void,Void,String>{
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {

            outpath = Environment.getExternalStorageDirectory() + "/Image Solution";
            File outfile = new File(outpath);
            outfile.mkdirs();
            if(format == Bitmap.CompressFormat.JPEG){
                outpath = outpath + "/Pic_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".jpeg";
            }else if (format == Bitmap.CompressFormat.PNG){
                outpath = outpath + "/Pic_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".png";
            } else {
                outpath = outpath + "/Pic_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".webp";
            }
            outfile = new File(outpath);
            try {
                outfile.createNewFile();
                fo = new FileOutputStream(outfile);
                new_bmp = Bitmap.createScaledBitmap(bmp, new_width, new_height, false);
                new_bmp.compress(format, 95, fo);
                fo.close();
            } catch (FileNotFoundException e) {
                return "Something went wrong.Try Again";
            } catch (IOException e) {
                return "Something went wrong.Try Again";
            }
            return "File Resized";
        }

        @Override
        protected void onPostExecute(String s) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(ResizeActivity.this,s,Toast.LENGTH_LONG).show();
            Answers.getInstance().logCustom(new CustomEvent("Lakshay")
                    .putCustomAttribute("Resize Image","Ended"));
            if(HomeFragment.camSelected) {
                File f = new File(uri.getPath());
                f.delete();
                HomeFragment.camSelected = false;
            }
            //MainActivity.refreshList();
            finish();
        }
    }

}

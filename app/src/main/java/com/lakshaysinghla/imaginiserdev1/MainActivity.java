package com.lakshaysinghla.imaginiserdev1;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    public static BottomNavigationView navigation ;
    public static ArrayList<PhotoListItem> list = new ArrayList<>();
    public static ArrayList<Uri> uri_list = new ArrayList<>();
    static String path;
    RelativeLayout progressBar ;
    static boolean refresh = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        float width = displayMetrics.widthPixels;
        float d = displayMetrics.density;
        width = width/d;
        */
        //Log.v("LAK" ," width: " +width + ": Density" +d);
        progressBar = (RelativeLayout) findViewById(R.id.pb);

        HomeFragment homeFragment = new HomeFragment();
        homeFragment.getMainActivity(this);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content , homeFragment);
        ft.commit();

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

    public BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedfragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    HomeFragment temp1 = (HomeFragment)getSupportFragmentManager().findFragmentByTag("Home");
                    if(temp1 == null) {
                        selectedfragment = HomeFragment.newFragment();
                        ((HomeFragment)selectedfragment).getMainActivity(MainActivity.this);
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.setCustomAnimations(R.anim.home_enter, R.anim.dashboard_exit);
                        ft.replace(R.id.content, selectedfragment, "Home");
                        //ft.addToBackStack(null);
                        ft.commit();
                    }
                     break;
                case R.id.navigation_dashboard:
                    DashboardFragment temp2 = (DashboardFragment) getSupportFragmentManager().findFragmentByTag("Dashboard");
                    if(temp2 == null) {
                        selectedfragment = DashboardFragment.newFragment();
                        ((DashboardFragment) selectedfragment).getMainActivity(MainActivity.this);
                        FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
                        ft1.setCustomAnimations(R.anim.dasboard_enter, R.anim.home_exit);
                        ft1.replace(R.id.content, selectedfragment, "Dashboard");
                        ft1.commit();
                    }
                    break;
            }
            return true;
        }

    };

    Set<String> getPreference(String key){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> set = new HashSet<>();
        if(pref.contains(key)){
            set = pref.getStringSet(key,null);
        }
        return set;
    }

    public void refreshList(){
        MyAsyncTask task = new MyAsyncTask();
        task.execute();
    }

    private class MyAsyncTask extends AsyncTask<Void,Void,String> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            Set<String> set = getPreference("list");
            path = Environment.getExternalStorageDirectory() + "/Image Solution" ;
            File file = new File(path);
            file.mkdirs();
            list.clear();
            uri_list.clear();
            for (File f : file.listFiles()) {
                PhotoListItem pli = new PhotoListItem();
                try {
                    if( !set.contains(f.getName()) ) {
                        //uri_list.add(Uri.fromFile(f));
                        //Log.v("LAK" , "Uri from Uri.fromfile: "+Uri.fromFile(f));
                        //Log.v("LAKLAK", "name:" + f.getName() + " path: " + Uri.parse(f.getAbsolutePath()) + " lstm: " + f.lastModified());
                        pli.setName(f.getName());
                        pli.setPath(f.getAbsolutePath());

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(f.lastModified());
                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH);
                        int date = calendar.get(Calendar.DATE);
                        pli.setCreatedAt(date + "/" + month + "/" + year);

                        String size = null;
                        long Filesize = getFolderSize(f) / 1024;    //call function and convert bytes into Kb
                        if (Filesize >= 1024)
                            size = String.format("%.2f", (float) Filesize / 1024) + " Mb";
                        else size = Filesize + " Kb";
                        pli.setSize(size);

                        Bitmap bmp = BitmapFactory.decodeFile(f.getAbsolutePath());
                        pli.setWidth(bmp.getWidth());
                        pli.setHeight(bmp.getHeight());
                        list.add(pli);
                    }
                }catch (NullPointerException e){
                    boolean x= f.delete();
                    return "Some files weren't properly saved last time, so deleting them";
                }
            }
            Collections.sort(list, new Comparator<PhotoListItem>() {
                @Override
                public int compare(PhotoListItem o1, PhotoListItem o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });
            for(PhotoListItem item : list){
                uri_list.add( Uri.fromFile( new File(item.getPath()) ) );
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            progressBar.setVisibility(View.GONE);
            if(s.length() != 0){
                Toast.makeText(MainActivity.this,s,Toast.LENGTH_LONG).show();
            }
        }
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

    @Override
    protected void onResume() {
        super.onResume();
        if (refresh) {
            refreshList();
        }
        refresh = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        //list.clear();
        //uri_list.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.about:
                Intent i = new Intent(this, AboutUsActivity.class);
                startActivity(i);
                return true;
            case R.id.rateus:
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                // To count with Play market backstack, After pressing back button,
                // to taken back to our application, we need to add following flags to intent.
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

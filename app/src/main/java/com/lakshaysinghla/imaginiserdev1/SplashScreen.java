package com.lakshaysinghla.imaginiserdev1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

/**
 * Created by Lakshay Singhla on 15-Jul-17.
 */

public class SplashScreen extends AppCompatActivity {

    boolean writeAccepted;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        if(hasPermission("android.permission.READ_EXTERNAL_STORAGE")){
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        }
        else askingpermission();

    }

    public boolean hasPermission(String permission){
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1){
            return(checkSelfPermission(permission)== PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE")==PackageManager.PERMISSION_GRANTED);
        }
        return true;
    }

    public void askingpermission(){
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1){
            String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE , Manifest.permission.WRITE_EXTERNAL_STORAGE} ;//{"android.permission. WRITE_EXTERNAL_STORAGE"};
            int permsRequestCode = 200;
            requestPermissions(perms, permsRequestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case 200:
                writeAccepted = grantResults[0]== PackageManager.PERMISSION_GRANTED;
                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
                finish();
                break;
            default: finish();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}

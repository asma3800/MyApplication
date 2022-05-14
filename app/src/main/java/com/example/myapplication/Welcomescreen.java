package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Admin.AdminHomeScreen;
import com.example.myapplication.User.Items;

public class Welcomescreen extends AppCompatActivity {
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcomescreen);


        Thread thread = new Thread () {
        @Override
        public void run() {
        try {
        sleep(1000);

            saveToCache();

        } catch (InterruptedException e) {
        e.printStackTrace();
         }
         }
        };
        thread.start();
    }
    void saveToCache() {
        sharedpreferences = getSharedPreferences("new", 0);
        editor = sharedpreferences.edit();
//        editor.remove("logged_in");
//        Toast.makeText(this, String.valueOf(sharedpreferences.contains("logged_in")), Toast.LENGTH_SHORT).show();
//        Log.d("asd" ,String.valueOf(sharedpreferences.contains("logged_in")));
        if (!sharedpreferences.contains("user_type")) {
                Intent intent = new Intent(getApplicationContext() , Introscreen1.class); // introscreen1.class);
                startActivity(intent);
                finish();

        } else {
            String type =  sharedpreferences.getString("user_type", "");
            if (type.equals("user")) {
                Intent intent = new Intent(getApplicationContext() , Items.class); // introscreen1.class);
                startActivity(intent);
                finish();
            }
            else if (type.equals("admin")){
                Intent intent = new Intent(getApplicationContext() , AdminHomeScreen.class); // introscreen1.class);
                startActivity(intent);
                finish();
            }
            else {
                Intent intent = new Intent(getApplicationContext() , Register.class); // introscreen1.class);
                startActivity(intent);
                finish();
            }
//            Toast.makeText(OrderInformation.this, String.valueOf(sharedpreferences.contains("logged_in")), Toast.LENGTH_SHORT).show();
        }
//        Toast.makeText(OrderInformation.this, String.valueOf(orderNumber), Toast.LENGTH_SHORT).show();
    }
//    public void checkMyPermission() {
//        Dexter.withContext(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
//            @Override
//            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
//                Toast.makeText(welcomescreen.this, "permission granted", Toast.LENGTH_LONG).show();
////                isPermissionGranted = true;
//            }
//
//            @Override
//            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
//                Intent intent = new Intent();
//                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                Uri uri = Uri.fromParts("package", getPackageName(), "");
//                intent.setData(uri);
//                startActivity(intent);
//            }
//
//            @Override
//            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
//                permissionToken.continuePermissionRequest();
//            }
//        }).check();
//    }
}
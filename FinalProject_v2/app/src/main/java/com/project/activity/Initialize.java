package com.project.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.gson.Gson;
import com.project.util.Const;

public class Initialize extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent;
        Gson gson = new Gson();

        SharedPreferences mPref =getApplicationContext().getSharedPreferences(Const.SHARED_PREFERENCE, MODE_PRIVATE);
        String userData = mPref.getString(Const.USER_KEY, "");
        if(userData == null || userData.compareTo("") == 0){
            intent = new Intent(this, LoginActivity.class);
        }else{
            intent = new Intent(this, MainActivity.class);
        }

        startActivity(intent);
    }
}
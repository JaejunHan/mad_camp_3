package com.example.reappearance01;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class StartSplashActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        try{

            Thread.sleep(2000);


        }catch (InterruptedException e) {

            e.printStackTrace();

        }


        Intent intent=new Intent(this, MainActivity.class);

        startActivity(intent);

        finish();




    }

}
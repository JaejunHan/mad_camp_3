package com.example.reappearance01;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.reappearance01.databinding.ActivityMainBinding;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity {
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityMainBinding binding;
    ImageButton search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        
        init();
        
        search = (ImageButton) findViewById(R.id.search_result);

        //검색버튼
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // intent로 다른 화면 띄위게 해주면 됨
                Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
                startActivity(intent);
            }
        });
    }

    private void init() {

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == 9001) {
                Intent intent = result.getData();
                String name = intent.getStringExtra("apple");
                Integer number = intent.getIntExtra("number",  0);
                Toast.makeText(getApplicationContext(), "fuck", Toast.LENGTH_SHORT).show();
                binding.textView4.setText(name);

            }
        });

        binding.fromPlaceText.setOnClickListener(v -> {
            Toast.makeText(this,"hey",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), SuperKotlinActivity.class);
            activityResultLauncher.launch(intent);
        });

    }

}
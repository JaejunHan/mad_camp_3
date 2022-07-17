package com.example.reappearance01;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.reappearance01.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main);

        init();
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
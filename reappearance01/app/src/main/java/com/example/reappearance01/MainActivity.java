package com.example.reappearance01;

import static android.content.ContentValues.TAG;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity {
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityMainBinding binding;
    ImageButton search;
    SearchResultEntity searchFromResult;
    SearchResultEntity searchToResult;
    ArrayList<PathSavedData> read_data = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        String dirPath = getFilesDir().getAbsolutePath();
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
            Log.d(TAG, "없어서 디렉토리 만들었음요"+dirPath);
        }
        File file = new File(dir, "/trackSearchList.txt");
        try {
            file.createNewFile();
            Log.d(TAG, "read_data 생성은 됨");
        }catch(Exception e){
            Log.e(TAG, "read_data 생성도 안돼요.");
        }
        String testStr = "ABCDEFGHIJK...";
        try{
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(testStr.getBytes());
            fos.close();
            Log.d(TAG, "teststr쓰임,"+file);
            //ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            //read_data = (ArrayList<PathSavedData>)ois.readObject();
            //Log.d(TAG, "read_data 띄워줄게. "+read_data.toString());
            //ois.close();
        }catch (Exception ex) {
            Log.e(TAG, "read_data 없는데요?");
        }

        Intent intent = getIntent();
        if (intent.hasExtra("SearchFromData")) {
            searchFromResult = intent.getParcelableExtra("SearchFromData");
            binding.fromPlaceText.setText(searchFromResult.getName());
            ((GlobalSearchResult)getApplication()).setFromName(searchFromResult.getName());
            ((GlobalSearchResult)getApplication()).setFromFullAddress(searchFromResult.getFullAddress());
            ((GlobalSearchResult)getApplication()).setFromLocation(searchFromResult.getLocationLatLng());
        }
        if (intent.hasExtra("SearchToData")) {
            searchToResult = intent.getParcelableExtra("SearchToData");
            binding.toPlaceText.setText(searchToResult.getName());
            ((GlobalSearchResult)getApplication()).setToName(searchToResult.getName());
            ((GlobalSearchResult)getApplication()).setToFullAddress(searchToResult.getFullAddress());
            ((GlobalSearchResult)getApplication()).setToLocation(searchToResult.getLocationLatLng());
        }
        binding.fromPlaceText.setText(((GlobalSearchResult)getApplication()).getFromName());
        binding.toPlaceText.setText(((GlobalSearchResult)getApplication()).getToName());

        binding.switchFromAndToButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String bufFromName = ((GlobalSearchResult)getApplication()).getFromName();
                String bufFromFullAddress = ((GlobalSearchResult)getApplication()).getFromFullAddress();
                LocationLatLngEntity bufFromLocationLatLng = ((GlobalSearchResult)getApplication()).getFromLocation();
                ((GlobalSearchResult)getApplication()).setFromName(((GlobalSearchResult)getApplication()).getToName());
                ((GlobalSearchResult)getApplication()).setFromFullAddress(((GlobalSearchResult)getApplication()).getToFullAddress());
                ((GlobalSearchResult)getApplication()).setFromLocation(((GlobalSearchResult)getApplication()).getToLocation());
                ((GlobalSearchResult)getApplication()).setToName(bufFromName);
                ((GlobalSearchResult)getApplication()).setToFullAddress(bufFromFullAddress);
                ((GlobalSearchResult)getApplication()).setToLocation(bufFromLocationLatLng);
                binding.fromPlaceText.setText(((GlobalSearchResult)getApplication()).getFromName());
                binding.toPlaceText.setText(((GlobalSearchResult)getApplication()).getToName());

            }
        });



        init();
        
        search = (ImageButton) findViewById(R.id.search_result);

        //검색버튼
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // intent로 다른 화면 띄위게 해주면 됨
                Intent intent = new Intent(getApplicationContext(), SplashBeforeResult.class);
                intent.putExtra("FromLatLng", ((GlobalSearchResult)getApplication()).getFromLocation());
                intent.putExtra("FromName", ((GlobalSearchResult)getApplication()).getFromName());
                intent.putExtra("ToLatLng", ((GlobalSearchResult)getApplication()).getToLocation());
                intent.putExtra("ToName", ((GlobalSearchResult)getApplication()).getToName());
                PathSavedData pathSavedDataToAdd = new PathSavedData(
                        ((GlobalSearchResult)getApplication()).getFromName(),
                        ((GlobalSearchResult)getApplication()).getFromFullAddress(),
                        ((GlobalSearchResult)getApplication()).getFromLocation(),
                        ((GlobalSearchResult)getApplication()).getToName(),
                        ((GlobalSearchResult)getApplication()).getToFullAddress(),
                        ((GlobalSearchResult)getApplication()).getToLocation()
                );
                read_data.add(pathSavedDataToAdd);
                String dirPath = getFilesDir().getAbsolutePath();
                File dir = new File(dirPath);
                File filex = new File(dir, "/trackSearchList.ser");
                try{
                    FileOutputStream fosnew = new FileOutputStream(filex);
                    ObjectOutputStream oos = new ObjectOutputStream(fosnew);
                    oos.writeObject(read_data);
                    Log.d(TAG, "read_data 띄워줄게. "+read_data.toString());
                    oos.close();
                } catch (Exception ex) {
                    Log.d(TAG, "read_data 또 없어요."+read_data.toString());
                }
                startActivity(intent);
            }
        });
    }

    private void init() {

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == 9001) {
                Intent intent = result.getData();

                binding.textView4.setText("hey");

            }
        });

        binding.fromPlaceText.setOnClickListener(v -> {
            Toast.makeText(this,"hey",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), SuperKotlinActivity.class);
            activityResultLauncher.launch(intent);
        });

        binding.toPlaceText.setOnClickListener(v -> {
            Toast.makeText(this,"hey",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), SearchToActivity.class);
            activityResultLauncher.launch(intent);
        });

    }

}
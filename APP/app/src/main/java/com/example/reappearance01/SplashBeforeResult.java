package com.example.reappearance01;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

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

public class SplashBeforeResult extends AppCompatActivity {
    private String localhost = "https://72f7-210-222-224-106.jp.ngrok.io";
    private JSONArray server_results = new JSONArray();     // 서버와 통신 후 막 받을 데이터
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_before_result);

        // 비디오뷰 가져오기
        VideoView mVideoView = (VideoView) findViewById(R.id.video_splash);

        // sample.mp4 설정
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/raw/loading_page");
        mVideoView.setVideoURI(uri);
        // 리스너 등록
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                // 준비 완료되면 비디오 재생
                mp.start();
            }
        });

        Intent intent = getIntent();
        LocationLatLngEntity ghfromLatLng = intent.getParcelableExtra("FromLatLng");
        LocationLatLngEntity ghtoLatLng = intent.getParcelableExtra("ToLatLng");
        String ghfromName = intent.getStringExtra("FromName");
        String ghfromNameReplaced = ghfromName.replace("[","");
        String ghfromNameReplaced2 = ghfromNameReplaced.replace("]","");
        String ghtoName = intent.getStringExtra("ToName");
        String ghtoNameReplaced = ghtoName.replace("[","");
        String ghtoNameReplaced2 = ghtoNameReplaced.replace("]","");
        // 서버와 통신하는 부분
        // 일단 위도, 경도를 하드코딩해둠.

        String latitude_from = Float.toString(ghfromLatLng.getLatitude());
        String longitude_from = Float.toString(ghfromLatLng.getLongitude());
        String latitude_to = Float.toString(ghtoLatLng.getLatitude());
        String longitude_to = Float.toString(ghtoLatLng.getLongitude());
        Log.d(TAG, "기현서버리퀘스트 요청.."+latitude_from+" "+longitude_from+" "+latitude_to+" "+longitude_to+" " + ghfromNameReplaced2 + " "+ ghtoNameReplaced2);
        // request(latitude_from, longitude_from, ghfromName, latitude_to, longitude_to, ghtoName);
        request(longitude_from, latitude_from, ghfromNameReplaced2, longitude_to, latitude_to, ghtoNameReplaced2);


        //request("126.8966655", "37.4830969", "출발지이름", "127.0276368", "37.4979502", "도착지이름");
    }
    public void request(String latitude_from, String longitude_from, String start, String latitude_to, String longitude_to, String end){
        //url 요청주소 넣는 editText를 받아 url만들기
        String url = localhost + "/path_find";
        //JSON형식으로 데이터 통신을 진행합니다!
        JSONObject testjson = new JSONObject();
        try {
            //입력해둔 edittext의 id와 pw값을 받아와 put해줍니다 : 데이터를 json형식으로 바꿔 넣어주었습니다.
            testjson.put("latitude_from", latitude_from);
            testjson.put("longitude_from", longitude_from);
            testjson.put("start", start);
            testjson.put("latitude_to", latitude_to);
            testjson.put("longitude_to", longitude_to);
            testjson.put("end", end);
            String jsonString = testjson.toString(); //완성된 json 포맷

            //이제 전송해볼까요?
            final RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

            System.out.println("zxcvasfsfasdfsdf");
            final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,testjson, new Response.Listener<JSONObject>() {

                //데이터 전달을 끝내고 이제 그 응답을 받을 차례입니다.
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        System.out.println("데이터전송 성공");

                        //받은 json형식의 응답을 받아
                        JSONObject jsonObject = new JSONObject(response.toString());

                        //key값에 따라 value값을 쪼개 받아옵니다.
                        String jsonWholeArray = jsonObject.getString("jsonArray");
                        JSONArray jsonArray = new JSONArray(jsonWholeArray);
                        server_results = jsonArray; // 로컬 변수에 검색 결과를 저장

                        Log.d(TAG,"서버 조회 결과"+ server_results.toString());
                        Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
                        intent.putExtra("server_results", server_results.toString());
                        startActivity(intent);
                    } catch (Exception e) {

                        System.out.println("에러발생에러발생에러발생에러발생에러발생에러발생");
                        e.printStackTrace();
                    }
                }
                //서버로 데이터 전달 및 응답 받기에 실패한 경우 아래 코드가 실행됩니다.
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("뭔가 이상해요");
                    error.printStackTrace();
                    System.out.println(error);
                    System.out.println("오아아ㅘ아ㅏ아아ㅏㅏㅇㅇㅇㅇㅇㅇㅇㅇ앙");
                    //Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                }
            });
            jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {return 50000000;}
                @Override
                public int getCurrentRetryCount() {return 50000000;}
                @Override
                public void retry(VolleyError error) throws VolleyError {return;}
            });
            requestQueue.add(jsonObjectRequest);
            //
        } catch (JSONException e) {
            System.out.println("에러발생에러발생에러발생에러발생에러발생에러발생12312312312321");
            e.printStackTrace();
        }
    }



}

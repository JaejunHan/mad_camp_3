package com.example.reappearance01;

import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ResultActivity extends AppCompatActivity {

    public ListView listView;
    private JSONArray server_results = new JSONArray();     // 서버와 통신 후 막 받을 데이터
    private JSONArray final_json_array = new JSONArray();   //
    private JSONArray final_json_array_element = new JSONArray();   // 화면에 띄울 각각의 아이템
    private String type = "";

    private double walking_speed = 1.0f;

    SeekBar seekBar;
    TextView speed_textview;
    ImageButton speed_button;
    LinearLayout kind_of_transport;
    ResultAdapter resultAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        kind_of_transport = (LinearLayout) findViewById(R.id.kind_of_transport);
        listView = (ListView) findViewById(R.id.result_list_view);
        speed_button = (ImageButton) findViewById(R.id.rum_button);
        speed_textview = (TextView) findViewById(R.id.speed);
        seekBar = findViewById(R.id.seekBar);

        Intent intent = getIntent();
        String jsonArray = intent.getStringExtra("server_results");

        try {
            server_results = new JSONArray(jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /*

        resultAdapter = new ResultAdapter(getApplicationContext(), final_json_array);
        listView.setAdapter(resultAdapter);
        resultAdapter.notifyDataSetChanged();
        */
        try {
            show_calculated_results(walking_speed);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                speed_textview.setText(String.format("달리기 x%.1f", seekBar.getProgress() / 10.0));
                System.out.println(walking_speed);
                System.out.println("애애애앵애애");
                walking_speed = seekBar.getProgress() / 10.0;
                try {
                    show_calculated_results(walking_speed);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void show_calculated_results(double walking_speed) throws JSONException {
        Calendar cal = Calendar.getInstance();
        //출력용으로 Calendar 클래스에서 Date 클래스를 가져옵니다.
        Date date = cal.getTime();

        SimpleDateFormat hour = new SimpleDateFormat("kk");
        String hour_str = hour.format(date);

        SimpleDateFormat minute = new SimpleDateFormat("mm");
        String minute_str = minute.format(date);

        final_json_array = new JSONArray(); // 초기화
        int what_hour = 0;  // 이 값이 0이면 현재 hour, 1이면 현재시간 +1, 2이면 현재시간 +2
        for (int i = 0; i < server_results.length(); i++){   // 각각의 경로에 따라
            double time_spent = 0.0f;
            final_json_array_element = new JSONArray();  // 초기화
            int is_route_calculateable = 1; // 계산 가능한 경로인지(버싀의 경우 배차간격이 제공이 안되면 그 루트 제외시킴.)
            JSONArray each_path_result = new JSONArray(server_results.get(i).toString());
            for (int j=0; j < each_path_result.length(); j++){  // 각각의 교통수단에 따라
                JSONObject json_object = new JSONObject(each_path_result.get(j).toString());
                type = (String) json_object.get("type");
                if (type.equals("0")){  // 도보의 경우
                    double time_take_to_walk = (double) Integer.parseInt((String)json_object.get("time"));
                    double v = time_take_to_walk / walking_speed;
                    time_spent += v;
                    json_object.put("time", Double.toString(v));
                    final_json_array_element.put(json_object);

                    // time_future = 앞의 대중교통을 탔다고 가정했을 때의 시간
                    int[] result = calculate_hour_minutes(hour_str, minute_str, v);
                    hour_str = time_format(Integer.toString(result[0]));
                    minute_str = time_format(Integer.toString(result[1]));
                    what_hour += result[2];
                }else if (type.equals("1")){    // 지하철의 경우
                    int is_there_next = 0;
                    if (what_hour == 0){    // 만약 현재 시각에서 지하철을 탄 경우
                        JSONArray timetable  = new JSONArray(json_object.get("timetable0").toString());
                         for (int k=0; k < timetable.length() ; k++){
                             if (minute_str.compareTo((String) timetable.get(k)) <=0){
                                int time_to_wait = Integer.parseInt((String) timetable.get(k)) - Integer.parseInt(minute_str);
                                if (time_to_wait > 0){  // 지하철을 기다려야하는 지하철 시간이 있으면
                                    is_there_next = 1;
                                    JSONObject wait_json_object = new JSONObject();
                                    wait_json_object.put("time", Integer.toString(time_to_wait));
                                    wait_json_object.put("type", "4");  //4는 다음 대중교통이 오는 데까지 기다리는 시간
                                    wait_json_object.put("place", "다음 대중교통까지 대기 시간");
                                    final_json_array_element.put(wait_json_object);
                                    time_spent += time_to_wait;
                                    int[] result = calculate_hour_minutes(hour_str, minute_str, (double) time_to_wait);
                                    hour_str = time_format(Integer.toString(result[0]));
                                    minute_str = time_format(Integer.toString(result[1]));
                                    what_hour += result[2];
                                }
                                break;
                             }
                         }
                         if (is_there_next == 0){
                             JSONArray timetable_1  = new JSONArray(json_object.get("timetable1").toString());
                             for (int k=0; k < timetable_1.length() ; k++){
                                 if (minute_str.compareTo((String) timetable_1.get(k)) <=0){
                                     int time_to_wait = Integer.parseInt((String) timetable_1.get(k)) - Integer.parseInt(minute_str);
                                     if (time_to_wait > 0){  // 지하철을 기다려야하는 지하철 시간이 있으면
                                         time_spent += time_to_wait;
                                         JSONObject wait_json_object = new JSONObject();
                                         wait_json_object.put("time", Integer.toString(time_to_wait));
                                         wait_json_object.put("type", "4");  //4는 다음 대중교통이 오는 데까지 기다리는 시간
                                         wait_json_object.put("place", "다음 대중교통까지 대기 시간");
                                         final_json_array_element.put(wait_json_object);

                                         int[] result = calculate_hour_minutes(hour_str, minute_str, (double) time_to_wait);
                                         hour_str = time_format(Integer.toString(result[0]));
                                         minute_str = time_format(Integer.toString(result[1]));
                                         what_hour += result[2];
                                     }
                                     break;
                                 }
                             }
                         }
                    } else if (what_hour == 1){ //1시간 뒤 있는 시간표에서 지하철을 탄 경우
                        JSONArray timetable  = new JSONArray(json_object.get("timetable1").toString());
                        for (int k=0; k < timetable.length() ; k++){
                            if (minute_str.compareTo((String) timetable.get(k)) <=0){
                                int time_to_wait = Integer.parseInt((String) timetable.get(k)) - Integer.parseInt(minute_str);
                                if (time_to_wait > 0){  // 지하철을 기다려야하는 지하철 시간이 있으면
                                    time_spent += time_to_wait;
                                    JSONObject wait_json_object = new JSONObject();
                                    wait_json_object.put("time", Integer.toString(time_to_wait));
                                    wait_json_object.put("type", "4");  //4는 다음 대중교통이 오는 데까지 기다리는 시간
                                    wait_json_object.put("place", "다음 대중교통까지 대기 시간");
                                    final_json_array_element.put(wait_json_object);

                                    int[] result = calculate_hour_minutes(hour_str, minute_str, (double) time_to_wait);
                                    hour_str = time_format(Integer.toString(result[0]));
                                    minute_str = time_format(Integer.toString(result[1]));
                                    what_hour += result[2];
                                }
                                break;
                            }
                        }
                        if (is_there_next == 0){
                            JSONArray timetable_1  = new JSONArray(json_object.get("timetable2").toString());
                            for (int k=0; k < timetable_1.length() ; k++){
                                if (minute_str.compareTo((String) timetable_1.get(k)) <=0){
                                    int time_to_wait = Integer.parseInt((String) timetable_1.get(k)) - Integer.parseInt(minute_str);
                                    if (time_to_wait > 0){  // 지하철을 기다려야하는 지하철 시간이 있으면
                                        time_spent += time_to_wait;
                                        JSONObject wait_json_object = new JSONObject();
                                        wait_json_object.put("time", Integer.toString(time_to_wait));
                                        wait_json_object.put("type", "4");  //4는 다음 대중교통이 오는 데까지 기다리는 시간
                                        wait_json_object.put("place", "다음 대중교통까지 대기 시간");
                                        final_json_array_element.put(wait_json_object);

                                        int[] result = calculate_hour_minutes(hour_str, minute_str, (double) time_to_wait);
                                        hour_str = time_format(Integer.toString(result[0]));
                                        minute_str = time_format(Integer.toString(result[1]));
                                        what_hour += result[2];
                                    }
                                    break;
                                }
                            }
                        }
                    } else if (what_hour == 2){     // 2시간 뒤 있는 시간표에서 지하철을 탄 경우
                        JSONArray timetable  = new JSONArray(json_object.get("timetable2").toString());
                        for (int k=0; k < timetable.length() ; k++){
                            if (minute_str.compareTo((String) timetable.get(k)) <=0){
                                int time_to_wait = Integer.parseInt((String) timetable.get(k)) - Integer.parseInt(minute_str);
                                if (time_to_wait > 0){  // 지하철을 기다려야하는 지하철 시간이 있으면
                                    time_spent += time_to_wait;
                                    JSONObject wait_json_object = new JSONObject();
                                    wait_json_object.put("time", Integer.toString(time_to_wait));
                                    wait_json_object.put("type", "4");  //4는 다음 대중교통이 오는 데까지 기다리는 시간
                                    wait_json_object.put("place", "다음 대중교통까지 대기 시간");
                                    final_json_array_element.put(wait_json_object);

                                    int[] result = calculate_hour_minutes(hour_str, minute_str, (double) time_to_wait);
                                    hour_str = time_format(Integer.toString(result[0]));
                                    minute_str = time_format(Integer.toString(result[1]));
                                    what_hour += result[2];
                                }
                                break;
                            }
                        }
                    }
                    double time_take = (double) Integer.parseInt((String)json_object.get("time"));
                    final_json_array_element.put(json_object);
                    time_spent += time_take;

                    // time_future = 앞의 대중교통을 탔다고 가정했을 때의 시간
                    int[] result = calculate_hour_minutes(hour_str, minute_str, time_take);
                    hour_str = time_format(Integer.toString(result[0]));
                    minute_str = time_format(Integer.toString(result[1]));
                    what_hour += result[2];

                }else if (type.equals("2")){    // 버스의 경우
                    int interval = 0;
                    try {
                        interval = Integer.parseInt((String) json_object.get("interval"));
                    } catch (Exception e){
                        is_route_calculateable = 0;
                        System.out.println(e);
                        break;
                    }
                    String next_bus = "";
                    String next_next_bus = "";
                    int next_bus_int = -1;
                    int next_next_bus_int = -1;
                    try {
                        next_bus = (String) json_object.get("next_time");
                        next_next_bus = (String) json_object.get("next_next_time");
                    } catch (Exception e){
                        System.out.println(e);
                    }

                    if (next_bus.equals("곧도착")) {next_bus_int = 1;}
                    else {
                        if (!next_bus.equals("")) {
                            next_bus_int = Integer.parseInt(next_bus.replaceAll("[^0-9]", ""));
                        }
                    }
                    if (!next_next_bus.equals("")){
                        next_next_bus_int = Integer.parseInt(next_next_bus.replaceAll("[^0-9]", ""));
                    }

                    if (next_bus_int == -1){    // 바로 다음 버스가 몇분 뒤 도착한다는 얘기가 없을 대
                        JSONObject wait_json_object = new JSONObject();
                        wait_json_object.put("time", Integer.toString(interval));
                        wait_json_object.put("type", "4");  //4는 다음 대중교통이 오는 데까지 기다리는 시간
                        wait_json_object.put("place", "다음 대중교통까지 대기 시간");
                        final_json_array_element.put(wait_json_object);
                    } else if (next_next_bus_int == -1){    //  다다음 버스가 몇분 뒤 도착한다는 얘기가 없을 대
                        int time = 0;
                        if (time_spent > next_bus_int){
                            time = next_bus_int;
                            while (time < time_spent){
                                time += interval;
                            }
                            time -= time_spent;
                        } else{
                            time = next_bus_int - (int) time_spent;
                        }
                        JSONObject wait_json_object = new JSONObject();
                        wait_json_object.put("time", Integer.toString(time));
                        wait_json_object.put("type", "4");  //4는 다음 대중교통이 오는 데까지 기다리는 시간
                        wait_json_object.put("place", "다음 대중교통까지 대기 시간");
                        final_json_array_element.put(wait_json_object);
                    } else {    // 다음 버스, 다다음 버스에 대한 정보가 있을 떄
                        int time = 0;
                        if (time_spent > next_next_bus_int){
                            time = next_next_bus_int;
                            while (time < time_spent){
                                time += interval;
                            }
                            time -= time_spent;
                        } else if (time_spent > next_bus_int){
                            time = next_next_bus_int;
                            time -= time_spent;
                        } else{
                            time = next_bus_int - (int) time_spent;
                        }
                        JSONObject wait_json_object = new JSONObject();
                        wait_json_object.put("time", Integer.toString(time));
                        wait_json_object.put("type", "4");  //4는 다음 대중교통이 오는 데까지 기다리는 시간
                        wait_json_object.put("place", "다음 대중교통까지 대기 시간");
                        final_json_array_element.put(wait_json_object);
                    }

                    // 버스로 가는 데에 걸리는 시간
                    String time_take = (String) json_object.get("time");
                    final_json_array_element.put(json_object);
                    time_spent += Integer.parseInt(time_take);

                    int[] result = calculate_hour_minutes(hour_str, minute_str, Integer.parseInt(time_take));
                    hour_str = time_format(Integer.toString(result[0]));
                    minute_str = time_format(Integer.toString(result[1]));
                    what_hour += result[2];

                } else {    // 그냥 하차의 경우
                    final_json_array_element.put(json_object);
                }
            }
            if (is_route_calculateable == 1) {
                final_json_array.put(final_json_array_element);
            }
        }
        System.out.println(final_json_array);
        resultAdapter = new ResultAdapter(getApplicationContext(), final_json_array);
        listView.setAdapter(resultAdapter);
        resultAdapter.notifyDataSetChanged();
    }


    public int[] calculate_hour_minutes(String hour_str, String minute_str, double v){
        int[] outputs = new int[3];
        int time_future = Integer.parseInt(minute_str);
        time_future += Math.round(v);
        int quo = time_future / 60;  // 몫
        int remainder = time_future % 60;    // 나머지
        // 시간을 몫만큼 더해줌
        // todo 시간이 24시 이상일 때 처리해야함.
        int hour = Integer.parseInt(hour_str) + quo;
        outputs[0] = hour;
        outputs[1] = remainder;
        outputs[2] = quo;
        return outputs;
    }

    public String time_format(String time){ // 만약 오전인 경우 "06"이런형태로 저장될 수 있게함.
        String time_str = time;
        if (time_str.length() == 1) {time_str += "0"+time_str;} // 만약 오전인 경우 "06"이런형태로 저장될 수 있게함.
        return time_str;
    }
}

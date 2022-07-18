package com.example.reappearance01;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ResultAdapter extends BaseAdapter {
    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    private JSONArray final_json_array = new JSONArray();   //

    public ResultAdapter(Context context, JSONArray data) {
        mContext = context;
        final_json_array = data;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return final_json_array.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return final_json_array.get(position);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) { // inflator를 통해 각 View 들을 객체화, 데이터 값 지정
        View view = mLayoutInflater.inflate(R.layout.item_result, null);
        TextView total_time = view.findViewById(R.id.total_time);
        LinearLayout transports = (LinearLayout) view.findViewById(R.id.kind_of_transport);
        LinearLayout detail_information = (LinearLayout) view.findViewById(R.id.detail_information);

        JSONArray position_json_array = new JSONArray();
        try {
            position_json_array = new JSONArray(this.getItem(position).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(position_json_array);

        int len_array = position_json_array.length();

        int total_time_int = 0;
        for (int i=0; i < len_array; i++){
            JSONObject one_transport = new JSONObject();
            try {
                one_transport = new JSONObject(position_json_array.get(i).toString());
                total_time_int += (int) Double.parseDouble((String)one_transport.get("time"));

                String transport_type = (String) one_transport.get("type");
                double time = Double.parseDouble((String) one_transport.get("time"));
                System.out.println(time);
                String time_str = Integer.toString((int) time);
                LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                        0, 40, (float) time);
                TextView tv = new TextView(view.getContext());
                tv.setLayoutParams(lparams);
                if (transport_type.equals("0")){    //
                    tv.setText(time_str+"분"+" 도보");
                    tv.setBackgroundResource(R.drawable.gray_walk_background_square);
                    
                    LinearLayout.LayoutParams lparams_outside_linear_layout = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lparams_outside_linear_layout.setMargins(0,10,0,0);
                    LinearLayout outside_linear_layout = new LinearLayout(view.getContext());
                    outside_linear_layout.setLayoutParams(lparams_outside_linear_layout);
                    outside_linear_layout.setOrientation(LinearLayout.HORIZONTAL);
                    detail_information.addView(outside_linear_layout);

                    LinearLayout.LayoutParams lparams_tv_walk = new LinearLayout.LayoutParams(
                            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
                    TextView tv_walk = new TextView(view.getContext());
                    tv_walk.setLayoutParams(lparams_tv_walk);
                    tv_walk.setText("도보");
                    outside_linear_layout.addView(tv_walk);

                    LinearLayout.LayoutParams lparams_tv_walk_time = new LinearLayout.LayoutParams(
                            0, LinearLayout.LayoutParams.WRAP_CONTENT, 5.0f);
                    TextView tv_walk_time = new TextView(view.getContext());
                    tv_walk_time.setLayoutParams(lparams_tv_walk_time);
                    tv_walk_time.setText("소요시간: "+ Integer.toString((int) Double.parseDouble((String)one_transport.get("time"))) +"분");
                    outside_linear_layout.addView(tv_walk_time);

                } else if (transport_type.equals("1")) {
                    tv.setText(time_str+"분"+" 지하철");
                    tv.setBackgroundResource(R.drawable.blue_subway_background_square);

                    LinearLayout.LayoutParams lparams_outside_linear_layout = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lparams_outside_linear_layout.setMargins(0,10,0,0);
                    LinearLayout outside_linear_layout = new LinearLayout(view.getContext());
                    outside_linear_layout.setLayoutParams(lparams_outside_linear_layout);
                    outside_linear_layout.setOrientation(LinearLayout.VERTICAL);
                    detail_information.addView(outside_linear_layout);

                    LinearLayout.LayoutParams lparams_inner_linear_layout_1 = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    LinearLayout inner_linear_layout_1 = new LinearLayout(view.getContext());
                    inner_linear_layout_1.setLayoutParams(lparams_inner_linear_layout_1);
                    inner_linear_layout_1.setOrientation(LinearLayout.HORIZONTAL);
                    outside_linear_layout.addView(inner_linear_layout_1);

                    LinearLayout.LayoutParams lparams_tv_subway = new LinearLayout.LayoutParams(
                            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
                    TextView tv_subway = new TextView(view.getContext());
                    tv_subway.setLayoutParams(lparams_tv_subway);
                    tv_subway.setText("지하철");
                    inner_linear_layout_1.addView(tv_subway);

                    LinearLayout.LayoutParams lparams_tv_walk_time = new LinearLayout.LayoutParams(
                            0, LinearLayout.LayoutParams.WRAP_CONTENT, 5.0f);
                    TextView tv_walk_time = new TextView(view.getContext());
                    tv_walk_time.setLayoutParams(lparams_tv_walk_time);
                    System.out.println("여기에요!!!!!!");
                    System.out.println((String)one_transport.get("place"));
                    tv_walk_time.setText((String)one_transport.get("place"));
                    inner_linear_layout_1.addView(tv_walk_time);

                    LinearLayout.LayoutParams lparams_inner_linear_layout_2 = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    LinearLayout inner_linear_layout_2 = new LinearLayout(view.getContext());
                    inner_linear_layout_2.setLayoutParams(lparams_inner_linear_layout_2);
                    inner_linear_layout_2.setOrientation(LinearLayout.HORIZONTAL);
                    outside_linear_layout.addView(inner_linear_layout_2);

                    LinearLayout.LayoutParams lparams_tv_nothing = new LinearLayout.LayoutParams(
                            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
                    TextView tv_subway_nothing = new TextView(view.getContext());
                    tv_subway_nothing.setLayoutParams(lparams_tv_nothing);
                    tv_subway_nothing.setText("");
                    inner_linear_layout_2.addView(tv_subway_nothing);

                    LinearLayout.LayoutParams lparams_tv_time = new LinearLayout.LayoutParams(
                            0, LinearLayout.LayoutParams.WRAP_CONTENT, 5.0f);
                    TextView tv_time = new TextView(view.getContext());
                    tv_time.setLayoutParams(lparams_tv_time);
                    tv_time.setText("소요시간: "+ Integer.toString((int) Double.parseDouble((String)one_transport.get("time"))) +"분");
                    inner_linear_layout_2.addView(tv_time);

                    LinearLayout.LayoutParams lparams_inner_linear_layout_3 = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    LinearLayout inner_linear_layout_3 = new LinearLayout(view.getContext());
                    inner_linear_layout_3.setLayoutParams(lparams_inner_linear_layout_3);
                    inner_linear_layout_3.setOrientation(LinearLayout.HORIZONTAL);
                    outside_linear_layout.addView(inner_linear_layout_3);

                    LinearLayout.LayoutParams lparams_tv_nothing_1 = new LinearLayout.LayoutParams(
                            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
                    TextView tv_nothing_1 = new TextView(view.getContext());
                    tv_nothing_1.setLayoutParams(lparams_tv_nothing_1);
                    tv_nothing_1.setText("");
                    inner_linear_layout_3.addView(tv_nothing_1);

                    LinearLayout.LayoutParams lparams_tv_direction = new LinearLayout.LayoutParams(
                            0, LinearLayout.LayoutParams.WRAP_CONTENT, 5.0f);
                    TextView tv_direction = new TextView(view.getContext());
                    tv_direction.setLayoutParams(lparams_tv_direction);
                    tv_direction.setText((String)one_transport.get("direction"));
                    inner_linear_layout_3.addView(tv_direction);

                } else if (transport_type.equals("2")) {
                    tv.setText(time_str+"분"+" 버스");
                    tv.setBackgroundResource(R.drawable.green_bus_background_square);

                    LinearLayout.LayoutParams lparams_outside_linear_layout = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lparams_outside_linear_layout.setMargins(0,10,0,0);
                    LinearLayout outside_linear_layout = new LinearLayout(view.getContext());
                    outside_linear_layout.setLayoutParams(lparams_outside_linear_layout);
                    outside_linear_layout.setOrientation(LinearLayout.VERTICAL);
                    detail_information.addView(outside_linear_layout);

                    LinearLayout.LayoutParams lparams_inner_linear_layout_1 = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    LinearLayout inner_linear_layout_1 = new LinearLayout(view.getContext());
                    inner_linear_layout_1.setLayoutParams(lparams_inner_linear_layout_1);
                    inner_linear_layout_1.setOrientation(LinearLayout.HORIZONTAL);
                    outside_linear_layout.addView(inner_linear_layout_1);

                    LinearLayout.LayoutParams lparams_tv_subway = new LinearLayout.LayoutParams(
                            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
                    TextView tv_subway = new TextView(view.getContext());
                    tv_subway.setLayoutParams(lparams_tv_subway);
                    tv_subway.setText("버스");
                    inner_linear_layout_1.addView(tv_subway);

                    LinearLayout.LayoutParams lparams_tv_walk_time = new LinearLayout.LayoutParams(
                            0, LinearLayout.LayoutParams.WRAP_CONTENT, 5.0f);
                    TextView tv_walk_time = new TextView(view.getContext());
                    tv_walk_time.setLayoutParams(lparams_tv_walk_time);
                    tv_walk_time.setText((String)one_transport.get("bus_name") + " " +(String)one_transport.get("place"));
                    inner_linear_layout_1.addView(tv_walk_time);


                    LinearLayout inner_linear_layout_2 = new LinearLayout(view.getContext());
                    inner_linear_layout_2.setLayoutParams(lparams_inner_linear_layout_1);
                    inner_linear_layout_2.setOrientation(LinearLayout.HORIZONTAL);
                    outside_linear_layout.addView(inner_linear_layout_2);

                    LinearLayout.LayoutParams lparams_tv_nothing = new LinearLayout.LayoutParams(
                            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
                    TextView tv_subway_nothing = new TextView(view.getContext());
                    tv_subway_nothing.setLayoutParams(lparams_tv_nothing);
                    tv_subway_nothing.setText("");
                    inner_linear_layout_2.addView(tv_subway_nothing);

                    try{
                        TextView tv_next_time = new TextView(view.getContext());
                        tv_next_time.setLayoutParams(lparams_tv_walk_time);
                        tv_next_time.setText((String)one_transport.get("next_time"));
                        inner_linear_layout_2.addView(tv_next_time);

                        LinearLayout inner_linear_layout_3 = new LinearLayout(view.getContext());
                        inner_linear_layout_3.setLayoutParams(lparams_inner_linear_layout_1);
                        inner_linear_layout_3.setOrientation(LinearLayout.HORIZONTAL);
                        outside_linear_layout.addView(inner_linear_layout_3);

                        LinearLayout.LayoutParams lparams_tv_nothing_1 = new LinearLayout.LayoutParams(
                                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
                        TextView tv_nothing_1 = new TextView(view.getContext());
                        tv_nothing_1.setLayoutParams(lparams_tv_nothing_1);
                        tv_nothing_1.setText("");
                        inner_linear_layout_3.addView(tv_nothing_1);

                        TextView tv_next_next_time = new TextView(view.getContext());
                        tv_next_next_time.setLayoutParams(lparams_tv_walk_time);
                        tv_next_next_time.setText((String)one_transport.get("next_next_time"));
                        inner_linear_layout_3.addView(tv_next_next_time);
                    }catch (Exception e){
                        // do nothing
                    }

                    try{
                        LinearLayout inner_linear_layout_4 = new LinearLayout(view.getContext());
                        inner_linear_layout_4.setLayoutParams(lparams_inner_linear_layout_1);
                        inner_linear_layout_4.setOrientation(LinearLayout.HORIZONTAL);
                        outside_linear_layout.addView(inner_linear_layout_4);


                        LinearLayout.LayoutParams lparams_tv_nothing_2 = new LinearLayout.LayoutParams(
                                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
                        TextView tv_nothing_2 = new TextView(view.getContext());
                        tv_nothing_2.setLayoutParams(lparams_tv_nothing_2);
                        tv_nothing_2.setText("");
                        inner_linear_layout_4.addView(tv_nothing_2);

                        TextView tv_interval = new TextView(view.getContext());
                        tv_interval.setLayoutParams(lparams_tv_walk_time);
                        tv_interval.setText("배차간격: " + (String)one_transport.get("interval")+"분");
                        inner_linear_layout_4.addView(tv_interval);

                    }catch (Exception e){
                        // do nothing
                    }
                    
                } else if (transport_type.equals("3")) {
                    LinearLayout.LayoutParams lparams_outside_linear_layout = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lparams_outside_linear_layout.setMargins(0,10,0,0);
                    LinearLayout outside_linear_layout = new LinearLayout(view.getContext());
                    outside_linear_layout.setLayoutParams(lparams_outside_linear_layout);
                    outside_linear_layout.setOrientation(LinearLayout.HORIZONTAL);
                    detail_information.addView(outside_linear_layout);

                    LinearLayout.LayoutParams lparams_tv_walk = new LinearLayout.LayoutParams(
                            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
                    TextView tv_walk = new TextView(view.getContext());
                    tv_walk.setLayoutParams(lparams_tv_walk);
                    tv_walk.setText("하차");
                    outside_linear_layout.addView(tv_walk);

                    LinearLayout.LayoutParams lparams_tv_walk_time = new LinearLayout.LayoutParams(
                            0, LinearLayout.LayoutParams.WRAP_CONTENT, 5.0f);
                    TextView tv_walk_time = new TextView(view.getContext());
                    tv_walk_time.setLayoutParams(lparams_tv_walk_time);
                    tv_walk_time.setText((String)one_transport.get("place"));
                    outside_linear_layout.addView(tv_walk_time);
                    continue;
                } else if (transport_type.equals("4")) {
                    tv.setText(time_str+"분"+" 대기");
                    tv.setBackgroundResource(R.drawable.yellow_wait_background_square);

                    LinearLayout.LayoutParams lparams_outside_linear_layout = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lparams_outside_linear_layout.setMargins(0,10,0,0);
                    LinearLayout outside_linear_layout = new LinearLayout(view.getContext());
                    outside_linear_layout.setLayoutParams(lparams_outside_linear_layout);
                    outside_linear_layout.setOrientation(LinearLayout.HORIZONTAL);
                    detail_information.addView(outside_linear_layout);

                    LinearLayout.LayoutParams lparams_tv_walk = new LinearLayout.LayoutParams(
                            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
                    TextView tv_walk = new TextView(view.getContext());
                    tv_walk.setLayoutParams(lparams_tv_walk);
                    tv_walk.setText("대기");
                    outside_linear_layout.addView(tv_walk);

                    LinearLayout.LayoutParams lparams_tv_walk_time = new LinearLayout.LayoutParams(
                            0, LinearLayout.LayoutParams.WRAP_CONTENT, 5.0f);
                    TextView tv_walk_time = new TextView(view.getContext());
                    tv_walk_time.setLayoutParams(lparams_tv_walk_time);
                    tv_walk_time.setText("다음 대중교통까지 대기시간: "+ Integer.toString((int) Double.parseDouble((String)one_transport.get("time"))) +"분");
                    outside_linear_layout.addView(tv_walk_time);
                }
                tv.setTextSize(7);
                tv.setTextAlignment(convertView.TEXT_ALIGNMENT_CENTER);
                tv.setGravity(Gravity.CENTER);
                //tv.setTextSize();
                transports.addView(tv);
            } catch (JSONException e) {
                //do nothing
                //e.printStackTrace();
            }

        }
        total_time.setText(Integer.toString(total_time_int)+"분");

        return view;
    }
}

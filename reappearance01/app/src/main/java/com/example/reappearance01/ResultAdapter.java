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
        System.out.println("list view 띄우기!!");
        TextView total_time = view.findViewById(R.id.total_time);
        LinearLayout transports = (LinearLayout) view.findViewById(R.id.kind_of_transport);
        System.out.println("어뎁터에서 띄우기!1");
        System.out.println(final_json_array);

        JSONArray position_json_array = new JSONArray();
        try {
            position_json_array = new JSONArray(this.getItem(position).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println("하나의 json array");
        System.out.println(position_json_array);

        int len_array = position_json_array.length();

        int total_time_int = 0;
        for (int i=0; i < len_array; i++){
            JSONObject one_transport = new JSONObject();
            try {
                System.out.println("하나의 json object");
                System.out.println(one_transport);
                one_transport = new JSONObject(position_json_array.get(i).toString());
                total_time_int += (int) Double.parseDouble((String)one_transport.get("time"));

                String transport_type = (String) one_transport.get("type");
                double time = Double.parseDouble((String) one_transport.get("time"));
                System.out.println("시간입니다!!!!");
                System.out.println(time);
                String time_str = Integer.toString((int) time);
                LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                        0, 40, (float) time);
                TextView tv = new TextView(view.getContext());
                tv.setLayoutParams(lparams);
                if (transport_type.equals("0")){    //
                    System.out.println("도보입니다!!!!");
                    tv.setText(time_str+"분"+" 도보");
                    tv.setBackgroundResource(R.drawable.gray_walk_background_square);
                    System.out.println("도보입니다123123!!!!");
                } else if (transport_type.equals("1")) {
                    tv.setText(time_str+"분"+" 지하철");
                    tv.setBackgroundResource(R.drawable.blue_subway_background_square);
                } else if (transport_type.equals("2")) {
                    tv.setText(time_str+"분"+" 버스");
                    tv.setBackgroundResource(R.drawable.green_bus_background_square);
                } else if (transport_type.equals("3")) {
                    continue;
                } else if (transport_type.equals("4")) {
                    tv.setText(time_str+"분"+" 대기");
                    tv.setBackgroundResource(R.drawable.yellow_wait_background_square);
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

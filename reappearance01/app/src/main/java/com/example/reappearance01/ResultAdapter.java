package com.example.reappearance01;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        System.out.println("여기 길이에요");
        System.out.println(final_json_array.length());
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
        

        JSONArray position_json_array = new JSONArray();
        try {
            position_json_array = new JSONArray(this.getItem(position).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        int len_array = position_json_array.length();

        int total_time_int = 0;
        for (int i=0; i < len_array; i++){
            JSONObject one_transport = new JSONObject();
            try {
                one_transport = new JSONObject(position_json_array.get(i).toString());
                total_time_int += (int) Double.parseDouble((String)one_transport.get("time"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        total_time.setText(Integer.toString(total_time_int)+"분");

        return view;
    }
}

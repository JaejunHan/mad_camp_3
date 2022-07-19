package com.example.reappearance01;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.nio.file.Path;
import java.util.ArrayList;

public class HistoryPathAdapter extends BaseAdapter
{
    LayoutInflater inflater = null;
    private ArrayList<PathSavedData> m_oData = null;
    private int nListCnt = 0;

    public HistoryPathAdapter(ArrayList<PathSavedData> _oData)
    {
        m_oData = _oData;
        nListCnt = m_oData.size();
    }

    @Override
    public int getCount()
    {
        Log.i("TAG", "getCount");
        return nListCnt;
    }

    @Override
    public Object getItem(int position)
    {
        return null;
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            final Context context = parent.getContext();
            if (inflater == null)
            {
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            convertView = inflater.inflate(R.layout.item_history_path, parent, false);
        }

        View clickableItem = (View) convertView.findViewById(R.id.cmdArea);

        clickableItem.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                /*
                Intent intent = new Intent(view.getContext(), ProfileActivity.class);

                intent.putExtra("name",m_oData.get(position).Name);
                intent.putExtra("number",m_oData.get(position).PhoneNumber);
                intent.putExtra("image",m_oData.get(position).ProfileImage);

                view.getContext().startActivity(intent);

                 */
                Intent intent = new Intent(view.getContext(), SplashBeforeResult.class);
                intent.putExtra("FromLatLng", m_oData.get(position).fromLocation);
                intent.putExtra("FromName", m_oData.get(position).fromName);
                intent.putExtra("ToLatLng", m_oData.get(position).toLocation);
                intent.putExtra("ToName", m_oData.get(position).toName);

                view.getContext().startActivity(intent);


            }
        });

        TextView oTextFrom = (TextView) convertView.findViewById(R.id.fromText);
        TextView oTextTo = (TextView) convertView.findViewById(R.id.toText);

        oTextFrom.setText(m_oData.get(position).fromName);
        oTextTo.setText(m_oData.get(position).toName);
        return convertView;
    }
}


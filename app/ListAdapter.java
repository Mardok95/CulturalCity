package com.icloud.andreadimartino.bellini;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Luogo> list;
    private Luogo luog;
    private static final String TAG = "ListAdapter";

    // Store the list of image IDs
    public ListAdapter(Context c, ArrayList<Luogo> ids) {
        mContext = c;
        this.list = ids;
    }

    // Return the number of items in the Adapter
    @Override
    public int getCount() {
        return list.size();
    }

    // Return the data item at position
    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    // Will get called to provide the ID that
    // is passed to OnItemClickListener.onItemClick()
    @Override
    public long getItemId(int position) {
        return position;
    }

    // Return an ImageView for each item referenced by the Adapter
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.adapter_list,  parent, false);
        luog = (Luogo) getItem(position);
        String nomeLuogo = luog.getNomeLuogo();




        TextView nomeL = (TextView) convertView.findViewById(R.id.info);
        nomeL.setText(nomeLuogo);


        Button btn1 = (Button) convertView.findViewById(R.id.btn1);
        btn1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(mContext, MainActivity.class);
                mContext.startActivity(intent);
                Log.d(TAG, "onClick: Button1 clicked");
                Toast.makeText(mContext,"btn1 clicked", Toast.LENGTH_SHORT).show();
            }
        });

        nomeL.setTextColor(Color.BLACK);


        return convertView;
    }

}


package com.icloud.andreadimartino.bellini;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class LuogoListAdapter extends ArrayAdapter<Luogo> {
    private static final String TAG = "LuogoListAdapter";
    private Context mcontext;
    int mresouce;
    ImageView icona;
    TextView tvNome;

    public LuogoListAdapter( Context context, int resource, ArrayList<Luogo> objects) {
        super(context, resource, objects);
        mcontext = context;
        mresouce = resource;

    }
    
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String name = getItem(position).getNomeLuogo();
        String tipo = getItem(position).getTipoLuogo();
        LayoutInflater inflater = LayoutInflater.from(mcontext);
        convertView = inflater.inflate(mresouce, parent, false);
        tvNome = (TextView) convertView.findViewById(R.id.textView2);
        tvNome.setText(name);
        icona = (ImageView) convertView.findViewById(R.id.iconimg);
        if(tipo.equals("Museo")){
            icona.setImageResource(R.drawable.museumicon);
        }
        else if(tipo.equals("Giardini_botanicci_e_zoologici")){
            icona.setImageResource(R.drawable.gardenicon);
        }
        else if(tipo.equals("Chiese")){
            icona.setImageResource(R.drawable.churchicon);
        }
        else if(tipo.equals("Palazzi")){
            icona.setImageResource(R.drawable.historicalicon);
        }
        else if(tipo.equals("Biblioteca")){
            icona.setImageResource(R.drawable.library);
        }
        else if(tipo.equals("Luogo_monumento")){
            icona.setImageResource(R.drawable.monumenticon);
        }
        else if(tipo.equals("Fotografia_e_studi_fotografici")){
            icona.setImageResource(R.drawable.photo);
        }
        else if(tipo.equals("Piazze")){
            icona.setImageResource(R.drawable.squareicon);
        }
        else if(tipo.equals("Teatro")){
            icona.setImageResource(R.drawable.theatreicon);
        }
        else icona.setImageResource(R.drawable.culturalcentreicon);



        return convertView;
    }


    }

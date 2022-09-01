package com.icloud.andreadimartino.bellini;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class aboutUs extends AppCompatActivity {
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        setTitle("About us");

        textView = (TextView)findViewById(R.id.textView3);
        ImageView image = (ImageView)findViewById(R.id.imageView2);
        image.setImageResource(R.drawable.about);
        ImageButton btnEnrico = (ImageButton)findViewById(R.id.imageButton2);
        ImageButton btnAndre = (ImageButton)findViewById(R.id.imageButton3);
        btnAndre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openRicerca = new Intent(aboutUs.this, AndreActivity.class);
                startActivity(openRicerca);
            }
        });

        btnEnrico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openRicerca = new Intent(aboutUs.this, EnricoActivity.class);
                startActivity(openRicerca);
            }
        });
    }
}

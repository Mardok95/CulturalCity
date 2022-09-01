package com.icloud.andreadimartino.bellini;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class km4city extends AppCompatActivity {
    TextView textW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_km4city);
        setTitle("km4city");

        textW = (TextView) findViewById(R.id.textView4);
        ImageView image = (ImageView)findViewById(R.id.imageView4);
        image.setImageResource(R.drawable.km4city_logo);
        Button videoButton = (Button)findViewById(R.id.button6);
        videoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(km4city.this, VideoActivity.class);
            startActivity(intent);
            }
        });
        Button historyButton = (Button)findViewById(R.id.button7);
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(km4city.this, HistoryActivity.class);
                startActivity(intent1);
            }
        });
    }
}

package com.icloud.andreadimartino.bellini;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import java.io.File;

public class VideoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        VideoView vidv = (VideoView) findViewById(R.id.videoView);
        String videoPath =  "android.resource://" + getPackageName() + "/" + R.raw.km4cityvideo;
        Uri uri = Uri.parse(videoPath);
        vidv.setVideoURI(uri);

        MediaController mediaController = new MediaController(this);
        vidv.setMediaController(mediaController);
        mediaController.setAnchorView(vidv);


    }
}

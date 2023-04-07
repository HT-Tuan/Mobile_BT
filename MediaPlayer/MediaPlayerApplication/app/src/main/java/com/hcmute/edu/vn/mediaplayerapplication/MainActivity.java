package com.hcmute.edu.vn.mediaplayerapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.music);

        Button bPlay = findViewById(R.id.playButton);
        Button bPause = findViewById(R.id.pauseButton);
        Button bStop= findViewById(R.id.stopButton);

        bPlay.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 mediaPlayer.start();
             }
        });
         bPause.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 mediaPlayer.pause();
             }
         });

         bStop.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 mediaPlayer.stop();
                 try {
                     mediaPlayer.prepare();
                 } catch (IOException e) {
                     throw new RuntimeException(e);
                 }
             }
         });
    }
}
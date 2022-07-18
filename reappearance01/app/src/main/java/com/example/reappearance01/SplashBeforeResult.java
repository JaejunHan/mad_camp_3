package com.example.reappearance01;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashBeforeResult extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_before_result);

        // 비디오뷰 가져오기
        VideoView mVideoView = (VideoView) findViewById(R.id.video_splash);

        // sample.mp4 설정
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/raw/loading_page");
        mVideoView.setVideoURI(uri);
        // 리스너 등록
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                // 준비 완료되면 비디오 재생
                mp.start();
            }
        });

        // 비디오 종료시 실행되는 리스너
        MediaPlayer.OnCompletionListener mComplete = new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mVideoView.start();
            }
        };
    }
}

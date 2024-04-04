package com.lazycoder.cakevpn.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.lazycoder.cakevpn.R;

public class LaunchActivity extends AppCompatActivity {

    /**
     *
     * @param savedInstanceState
     * Запускает загрузочный экран, спустя 5 секунд открывает главное окно
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(LaunchActivity.this, browser.class);
                startActivity(i);
            }
        }, 5000);

    }
}
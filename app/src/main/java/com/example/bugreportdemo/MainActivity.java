package com.example.bugreportdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mCrashBtn, mAnrBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCrashBtn = findViewById(R.id.btn_crash);
        mCrashBtn.setOnClickListener(this);
        mAnrBtn = findViewById(R.id.btn_anr);
        mAnrBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_crash:
                int a = 0;
                a = Integer.valueOf(a / 0);
                break;
            case R.id.btn_anr:
                Intent intent = new Intent();
                intent.setAction("com.clock.performance.tools.block");
                intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
                sendBroadcast(intent);
                break;
            default:
                break;
        }
    }
}

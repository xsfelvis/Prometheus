package com.xsf.dev.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends Activity implements View.OnClickListener {
    private TextView tvMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        tvMsg = findViewById(R.id.tvShowMsg);
        tvMsg.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvShowMsg:
                showMsg();
                break;
        }

    }

    private void showMsg() {
        String spiMsg = getSpIMsg();
        tvMsg.setText(spiMsg);
    }

    private String getSpIMsg() {
        //String spiStr = PrivateServiceInterfaceService.getInstance().getName();
        return "";
    }
}

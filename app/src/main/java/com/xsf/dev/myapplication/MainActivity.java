package com.xsf.dev.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.xsf.dev.foundation.spi.ServiceLoader;
import com.xsf.dev.myapplication.serviceinterface.PernalInfoServiceImppl;
import com.xsf.dev.myapplication.serviceinterface.PernalInfoInterface;
import com.xsf.dev.spiannotation.ServiceProvider;

@ServiceProvider(value = View.OnClickListener.class, priority = 3)
public class MainActivity extends Activity implements View.OnClickListener {
    private TextView tvMsg;
    private PernalInfoServiceImppl mPrivateServiceInterface;

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
            default:
                break;
        }

    }

    private void showMsg() {
        String spiMsg = PernalInfoService.getInstance().getName();
        tvMsg.setText(spiMsg);


    }

}

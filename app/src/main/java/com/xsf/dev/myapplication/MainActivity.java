package com.xsf.dev.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.xsf.dev.spiannotation.ServiceProvider;
import com.xsf.dev.spiloader.ServiceLoader;


@ServiceProvider(value = View.OnClickListener.class, priority = 3)

public class MainActivity extends Activity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        for (final View.OnClickListener listener : ServiceLoader.load(View.OnClickListener.class)) {
            Log.i("MainActivity", listener.toString());
        }
    }

    @Override
    public void onClick(View v) {

    }
}

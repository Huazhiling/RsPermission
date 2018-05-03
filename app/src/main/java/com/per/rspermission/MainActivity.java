package com.per.rspermission;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.per.rslibrary.RsPermission;


public class MainActivity extends AppCompatActivity {

    private android.widget.Button request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.request = findViewById(R.id.request);
        request.setOnClickListener(v->{
        });
    }
}

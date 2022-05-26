package com.dq.livemessagedemo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class RootActivity extends AppCompatActivity {

    private EditText minRefreshTimeEditText, maxCacheEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root);

        maxCacheEditText = findViewById(R.id.maxCacheEditText);
        minRefreshTimeEditText = findViewById(R.id.minRefreshTimeEditText);

        findViewById(R.id.to_live_btn).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent i = new Intent(RootActivity.this, LiveActivity.class);
                i.putExtra("maxCacheCount",Integer.valueOf(maxCacheEditText.getText().toString()));
                i.putExtra("minRefreshTime",Float.valueOf(minRefreshTimeEditText.getText().toString()));
                startActivity(i);
            }
        });
    }
}
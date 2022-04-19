package com.ropherfood.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class AboutUsActivity extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        initialize();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                startActivity(new Intent(AboutUsActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    private void initialize() {

        toolbar = findViewById(R.id.about_us_toolbar);
        setSupportActionBar(toolbar);

    }
}
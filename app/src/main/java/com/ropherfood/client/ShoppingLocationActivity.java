package com.ropherfood.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ShoppingLocationActivity extends AppCompatActivity implements View.OnClickListener {

    Button nakaseroBtn;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_location);

        initialize();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        nakaseroBtn.setOnClickListener(this);

    }

    private void initialize() {

        nakaseroBtn = findViewById(R.id.nakaseroBtn);
        toolbar = findViewById(R.id.shopping_location_toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.nakaseroBtn)
            startActivity(new Intent(this, MakeOrdersCategoryActivity.class));

    }

}
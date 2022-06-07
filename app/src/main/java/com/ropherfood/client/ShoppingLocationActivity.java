package com.ropherfood.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ShoppingLocationActivity extends AppCompatActivity implements View.OnClickListener {

    Button nakaseroBtn, nakawaBtn, seetaBtn, kansangaBtn;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_location);

        initialize();

        toolbar.setNavigationOnClickListener(v -> finish());

        nakaseroBtn.setOnClickListener(this);
        nakawaBtn.setOnClickListener(this);
        seetaBtn.setOnClickListener(this);
        kansangaBtn.setOnClickListener(this);

    }

    private void initialize() {

        nakaseroBtn = findViewById(R.id.nakaseroBtn);
        nakawaBtn = findViewById(R.id.nakawaBtn);
        seetaBtn = findViewById(R.id.seetaBtn);
        kansangaBtn = findViewById(R.id.kansangaBtn);
        toolbar = findViewById(R.id.shopping_location_toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.nakaseroBtn)
            startActivity(new Intent(this, MakeOrdersCategoryActivity.class));

        if(v.getId() == R.id.nakawaBtn)
            startActivity(new Intent(this, MakeOrdersCategoryActivity.class));

        if(v.getId() == R.id.seetaBtn)
            startActivity(new Intent(this, MakeOrdersCategoryActivity.class));

        if(v.getId() == R.id.kansangaBtn)
            startActivity(new Intent(this, MakeOrdersCategoryActivity.class));

    }

}
package com.ropherfood.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MakeOrderCategoryActivity extends AppCompatActivity implements View.OnClickListener{

    Button marketButton, superMarketButton;
    Toolbar toolbar;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_order_category);

        initialize();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        marketButton.setOnClickListener(this);
        superMarketButton.setOnClickListener(this);

    }

    private void initialize() {

        marketButton = findViewById(R.id.make_order_category_market_button);
        superMarketButton = findViewById(R.id.make_order_category_supermarket_button);

        toolbar = findViewById(R.id.make_order_category_toolbar);
        sharedPreferences = getSharedPreferences("Account_Information", MODE_PRIVATE);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.make_order_category_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.cart_menu_item){
            startActivity(new Intent(MakeOrderCategoryActivity.this, CartActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.make_order_category_market_button);
        startActivity(new Intent(MakeOrderCategoryActivity.this, MakeOrdersCategoryMarketActivity.class));

        if(v.getId() == R.id.make_order_category_supermarket_button)
            startActivity(new Intent(MakeOrderCategoryActivity.this, MakeOrdersCategorySuperMarketActivity.class));

    }

}
package com.ropherfood.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AdminHomeActivity extends AppCompatActivity implements View.OnClickListener {

    Button foodItemsButton, viewOrdersButton, employeesButton, marketsButton, categoriesButton;
    Toolbar toolbar;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        initialize();

        if(!sharedPreferences.getString("account_Type", "").equals("Administrator")){

            foodItemsButton.setVisibility(View.GONE);
            employeesButton.setVisibility(View.GONE);

        }

        toolbar.setNavigationOnClickListener(v -> finish());

        foodItemsButton.setOnClickListener(this);
        viewOrdersButton.setOnClickListener(this);
        employeesButton.setOnClickListener(this);
        marketsButton.setOnClickListener(this);
        categoriesButton.setOnClickListener(this);
    }

    private void initialize() {

        foodItemsButton = findViewById(R.id.food_items_admin_button);
        viewOrdersButton = findViewById(R.id.view_orders_admin_button);
        employeesButton = findViewById(R.id.employees_admin_button);
        marketsButton = findViewById(R.id.markets_admin_button);
        categoriesButton = findViewById(R.id.categories_admin_button);
        toolbar = findViewById(R.id.admin_home_toolbar);
        sharedPreferences = getSharedPreferences("Account_Information", MODE_PRIVATE);
        setSupportActionBar(toolbar);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.food_items_admin_button)
            startActivity(new Intent(AdminHomeActivity.this, FoodItemsActivity.class));

        if(v.getId() == R.id.view_orders_admin_button)
            startActivity(new Intent(AdminHomeActivity.this, ViewAllCustomersOrdersActivity.class));

        if(v.getId() == R.id.employees_admin_button)
            startActivity(new Intent(AdminHomeActivity.this, EmployeesActivity.class));

        if(v.getId() == R.id.markets_admin_button)
            startActivity(new Intent(AdminHomeActivity.this, ViewMarketsActivity.class));

        if(v.getId() == R.id.categories_admin_button)
            startActivity(new Intent(AdminHomeActivity.this, ViewCategoriesActivity.class));

    }
}
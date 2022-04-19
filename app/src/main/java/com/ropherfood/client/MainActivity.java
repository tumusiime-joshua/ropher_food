package com.ropherfood.client;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Toolbar toolbar;
    Button makeOrderButton, ordersButton, accountButton, aboutUsButton;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();

        makeOrderButton.setOnClickListener(this);
        ordersButton.setOnClickListener(this);
        accountButton.setOnClickListener(this);
        aboutUsButton.setOnClickListener(this);
    }

    private void initialize() {

        makeOrderButton = findViewById(R.id.make_order_home_button);
        ordersButton = findViewById(R.id.orders_home_button);
        accountButton = findViewById(R.id.account_home_button);
        aboutUsButton = findViewById(R.id.about_us_home_button);
        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        sharedPreferences = getSharedPreferences("Account_Information", MODE_PRIVATE);
        toolbar.setSubtitle(sharedPreferences.getString("last_name", "").toUpperCase());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu_items, menu);

        if(sharedPreferences.getString("account_Type", "").equals("Customer")){

            menu.findItem(R.id.main_menu_admin).setVisible(false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.main_menu_admin){
            startActivity(new Intent(MainActivity.this, AdminHomeActivity.class));
        }

        if(item.getItemId() == R.id.main_menu_about_app){
            aboutAppMenuItem();
        }

        if(item.getItemId() == R.id.cart_menu_item){
            startActivity(new Intent(MainActivity.this, CartActivity.class));
        }

        if(item.getItemId() == R.id.main_menu_log_out_app){
            logOut();
        }

        if(item.getItemId() == R.id.main_menu_exit){
            exitAppMenuItem();
        }
        return super.onOptionsItemSelected(item);
    }

    private void logOut() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit");
        builder.setMessage("Are you sure you want to log out ?");
        builder.setCancelable(false);

        builder.setNegativeButton("Cancel", (dialog, which) -> builder.setCancelable(true));

        builder.setPositiveButton("Ok", (dialog, which) -> {

            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString("status", "LogOut");
            editor.apply();
            editor.clear();
            editor.apply();

            startActivity(new Intent(MainActivity.this, LogInActivity.class));
            finish();



        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //Show dialog for the exit app menu item
    private void exitAppMenuItem() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Log out");
        builder.setMessage("Are you sure you want to exit ?");
        builder.setCancelable(false);

        builder.setNegativeButton("Cancel", (dialog, which) -> builder.setCancelable(true));

        builder.setPositiveButton("Ok", (dialog, which) -> finish());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //Show dialog for the about app menu item
    private void aboutAppMenuItem() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("About this app");
        builder.setMessage("Version 1.0  \n Developed by Tumusiime Joshua.");
        builder.setCancelable(false);

        builder.setNegativeButton("Close", (dialog, which) -> builder.setCancelable(true));

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.make_order_home_button)
            startActivity(new Intent(MainActivity.this, ShoppingLocationActivity.class));

        if(v.getId() == R.id.orders_home_button)
            startActivity(new Intent(MainActivity.this, MyOrdersActivity.class));

        if(v.getId() == R.id.account_home_button)
            startActivity(new Intent(MainActivity.this, MyAccountActivity.class));

        if(v.getId() == R.id.about_us_home_button)
            startActivity(new Intent(MainActivity.this, AboutUsActivity.class));

    }
}
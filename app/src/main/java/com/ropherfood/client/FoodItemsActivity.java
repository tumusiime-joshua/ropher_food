package com.ropherfood.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FoodItemsActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    Toolbar toolbar;
    RecyclerView foodItemsRecyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<FoodItemsData> foodItemsList;
    FoodItemsListAdapter adapter;
    TextView noItemsToDisplayTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_items);

        initialize();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });

        foodItemsList.clear();
        loadData();

        swipeRefreshLayout.setOnRefreshListener(this);
    }

    private void loadData() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, UrlLinks.UrlViewFoodItems, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    String message = jsonObject.getString("message");
                    JSONArray jsonArray = jsonObject.getJSONArray("view_food_items_array");

                    if(success.equals("1")){

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            String foodItemId = jsonObject1.getString("food_item_id");
                            String name = jsonObject1.getString("food_name");
                            String price = jsonObject1.getString("food_price");
                            String description = jsonObject1.getString("food_description");
                            String category = jsonObject1.getString("category");
                            String market = jsonObject1.getString("market");
                            String image_path = jsonObject1.getString("image_path");
                            String image_address_path = jsonObject1.getString("image_address_path");
                            String status = jsonObject1.getString("status");

                            FoodItemsData foodItemsData = new FoodItemsData();
                            foodItemsData.imageAddress = image_address_path;
                            foodItemsData.id = foodItemId;
                            foodItemsData.name = name;
                            foodItemsData.price = price;
                            foodItemsData.description = description;
                            foodItemsData.category = category;
                            foodItemsData.market = market;
                            foodItemsData.imagePath = image_path;
                            foodItemsData.status = status;


                            foodItemsList.add(foodItemsData);
                        }

                        foodItemsRecyclerView.setAdapter(adapter);
                        progressDialog.dismiss();
                        Log.e("TAG", response);

                    }else if(success.equals("0")){

                        progressDialog.dismiss();
                        displayAlertDialog(message);
                        noItemsToDisplayTextView.setVisibility(View.VISIBLE);
                        swipeRefreshLayout.setVisibility(View.GONE);
                        Log.e("TAG", response);
                    }

                } catch (JSONException e) {

                    e.printStackTrace();
                    displayAlertDialog("Cannot display list.");
                    noItemsToDisplayTextView.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setVisibility(View.GONE);
                    Log.e("TAG", response);
                    progressDialog.dismiss();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                displayAlertDialog("Failed to connect to sever");
                Log.e("TAG", error.getMessage());
                noItemsToDisplayTextView.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setVisibility(View.GONE);
                progressDialog.dismiss();

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void initialize() {

        foodItemsRecyclerView = findViewById(R.id.food_items_recyclerView);
        swipeRefreshLayout = findViewById(R.id.food_items_swipeRefreshLayout);
        foodItemsList = new ArrayList<>();
        adapter = new FoodItemsListAdapter(foodItemsList);
        toolbar = findViewById(R.id.food_items_toolbar);

        setSupportActionBar(toolbar);
        foodItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        noItemsToDisplayTextView = findViewById(R.id.food_items_no_items_textView);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.food_items_menu, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.food_items_search_menu_item).getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                adapter.getFilter().filter(newText);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.food_items_add_menu_item)
            startActivity(new Intent(FoodItemsActivity.this, AddFoodItemActivity.class));

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {

        foodItemsList.clear();
        loadData();
        swipeRefreshLayout.setRefreshing(false);
    }

    private void displayAlertDialog(String message){

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setCancelable(false);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                builder.setCancelable(true);
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
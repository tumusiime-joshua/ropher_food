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

import com.android.volley.AuthFailureError;
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
import java.util.HashMap;
import java.util.Map;

public class CategoryItemsActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    Toolbar toolbar;
    RecyclerView recyclerView;
    ArrayList<MakeOrdersData> list;
    SwipeRefreshLayout swipeRefreshLayout;
    MakeOrderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_items);

        initialize();

        loadData();

        swipeRefreshLayout.setOnRefreshListener(this);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void loadData() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, UrlLinks.view_category_food_items, new Response.Listener<String>() {
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
                            String foodItemIdText = jsonObject1.getString("food_item_id");
                            String nameText = jsonObject1.getString("food_name");
                            String priceText = jsonObject1.getString("food_price");
                            String descriptionText = jsonObject1.getString("food_description");
                            String image_pathText = jsonObject1.getString("image_address_path");

                            MakeOrdersData makeOrdersData = new MakeOrdersData();
                            makeOrdersData.id = foodItemIdText;
                            makeOrdersData.name = nameText;
                            makeOrdersData.price = priceText;
                            makeOrdersData.description = descriptionText;
                            makeOrdersData.imagePath = image_pathText;

                            list.add(makeOrdersData);
                        }

                        recyclerView.setAdapter(adapter);
                        progressDialog.dismiss();
//                        displayAlertDialog(message);
                        Log.e("TAG", response);

                    }else if(success.equals("0")){

                        progressDialog.dismiss();
                        displayAlertDialog(message);
                        Log.e("TAG", response);
                    }

                } catch (JSONException e) {

                    progressDialog.dismiss();
                    e.printStackTrace();
                    displayAlertDialog("Cannot display list.");
                    Log.e("TAG", response);

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                progressDialog.dismiss();
                displayAlertDialog("Failed to connect to sever");
                Log.e("TAG", error.getMessage());

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("category", getIntent().getStringExtra("category"));
                return params;

            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void initialize() {

        recyclerView = findViewById(R.id.category_items_activity_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        swipeRefreshLayout = findViewById(R.id.category_items_activity_swipeRefreshLayout);

        list = new ArrayList<>();
        adapter = new MakeOrderAdapter(list);

        toolbar = findViewById(R.id.category_items_activity_toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.make_super_market_order_menu, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.make_super_market_order_search_menu_item).getActionView();

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

        if(item.getItemId() == R.id.cart_menu_item){
            startActivity(new Intent(CategoryItemsActivity.this, CartActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {

        list.clear();
        loadData();
        swipeRefreshLayout.setColorSchemeResources(R.color.main_color);
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
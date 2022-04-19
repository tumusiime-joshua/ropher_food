package com.ropherfood.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

public class CartActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    Toolbar toolbar;
    RecyclerView recyclerView;
    ArrayList<MyOrdersData> list;
    SwipeRefreshLayout swipeRefreshLayout;
    MyOrdersAdapter adapter;
    SharedPreferences sharedPreferences;
    Button cartConfirmOrders;

    int total_orders, total_items, total_price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        initialize();

        list.clear();
        loadData();

        swipeRefreshLayout.setOnRefreshListener(this);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(CartActivity.this, MainActivity.class));
                finish();
            }
        });

        cartConfirmOrders.setOnClickListener(this);
    }

    private void loadData() {

        total_orders = 0;
        total_items = 0;
        total_price = 0;

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, UrlLinks.view_cart, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    String message = jsonObject.getString("message");
                    JSONArray jsonArray = jsonObject.getJSONArray("view_my_orders_array");

                    if (success.equals("1")) {

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            String orderId = jsonObject1.getString("order_Id");
                            String foodItemName = jsonObject1.getString("food_name");
                            String foodItemPrice = jsonObject1.getString("food_price");
                            String foodItemQuantity = jsonObject1.getString("food_item_quantity");
                            String date = jsonObject1.getString("date");

                            MyOrdersData myOrdersData = new MyOrdersData();
                            myOrdersData.orderId = orderId;
                            myOrdersData.foodItemName = foodItemName;
                            myOrdersData.foodItemQuantity = foodItemQuantity;
                            myOrdersData.foodItemPrice = foodItemPrice;
                            myOrdersData.dateOrdered = date;

                            total_orders++;
                            total_items = total_items + Integer.parseInt(foodItemQuantity);
                            total_price = total_price + Integer.parseInt(foodItemPrice);

                            list.add(myOrdersData);

                        }

                        recyclerView.setAdapter(adapter);
                        progressDialog.dismiss();

                    }else if(success.equals("0")){

                        progressDialog.dismiss();
                        displayAlertDialog(message);
                    }

                } catch (JSONException e) {

                    e.printStackTrace();
                    displayAlertDialog("Cannot display list.");
                    Toast.makeText(CartActivity.this, response, Toast.LENGTH_LONG).show();
                    Log.e("TAG", response);
                    progressDialog.dismiss();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                displayAlertDialog("Failed to connect to sever");
                Log.e("TAG", error.getMessage());
                progressDialog.dismiss();

            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("phone_number", sharedPreferences.getString("phone_number", ""));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void initialize() {

        recyclerView = findViewById(R.id.cart_recyclerView);
        list = new ArrayList<>();

        adapter = new MyOrdersAdapter(list);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        swipeRefreshLayout = findViewById(R.id.cart_SwipeRefreshLayout);
        toolbar = findViewById(R.id.cart_toolbar);
        cartConfirmOrders = findViewById(R.id.cart_confirm_orders_button);

        sharedPreferences = getSharedPreferences("Account_Information", MODE_PRIVATE);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.my_orders_menu, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.my_orders_search_menu_item).getActionView();

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
    public void onRefresh() {

        list.clear();
        loadData();
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setColorSchemeResources(R.color.main_color);

    }

    private void displayAlertDialog(String message){

        final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setCancelable(false);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                builder.setCancelable(true);
            }
        });

        androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void displayAlertDialogConfirmOrders(int total_orders_value, int total_items_value, int total_price_value){

        String message = "Total Number of Orders: " +total_orders_value+"\nTotal Number of Items: "+total_items_value+"\nTotal Amount: UGX "+total_price_value;

        final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Do you want to confirm this Order?");
        builder.setMessage(message);
        builder.setCancelable(false);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent1 = new Intent(CartActivity.this, ConfirmOrderActivity.class);
                intent1.putExtra("total_orders", ""+total_orders_value);
                intent1.putExtra("total_items", ""+total_items_value);
                intent1.putExtra("total_amount", ""+total_price_value);
                startActivity(intent1);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                builder.setCancelable(true);
            }
        });

        androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.cart_confirm_orders_button){
            displayAlertDialogConfirmOrders(total_orders, total_items, total_price);
        }
    }
}
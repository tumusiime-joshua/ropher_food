package com.ropherfood.client;

import androidx.appcompat.app.AppCompatActivity;
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
import android.view.View;
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

public class OrderedItemsListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    Toolbar toolbar;
    RecyclerView recyclerView;
    ArrayList<MyOrdersData> list;
    SwipeRefreshLayout swipeRefreshLayout;
    MyOrdersAdapter adapter;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordered_items_list);

        initialize();

        list.clear();
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

        StringRequest stringRequest = new StringRequest(Request.Method.POST, UrlLinks.UrlViewMyOrders, new Response.Listener<String>() {
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
                    Toast.makeText(OrderedItemsListActivity.this, response, Toast.LENGTH_LONG).show();
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
                params.put("phone_number", getIntent().getStringExtra("phone"));
                params.put("order_number", getIntent().getStringExtra("order_number"));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void initialize() {

        recyclerView = findViewById(R.id.ordered_items_recyclerView);
        list = new ArrayList<>();

        adapter = new MyOrdersAdapter(list);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        swipeRefreshLayout = findViewById(R.id.ordered_items_SwipeRefreshLayout);
        toolbar = findViewById(R.id.ordered_items_toolbar);

        sharedPreferences = getSharedPreferences("Account_Information", MODE_PRIVATE);
        setSupportActionBar(toolbar);
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
}
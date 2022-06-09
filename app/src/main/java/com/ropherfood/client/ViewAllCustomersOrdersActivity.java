package com.ropherfood.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ViewAllCustomersOrdersActivity extends AppCompatActivity  implements SwipeRefreshLayout.OnRefreshListener{

    Toolbar toolbar;
    RecyclerView recyclerView;
    ArrayList<FinalOrdersData> list;
    SwipeRefreshLayout swipeRefreshLayout;
    FinalOrdersListAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_customers_orders);

        initialize();

        loadAllCustomersOrdersData();

        swipeRefreshLayout.setOnRefreshListener(this);

        toolbar.setNavigationOnClickListener(v -> finish());
        
    }

    private void loadAllCustomersOrdersData() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://dimensioning-sixths.000webhostapp.com/ropher_food/view_all_final_orders.php", response -> {

            try {
                JSONObject jsonObject = new JSONObject(response);
                String success = jsonObject.getString("success");
                String message = jsonObject.getString("message");
                JSONArray jsonArray = jsonObject.getJSONArray("view_final_orders");

                if (success.equals("1")) {

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        String id = jsonObject1.getString("id");
                        String order_number = jsonObject1.getString("order_number");
                        String name = jsonObject1.getString("name");
                        String email = jsonObject1.getString("email");
                        String phone = jsonObject1.getString("phone");
                        String address = jsonObject1.getString("address");
                        String total_orders = jsonObject1.getString("total_orders");
                        String total_items = jsonObject1.getString("total_items");
                        String total_amount = jsonObject1.getString("total_amount");
                        String payment_method = jsonObject1.getString("payment_method");
                        String status = jsonObject1.getString("status");
                        String date = jsonObject1.getString("date");

                        FinalOrdersData finalOrdersData = new FinalOrdersData();
                        finalOrdersData.id = id;
                        finalOrdersData.order_number = order_number;
                        finalOrdersData.name = name;
                        finalOrdersData.email = email;
                        finalOrdersData.phone = phone;
                        finalOrdersData.address = address;
                        finalOrdersData.total_orders = total_orders;
                        finalOrdersData.total_items = total_items;
                        finalOrdersData.total_amount = total_amount;
                        finalOrdersData.payment_method = payment_method;
                        finalOrdersData.status = status;
                        finalOrdersData.date = date;

                        list.add(finalOrdersData);
                    }

                    recyclerView.setAdapter(adapter);
                    progressDialog.dismiss();

                }else if(success.equals("0")){

                    progressDialog.dismiss();
                    displayAlertDialog(message);
                    Log.e("TAG", response);

                }

            } catch (JSONException e) {

                e.printStackTrace();
                displayAlertDialog("Cannot display list.");
                Log.e("TAG", response);
                progressDialog.dismiss();

            }
        }, error -> {

            error.printStackTrace();
            displayAlertDialog("Failed to connect to sever");
            progressDialog.dismiss();

        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void initialize() {

        recyclerView = findViewById(R.id.view_all_customers_orders_recyclerView);
        swipeRefreshLayout = findViewById(R.id.view_all_customers_orders_SwipeRefreshLayout);
        list = new ArrayList<>();
        adapter = new FinalOrdersListAdapter(list);
        toolbar = findViewById(R.id.view_all_customers_orders_toolbar);

        setSupportActionBar(toolbar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onRefresh() {

        list.clear();
        loadAllCustomersOrdersData();
        swipeRefreshLayout.setRefreshing(false);

    }

    private void displayAlertDialog(String message){

        final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setCancelable(false);

        builder.setPositiveButton("Ok", (dialog, which) -> builder.setCancelable(true));

        androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    
}
package com.ropherfood.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class OrderTrackingActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView orderPlaced, pendingDelivery, outForDelivery, delivered;
    String state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_tracking);

        initialize();

        setStates(getIntent().getStringExtra("order_status").toString());

        toolbar.setNavigationOnClickListener(v -> finish());

    }

    private void setStates(String state) {

        // 1 -> Order Placed
        // 2 -> Pending Delivery
        // 3 -> Out for Delivery
        // 4 -> Delivered

        if(state.equals("1")){

            orderPlaced.setTextColor(Color.GREEN);
            orderPlaced.setTypeface(null, Typeface.BOLD);

        }

        if(state.equals("2")){
            orderPlaced.setTextColor(getResources().getColor(R.color.main_color));
//            orderPlaced.setTypeface(null, Typeface.BOLD);

            pendingDelivery.setTextColor(Color.GREEN);
            pendingDelivery.setTypeface(null, Typeface.BOLD);
        }

        if(state.equals("3")){
            orderPlaced.setTextColor(getResources().getColor(R.color.main_color));
//            orderPlaced.setTypeface(null, Typeface.BOLD);

            pendingDelivery.setTextColor(getResources().getColor(R.color.main_color));
//            pendingDelivery.setTypeface(null, Typeface.BOLD);

            outForDelivery.setTextColor(Color.GREEN);
            outForDelivery.setTypeface(null, Typeface.BOLD);
        }

        if(state.equals("4")){

            orderPlaced.setTextColor(getResources().getColor(R.color.main_color));
//            orderPlaced.setTypeface(null, Typeface.BOLD);

            pendingDelivery.setTextColor(getResources().getColor(R.color.main_color));
//            pendingDelivery.setTypeface(null, Typeface.BOLD);

            outForDelivery.setTextColor(getResources().getColor(R.color.main_color));
//            outForDelivery.setTypeface(null, Typeface.BOLD);

            delivered.setTextColor(Color.GREEN);
            delivered.setTypeface(null, Typeface.BOLD);
        }
    }

    private void initialize() {

        orderPlaced = findViewById(R.id.order_status_order_placed_textView);
        pendingDelivery = findViewById(R.id.order_status_pending_delivery_textView);
        outForDelivery = findViewById(R.id.order_status_out_for_delivery_textView);
        delivered = findViewById(R.id.order_status_delivered_textView);

        toolbar = findViewById(R.id.order_tracking_toolbar);
        setSupportActionBar(toolbar);

    }

    private void loadData() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, UrlLinks.UrlViewCategories, response -> {

            try {
                JSONObject jsonObject = new JSONObject(response);
                String success = jsonObject.getString("success");
                String message = jsonObject.getString("message");
                JSONArray jsonArray = jsonObject.getJSONArray("view_state_array");

                if(success.equals("1")){

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        String state = jsonObject1.getString("state");

                        setStates(state);

                    }

                    progressDialog.dismiss();
                    Log.e("TAG", response);

                }else if(success.equals("0")){

                    progressDialog.dismiss();
                    Log.e("TAG", response);
                }

            } catch (JSONException e) {

                e.printStackTrace();
                Log.e("TAG", response);
                progressDialog.dismiss();

            }
        }, error -> {

            Log.e("TAG", error.getMessage());
            progressDialog.dismiss();

        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

}
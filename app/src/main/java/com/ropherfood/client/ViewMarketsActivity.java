package com.ropherfood.client;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ViewMarketsActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    Toolbar toolbar;
    RecyclerView viewMarketsRecyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<MarketsData> marketsList;
    MarketsListAdapter adapter;
    TextView noItemsToDisplayTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_markets);

        initialize();

        toolbar.setNavigationOnClickListener(v -> finish());

        marketsList.clear();
        loadData();

        swipeRefreshLayout.setOnRefreshListener(this);
    }

    private void loadData() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, UrlLinks.UrlViewMarkets, response -> {

            try {
                JSONObject jsonObject = new JSONObject(response);
                String success = jsonObject.getString("success");
                String message = jsonObject.getString("message");
                JSONArray jsonArray = jsonObject.getJSONArray("view_food_items_array");

                if(success.equals("1")){

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        String marketId = jsonObject1.getString("market_id");
                        String marketName = jsonObject1.getString("market_name");


                        MarketsData marketData = new MarketsData();
                        marketData.marketId = marketId;
                        marketData.marketName = marketName;

                        marketsList.add(marketData);
                    }

                    viewMarketsRecyclerView.setAdapter(adapter);
                    progressDialog.dismiss();
                    Log.e("TAG", response);

                }else if(success.equals("0")){

                    progressDialog.dismiss();
                    displayAlertDialog(message);
                    noItemsToDisplayTextView.setVisibility(View.VISIBLE);
                    viewMarketsRecyclerView.setVisibility(View.GONE);
                    Log.e("TAG", response);
                }

            } catch (JSONException e) {

                e.printStackTrace();
                displayAlertDialog("Cannot display list.");
                noItemsToDisplayTextView.setVisibility(View.VISIBLE);
                viewMarketsRecyclerView.setVisibility(View.GONE);
                Log.e("TAG", response);
                progressDialog.dismiss();

            }
        }, error -> {

            displayAlertDialog("Failed to connect to sever");
            noItemsToDisplayTextView.setVisibility(View.VISIBLE);
            viewMarketsRecyclerView.setVisibility(View.GONE);
            Log.e("TAG", error.getMessage());
            progressDialog.dismiss();

        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void initialize() {

        viewMarketsRecyclerView = findViewById(R.id.view_markets_recyclerView);
        swipeRefreshLayout = findViewById(R.id.view_markets_swipeRefreshLayout);
        noItemsToDisplayTextView = findViewById(R.id.view_markets_no_items_textView);
        marketsList = new ArrayList<>();
        adapter = new MarketsListAdapter(marketsList);
        toolbar = findViewById(R.id.view_markets_toolbar);

        setSupportActionBar(toolbar);
        viewMarketsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.markets_menu, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.markets_search_menu_item).getActionView();

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
            startActivity(new Intent(ViewMarketsActivity.this, AddMarketsActivity.class));

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {

        marketsList.clear();
        loadData();
        swipeRefreshLayout.setRefreshing(false);
    }

    private void displayAlertDialog(String message){

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setCancelable(false);

        builder.setPositiveButton("Ok", (dialog, which) -> builder.setCancelable(true));

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
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
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

public class ViewCategoriesActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    Toolbar toolbar;
    RecyclerView viewCategoriesRecyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<CategoryData> categoryList;
    CategoryListAdapter adapter;
    TextView noItemsToDisplayTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_categories);

        initialize();

        toolbar.setNavigationOnClickListener(v -> finish());

        categoryList.clear();
        loadData();

        swipeRefreshLayout.setOnRefreshListener(this);

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
                JSONArray jsonArray = jsonObject.getJSONArray("view_food_items_array");

                if(success.equals("1")){

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        String categoryId = jsonObject1.getString("category_id");
                        String categoryName = jsonObject1.getString("category_name");


                        CategoryData categoryData = new CategoryData();
                        categoryData.categoryId = categoryId;
                        categoryData.categoryName = categoryName;

                        categoryList.add(categoryData);
                    }

                    viewCategoriesRecyclerView.setAdapter(adapter);
                    progressDialog.dismiss();
                    Log.e("TAG", response);

                }else if(success.equals("0")){

                    progressDialog.dismiss();
                    displayAlertDialog(message);
                    noItemsToDisplayTextView.setVisibility(View.VISIBLE);
                    viewCategoriesRecyclerView.setVisibility(View.GONE);
                    Log.e("TAG", response);
                }

            } catch (JSONException e) {

                e.printStackTrace();
                displayAlertDialog("Cannot display list.");
                noItemsToDisplayTextView.setVisibility(View.VISIBLE);
                viewCategoriesRecyclerView.setVisibility(View.GONE);
                Log.e("TAG", response);
                progressDialog.dismiss();

            }
        }, error -> {

            displayAlertDialog("Failed to connect to sever");
            noItemsToDisplayTextView.setVisibility(View.VISIBLE);
            viewCategoriesRecyclerView.setVisibility(View.GONE);
            Log.e("TAG", error.getMessage());
            progressDialog.dismiss();

        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void initialize() {

        viewCategoriesRecyclerView = findViewById(R.id.view_categories_recyclerView);
        swipeRefreshLayout = findViewById(R.id.view_categories_swipeRefreshLayout);
        noItemsToDisplayTextView = findViewById(R.id.view_categories_no_items_textView);
        categoryList = new ArrayList<>();
        adapter = new CategoryListAdapter(categoryList);
        toolbar = findViewById(R.id.view_categories_toolbar);

        setSupportActionBar(toolbar);
        viewCategoriesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.category_menu, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.category_search_menu_item).getActionView();

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

        if(item.getItemId() == R.id.category_add_menu_item)
            startActivity(new Intent(ViewCategoriesActivity.this, AddCategoriesActivity.class));

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {

        categoryList.clear();
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
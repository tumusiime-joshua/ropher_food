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

public class MakeOrdersCategoryActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    Toolbar toolbar;
    RecyclerView recyclerView;
    ArrayList<CategoryData> list;
    SwipeRefreshLayout swipeRefreshLayout;
    MakeOrdersCategoryListAdapter adapter;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_orders_category);

        initialize();

        list.clear();
        loadData();

        swipeRefreshLayout.setOnRefreshListener(this);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MakeOrdersCategoryActivity.this, MainActivity.class));
                finish();
            }
        });

    }

    private void loadData() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, UrlLinks.view_all_item_categories, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    String message = jsonObject.getString("message");
                    JSONArray jsonArray = jsonObject.getJSONArray("view_items_category_array");

                    if (success.equals("1")) {

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            String category = jsonObject1.getString("category");


                            CategoryData categoryData = new CategoryData();
                            categoryData.category = category;

                            list.add(categoryData);
                        }

                        recyclerView.setAdapter(adapter);
                        progressDialog.dismiss();

                    }else if(success.equals("0")){

                        progressDialog.dismiss();
                        displayAlertDialog(message);
                    }

                } catch (JSONException e) {

//                    e.printStackTrace();
                    displayAlertDialog("Cannot display list.");
//                    displayAlertDialog(response);
//                    Toast.makeText(MyOrdersActivity.this, response, Toast.LENGTH_LONG).show();
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
                params.put("phone", sharedPreferences.getString("phone_number", ""));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void initialize() {

        recyclerView = findViewById(R.id.make_orders_category_recyclerView);
        list = new ArrayList<>();

        adapter = new MakeOrdersCategoryListAdapter(list);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        swipeRefreshLayout = findViewById(R.id.make_orders_category_SwipeRefreshLayout);
        toolbar = findViewById(R.id.make_orders_category_toolbar);

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

}
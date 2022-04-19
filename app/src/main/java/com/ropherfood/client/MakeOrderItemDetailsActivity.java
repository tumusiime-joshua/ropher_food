package com.ropherfood.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import fr.ganfra.materialspinner.MaterialSpinner;

public class MakeOrderItemDetailsActivity extends AppCompatActivity implements View.OnClickListener{

    Toolbar toolbar;
    ImageView imageView;
    TextView foodItemName, foodItemPrice, foodItemDescription;
    Button makeOrder;
    Intent intent;
    EditText quantity;
    SharedPreferences sharedPreferences;

    String name, price, quantityText, description, imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_order_item_details);

        initialize();

        name = intent.getStringExtra("food_name");
        price = intent.getStringExtra("food_price");
        description = intent.getStringExtra("food_description");
        imagePath = intent.getStringExtra("image_path");

        foodItemName.setText(name);
        foodItemPrice.setText(price);
        foodItemDescription.setText(description);

        Picasso.get().load(imagePath).into(imageView);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        makeOrder.setOnClickListener(this);

    }

    private void initialize() {

        foodItemName = findViewById(R.id.make_order_item_details_foodName_textView);
        foodItemPrice = findViewById(R.id.make_order_item_details_foodPrice_textView);
        foodItemDescription = findViewById(R.id.make_order_item_details_foodDetails_textView);

        makeOrder = findViewById(R.id.make_order_item_details_button);
        imageView = findViewById(R.id.make_order_item_details_imageView);
        toolbar = findViewById(R.id.make_order_item_details_toolbar);

        quantity = findViewById(R.id.make_order_item_details_quantity_option);
        intent = getIntent();

        sharedPreferences = getSharedPreferences("Account_Information", MODE_PRIVATE);

        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.make_order_item_details_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.make_order_item_details_call_menu_item)
            callBranchToOrder();

        return super.onOptionsItemSelected(item);
    }

    private void callBranchToOrder() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to order through call ?");
        builder.setCancelable(false);

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                builder.setCancelable(true);
            }
        });

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent callNumber = new Intent(Intent.ACTION_DIAL);
                callNumber.setData(Uri.parse("tel: " + "+256771611956"));
                startActivity(callNumber);

            }

        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.make_order_item_details_button){

            if(quantity.getText().toString().isEmpty()){

                displayAlertDialog("Choose the number of items you want.");
            }else{

                submitOrder();
            }
        }
    }

    private void submitOrder() {

            submitMyOrder();
    }

    private void submitMyOrder() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Submitting order...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, UrlLinks.UrlMakeOrder, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    String message = jsonObject.getString("message");

                    if(success.equals("1")){

                        progressDialog.dismiss();
                        Toast.makeText(MakeOrderItemDetailsActivity.this, message, Toast.LENGTH_LONG).show();
                        finish();

                    }else if(success.equals("0")){

                        progressDialog.dismiss();
                        Toast.makeText(MakeOrderItemDetailsActivity.this, message, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {

                    progressDialog.dismiss();
                    e.printStackTrace();
                    displayAlertDialog("Internal server error.");
                    Log.e("TAG", response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                progressDialog.dismiss();
                displayAlertDialog("Field to connect due to technical error.");
                Log.e("TAG", error.getMessage());

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("food_item_name", intent.getStringExtra("food_name"));
                params.put("food_item_price", price);
                params.put("food_item_quantity", quantity.getText().toString());
                params.put("email", sharedPreferences.getString("email", ""));
                params.put("phone_number", sharedPreferences.getString("phone_number", ""));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
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
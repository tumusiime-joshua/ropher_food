package com.ropherfood.client;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import fr.ganfra.materialspinner.MaterialSpinner;

public class AddFoodItemActivity extends AppCompatActivity implements View.OnClickListener{

    Button addFoodItemButton, updateFoodItemButton, addFoodItemImageButton;
    TextInputEditText foodItemName, foodItemPrice, foodItemDescription;
    ImageView foodItemImage;
    Bitmap bitmap;
    private final int GET_FOOD_ITEM_IMAGE = 1;
    String stringForEncodedImage, foodItemImageLabel;
    Intent intent;
    Toolbar toolbar;
    Spinner categorySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food_item);

        initialize();

        intentToSetValuesForUpdating();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        addFoodItemImageButton.setOnClickListener(this);
        updateFoodItemButton.setOnClickListener(this);
        addFoodItemButton.setOnClickListener(this);
    }

    private void initialize() {

        addFoodItemButton = findViewById(R.id.add_food_item_button);
        updateFoodItemButton = findViewById(R.id.update_food_item_button);
        addFoodItemImageButton = findViewById(R.id.add_food_item_image_button);
        foodItemName = findViewById(R.id.add_food_item_name_editText);
        foodItemPrice = findViewById(R.id.add_food_item_price_editText);
        foodItemDescription = findViewById(R.id.add_food_item_description_editText);
        foodItemImage = findViewById(R.id.add_food_item_photo_imageView);
        stringForEncodedImage = "";
        intent = getIntent();
        toolbar = findViewById(R.id.add_food_items_toolbar);
        setSupportActionBar(toolbar);

        categorySpinner = findViewById(R.id.add_food_item_category_Spinner);
    }

    private void intentToSetValuesForUpdating(){

        if(intent.hasExtra("name") && intent.hasExtra("price")){

            addFoodItemButton.setVisibility(View.GONE);
            updateFoodItemButton.setVisibility(View.VISIBLE);

            foodItemName.setText(intent.getStringExtra("id"));
            foodItemName.setText(intent.getStringExtra("name"));
            foodItemPrice.setText(intent.getStringExtra("price"));
            foodItemDescription.setText(intent.getStringExtra("description"));

            Picasso.get().load(intent.getStringExtra("image_path")).into(foodItemImage);
        }

    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.add_food_item_image_button)
            getFoodItemImage();

        if(v.getId() == R.id.add_food_item_button)
            verifyFieldsAndAddFoodItem();

        if(v.getId() == R.id.update_food_item_button)
            updateFoodItem();

    }

    private void getFoodItemImage(){

        Intent addFoodImageIntent = new Intent(Intent.ACTION_GET_CONTENT);
        addFoodImageIntent.setType("image/*");
        startActivityForResult(addFoodImageIntent, GET_FOOD_ITEM_IMAGE);

    }

    private void updateFoodItem() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, UrlLinks.UrlUpdateFoodItemsInDatabase, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    String message = jsonObject.getString("message");

                    if(success.equals("1")){

                        progressDialog.dismiss();
                        Toast.makeText(AddFoodItemActivity.this, message, Toast.LENGTH_LONG).show();
                        finish();

                    }else if(success.equals("0")){

                        progressDialog.dismiss();
                        Toast.makeText(AddFoodItemActivity.this, message, Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {

                    progressDialog.dismiss();
                    e.printStackTrace();
                    displayAlertDialog("Internal error.");
                    Log.e("TAG", response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                progressDialog.dismiss();
                displayAlertDialog(error.getMessage());
                Log.e("TAG", error.getMessage());

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();

                params.put("food_item_id", intent.getStringExtra("id"));
                params.put("food_item_name", foodItemName.getText().toString());
                params.put("food_item_price", foodItemPrice.getText().toString());
                params.put("food_item_description", foodItemDescription.getText().toString());
                params.put("food_item_category", categorySpinner.getSelectedItem().toString());

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void verifyFieldsAndAddFoodItem() {

        if(foodItemName.getText().toString().isEmpty()){

            foodItemName.setError("Field cannot be empty.");

        }else if(foodItemPrice.getText().toString().isEmpty()){

            foodItemPrice.setError("Field cannot be empty.");

        }else if(foodItemDescription.getText().toString().isEmpty()) {

            foodItemDescription.setError("Field cannot be empty.");

        }else if(stringForEncodedImage.equals("")){

            Toast.makeText(AddFoodItemActivity.this, "Add an image for the food item.", Toast.LENGTH_LONG).show();

        }else{

            addFoodItemAction();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GET_FOOD_ITEM_IMAGE && resultCode == RESULT_OK && data != null){

            try {

                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                foodItemImage.setImageBitmap(bitmap);

            } catch (IOException e) {

                e.printStackTrace();
            }

            foodItemImageLabel = "IMG_" + System.currentTimeMillis() + ".jpeg";

            stringForEncodedImage = encodedImageString(bitmap);
        }
    }

    private String encodedImageString(Bitmap bitmap) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);

        byte[] bytes = byteArrayOutputStream.toByteArray();
        String encodedImage = Base64.encodeToString(bytes, Base64.DEFAULT);

        return encodedImage;
    }

    private void addFoodItemAction() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, UrlLinks.UrlAddFoodItemsToDatabase, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    String message = jsonObject.getString("message");

                    if(success.equals("1")){

                        progressDialog.dismiss();
                        Toast.makeText(AddFoodItemActivity.this, message, Toast.LENGTH_LONG).show();
                        finish();

                    }else if(success.equals("0")){

                        progressDialog.dismiss();
                        Toast.makeText(AddFoodItemActivity.this, message, Toast.LENGTH_LONG).show();
                        finish();

                    }
                } catch (JSONException e) {

                    progressDialog.dismiss();
                    e.printStackTrace();
                    displayAlertDialog("Internal error.");
                    Log.e("TAG", response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                progressDialog.dismiss();
                displayAlertDialog("System error.");

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("food_item_name", foodItemName.getText().toString());
                params.put("food_item_price", foodItemPrice.getText().toString());
                params.put("food_item_description", foodItemDescription.getText().toString());
                params.put("food_item_category", categorySpinner.getSelectedItem().toString());
                params.put("encoded_image", stringForEncodedImage);
                params.put("image_label", foodItemImageLabel);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

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
package com.ropherfood.client;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ConfirmOrderActivity extends AppCompatActivity implements View.OnClickListener {

    EditText first_name, last_name, email, phone, address, total_orders, total_items, total_amount;
    Toolbar toolbar;
    Intent intent;
    Button submit;
    SharedPreferences sharedPreferences;
    RadioGroup radioGroup;

    String radioButtonValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);

        initialized();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });



        submit.setOnClickListener(this);


    }

    public void initialized(){

        first_name = findViewById(R.id.confirm_orders_first_name);
        last_name = findViewById(R.id.confirm_orders_last_name);
        email = findViewById(R.id.confirm_orders_email);
        phone = findViewById(R.id.confirm_orders_phone_number);
        address = findViewById(R.id.confirm_orders_address);
        total_orders = findViewById(R.id.confirm_orders_total_orders);
        total_items = findViewById(R.id.confirm_orders_total_items);
        total_amount = findViewById(R.id.confirm_orders_total_amount);
        submit = findViewById(R.id.confirm_orders_submit_button);
        radioGroup = findViewById(R.id.confirm_orders_radioGroup);

        toolbar = findViewById(R.id.confirm_orders_toolbar);
        setSupportActionBar(toolbar);
        sharedPreferences = getSharedPreferences("Account_Information", MODE_PRIVATE);

        intent = getIntent();

        total_orders.setText(intent.getStringExtra("total_orders"));
        total_items.setText(intent.getStringExtra("total_items"));
        total_amount.setText(intent.getStringExtra("total_amount"));

        first_name.setText(sharedPreferences.getString("first_name", ""));
        last_name.setText(sharedPreferences.getString("last_name", ""));
        email.setText(sharedPreferences.getString("email", ""));
        phone.setText(sharedPreferences.getString("phone_number", ""));


    }

    public void displayAlertMessage(String message){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

    public void displayAlertMessageConfirmedSubmission(String message){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setCancelable(false);

        builder.setPositiveButton("Go to My Orders", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent1 = new Intent(ConfirmOrderActivity.this, MyOrdersActivity.class);
                startActivity(intent1);
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void submit() {
        int id = radioGroup.getCheckedRadioButtonId();

        if(id == R.id.radioBtn_direct_cash){
            radioButtonValue = "Direct Cash";
        }else if(id == R.id.radioBtn_mobile_money){
            radioButtonValue = "Mobile Money";
        }else{
            radioButtonValue = "Visa Card";
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, UrlLinks.add_final_orders, response -> {

            try {
                JSONObject jsonObject = new JSONObject(response);
                String success = jsonObject.getString("success");
                String message = jsonObject.getString("message");

                if(success.equals("1")){

                    displayAlertMessageConfirmedSubmission(message);

                }else if(success.equals("0")){

                    displayAlertMessage(message);
                }

            } catch (JSONException e) {

                displayAlertMessage("Internal system error.");
                Log.e("TAG", response);
            }

        }, error -> {

            displayAlertMessage("Cannot submit request.");
            Log.e("TAG", error.getMessage());

        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("name", first_name.getText().toString()+" "+last_name.getText().toString());
                params.put("email", email.getText().toString());
                params.put("phone", phone.getText().toString());
                params.put("address", address.getText().toString());
                params.put("total_items", total_items.getText().toString());
                params.put("total_orders", total_orders.getText().toString());
                params.put("total_amount", total_amount.getText().toString());
                params.put("order_number", ""+System.currentTimeMillis());
                params.put("payment_method", radioButtonValue);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void verifyAndSubmit() {

        if(first_name.getText().toString().length() == 0){

            displayAlertMessage("First Name field cannot be empty.");

        } else if(last_name.getText().toString().length() == 0){

            displayAlertMessage("Last Name field cannot be empty.");

        }else if(email.getText().toString().length() == 0){

            displayAlertMessage("Email field cannot be empty.");

        } else if(phone.getText().toString().length() == 0){

            displayAlertMessage("Phone Number field cannot be empty.");

        }else if(address.getText().toString().length() == 0){

            displayAlertMessage("Please fill in the address where you would like your items to be delivered to.");

        }else if(total_orders.getText().toString().length() == 0){

            displayAlertMessage("Total Orders field cannot be empty.");

        }else if(total_items.getText().toString().length() == 0){

            displayAlertMessage("Total Items field cannot be empty.");

        } else if(total_amount.getText().toString().length() == 0){

            displayAlertMessage("Total Amount field cannot be empty.");

        }else {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Do you want to submit your Order ?");
            builder.setCancelable(false);

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    submit();
                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    builder.setCancelable(true);
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }


    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.confirm_orders_submit_button){
            verifyAndSubmit();
        }

    }
}
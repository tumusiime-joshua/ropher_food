package com.ropherfood.client;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CreateAccountActivity extends AppCompatActivity implements View.OnClickListener{

    EditText firstName, lastName, emailAddress, address, mobileNumber, password, confirmPassword;
    Button createAccountButton;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        initialize();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        createAccountButton.setOnClickListener(this);
    }

    private void initialize() {

        firstName = findViewById(R.id.create_account_first_name_editText);
        lastName = findViewById(R.id.create_account_last_name_editText);
        emailAddress = findViewById(R.id.create_account_email_editText);
        address = findViewById(R.id.create_account_address_editText);
        mobileNumber = findViewById(R.id.create_account_mobile_phone_number_editText);
        password = findViewById(R.id.create_account_password_editText);
        confirmPassword = findViewById(R.id.create_account_confirm_password_editText);
        createAccountButton = findViewById(R.id.create_account_button);
        toolbar = findViewById(R.id.create_account_toolbar);

        setSupportActionBar(toolbar);
    }

    //Method to verify the fields and upload the account information to the database
    private void verifyAndCreateAccount(){

        if(firstName.getText().toString().isEmpty()){
            displayAlertDialog("First name field cannot be empty.");

        }else if(lastName.getText().toString().isEmpty()){
            displayAlertDialog("Last name field cannot be empty.");

        }else if(emailAddress.getText().toString().isEmpty()){
            displayAlertDialog("E-mail field cannot be empty.");

        }else if(address.getText().toString().isEmpty()){
            displayAlertDialog("Address field cannot be empty.");

        }else if(mobileNumber.getText().toString().isEmpty()){
            displayAlertDialog("Mobile Phone Number field cannot be empty.");

        }else if(password.getText().toString().isEmpty()){
            displayAlertDialog("Password field cannot be empty.");

        }else if(confirmPassword.getText().toString().isEmpty()){
            displayAlertDialog("Type password again to confirm.");

        }else if(!password.getText().toString().equals(confirmPassword.getText().toString())){
            displayAlertDialog("The two passwords provided do not match.");

        }else{
            createAccount();
        }
    }

    //Method to upload account information to the database
    private void createAccount() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creating account...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, UrlLinks.UrlCreateUserAccount, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    String message = jsonObject.getString("message");

                    if(success.equals("1")){

                        progressDialog.dismiss();
                        Toast.makeText(CreateAccountActivity.this, message, Toast.LENGTH_LONG).show();
                        finish();

                    }else if(success.equals("0")){

                        progressDialog.dismiss();
                        Toast.makeText(CreateAccountActivity.this, message, Toast.LENGTH_LONG).show();
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
                params.put("first_name", firstName.getText().toString());
                params.put("last_name", lastName.getText().toString());
                params.put("email", emailAddress.getText().toString());
                params.put("address", address.getText().toString());
                params.put("phone_number", mobileNumber.getText().toString());
                params.put("account_type", "Customer");
                params.put("password", password.getText().toString());
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

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.create_account_button)
            verifyAndCreateAccount();
    }
}
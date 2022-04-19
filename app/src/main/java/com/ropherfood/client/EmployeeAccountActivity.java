package com.ropherfood.client;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import fr.ganfra.materialspinner.MaterialSpinner;

public class EmployeeAccountActivity extends AppCompatActivity implements View.OnClickListener {

    TextInputEditText firstName, lastName, emailEditTex, addressEditText, mobileNumberEditText, passwordEditText, confirmPasswordEditTex;
    Spinner accountTypeSpinner;
    Button updateAccountButton, createAccountButton;
    Toolbar toolbar;
    Intent intent;

    String account_type_text, branch_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_account);

        initialize();

        if(intent.hasExtra("email")){

            firstName.setEnabled(false);
            lastName.setEnabled(false);
            emailEditTex.setEnabled(false);
            addressEditText.setEnabled(false);
            mobileNumberEditText.setEnabled(false);
            passwordEditText.setEnabled(false);
            confirmPasswordEditTex.setEnabled(false);

            firstName.setText(intent.getStringExtra("first_name"));
            lastName.setText(intent.getStringExtra("last_name"));
            emailEditTex.setText(intent.getStringExtra("email"));
            addressEditText.setText(intent.getStringExtra("address"));
            mobileNumberEditText.setText(intent.getStringExtra("mobile_phone_number"));

            createAccountButton.setVisibility(View.GONE);
            updateAccountButton.setVisibility(View.VISIBLE);

        }

        createAccountButton.setOnClickListener(this);
        updateAccountButton.setOnClickListener(this);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void initialize() {

        firstName = findViewById(R.id.employee_account_first_name_editText);
        lastName = findViewById(R.id.employee_account_last_name_editText);
        emailEditTex = findViewById(R.id.employee_account_email_editText);
        addressEditText = findViewById(R.id.employee_account_address_editText);
        mobileNumberEditText = findViewById(R.id.employee_account_mobile_phone_number_editText);
        passwordEditText = findViewById(R.id.employee_account_password_editText);
        confirmPasswordEditTex = findViewById(R.id.employee_account_confirm_password_editText);
        accountTypeSpinner = findViewById(R.id.employee_account_account_type_Spinner);
        createAccountButton = findViewById(R.id.employee_account_create_button);
        updateAccountButton = findViewById(R.id.employee_account_update_button);
        toolbar = findViewById(R.id.employee_account_toolbar);
        intent = getIntent();

        setSupportActionBar(toolbar);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.employee_account_create_button)
            verifyFieldsAndCreateAccount();

        if (v.getId() == R.id.employee_account_update_button)
            verifyFieldsAndUpdateAccount();

    }

    private void verifyFieldsAndUpdateAccount() {

        try{

            account_type_text = accountTypeSpinner.getSelectedItem().toString();
            updateEmployeeAccount();

        }catch (Exception e){

            Toast.makeText(EmployeeAccountActivity.this, "Choose account type.", Toast.LENGTH_LONG).show();
        }

    }

    private void updateEmployeeAccount() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating account...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, UrlLinks.UrlUpdateEmployeesAccount, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    String message = jsonObject.getString("message");

                    if(success.equals("1")){

                        progressDialog.dismiss();
                        Toast.makeText(EmployeeAccountActivity.this, message, Toast.LENGTH_LONG).show();
                        finish();

                    }else if(success.equals("0")){

                        progressDialog.dismiss();
                        Toast.makeText(EmployeeAccountActivity.this, message, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {

                    progressDialog.dismiss();
                    e.printStackTrace();
                    displayAlertDialog("Internal server error.");
//                    displayAlertDialog(response);
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
                params.put("phone_number", mobileNumberEditText.getText().toString());
                params.put("account_type", accountTypeSpinner.getSelectedItem().toString());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void verifyFieldsAndCreateAccount() {

        if (firstName.getText().toString().isEmpty()){

            displayAlertDialog("First name field cannot be empty.");

        }else if(lastName.getText().toString().isEmpty()){

            displayAlertDialog("Last name field cannot be empty.");

        }else if(emailEditTex.getText().toString().isEmpty()){

            displayAlertDialog("Email field cannot be empty.");

        }else if(addressEditText.getText().toString().isEmpty()){

            displayAlertDialog("Address field cannot be empty.");

        }else if(mobileNumberEditText.getText().toString().isEmpty()){

            displayAlertDialog("Mobile number field cannot be empty.");

        }else if(passwordEditText.getText().toString().isEmpty()){

            displayAlertDialog("Password field cannot be empty.");

        }else if(passwordEditText.getText().length() < 8){

            displayAlertDialog("Password cannot be less than eight characters.");

        }else if(confirmPasswordEditTex.getText().toString().isEmpty()){

            displayAlertDialog("Confirm password field cannot be empty.");

        }else if(!passwordEditText.getText().toString().equals(confirmPasswordEditTex.getText().toString())){

            displayAlertDialog("The passwords provide do not match.");

        }else{

            try{

                accountTypeSpinner.getSelectedItem().toString();
                createEmployeeAccount();

            }catch (Exception e){

                Toast.makeText(EmployeeAccountActivity.this, "Choose account type or branch.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void createEmployeeAccount() {

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
                        Toast.makeText(EmployeeAccountActivity.this, message, Toast.LENGTH_LONG).show();
                        finish();

                    }else if(success.equals("0")){

                        progressDialog.dismiss();
                        Toast.makeText(EmployeeAccountActivity.this, message, Toast.LENGTH_LONG).show();
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
                params.put("email", emailEditTex.getText().toString());
                params.put("address", addressEditText.getText().toString());
                params.put("phone_number", mobileNumberEditText.getText().toString());
                params.put("account_type", accountTypeSpinner.getSelectedItem().toString());
                params.put("password", passwordEditText.getText().toString());
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
package com.ropherfood.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

public class MyAccountActivity extends AppCompatActivity {

    Toolbar toolbar;
    EditText firstName, lastName, email, address, mobileNumber, currentPassword, password, confirmPassword;
    Menu menuItems;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        initialize();
        disableFieldsByDefault();

        firstName.setText(sharedPreferences.getString("first_name", ""));
        lastName.setText(sharedPreferences.getString("last_name", ""));
        email.setText(sharedPreferences.getString("email", ""));
        address.setText(sharedPreferences.getString("address", ""));
        mobileNumber.setText(sharedPreferences.getString("phone_number", ""));


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MyAccountActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    private void initialize() {

        toolbar = findViewById(R.id.my_account_toolbar);
        firstName = findViewById(R.id.my_account_first_name_editText);
        lastName = findViewById(R.id.my_account_last_name_editText);
        email = findViewById(R.id.my_account_email_editText);
        address = findViewById(R.id.my_account_address_editText);
        mobileNumber = findViewById(R.id.my_account_mobile_phone_number_editText);
        currentPassword = findViewById(R.id.my_account_current_password_editText);
        password = findViewById(R.id.my_account_password_editText);
        confirmPassword = findViewById(R.id.my_account_confirm_password_editText);

        sharedPreferences = getSharedPreferences("Account_Information", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        setSupportActionBar(toolbar);
    }

    //This method disables all the fields by default
    private void disableFieldsByDefault(){

        firstName.setEnabled(false);
        lastName.setEnabled(false);
        email.setEnabled(false);
        address.setEnabled(false);
        mobileNumber.setEnabled(false);
        currentPassword.setEnabled(false);
        password.setEnabled(false);
        confirmPassword.setEnabled(false);
    }

    //This method enables all the when clicked on
    private void enableFields(){

        firstName.setEnabled(true);
        lastName.setEnabled(true);
        email.setEnabled(true);
        address.setEnabled(true);
        mobileNumber.setEnabled(true);
        currentPassword.setEnabled(true);
        password.setEnabled(true);
        confirmPassword.setEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.my_account_menu, menu);

        menuItems = menu;
        menuItems.findItem(R.id.my_account_update_menu_item).setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.my_account_edit_menu_item){

            enableFields();
            menuItems.findItem(R.id.my_account_edit_menu_item).setVisible(false);
            menuItems.findItem(R.id.my_account_update_menu_item).setVisible(true);

        }

        if(item.getItemId() == R.id.my_account_update_menu_item){

            updateTheFields();
            disableFieldsByDefault();
            menuItems.findItem(R.id.my_account_edit_menu_item).setVisible(true);
            menuItems.findItem(R.id.my_account_update_menu_item).setVisible(false);
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateTheFields() {

        if(currentPassword.getText().toString().isEmpty()){

            updateMyAccountWithoutPassword();

        }else {

            if(password.getText().toString().isEmpty()){

                displayAlertDialog("Type a new password.");

            }else if(password.getText().length() < 8){

                displayAlertDialog("Password cannot be less than eight characters.");

            }else if(currentPassword.getText().toString().isEmpty()){

                displayAlertDialog("Type password again to confirm.");

            }else if(!password.getText().toString().equals(confirmPassword.getText().toString())){

                displayAlertDialog("Passwords provided do not match.");
            }else {

                updateMyAccountWithPassword();

            }

        }
    }

    private void updateMyAccountWithPassword() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating account...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, UrlLinks.UrlUpdateMyAccountWithPassword, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    String message = jsonObject.getString("message");

                    if(success.equals("1")){

                        progressDialog.dismiss();
                        Toast.makeText(MyAccountActivity.this, message, Toast.LENGTH_LONG).show();

                    }else if(success.equals("0")){

                        progressDialog.dismiss();
                        Toast.makeText(MyAccountActivity.this, message, Toast.LENGTH_LONG).show();
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
                params.put("email", email.getText().toString());
                params.put("address", address.getText().toString());
                params.put("phone_number", mobileNumber.getText().toString());
                params.put("current_password", currentPassword.getText().toString());
                params.put("new_password", password.getText().toString());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void updateMyAccountWithoutPassword() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating account...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, UrlLinks.UrlUpdateMyAccountWithoutPassword, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    String message = jsonObject.getString("message");

                    if(success.equals("1")){

                        progressDialog.dismiss();
                        Toast.makeText(MyAccountActivity.this, message, Toast.LENGTH_LONG).show();

                    }else if(success.equals("0")){

                        progressDialog.dismiss();
                        Toast.makeText(MyAccountActivity.this, message, Toast.LENGTH_LONG).show();
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
                params.put("email", email.getText().toString());
                params.put("address", address.getText().toString());
                params.put("phone_number", mobileNumber.getText().toString());
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
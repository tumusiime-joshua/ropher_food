package com.ropherfood.client;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.HashMap;
import java.util.Map;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener{

    Button logInButton;
    EditText logInPhoneNumber, logInPassword;
    CheckBox logInStatusCheckBox;
    TextView createAccount;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        initialized();

        checkLogInStatus();

        createAccount.setOnClickListener(this);
        logInButton.setOnClickListener(this);
    }

    private void initialized() {

        logInButton = findViewById(R.id.log_in_button);
        logInPhoneNumber = findViewById(R.id.log_in_phone_number_EditText);
        logInPassword = findViewById(R.id.log_in_password_EditText);
        logInStatusCheckBox = findViewById(R.id.stay_logged_in_CheckBox);
        createAccount = findViewById(R.id.create_account_TextView);

        sharedPreferences = getSharedPreferences("Account_Information", MODE_PRIVATE);
        editor = sharedPreferences.edit();

    }

    public void checkLogInStatus(){

        String status = sharedPreferences.getString("status", "");

        if(status.equals("Keep LogIn")){

            Intent intent1 = new Intent(LogInActivity.this, MainActivity.class);
            intent1.putExtra("first_name", sharedPreferences.getString("first_name", ""));
            intent1.putExtra("last_name", sharedPreferences.getString("last_name", ""));
            intent1.putExtra("email", sharedPreferences.getString("email", ""));
            intent1.putExtra("address", sharedPreferences.getString("address", ""));
            intent1.putExtra("phone_number", sharedPreferences.getString("phone_number", ""));
            intent1.putExtra("account_Type", sharedPreferences.getString("account_type", ""));
            intent1.putExtra("branch", sharedPreferences.getString("branch", ""));
            Toast.makeText(getApplicationContext(), status, Toast.LENGTH_LONG).show();
            startActivity(intent1);
            finish();
        }
    }

    //Check if the fields are not empty and log in
    private void verifyFieldsAndLogIn(){

        if(logInPhoneNumber.getText().toString().isEmpty()){
            displayAlertMessage("Type in a valid email");

        }else if(logInPassword.getText().toString().isEmpty()){
            displayAlertMessage("Type in a valid password");

        }else{
            logIn();
        }
    }

    //The log in action method
    private void logIn() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        progressDialog.setCancelable(false);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, UrlLinks.UrlLogIn, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    String message = jsonObject.getString("message");
                    JSONArray jsonArray = jsonObject.getJSONArray("login_array");

                    if (success.equals("1")) {

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject object = jsonArray.getJSONObject(i);

                            String first_name = object.getString("first_name");
                            String last_name = object.getString("last_name");
                            String email = object.getString("email");
                            String address = object.getString("address");
                            String phone_number = object.getString("phone_number");
                            String account_type = object.getString("account_type");
//

                            Toast.makeText(getApplicationContext(), "Log In Successful", Toast.LENGTH_LONG).show();

                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            if(logInStatusCheckBox.isChecked()){

                                editor.putString("status", "Keep LogIn");
                                editor.putString("first_name", first_name);
                                editor.putString("last_name", last_name);
                                editor.putString("email", email);
                                editor.putString("address", address);
                                editor.putString("phone_number", phone_number);
                                editor.putString("account_Type", account_type);
                                editor.apply();

                                progressDialog.dismiss();

                                Intent intent1 = new Intent(LogInActivity.this, MainActivity.class);
                                intent1.putExtra("first_name",first_name);
                                intent1.putExtra("last_name", last_name);
                                intent1.putExtra("email", email);
                                intent1.putExtra("address", address);
                                intent1.putExtra("phone_number", phone_number);
                                intent1.putExtra("account_Type", account_type);

                                Toast.makeText(LogInActivity.this, "Welcome", Toast.LENGTH_LONG).show();
                                startActivity(intent1);
                                finish();

                            }else{

                                editor.putString("status", "Don,t Keep LogIn");
                                editor.putString("first_name", first_name);
                                editor.putString("last_name", last_name);
                                editor.putString("email", email);
                                editor.putString("address", address);
                                editor.putString("phone_number", phone_number);
                                editor.putString("account_Type", account_type);

                                editor.apply();

                                progressDialog.dismiss();

                                Intent intent1 = new Intent(LogInActivity.this, MainActivity.class);
                                intent1.putExtra("first_name",first_name);
                                intent1.putExtra("last_name", last_name);
                                intent1.putExtra("email", email);
                                intent1.putExtra("address", address);
                                intent1.putExtra("phone_number", phone_number);
                                intent1.putExtra("account_Type", account_type);
                                Toast.makeText(LogInActivity.this, "Welcome", Toast.LENGTH_LONG).show();
                                startActivity(intent1);
                                finish();
                            }

                        }
                    }
                    else if (success.equals("0")) {

                        progressDialog.dismiss();
                        displayAlertMessage(message);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                    progressDialog.dismiss();
                    displayAlertMessage("Internal sever error.");
                    Log.e("TAG", response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                progressDialog.dismiss();
                displayAlertMessage("Failed to connect to internet.");
                Log.e("TAG",error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("phone_number", logInPhoneNumber.getText().toString());
                params.put("password", logInPassword.getText().toString());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void displayAlertMessage(String message){

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

        if(v.getId() == R.id.create_account_TextView)
            startActivity(new Intent(LogInActivity.this, CreateAccountActivity.class));

        if(v.getId() == R.id.log_in_button)
            verifyFieldsAndLogIn();
    }
}
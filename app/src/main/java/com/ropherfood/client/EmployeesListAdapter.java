package com.ropherfood.client;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EmployeesListAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<EmployeesListAdapter.ViewHolder> implements Filterable {

    ArrayList<EmployeeAccountData> list;
    ArrayList<EmployeeAccountData> listCopy;

    public EmployeesListAdapter(ArrayList<EmployeeAccountData> list) {
        this.list = list;
        this.listCopy = list;
    }

    @NonNull
    @Override
    public EmployeesListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.employee_account_list_details, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeesListAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.email.setText(list.get(position).email);
        holder.firstName.setText(list.get(position).firstName);
        holder.lastName.setText(list.get(position).lastName);
        holder.phoneNumber.setText(list.get(position).mobilePhoneNumber);
        holder.address.setText(list.get(position).address);
        holder.accountType.setText(list.get(position).accountType);

        holder.call.setVisibility(View.GONE);
        holder.edit.setVisibility(View.GONE);
        holder.delete.setVisibility(View.GONE);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(holder.call.getVisibility() == View.GONE &&
                        holder.edit.getVisibility() == View.GONE &&
                        holder.delete.getVisibility() == View.GONE){

                    holder.call.setVisibility(View.VISIBLE);
                    holder.edit.setVisibility(View.VISIBLE);
                    holder.delete.setVisibility(View.VISIBLE);

                }else {

                    holder.call.setVisibility(View.GONE);
                    holder.edit.setVisibility(View.GONE);
                    holder.delete.setVisibility(View.GONE);

                }
            }
        });

        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                builder.setMessage("Do you want to call this employee ?");
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
                        callNumber.setData(Uri.parse("tel: " + list.get(position).mobilePhoneNumber));
                        holder.itemView.getContext().startActivity(callNumber);

                    }

                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                builder.setMessage("Do you want to modify this employee's account ?");
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

                        Intent intent = new Intent(holder.itemView.getContext(), EmployeeAccountActivity.class);

                        intent.putExtra("email", list.get(position).email);
                        intent.putExtra("first_name", list.get(position).firstName);
                        intent.putExtra("last_name", list.get(position).lastName);
                        intent.putExtra("address", list.get(position).address);
                        intent.putExtra("mobile_phone_number", list.get(position).mobilePhoneNumber);

                        holder.itemView.getContext().startActivity(intent);

                    }

                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                builder.setMessage("Do you want to delete this employee's account ?");
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

                        final ProgressDialog progressDialog = new ProgressDialog(holder.itemView.getContext());
                        progressDialog.setMessage("Deleting...");
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                        StringRequest stringRequest = new StringRequest(Request.Method.POST, UrlLinks.UrlDeleteEmployeesAccount, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String success = jsonObject.getString("success");
                                    String message = jsonObject.getString("message");

                                    if(success.equals("1")){

                                        progressDialog.dismiss();
                                        Snackbar.make(holder.itemView.getRootView(), message, Snackbar.LENGTH_LONG).show();

                                    }else if(success.equals("0")){

                                        progressDialog.dismiss();
                                        Snackbar.make(holder.itemView.getRootView(), message, Snackbar.LENGTH_LONG).show();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    progressDialog.dismiss();
                                    Log.e("TAG", response);
                                    Snackbar.make(holder.itemView.getRootView(), "Internal system error.", Snackbar.LENGTH_LONG).show();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                progressDialog.dismiss();
                                Log.e("TAG", error.getMessage());
                                Snackbar.make(holder.itemView.getRootView(), "Failed to connect to internet.", Snackbar.LENGTH_LONG).show();
                            }
                        }){
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {

                                Map<String, String> params = new HashMap<>();
                                params.put("phone_number", list.get(position).mobilePhoneNumber);
                                return params;
                            }
                        };

                        RequestQueue requestQueue = Volley.newRequestQueue(holder.itemView.getContext());
                        requestQueue.add(stringRequest);

                    }

                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                String searchText = constraint.toString();

                if(searchText.isEmpty()){

                    list = listCopy;
                }else {

                    ArrayList<EmployeeAccountData> searchResults = new ArrayList<>();

                    for (EmployeeAccountData data: listCopy) {

                        if(data.email.toLowerCase().contains(searchText) ||
                                data.firstName.toLowerCase().contains(searchText) ||
                                data.lastName.toLowerCase().contains(searchText) ||
                                data.mobilePhoneNumber.toLowerCase().contains(searchText) ||
                                data.address.toLowerCase().contains(searchText)){

                            searchResults.add(data);
                        }
                    }

                    list = searchResults;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = list;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                list = (ArrayList<EmployeeAccountData>) results.values;
                notifyDataSetChanged();

            }
        };
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView firstName, lastName, email, address, phoneNumber, accountType, branch, call, edit, delete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            firstName = itemView.findViewById(R.id.employee_account_list_detail_first_name_textView);
            lastName = itemView.findViewById(R.id.employee_account_list_detail_last_name_textView);
            email = itemView.findViewById(R.id.employee_account_list_detail_email_textView);
            address = itemView.findViewById(R.id.employee_account_list_detail_customer_address_textView);
            phoneNumber = itemView.findViewById(R.id.employee_account_list_detail_mobile_phone_number_textView);
            accountType = itemView.findViewById(R.id.employee_account_list_detail_account_type_textView);
            call = itemView.findViewById(R.id.employee_account_list_detail_call_textView);
            edit = itemView.findViewById(R.id.employee_account_list_detail_edit_textView);
            delete = itemView.findViewById(R.id.employee_account_list_detail_delete_textView);

        }
    }
}

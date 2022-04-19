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

public class ViewCustomerOrdersAdapter extends RecyclerView.Adapter<ViewCustomerOrdersAdapter.ViewHolder> implements Filterable {
    ArrayList<ViewCustomerOrdersData> list;
    ArrayList<ViewCustomerOrdersData> listCopy;

    public ViewCustomerOrdersAdapter(ArrayList<ViewCustomerOrdersData> list) {
        this.list = list;
        this.listCopy = list;
    }


    @NonNull
    @Override
    public ViewCustomerOrdersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_customer_orders_list_details, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewCustomerOrdersAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.ordersId.setText(list.get(position).ordersId);
        holder.foodName.setText(list.get(position).foodName);
        holder.foodQuantity.setText(list.get(position).foodQuantity);
        holder.foodPrice.setText(list.get(position).foodPrice);
        holder.customerFirstName.setText(list.get(position).customerFirstName);
        holder.customerLastName.setText(list.get(position).customerLastName);
        holder.customerPhoneNumber.setText(list.get(position).customerPhoneNumber);
        holder.customerAddress.setText(list.get(position).customerAddress);
        holder.email.setText(list.get(position).email);
        holder.dateOrdered.setText(list.get(position).dateOrdered);

        holder.callItem.setVisibility(View.GONE);
        holder.deleteItem.setVisibility(View.GONE);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (holder.deleteItem.getVisibility() == View.GONE && holder.callItem.getVisibility() == View.GONE) {

                    holder.callItem.setVisibility(View.VISIBLE);
                    holder.deleteItem.setVisibility(View.VISIBLE);

                } else {

                    holder.callItem.setVisibility(View.GONE);
                    holder.deleteItem.setVisibility(View.GONE);
                }

            }
        });

        holder.callItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                builder.setMessage("Do you want to call this customer ?");
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
                        callNumber.setData(Uri.parse("tel: " + list.get(position).customerPhoneNumber));
                        holder.itemView.getContext().startActivity(callNumber);

                    }

                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });

        holder.deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                builder.setMessage("Do you want to delete this item ?");
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
                        progressDialog.setMessage("Deleting order...");
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                        StringRequest stringRequest = new StringRequest(Request.Method.POST, UrlLinks.UrlDeleteCustomerOrder, new Response.Listener<String>() {
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
                                params.put("order_Id", list.get(position).ordersId);
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

                    ArrayList<ViewCustomerOrdersData> searchResults = new ArrayList<>();

                    for (ViewCustomerOrdersData data: listCopy) {

                        if(data.ordersId.toLowerCase().contains(searchText) ||
                                data.foodName.toLowerCase().contains(searchText) ||
                                data.foodQuantity.toLowerCase().contains(searchText) ||
                                data.foodPrice.toLowerCase().contains(searchText) ||
                                data.customerFirstName.toLowerCase().contains(searchText) ||
                                data.customerLastName.toLowerCase().contains(searchText) ||
                                data.customerPhoneNumber.toLowerCase().contains(searchText) ||
                                data.customerAddress.toLowerCase().contains(searchText)){

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

                list = (ArrayList<ViewCustomerOrdersData>) results.values;
                notifyDataSetChanged();
            }
        };
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView ordersId, foodName, foodQuantity, foodPrice, customerFirstName, customerLastName,
                customerAddress, customerPhoneNumber, email, payment_method, deleteItem, callItem, dateOrdered;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ordersId = itemView.findViewById(R.id.view_customer_order_list_detail_order_id_textView);
            foodName = itemView.findViewById(R.id.view_customer_order_list_detail_name_textView);
            foodQuantity = itemView.findViewById(R.id.view_customer_order_list_detail_quantity_textView);
            foodPrice = itemView.findViewById(R.id.view_customer_order_list_detail_price_textView);
            customerFirstName = itemView.findViewById(R.id.view_customer_order_list_detail_customer_first_name_textView);
            customerLastName = itemView.findViewById(R.id.view_customer_order_list_detail_customer_last_name_textView);
            customerAddress = itemView.findViewById(R.id.view_customer_order_list_detail_customer_address_textView);
            customerPhoneNumber = itemView.findViewById(R.id.view_customer_order_list_detail_phone_number_textView);
            email = itemView.findViewById(R.id.view_customer_order_list_detail_email_textView);
            dateOrdered = itemView.findViewById(R.id.view_customer_order_list_detail_date_ordered_textView);
            callItem = itemView.findViewById(R.id.view_customer_orders_list_detail_call_textView);
            deleteItem = itemView.findViewById(R.id.view_customer_orders_list_detail_delete_textView);

        }
    }
}

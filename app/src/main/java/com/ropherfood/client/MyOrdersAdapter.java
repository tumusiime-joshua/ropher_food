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

public class MyOrdersAdapter  extends RecyclerView.Adapter<MyOrdersAdapter.ViewHolder> implements Filterable {

    ArrayList<MyOrdersData> list;
    ArrayList<MyOrdersData> listCopy;

    public MyOrdersAdapter(ArrayList<MyOrdersData> list) {
        this.list = list;
        this.listCopy = list;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_orders_list_details, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.orderId.setText(list.get(position).orderId);
        holder.foodItemName.setText(list.get(position).foodItemName);
        holder.foodItemQuantity.setText(list.get(position).foodItemQuantity);
        holder.foodItemPrice.setText(list.get(position).foodItemPrice);
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

        holder.orderStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                builder.setMessage("Do you want see the order status ?");
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

                        Intent orderTrackingIntent = new Intent(holder.itemView.getContext(), OrderTrackingActivity.class);
                        orderTrackingIntent.putExtra("id", list.get(position).orderId);
                        holder.itemView.getContext().startActivity(orderTrackingIntent);

                    }

                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });

        holder.callItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
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
                builder.setMessage("Do you want to delete this order ?");
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
                                params.put("order_Id", list.get(position).orderId);
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

                    ArrayList<MyOrdersData> searchResults = new ArrayList<>();

                    for (MyOrdersData data: listCopy) {

                        if(data.orderId.toLowerCase().contains(searchText) ||
                                data.foodItemName.toLowerCase().contains(searchText) ||
                                data.foodItemQuantity.toLowerCase().contains(searchText) ||
                                data.foodItemPrice.toLowerCase().contains(searchText)){

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

                list = (ArrayList<MyOrdersData>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderId, foodItemName, foodItemQuantity, foodItemPrice, dateOrdered, orderStatus, callItem, deleteItem;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            orderId = itemView.findViewById(R.id.my_order_list_detail_order_id_textView);
            foodItemName = itemView.findViewById(R.id.my_order_list_detail_name_textView);
            foodItemQuantity = itemView.findViewById(R.id.my_order_list_detail_quantity_textView);
            foodItemPrice = itemView.findViewById(R.id.my_order_list_detail_price_textView);
            dateOrdered = itemView.findViewById(R.id.my_order_list_detail_date_ordered_textView);
            orderStatus = itemView.findViewById(R.id.my_orders_list_detail_order_status_textView);
            callItem = itemView.findViewById(R.id.my_orders_list_detail_call_textView);
            deleteItem = itemView.findViewById(R.id.my_orders_list_detail_delete_textView);
        }
    }
}

package com.ropherfood.client;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FinalOrdersListAdapter extends RecyclerView.Adapter<FinalOrdersListAdapter.ViewHolder> implements Filterable {

    ArrayList<FinalOrdersData> list;
    ArrayList<FinalOrdersData> listCopy;

    public FinalOrdersListAdapter(ArrayList<FinalOrdersData> list) {
        this.list = list;
        this.listCopy = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_final_order_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.order_number.setText(list.get(position).order_number);
        holder.name.setText(list.get(position).name);
        holder.email.setText(list.get(position).email);
        holder.phone.setText(list.get(position).phone);
        holder.address.setText(list.get(position).address);
        holder.total_orders.setText(list.get(position).total_orders);
        holder.total_items.setText(list.get(position).total_items);
        holder.total_amount.setText(list.get(position).total_amount);
        holder.payment_method.setText(list.get(position).payment_method);
        holder.date.setText(list.get(position).date);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                builder.setMessage("Do you want to view order details ?");
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

                        Intent intent = new Intent(holder.itemView.getContext(), OrderedItemsListActivity.class);

                        intent.putExtra("phone", list.get(position).phone);
                        intent.putExtra("order_number", list.get(position).order_number);

                        holder.itemView.getContext().startActivity(intent);

                    }

                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });

        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                builder.setMessage("Do you want to call?");
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
                        callNumber.setData(Uri.parse("tel: " + list.get(position).phone));
                        holder.itemView.getContext().startActivity(callNumber);

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
                builder.setMessage("Do you want to delete this item ?");
                builder.setCancelable(false);

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        StringRequest stringRequest = new StringRequest(Request.Method.POST, UrlLinks.delete_final_orders, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String success = jsonObject.getString("success");
                                    String message = jsonObject.getString("message");

                                    if(success.equals("1")){

                                        Toast.makeText(holder.itemView.getContext(), message, Toast.LENGTH_LONG).show();

                                    }else if(success.equals("0")){

                                        Toast.makeText(holder.itemView.getContext(), message, Toast.LENGTH_LONG).show();

                                    }

                                } catch (JSONException e) {

                                    e.printStackTrace();
                                    Log.e("TAG", response);
                                    Toast.makeText(holder.itemView.getContext(), "System error occurred.", Toast.LENGTH_LONG).show();
                                }

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                Log.e("TAG", error.getMessage());
                                Toast.makeText(holder.itemView.getContext(), "Failed to connect to internet.", Toast.LENGTH_LONG).show();

                            }
                        }){

                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {

                                Map<String, String> params = new HashMap<>();
                                params.put("order_number", list.get(position).order_number);
                                return params;
                            }
                        };

                        RequestQueue requestQueue = Volley.newRequestQueue(holder.itemView.getContext());
                        requestQueue.add(stringRequest);

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
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView order_number, name, email, phone, address, total_items, total_orders, total_amount, payment_method, date, call, delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            order_number = itemView.findViewById(R.id.view_final_order_list_item_order_number);
            name = itemView.findViewById(R.id.view_final_order_list_item_name);
            email = itemView.findViewById(R.id.view_final_order_list_item_email);
            phone = itemView.findViewById(R.id.view_final_order_list_item_phone_number);
            address = itemView.findViewById(R.id.view_final_order_list_item_address);
            total_orders = itemView.findViewById(R.id.view_final_order_list_item_total_orders);
            total_amount = itemView.findViewById(R.id.view_final_order_list_item_total_cost);
            total_items = itemView.findViewById(R.id.view_final_order_list_item_total_items);
            payment_method = itemView.findViewById(R.id.view_final_order_list_item_payment_method);
            date = itemView.findViewById(R.id.view_final_order_list_item_date);
            call = itemView.findViewById(R.id.view_final_order_list_item_call);
            delete = itemView.findViewById(R.id.view_final_order_list_item_delete);
        }
    }
}

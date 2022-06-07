package com.ropherfood.client;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

public class MarketsListAdapter extends RecyclerView.Adapter<MarketsListAdapter.ViewHolder> implements Filterable {

    ArrayList<MarketsData> list;
    ArrayList<MarketsData> listCopy;

    public MarketsListAdapter(ArrayList<MarketsData> list) {
        this.list = list;
        this.listCopy = list;
    }

    @NonNull
    @Override
    public MarketsListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_item_list_details, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MarketsListAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.singleItemName.setText(list.get(position).marketName);

        holder.deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                builder.setMessage("Do you want to delete this item ?");
                builder.setCancelable(false);

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        StringRequest stringRequest = new StringRequest(Request.Method.POST, UrlLinks.UrlDeleteMarkets, new Response.Listener<String>() {
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
                                params.put("market_Id", list.get(position).marketId);
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
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                String searchText = constraint.toString();
                if (searchText.isEmpty()) {

                    list = listCopy;
                } else {

                    ArrayList<MarketsData> searchedData = new ArrayList<>();

                    for (MarketsData data : listCopy) {

                        if (data.marketName.toLowerCase().contains(searchText)) {

                            searchedData.add(data);
                        }
                    }

                    list = searchedData;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = list;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                list = (ArrayList<MarketsData>) results.values;
                notifyDataSetChanged();

            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView singleItemName, deleteItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            singleItemName = itemView.findViewById(R.id.single_items_list_detail_name_textView);
            deleteItem = itemView.findViewById(R.id.single_items_list_detail_delete_textView);

        }
    }
}

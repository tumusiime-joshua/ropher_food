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
import android.widget.ImageView;
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

public class FoodItemsListAdapter extends RecyclerView.Adapter<FoodItemsListAdapter.ViewHolder> implements Filterable {

    ArrayList<FoodItemsData> list;
    ArrayList<FoodItemsData> listCopy;

    public FoodItemsListAdapter(ArrayList<FoodItemsData> list) {
        this.list = list;
        this.listCopy = list;
    }

            @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item_list_details, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.foodName.setText(list.get(position).name);
        holder.foodPrice.setText(list.get(position).price);
        holder.foodCategory.setText(list.get(position).category);
        holder.foodMarket.setText(list.get(position).market);

        if(list.get(position).imageAddress.isEmpty()){

            holder.imageView.setImageResource(R.drawable.ic_image);

        }else {

            Picasso.get().load(list.get(position).imageAddress).into(holder.imageView);
        }


        holder.active.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                builder.setMessage("Do you want to activate this item ?");
                builder.setCancelable(false);

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        StringRequest stringRequest = new StringRequest(Request.Method.POST, UrlLinks.update_food_item_status, new Response.Listener<String>() {
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
                                params.put("food_item_id", list.get(position).id);
                                params.put("status", "Activated");
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

        holder.de_activate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                builder.setMessage("Do you want to de-activate item " +list.get(position).id+ " ?");
                builder.setCancelable(false);

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        StringRequest stringRequest = new StringRequest(Request.Method.POST, UrlLinks.update_food_item_status, new Response.Listener<String>() {
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
                                    Toast.makeText(holder.itemView.getContext(), "System error occurred." + response, Toast.LENGTH_LONG).show();
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
                                params.put("food_item_id", list.get(position).id);
                                params.put("status", "De-Activated");
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

        holder.editItem.setVisibility(View.GONE);
        holder.deleteItem.setVisibility(View.GONE);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(holder.editItem.getVisibility() == View.GONE && holder.deleteItem.getVisibility() == View.GONE){

                    holder.editItem.setVisibility(View.VISIBLE);
                    holder.deleteItem.setVisibility(View.VISIBLE);

                    if(list.get(position).status.toString().equals("Activated")){
                        holder.de_activate.setVisibility(View.VISIBLE);
                        holder.active.setVisibility(View.GONE);
                    }

                    if(list.get(position).status.toString().equals("De-Activated")){
                        holder.active.setVisibility(View.VISIBLE);
                        holder.de_activate.setVisibility(View.GONE);
                    }

                }else {

                    holder.editItem.setVisibility(View.GONE);
                    holder.deleteItem.setVisibility(View.GONE);

                    if(list.get(position).status.toString().equals("Activated")){
                        holder.de_activate.setVisibility(View.GONE);
                    }

                    if(list.get(position).status.toString().equals("De-Activated")){
                        holder.active.setVisibility(View.GONE);
                    }

                }

            }
        });

        holder.editItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                builder.setMessage("Do you want to edit this item ?");
                builder.setCancelable(false);

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(holder.itemView.getContext(), AddFoodItemActivity.class);

                        intent.putExtra("id", list.get(position).id);
                        intent.putExtra("name", list.get(position).name);
                        intent.putExtra("price", list.get(position).price);
                        intent.putExtra("description", list.get(position).description);
                        intent.putExtra("image_path", list.get(position).imageAddress);
                        intent.putExtra("current_image_path", list.get(position).imagePath);

                        holder.itemView.getContext().startActivity(intent);

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

        holder.deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                builder.setMessage("Do you want to delete this item ?");
                builder.setCancelable(false);

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        StringRequest stringRequest = new StringRequest(Request.Method.POST, UrlLinks.UrlDeleteFoodItemsFromDatabase, new Response.Listener<String>() {
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
                                params.put("food_item_Id", list.get(position).id);
                                params.put("food_item_image_path", list.get(position).imagePath);
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

                    ArrayList<FoodItemsData> searchedData = new ArrayList<>();

                    for (FoodItemsData data : listCopy) {

                        if (data.name.toLowerCase().contains(searchText) || data.price.toLowerCase().contains(searchText)) {

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

                list = (ArrayList<FoodItemsData>) results.values;
                notifyDataSetChanged();

            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView foodName, foodPrice, foodCategory, foodMarket, editItem, deleteItem, active, de_activate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.food_item_list_detail_imageView);
            foodName = itemView.findViewById(R.id.food_item_list_detail_name_textView);
            foodPrice = itemView.findViewById(R.id.food_item_list_detail_price_textView);
            foodCategory = itemView.findViewById(R.id.food_item_list_category_textView);
            foodMarket = itemView.findViewById(R.id.food_item_list_market_textView);
            editItem = itemView.findViewById(R.id.food_items_list_detail_edit_textView);
            deleteItem = itemView.findViewById(R.id.food_items_list_detail_delete_textView);
            active = itemView.findViewById(R.id.food_items_list_detail_active_textView);
            de_activate = itemView.findViewById(R.id.food_items_list_detail_de_active_textView);
        }
    }
}

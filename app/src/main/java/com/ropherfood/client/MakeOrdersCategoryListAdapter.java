package com.ropherfood.client;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MakeOrdersCategoryListAdapter extends RecyclerView.Adapter<MakeOrdersCategoryListAdapter.ViewHolder> implements Filterable {

    ArrayList<CategoryData> list;
    ArrayList<CategoryData> listCopy;

    public MakeOrdersCategoryListAdapter(ArrayList<CategoryData> list) {
        this.list = list;
        this.listCopy = list;
    }

    @NonNull
    @Override
    public MakeOrdersCategoryListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_orders_category_list_details, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MakeOrdersCategoryListAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.category.setText(list.get(position).category);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(holder.itemView.getContext(), CategoryItemsActivity.class);

                intent.putExtra("category", list.get(position).category);

                holder.itemView.getContext().startActivity(intent);
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

        TextView category;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            category = itemView.findViewById(R.id.view_orders_category_list_details_textView);

        }
    }
}

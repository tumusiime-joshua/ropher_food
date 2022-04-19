package com.ropherfood.client;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MakeOrderAdapter extends RecyclerView.Adapter<MakeOrderAdapter.ViewHolder> implements Filterable {

    ArrayList<MakeOrdersData> list;
    ArrayList<MakeOrdersData> listCopy;

    public MakeOrderAdapter(ArrayList<MakeOrdersData> list) {
        this.list = list;
        this.listCopy = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.make_order_list_details, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.foodItemName.setText(list.get(position).name);
        holder.foodItemPrice.setText(list.get(position).price);

        Picasso.get().load(list.get(position).imagePath).into(holder.imageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent itemDetail = new Intent(holder.itemView.getContext(), MakeOrderItemDetailsActivity.class);

                itemDetail.putExtra("food_name", list.get(position).name);
                itemDetail.putExtra("food_price", list.get(position).price);
                itemDetail.putExtra("food_description", list.get(position).description);
                itemDetail.putExtra("image_path", list.get(position).imagePath);

                holder.itemView.getContext().startActivity(itemDetail);
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

                    ArrayList<MakeOrdersData> searchResults = new ArrayList<>();

                    for (MakeOrdersData data: listCopy) {

                        if(data.name.toLowerCase().contains(searchText) ||
                                data.price.toLowerCase().contains(searchText)){

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

                list = (ArrayList<MakeOrdersData>) results.values;
                notifyDataSetChanged();
            }
        };
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView foodItemName, foodItemPrice;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            foodItemName = itemView.findViewById(R.id.make_order_list_detail_name_textView);
            foodItemPrice = itemView.findViewById(R.id.make_order_list_detail_price_textView);
            imageView = itemView.findViewById(R.id.make_order_list_detail_imageView);

        }
    }
}

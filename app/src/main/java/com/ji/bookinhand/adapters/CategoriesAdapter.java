package com.ji.bookinhand.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ji.bookinhand.R;
import com.ji.bookinhand.ui.ResultsActivity;

import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoriesAdapterViewHolder> {

    Context mContext;
    Double rating;
    List<String> categories;

    public CategoriesAdapter(Context mContext, List<String> categories, Double rating) {
        this.mContext = mContext;
        this.categories = categories;
        this.rating = rating;
    }

    @Override
    public CategoriesAdapter.CategoriesAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = R.layout.category_item;
        View view = LayoutInflater.from(mContext).inflate(layoutId, parent, false);
        CategoriesAdapterViewHolder holder = new CategoriesAdapterViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(CategoriesAdapter.CategoriesAdapterViewHolder holder, int position) {
        String name = categories.get(position);
        if (name != null && name.length() > 1)
            holder.name.setText(name);
        else holder.itemView.setVisibility(View.GONE);

    }


    @Override
    public int getItemCount() {
        return categories == null ? 0 : categories.size();
    } //the 1 is for the rating fab

    public class CategoriesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name;

        public CategoriesAdapterViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.category_name);
            name.setOnClickListener(this);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            mContext.startActivity(new Intent(mContext, ResultsActivity.class)
                    .putExtra("result", categories.get(position))
                    .putExtra("isCat", true));
        }
    }
}

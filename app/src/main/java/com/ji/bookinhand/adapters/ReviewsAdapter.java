package com.ji.bookinhand.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ji.bookinhand.R;
import com.ji.bookinhand.ui.ResultsActivity;

import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsAdapterViewHolder> {

    Context mContext;
    Double rating;
    List<String> categories;

    public ReviewsAdapter(Context mContext, List<String> categories, Double rating) {
        this.mContext = mContext;
        this.categories = categories;
        this.rating = rating;
    }

    @Override
    public ReviewsAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = R.layout.category_item;
        View view = LayoutInflater.from(mContext).inflate(layoutId, parent, false);
        ReviewsAdapterViewHolder holder = new ReviewsAdapterViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ReviewsAdapterViewHolder holder, int position) {

        String name = categories.get(position);
        holder.name.setText(name);

    }


    @Override
    public int getItemCount() {
        return categories == null ? 0 : categories.size();
    } //the 1 is for the rating fab

    public class ReviewsAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name;
        FloatingActionButton fabc;

        public ReviewsAdapterViewHolder(View itemView) {
            super(itemView);
            fabc = itemView.findViewById(R.id.fabc);
            name = itemView.findViewById(R.id.category_name);
            fabc.setOnClickListener(this);
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

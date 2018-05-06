package com.ji.bookinhand.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ji.bookinhand.R;
import com.ji.bookinhand.api.models.Review;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsAdapterViewHolder> {

    Context mContext;
    Review reviews;

    public ReviewsAdapter(Context mContext, Review reviews) {
        this.mContext = mContext;
        this.reviews = reviews;
    }

    @Override
    public ReviewsAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = R.layout.review_item;
        View view = LayoutInflater.from(mContext).inflate(layoutId, parent, false);
        ReviewsAdapterViewHolder holder = new ReviewsAdapterViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ReviewsAdapterViewHolder holder, int position) {
        String summary = reviews.getResults().get(position).getSummary();
        String author = reviews.getResults().get(position).getByline();
        if (summary.length() < 1 || author.length() < 1)
            holder.itemView.setVisibility(View.GONE);
        holder.review_text.setText(summary);
        holder.name.setText(author);
    }


    @Override
    public int getItemCount() {
        if (reviews != null && reviews.getNumResults() > 3) return 3;
        return reviews == null ? 0 : reviews.getResults().size();
    } //the 1 is for the rating fab

    public class ReviewsAdapterViewHolder extends RecyclerView.ViewHolder {
        TextView name, review_text;

        public ReviewsAdapterViewHolder(View itemView) {
            super(itemView);
            review_text = itemView.findViewById(R.id.review_text);
            name = itemView.findViewById(R.id.author);

        }

    }
}

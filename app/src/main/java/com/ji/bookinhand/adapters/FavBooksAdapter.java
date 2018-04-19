package com.ji.bookinhand.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.intrusoft.sectionedrecyclerview.SectionRecyclerViewAdapter;
import com.ji.bookinhand.R;
import com.ji.bookinhand.api.models.ImageLinks;
import com.ji.bookinhand.api.models.Item;
import com.ji.bookinhand.ui.fragments.FavouritesFragment;

import java.util.List;
/**
 *
 * WORKS ONLY IF A VERTICAL LINEAR LAYOUT MANAGER IS USED
 *
 * */

public class FavBooksAdapter extends SectionRecyclerViewAdapter<FavouritesFragment.SectionHeader, Item, FavBooksAdapter.SectionViewHolder, FavBooksAdapter.MyItemViewHolder> {
    private static final String TAG = FavBooksAdapter.class.getSimpleName();
    List<FavouritesFragment.SectionHeader> mSectionsDataList;
    Context mContext;

    public FavBooksAdapter(Context context, List<FavouritesFragment.SectionHeader> mSectionsDataList) {
        super(context, mSectionsDataList);
        this.mContext = context;
        this.mSectionsDataList = mSectionsDataList;
    }


    @Override
    public SectionViewHolder onCreateSectionViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_section, viewGroup, false);
        return new SectionViewHolder(view);
    }

    @Override
    public MyItemViewHolder onCreateChildViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_book_list, viewGroup, false);
        return new MyItemViewHolder(view);
    }

    @Override
    public void onBindSectionViewHolder(SectionViewHolder sectionViewHolder, int sectionPosition, FavouritesFragment.SectionHeader sectionHeader) {
        String sectionName = mSectionsDataList.get(sectionPosition).getSectionText();
        Log.d(TAG, "Section name is: "+sectionName);
        sectionViewHolder.name.setText(sectionName);
    }

    @Override
    public void onBindChildViewHolder(MyItemViewHolder holder, int sectionPosition, int childPosition, Item item) {
        String title = item.getVolumeInfo().getTitle();
        List<String> author = item.getVolumeInfo().getAuthors();
        Double rating = item.getVolumeInfo().getAverageRating();
        ImageLinks image =item.getVolumeInfo().getImageLinks();
        holder.title.setText(title);
        if (author != null)
            if (author.size() == 1)
                holder.author.setText(author.get(0));
            else if (author.size() > 1)
                holder.author.setText(author.get(0) + "...");
        if (rating != null && rating != 0.0)
            holder.rating.setText("Rating " + rating);
        else holder.rating.setText("N/A");
        if (image != null)
            if (image.getExtraLarge() != null)
                Glide.with(mContext)
                        .load(image.getExtraLarge())
                        .into(holder.thumbnail);
            else if (image.getLarge() != null)
                Glide.with(mContext)
                        .load(image.getLarge())
                        .into(holder.thumbnail);
            else if (image.getMedium() != null)
                Glide.with(mContext)
                        .load(image.getMedium())
                        .into(holder.thumbnail);
            else if (image.getThumbnail() != null)
                Glide.with(mContext)
                        .load(image.getThumbnail())
                        .into(holder.thumbnail);


    }

    class MyItemViewHolder extends RecyclerView.ViewHolder {
        TextView title, author, rating;
        ImageView thumbnail;

        public MyItemViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            author = view.findViewById(R.id.author);
            rating = view.findViewById(R.id.rating);
            thumbnail = view.findViewById(R.id.thumbnail);
        }
    }

    class SectionViewHolder extends RecyclerView.ViewHolder {

        TextView name;

        public SectionViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.section_text);
        }
    }

}
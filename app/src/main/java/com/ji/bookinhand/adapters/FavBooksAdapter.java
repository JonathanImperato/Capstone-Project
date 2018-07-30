package com.ji.bookinhand.adapters;

/**
 * WORKS ONLY IF A VERTICAL LINEAR LAYOUT MANAGER IS USED
 * CURRENTLY NOT USED
 */
/*
public class FavBooksAdapter extends SectionRecyclerViewAdapter<FavouritesFragment.SectionHeader, Item, FavBooksAdapter.SectionViewHolder, FavBooksAdapter.MyItemViewHolder> {
    private static final String TAG = FavBooksAdapter.class.getSimpleName();
    List<FavouritesFragment.SectionHeader> mSectionsDataList;
    Context mContext;
    List<Item> mFavList;

    public FavBooksAdapter(Context context, List<FavouritesFragment.SectionHeader> mSectionsDataList, List<Item> mFavList) {
        super(context, mSectionsDataList);
        this.mContext = context;
        this.mSectionsDataList = mSectionsDataList;
        this.mFavList = mFavList;
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
        Log.d(TAG, "Section name is: " + sectionName);
        sectionViewHolder.name.setText(sectionName);
    }

    @Override
    public void onBindChildViewHolder(MyItemViewHolder holder, int sectionPosition, int childPosition, Item item) {
        String title = item.getVolumeInfo().getTitle();
        List<String> author = item.getVolumeInfo().getAuthors();
        Double rating = item.getVolumeInfo().getAverageRating();
        ImageLinks image = item.getVolumeInfo().getImageLinks();
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

    int getSectionPositionFor(Item item) {
        for (int i = 0; i < mSectionsDataList.size(); i++) {
            if (mSectionsDataList.get(i).getChildItems().contains(item))
                return i;
        }
        return -1;
    }

    int getItemPositionBy(String title) {
        for (int i = 0; i < mSectionsDataList.size(); i++) {
            Item item = getItemBy(title);
            if (mSectionsDataList.get(i).getChildItems().contains(item))
                return mSectionsDataList.get(i).getChildItems().indexOf(item);
        }
        return -1;
    }

    Item getItemBy(String title){
        for (Item item1 : mFavList){
            if (item1.getVolumeInfo().getTitle().equals(title))
                return item1;
        }
        return null;
    }

    class MyItemViewHolder extends RecyclerView.ViewHolder {
        TextView title, author, rating;
        ImageView thumbnail, menu;

        public MyItemViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            author = view.findViewById(R.id.author);
            rating = view.findViewById(R.id.rating);
            thumbnail = view.findViewById(R.id.thumbnail);
            menu = view.findViewById(R.id.more_fav);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int position = getItemPositionBy(title.getText().toString());
                    final int sectionPosition = getSectionPositionFor(mFavList.get(position));
                    Toast.makeText(mContext, "Item pos is " + position + " and section pos is " + sectionPosition, Toast.LENGTH_SHORT).show();
            /*
            final PopupMenu popup = new PopupMenu(mContext, view);
            MenuInflater inflater = popup.getMenuInflater();
            Intent intent = new Intent(mContext, BookDetailActivity.class);
            // Get the transition name from the string
            String transitionName = mContext.getString(R.string.transition_string);
            ActivityOptionsCompat options =
                    ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContext,
                            view,   // Starting view
                            transitionName    // The String
                    );

            switch (view.getId()) {
                case R.id.more_fav:
                    inflater.inflate(R.menu.actions_fav, popup.getMenu());
                    popup.show();
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            final String bookTitle = mFavList.get(position).getVolumeInfo().getTitle();
                            final boolean[] hasRestored = new boolean[1];
                            removeChild(sectionPosition, position);
                            final Item justRemovedItem = mFavList.get(position);
                            mFavList.remove(position);
                            Snackbar snack = Snackbar.make(view, bookTitle + " has been removed from favourites", Snackbar.LENGTH_LONG).setAction("Cancel", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (position != -1) {
                                        //       addFavourite(position);
                                        mFavList.add(position, justRemovedItem);
                                        //        notifyItemRangeInserted(position, mFavList.size());
                                        insertNewChild(mFavList.get(position), sectionPosition);
                                        hasRestored[0] = true;
                                    }
                                }
                            });
                            snack.show();
                            snack.addCallback(new Snackbar.Callback() {
                                @Override
                                public void onDismissed(Snackbar snackbar, int event) {
                                    if (!snackbar.isShown() && !hasRestored[0])
                                        removeFromFav(bookTitle);

                                }
                            });
                            return true;
                        }
                    });
                    break;
                case R.id.book_item:
                    intent.putExtra("volume", mFavList.get(position).getVolumeInfo());
                    intent.putExtra("isFav", true);
                    intent.putExtra("imgs", mFavList.get(position).getVolumeInfo().getImageLinks());
                    ActivityCompat.startActivity(mContext, intent, options.toBundle());
                    break;
                case R.id.thumbnail:
                    intent.putExtra("volume", mFavList.get(position).getVolumeInfo());
                    intent.putExtra("isFav", true);
                    intent.putExtra("imgs", mFavList.get(position).getVolumeInfo().getImageLinks());
                    ActivityCompat.startActivity(mContext, intent, options.toBundle());
                    break;
            }
            *//*
                }
            });
        }


        void removeFromFav(String title) {
            mContext.getContentResolver().delete(
                    BASE_CONTENT_URI,
                    COLUMN_TITLE + " =? ",
                    new String[]{title}
            );
        }
    }

    class SectionViewHolder extends RecyclerView.ViewHolder {

        TextView name;

        public SectionViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.section_text);
        }
    }

}*/
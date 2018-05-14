package com.ji.bookinhand.widgets;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.ji.bookinhand.R;
import com.ji.bookinhand.api.models.ImageLinks;
import com.ji.bookinhand.api.models.VolumeInfo;
import com.ji.bookinhand.database.ItemsContract;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import static com.ji.bookinhand.database.ItemsContract.BASE_CONTENT_URI;

public class BookGridService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new GridRemoteViewsFactory(this.getApplicationContext());
    }

}

class GridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    Context mContext;
    Cursor mCursor;

    public GridRemoteViewsFactory(Context applicationContext) {
        mContext = applicationContext;

    }

    @Override
    public void onCreate() {
        if (mCursor != null) mCursor.close();
        mCursor = mContext.getContentResolver().query(
                BASE_CONTENT_URI,
                null,
                null,
                null,
                null
        );
    }

    //called on start and when notifyAppWidgetViewDataChanged is called
    @Override
    public void onDataSetChanged() {
        if (mCursor != null) mCursor.close();
        mCursor = mContext.getContentResolver().query(
                BASE_CONTENT_URI,
                null,
                null,
                null,
                null
        );
        mCursor.moveToFirst();

    }

    @Override
    public void onDestroy() {
        mCursor.close();
    }

    @Override
    public int getCount() {
        if (mCursor == null) return 0;
        return mCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (mCursor == null || mCursor.getCount() == 0) return null;
        mCursor.moveToPosition(position);

        int titleIndex = mCursor.getColumnIndex(ItemsContract.BookEntry.COLUMN_TITLE);
        int authorsIndex = mCursor.getColumnIndex(ItemsContract.BookEntry.COLUMN_AUTHORS);
        int ratingIndex = mCursor.getColumnIndex(ItemsContract.BookEntry.COLUMN_AVERAGE_RATING);
        int catIndex = mCursor.getColumnIndex(ItemsContract.BookEntry.COLUMN_CATEGORIES);
        int descrIndex = mCursor.getColumnIndex(ItemsContract.BookEntry.COLUMN_DESCRIPTION);
        int imagesIndex = mCursor.getColumnIndex(ItemsContract.BookEntry.COLUMN_IMAGE_LINKS);
        int infoIndex = mCursor.getColumnIndex(ItemsContract.BookEntry.COLUMN_INFO_LINK);
        int pagecountIndex = mCursor.getColumnIndex(ItemsContract.BookEntry.COLUMN_PAGE_COUNT);
        int previewIndex = mCursor.getColumnIndex(ItemsContract.BookEntry.COLUMN_PREVIEW_LINK);

        String title = mCursor.getString(titleIndex);
        String authors = mCursor.getString(authorsIndex);
        Double rating = mCursor.getDouble(ratingIndex);
        String categories = mCursor.getString(catIndex);
        String descr = mCursor.getString(descrIndex);
        final String images = mCursor.getString(imagesIndex);
        String info = mCursor.getString(infoIndex);
        int pages = mCursor.getInt(pagecountIndex);
        String preview = mCursor.getString(previewIndex);

        final RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.books_list);

        //  views.setImageViewResource(R.id.widget_plant_image, imgRes);

        views.setTextViewText(R.id.b_title, title.toString());
        views.setTextViewText(R.id.b_authors, authors.toString());
        //load Posters for books

        try {
            RequestOptions options = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_sync_black_24dp)
                    .error(R.drawable.ic_error_outline_black_24dp);

            Bitmap bitmap = Glide.with(mContext)
                    .applyDefaultRequestOptions(options)
                    .asBitmap()
                    .load(images)
                    .submit()
                    .get();

            views.setImageViewBitmap(R.id.poster, bitmap);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        VolumeInfo createdVolume = new VolumeInfo();
        createdVolume.setTitle(title); //and so on
        if (authors != null)
            createdVolume.setAuthors(Arrays.asList(authors.split(",")));
        createdVolume.setAverageRating(rating);
        if (categories != null)
            createdVolume.setCategories(Arrays.asList(categories.split(",")));
        createdVolume.setDescription(descr);
        ImageLinks imageLinks = new ImageLinks();
        imageLinks.setThumbnail(images);
        createdVolume.setImageLinks(imageLinks);
        createdVolume.setInfoLink(info);
        createdVolume.setPageCount(pages);
        createdVolume.setPreviewLink(preview);

        // Fill in the onClick PendingIntent Template using the specific plant Id for each item individually
        Bundle extras = new Bundle();
        extras.putParcelable("imgs", imageLinks);
        extras.putParcelable("volume", createdVolume);

        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        views.setOnClickFillInIntent(R.id.book_widget_item, fillInIntent);

        return views;

    }


    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1; // Treat all items in the GridView the same
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

}
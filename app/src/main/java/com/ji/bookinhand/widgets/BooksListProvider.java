package com.ji.bookinhand.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.ji.bookinhand.R;
import com.ji.bookinhand.ui.BookDetailActivity;
import com.ji.bookinhand.ui.OcrCaptureActivity;


/**
 * Implementation of App Widget functionality.
 */
public class BooksListProvider extends AppWidgetProvider {


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        // Construct the RemoteViews object
        RemoteViews views = getGridRemoteView(context);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private static RemoteViews getGridRemoteView(Context context) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_grid_view);

        // The empty view is displayed when the collection has no items.
        // It should be in the same layout used to instantiate the RemoteViews
        // object above.
        // Set the GridWidgetService intent to act as the adapter for the GridView
        Intent intent = new Intent(context, BookGridService.class);
        views.setRemoteAdapter(R.id.widget_grid_view, intent);
        views.setEmptyView(R.id.widget_grid_view, R.id.empty_view);

        //refresh button
        Intent intentSync = new Intent(context, BooksListProvider.class);
        intentSync.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intentSync.putExtra("isList", true);
        PendingIntent pendingSync = PendingIntent.getBroadcast(context, 0, intentSync, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.refreshButton, pendingSync);

        // Set the BookDetailActivity intent to launch when clicked
        Intent appIntent = new Intent(context, BookDetailActivity.class);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.widget_grid_view, appPendingIntent);

        //take photo button
        Intent configIntent = new Intent(context, OcrCaptureActivity.class);
        configIntent.putExtra("iswidget", true);
        PendingIntent photoFillIntent = PendingIntent.getActivity(context, 0, configIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.takeWidgetPhoto, photoFillIntent);
        // Handle empty gardens
        //  views.setEmptyView(R.id.widget_grid_view, R.id.empty_view);
        return views;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
            Log.d(this.getClass().getSimpleName(), "Updating Widget with id " + appWidgetId);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null) {
            switch (intent.getAction()) {
                case AppWidgetManager.ACTION_APPWIDGET_UPDATE:
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                    ComponentName thisAppWidget = new ComponentName(context.getPackageName(), BooksListProvider.class.getName());
                    int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
                    onUpdate(context, appWidgetManager, appWidgetIds);
                    if (intent.hasExtra("isList")) {
                        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_grid_view);
                    }
                    break;
            }
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}


package com.ji.bookinhand.ui;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.commit451.elasticdragdismisslayout.ElasticDragDismissFrameLayout;
import com.commit451.elasticdragdismisslayout.ElasticDragDismissLinearLayout;
import com.commit451.elasticdragdismisslayout.ElasticDragDismissListener;
import com.ji.bookinhand.R;
import com.ji.bookinhand.api.models.VolumeInfo;
import com.ji.bookinhand.api.nytmodels.BookDetail;

public class MoreInfoActivity extends AppCompatActivity {
    TextView description, pages, ISBN;
    ElasticDragDismissFrameLayout mDraggableFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info);
        Toolbar toolbar = findViewById(R.id.mtoolbar);
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_close_black_24dp);
        toolbar.setNavigationIcon(drawable);
        setSupportActionBar(toolbar);
        description = findViewById(R.id.descriptionBook);
        pages = findViewById(R.id.pages);
        ISBN = findViewById(R.id.isbn);
        mDraggableFrame = findViewById(R.id.draggable_frame);

        VolumeInfo item = getIntent().getExtras().getParcelable("item");
        BookDetail libro = getIntent().getExtras().getParcelable("libro");
        String data = getIntent().getExtras().getString("isbn");
        if (item != null) {
            description.setText(item.getDescription());
            pages.setText(item.getPageCount() + " " + getString(R.string.pages));
            if (data != null && data.length() > 1)
                ISBN.setText("ISBN " + data);
            else ISBN.setVisibility(View.GONE);
        } else {
            description.setText(libro.getDescription());
            if (data != null && data.length() > 1)
                ISBN.setText("ISBN " + data);
            else ISBN.setVisibility(View.GONE);
        }
        mDraggableFrame.addListener(new ElasticDragDismissListener() {
            @Override
            public void onDrag(float elasticOffset, float elasticOffsetPixels, float rawOffset, float rawOffsetPixels) {
            }

            @Override
            public void onDragDismissed() {
                //if you are targeting 21+ you might want to finish after transition
                if (mDraggableFrame.getTranslationY() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        getWindow().setReturnTransition(
                                TransitionInflater.from(MoreInfoActivity.this)
                                        .inflateTransition(R.transition.about_return_downward));
                    }
                }
                if (Build.VERSION.SDK_INT >= 21) {
                    finishAfterTransition();
                } else {
                    finish();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(0, android.R.anim.fade_out);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) // Press Back Icon
        {
            finish();
            overridePendingTransition(0, android.R.anim.fade_out);
        }
        return super.onOptionsItemSelected(item);
    }
}

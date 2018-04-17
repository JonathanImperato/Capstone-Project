package com.ji.bookinhand.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ji.bookinhand.R;
import com.ji.bookinhand.api.models.VolumeInfo;

public class MoreInfoActivity extends AppCompatActivity {
    TextView description, pages, ISBN;

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

        VolumeInfo item = getIntent().getExtras().getParcelable("item");
        String data = getIntent().getExtras().getString("isbn");
        description.setText(item.getDescription());
        pages.setText(item.getPageCount() + " " + getString(R.string.pages));
        if (data != null && data.length() > 1)
            ISBN.setText("ISBN " + data);
        else ISBN.setVisibility(View.GONE);
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

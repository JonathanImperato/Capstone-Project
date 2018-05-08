package com.ji.bookinhand.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ListView;

import com.franmontiel.attributionpresenter.AttributionPresenter;
import com.franmontiel.attributionpresenter.entities.Attribution;
import com.franmontiel.attributionpresenter.entities.Library;
import com.franmontiel.attributionpresenter.entities.License;
import com.ji.bookinhand.R;

public class LicensesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_licenses);

        ListView list = (ListView) findViewById(R.id.list);
        AttributionPresenter attributionPresenter = new AttributionPresenter.Builder(this)
                .addAttributions(
                        new Attribution.Builder("AttributionPresenter")
                                .addCopyrightNotice("Copyright 2017 Francisco José Montiel Navarro")
                                .addLicense(License.APACHE)
                                .setWebsite("https://github.com/franmontiel/AttributionPresenter")
                                .build()
                )
                .addAttributions(
                        new Attribution.Builder("PersistentSearchView")
                                .addCopyrightNotice("Copyright 2015 Cryse Hillmes")
                                .addLicense(License.APACHE)
                                .setWebsite("https://github.com/crysehillmes/PersistentSearchView")
                                .build()
                )
                .addAttributions(
                        new Attribution.Builder("PersistentSearchView")
                                .addCopyrightNotice("Copyright 2015 Cryse Hillmes")
                                .addLicense(License.APACHE)
                                .setWebsite("https://github.com/crysehillmes/PersistentSearchView")
                                .build()
                )
                .addAttributions(
                        new Attribution.Builder("ElasticDragDismissLayout")
                                .addCopyrightNotice("Copyright 2015 Google, Inc.\n" +
                                        "Copyright 2017 Commit 451")
                                .addLicense(License.APACHE)
                                .setWebsite("https://github.com/Commit451/ElasticDragDismissLayout")
                                .build()
                )
                .addAttributions(
                        new Attribution.Builder("Android Vision API")
                                .addCopyrightNotice("Copyright 2015 Google, Inc")
                                .addLicense(License.APACHE)
                                .setWebsite("https://github.com/googlesamples/android-vision")
                                .build()
                )
                .addAttributions(
                        Library.RETROFIT,
                        Library.GLIDE,
                        Library.GSON)
                .build();
        list.setAdapter(attributionPresenter.getAdapter());
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return false;
    }
}

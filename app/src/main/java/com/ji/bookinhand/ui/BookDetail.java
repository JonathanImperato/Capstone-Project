package com.ji.bookinhand.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ji.bookinhand.R;
import com.ji.bookinhand.api.models.ImageLinks;
import com.ji.bookinhand.api.models.Item;
import com.ji.bookinhand.api.models.VolumeInfo;

public class BookDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        if (getIntent().getExtras() != null) {
            VolumeInfo item = getIntent().getExtras().getParcelable("volume");
            ImageLinks imgs = getIntent().getExtras().getParcelable("imgs");
            if (item != null)
                Toast.makeText(this, "Item title is: " + item.getTitle(), Toast.LENGTH_SHORT).show();
            ImageView img = findViewById(R.id.img);
            if (imgs != null)
            Glide.with(this).load(imgs.getThumbnail()).into(img);
        }
    }
}

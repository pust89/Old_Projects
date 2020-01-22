package com.pustovit.flickrbrowser;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class PhotoDetailActivity extends BaseActivity {
    private ImageView imageView;
    private TextView tvAuthor;
    private TextView tvTitle;
    private TextView tvTags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        activateToolbar(true);

        imageView = findViewById(R.id.card_image);
        tvAuthor = findViewById(R.id.card_author);
        tvTitle = findViewById(R.id.card_title);
        tvTags = findViewById(R.id.card_tags);

        Intent intent = getIntent();

        Photo photo = (Photo) intent.getSerializableExtra(PHOTO_TRANSFER);
        if (photo != null) {

            tvAuthor.setText(photo.getAuthor());

            Resources resources = getResources();

            tvTitle.setText(resources.getString(R.string.photo_title_text, photo.getTitle()));
            tvTags.setText(resources.getString(R.string.photo_tags_text, photo.getTags()));

            Picasso.get().load(photo.getBigImage()).error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder).into(imageView);
        }
    }

}

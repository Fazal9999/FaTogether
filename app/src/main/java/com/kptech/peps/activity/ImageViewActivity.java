package com.kptech.peps.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.jsibbold.zoomage.ZoomageView;
import com.kptech.peps.R;
import com.squareup.picasso.Picasso;

public class ImageViewActivity extends AppBaseActivity {
    ZoomageView myZoomageView;
    ImageView close;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_img_view);
        myZoomageView = findViewById(R.id.myZoomageView);
        close = findViewById(R.id.close);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Picasso.with(this).load(getIntent().getStringExtra("img")).
                into(myZoomageView);

    }
}

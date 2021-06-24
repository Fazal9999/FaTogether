package com.kptech.peps.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;

import android.webkit.WebView;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;
import com.kptech.peps.R;

public class TncView extends AppBaseActivity {
    WebView pdf_web;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_tnc);
        setHeaderView("Terms & conditions");
        TextView done_button=findViewById(R.id.done_button);
        done_button.setOnClickListener(view -> onBackPressed());
        PDFView pdfView=findViewById(R.id.pdf_web);

        Intent intent = getIntent();
        String pdf = intent.getStringExtra("pdf");
        if (pdf.equalsIgnoreCase("login"))
            pdfView.fromAsset("tnc.pdf").load();
        else
            pdfView.fromAsset("Id info.pdf").load();
    }
}

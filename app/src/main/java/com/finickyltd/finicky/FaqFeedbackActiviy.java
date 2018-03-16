package com.finickyltd.finicky;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FaqFeedbackActiviy extends AppCompatActivity implements View.OnClickListener{
    private WebView faqWebview;
    private LinearLayout llGotoMenu, llBackArrow;
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq_feedback);

        TextView tx = (TextView)findViewById(R.id.hometitle);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Sriracha-Regular.ttf");
        tx.setTypeface(custom_font);

        llGotoMenu = (LinearLayout) findViewById(R.id.llGoToMenu);
        llGotoMenu.setOnClickListener(this);

        llBackArrow = (LinearLayout) findViewById(R.id.llBackArrow);
        llBackArrow.setOnClickListener(this);

        // progress dialog
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading Page...");
        dialog.show();

        faqWebview = (WebView) findViewById(R.id.faqWebview);
        faqWebview.getSettings().setJavaScriptEnabled(true);
        faqWebview.getSettings().setBuiltInZoomControls(true);
        faqWebview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        faqWebview.getSettings().setLightTouchEnabled(true);
        faqWebview.getSettings().setSavePassword(true);
        faqWebview.getSettings().setSaveFormData(true);
        faqWebview.setWebViewClient(new MyWebviewClient());
        //faqWebview.loadUrl("http://finicky-app.com/faq/");
        faqWebview.loadUrl("file:///android_asset/www/new_faq.html");
    }

    private class MyWebviewClient extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            dialog.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.llGoToMenu:
                startActivity(new Intent(this, MenuActivity.class));
                break;
            case R.id.llBackArrow:
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

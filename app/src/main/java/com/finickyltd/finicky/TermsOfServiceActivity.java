package com.finickyltd.finicky;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TermsOfServiceActivity extends AppCompatActivity implements View.OnClickListener{
    private LinearLayout llBackArrow, llGotoMenu;
    public GlobalActivity globalActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_of_service);

        TextView tx = (TextView)findViewById(R.id.hometitle);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Sriracha-Regular.ttf");
        tx.setTypeface(custom_font);

        llBackArrow = (LinearLayout) findViewById(R.id.llBackArrow);
        llBackArrow.setOnClickListener(this);

        llGotoMenu = (LinearLayout) findViewById(R.id.llGoToMenu);
        llGotoMenu.setOnClickListener(this);

        WebView wv;
        wv = (WebView) findViewById(R.id.wvTermOfService);
        wv.loadUrl("file:///android_asset/www/TermsOfService.html");

//        AdView mAdView = (AdView) findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);
//
//        globalActivity = (GlobalActivity) getApplicationContext();
//
//        if(!globalActivity.getMembership_type().equals("PRO"))
//            mAdView.setVisibility(View.VISIBLE);
//        else
//            mAdView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.llBackArrow:
                finish();
                break;
            case R.id.llGoToMenu:
                startActivity(new Intent(this, MenuActivity.class));
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

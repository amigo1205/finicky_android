package com.finickyltd.finicky;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class SupportActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout llGotoMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        TextView tx = (TextView)findViewById(R.id.hometitle);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Sriracha-Regular.ttf");
        tx.setTypeface(custom_font);

        llGotoMenu = (LinearLayout) findViewById(R.id.llGoToMenu);
        llGotoMenu.setOnClickListener(this);

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.llGoToMenu:
                startActivity(new Intent(this, MenuActivity.class));
                break;
        }
    }
}

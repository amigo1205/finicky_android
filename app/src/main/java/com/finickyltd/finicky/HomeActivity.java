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

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{
    private LinearLayout llScan, llManualSearch, llGotoMenu;
    public GlobalActivity globalActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        TextView tx = (TextView)findViewById(R.id.hometitle);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Sriracha-Regular.ttf");
        tx.setTypeface(custom_font);

        llScan = (LinearLayout) findViewById(R.id.llScan);
        llManualSearch = (LinearLayout) findViewById(R.id.llManualSearch);
        llGotoMenu = (LinearLayout) findViewById(R.id.llGoToMenu);
        llGotoMenu.setOnClickListener(this);
        llScan.setOnClickListener(this);
        llManualSearch.setOnClickListener(this);

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        globalActivity = (GlobalActivity) getApplicationContext();

        if(globalActivity.getMembership_type().equals("LITE"))
            mAdView.setVisibility(View.VISIBLE);
        else
            mAdView.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.llScan:
                startActivity(new Intent(this, ZBarBarcodeScannerActivity.class));
                return;
            case R.id.llManualSearch:
                startActivity(new Intent(this, ManualSearchActivity.class));
                return;
            case R.id.llGoToMenu:
                startActivity(new Intent(this, MenuActivity.class));
                break;
        }
    }
}

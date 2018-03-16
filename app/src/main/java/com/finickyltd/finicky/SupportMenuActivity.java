package com.finickyltd.finicky;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SupportMenuActivity extends AppCompatActivity implements View.OnClickListener{
    private LinearLayout llFAQ, llTermsOfService, llContactUs, llAboutUs, llGotoMenu, llPrivacy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_support);

        llFAQ = (LinearLayout) findViewById(R.id.llFaqFeedback);
        llPrivacy = (LinearLayout) findViewById(R.id.llPrivacy);
        llTermsOfService = (LinearLayout) findViewById(R.id.llTermsOfService);
        llContactUs = (LinearLayout) findViewById(R.id.llContactUs);
        llAboutUs = (LinearLayout) findViewById(R.id.llAboutUs);
        llGotoMenu = (LinearLayout) findViewById(R.id.llGoToMenu);

        llFAQ.setOnClickListener(this);
        llPrivacy.setOnClickListener(this);
        llTermsOfService.setOnClickListener(this);
        llContactUs.setOnClickListener(this);
        llAboutUs.setOnClickListener(this);
        llGotoMenu.setOnClickListener(this);

        TextView tx = (TextView)findViewById(R.id.hometitle);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Sriracha-Regular.ttf");
        tx.setTypeface(custom_font);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.llFaqFeedback:
                startActivity(new Intent(this, FaqFeedbackActiviy.class));   // web view for further
                //startActivity(new Intent(this, FAQ_Table_Activity.class));
                break;
            case  R.id.llPrivacy:
                startActivity(new Intent(this, PrivacyActivity.class));
                break;
            case R.id.llTermsOfService:
                startActivity(new Intent(this, TermsOfServiceActivity.class));
                break;
            case R.id.llContactUs:
                startActivity(new Intent(this, ContactUs.class));
                break;
            case R.id.llAboutUs:
                startActivity(new Intent(this, AboutUsActivity.class));
                break;
            case R.id.llGoToMenu:
                startActivity(new Intent(this, MenuActivity.class));
                break;
        }
    }
}

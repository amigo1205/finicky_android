package com.finickyltd.finicky;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;

import com.finickyltd.finicky.helper.SharedPrefManager;

public class MenuActivity extends Activity implements OnClickListener{
    private LinearLayout llMenuClose, llExplorePro, llMyAccount, llFilterSetting, llSupport, llSignOut, llGotoHome;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_menu);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);

        TextView tx = (TextView)findViewById(R.id.hometitle);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Sriracha-Regular.ttf");
        tx.setTypeface(custom_font);

        llMenuClose = (LinearLayout) findViewById(R.id.llMenuClose);
        llMenuClose.setOnClickListener(this);

        llExplorePro = (LinearLayout) findViewById(R.id.llexploreProPage);
        llExplorePro.setOnClickListener(this);

        llMyAccount = (LinearLayout) findViewById(R.id.llmy_account);
        llMyAccount.setOnClickListener(this);

        llFilterSetting = (LinearLayout) findViewById(R.id.llFilterSetting);
        llFilterSetting.setOnClickListener(this);

        llSupport = (LinearLayout) findViewById(R.id.llSupport);
        llSupport.setOnClickListener(this);

        llSignOut = (LinearLayout) findViewById(R.id.llSignOut);
        llSignOut.setOnClickListener(this);

        llGotoHome = (LinearLayout) findViewById(R.id.llHome);
        llGotoHome.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.llHome:
                startActivity(new Intent(this, HomeActivity.class));
                return;
            case R.id.llexploreProPage:
                startActivity(new Intent(this, ExploreActivity.class));
                return;
            case R.id.llmy_account:
                startActivity(new Intent(this, MyAccountActivity.class));
                return;
            case R.id.llFilterSetting:
                startActivity(new Intent(this, FilterSettingChangeActivity.class));
                //finish();
                return;
            case R.id.llSupport:
                startActivity(new Intent(this, SupportMenuActivity.class));
                return;
            case R.id.llSignOut:
                showDialog();
                return;
            case R.id.llMenuClose:
                finish();
                return;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void showDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(1);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.sign_out_dialog);
        ((LinearLayout) dialog.findViewById(R.id.llSignOutYes)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // to check if facebook is logged in
                if(AccessToken.getCurrentAccessToken() != null){
                    disconnectFromFacebook();
                }
                SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(getApplicationContext());
                sharedPrefManager.logout();

                MenuActivity.this.startActivity(new Intent(MenuActivity.this, StartActivity.class));
                MenuActivity.this.finish();
                dialog.dismiss();
            }
        });
        ((LinearLayout) dialog.findViewById(R.id.llSignoutNo)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void disconnectFromFacebook() {
        if (AccessToken.getCurrentAccessToken() == null) {
            return; // already logged out
        }

        LoginManager.getInstance().logOut();
//        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
//                .Callback() {
//            @Override
//            public void onCompleted(GraphResponse graphResponse) {
//                LoginManager.getInstance().logOut();
//
//            }
//        }).executeAsync();
    }
}

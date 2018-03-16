package com.finickyltd.finicky;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.finickyltd.finicky.helper.SharedPrefManager;

public class StartActivity extends Activity {
    public GlobalActivity globalActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(getApplicationContext());
        globalActivity = (GlobalActivity) getApplicationContext();
        if(sharedPrefManager.isLoggedIn()){
            String email = sharedPrefManager.getUser().getEmail();
            String membership_type = sharedPrefManager.getUser().getMembership_type();
            globalActivity.setMembership_type(membership_type);
            boolean isRegistered =  sharedPrefManager.readIsFilterSetting();
            if(isRegistered)
                goHomeActivity();
            else
                startActivity(new Intent(this, FilterSettingActivity.class));
        }
    }

    public void goHomeActivity(){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    public  void onNextPage(View view){
        switch (view.getId()){
            case R.id.bLogin:
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.bSignUp:
                startActivity(new Intent(this, SignUpActivity.class));
//                Intent intent = new Intent(this, ProductActivity.class);
//                intent.putExtra("barcodeContent", "0000001724981");
//                startActivity(intent);
                break;
        }
    }
}

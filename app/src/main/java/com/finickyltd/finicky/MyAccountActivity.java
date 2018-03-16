package com.finickyltd.finicky;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.security.MessageDigest;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.finickyltd.finicky.api.APIService;
import com.finickyltd.finicky.api.APIUrl;
import com.finickyltd.finicky.helper.SharedPrefManager;
import com.finickyltd.finicky.models.Result;

public class MyAccountActivity extends AppCompatActivity implements View.OnClickListener{
    private LinearLayout llGotoMenu, llBackArrow, llRenewalDateContainer, llRenewalStatusContainer, llBtnContainer;
    private TextView tvEmailAddress, tvExpiredDate, tvAccountType, tvExpireDateLabel, tvExpireDescription;
    private TextView tvRenewalStatus;
    private Button btnChangePwd, btnResendEmail, btnCancelSub, btnRenewEnable;
    private View vwRenewDataLine, vwRenewDateLine;
    private ImageView ivIsVerified, ivBackgroudView;
    private int id;
    private String email;
    private Boolean isFacebook;
    public GlobalActivity globalActivity;

    // account info
    private String accountType;
    private String membershipType;
    private String subscrExpiration;
    private Boolean isRenewalEnabled;
    private AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        // initialize the info value
        accountType = "UNVERIFIED";
        membershipType = "LITE";
        subscrExpiration = "";
        isRenewalEnabled = false;

        isFacebook = false;
        id = SharedPrefManager.getInstance(getApplicationContext()).getUser().getId();
        email = SharedPrefManager.getInstance(getApplicationContext()).getUser().getEmail();

        TextView tx = (TextView)findViewById(R.id.hometitle);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Sriracha-Regular.ttf");
        tx.setTypeface(custom_font);

        btnChangePwd = (Button) findViewById(R.id.btnChangePwd);
        btnChangePwd.setOnClickListener(this);

        btnResendEmail = (Button) findViewById(R.id.btnResendEmail);
        btnResendEmail.setOnClickListener(this);

        btnCancelSub = (Button) findViewById(R.id.btnCancelSub);
        btnCancelSub.setOnClickListener(this);

        btnRenewEnable = (Button) findViewById(R.id.btnRenewEnable);
        btnRenewEnable.setOnClickListener(this);

        llGotoMenu = (LinearLayout) findViewById(R.id.llGoToMenu);
        llGotoMenu.setOnClickListener(this);

        llBackArrow = (LinearLayout) findViewById(R.id.llBackArrow);
        llBackArrow.setOnClickListener(this);

        llRenewalDateContainer = (LinearLayout) findViewById(R.id.llRenewalDataContainer);
        llRenewalStatusContainer = (LinearLayout) findViewById(R.id.llRenewalStausContainer);


        // "changepassword" and "resend email" button LinearLayout
        llBtnContainer = (LinearLayout) findViewById(R.id.llButtonContainer);

        tvEmailAddress = (TextView) findViewById(R.id.tvEmailAddress);
        tvExpiredDate = (TextView) findViewById(R.id.tvExpiredDate);
        tvAccountType = (TextView) findViewById(R.id.tvAccountType);
        tvRenewalStatus = (TextView) findViewById(R.id.tvRenewStatus);
        ivIsVerified = (ImageView) findViewById(R.id.ivIsVerified);

        tvExpireDateLabel = (TextView) findViewById(R.id.tvExpiredDateLabel);
        tvExpireDescription = (TextView) findViewById(R.id.tvExpireDescription);

        vwRenewDataLine = (View) findViewById(R.id.vwRenewDataLine);
        vwRenewDateLine = (View) findViewById(R.id.vwDateLine);

        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(getApplicationContext());
        String email = sharedPrefManager.getUser().getEmail();

        if (email.matches("[0-9]+") && email.length() > 2) {
            // user is facebook user, because email is facebook id
            isFacebook = true;
            llBtnContainer.setVisibility(View.GONE);

            String fb_name = sharedPrefManager.getFbUser().getFb_name();
            if(fb_name.equals("") || fb_name.equals(null))
                fb_name = "user";
            tvEmailAddress.setText(fb_name + " via Facebook");
        }
        else{
            llBtnContainer.setVisibility(View.VISIBLE);
            tvEmailAddress.setText(email);
        }

        // backgroud View
        ivBackgroudView = (ImageView) findViewById(R.id.ivBackgroundView);
        ivBackgroudView.setVisibility(View.VISIBLE);
        // admob
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        globalActivity = (GlobalActivity) getApplicationContext();

        if(globalActivity.getMembership_type().equals("LITE"))
            mAdView.setVisibility(View.VISIBLE);
        else
            mAdView.setVisibility(View.INVISIBLE);

        // to load user's payment status
        getPaymentStatus();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.llBackArrow:
                startActivity(new Intent(this, HomeActivity.class));
                break;
            case R.id.llGoToMenu:
                startActivity(new Intent(this, MenuActivity.class));
                break;
            case R.id.btnChangePwd:
                changePwdDialog();
                break;
            case R.id.btnResendEmail:
                submitEmailForVerify(this.email);
                break;
            case R.id.btnRenewEnable:
                updateRenewalStatus();
                break;
            case R.id.btnCancelSub:
                break;
        }
    }

    public void getPaymentStatus(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Getting your infomation...");
        progressDialog.show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIUrl.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIService service = retrofit.create(APIService.class);
        Call<Result> call = service.getPaymentStatus(id);

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                progressDialog.dismiss();

                if(response.body().getError() == false){
                    accountType = response.body().getUser().getAccount_type();
                    membershipType = response.body().getUser().getMembership_type();
                    subscrExpiration = response.body().getUser().getSubscription_expiration();
                    int enabled_value = response.body().getUser().getRenewalEnabled();
                    globalActivity.setMembership_type(response.body().getUser().getMembership_type());
                    if(enabled_value == 1){
                        isRenewalEnabled = true;
                    }
                    else {
                        isRenewalEnabled = false;
                    }
                    handler.sendEmptyMessage(1);

                }
                else {
                    //Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                //Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 1:
                    showAccount();
                    break;
                case 2:
                    refreshRenewalInfo();
                    break;
            }
        }
    };

    public void refreshRenewalInfo(){
        if(isRenewalEnabled){
            isRenewalEnabled = false;
            btnRenewEnable.setText("Enable");
            tvRenewalStatus.setText("OFF");
        }
        else {
            isRenewalEnabled = true;
            btnRenewEnable.setText("Disable");
            tvRenewalStatus.setText("ON");
        }
    }

    public void showAccount(){
        ivBackgroudView.setVisibility(View.GONE);

        if(globalActivity.getMembership_type().equals("LITE"))
            mAdView.setVisibility(View.VISIBLE);
        else
            mAdView.setVisibility(View.INVISIBLE);

        if(accountType.equals("UNVERIFIED")){
            if(isFacebook)
                ivIsVerified.setImageResource(R.drawable.checkmark);
            else{
                ivIsVerified.setImageResource(R.drawable.cancelmark);
                btnResendEmail.setVisibility(View.VISIBLE);
            }
        }
        else{
            ivIsVerified.setImageResource(R.drawable.checkmark);
            btnResendEmail.setVisibility(View.INVISIBLE);
        }


        if(membershipType.equals("PRO")){
            llRenewalDateContainer.setVisibility(View.VISIBLE);
            llRenewalStatusContainer.setVisibility(View.VISIBLE);
            vwRenewDataLine.setVisibility(View.VISIBLE);
            vwRenewDateLine.setVisibility(View.VISIBLE);

            tvExpiredDate.setText(subscrExpiration);

            if(isRenewalEnabled){
                tvRenewalStatus.setText("ON");
                btnRenewEnable.setText("Disable");
            }
            else {
                tvRenewalStatus.setText("OFF");
                btnRenewEnable.setText("Enable");
            }
            //btnCancelSub.setVisibility(View.VISIBLE);
        }

        tvAccountType.setText(membershipType);
    }

    public void updateRenewalStatus(){
        int flag = 0;

        if(isRenewalEnabled){
            flag = 0;
        }
        else {
            flag = 1;
        }

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating Auto Renewal...");
        progressDialog.show();
        progressDialog.setCancelable(false);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(5, TimeUnit.MINUTES)
                .connectTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIUrl.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIService service = retrofit.create(APIService.class);

        Call<Result> call =service.updateAutoRenewal(id, flag);
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                Boolean error = response.body().getError();

                if(error){
                    // error occured
                    Log.d("error", "error");
                }
                else {
                    // success
                    handler.sendEmptyMessage(2);
                }

                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                progressDialog.dismiss();
            }
        });
    }

    public void submitEmailForVerify(String email){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sending email...");
        progressDialog.show();
        progressDialog.setCancelable(false);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(5, TimeUnit.MINUTES)
                .connectTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIUrl.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIService service = retrofit.create(APIService.class);
        Call<Result> call = service.sendEmailForVefify(email);
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                String msg = "";
                progressDialog.dismiss();
                if(!response.body().getError()){
                    msg = "Please check your email for instructions on how to verify your account.";
                    alertDialog("EmailSentSuccess", msg);
                }
                else {
                    msg = "Network connection time out";
                    alertDialog("EmailSentError", msg);
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Log.d("mailException", t.getMessage());
                progressDialog.dismiss();
                alertDialog("EmailSentError", "Network Connection Time out.");
            }
        });
    }

    private void alertDialog(String type, String errTxt){
        final Dialog dialog = new Dialog(this);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.alert_login_error);
        TextView tvTitle = (TextView) dialog.findViewById(R.id.tvAlertTitle);
        TextView tvErrorText = (TextView) dialog.findViewById(R.id.tvErrorMsg);
        TextView tvDismiss = (TextView) dialog.findViewById(R.id.tvDismiss);
        tvErrorText.setText(errTxt);
        tvDismiss.setText("Dismiss");

        if(type.equals("EmailSentSuccess")){
            tvTitle.setText("Verify your account");
            tvDismiss.setText("OK");
        }
        else if (type.equals("Validate")){
            tvTitle.setText("Alert");
        }
        else if (type.equals("Renewal")){
            //tvTitle.setText("");
        }
        else
            tvTitle.setText("Verify Failed");



        ((TextView) dialog.findViewById(R.id.tvDismiss)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void changePwdDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.change_password_dialog);
        ((TextView) dialog.findViewById(R.id.tvChangePwdTitle)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Lato-Regular.ttf"));
        ((Button) dialog.findViewById(R.id.btnChangePwdOK)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // to change the password
                //String oldpassword = dialog.findViewById(R.id.etOldPwd).gette
                EditText etOldPwd = (EditText) dialog.findViewById(R.id.etOldPwd);
                String oldpassword = etOldPwd.getText().toString().trim();
                EditText etNewPwd = (EditText) dialog.findViewById(R.id.etNewPwd);
                String newpassword = etNewPwd.getText().toString().trim();
                EditText etConfirmPwd = (EditText) dialog.findViewById(R.id.etConfirmPwd);
                String confirmpassword = etConfirmPwd.getText().toString().trim();

                // to oldpassword is match to correct password
                if(!isValidPassword(oldpassword)){
                    //alertDialog("Test", "This is test");
                    alertDialog("Validate", "Enter correctly old password ");
                    etOldPwd.setText(null);
                    return;
                }
                if(newpassword.length() < 6){
                    etNewPwd.setText(null);
                    etConfirmPwd.setText(null);
                    alertDialog("Validate", "Enter a Password with at least 6 Characters");
                    return;
                }

                if(!isMatchPassword(newpassword, confirmpassword)){
                    etNewPwd.setText(null);
                    etConfirmPwd.setText(null);
                    alertDialog("Validate", "Enter password correctly, password not matched");
                    return;
                }

                updatePassword(newpassword);
                dialog.dismiss();
            }
        });

        ((Button) dialog.findViewById(R.id.btnChangePwdCancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private Boolean isValidPassword(String oldpassword){
        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(getApplicationContext());
        String originPwd = sharedPrefManager.getUser().getPassword();
        // to convert plain text to crypted
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] hash = digest.digest(oldpassword.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            if(hexString.toString().equals(originPwd)){
                return true;
            }
            else {
                return false;
            }
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    private Boolean isMatchPassword(String password1, String password2){
        if(password1.equals(password2)){
            return true;
        }
        return false;
    }

    private void updatePassword(final String password){
        final SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(getApplicationContext());
        int id = sharedPrefManager.getUser().getId();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(5, TimeUnit.MINUTES)
                .connectTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIUrl.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIService service = retrofit.create(APIService.class);
        Call<Result> call = service.updateUserPassword(id, password);
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                sharedPrefManager.updatePassword(response.body().getUser().getPassword());
                Log.d("response", response.body().getMessage());
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Log.d("failure", t.getMessage());
            }
        });
    }
}

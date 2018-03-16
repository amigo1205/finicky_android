package com.finickyltd.finicky;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.finickyltd.finicky.adapter.ExplorerPagerAdapter;
import com.finickyltd.finicky.api.APIService;
import com.finickyltd.finicky.api.APIUrl;
import com.finickyltd.finicky.helper.SharedPrefManager;
import com.finickyltd.finicky.models.Result;

public class ExploreActivity extends FragmentActivity implements View.OnClickListener{
    //sale's: AYWozam1j3vzKZWGSAWHuQfqNWBnj2PKDxHQPZ5GSib4UNsSEwWCGIDyFU78IcX9gEIdAUC0Hht9vEIW
    // faciliater : AePtMsyHgq2sHWKxPu0Yr_gX8z89tNuQyveQeVCGIulo1LEFGltANF7VBa1IaUO5-EHZ7E0ODTARg_31
    // production : AVoc8l2LvSGIPo1OrYEqicEfb8Yo0RZIJntlRzkPg85UutzxLLzcwYFdL-5Qp2X930Xw0m34wZVG4j11
    private ViewPager mViewPager;
    private RelativeLayout rlMonthlyPay, rlYeaylyPay;
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_PRODUCTION)
            .clientId("AVoc8l2LvSGIPo1OrYEqicEfb8Yo0RZIJntlRzkPg85UutzxLLzcwYFdL-5Qp2X930Xw0m34wZVG4j11")
            .merchantName("facilitator's Finicky Store")
            .merchantPrivacyPolicyUri(Uri.parse("https://www.iubenda.com/privacy-policy/8273791"))
            .merchantUserAgreementUri(Uri.parse("http://finicky-app.com/new_faq.html"));

    private LinearLayout llGotoMenu;
    public static int PAYPAL_REQUEST_CODE  = 123;
    private  int id;
    private static String metadata_id;
    private Boolean periodType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        metadata_id = "";
        periodType = false;

        id = SharedPrefManager.getInstance(getApplicationContext()).getUser().getId();

        TextView tx = (TextView)findViewById(R.id.hometitle);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Sriracha-Regular.ttf");
        tx.setTypeface(custom_font);

        // monthly and yearly pay button
        rlMonthlyPay = (RelativeLayout) findViewById(R.id.rlMonthlyPay);
        rlYeaylyPay = (RelativeLayout) findViewById(R.id.rlYearlyPay);
        rlMonthlyPay.setOnClickListener(this);
        rlYeaylyPay.setOnClickListener(this);

        llGotoMenu = (LinearLayout) findViewById(R.id.llGoToMenu);
        llGotoMenu.setOnClickListener(this);
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        ExplorerPagerAdapter mAdapter = new ExplorerPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ImageView img = (ImageView)findViewById(R.id.imgCheck);
                switch (position){
                    case 0:
                        img.setImageResource(R.drawable.check1);
                        break;
                    case 1:
                        img.setImageResource(R.drawable.check2);
                        break;
                    case 2:
                        img.setImageResource(R.drawable.check3);
                        break;
                    case 3:
                        img.setImageResource(R.drawable.check4);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rlMonthlyPay:
                onFuturePaymentPressed(false);
                break;

            case R.id.rlYearlyPay:
                onFuturePaymentPressed(true);
                break;

            case R.id.llGoToMenu:
                startActivity(new Intent(this, MenuActivity.class));
                break;
        }
    }

    public void onFuturePaymentPressed(Boolean periodType){
        this.periodType = periodType;
        metadata_id = PayPalConfiguration.getClientMetadataId(this);
        Intent intent = new Intent(this, PayPalFuturePaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }


    // ----------------***  once payment method  *** --------------------- //
    public void startPaypalService(String payType){

        String paymentAmount = "";

        if(payType.equals("Monthly")){
            paymentAmount = "1.99";
        }
        else if(payType.equals("Yearly")){
            paymentAmount = "14.99";
        }
        else {
            paymentAmount = "1.99";
        }

        PayPalPayment payment = new PayPalPayment(new BigDecimal(paymentAmount), "GBP", "Finicky",
                PayPalPayment.PAYMENT_INTENT_SALE);
        String strId = Integer.toString(id);
        payment.custom(strId);

        Intent intent = new Intent(this, PaymentActivity.class);

        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
        //intent.putExtra(PaymentActivity.E, id);
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }

    // ------------------- -----------------------------------------------------//
    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if(requestCode == PAYPAL_REQUEST_CODE){
            if (resultCode == Activity.RESULT_OK) {
                PayPalAuthorization authorization = data.getParcelableExtra(PayPalFuturePaymentActivity.EXTRA_RESULT_AUTHORIZATION);

                if(authorization != null){
                    String auth_code = authorization.getAuthorizationCode();
                    sendAuthorizationToServer(auth_code);
                }
                else {
                    showPaymentDialog("", "An invalid Payment or PayPalConfiguration was submitted.");
                }
            }
            else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("paymentExample", "The user canceled.");
                Toast.makeText(getApplicationContext(), "you canceled payment.", Toast.LENGTH_SHORT).show();
                showPaymentDialog("", "Payment Cancelled");
            }
            else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
                Toast.makeText(getApplicationContext(), "An invalid Payment or PayPalConfiguration was submitted.", Toast.LENGTH_SHORT).show();
                showPaymentDialog("", "An invalid Payment or PayPalConfiguration was submitted.");
            }
        }
        else {
            Log.d("Alert", "Paypal Activity is not returned");
            Toast.makeText(getApplicationContext(), "Paypal Activity not returned.", Toast.LENGTH_LONG).show();
            showPaymentDialog("", "An invalid Payment or PayPalConfiguration was submitted.");
        }
    }

    private void sendAuthorizationToServer(String authCode) {
        String period = "";
        if(periodType)
            period = "annually";
        else
            period = "monthly";


        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sending authorization code to server...");
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

        Call<Result> call = service.userSubscribe(id, authCode, metadata_id, period);

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                progressDialog.dismiss();
                Boolean error = response.body().getError();

                if(error){  // error occured
                    showPaymentDialog("", "You tried with invalid token, Try again later.");
                }
                else {      // error not occured, success

                    String amount = "";

                    if(periodType)
                        amount = "14.99 GBP";
                    else
                        amount = "1.99 GBP";

                    showPaymentDialog(amount, "Payment Successful");
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });
    }

    public void showPaymentDialog(String paymentAmount, String status) {
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable
                (new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.requestWindowFeature(1);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.payment_result_dialog);

        ImageView ivStatusImg = (ImageView) dialog.findViewById(R.id.ivStatusImg);

        TextView tvPaymentAmount = (TextView) dialog.findViewById(R.id.tvPaymentAmount);
        tvPaymentAmount.setText(paymentAmount);
        LinearLayout llPayAgain = (LinearLayout) dialog.findViewById(R.id.llPayagain);

        TextView tvPaymentResult = (TextView) dialog.findViewById(R.id.tvPaymentResult);
        tvPaymentResult.setText(status);
        if(status.equals("Payment Successful")){
            llPayAgain.setVisibility(View.INVISIBLE);
            ivStatusImg.setImageResource(R.drawable.done);
            tvPaymentResult.setTextColor(getResources().getColor(R.color.green_app_color));
        }
        else{
            ivStatusImg.setImageResource(R.drawable.failure);
            tvPaymentResult.setTextColor(getResources().getColor(R.color.holo_red_light));
        }

        ((LinearLayout) dialog.findViewById(R.id.llPayagain)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        ((LinearLayout) dialog.findViewById(R.id.llGotoHome)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToHomeActivity();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void goToHomeActivity(){
        startActivity(new Intent(this, HomeActivity.class));
    }

}

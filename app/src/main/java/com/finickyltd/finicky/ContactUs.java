package com.finickyltd.finicky;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

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

public class ContactUs extends AppCompatActivity  implements View.OnClickListener{
    private LinearLayout llGotoMenu, llBackArrow;
    private EditText etSubject, etMessage;
    private Button btnSubmit;
    private int id;
    public GlobalActivity globalActivity;
    private RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        globalActivity = (GlobalActivity) getApplicationContext();

        relativeLayout = (RelativeLayout) findViewById(R.id.rlLayoutContainer);

        TextView tx = (TextView)findViewById(R.id.hometitle);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Sriracha-Regular.ttf");
        tx.setTypeface(custom_font);

        id = SharedPrefManager.getInstance(getApplicationContext()).getUser().getId();

        llGotoMenu = (LinearLayout) findViewById(R.id.llGoToMenu);
        llGotoMenu.setOnClickListener(this);


        llBackArrow = (LinearLayout) findViewById(R.id.llBackArrow) ;
        llBackArrow.setOnClickListener(this);

        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(this);

        etSubject = (EditText) findViewById(R.id.etSubject);
        etMessage = (EditText) findViewById(R.id.etMessage);

        // admob
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        if(globalActivity.getMembership_type().equals("LITE"))
            mAdView.setVisibility(View.VISIBLE);
        else
            mAdView.setVisibility(View.INVISIBLE);

        final View activityRootView = findViewById(R.id.llContactUsRootView);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                if (heightDiff > 100) {
                    layoutParams.setMargins(40, 0, 40, heightDiff + 100);
                    //Toast.makeText(getApplicationContext(), "S", Toast.LENGTH_SHORT).show();
                    //btnSubmit.setLayoutParams(layoutParams);
                } else {
                    //Toast.makeText(getApplicationContext(), "H", Toast.LENGTH_SHORT).show();
                    layoutParams.setMargins(40, 50, 40, 0);
                    //btnSubmit.setLayoutParams(layoutParams);
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.llGoToMenu:
                startActivity(new Intent(this, MenuActivity.class));
                break;
            case R.id.btnSubmit:
                String subject = etSubject.getText().toString();

                if(subject.equals("") || subject.equals(null)){
                    Toast.makeText(getApplicationContext(), "Fill out in subject", Toast.LENGTH_LONG).show();
                    return;
                }

                String message = etMessage.getText().toString();
                if(message.equals("") || message.equals(null)){
                    Toast.makeText(getApplicationContext(), "Fill out in content", Toast.LENGTH_LONG).show();
                    return;
                }
                submitToSupport(id, subject, message);
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

    private void submitToSupport(int id, String subject, String message){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Send message ...");
        progressDialog.show();

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
        Call<Result> call = service.contactUs(id, subject, message);
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                progressDialog.dismiss();
                String resultTxt = response.body().getMessage();
                if(resultTxt.equals("") || resultTxt.equals(null))
                    resultTxt = "Network connection time out";

                if(response.body().getError()){
                    alertDialog("Message Sent Error", resultTxt);
                }
                else {
                    alertDialog("You sent Message to us", resultTxt);
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                progressDialog.dismiss();
                alertDialog("Failure to send message", "Network Connection Time out.");
            }
        });
    }

    private void alertDialog(String title, String errTxt){
        final Dialog dialog = new Dialog(this);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.alert_login_error);
        TextView tvTitle = (TextView) dialog.findViewById(R.id.tvAlertTitle);
        TextView tvErrorText = (TextView) dialog.findViewById(R.id.tvErrorMsg);
        TextView tvDismiss = (TextView) dialog.findViewById(R.id.tvDismiss);
        tvErrorText.setText(errTxt);
        tvTitle.setText(title);
        tvDismiss.setText("Dismiss");

        ((TextView) dialog.findViewById(R.id.tvDismiss)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}

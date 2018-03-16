package com.finickyltd.finicky;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

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

public class FilterSettingActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "FILTER SETTING";
    private static  final  String KEY_IsFILTER = "isRegisterdFilterSetting";
    CheckBox checkbox;
    ProgressDialog pDialog;
    ToggleButton tbCelery;
    ToggleButton tbCrustacean;
    ToggleButton tbDairy;
    ToggleButton tbEgg;
    ToggleButton tbGluten;
    ToggleButton tbHoney;
    ToggleButton tbLupin;
    ToggleButton tbMollusc;
    ToggleButton tbMustard;
    ToggleButton tbPalmOil;
    ToggleButton tbPeanut;
    ToggleButton tbSeaFish;
    ToggleButton tbSeasame;
    ToggleButton tbSoya;
    ToggleButton tbSugar;
    ToggleButton tbSulphites;
    ToggleButton tbTreeNut;
    ToggleButton tbVegan;
    boolean veganItemShowStatus = true;

    private LinearLayout llTermsOfService, llBackArrow, llchildVegan;
    private Button btnCompleteSignup;
    public String email;
    public int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_setting);

        TextView tx = (TextView)findViewById(R.id.hometitle);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Sriracha-Regular.ttf");
        tx.setTypeface(custom_font);

        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(getApplicationContext());
        this.id = sharedPrefManager.getUser().getId();

        this.loadUIComponents();

        llTermsOfService = (LinearLayout) findViewById(R.id.llTermsOfService);
        llTermsOfService.setOnClickListener(this);
        llBackArrow = (LinearLayout) findViewById(R.id.llBackArrow);
        llBackArrow.setOnClickListener(this);
        btnCompleteSignup = (Button) findViewById(R.id.btnCompleteSignup);
        btnCompleteSignup.setOnClickListener(this);
        llchildVegan = (LinearLayout) findViewById(R.id.llchildVergan);

        /*
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        */
    }

    // to load toggle buttons and check option
    public void loadUIComponents(){
        this.pDialog = new ProgressDialog(this);
        this.pDialog.setMessage("Please wait...");
        this.pDialog.setCancelable(false);
        this.checkbox = (CheckBox) findViewById(R.id.chTermsOfService);
        this.tbVegan = (ToggleButton) findViewById(R.id.tbVegan);
        this.tbCrustacean = (ToggleButton) findViewById(R.id.tbCrustacean);
        this.tbMollusc = (ToggleButton) findViewById(R.id.tbMollusc);
        this.tbHoney = (ToggleButton) findViewById(R.id.tbHoney);
        this.tbPalmOil = (ToggleButton) findViewById(R.id.tbPalmOil);
        this.tbSugar = (ToggleButton) findViewById(R.id.tbSugar);
        this.tbTreeNut = (ToggleButton) findViewById(R.id.tbTreeNut);
        this.tbSeaFish = (ToggleButton) findViewById(R.id.tbSeaFish);
        this.tbGluten = (ToggleButton) findViewById(R.id.tbGluten);
        this.tbDairy = (ToggleButton) findViewById(R.id.tbDairy);
        this.tbCelery = (ToggleButton) findViewById(R.id.tbCelery);
        this.tbLupin = (ToggleButton) findViewById(R.id.tbLupin);
        this.tbSeasame = (ToggleButton) findViewById(R.id.tbSeasame);
        this.tbSulphites = (ToggleButton) findViewById(R.id.tbSulphites);
        this.tbEgg = (ToggleButton) findViewById(R.id.tbEgg);
        this.tbSoya = (ToggleButton) findViewById(R.id.tbSoya);
        this.tbPeanut = (ToggleButton) findViewById(R.id.tbPeanut);
        this.tbMustard = (ToggleButton) findViewById(R.id.tbMustard);
        this.tbVegan.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.llTermsOfService:
                startActivity(new Intent(this, TermsOfServiceActivity.class));
                break;
            case R.id.btnCompleteSignup:
                this.gotoNextpage();
                break;
            case R.id.llBackArrow:
                startActivity(new Intent(this, SignUpActivity.class));
                break;
            case R.id.tbVegan:
                loadChildVegan();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void loadChildVegan(){
        LinearLayout childLayout = (LinearLayout) findViewById(R.id.llchildVergan);
        if(this.veganItemShowStatus){
            childLayout.setVisibility(View.VISIBLE);
            this.veganItemShowStatus = false;
        }
        else {
            childLayout.setVisibility(View.GONE);
            this.veganItemShowStatus = true;
        }
    }

    // to complete sign up
    public void gotoNextpage(){
        if(!this.checkTermsAndField()){
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Register Filter setting...");
        progressDialog.show();

        // to register option of user to server

        //building retrofit object

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

        //Defining retrofit api service
        APIService service = retrofit.create(APIService.class);

        Call<Result> call = service.registerFilterSetting(
                this.tbVegan.getText().toString(),
                this.tbCrustacean.getText().toString(),
                this.tbMollusc.getText().toString(),
                this.tbTreeNut.getText().toString(),
                this.tbSeaFish.getText().toString(),
                this.tbGluten.getText().toString(),
                this.tbDairy.getText().toString(),
                this.tbCelery.getText().toString(),
                this.tbLupin.getText().toString(),
                this.tbSeasame.getText().toString(),
                this.tbSulphites.getText().toString(),
                this.tbEgg.getText().toString(),
                this.tbSoya.getText().toString(),
                this.tbPeanut.getText().toString(),
                this.tbMustard.getText().toString(),
                this.tbHoney.getText().toString(),
                this.tbPalmOil.getText().toString(),
                this.tbSugar.getText().toString(),
                this.id
        );

        //calling the api
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {

                //hiding progress dialog
                progressDialog.dismiss();

                //displaying the message from the response as toast
                //Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                goToHomeActivity();
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void goToHomeActivity(){
        // to write to the preferences
        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(getApplicationContext());
        sharedPrefManager.writeIsFilterSetting();

        // to go to Home Activity
        startActivity(new Intent(this, HomeActivity.class));
        FilterSettingActivity.this.finish();
    }

    // validate if user agree on "Terms of Service" and check at lease one option
    public boolean checkTermsAndField(){
        if(!this.checkbox.isChecked()){
            Toast.makeText(getApplicationContext(), "Please confirm to accept the Terms of Service.", Toast.LENGTH_SHORT).show();
            this.checkbox.setError("*");
            return false;
        }
        else if(!this.isValidToggleButtons()){
            Toast.makeText(getApplicationContext(), "Please select at least one filter.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    // to validate if one toggle button is checked at least
    public boolean isValidToggleButtons(){
        if(this.tbCelery.isChecked() || this.tbCrustacean.isChecked() || this.tbDairy.isChecked() || this.tbEgg.isChecked() || this.tbGluten.isChecked() ||
                this.tbLupin.isChecked() || this.tbMollusc.isChecked() || this.tbMustard.isChecked() || this.tbPeanut.isChecked() || this.tbSeaFish.isChecked() || this.tbSeasame.isChecked() ||
                this.tbSulphites.isChecked() || this.tbSoya.isChecked() || this.tbTreeNut.isChecked() || this.tbVegan.isChecked())
        {
            return true;
        }
        else
            return false;
    }
}


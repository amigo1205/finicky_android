package com.finickyltd.finicky;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

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
import com.finickyltd.finicky.models.FilterResult;
import com.finickyltd.finicky.models.Result;

public class FilterSettingChangeActivity extends AppCompatActivity implements View.OnClickListener{
    private ToggleButton tbCelery;
    private ToggleButton tbCrustacean;
    private ToggleButton tbDairy;
    private ToggleButton tbEgg;
    private ToggleButton tbGluten;
    private ToggleButton tbHoney;
    private ToggleButton tbLupin;
    private ToggleButton tbMollusc;
    private ToggleButton tbMustard;
    private ToggleButton tbPalmOil;
    private ToggleButton tbPeanut;
    private ToggleButton tbSeaFish;
    private ToggleButton tbSeasame;
    private ToggleButton tbSoya;
    private ToggleButton tbSugar;
    private ToggleButton tbSulphites;
    private ToggleButton tbTreeNut;
    private ToggleButton tbVegan;
    private boolean veganItemShowStatus = true;     // variable for switch toggle
    private boolean veganFilterStatus;              // status to set on server
    private LinearLayout childLayout;
    public String email;
    private LinearLayout llGotoMenu;
    private Button btnConfirmChange;
    private int id;

    public GlobalActivity globalActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_setting_change);

        TextView tx = (TextView)findViewById(R.id.hometitle);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Sriracha-Regular.ttf");
        tx.setTypeface(custom_font);

        llGotoMenu = (LinearLayout) findViewById(R.id.llGoToMenu);
        llGotoMenu.setOnClickListener(this);
        btnConfirmChange = (Button) findViewById(R.id.btnConfirmChange);
        btnConfirmChange.setOnClickListener(this);

        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(getApplicationContext());
         this.id = sharedPrefManager.getUser().getId();

        globalActivity = (GlobalActivity) getApplicationContext();

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        if(globalActivity.getMembership_type().equals("LITE"))
            mAdView.setVisibility(View.VISIBLE);
        else
            mAdView.setVisibility(View.INVISIBLE);

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

        this.childLayout = (LinearLayout) findViewById(R.id.llchildVergan);
        //loadUIComponents();
        loadUI();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.llGoToMenu:
                startActivity(new Intent(this, MenuActivity.class));
                break;
            case R.id.btnConfirmChange:
                changeFilterOptions();
                break;
            case R.id.tbVegan:
                loadChildVegan();
                break;
        }
    }

    public void loadUIComponents(){
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

    public void loadUI(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Getting your Filters...");
        progressDialog.show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIUrl.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIService service = retrofit.create(APIService.class);
        id = SharedPrefManager.getInstance(getApplicationContext()).getUser().getId();
        Call<FilterResult> call = service.getUserOfFilters(id);
        call.enqueue(new Callback<FilterResult>() {
            @Override
            public void onResponse(Call<FilterResult> call, Response<FilterResult> response) {
                Log.d("response", "");
                //Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();

                if(response.body().getError() == false){
                    tbVegan.setChecked(intToBoolean(response.body().getFilterModel().getVegan()));
                    if(intToBoolean(response.body().getFilterModel().getVegan())){
                        childLayout.setVisibility(View.VISIBLE);
                    }
                    veganFilterStatus = intToBoolean(response.body().getFilterModel().getVegan());
                    tbCrustacean.setChecked(intToBoolean(response.body().getFilterModel().getCrustacean()));
                    tbMollusc.setChecked(intToBoolean(response.body().getFilterModel().getMollusc()));
                    tbTreeNut.setChecked(intToBoolean(response.body().getFilterModel().getTreenut()));
                    tbSeaFish.setChecked(intToBoolean(response.body().getFilterModel().getFish()));
                    tbGluten.setChecked(intToBoolean(response.body().getFilterModel().getGluten()));
                    tbDairy.setChecked(intToBoolean(response.body().getFilterModel().getDairy()));
                    tbCelery.setChecked(intToBoolean(response.body().getFilterModel().getCelery()));
                    tbLupin.setChecked(intToBoolean(response.body().getFilterModel().getLupin()));
                    tbSeasame.setChecked(intToBoolean(response.body().getFilterModel().getSesame()));
                    tbSulphites.setChecked(intToBoolean(response.body().getFilterModel().getSulphites()));
                    tbEgg.setChecked(intToBoolean(response.body().getFilterModel().getEgg()));
                    tbSoya.setChecked(intToBoolean(response.body().getFilterModel().getSoya()));
                    tbPeanut.setChecked(intToBoolean(response.body().getFilterModel().getPeanut()));
                    tbMustard.setChecked(intToBoolean(response.body().getFilterModel().getMustard()));
                    tbHoney.setChecked(intToBoolean(response.body().getFilterModel().getHoney()));
                    tbPalmOil.setChecked(intToBoolean(response.body().getFilterModel().getPalmOil()));
                    tbSugar.setChecked(intToBoolean(response.body().getFilterModel().getSugar()));
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<FilterResult> call, Throwable t) {
                Log.d("failure", t.getMessage());
                //Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    public boolean intToBoolean(int value){
        if(value == 1)
            return true;
        return false;
    }

    public void loadChildVegan(){
        // vegan is true
        if(veganFilterStatus){
            if(!this.veganItemShowStatus){
                childLayout.setVisibility(View.VISIBLE);
                this.veganItemShowStatus = true;
            }
            else {
                childLayout.setVisibility(View.GONE);
                this.veganItemShowStatus = false;
            }
        }
        // vegan is false
        else {
            if(this.veganItemShowStatus){
                childLayout.setVisibility(View.VISIBLE);
                this.veganItemShowStatus = false;
            }
            else {
                childLayout.setVisibility(View.GONE);
                this.veganItemShowStatus = true;
            }
        }
    }


    public boolean changeFilterOptions(){
        if(!this.checkTermsAndField()){
            return false;
        }
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Modify Filter setting...");
        progressDialog.show();

        // to register changed option of user to server

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


        Call<Result> call = service.changeFilterSetting(
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
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        return false;
    }

    public boolean checkTermsAndField(){
        if(!this.isValidToggleButtons()){
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

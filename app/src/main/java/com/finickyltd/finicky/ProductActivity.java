package com.finickyltd.finicky;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.finickyltd.finicky.adapter.ListViewFilterAdapter;
import com.finickyltd.finicky.api.APIService;
import com.finickyltd.finicky.api.APIUrl;
import com.finickyltd.finicky.helper.SharedPrefManager;
import com.finickyltd.finicky.models.FilterItem;
import com.finickyltd.finicky.models.ProductResult;
import com.finickyltd.finicky.models.Result;

public class ProductActivity extends AppCompatActivity implements View.OnClickListener{
    private LinearLayout llGotoMenu, lnSpinner;
    private ListView listView;
    private ListViewFilterAdapter filterAdapter;
    public String barcodeContent, productName;
    private TextView tvProductName, tvIngredientShow, tvIngredients, txtSelectedCategory;
    private Boolean isShowHide = true;
    private Button btnReport;
    private int id;
    public ImageView ivSplashImage;
    private Boolean isFromScan;
    public GlobalActivity globalActivity;
    private Dialog reportDialog;
    private Spinner spinnerCategory;
    ArrayList<String> m_categoryStrings = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        TextView tx = (TextView)findViewById(R.id.hometitle);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Sriracha-Regular.ttf");
        tx.setTypeface(custom_font);

        reportDialog = new Dialog(this);
        reportDialog.setCancelable(true);
        reportDialog.setContentView(R.layout.report_dialog);

        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(getApplicationContext());
        id = sharedPrefManager.getUser().getId();

        ivSplashImage = (ImageView) findViewById(R.id.ivSplashImage);

        llGotoMenu = (LinearLayout) findViewById(R.id.llGoToMenu);
        llGotoMenu.setOnClickListener(this);

        btnReport = (Button) findViewById(R.id.btnReportProduct);
        btnReport.setOnClickListener(this);

        tvProductName = (TextView) findViewById(R.id.tvProductName);
        tvIngredients = (TextView) findViewById(R.id.tvIngredients);
        tvIngredientShow = (TextView) findViewById(R.id.tvIngredientShow);
        tvIngredientShow.setOnClickListener(this);

        //tvIngredients = (TextView) findViewById(R.id.tvIngredient);

        // list view
        filterAdapter = new ListViewFilterAdapter();
        listView = (ListView) findViewById(R.id.lvFilters);
        listView.setAdapter(filterAdapter);
        /*
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListViewFilterItem item = (ListViewFilterItem) parent.getItemAtPosition(position);
            }
        });
        */
        filterAdapter.notifyDataSetChanged();

        // admob
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        globalActivity = (GlobalActivity) getApplicationContext();

        if(globalActivity.getMembership_type().equals("LITE"))
            mAdView.setVisibility(View.VISIBLE);
        else
            mAdView.setVisibility(View.INVISIBLE);

        // to get the barcodeContent
        Intent intent = getIntent();
        barcodeContent = intent.getStringExtra("barcodeContent");
        Log.d("barcodeContent", barcodeContent);
        productName = intent.getStringExtra("product_name");
        isFromScan = intent.getBooleanExtra("isFromScan", false);

        if(isFromScan){
            ivSplashImage.setVisibility(View.VISIBLE);
        }
        else {
            ivSplashImage.setVisibility(View.GONE);
        }

        loadProductInfo(barcodeContent, id);

        // to define the spinner dialog for report
        lnSpinner = (LinearLayout) reportDialog.findViewById(R.id.lnSpinner);
        txtSelectedCategory = (TextView)reportDialog.findViewById(R.id.txtSelectedCategory);
        spinnerCategory = (Spinner) reportDialog.findViewById(R.id.spnReportCategory);

        lnSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerCategory.performClick();
            }
        });

        m_categoryStrings.add("Wrong product Ingredients");
        m_categoryStrings.add("Wrong product found");
        m_categoryStrings.add("Other");
        ArrayAdapter<String> adapterEst = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, m_categoryStrings);

        spinnerCategory.setAdapter(adapterEst);
        spinnerCategory.setOnItemSelectedListener(mSpListener);
    }

    private AdapterView.OnItemSelectedListener mSpListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {

            txtSelectedCategory.setText(m_categoryStrings.get(position));
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    public void onClick(View view){
        switch (view.getId()){
            case R.id.llGoToMenu:
                startActivity(new Intent(this, MenuActivity.class));
                break;
            case R.id.tvIngredientShow:
                showIngrediens();
                break;
            case R.id.btnReportProduct:
                // show report dialog
                showReportDialog(this.barcodeContent);
                break;
        }
    }

    public void displaySplash(Integer type){
        if(isFromScan) {
            ivSplashImage.setVisibility(View.VISIBLE);
            switch (type){
                case 0:
                    ivSplashImage.setBackgroundResource(R.drawable.not_suitable);
                    break;
                case 1:
                    ivSplashImage.setBackgroundResource(R.drawable.maybe_suitable);
                    break;
                case 2:
                    ivSplashImage.setBackgroundResource(R.drawable.not_suitable);
                    break;
                case 3:
                    ivSplashImage.setBackgroundResource(R.drawable.suitable);
                    break;
                case 5:  // product_error
                    ivSplashImage.setBackgroundResource(R.drawable.product_error);
                    break;
            }
        }
    }

    public void hideSplash(){
        ivSplashImage.setVisibility(View.GONE);
    }

    public void showIngrediens(){
        if(isShowHide){
            tvIngredientShow.setText("Hide");
            //tvIngredientShow.setTextColor(getResources().getColor(R.color.holo_red_light));
            tvIngredients.setVisibility(View.VISIBLE);
            isShowHide = false;
        }
        else {
            tvIngredientShow.setText("Show");
            //tvIngredientShow.setTextColor(getResources().getColor(R.color.green_app_color));
            tvIngredients.setVisibility(View.GONE);
            isShowHide = true;
        }
    }

    public void showReportDialog(final String barcodeContent){

        ((TextView) reportDialog.findViewById(R.id.tvCancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportDialog.dismiss();
            }
        });

        ((TextView) reportDialog.findViewById(R.id.tvOk)).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // report product
//                ArrayList<String> categories = new ArrayList<String>();
//                categories.add("Wrong product Ingredients");
//                categories.add("Wrong product found");
//                categories.add("Other");
//
//                Spinner spinner;
//                ArrayAdapter<String> adapter;
//                spinner = (Spinner) reportDialog.findViewById(R.id.spnReportCategory);
//                adapter = new ArrayAdapter<String>(ProductActivity.this, R.layout.spinner_item, categories);
//                spinner.setAdapter(adapter);
//
//                EditText etDetailContent = (EditText) reportDialog.findViewById(R.id.etRptDetailContent);
//                final String detailContents = etDetailContent.getText().toString();
//
//                //Spinner spinnerCategory = (Spinner) reportDialog.findViewById(R.id.spnReportCategory);

                String categoryTxt = txtSelectedCategory.getText().toString();

                EditText etDetailContent = (EditText) reportDialog.findViewById(R.id.etRptDetailContent);
                String detailContents = etDetailContent.getText().toString();

                reportProduct(barcodeContent, categoryTxt, detailContents);
                reportDialog.dismiss();
            }
        });
        reportDialog.show();
    }

    public void reportProduct(String barcodeContent, String category, String detailContent){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Reporting this product");
        progressDialog.show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIUrl.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIService service = retrofit.create(APIService.class);
        Call<Result> call = service.reportProduct(this.id, barcodeContent, category, detailContent);
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                progressDialog.dismiss();
                String resultTxt = "";
                resultTxt = response.body().getMessage();
                if(resultTxt.equals("") || resultTxt.equals(null)){
                    resultTxt = "Network connection time out";
                }

                alertDialog("success", "Report Result",resultTxt);
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                progressDialog.dismiss();
                alertDialog("", "Report Error","Network Connection Time out.");
            }
        });
    }

    private void alertDialog(String type, String title,String errTxt){
        final Dialog dialog = new Dialog(this);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.alert_login_error);
        TextView tvTitle = (TextView) dialog.findViewById(R.id.tvAlertTitle);
        TextView tvErrorText = (TextView) dialog.findViewById(R.id.tvErrorMsg);
        TextView tvDismiss = (TextView) dialog.findViewById(R.id.tvDismiss);
        tvErrorText.setText(errTxt);
        tvTitle.setText(title);

        if(type.equals("success")){
            tvDismiss.setText("OK");
        }
        else {
            tvDismiss.setText("Dismiss");
        }

        ((TextView) dialog.findViewById(R.id.tvDismiss)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void loadProductInfo(String barcodeContent, int id){
        //building retrofit object
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading product info...");
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

        //Defining retrofit api service
        APIService service = retrofit.create(APIService.class);

        //Defining the user object as we need to pass it with the call

        Call<ProductResult> call = service.getProductWithBarcode(
                barcodeContent,
                id
        );

        call.enqueue(new Callback<ProductResult>() {
            @Override
            public void onResponse(Call<ProductResult> call, Response<ProductResult> response) {
                //Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();
                Boolean status = response.body().getStatus();

                if(status == true){////////////////

                    String productName = response.body().getProductName();
                    //String ingredients = response.body().getIngredients();
                    tvProductName.setText(productName);
                    tvProductName.setTypeface(null, Typeface.BOLD_ITALIC);
                    tvIngredients.setText(response.body().getIngredients());

                    if(response.body().getIngredients().equals("UNKNOWN_INGREDIENTS")){
                        displaySplash(5);

                        new Timer().schedule(new TimerTask() {
                            public void run() {
                                mHandler.sendEmptyMessage(1);
                            }
                        }, 2000);
                    }
                    else {
                        Integer notSuitable = response.body().getNotSuitable();
                        Integer maybeSuitable = response.body().getMaybeSuitable();
                        Integer splashType = 3;   // suitable
                        if(notSuitable == 1 && maybeSuitable == 0){
                            splashType = 0;      // not suitable
                        }
                        else if (notSuitable == 0 && maybeSuitable == 1){
                            splashType = 1;    //  maybesuitable
                        }
                        else if(notSuitable == 1 && maybeSuitable == 1)
                            splashType = 2;  // not suitable

                        displaySplash(splashType);

                        new Timer().schedule(new TimerTask() {
                            public void run() {
                                mHandler.sendEmptyMessage(1);
                            }
                        }, 2000);

                        List<FilterItem> filterItems = response.body().getFilterItems();
                        if(filterItems != null){
                            // to display the items
                            addItemToListView(filterItems);
                        }
                    }
                }
                else {
                    //Toast.makeText(getApplicationContext(), response.body().getError(), Toast.LENGTH_LONG).show();
                    displaySplash(5);
                    new Timer().schedule(new TimerTask() {
                        public void run() {
                            mHandler.sendEmptyMessage(1);
                        }
                    }, 2000);
                    alertDialog("Product Error", "Product Result","Sorry, we have no info of this product");
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ProductResult> call, Throwable t) {
                //Toast.makeText(getApplicationContext(), "Network Connection Time out.", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
                alertDialog("Network Error", "Network Status", "Network Connection Time out.");
            }
        });
    }
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1) {
                hideSplash();
                tvIngredients.setMovementMethod(new ScrollingMovementMethod());
            }
        }
    };

    public void addItemToListView(List<FilterItem> items){
        for (FilterItem item : items){
            int titleBackground;
            if(item.getStatus() != null){
                String status = item.getStatus().toString();
                if(status.equals("true")){
                    // not suitable
                    titleBackground = R.color.not_suitable;
                }
                else if(status.equals("false")){
                    // suitable
                    titleBackground = R.color.suitable;
                }
                else if(status.equals("maybe")){
                    titleBackground = R.color.maybe_suitable;
                }
                else
                    titleBackground = R.color.not_suitable;
                // description is existed or not
                Boolean visible = true;
                if(item.getDesc() == "" || item.getDesc() == null){
                    visible = false;
                }

                filterAdapter.addItem(titleBackground, visible,ContextCompat.getDrawable(this, R.drawable.expand), item.getTitle(), item.getDesc());
                filterAdapter.notifyDataSetChanged();
            }
        }
    }
}

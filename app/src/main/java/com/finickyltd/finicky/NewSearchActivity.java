package com.finickyltd.finicky;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.finickyltd.finicky.adapter.ListViewProductAdapter;
import com.finickyltd.finicky.api.APIService;
import com.finickyltd.finicky.api.APIUrl;
import com.finickyltd.finicky.helper.SharedPrefManager;
import com.finickyltd.finicky.models.ListViewProductItem;
import com.finickyltd.finicky.models.ManualSearchItems;
import com.finickyltd.finicky.models.SearchItem;

public class NewSearchActivity extends AppCompatActivity implements View.OnClickListener{
    private LinearLayout llGotoScanActivity, lnExactTab, lnExploreTab, llFilterSetting;
    private LinearLayout llNotFoundContainer;
    private ImageView ivBackArrow, ivGotoMenu;
    private EditText etSearchKeyword;
    private ListView listView;
    private ListViewProductAdapter listViewProductAdapter;
    private TextView tvExactTab, tvExploreTab, tvDescription, tvNotfoundTxt;
    private int id;
    public int filterType ;            // if O is showAll, 1 is showOnly Suitable, 2 is Show Maybe & Not suitable
    private String searchType;
    private Boolean isOnceSearched;   // if user try to search at leat once.
    public GlobalActivity globalActivity;
    public List<SearchItem> allItems, onlyItems, maybeOrNotItems;
    private ScrollView scrollView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_search);

        allItems = new ArrayList<SearchItem>();
        onlyItems = new ArrayList<SearchItem>();
        maybeOrNotItems = new ArrayList<SearchItem>();

        filterType = 0;
        searchType = "explore";
        isOnceSearched = false;

        TextView tx = (TextView)findViewById(R.id.hometitle);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Sriracha-Regular.ttf");
        tx.setTypeface(custom_font);



        // part for the text when result not found
        llNotFoundContainer = (LinearLayout) findViewById(R.id.llNotFoundContainer);
        tvNotfoundTxt = (TextView) findViewById(R.id.tvNotFoundTxt);
        tvNotfoundTxt.setTypeface(custom_font);

        // to load from preference
        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(getApplicationContext());
        id = sharedPrefManager.getUser().getId();

        etSearchKeyword = (EditText) findViewById(R.id.etSearchKeyword);
        etSearchKeyword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    String keyword = etSearchKeyword.getText().toString();
                    if(isOnceSearched == false)
                        isOnceSearched = true;
                    if(keyword.equals(null) || keyword.equals("")){
                        alertDialog("Search Error", "Please type keyword for Product Search");
                    }
                    else {
                        searchManually(searchType);
                    }
                }
                return false;
            }
        });

//        llGotoScanActivity = (LinearLayout) findViewById(R.id.llGotoScan);
//        llGotoScanActivity.setOnClickListener(this);

        llFilterSetting = (LinearLayout) findViewById(R.id.llFilterSetting);
        llFilterSetting.setOnClickListener(this);

        ivBackArrow = (ImageView) findViewById(R.id.ivBackArrow);
        ivBackArrow.setOnClickListener(this);
        ivGotoMenu = (ImageView) findViewById(R.id.ivGoToMenu);
        ivGotoMenu.setOnClickListener(this);

        lnExactTab = (LinearLayout) findViewById(R.id.lnExactTab);
        lnExactTab.setOnClickListener(this);

        lnExploreTab = (LinearLayout) findViewById(R.id.lnExploreTab);
        lnExploreTab.setOnClickListener(this);

        tvExactTab = (TextView) findViewById(R.id.tvselectExact);
        tvExploreTab = (TextView) findViewById(R.id.tvselectExplore);

        // to get ListView
        listView = (ListView) findViewById(R.id.lvProducts);
        listViewProductAdapter = new ListViewProductAdapter();
        listView.setAdapter(listViewProductAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListViewProductItem item = (ListViewProductItem) parent.getItemAtPosition(position);
                String productName = item.getProductName();
                String barcodeContent = item.getBarcodeContent();

                Intent intent = new Intent(getApplicationContext(), ProductActivity.class);
                intent.putExtra("barcodeContent", barcodeContent);
                startActivity(intent);
            }
        });

        scrollView = (ScrollView) findViewById(R.id.svProducts);

        tvDescription = (TextView) findViewById(R.id.tvDescription);

        // to show description and hide the ListView

        tvDescription.setText(R.string.exactDescription);
        tvExactTab.setBackground(getResources().getDrawable(R.color.green_app_color));
        //tvDescription.setVisibility(View.VISIBLE);
        listView.setVisibility(View.INVISIBLE);

        // image loader configuration
        DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration imageLoaderConfiguration = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(displayImageOptions)
                .build();
        ImageLoader.getInstance().init(imageLoaderConfiguration);

        // admob
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
    protected void onStop() {
        if(globalActivity.isbImgLoaded()){
            globalActivity.setbImgLoaded(false);
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if(globalActivity.isbImgLoaded()){
            globalActivity.setbImgLoaded(false);
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
//            case R.id.llGotoScan:
//                startActivity(new Intent(this, ZBarBarcodeScannerActivity.class));
//                break;
            case R.id.ivBackArrow:
                startActivity(new Intent(this, HomeActivity.class));
                break;
            case R.id.ivGoToMenu:
                startActivity(new Intent(this, MenuActivity.class));
                break;
            case R.id.llFilterSetting:
                showFilterDialog();
                break;
            case R.id.lnExactTab:
                //this.searchType = "exact";
                //searchTabClicked("exact");
                break;
            case R.id.lnExploreTab:
                //this.searchType = "explore";
                //searchTabClicked("explore");
                break;
        }
    }

    public void clearData(){

    }

    private void searchTabClicked(String searchType){
        if(searchType.equals("exact")){
            tvDescription.setText(R.string.exactDescription);
            tvExactTab.setBackground(getResources().getDrawable(R.color.green_app_color));
            tvExploreTab.setBackground(getResources().getDrawable(R.color.grey_app_color));
        }
        else {
            tvDescription.setText(R.string.exploreDescrition);
            tvExploreTab.setBackground(getResources().getDrawable(R.color.green_app_color));
            tvExactTab.setBackground(getResources().getDrawable(R.color.grey_app_color));
        }

        if(this.isOnceSearched){    // in case if user try to search at least once
            listView.setVisibility(View.VISIBLE);
            tvDescription.setVisibility(View.GONE);
        }
        else {              // user not try to search yet
            listView.setVisibility(View.INVISIBLE);
            tvDescription.setVisibility(View.VISIBLE);
        }
    }

    public void searchManually(String searchType){
        // to initialize the arrays for search items

        allItems.clear();
        onlyItems.clear();
        maybeOrNotItems.clear();

        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(etSearchKeyword.getWindowToken(), 0);

        String keyword = etSearchKeyword.getText().toString();
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Searching products...");
        progressDialog.show();
        progressDialog.setCancelable(false);

        llNotFoundContainer.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
        tvDescription.setVisibility(View.GONE);
        listViewProductAdapter.clearData();

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
        Call<ManualSearchItems> call = service.manualSearchWithKeyword(
                searchType,
                keyword,
                id
        );

        call.enqueue(new Callback<ManualSearchItems>() {
            @Override
            public void onResponse(Call<ManualSearchItems> call, Response<ManualSearchItems> response) {
                progressDialog.dismiss();
                if(response.body().getStaus()){
                    // to add the items to View
                    //Toast.makeText(getApplicationContext(), "Success to search products", Toast.LENGTH_SHORT).show();
                    List<SearchItem> items = response.body().getItems();

                    for (SearchItem tempItem :items){

                        allItems.add(tempItem);

                        switch (tempItem.getSuitableType()){
                            case 0:      // not suitable
                            case 1:
                                maybeOrNotItems.add(tempItem);
                                break;
                            case 2:
                                onlyItems.add(tempItem);
                                break;
                        }
                    }

                    switch (filterType){
                        case 0:
                            addItemsToView(items);
                            break;
                        case 1:
                            addItemsToView(onlyItems);
                            break;
                        case 2:
                            addItemsToView(maybeOrNotItems);
                            break;
                    }

                    listViewProductAdapter.notifyDataSetChanged();
                }
                else {
                    Log.d("search error", "Product Not Found");
                    //alertDialog("Alert", "No Results Found, Try Again with other keyword.");
                    screenHandler.sendEmptyMessage(1);
                }
            }

            @Override
            public void onFailure(Call<ManualSearchItems> call, Throwable t) {
                //Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
                alertDialog("Alert", "Network Connection Time out. Try Again later.");
                screenHandler.sendEmptyMessage(2);
            }
        });
    }

    private Handler screenHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 1){   // in case when no results found
                llNotFoundContainer.setVisibility(View.VISIBLE);
            }
            else if (msg.what == 2){  // in case when network connection time out
                llNotFoundContainer.setVisibility(View.VISIBLE);
            }
        }
    };

    public void addItemsToView(List<SearchItem> items){
        int cnt = 0;
        for (SearchItem item : items){
            cnt++;
            listViewProductAdapter.addItem(cnt, item.getProductName(), item.getBarcodeContent(), item.getSuitableType(), item.getProductSource());
        }
    }

    public  void  addItemToScrollView(List<SearchItem> items){
        int cnt = 0;
        for (SearchItem item : items){
            cnt++;
            //listViewProductAdapter.addItem(cnt, item.getProductName(), item.getBarcodeContent(), item.getSuitableType(), item.getProductSource());
            //LinearLayout linearLayout = (LinearLayout) findViewById()
        }
    }

    public void showFilterDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.search_filter_popup);

        final TextView tvShowAll = (TextView) dialog.findViewById(R.id.tvFiterShowAll);
        final TextView tvShowOnly = (TextView) dialog.findViewById(R.id.tvFilterOnlySuitable);
        final TextView tvShowMaybeNot = (TextView) dialog.findViewById(R.id.tvFiterMaybeNotSuitable);

        switch (filterType){
            case 0:
                tvShowAll.setTextColor(getResources().getColor(R.color.green_app_color));
                break;
            case 1:
                tvShowOnly.setTextColor(getResources().getColor(R.color.green_app_color));
                break;
            case 2:
                tvShowMaybeNot.setTextColor(getResources().getColor(R.color.green_app_color));
                break;
        }

        tvShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterType = 0;
                tvShowAll.setTextColor(getResources().getColor(R.color.green_app_color));
                tvShowOnly.setTextColor(getResources().getColor(R.color.bright_foreground_dark));
                tvShowMaybeNot.setTextColor(getResources().getColor(R.color.bright_foreground_dark));
                displayItemsByFilter();
                dialog.dismiss();
            }
        });

        tvShowOnly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterType = 1;
                tvShowOnly.setTextColor(getResources().getColor(R.color.green_app_color));
                tvShowAll.setTextColor(getResources().getColor(R.color.bright_foreground_dark));
                tvShowMaybeNot.setTextColor(getResources().getColor(R.color.bright_foreground_dark));
                displayItemsByFilter();
                dialog.dismiss();
            }
        });


        tvShowMaybeNot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterType = 2;
                tvShowMaybeNot.setTextColor(getResources().getColor(R.color.green_app_color));
                tvShowAll.setTextColor(getResources().getColor(R.color.bright_foreground_dark));
                tvShowMaybeNot.setTextColor(getResources().getColor(R.color.bright_foreground_dark));
                displayItemsByFilter();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void displayItemsByFilter(){
        if(etSearchKeyword.getText().length() > 0 && !allItems.isEmpty()){

            listViewProductAdapter.clearData();

            switch (filterType){
                case 0:
                    addItemsToView(allItems);
                    break;
                case 1:
                    addItemsToView(onlyItems);
                    break;
                case 2:
                    addItemsToView(maybeOrNotItems);
                    break;
            }

            listViewProductAdapter.notifyDataSetChanged();
        }
    }

    private void alertDialog(String title, String errTxt){
        final Dialog dialog = new Dialog(this);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.alert_login_error);
        TextView tvTitle = (TextView) dialog.findViewById(R.id.tvAlertTitle);
        TextView tvErrorText = (TextView) dialog.findViewById(R.id.tvErrorMsg);
        TextView tvDismiss = (TextView) dialog.findViewById(R.id.tvDismiss);
        tvTitle.setText(title);
        tvErrorText.setText(errTxt);
        tvDismiss.setText("Dismiss");

        ((TextView) dialog.findViewById(R.id.tvDismiss)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}

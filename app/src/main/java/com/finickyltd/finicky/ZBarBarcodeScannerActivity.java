package com.finickyltd.finicky;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.finickyltd.finicky.api.APIService;
import com.finickyltd.finicky.api.APIUrl;
import com.finickyltd.finicky.helper.SharedPrefManager;
import com.finickyltd.finicky.models.ProductResult;
import com.finickyltd.finicky.models.SearchResult;

public class ZBarBarcodeScannerActivity extends AppCompatActivity implements ZBarScannerView.ResultHandler, View.OnClickListener{
    private ZBarScannerView mScannerView;
    private LinearLayout llFlashOnOff, llBackArrow;
    private ImageView imageView, ivSearchManually;
    private boolean isFlashOn;
    private TextView tvflash;
    private EditText editTextManually;
    private int user_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zbar_barcode_scanner);

        TextView tx = (TextView)findViewById(R.id.hometitle);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Sriracha-Regular.ttf");
        tx.setTypeface(custom_font);

        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new ZBarScannerView(this);
        contentFrame.addView(mScannerView);

        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(getApplicationContext());
        user_id = sharedPrefManager.getUser().getId();

        llFlashOnOff = (LinearLayout) findViewById(R.id.llFlashOnOff);
        llFlashOnOff.setOnClickListener(this);
        isFlashOn = false;

        imageView = (ImageView) findViewById(R.id.ivFlash);
        tvflash = (TextView) findViewById(R.id.tvFlash);

        ivSearchManually = (ImageView) findViewById(R.id.ivSearchManually);
        ivSearchManually.setOnClickListener(this);

        llBackArrow = (LinearLayout) findViewById(R.id.llBackArrow);
        llBackArrow.setOnClickListener(this);

        editTextManually = (EditText) findViewById(R.id.etManualKeyword);
        editTextManually.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(editTextManually.getWindowToken(), 0);
                    searchManually();
                    return true;
                }
                return false;
            }
        });

        // to check camera permission
        checkCameraPermission();
    }

    public void onClick(View view)
    {
        switch (view.getId()){
            case R.id.llFlashOnOff:
                if(isFlashOn){
                    this.isFlashOn = false;
                    imageView.setImageResource(R.mipmap.flash_off);
                    tvflash.setText(R.string.flash_off);
                    mScannerView.setFlash(false);
                }
                else{
                    this.isFlashOn = true;
                    imageView.setImageResource(R.mipmap.flash_on);
                    tvflash.setText(R.string.flash_on);
                    mScannerView.setFlash(true);
                }
                break;
            case R.id.llBackArrow:
                finish();
                break;
            case R.id.ivSearchManually:
                searchManually();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void checkCameraPermission(){
        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);

        if(permissionCheck == PackageManager.PERMISSION_GRANTED){
            return;
        }
        requestPermissions(new String[]{Manifest.permission.CAMERA}, 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        /*
        Toast.makeText(this, "Contents = " + rawResult.getContents() + ", Format = " + rawResult.getBarcodeFormat().getName(), Toast.LENGTH_LONG).show();*/
        String barcodeContent =  rawResult.getContents();

        if(barcodeContent.length() < 13){
            int diviation = 13 - barcodeContent.length();

            String prefix = "";

            for(int i = 0; i < diviation; i++){
                prefix += "0";
            }

            barcodeContent = prefix + barcodeContent;
        }

        if(barcodeContent != null){
            this.onPause();
            getProduct(barcodeContent);
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //mScannerView.resumeCameraPreview(ZBarBarcodeScannerActivity.this);
            }
        }, 2000);
    }

    public void searchManually(){
        this.onPause();

        // to validate if keyword is empty or not

        String keyword = editTextManually.getText().toString();
        if(keyword == "" || keyword == null){
            Toast.makeText(getApplicationContext(), "Input the barcode manually", Toast.LENGTH_SHORT).show();
            return;
        }
        getProduct(keyword);
        editTextManually.setText(null);
    }

    public void getProduct(final String barcodeContent){
        ProductResult productResult = new ProductResult();

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Searching Up product with barcode");
        progressDialog.show();
        progressDialog.setCancelable(false);
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

        //Defining the user object as we need to pass it with the call

        Call<SearchResult> call = service.searchProductWithBarcode(
                barcodeContent
        );
        //calling the api
        call.enqueue(new Callback<SearchResult>() {
            @Override
            public void onResponse(Call<SearchResult> call, Response<SearchResult> response) {

                //hiding progress dialog
                progressDialog.dismiss();

                //displaying the message from the response as toast
                boolean isFound = response.body().getStatus();
                //Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                if(isFound){
                    String productName = response.body().getProductName();
                    gotoResultActivity(barcodeContent, productName);
                }
                else {
                    onResume();
                    alertDialog("Alert", "This product is not found in our database.");
                    addNotFoundProduct(user_id, barcodeContent);
                }
            }

            @Override
            public void onFailure(Call<SearchResult> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                onResume();
            }
        });
    }

    public void addNotFoundProduct(int id, String barcodeContent){

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

        Call<com.finickyltd.finicky.models.Result> call = service.addNotFoundProduct(
                user_id,
                barcodeContent
        );

        call.enqueue(new Callback<com.finickyltd.finicky.models.Result>() {
            @Override
            public void onResponse(Call<com.finickyltd.finicky.models.Result> call, Response<com.finickyltd.finicky.models.Result> response) {
                //Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG);
                if(response.body().getError()){
                    //alertDialog("Alert", "Failed to save product not found to server.");
                }
                else {
                    //alertDialog("Alert", "Success to save this product to server");
                }
            }

            @Override
            public void onFailure(Call<com.finickyltd.finicky.models.Result> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG);
            }
        });
    }

    public void gotoResultActivity(String barcodeContent, String productName){
        Intent intent = new Intent(this, ProductActivity.class);
        intent.putExtra("barcodeContent", barcodeContent);
        intent.putExtra("product_name", productName);
        intent.putExtra("isFromScan", true);
        startActivity(intent);
    }

    private void alertDialog(String title, String errTxt){
        final Dialog dialog = new Dialog(this);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.alert_login_error);

        TextView tvTitle = (TextView) dialog.findViewById(R.id.tvAlertTitle);
        TextView tvErrorText = (TextView) dialog.findViewById(R.id.tvErrorMsg);
        TextView tvDismiss = (TextView) dialog.findViewById(R.id.tvDismiss);
        tvDismiss.setText("Dismiss");
        tvTitle.setText(title);
        tvErrorText.setText(errTxt);

        ((TextView) dialog.findViewById(R.id.tvDismiss)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
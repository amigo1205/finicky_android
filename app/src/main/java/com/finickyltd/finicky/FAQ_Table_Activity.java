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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.finickyltd.finicky.api.APIService;
import com.finickyltd.finicky.api.APIUrl;
import com.finickyltd.finicky.models.FaqQuesData;
import com.finickyltd.finicky.models.QuestionData;

public class FAQ_Table_Activity extends AppCompatActivity implements View.OnClickListener {

    private Spinner spinner1, spinner2, spinner3;
    public final static String CATEGORY_1 = "General";
    public final static String CATEGORY_2 = "Specially";
    public final static String CATEGORY_3 = "Extremely";
    private int spinnerWidth;
    public List<QuestionData> questionDataList, dataList1, dataList2, dataList3;
    private LinearLayout llGotoMenu, llBackArrow;
    private TextView tvAnswerData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq_table);

        spinnerWidth = 100;
        questionDataList = new ArrayList<QuestionData>();
        dataList1 = new ArrayList<QuestionData>();
        dataList2 = new ArrayList<QuestionData>();
        dataList3 = new ArrayList<QuestionData>();

        TextView tx = (TextView)findViewById(R.id.hometitle);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Sriracha-Regular.ttf");
        tx.setTypeface(custom_font);

        llGotoMenu = (LinearLayout) findViewById(R.id.llGoToMenu);
        llGotoMenu.setOnClickListener(this);
        llBackArrow = (LinearLayout) findViewById(R.id.llBackArrow) ;
        llBackArrow.setOnClickListener(this);

        tvAnswerData = (TextView) findViewById(R.id.tvAnswerTextview);

        spinner1 = (Spinner) findViewById(R.id.spinCategory1);
        spinner2 = (Spinner) findViewById(R.id.spinCategory2);
        spinner3 = (Spinner) findViewById(R.id.spinCategory3);

        getQuestionDataFromServer();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.llGoToMenu:
                startActivity(new Intent(this, MenuActivity.class));
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

    public void getQuestionDataFromServer(){

        questionDataList.clear();
        dataList1.clear();
        dataList2.clear();
        dataList3.clear();

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Data...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(3, TimeUnit.MINUTES)
                .connectTimeout(3, TimeUnit.MINUTES)
                .writeTimeout(3, TimeUnit.MINUTES)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIUrl.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIService service = retrofit.create(APIService.class);
        Call<FaqQuesData> call = service.loadQuesdata(1);
        call.enqueue(new Callback<FaqQuesData>() {
            @Override
            public void onResponse(Call<FaqQuesData> call, Response<FaqQuesData> response) {
                Boolean error = response.body().getError();

                if(error){  // error occured.
                    alertDialog("Alert", response.body().getMessage());
                }
                else {      // success to load question data
                    for(QuestionData item : response.body().getQuesData()){
                        questionDataList.add(item);

                        if(item.getCategory().equals(CATEGORY_1)){
                            dataList1.add(item);
                        }
                        else if(item.getCategory().equals(CATEGORY_2)){
                            dataList2.add(item);
                        }
                        else if(item.getCategory().equals(CATEGORY_3)){
                            dataList3.add(item);
                        }
                        else {
                            dataList1.add(item);
                        }

                    }

                    if(questionDataList.size() > 0){
                        questionHandler.sendEmptyMessage(1);
                    }
                    else {
                        alertDialog("Alert", "Sorry, FAQ part is being update, It is stoped shortly.");
                    }
                }

                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<FaqQuesData> call, Throwable t) {
                alertDialog("Alert", "Network Connection Time out.");
                progressDialog.dismiss();
            }
        });
    }

    private Handler questionHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 1){   // load question data
                loadQuestions();
            }
            else if (msg.what == 2){

            }
        }
    };

    public void loadQuestions(){
        int length_1 = 0, length_2 = 0, length_3 = 0;
        int index_1 = 0, index_2 = 0, index_3 = 0;
        String[] array_cat1, array_cat2, array_cat3;
        array_cat1 = new String[10];

        for(QuestionData item : questionDataList){
            if(item.getCategory().equals(CATEGORY_1)){
                length_1++;
            }
            else if (item.getCategory().equals(CATEGORY_2)){
                length_2++;
            }
            else if (item.getCategory().equals(CATEGORY_3)){
                length_3++;
            }
            else {
                length_1++;
            }
        }

        array_cat1 = new String[length_1];
        array_cat2 = new String[length_2];
        array_cat3 = new String[length_3];

        for(QuestionData item : questionDataList){
            if(item.getCategory().equals(CATEGORY_1)){
                array_cat1[index_1] = item.getQuestionData();
                index_1++;
            }
            else if (item.getCategory().equals(CATEGORY_2)){
                array_cat2[index_2] = item.getQuestionData();
                index_2++;
            }
            else if (item.getCategory().equals(CATEGORY_3)){
                array_cat3[index_3] = item.getQuestionData();
                index_3++;
            }
            else {
                array_cat1[index_1] = item.getQuestionData();
                index_1++;
            }
        }

        if(array_cat1.length >0 ){
            ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this
                    , android.R.layout.simple_spinner_item, array_cat1);
            adapter1.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
            spinner1.setAdapter(adapter1);
        }

        if(array_cat2.length > 0){
            ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this
                    , android.R.layout.simple_spinner_item, array_cat2);
            adapter2.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
            spinner2.setAdapter(adapter2);
        }

        if(array_cat3.length > 0){
            ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this
                    , android.R.layout.simple_spinner_item, array_cat3);
            adapter3.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
            spinner3.setAdapter(adapter3);
        }

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                spinnerWidth = spinner2.getWidth();
                spinner1.setDropDownWidth(spinnerWidth);
                spinner2.setDropDownWidth(spinnerWidth);
                spinner3.setDropDownWidth(spinnerWidth);
            }
        }, 500);

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.v("item clicked", (String) parent.getItemAtPosition(position));
                displayAnswer(1, position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.v("item clicked", (String) parent.getItemAtPosition(position));
                displayAnswer(2, position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.v("item clicked", (String) parent.getItemAtPosition(position));
                displayAnswer(3, position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void displayAnswer(int category_type, int index){
        String answerTxt = "";
        switch (category_type){
            case 1:
                answerTxt = dataList1.get(index).getAnswerData();
                break;
            case 2:
                answerTxt = dataList2.get(index).getAnswerData();
                break;
            case 3:
                answerTxt = dataList3.get(index).getAnswerData();
                break;
        }

        tvAnswerData.setText(answerTxt);
    }

    private void alertDialog(String title, String errTxt){
        final Dialog dialog = new Dialog(this);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.alert_login_error);

        TextView tvTitle = (TextView) dialog.findViewById(R.id.tvAlertTitle);
        tvTitle.setText(title);

        TextView tvDismiss = (TextView) dialog.findViewById(R.id.tvDismiss);
        tvDismiss.setText("Dismiss");

        TextView tvErrorText = (TextView) dialog.findViewById(R.id.tvErrorMsg);
        tvErrorText.setText(errTxt);

        ((TextView) dialog.findViewById(R.id.tvDismiss)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
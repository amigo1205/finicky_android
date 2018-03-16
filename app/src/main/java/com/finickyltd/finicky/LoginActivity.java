package com.finickyltd.finicky;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;
import java.util.Arrays;
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
import com.finickyltd.finicky.models.FBResult;
import com.finickyltd.finicky.models.Result;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText editTextMail, editTextPassword;
    private CallbackManager callbackManager;
    public String membership_type;
    public GlobalActivity globalActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        globalActivity = (GlobalActivity) getApplicationContext();
        membership_type = "LITE";

        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_login);

        //disconnectFromFacebook();

        this.callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
        //loginButton.setOnClickListener(this);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                GraphRequest request;
                request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                        if(graphResponse.getError() != null){
                            Log.d("status", "error occured");
                        }
                        else {
                            Log.i("TAG", "user:" + jsonObject.toString());
                            Log.i("TAG", "AccessToken :" + loginResult.getAccessToken().getToken());
                            setResult(RESULT_OK);
                            // to save facebook id to shared preferences.
                            String fb_id = loginResult.getAccessToken().getUserId();
                            if(fb_id != null && fb_id.length() > 0)
                                facebookLogin(fb_id);
                            //finish();
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, name, email, gender, birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.d("login cancel", "");
            }

            @Override
            public void onError(FacebookException e) {
                Log.d("LoginErr", e.toString());
            }
        });

        findViewById(R.id.tvForgotPassword).setOnClickListener(this);
        editTextMail = (EditText) findViewById(R.id.etuseremail);
        editTextPassword = (EditText) findViewById(R.id.etuserPassword);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.login_button:
                //onLoginFacebook();
                break;
            case R.id.tvForgotPassword:
                forgetPassDialog();
                break;
        }
    }

    public void facebookLogin(String fb_id){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing in with facebook account...");
        progressDialog.show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIUrl.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIService service = retrofit.create(APIService.class);

        Call<FBResult> call = service.facebookLogin(fb_id);
        call.enqueue(new Callback<FBResult>() {
            @Override
            public void onResponse(Call<FBResult> call, Response<FBResult> response) {
                progressDialog.dismiss();

                if(response.body().getError() == false){
                    // to sharedpreference id to
                    //Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(getApplicationContext());
                    sharedPrefManager.fbUserLogin(response.body().getFbUser());
                    globalActivity.setMembership_type(response.body().getFbUser().getMembership_type());
                    if(response.body().getFilter()){
                        sharedPrefManager.writeIsFilterSetting();
                        gotoHomeActivity();
                    }
                    else {
                        gotoFilterSetting();
                    }
                }
                else{
                    //disconnectFromFacebook();
                    alertDialog("LoginError", response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<FBResult> call, Throwable t) {
                progressDialog.dismiss();
                alertDialog("LoginError","Network Connection Time out.");
            }
        });
    }

    public void forgetPassDialog() {
        final Dialog dialog = new Dialog(this);
        //dialog.setCancelable(true);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.forget_password_dialog);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        ((EditText) dialog.findViewById(R.id.etforgetpassEmail)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Lato-Regular.ttf"));
        ((Button) dialog.findViewById(R.id.bEmailSubmit)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText editText = (EditText) dialog.findViewById(R.id.etforgetpassEmail);
                String email = editText.getText().toString();
                if(email.matches("")  || !isEmailValid(email))
                    Toast.makeText(getApplicationContext(), "Please enter e-mail address", Toast.LENGTH_SHORT).show();
                else {
                    if(isOnline()){
                        submitEmailForResetPass(email);
                        dialog.dismiss();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "You are offline", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        dialog.show();
    }
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public void submitEmailForResetPass(String email){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sending email...");
        progressDialog.show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIUrl.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIService service = retrofit.create(APIService.class);
        Call<Result> call = service.sendEmailForResetPassword(email);
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                String msg = "";
                progressDialog.dismiss();
                if(!response.body().getError()){
                    msg = "We 've sent you an email with instructions on how to reset your password.";
                    alertDialog("EmailSentSuccess", msg);
                }
                else {
                    msg = "Network error, try again with correct email address";
                    alertDialog("EmailSentError", msg);
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Log.d("mailException", t.getMessage());
                progressDialog.dismiss();
                alertDialog("EmailSentError", "Network Connection Time out.");
            }
        });
    }

    public void goNextPage(View view){
        switch (view.getId()){
            case R.id.bLogin:
                Log.d("log in ", "clicked");
                if(!getValidateField()){
                    return;
                }
                if(isEmailValid(this.editTextMail.getText().toString())){
                    userLogin();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Enter a Correct Email Address", Toast.LENGTH_LONG).show();
                    //this.editTextMail.setError("Please insert correct email");
                }
                return;
            case R.id.bSignUp:
                startActivity(new Intent(this, SignUpActivity.class));
                break;
        }
    }

    public void userLogin() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing In...");
        progressDialog.show();
        progressDialog.setCancelable(false);

        final String email = editTextMail.getText().toString().trim().toLowerCase();
        String password = editTextPassword.getText().toString().trim();

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

        Call<Result> call = service.userLogin(email, password);

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                progressDialog.dismiss();
                if (!response.body().getError()) {
                    finish();
                    SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(getApplicationContext());
                    sharedPrefManager.userLogin(response.body().getUser());
                    globalActivity.setMembership_type(response.body().getUser().getMembership_type());
                    if(response.body().getFilter()){
                        sharedPrefManager.writeIsFilterSetting();
                        //startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        gotoHomeActivity();
                    }
                    else
                        startActivity(new Intent(getApplicationContext(), FilterSettingActivity.class));
                } else {
                    //Toast.makeText(getApplicationContext(), "Invalid email or password", Toast.LENGTH_LONG).show();
                    String errTxt = "You entered an invalid email or password. Please re-enter.";
                    alertDialog("LoginError", errTxt);
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                progressDialog.dismiss();
                alertDialog("LoginError", "Network Connection Time out.");
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
        if(type.equals("LoginError") || type.equals("Security and Privacy")){
            tvTitle.setText("Alert");
            tvDismiss.setText("Dismiss");
        }
        else{
            if(type.equals("EmailSentSuccess")){
                tvTitle.setText("Your Info Has Been Sent");
            }
            else
                tvTitle.setText("Your Info Has Not Been Sent");

            tvDismiss.setText("OK");

        }


        ((TextView) dialog.findViewById(R.id.tvDismiss)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void disconnectFromFacebook() {
        if (AccessToken.getCurrentAccessToken() == null) {
            return; // already logged out
        }

        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                .Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {

                LoginManager.getInstance().logOut();

            }
        }).executeAsync();
    }

    public void gotoHomeActivity(){
        startActivity(new Intent(this, HomeActivity.class));
        LoginActivity.this.finish();
        //this.finish();
    }

    public  void  gotoFilterSetting(){
        startActivity(new Intent(this, FilterSettingActivity.class));
        finish();
    }

    protected  void  onActivityResult(int requestCode, int resultCode, Intent data){
        Log.d("activity result ", String.format("%s , %s", String.valueOf(requestCode), String.valueOf(resultCode)));
        super.onActivityResult(requestCode, resultCode, data);
        this.callbackManager.onActivityResult(requestCode, resultCode, data);
        //disconnectFromFacebook();
    }

    public boolean isEmailValid(String email) {
        if (email == null) {
            return false;
        }
        //return Patterns.EMAIL_ADDRESS.matcher(email).matches();

        String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\\.+[a-z]+";
        boolean isValid = false;
        // onClick of button perform this simplest code.
        if (email.matches(emailPattern))
        {
            isValid = true;
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Invalid email address", Toast.LENGTH_SHORT).show();
        }

        return isValid;
    }

    private boolean getValidateField() {
        if (this.editTextMail.getText().toString().length() == 0 || this.editTextMail.getText().toString().equals("")) {
            alertDialog("Security and Privacy", "Enter a Correct Email Address");
            return false;
        } else if (this.editTextPassword.getText().toString().length() == 0 || this.editTextPassword.getText().toString().equals("")) {
            alertDialog("Security and Privacy", "Enter a Password with at least 6 Characters");
            return false;
        } else if (this.editTextPassword.getText().toString().length() >= 6) {
            return true;
        } else {
            alertDialog("Security and Privacy", "Enter a Email and Password correctly");
            return false;
        }
    }
}




package com.finickyltd.finicky;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.Callback;
import retrofit2.converter.gson.GsonConverterFactory;
import com.finickyltd.finicky.api.APIService;
import com.finickyltd.finicky.api.APIUrl;
import com.finickyltd.finicky.helper.SharedPrefManager;
import com.finickyltd.finicky.models.FBResult;
import com.finickyltd.finicky.models.FbUser;
import com.finickyltd.finicky.models.Result;

public class SignUpActivity extends AppCompatActivity  implements View.OnClickListener{
    public EditText editTextName, editTextEmail, editTextPassword;
    public Button btnSignUp, btnLogin;
    private CallbackManager callbackManager;
    private LoginButton loginButton;
    public GlobalActivity globalActivity;
    private static String membership_type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        super.onCreate(savedInstanceState);

        membership_type = "";

        setContentView(R.layout.activity_sign_up);
        editTextName = (EditText) findViewById(R.id.etuser_name);
        editTextEmail = (EditText) findViewById(R.id.etuser_email);
        editTextPassword = (EditText) findViewById(R.id.etuser_password);
        btnSignUp = (Button) findViewById(R.id.bSignUp);
        btnLogin = (Button) findViewById(R.id.bLogin);
        btnSignUp.setOnClickListener(this);
        btnLogin.setOnClickListener(this);

        globalActivity = (GlobalActivity) getApplicationContext() ;

        //disconnectFromFacebook();


        this.callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                GraphRequest request;
                request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject user, GraphResponse response) {
                        if (response.getError() != null) {

                        } else {
                            Log.i("TAG", "user: " + user.toString());
                            Log.i("TAG", "AccessToken: " + loginResult.getAccessToken().getToken());
                            setResult(RESULT_OK);
                            // to save fb to server
                            try {
                                String fb_name = user.getString("name");
                                String fb_id = loginResult.getAccessToken().getUserId();
                                String fb_email = "unknown";
                                if(user.has("email")){
                                    fb_email = user.getString("email");
                                }

                                registerFacebookUser(fb_email, fb_id, fb_name);
                            }
                            catch (Exception e){
                                Log.d("json object error", e.toString());
                            }
                            //finish();
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.d("user cancelled", "facebook login cancel");
            }

            @Override
            public void onError(FacebookException e) {
                Log.d("Facebook exception", e.toString());
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void  onClick(View view){
        switch (view.getId()){
            case R.id.bSignUp:
                if(!getValidateField()){
                    return;
                }
                if(isEmailValid(this.editTextEmail.getText().toString())){
                    userSignUp();
                }
                else {
                    alertDialog("Security and Privacy", "Please Enter a Correct e-mail");
                }
                break;
            case R.id.bLogin:
                startActivity(new Intent(this, LoginActivity.class));
                return;
        }
    }

    public void registerFacebookUser(String fb_email, String fb_id, String fb_name){
        Log.d("fb_register", "");
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing Up with facebook account...");
        progressDialog.show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIUrl.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIService service = retrofit.create(APIService.class);

        Call<FBResult> call = service.createUserWithFB(fb_email, fb_id, fb_name);
        call.enqueue(new Callback<FBResult>() {
            @Override
            public void onResponse(Call<FBResult> call, Response<FBResult> response) {
                progressDialog.dismiss();
                if(response.body().getError() == false){
                    //Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(getApplicationContext());
                    FbUser fbUser = new FbUser(response.body().getFbUser().getId(), response.body().getFbUser().getFb_id(), response.body().getFbUser().getFb_name());
                    sharedPrefManager.fbUserLogin(fbUser);
                    membership_type = response.body().getFbUser().getMembership_type();
                    handler.sendEmptyMessage(2);
                    gotoFiterSettingActivity();
                }
                else if (response.body().getMessage().equals("This email already exists, please try with other facebook mail")){
                    Toast.makeText(getApplicationContext(), "Email address already in use.", Toast.LENGTH_SHORT).show();
                    SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(getApplicationContext());
                    FbUser fbUser = new FbUser(response.body().getFbUser().getId(), response.body().getFbUser().getFb_id(), response.body().getFbUser().getFb_name());
                    sharedPrefManager.fbUserLogin(fbUser);
                    goHomeActivity();
                }
                else if(response.body().getMessage().equals("Some error occurred")){
                    alertDialog("Sign up Error", "This email might already be used.");
                }
                else {
                    //disconnectFromFacebook();
                    alertDialog("Signup Error", response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<FBResult> call, Throwable t) {
                alertDialog("Signup Error", "Network Connection Time out.");
                progressDialog.dismiss();
            }
        });
    }

    public void gotoFiterSettingActivity(){
        startActivity(new Intent(this, FilterSettingActivity.class));
        finish();
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

    protected  void  onActivityResult(int requestCode, int resultCode, Intent data){
        Log.d("activity result ", String.format("%s , %s", String.valueOf(requestCode), String.valueOf(resultCode)));
        super.onActivityResult(requestCode, resultCode, data);
        this.callbackManager.onActivityResult(requestCode, resultCode, data);
        //disconnectFromFacebook();
    }

    private void userSignUp() {

        //defining a progress dialog to show while signing up
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing Up...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        //getting the user values
        String name = editTextName.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim().toLowerCase();
        String password = editTextPassword.getText().toString().trim();

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
        Call<Result> call = service.createUser(
                name,
                email,
                password
        );
        //calling the api
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {

                //hiding progress dialog
                progressDialog.dismiss();

                //displaying the message from the response as toast
                String responseMsg = response.body().getMessage();
                if(responseMsg.equals("Registered successfully")){
                    //Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(getApplicationContext());
                    sharedPrefManager.userLogin(response.body().getUser());

                    membership_type = response.body().getUser().getMembership_type();
                    handler.sendEmptyMessage(1);
                    goNextPage(email);
                }
                else if (responseMsg.equals("This email already exist, please try with other mail")){
                    // show alert for account exists
                    SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(getApplicationContext());
                    sharedPrefManager.userLogin(response.body().getUser());
                    alertAccountExists("Email address already in use.");
                }
                else if(responseMsg.equals("Failed to register you, Other might register with your email.")){
                    alertDialog("Sign up Error", "This email might be already used as Facebook email.");
                }
                else {
                    alertDialog("Signup Error", response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                progressDialog.dismiss();
                alertDialog("Sign up Error", "Network Connection Time out.");
            }
        });
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 1){        // in case from ordinary email
                globalActivity.setMembership_type(membership_type);
            }
            else if (msg.what == 2){   // in case from facebook
                globalActivity.setMembership_type(membership_type);
            }
        }
    };

    public void goNextPage(String email){
        Intent intent = new Intent(this, FilterSettingActivity.class);
        intent.putExtra("email", email);
        startActivity(intent);
        finish();
    }

    public void goLoginActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void goHomeActivity(){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        SignUpActivity.this.finish();
    }

    private boolean getValidateField() {
        if (this.editTextName.getText().toString().length() == 0 || this.editTextName.getText().toString().equals("")) {
            alertDialog("Security and Privacy", "Enter a name with at least 3 Characters");
            return false;
        } else if (this.editTextEmail.getText().toString().length() == 0 || this.editTextEmail.getText().toString().equals("")) {
            alertDialog("Security and Privacy", "Enter a Correct Email Address");
            return false;
        } else if (this.editTextPassword.getText().toString().length() == 0 || this.editTextPassword.getText().toString().equals("")) {
            alertDialog("Security and Privacy", "Enter a Password with at least 6 Characters");
            return false;
        } else if (this.editTextPassword.getText().toString().length() >= 6) {
            return true;
        } else {
            alertDialog("Security and Privacy", "Enter a Password with at least 6 Characters");
            return false;
        }
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
            alertDialog("Security and Privacy", "Enter a Correct Email Address");
        }
        return isValid;
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

    private void alertAccountExists(String errTxt){
        final Dialog dialog = new Dialog(this);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.alert_email_sent);

        TextView tvErrorText = (TextView) dialog.findViewById(R.id.tvErrorMsg);
        tvErrorText.setText(errTxt);

        ((TextView) dialog.findViewById(R.id.tvCancel)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        ((TextView) dialog.findViewById(R.id.tvOk)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goLoginActivity();
            }
        });
        dialog.show();
    }
}

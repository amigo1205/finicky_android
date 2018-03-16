package com.finickyltd.finicky.api;

import com.finickyltd.finicky.models.FBResult;
import com.finickyltd.finicky.models.FaqQuesData;
import com.finickyltd.finicky.models.FilterResult;
import com.finickyltd.finicky.models.ManualSearchItems;
import com.finickyltd.finicky.models.MessageResponse;
import com.finickyltd.finicky.models.Messages;
import com.finickyltd.finicky.models.ProductResult;
import com.finickyltd.finicky.models.Result;
import com.finickyltd.finicky.models.SearchResult;
import com.finickyltd.finicky.models.Users;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface APIService {
    @FormUrlEncoded
    @POST("register")
    Call<Result> createUser(
            @Field("name") String name,
            @Field("email") String email,
            @Field("password") String password);

    @FormUrlEncoded
    @POST("registerWithFB")
    Call<FBResult> createUserWithFB(
            @Field("email") String fb_email,
            @Field("fb_id") String fb_id,
            @Field("fb_name") String fb_name
    );

    @FormUrlEncoded
    @POST("login")
    Call<Result> userLogin(
            @Field("email") String email,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("facebookLogin")
    Call<FBResult> facebookLogin(
            @Field("fb_id") String fb_id
    );

    @FormUrlEncoded
    @POST("registerFilterSetting")
    Call<Result> registerFilterSetting(
            @Field("vegan") String vegan,
            @Field("crustacean") String crustacean,
            @Field("mollusc") String mollusc,
            @Field("treenut") String treenut,
            @Field("fish") String fish,
            @Field("gluten") String gluten,
            @Field("dairy") String dairy,
            @Field("celery") String celery,
            @Field("lupin") String lupin,
            @Field("sesame") String sesame,
            @Field("sulphites") String sulphites,
            @Field("egg") String egg,
            @Field("soya") String soya,
            @Field("peanut") String peanut,
            @Field("mustard") String mustard,
            @Field("honey") String honey,
            @Field("palmOil") String pamlOil,
            @Field("sugar") String sugar,
            @Field("id") int id
    );


    @FormUrlEncoded
    @POST("changeFilterSetting")
    Call<Result> changeFilterSetting(
            @Field("vegan") String vegan,
            @Field("crustacean") String crustacean,
            @Field("mollusc") String mollusc,
            @Field("treenut") String treenut,
            @Field("fish") String fish,
            @Field("gluten") String gluten,
            @Field("dairy") String dairy,
            @Field("celery") String celery,
            @Field("lupin") String lupin,
            @Field("sesame") String sesame,
            @Field("sulphites") String sulphites,
            @Field("egg") String egg,
            @Field("soya") String soya,
            @Field("peanut") String peanut,
            @Field("mustard") String mustard,
            @Field("honey") String honey,
            @Field("palmOil") String pamlOil,
            @Field("sugar") String sugar,
            @Field("id") int id
    );

    @FormUrlEncoded
    @POST("productNotFound")
    Call<Result> addNotFoundProduct(
            @Field("user_id") int user_id,
            @Field("barcodeContent") String barcodeContent
    );

    @FormUrlEncoded
    @POST("searchProductWithBarcode")
    Call<SearchResult> searchProductWithBarcode(
            @Field("barcodeContent") String barcodeContent
    );

    @FormUrlEncoded
    @POST("getProductWithBarcode")
    Call<ProductResult> getProductWithBarcode(
            @Field("barcodeContent")  String barcodeContent,
            @Field("id") int id
    );

    @FormUrlEncoded
    @POST("searchManually")
    Call<ManualSearchItems> manualSearchWithKeyword(
            @Field("searchType") String searchType,
            @Field("keyword") String keyword,
            @Field(("id")) int id
    );

    @FormUrlEncoded
    @POST("updatePassword/{id}")
    Call<Result> updateUserPassword(
            @Path("id") int id,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("getUserOfFilters")
    Call<FilterResult> getUserOfFilters(
            @Field("id") int id
    );

    @FormUrlEncoded
    @POST("getPaymentStatus")
    Call<Result> getPaymentStatus(
            @Field("id") int id
    );

    @FormUrlEncoded
    @POST("sendEmail")
    Call<Result> sendEmailForResetPassword(
            @Field("email") String email
    );

    @FormUrlEncoded
    @POST("verifyEmail")
    Call<Result> sendEmailForVefify(
            @Field("email") String email
    );

    @FormUrlEncoded
    @POST("reportProduct")
    Call<Result> reportProduct(
            @Field("userid") int id,
            @Field("barcodeContent") String barcodeContent,
            @Field("category") String category,
            @Field("content") String content
    );

    @FormUrlEncoded
    @POST("contactUs")
    Call<Result> contactUs(
            @Field("user_id") int id,
            @Field("subject") String subject,
            @Field("message") String message
    );

    @FormUrlEncoded
    @POST("subscribe")
    Call<Result> userSubscribe(
            @Field("user_id") int user_id,
            @Field("authorization_code") String authCode,
            @Field("metadata_id") String metadata_id,
            @Field("period") String period
    );

    @FormUrlEncoded
    @POST("updateAutoRenewal")
    Call<Result> updateAutoRenewal(
            @Field("user_id") int user_id,
            @Field("isEnabled") int isEnabled
    );

    @FormUrlEncoded
    @POST("loadQuestionData")
    Call<FaqQuesData> loadQuesdata(
            @Field("id") int id
    );

    // --------------------------- default part ----------------------------------//
    @GET("users")
    Call<Users> getUsers();

    @FormUrlEncoded
    @POST("sendmessage")
    Call<MessageResponse> sendMessage(
            @Field("from") int from,
            @Field("to") int to,
            @Field("title") String title,
            @Field("message") String message);

    //getting messages
    @GET("messages/{id}")
    Call<Messages> getMessages(@Path("id") int id);
}

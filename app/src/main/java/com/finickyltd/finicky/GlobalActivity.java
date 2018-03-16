package com.finickyltd.finicky;

import android.app.Application;

/**
 * Created by common on 10/13/2017.
 */

public class GlobalActivity extends Application {
    public String membership_type;
    public boolean bImgLoaded;

    public GlobalActivity(){
        membership_type = "";
        bImgLoaded = false;
    }

    @Override
    public void onCreate() {
        membership_type = "";
        bImgLoaded = false;
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public String getMembership_type() {
        return membership_type;
    }

    public void setMembership_type(String membership_type) {
        this.membership_type = membership_type;
    }

    public boolean isbImgLoaded(){
        return this.bImgLoaded;
    }

    public void setbImgLoaded(boolean loaded){
        this.bImgLoaded = loaded;
    }

}

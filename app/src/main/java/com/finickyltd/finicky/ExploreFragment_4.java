package com.finickyltd.finicky;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by common on 8/22/2017.
 */

public class ExploreFragment_4 extends Fragment {
    Context mContext;
    public ExploreFragment_4(){

    }

    public Context getmContext() {
        return mContext;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore_4, container, false);
        return view;
    }
}

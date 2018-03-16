package com.finickyltd.finicky.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.finickyltd.finicky.ExploreFragment_1;
import com.finickyltd.finicky.ExploreFragment_2;
import com.finickyltd.finicky.ExploreFragment_3;
import com.finickyltd.finicky.ExploreFragment_4;

public class ExplorerPagerAdapter extends FragmentPagerAdapter {

    public ExplorerPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                return new ExploreFragment_1();
            case 1:
                return new ExploreFragment_2();
            case 2:
                return new ExploreFragment_3();
            case 3:
                return new ExploreFragment_4();
        }
        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 4;
    }

}

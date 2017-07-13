package com.example.bckim.tabwithnavi;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

/**
 * Created by bckim on 2017-06-13.
 */

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public static final String TAG = "gandhi";

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).

        Log.d(TAG, Integer.toString(position));

        if(position == 0)
            return new PlaceholderFragment1().newInstance(position + 1);
        else if(position == 1)
            return new PlaceholderFragment2().newInstance(position + 1);
        else if(position == 2)
            return new PlaceholderFragment3().newInstance(position + 1);
        return null;
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "SECTION 1";
            case 1:
                return "SECTION 2";
            case 2:
                return "SECTION 3";
        }
        return null;
    }
}
package com.example.bckim.tabwithnavi;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.SystemClock;

class CustomLoader extends AsyncTaskLoader<String> {
    public CustomLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();

        forceLoad();
    }

    @Override
    public String loadInBackground() {
        return loadData();
    }

    private String loadData() {
        SystemClock.sleep(2000);
        return "Hello World";
    }
}
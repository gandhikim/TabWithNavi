package com.example.bckim.tabwithnavi;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.api.services.youtube.model.PlaylistItem;

import java.util.Iterator;


public class PlaceholderFragment2 extends Fragment implements LoaderManager.LoaderCallbacks<Iterator<PlaylistItem>> {

    public static final String TAG = "gandhi";

    private static final String ARG_SECTION_NUMBER = "section_number";

    private CustomAdapter2 adapter;

    private ListView listView;

    public PlaceholderFragment2() {}

    public static PlaceholderFragment2 newInstance(int sectionNumber) {
        PlaceholderFragment2 fragment = new PlaceholderFragment2();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        Log.d(TAG,"222");
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main2, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));

        adapter = new CustomAdapter2();
        listView = (ListView) rootView.findViewById(R.id.listView);

        setData();

        listView.setAdapter(adapter);

        return rootView;
    }

    private void setData() {

        MyLoader mu = new MyLoader(this.getContext());
        //mu.reqData();

        TypedArray arrResId = getResources().obtainTypedArray(R.array.resId);
        String[] titles = getResources().getStringArray(R.array.title);
        String[] contents = getResources().getStringArray(R.array.content);

        for (int i = 0; i < arrResId.length(); i++) {
            CustomDTO2 dto = new CustomDTO2();

            dto.setTitle(titles[i]);


            adapter.addItem(dto);
        }
    }


    @Override
    public Loader<Iterator<PlaylistItem>> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Iterator<PlaylistItem>> loader, Iterator<PlaylistItem> data) {

    }

    @Override
    public void onLoaderReset(Loader<Iterator<PlaylistItem>> loader) {

    }
}



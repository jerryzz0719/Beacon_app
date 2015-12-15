package com.estimote.examples.demos.activities;

import android.os.Bundle;

import com.estimote.examples.demos.R;

public class MapActivity extends BaseActivity{

    @Override
    protected int getLayoutResId() {
        return R.layout.main;
    }

    /** INSTANCE METHODS */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapview);


    }

}


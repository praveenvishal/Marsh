package com.android.marshplay.ui.crop;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.ViewDataBinding;

import com.android.marshplay.R;
import com.android.marshplay.base.BaseActivity;

public class CropImageActivity extends BaseActivity {

    public static final String CROP_IMAGE_EXTRA = "CROP_IMAGE_EXTRA";

    public static void startCropImageActivity(String file, Context context) {
        Intent intent = new Intent(context, CropImageActivity.class);
        intent.putExtra(CROP_IMAGE_EXTRA, file);
        context.startActivity(intent);
    }


    @Override
    protected int layoutRes() {
        return R.layout.activity_common;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onViewInitialized(ViewDataBinding binding) {
        Intent intent = getIntent();
        String path = intent.getStringExtra(CROP_IMAGE_EXTRA);
        setFragment(CropImageFragment.getInstance(path), false, CropImageFragment.TAG);
    }
}

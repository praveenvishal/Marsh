package com.android.marshplay.ui.camera;

import android.content.Context;
import android.content.Intent;

import androidx.databinding.ViewDataBinding;

import com.android.marshplay.R;
import com.android.marshplay.base.BaseActivity;

public class CameraActivity extends BaseActivity {


    public static void startCameraActivity(Context context) {
        Intent intent = new Intent(context, CameraActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int layoutRes() {
        return R.layout.activity_common;
    }

    @Override
    protected void onViewInitialized(ViewDataBinding binding) {
        setFragment(CameraFragment.getInstance(null), false, CameraFragment.TAG);
    }
}

package com.android.marshplay.base;

import android.os.Bundle;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

import dagger.android.support.DaggerAppCompatActivity;

public abstract class BaseActivity extends DaggerAppCompatActivity {

    private ViewDataBinding binding;

    @LayoutRes
    protected abstract int layoutRes();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, layoutRes());
        onViewInitialized(binding);
    }

    protected void setFragment(Fragment fragment, boolean addToBackStack, String tag) {
        FragmentUtil.setFragment(this, fragment, tag, addToBackStack);
    }

    protected abstract void onViewInitialized(ViewDataBinding binding);


}
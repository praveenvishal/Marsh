package com.android.marshplay.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

import dagger.android.support.DaggerFragment;

public abstract class BaseFragment extends DaggerFragment {

    private AppCompatActivity activity;
    private ViewDataBinding binding;


    @LayoutRes
    protected abstract int layoutRes();

    protected void setFragment(Fragment fragment, boolean addToBackStack, String tag) {
        try {
            BaseActivity activity= (BaseActivity) getActivity();
            if (null != activity) {
                FragmentUtil.setFragment(activity, fragment, tag, addToBackStack);
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }

    }

    protected abstract void onViewsInitialized(ViewDataBinding binding, View view);


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        onViewsInitialized(binding, view);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, layoutRes(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (AppCompatActivity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    protected void finishActivity() {
        Activity activity = getActivity();
        if (activity != null) activity.finish();

    }


    public AppCompatActivity getBaseActivity() {
        return activity;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}
package com.android.marshplay.base;

import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;
import com.android.marshplay.di.component.ApplicationComponent;
import com.android.marshplay.di.component.DaggerApplicationComponent;

public class BaseApplication extends DaggerApplication {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        ApplicationComponent component = DaggerApplicationComponent.builder().application(this).build();
        component.inject(this);
        return component;
    }
}

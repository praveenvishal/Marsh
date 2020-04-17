package com.android.marshplay.di.module;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import com.android.marshplay.ui.camera.CameraFragment;

@Module
public abstract class CameraFragmentBindingModule {

    @ContributesAndroidInjector
    abstract CameraFragment provideCameraFragment();
}

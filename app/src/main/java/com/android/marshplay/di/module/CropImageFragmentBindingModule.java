package com.android.marshplay.di.module;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import com.android.marshplay.ui.crop.CropImageFragment;
import com.android.marshplay.ui.crop.CropImageResultFragment;

@Module
public abstract class CropImageFragmentBindingModule {

    @ContributesAndroidInjector
    abstract CropImageFragment provideCropImageFragment();

    @ContributesAndroidInjector
    abstract CropImageResultFragment provideCropImageResultFragment();


}

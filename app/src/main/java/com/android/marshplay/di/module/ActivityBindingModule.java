package com.android.marshplay.di.module;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import com.android.marshplay.ui.camera.CameraActivity;
import com.android.marshplay.ui.saved.SavedImageListActivity;
import com.android.marshplay.ui.crop.CropImageActivity;
import com.android.marshplay.ui.main.MainActivity;

@Module
public abstract class ActivityBindingModule {


    @ContributesAndroidInjector(modules = {CameraFragmentBindingModule.class})
    abstract CameraActivity bindCameraActivity();

    @ContributesAndroidInjector(modules = {CameraFragmentBindingModule.class})
    abstract MainActivity bindMainActivity();


    @ContributesAndroidInjector(modules = {CameraFragmentBindingModule.class})
    abstract SavedImageListActivity bindUploadListActivity();


    @ContributesAndroidInjector(modules = {CropImageFragmentBindingModule.class})
    abstract CropImageActivity bindCropImageActivity();
}

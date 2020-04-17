package com.android.marshplay.di.module;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import com.android.marshplay.di.util.ViewModelKey;
import com.android.marshplay.ui.viewmodel.ImageListViewModel;
import com.android.marshplay.di.util.ViewModelFactory;

@Module
public abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(ImageListViewModel.class)
    abstract ViewModel bindListViewModel(ImageListViewModel listViewModel);


    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory factory);
}

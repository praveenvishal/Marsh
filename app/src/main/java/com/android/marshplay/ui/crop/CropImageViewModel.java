package com.android.marshplay.ui.crop;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import com.android.marshplay.data.repository.ImageRepository;
import com.android.marshplay.data.local.entity.Image;

public class CropImageViewModel extends ViewModel {

    private final ImageRepository repository;
    private final MutableLiveData<List<Image>> mImageListLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> repoLoadError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private CompositeDisposable disposable;

    @Inject
    public CropImageViewModel(ImageRepository repoRepository) {
        this.repository = repoRepository;
        disposable = new CompositeDisposable();
        loadImages();
    }

    public LiveData<List<Image>> getImageList() {
        return mImageListLiveData;
    }


    private void loadImages() {
        disposable.add(repository.getUploadedImages().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableSingleObserver<List<Image>>() {
                    @Override
                    public void onSuccess(List<Image> value) {
                        repoLoadError.setValue(false);
                        loading.setValue(false);
                        mImageListLiveData.setValue(value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        repoLoadError.setValue(true);
                        loading.setValue(false);
                    }
                }));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (disposable != null) {
            disposable.clear();
            disposable = null;
        }
    }
}
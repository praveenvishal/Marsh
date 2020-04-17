package com.android.marshplay.ui.viewmodel;


import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import com.android.marshplay.data.local.Image;
import com.android.marshplay.data.rest.ImageRepository;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ImageListViewModel extends ViewModel {

    private final ImageRepository repository;
    private final MutableLiveData<List<Image>> mImageListLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> repoLoadError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private CompositeDisposable disposable;

    @Inject
    public ImageListViewModel(ImageRepository repoRepository) {
        this.repository = repoRepository;
        disposable = new CompositeDisposable();
        loadImages();
    }

    public LiveData<List<Image>> getImageList() {
        return mImageListLiveData;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<Boolean> getError() {
        return repoLoadError;
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

    public void saveImageToDatabase(String imageFilePath) {
        Image image = new Image();
        image.setImagePath(imageFilePath);
        image.setSavedAt(System.currentTimeMillis());
        disposable.add(repository.insertImage(image)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                        },
                        throwable -> Log.e(TAG, "Unable to update username", throwable)));
    }
}
package com.android.marshplay.data.rest;

import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Single;
import com.android.marshplay.data.local.ImageDao;
import com.android.marshplay.data.local.Image;

public class ImageRepository {

    private final ImageDao imageDao;

    @Inject
    public ImageRepository(ImageDao imageDao) {
        this.imageDao = imageDao;
    }

    public Single<List<Image>> getUploadedImages() {
        return Single.fromCallable(new Callable<List<Image>>() {
            @Override
            public List<Image> call() throws Exception {
                return imageDao.getImages();
            }
        });
    }

    public Completable insertImage(Image image) {
        return imageDao.insertImage(image);
    }

}

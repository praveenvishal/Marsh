package com.android.marshplay.di.module;

import android.app.Application;

import androidx.room.Room;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import com.android.marshplay.data.local.ImageDao;
import com.android.marshplay.di.util.AppDataBase;
import com.android.marshplay.util.DatabaseConstants;

@Module(includes = ViewModelModule.class)
public class ApplicationModule {

    @Singleton
    @Provides
    ImageDao provideImageDao(AppDataBase db) {
        return db.getImageDao();
    }


    @Singleton
    @Provides
    AppDataBase provideDb(Application app) {
        return Room.databaseBuilder(app, AppDataBase.class, DatabaseConstants.DATABASE_NAME).allowMainThreadQueries().fallbackToDestructiveMigration().build();
    }
}

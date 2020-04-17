package com.android.marshplay.di.util;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.android.marshplay.data.local.ImageDao;
import com.android.marshplay.data.local.Image;


@Database(entities = {Image.class}, version = 2, exportSchema = false)

public abstract class AppDataBase extends RoomDatabase {
    public abstract ImageDao getImageDao();


}

package com.android.marshplay.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.android.marshplay.data.local.dao.ImageDao;
import com.android.marshplay.data.local.entity.Image;


@Database(entities = {Image.class}, version = 2, exportSchema = false)

public abstract class AppDataBase extends RoomDatabase {
    public abstract ImageDao getImageDao();


}

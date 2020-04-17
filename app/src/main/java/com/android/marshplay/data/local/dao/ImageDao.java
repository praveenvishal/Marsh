package com.android.marshplay.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.android.marshplay.data.local.entity.Image;

import java.util.List;

import io.reactivex.Completable;

/**
 * Data Access Object for the images table.
 */
@Dao
public interface ImageDao {

    /**
     * Get the images from the table. Since for simplicity we only have one user in the database,
     * this query gets all images from the table, but limits the result to just the 1st image.
     *
     * @return the image from the table
     */
    @Query("SELECT * FROM Image ORDER BY savedAt DESC LIMIT 500")
    List<Image> getImages();

    /**
     * Insert a image in the database. If the user already exists, replace it.
     *
     * @param image the user to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertImage(Image image);

    /**
     * Delete all image.
     */
    @Query("DELETE FROM Image")
    void deleteAllImages();
}
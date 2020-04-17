package com.android.marshplay.azure;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.File;
import java.io.FileInputStream;

public class FileUploadService extends IntentService {
    public static final String EXTRA_FILE = "EXTRA_FILE";
    public static final String TAG = "FileUploadService";
    private long fileLength;
    private long totalBytes;
    private final CustomInputStream.ReadListener readListener = new CustomInputStream.ReadListener() {

        @Override
        public void onRead(long bytes) {
            totalBytes += bytes;
            if (totalBytes == fileLength) {
                Log.d(TAG, "File Uploaded");


            }
            double progress = ((totalBytes * 1D / fileLength)) * 100;
            Intent intent = new Intent();
            intent.setAction("progress");
            intent.putExtra("progressValue", (int)progress);
            LocalBroadcastManager.getInstance(FileUploadService.this).sendBroadcast(intent);
            System.out.println("progress: " + ((totalBytes * 1D / fileLength)) * 100);
        }
    };


    public FileUploadService() {
        super("FileUploadService");
    }

    private void uploadFile(File file) {
        if (file == null) return;
        try {
            fileLength = file.length();
            Log.d("FILESIZE", "FILELENGTH: " + fileLength);
            FileInputStream fis = new FileInputStream(file);
            CustomInputStream customInputStream = new CustomInputStream(fis, readListener, 2000, fileLength);
            final int imageLength = customInputStream.available();
            try {
                String filePath = AzureManager.uploadFile(file.getName(), customInputStream, imageLength);



            } catch (Exception ex) {


            }
        } catch (Exception ex) {


        }
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String path = intent.getStringExtra(EXTRA_FILE);
        File file = new File(path);
        uploadFile(file);
    }
}

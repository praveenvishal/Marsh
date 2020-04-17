package com.android.marshplay.ui.main;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.ViewDataBinding;

import java.net.URISyntaxException;

import com.android.marshplay.R;
import com.android.marshplay.base.BaseActivity;
import com.android.marshplay.databinding.ActivityMainBinding;
import com.android.marshplay.ui.camera.CameraActivity;
import com.android.marshplay.ui.saved.SavedImageListActivity;
import com.android.marshplay.ui.crop.CropImageActivity;
import com.android.marshplay.util.FileUtils;

public class MainActivity extends BaseActivity implements View.OnClickListener {


    private static final int REQUEST_EXTERNAL_IMAGE_STORAGE_PERMISSION = 1001;
    private static final int REQUEST_PICK_IMAGE_FROM_GALLERY = 1002;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"};
    ActivityMainBinding mainBinding;

    @Override
    protected int layoutRes() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onViewInitialized(ViewDataBinding binding) {
        mainBinding = (ActivityMainBinding) binding;
        mainBinding.setClickListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean permissionGranted =
                grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
        switch (requestCode) {
            case REQUEST_EXTERNAL_IMAGE_STORAGE_PERMISSION:
                if (permissionGranted) {
                    openGalleryIntent();
                }
                break;
        }
        if (!permissionGranted) {
            Toast.makeText(this, getString(R.string.grant_access_to_read), Toast.LENGTH_SHORT).show();
        }

    }

    private void openGalleryIntent() {
        Intent pickImageIntent = new Intent(Intent.ACTION_GET_CONTENT);
        pickImageIntent.setType("image/*");
        startActivityForResult(pickImageIntent, REQUEST_PICK_IMAGE_FROM_GALLERY);
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        handleChooseImageResult(resultCode, data);

    }

    private void handleChooseImageResult(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            String path = null;
            try {
                path = FileUtils.getPath(this, data.getData());
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            handleImageResult(path);
            return;
        }

    }

    private void handleImageResult(String path) {
        CropImageActivity.startCropImageActivity(path, this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonOpenCamera:
                CameraActivity.startCameraActivity(this);
                break;
            case R.id.buttonOpenUploadList:
                SavedImageListActivity.startUploadListActivity(this);
                break;
            case R.id.buttonOpenGallery:
                if (allPermissionsGranted()) {
                    openGalleryIntent();
                } else {
                    ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_EXTERNAL_IMAGE_STORAGE_PERMISSION);

                }

                break;
        }
    }
}

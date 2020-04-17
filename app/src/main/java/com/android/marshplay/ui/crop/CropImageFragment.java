package com.android.marshplay.ui.crop;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.ViewDataBinding;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;

import com.android.marshplay.R;
import com.android.marshplay.base.BaseFragment;
import com.android.marshplay.databinding.FragmentCropBinding;
import com.android.marshplay.util.FileUtils;


public class CropImageFragment extends BaseFragment implements CropImageView.OnSetImageUriCompleteListener,
        CropImageView.OnCropImageCompleteListener, View.OnClickListener {

    public static final String CROP_IMAGE_PATH = "CROP_IMAGE_PATH";
    public static final String TAG = "CropImageFragment";
    private FragmentCropBinding binding;
    private String path;

    public static CropImageFragment getInstance(String filePath) {
        CropImageFragment cropImageResultFragment = new CropImageFragment();
        Bundle bundle = new Bundle();
        bundle.putString(CROP_IMAGE_PATH, filePath);
        cropImageResultFragment.setArguments(bundle);
        return cropImageResultFragment;
    }


    @Override
    protected int layoutRes() {
        return R.layout.fragment_crop;
    }

    @Override
    protected void onViewsInitialized(ViewDataBinding viewDataBinding, View view) {
        binding = (FragmentCropBinding) viewDataBinding;
        path = getArguments().getString(CROP_IMAGE_PATH);
        Uri imageUri = FileUtils.getImageUri(path, getActivity());
        binding.imageView.setImageUriAsync(imageUri);
        binding.imageView.setOnSetImageUriCompleteListener(this);
        binding.imageView.setOnCropImageCompleteListener(this);
        binding.imageViewCrop.setOnClickListener(this);

    }

    @Override
    public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
        handleCropResult(result);
    }

    @Override
    public void onSetImageUriComplete(CropImageView view, Uri uri, Exception error) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            handleCropResult(result);
        }
    }

    private void handleCropResult(CropImageView.CropResult result) {
        if (result.getError() == null) {
            if (result.getBitmap() != null) {
                try {
                    FileUtils.savebitmap(result.getBitmap(), new File(path).getName());
                    setFragment(CropImageResultFragment.getInstance(path), false, CropImageResultFragment.TAG);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        } else {
            Log.e("AIC", "Failed to crop image", result.getError());
            Toast.makeText(
                    getActivity(),
                    "Image crop failed: " + result.getError().getMessage(),
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewCrop:
                binding.imageView.getCroppedImageAsync();
                break;
        }
    }
}

package com.android.marshplay.ui.crop;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.ViewDataBinding;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.File;

import javax.inject.Inject;

import com.android.marshplay.R;
import com.android.marshplay.azure.FileUploadService;
import com.android.marshplay.base.BaseFragment;
import com.android.marshplay.databinding.FragmentCropResultBinding;
import com.android.marshplay.ui.viewmodel.ImageListViewModel;
import com.android.marshplay.ui.saved.SavedImageListActivity;
import com.android.marshplay.util.GlideUtil;

public class CropImageResultFragment extends BaseFragment implements View.OnClickListener {
    public static final String CROP_IMAGE_PATH = "CROP_IMAGE_PATH";
    public static final String TAG = "CropImageResultFragment";
    FragmentCropResultBinding binding;

    @Inject
    ImageListViewModel viewModel;
    private String imageFilePath;
    private UploadImageReceiver receiver;
    private boolean isUploadInProgress;

    public static CropImageResultFragment getInstance(String filePath) {
        CropImageResultFragment cropImageResultFragment = new CropImageResultFragment();
        Bundle bundle = new Bundle();
        bundle.putString(CROP_IMAGE_PATH, filePath);
        cropImageResultFragment.setArguments(bundle);
        return cropImageResultFragment;
    }


    @Override
    protected int layoutRes() {
        return R.layout.fragment_crop_result;
    }

    @Override
    protected void onViewsInitialized(ViewDataBinding viewDataBinding, View view) {
        binding = (FragmentCropResultBinding) viewDataBinding;
        binding.setClickListener(this);
        receiver = new UploadImageReceiver();
        imageFilePath = getArguments().getString(CROP_IMAGE_PATH);
        GlideUtil.showLocalImage(binding.imageView, getArguments().getString(CROP_IMAGE_PATH));
        registerUploadProgressReceiver();
    }

    private void registerUploadProgressReceiver() {
        IntentFilter filter = new IntentFilter("progress");
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unregisterReceiver();
    }

    private void unregisterReceiver() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.saveTextView:
                startUpload();
                break;
        }

    }

    private void startUpload() {
        if (isUploadInProgress) return;
        Intent intent = new Intent(getActivity(), FileUploadService.class);
        intent.putExtra(FileUploadService.EXTRA_FILE, imageFilePath);
        getActivity().startService(intent);
    }

    private class UploadImageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case "progress":
                    int progress = intent.getIntExtra("progressValue", 0);
                    isUploadInProgress = true;
                    binding.progressBar.setVisibility(View.VISIBLE);
                    binding.progressBarTextView.setVisibility(View.VISIBLE);
                    binding.progressBar.setProgress(progress);
                    binding.progressBarTextView.setText("" + progress + " " + "%");
                    if (progress == 100) {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.progressBarTextView.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), getString(R.string.textCompleteSavingImage), Toast.LENGTH_LONG).show();
                        viewModel.saveImageToDatabase(new File(imageFilePath).getName());
                        isUploadInProgress = false;
                        SavedImageListActivity.startUploadListActivity(getActivity());
                        finishActivity();

                    }
                    break;

            }
        }
    }

}

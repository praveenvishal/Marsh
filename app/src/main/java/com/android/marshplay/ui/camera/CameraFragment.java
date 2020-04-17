package com.android.marshplay.ui.camera;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.ViewDataBinding;

import com.android.marshplay.databinding.FragmentCameraBinding;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import com.android.marshplay.R;
import com.android.marshplay.base.BaseFragment;
import com.android.marshplay.ui.crop.CropImageActivity;

import static java.lang.Math.abs;
import static java.lang.Math.min;

public class CameraFragment extends BaseFragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    public static final String TAG = "CameraFragment";
    private static final double RATIO_4_3_VALUE = 4.0 / 3.0;
    private static final double RATIO_16_9_VALUE = 16.0 / 9.0;
    private static final int[] FLASH_OPTIONS = {
            ImageCapture.FLASH_MODE_AUTO,
            ImageCapture.FLASH_MODE_OFF,
            ImageCapture.FLASH_MODE_ON,
    };
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};
    private final long ANIMATION_FAST_MILLIS = 50L;
    private final long ANIMATION_SLOW_MILLIS = 100L;
    private int screenAspectRatio;
    private int rotation;
    private FragmentCameraBinding binding;
    private int REQUEST_CODE_PERMISSIONS = 10;
    private int displayId = -1;
    private Preview preview;
    private int mFlashMode = ImageCapture.FLASH_MODE_OFF;
    private File file;
    private ImageCapture imgCap;
    private ImageAnalysis analysis;
    private Executor mainExecuter;
    private int lencFacing = CameraSelector.LENS_FACING_BACK;
    private DisplayManager displayManager;
    private Camera camera;
    ScaleGestureDetector.SimpleOnScaleGestureListener scaleGestureListener = new ScaleGestureDetector.SimpleOnScaleGestureListener() {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scale = camera.getCameraInfo().getZoomState().getValue().getZoomRatio() * detector.getScaleFactor();
            camera.getCameraControl().setZoomRatio(scale);
            return true;
        }
    };

    private DisplayManager.DisplayListener displayListener = new DisplayManager.DisplayListener() {
        @Override
        public void onDisplayAdded(int displayId) {

        }

        @Override
        public void onDisplayRemoved(int displayId) {

        }

        @Override
        public void onDisplayChanged(int displayId) {
            if (displayId == CameraFragment.this.displayId) {
                if (imgCap != null) {
                    imgCap.setTargetRotation(getView().getDisplay().getRotation());
                    analysis.setTargetRotation(getView().getDisplay().getRotation());
                }
            }
        }
    };
    private ImageCapture.OnImageSavedCallback captureListener = new ImageCapture.OnImageSavedCallback() {
        @Override
        public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
            CropImageActivity.startCropImageActivity(file.getAbsolutePath(), getActivity());
        }

        @Override
        public void onError(@NonNull ImageCaptureException exception) {
            String msg = "Photo capture failed: " + exception.getMessage();
            Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
            if (exception != null) {
                exception.printStackTrace();
            }
        }


    };

    public static CameraFragment getInstance(Bundle bundle) {
        CameraFragment previewFragment = new CameraFragment();
        if (bundle != null) previewFragment.setArguments(bundle);
        return previewFragment;
    }

    @Override
    protected int layoutRes() {
        return R.layout.fragment_camera;
    }

    @Override
    protected void onViewsInitialized(ViewDataBinding viewDataBinding, View view) {
        binding = (FragmentCameraBinding) viewDataBinding;
        mainExecuter = ContextCompat.getMainExecutor(requireContext());
        displayId = binding.viewFinder.getId();
        binding.flashMode.displayFlashAuto();
        setClickListeners();
        registerDisplayManager();

    }

    private void setClickListeners() {
        binding.flashMode.setOnClickListener(this::onClick);
        binding.zoomSeekBar.setOnSeekBarChangeListener(this);
        binding.captureButton.setOnClickListener(this::onClick);

    }


    private void configureCaptureImage() {
        ImageCapture.Metadata metadata = new ImageCapture.Metadata();
        metadata.setReversedHorizontal(lencFacing == CameraSelector.LENS_FACING_FRONT);
        file = new File(Environment.getExternalStorageDirectory() + "/" + System.currentTimeMillis() + ".jpg");
        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(file)
                .setMetadata(metadata)
                .build();
        imgCap.takePicture(outputOptions, mainExecuter, captureListener);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.container.postDelayed(() -> {
                binding.container.setForeground(new ColorDrawable(Color.WHITE));
                binding.container.postDelayed(() -> binding.container.setForeground(null), ANIMATION_FAST_MILLIS);
            }, ANIMATION_SLOW_MILLIS);


        }
    }

    private void registerDisplayManager() {
        displayManager = (DisplayManager) getActivity().getSystemService(Context.DISPLAY_SERVICE);
        displayManager.registerDisplayListener(displayListener, null);

    }

    @Override
    public void onResume() {
        super.onResume();

        if (allPermissionsGranted()) {
            binding.viewFinder.post(() -> startCamera());
        } else {
            ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private int aspectRatio(int width, int height) {
        double previewRatio = Math.max(width, height) / min(width, height);
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3;
        }
        return AspectRatio.RATIO_16_9;
    }

    private void startCamera() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        binding.viewFinder.getDisplay().getMetrics(displayMetrics);
        rotation = binding.viewFinder.getDisplay().getRotation();
        screenAspectRatio = aspectRatio(displayMetrics.widthPixels, displayMetrics.heightPixels);
        CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(lencFacing).build();
        ListenableFuture<ProcessCameraProvider> cameraProvider = ProcessCameraProvider.getInstance(requireContext());
        cameraProvider.addListener(() -> {
            try {
                ProcessCameraProvider processCameraProvider = cameraProvider.get();
                preview = new Preview.Builder().setTargetAspectRatio(screenAspectRatio)
                        .setTargetRotation(rotation)
                        .build();
                configureCapture();
                analysis = new ImageAnalysis.Builder().setTargetAspectRatio(screenAspectRatio)
                        .setTargetRotation(rotation)
                        .build();
                processCameraProvider.unbindAll();
                try {
                    camera = processCameraProvider.bindToLifecycle(CameraFragment.this, cameraSelector, preview, imgCap, analysis);
                    preview.setSurfaceProvider(binding.viewFinder.createSurfaceProvider(camera.getCameraInfo()));
                } catch (Exception exc) {
                    Log.e(TAG, "Use case binding failed", exc);
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, mainExecuter);

    }

    private void configureCapture() {
        imgCap = new ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .setTargetAspectRatio(screenAspectRatio)
                .setTargetRotation(rotation)
                .setFlashMode(mFlashMode)
                .build();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(getActivity(), "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                finishActivity();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.capture_button:
                configureCaptureImage();
                break;

            case R.id.flashMode:
                configureFlashMode();
                break;
            case R.id.camera_switch_button:
                lencFacing = lencFacing == CameraSelector.LENS_FACING_FRONT ? CameraSelector.LENS_FACING_BACK : CameraSelector.LENS_FACING_FRONT;
                startCamera();
                break;

        }
    }

    private void configureFlashMode() {
        mFlashMode = (mFlashMode + 1) % FLASH_OPTIONS.length;
        switch (mFlashMode) {
            case ImageCapture.FLASH_MODE_OFF:
                mFlashMode = ImageCapture.FLASH_MODE_OFF;
                binding.flashMode.displayFlashOff();
                break;
            case ImageCapture.FLASH_MODE_AUTO:
                mFlashMode = ImageCapture.FLASH_MODE_AUTO;
                binding.flashMode.displayFlashAuto();
                break;
            case ImageCapture.FLASH_MODE_ON:
                mFlashMode = ImageCapture.FLASH_MODE_ON;
                binding.flashMode.displayFlashOn();
                break;
        }
        startCamera();
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (camera != null) {
            camera.getCameraControl().setLinearZoom(progress / 100.0f);

        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}

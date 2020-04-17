package com.android.marshplay.ui.saved;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.OnScaleChangedListener;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.android.marshplay.R;
import com.android.marshplay.base.BaseActivity;
import com.android.marshplay.data.local.entity.Image;
import com.android.marshplay.databinding.ActivityUploadBinding;
import com.android.marshplay.ui.viewmodel.ImageListViewModel;
import com.android.marshplay.util.GlideUtil;

public class SavedImageListActivity extends BaseActivity implements SavedImageListAdapter.OnImageClickListener {

    ActivityUploadBinding binding;
    SavedImageListAdapter mUploadListAdapeter;
    @Inject
    ImageListViewModel viewModel;


    public static void startUploadListActivity(Context context) {
        Intent intent = new Intent(context, SavedImageListActivity.class);
        context.startActivity(intent);
    }


    @Override
    protected int layoutRes() {
        return R.layout.activity_upload;
    }

    @Override
    protected void onViewInitialized(ViewDataBinding viewDataBinding) {
        binding = (ActivityUploadBinding) viewDataBinding;
        binding.imageView.setOnPhotoTapListener(new OnPhotoTapListener() {
            @Override
            public void onPhotoTap(ImageView imageView, float x, float y) {
                binding.recyclerView.setVisibility(View.VISIBLE);

            }
        });
        binding.imageView.setOnScaleChangeListener(new OnScaleChangedListener() {
            @Override
            public void onScaleChange(float scaleFactor, float focusX, float focusY) {
                binding.recyclerView.setVisibility(View.GONE);

            }
        });
        initRecyclerView();
        fetchImages();

    }


    private void fetchImages() {
        viewModel.getImageList().observe(this, images -> {
            setImages(images);
        });
        viewModel.getError().observe(this, isError -> {
            if (isError != null) if (isError) {
                binding.setIsLoading(false);
                Toast.makeText(this, "No Image Found", Toast.LENGTH_SHORT).show();
            }

        });
        viewModel.getLoading().observe(this, isLoading -> {
            if (isLoading != null) {
                binding.setIsLoading(isLoading);

            }
        });
    }

    private void setImages(List<Image> images) {
        if (images != null && images.size() > 0) {
            mUploadListAdapeter.setImages(images);
            onImageClicked(images.get(0));
        } else {
            Toast.makeText(this, "No Image Found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initRecyclerView() {
        mUploadListAdapeter = new SavedImageListAdapter(new ArrayList<>(), this, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setAdapter(mUploadListAdapeter);
    }


    @Override
    public void onImageClicked(Image image) {
        GlideUtil.showRemoteImage(binding.imageView, image.getImagePath());
    }
}

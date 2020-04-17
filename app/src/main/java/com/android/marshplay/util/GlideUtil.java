package com.android.marshplay.util;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.ImageView;

import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import com.android.marshplay.R;
import com.android.marshplay.azure.AzureManager;

public class GlideUtil {

    public static Drawable getColorPlaceHolder(Context context, @ColorRes int colorId) {
        return new ColorDrawable(ContextCompat.getColor(context, colorId));
    }

    public static void showRemoteImage(ImageView imageView, String fileName) {
        String remoteUrl = !TextUtils.isEmpty(fileName) ? AzureManager.BASE_AZURE_URL + fileName : "";
        RequestBuilder requestBuilder = Glide.with(imageView.getContext()).load(remoteUrl).transition(DrawableTransitionOptions.withCrossFade());
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.placeholder(getColorPlaceHolder(imageView.getContext(), R.color.border_gray));
        requestOptions.error(getColorPlaceHolder(imageView.getContext(), R.color.border_gray));
        requestOptions.fitCenter();
        requestBuilder.thumbnail(0.25f).apply(requestOptions).into(imageView);
    }

    public static void showLocalImage(ImageView imageView, String fileName) {
        RequestBuilder requestBuilder = Glide.with(imageView.getContext()).load(fileName);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.placeholder(getColorPlaceHolder(imageView.getContext(), R.color.border_gray));
        requestOptions.error(getColorPlaceHolder(imageView.getContext(), R.color.border_gray));
        requestOptions.fitCenter();
        requestBuilder.thumbnail(0.25f).apply(requestOptions).into(imageView);
    }


}

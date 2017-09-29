package me.gavin.photo.viewer.app.base;

import android.databinding.BindingAdapter;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class BindingHelper {

    @BindingAdapter({"loadById"})
    public static void loadById(ImageView imageView, long id) {
        Glide.with(imageView.getContext())
                .fromMediaStore()
                .load(Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, String.valueOf(id)))
                .override(300, 300)
                .into(imageView);
    }

    @BindingAdapter({"imageUrl"})
    public static void loadImage(ImageView imageView, String url) {
        Glide.with(imageView.getContext())
                .load(url)
                .into(imageView);
    }

    @BindingAdapter({"paddingStatus"})
    public static void paddingStatus(AppBarLayout appBarLayout, boolean padding) {
        if (appBarLayout.isPaddingRelative()) {
            appBarLayout.setPaddingRelative(appBarLayout.getPaddingStart(), appBarLayout.getPaddingTop() + DisplayUtil.getStatusHeight(),
                    appBarLayout.getPaddingEnd(), appBarLayout.getPaddingBottom());
        } else {
            appBarLayout.setPadding(appBarLayout.getPaddingLeft(), appBarLayout.getPaddingTop() + DisplayUtil.getStatusHeight(),
                    appBarLayout.getPaddingRight(), appBarLayout.getPaddingBottom());
        }
    }
}
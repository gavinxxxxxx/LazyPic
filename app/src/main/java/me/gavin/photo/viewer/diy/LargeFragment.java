package me.gavin.photo.viewer.diy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.gavin.photo.viewer.R;
import me.gavin.photo.viewer.app.Image;
import me.gavin.photo.viewer.base.RecyclerAdapter;
import me.gavin.photo.viewer.base.RecyclerHolder;
import me.gavin.photo.viewer.databinding.FragLargeImageBinding;
import me.gavin.photo.viewer.databinding.ItemImageLargeDiyBinding;
import me.gavin.photo.viewer.util.DisplayUtil;
import me.yokeyword.fragmentation.SupportFragment;

/**
 * 这里是萌萌哒注释君
 *
 * @author gavin.xiong 2017/9/30
 */
public class LargeFragment extends SupportFragment {

    FragLargeImageBinding binding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragLargeImageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        @SuppressWarnings("unchecked")
        ArrayList<Image> images = (ArrayList<Image>) getArguments().getSerializable("images");
        int position = getArguments().getInt("position", 0);

        new PagerSnapHelper().attachToRecyclerView(binding.recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        layoutManager.setItemPrefetchEnabled(true);
        layoutManager.setInitialPrefetchItemCount(5);
        binding.recycler.setLayoutManager(layoutManager);
        binding.recycler.setAdapter(new B(getContext(), images));
        binding.recycler.scrollToPosition(position);
        binding.recycler.setItemViewCacheSize(5);
    }

    class B extends RecyclerAdapter<Image, ItemImageLargeDiyBinding> {

        public B(Context context, List<Image> mData) {
            super(context, mData, R.layout.item_image_large_diy);
        }

        @Override
        protected void onBind(RecyclerHolder<ItemImageLargeDiyBinding> holder, Image t, int position) {
            Observable.just(0)
                    .map(arg0 -> Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, String.valueOf(t.getId())))
                    .map(this::compressImageToBitmap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(holder.binding.item::setImageBitmap, Throwable::printStackTrace);
        }


        private Bitmap compressImageToBitmap(Uri uri) throws FileNotFoundException {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(uri), null, options);
            options.inJustDecodeBounds = false;
            // 设置压缩倍数 长宽均为 1/inSampleSize 最后成图大小为原图 1/inSampleSize^2
            options.inSampleSize = Math.min(options.outWidth / DisplayUtil.getScreenWidth(), options.outHeight / DisplayUtil.getScreenHeight());
            return BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(uri), null, options);
        }
    }
}

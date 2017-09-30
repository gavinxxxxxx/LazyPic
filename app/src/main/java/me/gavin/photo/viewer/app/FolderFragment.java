package me.gavin.photo.viewer.app;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
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
import me.gavin.photo.viewer.base.BindingAdapter;
import me.gavin.photo.viewer.databinding.LayoutToolbarRecyclerBinding;
import me.gavin.photo.viewer.util.DisplayUtil;
import me.yokeyword.fragmentation.SupportFragment;

/**
 * 这里是萌萌哒注释君
 *
 * @author gavin.xiong 2017/9/30
 */
public class FolderFragment extends SupportFragment {

    LayoutToolbarRecyclerBinding binding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LayoutToolbarRecyclerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        binding.toolbar.setTitle(R.string.app_name);

        int padding = DisplayUtil.dp2px(4);
        binding.recycler.setPadding(padding, padding + DisplayUtil.getStatusHeight() + DisplayUtil.dp2px(56F),
                padding, padding + DisplayUtil.getNavigationBarHeight(getContext()));

        queryImageFolder()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(folders -> {
                    binding.recycler.setLayoutManager(new GridLayoutManager(getContext(), 2));
                    BindingAdapter adapter = new BindingAdapter<>(getContext(), folders, R.layout.item_folder);
                    adapter.setOnItemClickListener(position -> {
                        ImageFragment fragment = new ImageFragment();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("image", folders.get(position));
                        fragment.setArguments(bundle);
                        start(fragment);
                    });
                    binding.recycler.setAdapter(adapter);
                });
    }

    /**
     * 按文件位置获取所有包含图片的文件夹列表
     */
    private Observable<List<Image>> queryImageFolder() {
        return Observable.just(0)
                .map(arg0 -> MediaStore.Images.Media.query(
                        getContext().getContentResolver(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        new String[]{
                                MediaStore.Images.Media._ID,
                                MediaStore.Images.Media.BUCKET_ID,
                                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                                "COUNT(*) AS count"},
                        "0 = 0) GROUP BY (" + MediaStore.Images.Media.BUCKET_ID,
                        null,
                        MediaStore.Images.Media.DATE_ADDED + " DESC")
                )
                .map(this::getImageList);
    }

    private List<Image> getImageList(Cursor cursor) throws FileNotFoundException {
        try {
            List<Image> images = new ArrayList<>();
            while (cursor.moveToNext()) {
                Image image = new Image();
                image.setId(cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID)));
                image.setParentId(cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID)));
                image.setParent(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)));
                image.setCount(cursor.getInt(cursor.getColumnIndex("count")));
                images.add(image);
            }
            return images;
        } finally {
            cursor.close();
        }
    }
}

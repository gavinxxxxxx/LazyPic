package me.gavin.photo.viewer.app;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
public class ImageFragment extends SupportFragment {

    LayoutToolbarRecyclerBinding binding;
    Image mFolder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LayoutToolbarRecyclerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mFolder = (Image) getArguments().getSerializable("image");
        binding.toolbar.setTitle(mFolder.getParent());
        binding.toolbar.setNavigationIcon(R.drawable.vt_arrow_back_24dp);
        binding.toolbar.setNavigationOnClickListener(v -> pop());

        int padding = DisplayUtil.dp2px(4);
        binding.recycler.setPadding(padding, padding + DisplayUtil.getStatusHeight() + DisplayUtil.dp2px(56F),
                padding, padding + DisplayUtil.getNavigationBarHeight(getContext()));

        queryImageByFolder()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(images -> {
                    binding.recycler.setLayoutManager(new GridLayoutManager(getContext(), 3));
                    BindingAdapter adapter = new BindingAdapter<>(getContext(), images, R.layout.item_image);
                    adapter.setOnItemClickListener(position -> {
                        LargeFragment fragment = new LargeFragment();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("images", ((ArrayList<Image>) images));
                        bundle.putInt("position", position);
                        fragment.setArguments(bundle);
                        start(fragment);
                    });
                    binding.recycler.setAdapter(adapter);
                }, Throwable::printStackTrace);
    }


    /**
     * 获取指定文件夹下的所有图片
     */
    private Observable<List<Image>> queryImageByFolder() {
        return Observable.just(0)
                .map(arg0 ->
                        MediaStore.Images.Media.query(
                                getContext().getContentResolver(),
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                new String[]{MediaStore.Images.Media._ID},
                                MediaStore.Images.Media.BUCKET_ID + " = ? ",
                                new String[]{String.valueOf(mFolder.getParentId())},
                                MediaStore.Images.Media.DATE_ADDED + " DESC"))
                .map(this::getImageList);
    }

    private List<Image> getImageList(Cursor cursor) {
        try {
            ArrayList<Image> images = new ArrayList<>();
            while (cursor.moveToNext()) {
                Image image = new Image();
                image.setId(cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID)));
                images.add(image);
            }
            return images;
        } finally {
            cursor.close();
        }
    }
}

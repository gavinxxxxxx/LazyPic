package me.gavin.photo.viewer.app;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import me.gavin.photo.viewer.R;
import me.gavin.photo.viewer.base.RecyclerAdapter;
import me.gavin.photo.viewer.base.RecyclerHolder;
import me.gavin.photo.viewer.databinding.FragLargeImageBinding;
import me.gavin.photo.viewer.databinding.ItemImageLargeBinding;
import me.gavin.photo.viewer.util.DisplayUtil;
import me.gavin.photo.viewer.util.L;
import me.yokeyword.fragmentation.SupportFragment;

import static android.support.v7.widget.RecyclerView.TOUCH_SLOP_PAGING;

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
        layoutManager.setInitialPrefetchItemCount(3);
        binding.recycler.setLayoutManager(layoutManager);
        binding.recycler.setAdapter(new B(getContext(), images));
        binding.recycler.scrollToPosition(position);
        binding.recycler.setItemViewCacheSize(5);

//        binding?.recycler?.addOnItemTouchListener(object: RecyclerView.OnItemTouchListener{
//            override fun onTouchEvent(rv: RecyclerView?, e: MotionEvent?) {
//                L.e("onTouchEvent - " + e?.action)
//            }
//
//            override fun onInterceptTouchEvent(rv: RecyclerView?, e: MotionEvent?): Boolean {
//                return true
//            }
//
//            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
//
//            }
//        })

//        binding?.recycler?.mTouchSlop = 0
//        binding.recycler.setScrollingTouchSlop(TOUCH_SLOP_PAGING);
//        L.e(ViewConfiguration.get(getContext()).getScaledTouchSlop());
//        L.e(ViewConfiguration.get(getContext()).getScaledPagingTouchSlop());
    }

    class B extends RecyclerAdapter<Image, ItemImageLargeBinding> {

        public B(Context context, List<Image> mData) {
            super(context, mData, R.layout.item_image_large);
        }

        @Override
        protected void onBind(RecyclerHolder<ItemImageLargeBinding> holder, Image t, int position) {
            Glide.with(mContext)
                    .fromMediaStore()
                    .load(Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, String.valueOf(t.getId())))
                    .override(DisplayUtil.getScreenWidth(), DisplayUtil.getScreenHeight())
                    .into(holder.binding.item);
        }

        @Override
        public void onViewRecycled(RecyclerHolder<ItemImageLargeBinding> holder) {
            holder.binding.item.setImageDrawable(null);
        }
    }

}

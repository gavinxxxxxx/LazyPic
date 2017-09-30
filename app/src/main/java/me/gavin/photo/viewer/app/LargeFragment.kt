package me.gavin.photo.viewer.app

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PagerSnapHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import me.gavin.photo.viewer.R
import me.gavin.photo.viewer.app.base.DisplayUtil
import me.gavin.photo.viewer.app.base.RecyclerAdapter
import me.gavin.photo.viewer.app.base.RecyclerHolder
import me.gavin.photo.viewer.databinding.FragLargeImageBinding
import me.gavin.photo.viewer.databinding.ItemImageLargeBinding
import me.yokeyword.fragmentation.SupportFragment

/**
 * 这里是萌萌哒注释君
 *
 * @author gavin.xiong 2017/9/28
 */
class LargeFragment : SupportFragment() {

    var binding: FragLargeImageBinding? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragLargeImageBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        val images = arguments.getSerializable("images") as ArrayList<Image>
        val position = arguments.getInt("position", 0)

        PagerSnapHelper().attachToRecyclerView(binding?.recycler)
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        layoutManager.isItemPrefetchEnabled = true
        layoutManager.initialPrefetchItemCount = 3
        binding?.recycler?.layoutManager = layoutManager
        binding?.recycler?.adapter = B(context, images)
        binding?.recycler?.scrollToPosition(position)
        binding?.recycler?.setItemViewCacheSize(5)

        Toast.makeText(context, "imageView", Toast.LENGTH_SHORT).show()
    }

    class B(context: Context, list: List<Image>) : RecyclerAdapter<Image, ItemImageLargeBinding>(context, list, R.layout.item_image_large) {
        override fun onBind(holder: RecyclerHolder<ItemImageLargeBinding>?, t: Image?, position: Int) {
            Glide.with(mContext)
                    .fromMediaStore()
                    .load(Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, t?.id.toString()))
                    .override(DisplayUtil.getScreenWidth(), DisplayUtil.getScreenHeight())
                    .into(holder?.binding?.item)
        }

        override fun onViewRecycled(holder: RecyclerHolder<ItemImageLargeBinding>?) {
            holder?.binding?.item?.setImageDrawable(null)
        }
    }

}
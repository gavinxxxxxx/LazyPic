package me.gavin.photo.viewer.app

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PagerSnapHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.gavin.photo.viewer.R
import me.gavin.photo.viewer.app.base.DisplayUtil
import me.gavin.photo.viewer.app.base.RecyclerAdapter
import me.gavin.photo.viewer.app.base.RecyclerHolder
import me.gavin.photo.viewer.databinding.FragLargeImageBinding
import me.gavin.photo.viewer.databinding.ItemPhotoViewTwoBinding
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

        Toast.makeText(context, "zoomView", Toast.LENGTH_SHORT).show()
    }

    class B(context: Context, list: List<Image>) : RecyclerAdapter<Image, ItemPhotoViewTwoBinding>(context, list, R.layout.item_photo_view_two) {
        override fun onBind(holder: RecyclerHolder<ItemPhotoViewTwoBinding>?, t: Image?, position: Int) {
            Observable.just(0)
                    .map { Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, t?.id?.toString()) }
                    .map { uri -> compressImageToBitmap(uri) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ bitmap -> holder?.binding?.item?.setImageBitmap(bitmap) },
                            { throwable -> throwable.printStackTrace() })
        }

        private fun compressImageToBitmap(uri: Uri): Bitmap {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeStream(mContext.contentResolver.openInputStream(uri), null, options)
            options.inJustDecodeBounds = false
            // 设置压缩倍数 长宽均为 1/inSampleSize 最后成图大小为原图 1/inSampleSize^2
            options.inSampleSize = Math.min(options.outWidth / DisplayUtil.getScreenWidth(), options.outHeight / DisplayUtil.getScreenHeight())
            return BitmapFactory.decodeStream(mContext.contentResolver.openInputStream(uri), null, options)
        }
    }


}
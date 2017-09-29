package me.gavin.photo.viewer.app

import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.gavin.photo.viewer.R
import me.gavin.photo.viewer.app.base.BindingAdapter
import me.gavin.photo.viewer.app.base.DisplayUtil
import me.gavin.photo.viewer.databinding.LayoutToolbarRecyclerBinding
import me.yokeyword.fragmentation.SupportFragment
import java.util.*

/**
 * 这里是萌萌哒注释君
 *
 * @author gavin.xiong 2017/9/28
 */
class ImageFragment : SupportFragment() {

    var binding: LayoutToolbarRecyclerBinding? = null

    var mFolder: Image? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = LayoutToolbarRecyclerBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        mFolder = arguments["image"] as Image

        binding?.toolbar?.title = mFolder?.parent
        binding?.toolbar?.setNavigationIcon(R.drawable.vt_arrow_back_24dp)
        binding?.toolbar?.setNavigationOnClickListener { pop() }

        val padding = DisplayUtil.dp2px(4F)
        binding?.recycler?.setPadding(padding, padding + DisplayUtil.getStatusHeight() + DisplayUtil.dp2px(56F),
                padding, padding + DisplayUtil.getNavigationBarHeight(context))

        queryImageByFolder()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ images ->
                    binding?.recycler?.layoutManager = GridLayoutManager(context, 3)
                    val adapter = BindingAdapter(context, images, R.layout.item_image)
                    adapter.setOnItemClickListener { position ->
                        val fragment = if (position % 2 == 0) LargeFragment() else LargeFragment2()
                        val bundle = Bundle()
                        bundle.putSerializable("images", images as ArrayList<*>)
                        bundle.putInt("position", position)
                        fragment.arguments = bundle
                        start(fragment)
                    }
                    binding?.recycler?.adapter = adapter
                }, { throwable -> throwable.printStackTrace() })
    }

    /**
     * 获取指定文件夹下的所有图片
     */
    private fun queryImageByFolder(): Observable<List<Image>> {
        return Observable.just(0)
                .map {
                    MediaStore.Images.Media.query(
                            context.contentResolver,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            arrayOf(MediaStore.Images.Media._ID),
                            MediaStore.Images.Media.BUCKET_ID + " = ? ",
                            arrayOf(mFolder?.parentId?.toString()),
                            MediaStore.Images.Media.DATE_ADDED + " DESC")
                }
                .map { cursor -> getImageList(cursor) }
    }

    private fun getImageList(cursor: Cursor): List<Image> {
        cursor.use {
            val images = ArrayList<Image>()
            while (cursor.moveToNext()) {
                val image = Image()
                image.id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID))
                images.add(image)
            }
            return images
        }
    }
}
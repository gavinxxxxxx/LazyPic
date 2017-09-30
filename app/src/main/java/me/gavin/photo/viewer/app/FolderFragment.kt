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
import me.gavin.photo.viewer.util.DisplayUtil
import me.gavin.photo.viewer.base.BindingAdapter
import me.gavin.photo.viewer.databinding.LayoutToolbarRecyclerBinding
import me.yokeyword.fragmentation.SupportFragment

/**
 * 这里是萌萌哒注释君
 *
 * @author gavin.xiong 2017/9/28
 */
class FolderFragment : SupportFragment() {

    var binding: LayoutToolbarRecyclerBinding? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = LayoutToolbarRecyclerBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        binding?.toolbar?.title = getString(R.string.app_name)

        val padding = DisplayUtil.dp2px(4F)
        binding?.recycler?.setPadding(padding, padding + DisplayUtil.getStatusHeight() + DisplayUtil.dp2px(56F),
                padding, padding + DisplayUtil.getNavigationBarHeight(context))

        queryImageFolder()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { folders ->
                    binding?.recycler?.layoutManager = GridLayoutManager(context, 2)
                    val adapter = BindingAdapter(context, folders, R.layout.item_folder)
                    adapter.setOnItemClickListener { position ->
                        val fragment = ImageFragment()
                        val bundle = Bundle()
                        bundle.putSerializable("image", folders[position])
                        fragment.arguments = bundle
                        start(fragment)
                    }
                    binding?.recycler?.adapter = adapter
                }
    }


    /**
     * 按文件位置获取所有包含图片的文件夹列表
     */
    private fun queryImageFolder(): Observable<List<Image>> {
        return Observable.just(0)
                .map {
                    MediaStore.Images.Media.query(
                            context.contentResolver,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            arrayOf(MediaStore.Images.Media._ID,
                                    MediaStore.Images.Media.BUCKET_ID,
                                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                                    "COUNT(*) AS count"),
                            "0 = 0) GROUP BY (" + MediaStore.Images.Media.BUCKET_ID,
                            null,
                            MediaStore.Images.Media.DATE_ADDED + " DESC")
                }
                .map { this.getImageList(it) }
    }

    private fun getImageList(cursor: Cursor): List<Image> {
        cursor.use {
            val images = ArrayList<Image>()
            while (cursor.moveToNext()) {
                val image = Image()
                image.id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID))
                image.parentId = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID))
                image.parent = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
                image.count = cursor.getInt(cursor.getColumnIndex("count"))
                images.add(image)
            }
            return images
        }
    }
}
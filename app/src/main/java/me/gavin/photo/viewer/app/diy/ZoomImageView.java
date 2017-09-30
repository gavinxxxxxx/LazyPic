package me.gavin.photo.viewer.app.diy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import me.gavin.photo.viewer.app.base.L;

/**
 * 这里是萌萌哒注释君
 *
 * @author gavin.xiong 2017/9/27
 */
public class ZoomImageView extends View {

    private Context mContext;

    private Bitmap mBitmap = null;

    private Matrix mMatrix, mMatrix2;
    private Paint mPaint;

    public ZoomImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mMatrix = new Matrix();
        mMatrix2 = new Matrix();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        mPaint.setDither(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(
                MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY
                        ? MeasureSpec.getSize(widthMeasureSpec) : 0,
                MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY
                        ? MeasureSpec.getSize(heightMeasureSpec) : 0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mBitmap == null) {
            return;
        }

        mMatrix.reset();
        mMatrix.setScale(2f, 2f);
        Bitmap bm = Bitmap.createBitmap(mBitmap, 500, 900, 500, 900, mMatrix, true);

        canvas.drawBitmap(newBitmap(bm), mMatrix2, mPaint);
    }

    public void setImageBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
        postInvalidate();
    }

    public static Bitmap compressImageToBitmap(String imagePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
        L.e("采样率压缩 - " + bitmap.getWidth() + "/" + bitmap.getHeight());
        options.inJustDecodeBounds = false;
        options.inSampleSize = 2; // 设置压缩倍数 长宽均为1/2最后成图为原图1/4大小
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        return bitmap;
    }

    /**
     * bitmap 绘制边框 - debug
     */
    private Bitmap newBitmap(Bitmap bit) {
        //创建一个空的Bitmap(内存区域),大小和原图一致
        Bitmap bitmap = Bitmap.createBitmap(bit.getWidth(), bit.getHeight(), Bitmap.Config.ARGB_8888);
        //将bitmap放置到绘制区域,并绘制
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(bit, 0, 0, null);

        //将canvas传递进去并设置其边框
        Rect rect = canvas.getClipBounds();
        Paint paint = new Paint();
        //设置边框颜色
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        //设置边框宽度
        paint.setStrokeWidth(20);
        canvas.drawRect(rect, paint);

        return bitmap;
    }

}

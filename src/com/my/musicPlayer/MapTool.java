package com.my.musicPlayer;

import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.renderscript.*;

public class MapTool
{
	public static Bitmap blurBitmap(Bitmap bitmap, int radius,Context context) {
        //创建一个空bitmap，其大小与我们想要模糊的bitmap大小相同
        Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        //实例化一个新的Renderscript
        RenderScript rs = RenderScript.create(context);
        //创建Allocation对象
        Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
        Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);
        //创建ScriptIntrinsicBlur对象，该对象实现了高斯模糊算法
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        //设置模糊半径，0 <radius <= 25
        blurScript.setRadius(radius);
        //执行Renderscript
        blurScript.setInput(allIn);
        blurScript.forEach(allOut);
        //将allOut创建的Bitmap复制到outBitmap
        allOut.copyTo(outBitmap);
        //释放内存占用
        bitmap.recycle();
        //销毁Renderscript。
        rs.destroy();
        return outBitmap;
    }
	

    public static Bitmap getCirleBitmap(Bitmap bmp) {
        //获取bmp的宽高 小的一个做为圆的直径r
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int r = Math.min(w, h);

        //创建一个paint
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        //新创建一个Bitmap对象newBitmap 宽高都是r
        Bitmap newBitmap = Bitmap.createBitmap(r, r, Bitmap.Config.ARGB_8888);
        //创建一个使用newBitmap的Canvas对象
        Canvas canvas = new Canvas(newBitmap);
        //canvas画一个圆形
        canvas.drawCircle(r / 2, r / 2, r / 2, paint);
        //然后 paint要设置Xfermode 模式为SRC_IN 显示上层图像（后绘制的一个）的相交部分
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        //canvas调用drawBitmap直接将bmp对象画在画布上 因为paint设置了Xfermode，所以最终只会显示这个bmp的一部分 也就
        //是bmp的和下层圆形相交的一部分圆形的内容
        canvas.drawBitmap(bmp, 0, 0, paint);
        return newBitmap;
	 }
	

	public static Bitmap toRoundCorner(Bitmap bitmap, int pset/*百分比*/) {  
         //图片切圆角
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),Bitmap.Config.ARGB_8888);  
        Canvas canvas = new Canvas(output);  

        final int color = 0xff424242;  
        final Paint paint = new Paint();  
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());  
        final RectF rectF = new RectF(rect);  
        final float roundPx = pset*(bitmap.getHeight()+bitmap.getWidth())/200;  

        paint.setAntiAlias(true);  
        canvas.drawARGB(0, 0, 0, 0);  
        paint.setColor(color);  
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);  

        paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN));  
        canvas.drawBitmap(bitmap, rect, rect, paint);  

        return output;  
    }
	 
	
}

package utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

/**
 * Created by Administrator on 2016/8/14.
 */

public class BlurImg {

    /***
     * 对Bitmap图片进行模糊处理
     * @param context
     * @param s
     * @return
     */
    public static Bitmap blur(Context context, Bitmap s) {

        Bitmap bitmap = Bitmap.createBitmap(s.getWidth(), s.getHeight(), Bitmap.Config.ARGB_8888);

        RenderScript rs = RenderScript.create(context);

        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, Element.F64(rs));

        Allocation input = Allocation.createFromBitmap(rs, s);
        Allocation out = Allocation.createFromBitmap(rs, bitmap);

        blur.setRadius(25.f);
        blur.setInput(input);
        blur.forEach(out);

        out.copyTo(bitmap);

        s.recycle();

        rs.destroy();

        return bitmap;

    }

    /**
     * 根据资源id模糊图片
     * */
    public static Bitmap blur(Context context, int resId) {

        return  blur(context, BitmapFactory.decodeResource(context.getResources(), resId));
    }

}

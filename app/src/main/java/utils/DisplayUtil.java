package utils;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by zpf on 2016/12/12.
 */

public class DisplayUtil {

    public static int dp2px(Context context, float dpValue) {

        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue,
                context.getResources().getDisplayMetrics());
    }
}

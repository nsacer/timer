package customview;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/8/14.
 */

public class TextViewFZ extends TextView {
    public TextViewFZ(Context context) {
        super(context);
        setCustomTypeFace(context);
    }

    public TextViewFZ(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomTypeFace(context);
    }

    public TextViewFZ(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setCustomTypeFace(context);
    }

    public TextViewFZ(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setCustomTypeFace(context);
    }

    private void setCustomTypeFace(Context context){

        setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/FZYBGSJT.ttf"));
    }
}

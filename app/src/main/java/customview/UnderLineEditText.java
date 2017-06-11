package customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by Administrator on 2016/8/7.
 */
public class UnderLineEditText extends EditText {

    private Paint paint;

    public UnderLineEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.GRAY);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int lines = getLineCount();

        int height = getHeight();

        int lineHeight = getLineHeight();

        int totalLines = height / lineHeight;

        float lineDraw = getLineHeight() - getTextSize() - 2;

        if (lines < totalLines) lines = totalLines;

        for (int i=0; i<=lines; i++) {

            canvas.drawLine(0.0f, lineHeight * i + lineDraw, getRight(),
                    lineHeight * i + lineDraw, paint);
        }
    }
}

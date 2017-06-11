package customview;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import java.math.BigDecimal;

import model.DailyPay;
import model.Target;
import utils.SharedPreferencesHelper;
import zhao.pary.timer.R;
import zhao.pary.timer.TimerSetting;

/**
 * Created by zpf on 2016/11/7.
 * 圆环图
 */
public class CircleRingGraph extends View {

    private Context context;

    /**  */
    private final float DEFAULT_STROKE_WIDTH = 24;
    private final float DEFAULT_TEXT_TOP_SIZE = 40;
    private final float DEFAULT_TEXT_BTM_SIZE = 24;
    private final float DEFAULT_SPLIT_LINE_WIDTH = 2;
    private final float DEFAULT_TEXT_MARGIN = 16;

    /**
     * 数据值对比360的转换率
     */
    private float total = 0;
    private float totalSingle = 1;

    /**
     * 默认的Padding
     */
    private final float DEFAULT_PADDING = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            16, getResources().getDisplayMetrics());

    /**
     * 控件中心坐标x, y, 外环半径， 内环半径
     */
    private float centerX, centerY, radiusOut, radiusIn;

    /**
     * 绘制所有用到的画笔
     */
    private Paint paintOut, paintIn, paintPro, paintLineDividing, paintTxtTop, paintTextBtm;

    /**
     * colorOut: 进度条背景色
     * colorIn: 圆心背景色
     * colorPro: 进度条的颜色
     * colorShadow:图表的阴影颜色
     */
    private int colorOut = getResources().getColor(R.color.circle_out),
            colorIn = getResources().getColor(R.color.circle_in),
            colorPro = getResources().getColor(R.color.circle_progress),
            colorShadow = getResources().getColor(R.color.black_alpha_1);
    /**
     * 用来画圆弧的RectF
     */
    private RectF rectF;
    /**
     * 进度条的宽度
     */
    private float strokeWidth = DEFAULT_STROKE_WIDTH;
    /**
     * 显示圆形进度条的头部
     */
    private boolean roundCap = Boolean.FALSE;
    /**
     * 进度条显示的进度
     */
    private float totalShow = 0;

    /**
     * 圆心位置显示的Text
     * sCenterText: 圆心位置的文本信息
     * colorCenterText: TextView文字颜色
     * centerTextSize: TextView文字大小
     */
    private String textTop = "顶部文字";
    private int colorTextTop = getResources().getColor(R.color.circle_text_top_color);
    private float textTopSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX,
            DEFAULT_TEXT_TOP_SIZE, getResources().getDisplayMetrics());

    /**
     * 中心文本绘制的baseLineX, baseLineY
     */
    private float fTextTopCenterX = 0, fTextTopCenterY = 0;

    /**
     * 显示的数据保留的小数点位数
     */
    private static final int DEFAULT_PRECISION = 0;
    private int precisionTop = 0;
    private int precisionBtm = 0;

    /**
     * 中心上下数据分割线
     */
    private int colorSplitLine = getResources().getColor(R.color.circle_split_line_color);
    private float splitLineWidth = DEFAULT_SPLIT_LINE_WIDTH;
    private float fSplitLineStartX = 0, fSplitLineEndX = 0, fSplitLineY = 0;

    /**
     * 中心位置下边文本/文本颜色/文本字体大小
     */
    private String textBtm = "底部文字";
    private int colorTextBtm = getResources().getColor(R.color.circle_text_bottom_color);
    private float textBtmSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX,
            DEFAULT_TEXT_BTM_SIZE, getResources().getDisplayMetrics());
    private float fTextBtmCenterX = 0, fTextBtmCenterY = 0;


    public CircleRingGraph(Context context) {
        super(context);
        this.context = context;
    }

    public CircleRingGraph(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initDefineAttr(context, attrs);
        initPaint();
    }

    public CircleRingGraph(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initDefineAttr(context, attrs);
        initPaint();
    }

    /**
     * Total表示金额总数
     */
    public void setTotal(float total) {
        this.total = total;
        this.textTop = getStrByPrecisionUp(this.total, precisionTop);
        this.textBtm = getStrByPrecisionUp(this.total, precisionBtm);
        this.totalSingle = 360 / this.total;
        this.totalShow = (totalSingle * this.total);
        doAnimation();
    }

    public void appendTotal(float appendTotal) {

        this.total += appendTotal;
        this.textTop = getStrByPrecisionUp(this.total, precisionTop);
        float fTotal = Float.parseFloat(textBtm) + appendTotal;
        this.textBtm = getStrByPrecisionUp(fTotal, precisionBtm);
        this.totalSingle = 360 / fTotal;
        this.totalShow = (totalSingle * this.total);
        doAnimation();
    }

    public double getTotal() {
        return total;
    }

    public float getTotalShow() {
        return this.totalShow;
    }

    public void setTarget(Target target) {

        if(target == null)
            return;

        this.total = Float.valueOf(target.getMoneyTotal());
        this.textTop = getStrByPrecisionUp(target.getMoneySurplus(), precisionTop);
        this.textBtm = getStrByPrecisionUp(target.getMoneyTotal(), precisionBtm);
        this.totalSingle = 360 / this.total;
        this.totalShow = (totalSingle * Float.valueOf(target.getMoneySurplus()));

        changeColor(this.total, Float.valueOf(target.getMoneySurplus()));
        doAnimation();
    }

    public void setSinglePayment(float paySingle) {

        this.total -= paySingle;
        this.textTop = getStrByPrecisionUp(this.total, precisionTop);
        this.totalShow = (this.total * totalSingle);
        doAnimation();
    }

    /**
     * 初始化计算设置每日消费金额
     */
//    public void setDailyPay(float dailyPay, int days) {
//
//        this.total = dailyPay;
//        this.textTop = getStrByPrecisionUp(this.total, precisionTop);
//        this.textBtm = getStrByPrecisionUp(days, precisionBtm) + "天";
//        this.totalSingle = 360 / this.total;
//        this.totalShow = (totalSingle * this.total);
//        doAnimation();
//    }

    /**
     * 初始化设置DailyPay
     * */
    public void setDailyPay(DailyPay dailyPay) {

        if(dailyPay == null)
            return;

        this.total = dailyPay.getDailyTotal();
        this.textTop = getStrByPrecisionUp(dailyPay.getDailySurplus(), precisionTop);
        this.textBtm = getStrByPrecisionUp(this.total, precisionBtm);
        this.totalSingle = 360 / this.total;
        this.totalShow = this.totalSingle * dailyPay.getDailySurplus();

        changeColor(this.total, dailyPay.getDailySurplus());
        doAnimation();
    }

    /**
     * 重新设置计算设置每日消费金额(追加金额情况下改变)
     */
//    public void resetDailyPay(float appendMoney) {
//
//        SharedPreferencesHelper helper = SharedPreferencesHelper.getInstance(context);
//
//        float currentTotal = helper.getFloat(TimerSetting.MONEY_TOTAL, 0);
//        float totalMoney = currentTotal + appendMoney;
//
//        int totalDays = helper.getInt(TimerSetting.DAYS_TOTAL, 1);
//        this.total = totalMoney / totalDays;
//    }

    /**
     * 设置显示对应小数点位数之后的数值
     */
    private String getStrByPrecisionUp(String sNumber, int precision) {

        if (!TextUtils.isEmpty(sNumber)) {

            BigDecimal temp = new BigDecimal(sNumber);

            return temp.setScale(precision, BigDecimal.ROUND_HALF_UP).toString();
        }

        return "";
    }

    private String getStrByPrecisionUp(float fNumber, int precision) {

        BigDecimal temp = new BigDecimal(fNumber);
        return temp.setScale(precision, BigDecimal.ROUND_HALF_UP).toString();
    }

    /**
     * 获取自定义的属性值
     */
    private void initDefineAttr(Context context, AttributeSet attrs) {

        if (attrs == null)
            return;

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleRingGraph);
        int count = typedArray.getIndexCount();
        for (int i = 0; i < count; i++) {

            int attr = typedArray.getIndex(i);
            switch (attr) {

                case R.styleable.CircleRingGraph_progressBackgroundColor:

                    colorOut = typedArray.getColor(attr,
                            ContextCompat.getColor(context, R.color.circle_out));
                    break;

                case R.styleable.CircleRingGraph_progressColor:

                    colorPro = typedArray.getColor(attr,
                            ContextCompat.getColor(context, R.color.circle_progress));
                    break;

                case R.styleable.CircleRingGraph_circleShadowColor:

                    colorShadow = typedArray.getColor(attr,
                            ContextCompat.getColor(context, R.color.black_alpha_1));
                    break;

                case R.styleable.CircleRingGraph_circleHeartColor:

                    colorIn = typedArray.getColor(attr,
                            ContextCompat.getColor(context, R.color.circle_in));
                    break;

                case R.styleable.CircleRingGraph_progressRoundCap:

                    roundCap = typedArray.getBoolean(attr, Boolean.FALSE);
                    break;

                case R.styleable.CircleRingGraph_progressWidth:

                    strokeWidth = typedArray.getDimension(attr, DEFAULT_STROKE_WIDTH);
                    break;

                case R.styleable.CircleRingGraph_circleTextTop:

                    textTop = typedArray.getString(attr);
                    break;

                case R.styleable.CircleRingGraph_circleTextTopColor:

                    colorTextTop = typedArray.getColor(attr,
                            ContextCompat.getColor(context, R.color.colorBlack));
                    break;

                case R.styleable.CircleRingGraph_circleTextTopSize:

                    textTopSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX,
                            typedArray.getDimensionPixelSize(attr, (int) DEFAULT_TEXT_TOP_SIZE), displayMetrics);
                    break;

                case R.styleable.CircleRingGraph_centerTextPrecisionTop:

                    precisionTop = typedArray.getInt(attr, DEFAULT_PRECISION);
                    break;

                case R.styleable.CircleRingGraph_centerTextPrecisionBtm:

                    precisionBtm = typedArray.getInt(attr, DEFAULT_PRECISION);
                    break;

                case R.styleable.CircleRingGraph_circleSplitLineColor:

                    colorSplitLine = typedArray.getColor(attr,
                            getResources().getColor(R.color.circle_split_line_color));
                    break;

                case R.styleable.CircleRingGraph_circleSplitLineWidth:

                    splitLineWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX,
                            typedArray.getDimensionPixelSize(attr, (int) DEFAULT_SPLIT_LINE_WIDTH), displayMetrics);
                    break;

                case R.styleable.CircleRingGraph_circleTextBtm:

                    textBtm = typedArray.getString(attr);
                    break;

                case R.styleable.CircleRingGraph_circleTextBtmColor:

                    colorTextBtm = typedArray.getColor(attr,
                            getResources().getColor(R.color.circle_text_bottom_color));
                    break;

                case R.styleable.CircleRingGraph_circleTextBtmSize:

                    textBtmSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX,
                            typedArray.getDimensionPixelSize(attr, (int) DEFAULT_TEXT_BTM_SIZE), displayMetrics);
                    break;

                default:
                    break;
            }
        }

        typedArray.recycle();

    }

    /**
     * init Paint
     */
    private void initPaint() {

        setLayerType(LAYER_TYPE_SOFTWARE, null);

        paintOut = new Paint();
        paintOut.setAntiAlias(true);
        paintOut.setStyle(Paint.Style.FILL);
        paintOut.setColor(colorOut);
        paintOut.setShadowLayer(16, 0, 0, colorShadow);

        paintIn = new Paint();
        paintIn.setAntiAlias(true);
        paintIn.setStyle(Paint.Style.FILL);
        paintIn.setColor(colorIn);
        paintIn.setShadowLayer(16, 0, 0, colorShadow);

        paintPro = new Paint();
        paintPro.setAntiAlias(true);
        if (roundCap)
            paintPro.setStrokeCap(Paint.Cap.ROUND);
        paintPro.setStyle(Paint.Style.STROKE);
        paintPro.setStrokeWidth(strokeWidth);
        paintPro.setColor(colorPro);

        //setMaskFilter
//        paintPro.setMaskFilter(new BlurMaskFilter(1, BlurMaskFilter.Blur.INNER));
        //set shadow
//        paintPro.setShadowLayer(40, 0, 0, getResources().getColor(R.color.black_alpha_3));

        //雷达渐变
//        Shader shader = new SweepGradient(centerX, centerY,
//                ContextCompat.getColor(context, R.color.colorPink),
//                ContextCompat.getColor(context, R.color.colorPurple));
//        paintPro.setShader(shader);
        //线性渐变
//        Shader shaderLinear = new LinearGradient(0, 0, 200, 0, Color.WHITE, Color.RED,
//                Shader.TileMode.CLAMP);

        paintTxtTop = new Paint();
        paintTxtTop.setAntiAlias(true);
        paintTxtTop.setTextSize(textTopSize);
        paintTxtTop.setTextAlign(Paint.Align.CENTER);
        paintTxtTop.setColor(colorTextTop);

        paintLineDividing = new Paint();
        paintLineDividing.setAntiAlias(true);
        paintLineDividing.setStyle(Paint.Style.FILL);
        paintLineDividing.setStrokeWidth(splitLineWidth);
        paintLineDividing.setColor(colorSplitLine);
        paintLineDividing.setStrokeCap(Paint.Cap.ROUND);

        paintTextBtm = new Paint();
        paintTextBtm.setAntiAlias(true);
        paintTextBtm.setTextSize(textBtmSize);
        paintTextBtm.setTextAlign(Paint.Align.CENTER);
        paintTextBtm.setColor(colorTextBtm);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        radiusOut = Math.min(w, h) / 2 - DEFAULT_PADDING;
        radiusIn = radiusOut - strokeWidth;
        centerX = w / 2;
        centerY = h / 2;

        float left = centerX - radiusOut + strokeWidth / 2;
        float top = centerY - radiusOut + strokeWidth / 2;
        float right = left + radiusOut * 2 - strokeWidth;
        float bottom = top + radiusOut * 2 - strokeWidth;
        rectF = new RectF(left, top, right, bottom);

        //计算横向分割线的起止点坐标
        fSplitLineStartX = centerX - (radiusIn / 3 * 2);
        fSplitLineEndX = centerX + (radiusIn / 3 * 2);
        fSplitLineY = centerY + (radiusIn / 4);

        //根据横向分割线坐标计算文本中心坐标（顶部）
        fTextTopCenterX = centerX;
        fTextTopCenterY = fSplitLineY - (textTopSize / 3 + DEFAULT_TEXT_MARGIN / 2);

        //根据横向分割线坐标计算文本中心坐标（底部）
        fTextBtmCenterX = centerX;
        fTextBtmCenterY = fSplitLineY + textBtmSize * 2 / 3 + DEFAULT_TEXT_MARGIN;

        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        drawCircleRing(canvas);
        super.onDraw(canvas);
    }

    /**
     * draw circle ring
     */
    private void drawCircleRing(Canvas canvas) {

        // 画灰色的圆
        canvas.drawCircle(centerX, centerY, radiusOut, paintOut);

        // 画进度环
        if (totalShow != 0f)
            canvas.drawArc(rectF, -90, totalShow, false, paintPro);

        // 画内填充圆
        canvas.drawCircle(centerX, centerY, radiusIn, paintIn);

        //绘制上下文本分割线
        canvas.drawLine(fSplitLineStartX, fSplitLineY, fSplitLineEndX, fSplitLineY, paintLineDividing);

        //画中心位置文字(上部currentTotal)
        canvas.drawText(textTop, fTextTopCenterX, fTextTopCenterY, paintTxtTop);

        //画中心位置文字(底部Total)
        canvas.drawText(textBtm, fTextBtmCenterX, fTextBtmCenterY, paintTextBtm);
    }

    public void doAnimation() {

        ValueAnimator animator = ValueAnimator.ofObject(new ProgressEvaluator(), 0f, totalShow);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                totalShow = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        animator.setDuration(400);
        animator.setInterpolator(new OvershootInterpolator());
        animator.start();
    }

    private class ProgressEvaluator implements TypeEvaluator<Float> {
        @Override
        public Float evaluate(float fraction, Float startValue, Float endValue) {

            return startValue + fraction * (endValue - startValue);
        }
    }

    private void changeColor(float total, float surplus) {

        if(surplus < 0)
            colorPro = ContextCompat.getColor(context, R.color.circle_progress_red);
        else if(total/2 > surplus)
            colorPro = ContextCompat.getColor(context, R.color.circle_progress_blue);
        else
            colorPro = ContextCompat.getColor(context, R.color.circle_progress_green);
    }
}

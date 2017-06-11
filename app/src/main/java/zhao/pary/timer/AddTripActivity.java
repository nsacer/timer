package zhao.pary.timer;

import android.animation.ObjectAnimator;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.srx.widget.TabBarView;

import java.util.Calendar;
import java.util.Timer;

import tyrantgit.explosionfield.ExplosionField;

public class AddTripActivity extends BaseActivity {

    /**
     * TextClock
     */
    private Timer timer = new Timer();
    /**
     * TextInputLayout
     */
    private TextInputLayout layoutTitle, layoutTarget;
    /**
     * EditText标题和目标
     */
    private EditText etTitle, etTarget;
    /**
     * EditText的输入内容是否合法
     */
    private boolean bTitle = false, bTarget = false;

    /**
     * 任意View的爆炸效果
     */
    private ExplosionField field;

    /**
     * 点击横向展开的menu菜单
     */
    private TabBarView tabBarView;

    /**
     * Fab 提交按钮
     */
    private FloatingActionButton fabSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);

        initView();
    }

    private void initView() {

        findView();

        initEditTextEvent();

        field = ExplosionField.attach2Window(this);

    }

    /**
     * 查找控件
     */
    private void findView() {

        layoutTitle = (TextInputLayout) findViewById(R.id.layout_title);
        layoutTarget = (TextInputLayout) findViewById(R.id.layout_target);

        etTitle = (EditText) findViewById(R.id.et_title);
        etTarget = (EditText) findViewById(R.id.et_target);

        fabSubmit = (FloatingActionButton) findViewById(R.id.fab_submit);

        TextView tvTimeStart = (TextView) findViewById(R.id.tv_time_start);
        assert tvTimeStart != null;
        tvTimeStart.setText(getFormatDate());
        TextView tvTimeEnd = (TextView) findViewById(R.id.tv_time_end);
        assert tvTimeEnd != null;
        tvTimeEnd.setText(getFormatDate());
    }

    /**
     * 获取当前年月日
     */
    private CharSequence getFormatDate() {

        return DateUtils.formatDateTime(this, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_YEAR |
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_WEEKDAY);
    }

    /**
     * 设置EditText相关事件
     */
    private void initEditTextEvent() {

        formatEditTextEvent(etTitle);
        formatEditTextEvent(etTarget);
    }

    /**
     * 设置EditText的TextChangeListener
     */
    private void formatEditTextEvent(final EditText et) {

        //用来判断输入框输入内容是否合法
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                ((TextInputLayout) et.getParent()).setError(null);

                boolean bResult = Boolean.FALSE;

                if (s.length() > 3)
                    bResult = Boolean.TRUE;

                switch (et.getId()) {

                    case R.id.et_title:
                        bTitle = bResult;
                        break;
                    case R.id.et_target:
                        bTarget = bResult;
                        break;
                    default:
                        break;
                }

            }
        });

        //DrawableRight 点击清空所有输入的信息
        et.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {

                    case MotionEvent.ACTION_UP:

                        Drawable drawable = et.getCompoundDrawables()[2];
                        if (drawable != null &&
                                event.getRawX() >= (et.getRight() - drawable.getBounds().width())) {

                            if (et.getText().length() > 0)
                                et.setText(null);
                        }
                        break;
                }
                return false;
            }
        });

        //用来设置输入框失去焦点时候清空Error信息
        et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (!hasFocus)
                    ((TextInputLayout) et.getParent()).setError(null);
            }
        });
    }

    /**
     * 开始时间TextView的点击事件
     */
    public void setTripStartTime(View view) {

        popDatePickerDialog(view);
    }

    /**
     * 开始结束TextView的点击事件
     */
    public void setTripEndTime(View view) {

        popDatePickerDialog(view);
    }

    /**
     * 点击Fab提交按钮
     */
    public void submitInfo(View view) {

        //爆炸效果
        field.explode(fabSubmit);
        view.setOnClickListener(null);

        if (checkEditTextInout()) {

            view.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary)));
            bTitle = bTarget = false;
        } else {
            view.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorAccent)));
            ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationX", 0, 20, -20, 20, 0);
            animator.setDuration(400);
            animator.start();
        }

        startActivity(new Intent(this, TestActivity.class));
    }

    /**
     * Dialog提交按钮点击判断EditText处理
     */
    private boolean checkEditTextInout() {

        if (!bTitle) {

            layoutTitle.setError("Error Warning");
            return bTitle;
        }
        if (!bTarget) {

            layoutTarget.setError("Target not empty");
            return bTarget;
        }

        return true;
    }

    /**
     * 弹出时间选择Dialog
     */
    private void popDatePickerDialog(final View timeView) {

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        String time = year + "年" + (monthOfYear + 1) + "月" + dayOfMonth + "日";
                        ((TextView) timeView).setText(time);
                    }
                }, year, month, day);

        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null)
            timer.cancel();
    }
}

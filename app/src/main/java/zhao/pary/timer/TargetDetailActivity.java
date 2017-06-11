package zhao.pary.timer;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;
import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import adapter.DayPayChartAdapter;
import customview.CircleRingGraph;
import model.DailyPay;
import model.DailyPayBills;
import model.Target;
import utils.DateUtil;
import utils.MathUtil;

@ContentView(R.layout.activity_target_detial)
public class TargetDetailActivity extends BaseActivity implements View.OnClickListener {

    private static final int TEXT_SIZE_UNABLE = 20;

    private Target target;

    private DailyPay dailyPay;

    private DateUtil dateUtil;

    private DbManager dbManager = x.getDb(TimerApplication.getInstance().getDaoConfig());

    @ViewInject(R.id.toolbar_target_detail)
    private Toolbar toolbar;

    @ViewInject(R.id.appbar_target_detail)
    private AppBarLayout appBarLayout;

    @ViewInject(R.id.collapsing_target_detail)
    private CollapsingToolbarLayout collapsing;

    @ViewInject(R.id.et_money)
    private EditText etMoney;

    @ViewInject(R.id.et_money_append)
    private EditText etMoneyAppend;

    @ViewInject(R.id.iv_money_target_done)
    private ImageView ivMoney;

    @ViewInject(R.id.iv_money_append_remove)
    private ImageView ivAppendRemove;

    @ViewInject(R.id.iv_money_append_add)
    private ImageView ivAppendAdd;

    @ViewInject(R.id.tv_time_start)
    private TextView tvTimeStart;

    @ViewInject(R.id.tv_time_end)
    private TextView tvTimeEnd;

    @ViewInject(R.id.crg_total)
    private CircleRingGraph crgTotal;

    @ViewInject(R.id.crg_daily)
    private CircleRingGraph crgDaily;

    @ViewInject(R.id.ts_day_surplus)
    private TextSwitcher tsDaysSurplus;

    @ViewInject(R.id.et_pay_input)
    private EditText etPayInput;

    @ViewInject(R.id.iv_pay_done)
    private ImageView ivPayDone;

    @ViewInject(R.id.fab_target_start)
    private FloatingActionButton fabTargetStart;

    @ViewInject(R.id.list_view_pay_item)
    private ListView lvPayItem;

    @ViewInject(R.id.tv_expense_total)
    private TextView tvExpenseTotal;

    @ViewInject(R.id.recyclerView_chart)
    private RecyclerView rvChart;

    private String sTimeStart, sTimeEnd;

    private ArrayAdapter<String> arrayAdapter;
    private List<String> payItemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dateUtil = DateUtil.getInstance();

        initDbData();

        initView();
    }

    private void initDbData() {

        target = selectTargetFromDb();

        if (target != null) {

            findOrCreateDailyPay();
            if (dailyPay != null) {

                List<DailyPayBills> billses = new ArrayList<>();
                try {
                    billses = dbManager.selector(DailyPayBills.class)
                            .where("dailyPayId", "=", dailyPay.getId()).findAll();
                } catch (DbException e) {
                    e.printStackTrace();
                }

                if (billses != null && !billses.isEmpty()) {

                    for (DailyPayBills bills : billses) {

                        payItemList.add(bills.getPayDetail());
                    }
                }
            }
        }
    }

    /**
     * 从数据库中查找传递id的Target;
     */
    private Target selectTargetFromDb() {

        int id = getIntent().getIntExtra(TimerSetting.TARGET_TO_DETAIL_ID, -1);

        Target target = null;
        try {
            target = dbManager.selector(Target.class).where("id", "=", id).findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }

        return target;
    }

    private void initView() {

        initToolbar();

        initAppBarLayout();

        initCollapsingToolbar();

        initHeaderView();

        initPayInput();

        initListViewForPayItem();

        initRecyclerViewChart();
    }

    private void initToolbar() {

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this,
                R.mipmap.ic_arrow_back_white_24dp));

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });
    }

    private void initAppBarLayout() {

        boolean isExpanded = target == null;
        appBarLayout.setExpanded(isExpanded);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                int total = appBarLayout.getTotalScrollRange();
                if (total == Math.abs(verticalOffset) && toolbar.getNavigationIcon() != null)
                    DrawableCompat.setTint(toolbar.getNavigationIcon(),
                            ContextCompat.getColor(TargetDetailActivity.this, R.color.colorWhite));
                else if (toolbar.getNavigationIcon() != null)
                    DrawableCompat.setTint(toolbar.getNavigationIcon(),
                            ContextCompat.getColor(TargetDetailActivity.this, R.color.colorAccent));

            }
        });

    }

    private void initCollapsingToolbar() {

        String sTimeTitle = dateUtil.getDateFormat(System.currentTimeMillis());

        collapsing.setTitle(sTimeTitle);
        collapsing.setExpandedTitleColor(ContextCompat.getColor(this, R.color.colorAccent));
    }

    private void initHeaderView() {

        setFocusChangeListener(etMoneyAppend);
        formatMoneyInput(etMoneyAppend);
        ivAppendRemove.setOnClickListener(this);
        ivAppendAdd.setOnClickListener(this);

        setFocusChangeListener(etPayInput);
        formatMoneyInput(etPayInput);

        if (target == null) {

            setFocusChangeListener(etMoney);
            formatMoneyInput(etMoney);

            ivMoney.setOnClickListener(this);
            tvTimeStart.setOnClickListener(this);
            tvTimeEnd.setOnClickListener(this);
            fabTargetStart.setOnClickListener(this);
        } else {

            if (!target.getInProgress()) {

                ivAppendAdd.setEnabled(false);
                ivAppendRemove.setEnabled(false);
                ivAppendAdd.setBackground(ContextCompat.getDrawable(this,
                        R.drawable.shape_left_rect_right_corner_unable));
                ivAppendRemove.setBackground(ContextCompat.getDrawable(this,
                        R.drawable.shape_left_corner_right_rect_unable));
            }

            ivPayDone.setOnClickListener(this);

            etMoney.setText(target.getMoneyTotal());
            setEtMoneyInputUnable(etMoney);

            setIvMoneyDoneUnable();

            tvTimeStart.setText(target.getDateStart());
            setTvDateUnable(tvTimeStart);

            tvTimeEnd.setText(target.getDateEnd());
            setTvDateUnable(tvTimeEnd);

            setFabDoneEnable();

            crgTotal.setTarget(target);

            crgDaily.setDailyPay(dailyPay);
        }

    }

    /**
     * init 单笔消费输入框
     */
    private void initPayInput() {

        tsDaysSurplus.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                return createTextView();
            }
        });
        tsDaysSurplus.setText("0");
        if (target != null) {

            tsDaysSurplus.setText(target.getDaysSurplus());
            ivPayDone.setEnabled(target.getInProgress());
            if (!target.getInProgress())
                ivPayDone.setBackground(ContextCompat.getDrawable(this,
                        R.drawable.shape_left_rect_right_corner_unable));
        }
    }

    /**
     * init ListView for pay item list
     */
    private void initListViewForPayItem() {

        if (target == null)
            return;
        lvPayItem.setDivider(ContextCompat.getDrawable(this, R.drawable.divider_pay_item_list));
        arrayAdapter = new ArrayAdapter<>(this,
                R.layout.item_daily_pay_list, payItemList);
        lvPayItem.setAdapter(arrayAdapter);
        setListViewHeightBasedOnChildren(lvPayItem);

        String expenseTotal = getResources().getString(R.string.target_expense_total) +
                MathUtil.getStrByPrecisionUp(dailyPay.getDailyExpense(), 2) + "元";
        tvExpenseTotal.setText(expenseTotal);

    }

    /**
     * init RecyclerView for day expense chart
     */
    private void initRecyclerViewChart() {

        rvChart.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvChart.setHasFixedSize(true);

        if (target == null)
            return;
        DayPayChartAdapter adapter = new DayPayChartAdapter(dbManager, target);
        rvChart.setAdapter(adapter);
    }


    /**
     * fab完成按钮的点击事件
     */
    private void checkInput() {

        if (TextUtils.isEmpty(etMoney.getText().toString().trim())) {

            doShockAnimation(etMoney);
        } else if (TextUtils.isEmpty(tvTimeStart.getText().toString())) {

            doShockAnimation(tvTimeStart);
        } else if (TextUtils.isEmpty(tvTimeEnd.getText().toString())) {

            doShockAnimation(tvTimeEnd);
        } else {

            checkTargetMoneyInput();
            saveTargetToDb(etMoney.getText().toString(),
                    tvTimeStart.getText().toString(), tvTimeEnd.getText().toString());
        }
    }

    /**
     * Check Target Money Input
     */
    private void checkTargetMoneyInput() {

        if (TextUtils.isEmpty(etMoney.getText().toString().trim()))
            doShockAnimation(etMoney);
        else {

            setEtMoneyInputUnable(etMoney);

            setIvMoneyDoneUnable();

            crgTotal.setTotal(Float.valueOf(etMoney.getText().toString()));
        }
    }

    /**
     * Check Target Money Append Input
     */
    private void checkTargetMoneyAppendInput(View vClick, EditText etInput) {

        boolean isAdd = (vClick.getId() == R.id.iv_money_append_add);

        if (TextUtils.isEmpty(etInput.getText().toString().trim()))
            doShockAnimation(etInput);
        else {

            double iMoney = 0;
            if (!TextUtils.isEmpty(etMoney.getText().toString().trim()))
                iMoney = Double.parseDouble(etMoney.getText().toString());

            double iAppend = Double.parseDouble(etInput.getText().toString());

            etInput.setText(null);
            etInput.clearFocus();

            if (isAdd) {

                double iTotal = iMoney + iAppend;
                etMoney.setText(String.valueOf(iTotal));
                setEtMoneyInputUnable(etMoney);

                setIvMoneyDoneUnable();

                if (target != null) {

                    updateTarget(String.valueOf(iTotal),
                            String.valueOf(Float.valueOf(target.getMoneySurplus()) + iAppend));
                    crgTotal.setTarget(target);

                    updateDailyPayTotal();
                    crgDaily.setDailyPay(dailyPay);
                }

            } else {

                double iTotal = iMoney - iAppend;
                if (iTotal >= 0) {

                    etMoney.setText(String.valueOf(iTotal));
                    setEtMoneyInputUnable(etMoney);

                    setIvMoneyDoneUnable();

                    if (target != null) {

                        updateTarget(String.valueOf(iTotal),
                                String.valueOf(Float.valueOf(target.getMoneySurplus()) - iAppend));
                        crgTotal.setTarget(target);

                        updateDailyPayTotal();
                        crgDaily.setDailyPay(dailyPay);
                    }

                } else {

                    etInput.setHint(getResources().getString(R.string.target_money_remove_error));
                    doShockAnimation(etInput);
                }
            }

            etInput.setHintTextColor(ContextCompat.getColor(this, R.color.target_text_hint));
        }
    }

    /**
     * Check daily pay input
     */
    private void checkTargetPayInput() {

        if (TextUtils.isEmpty(etPayInput.getText().toString().trim()))
            doShockAnimation(etPayInput);
        else {

            float dPay = Float.valueOf(etPayInput.getText().toString());

            if (target != null) {

                float moneySurplus = Float.valueOf(target.getMoneySurplus()) - dPay;
                updateTarget(null, String.valueOf(moneySurplus));
                crgTotal.setTarget(target);

                updateDailyPay(dPay);
                crgDaily.setDailyPay(dailyPay);
                etPayInput.clearFocus();
                etPayInput.setText(null);
            }
        }
    }

    /**
     * EditText的焦点监听
     */
    private void setFocusChangeListener(EditText etInput) {

        etInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (!hasFocus)
                    hideInputMethod(v);
            }
        });
    }

    /**
     * 设置EditText的小数点后位数（2位）
     */
    private void formatMoneyInput(EditText etMoney) {

        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

                if ("".equals(source.toString()))
                    return null;

                String sInput = dest.toString();
                String[] sArray = sInput.split("\\.");
                if (sArray.length > 1) {

                    String sDotValue = sArray[1];
                    int iDiff = sDotValue.length() + 1 - 2;
                    if (iDiff > 0)
                        return source.subSequence(start, end - iDiff);
                }
                return null;
            }
        };

        etMoney.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(7)});
    }

    /**
     * EditText的横向震动效果
     */
    private void doShockAnimation(View target) {

        ObjectAnimator animator = ObjectAnimator.ofFloat(target, "translationX",
                0f, -24f, 24f, -24f, 24f, 0f);
        animator.setDuration(200);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
    }

    /**
     * 弹出时间选择Dialog
     */
    private void popDatePickerDialog(final View timeView, final boolean isStart) {

        hideInputMethod(timeView);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int monthBefore = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        final String sToday = year + "/" + (monthBefore + 1) + "/" + day;

        DatePickerDialog dialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        String time = year + "/" + (monthOfYear + 1) + "/" + dayOfMonth;
                        if (isStart) {

                            //判断日期是否小于当前日
                            long currentToday = dateUtil.getTimeInMillisFromString(sToday);
                            long selectDate = dateUtil.getTimeInMillisFromString(time);
                            long selectEnd = dateUtil.getTimeInMillisFromString(sTimeEnd);

                            if (currentToday > selectDate) {

                                tvTimeStart.setHint(getResources().getString(R.string.target_time_after_current));
                                doShockAnimation(tvTimeStart);
                            } else {

                                //是否已经选择了结束日
                                if (TextUtils.isEmpty(sTimeEnd) || selectDate < selectEnd) {

                                    sTimeStart = time;
                                    ((TextView) timeView).setText(time);
                                } else {

                                    doShockAnimation(tvTimeStart);
                                }
                            }
                        } else {

                            if (TextUtils.isEmpty(sTimeStart)) {

                                tvTimeStart.setHint(getResources().getString(R.string.target_time_start_first));
                                doShockAnimation(tvTimeStart);
                            } else {

                                sTimeEnd = time;
                                if (dateUtil.getTimeInMillisFromString(sTimeEnd) -
                                        dateUtil.getTimeInMillisFromString(sTimeStart) > 0) {

                                    ((TextView) timeView).setText(sTimeEnd);
                                    int currentDays = dateUtil.getCurrentDays(sTimeStart, sTimeEnd);
                                    tsDaysSurplus.setText(String.valueOf(currentDays));

                                    if (!TextUtils.isEmpty(etMoney.getText())) {

                                        float dailyTotal = Float.valueOf(etMoney.getText().toString()) /
                                                currentDays;
                                        DailyPay dailyPay = new DailyPay(0, dailyTotal, 0);
                                        crgDaily.setDailyPay(dailyPay);
                                    }

                                } else {

                                    ((TextView) timeView).setHint(
                                            getResources().getString(R.string.target_time_after_must));
                                    doShockAnimation(timeView);
                                }
                            }
                        }
                    }
                }, year, monthBefore, day);

        dialog.show();
    }

    /**
     * 收起键盘
     */
    private void hideInputMethod(View view) {

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive())
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * 将生成的数据存储到数据库中
     */
    private void saveTargetToDb(String moneyTotal, String dateStart, String dateEnd) {

        Target target = new Target(moneyTotal, dateStart, dateEnd);
        Boolean bAdd = Boolean.FALSE;
        try {
            bAdd = dbManager.saveBindingId(target);
            TimerSetting.DB_UPDATE = true;
        } catch (DbException e) {
            e.printStackTrace();
        }

        String sToast = getResources().getString(R.string.trip_add_fail);
        if (bAdd) {

            sToast = getResources().getString(R.string.trip_add_success);
            this.target = target;
            crgTotal.setTarget(this.target);

            DailyPay dailyPay = new DailyPay(target.getId(), Float.valueOf(target.getMoneyDaily()), 0);
            boolean bAddDaily = false;
            try {
                bAddDaily = dbManager.saveBindingId(dailyPay);
            } catch (DbException e) {
                e.printStackTrace();
            }

            if (bAddDaily) {

                this.dailyPay = dailyPay;
                crgDaily.setDailyPay(this.dailyPay);
            }

            setTvDateUnable(tvTimeStart);
            setTvDateUnable(tvTimeEnd);
            appBarLayout.setExpanded(false);
            setFabDoneEnable();
            tsDaysSurplus.setText(this.target.getDaysSurplus());
            ivPayDone.setOnClickListener(this);
        }

        Toast.makeText(this, sToast, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.iv_money_target_done:

                checkTargetMoneyInput();

                break;

            case R.id.iv_money_append_remove:

                checkTargetMoneyAppendInput(v, etMoneyAppend);
                break;

            case R.id.iv_money_append_add:

                checkTargetMoneyAppendInput(v, etMoneyAppend);
                break;

            case R.id.tv_time_start:

                popDatePickerDialog(v, Boolean.TRUE);
                break;

            case R.id.tv_time_end:

                popDatePickerDialog(v, Boolean.FALSE);
                break;

            case R.id.iv_pay_done:

                checkTargetPayInput();
                break;

            case R.id.fab_target_start:

                checkInput();
                break;

            default:
                break;
        }
    }

    /**
     * 设置Total金额输入框不可用
     */
    private void setEtMoneyInputUnable(EditText etMoneyInput) {

        etMoneyInput.setTextSize(TEXT_SIZE_UNABLE);
        etMoneyInput.setTextColor(ContextCompat.getColor(this, R.color.target_text_unable));
        etMoneyInput.setEnabled(false);
    }

    /**
     * 设置Target金额输入框后边完成按钮图标不可用
     */
    private void setIvMoneyDoneUnable() {

        ivMoney.setEnabled(false);
        ivMoney.setBackground(ContextCompat.getDrawable(this,
                R.drawable.shape_left_rect_right_corner_unable));
    }

    /**
     * 设置DateStart和DateEnd不可用
     */
    private void setTvDateUnable(TextView tvDate) {

        tvDate.setTextColor(ContextCompat.getColor(this, R.color.target_text_unable));
        tvDate.setEnabled(false);
    }

    /**
     * 设置Fab完成按钮不可用
     */
    private void setFabDoneEnable() {

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(ObjectAnimator.ofFloat(fabTargetStart, "rotation", 0f, 180f));
        animatorSet.setDuration(200);
        animatorSet.start();

        fabTargetStart.setEnabled(false);
    }

    /**
     * 更新Target信息
     *
     * @param moneyTotal   target总金额
     * @param moneySurplus target剩余金额
     */
    private void updateTarget(String moneyTotal, String moneySurplus) {

        if (!TextUtils.isEmpty(moneySurplus))
            target.setMoneySurplus(moneySurplus);

        if (!TextUtils.isEmpty(moneyTotal)) {

            target.setMoneyTotal(moneyTotal);
            target.setMoneyDaily(String.valueOf(Float.valueOf(target.getMoneySurplus()) /
                    Integer.valueOf(target.getDaysSurplus())));
        }

        try {
            dbManager.update(target);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * init DailyPay
     */
    private void findOrCreateDailyPay() {

        int currentDays = dateUtil.getCurrentDays(target.getDateStart(),
                dateUtil.getDateFormat(System.currentTimeMillis()));
        try {
            dailyPay = dbManager.selector(DailyPay.class).where("id", "=", currentDays).findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }

        if (dailyPay == null)
            dailyPay = new DailyPay(target.getId(), Float.valueOf(target.getMoneyDaily()), 0);
    }

    /**
     * 更新DailyPay信息
     */
    private void updateDailyPayTotal() {

        if (dailyPay != null) {

            dailyPay.setDailyTotal(Float.valueOf(target.getMoneyDaily()));
            dailyPay.setDailySurplus(dailyPay.getDailyTotal() - dailyPay.getDailyExpense());

            try {
                dbManager.update(dailyPay);
            } catch (DbException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 更新DailyPay消费信息
     *
     * @param singlePay 单笔消费金额（都为正数）
     */
    private void updateDailyPay(float singlePay) {

        if (dailyPay != null) {

            dailyPay.setDailyExpense(dailyPay.getDailyExpense() + singlePay);
            dailyPay.setDailySurplus(dailyPay.getDailySurplus() - singlePay);
            try {
                dbManager.update(dailyPay);
            } catch (DbException e) {
                e.printStackTrace();
            }

            String sPayDetail = dateUtil.getDateFormatDetail(System.currentTimeMillis()) +
                    "-" + MathUtil.getStrByPrecisionUp(singlePay, 2) + "元";
            DailyPayBills dailyPayBills = new DailyPayBills(dailyPay.getId(), sPayDetail);
            boolean bAddBills = false;
            try {
                bAddBills = dbManager.saveBindingId(dailyPayBills);
            } catch (DbException e) {
                e.printStackTrace();
            }
            if (bAddBills) {

                payItemList.add(dailyPayBills.getPayDetail());
                arrayAdapter.notifyDataSetChanged();
                setListViewHeightBasedOnChildren(lvPayItem);
                tvExpenseTotal.setText(getResources().getString(R.string.target_expense_total) +
                        String.valueOf(dailyPay.getDailyExpense()) + "元");
            }

        }
    }

    /**
     * TextSwitch内创建的index指示器的TextView
     */
    private TextView createTextView() {

        TextView tv = new TextView(this);
        tv.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        tv.setTextSize(28);
        tv.setTextColor(Color.WHITE);
        tv.setGravity(Gravity.CENTER);

        return tv;
    }

    /**
     * 动态计算ListView的高度
     */
    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }
}

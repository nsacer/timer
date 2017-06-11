package adapter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import org.xutils.DbManager;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import customview.CircleRingGraph;
import model.DailyPay;
import model.DailyPayBills;
import model.Target;
import utils.DateUtil;
import utils.MathUtil;
import zhao.pary.timer.R;
import zhao.pary.timer.TargetDetailActivity;
import zhao.pary.timer.TimerSetting;

/**
 * 首页TripItem的Adapter
 */

public class MainPagerAdapter extends PagerAdapter {

    private Context context;

    private DbManager dbManager;

    private LayoutInflater inflater;

    private ArrayList<View> views = new ArrayList<>();

    private ArrayList<Target> targets = new ArrayList<>();

    private LinkedList<View> cacheViews = new LinkedList<>();

    private View viewAdd;

    public MainPagerAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setCardViews(List<View> views) {

        views.clear();
        for (View card : views)
            views.add(card);
    }

    public List<View> getCardViews() {
        return views;
    }

    public void addView(View view) {
        views.add(view);
        notifyDataSetChanged();
    }

    public ArrayList<Target> getTargets() {
        return targets;
    }

    public void setTargets(ArrayList<Target> targets) {
        this.targets = targets;
        notifyDataSetChanged();
    }

    public void setDbManager(DbManager dbManager) {

        this.dbManager = dbManager;
    }

    @Override
    public int getCount() {
        return targets.size() + 1;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        final ViewHolder holder;
        View convertView;

        if (position != targets.size()) {

            final Target target = targets.get(position);
            final int targetId = target.getId();

            if (cacheViews.size() == 0) {

                convertView = inflater.inflate(R.layout.item_card, container, false);
                holder = new ViewHolder();
                holder.cardView = (CardView) convertView.findViewById(R.id.card_view_target);
                holder.crgTotal = (CircleRingGraph) convertView.findViewById(R.id.crg_total_card);
                holder.crgDaily = (CircleRingGraph) convertView.findViewById(R.id.crg_daily_card);
                holder.tsDaysSurplus = (TextSwitcher) convertView.findViewById(R.id.ts_day_surplus);
                holder.etPayInput = (EditText) convertView.findViewById(R.id.et_pay_input);
                holder.ivPayDone = (ImageView) convertView.findViewById(R.id.iv_pay_done);

                convertView.setTag(holder);
            } else {

                convertView = cacheViews.removeFirst();
                holder = (ViewHolder) convertView.getTag();
            }

            //整个View的点击事件
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context, TargetDetailActivity.class);
                    intent.putExtra(TimerSetting.TARGET_TO_DETAIL_ID, targetId);
                    context.startActivity(intent);
                }
            });

            holder.crgTotal.setTarget(target);

            DailyPay dailyPay = null;
            try {
                long count = dbManager.selector(DailyPay.class).where("targetId", "=", targetId).count();
                dailyPay = dbManager.selector(DailyPay.class).where("id", "=", count).findFirst();
            } catch (DbException e) {
                e.printStackTrace();
            }

            if (dailyPay != null)
                holder.crgDaily.setDailyPay(dailyPay);

            initTextSwitcher(holder.tsDaysSurplus, target);

            holder.etPayInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {

                    if(!hasFocus)
                        hideInputMethod(v);
                }
            });

            if(target.getInProgress()) {

                holder.ivPayDone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (TextUtils.isEmpty(holder.etPayInput.getText().toString().trim()))
                            doShockAnimation(holder.etPayInput);
                        else {

                            float dPay = Float.valueOf(holder.etPayInput.getText().toString());
                            float moneySurplus = Float.valueOf(target.getMoneySurplus()) - dPay;
                            updateTarget(target, null, String.valueOf(moneySurplus));
                            holder.crgTotal.setTarget(target);

                            DailyPay dailyPay = null;
                            try {
                                long count = dbManager.selector(DailyPay.class).where("targetId", "=", targetId).count();
                                dailyPay = dbManager.selector(DailyPay.class).where("id", "=", count).findFirst();
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                            if(dailyPay != null) {

                                updateDailyPay(dailyPay, dPay);
                                holder.crgDaily.setDailyPay(dailyPay);
                                holder.etPayInput.clearFocus();
                                holder.etPayInput.setText(null);
                            }
                        }

                    }
                });
            } else {

                holder.ivPayDone.setEnabled(false);
                holder.ivPayDone.setBackground(ContextCompat.getDrawable(context,
                        R.drawable.shape_left_rect_right_corner_unable));
            }

        } else {

            if (viewAdd == null)
                viewAdd = convertView = inflater.inflate(R.layout.item_add, container, false);
            else
                convertView = viewAdd;

            viewAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //跳转到目标编辑页面
                    context.startActivity(new Intent(context, TargetDetailActivity.class));
                }
            });
        }

        container.addView(convertView);
        return convertView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View convertView = (View) object;
        container.removeView(convertView);

        if (position == targets.size())
            return;
        cacheViews.add(convertView);
    }

    private class ViewHolder {

        private CardView cardView;
        private CircleRingGraph crgTotal;
        private CircleRingGraph crgDaily;
        private TextSwitcher tsDaysSurplus;
        private EditText etPayInput;
        private ImageView ivPayDone;
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
     * 更新DailyPay消费信息
     *
     * @param singlePay 单笔消费金额（都为正数）
     */
    private void updateDailyPay(DailyPay dailyPay, float singlePay) {

        if (dailyPay != null) {

            dailyPay.setDailyExpense(dailyPay.getDailyExpense() + singlePay);
            dailyPay.setDailySurplus(dailyPay.getDailySurplus() - singlePay);
            try {
                dbManager.update(dailyPay);
            } catch (DbException e) {
                e.printStackTrace();
            }

            String sPayDetail = DateUtil.getInstance().getDateFormatDetail(System.currentTimeMillis()) +
                    "-" + MathUtil.getStrByPrecisionUp(singlePay, 2) + "元";
            DailyPayBills dailyPayBills = new DailyPayBills(dailyPay.getId(), sPayDetail);
            boolean bAddBills = false;
            try {
                bAddBills = dbManager.saveBindingId(dailyPayBills);
            } catch (DbException e) {
                e.printStackTrace();
            }

            if (!bAddBills)
                Toast.makeText(context, "数据添加失败！", Toast.LENGTH_SHORT).show();

        }
    }

    /**
     * 更新Target信息
     *
     * @param moneyTotal   target总金额
     * @param moneySurplus target剩余金额
     */
    private void updateTarget(Target target, String moneyTotal, String moneySurplus) {

        if (!TextUtils.isEmpty(moneySurplus))
            target.setMoneySurplus(moneySurplus);

        if (!TextUtils.isEmpty(moneyTotal)) {

            target.setMoneyTotal(moneyTotal);
            target.setMoneyDaily(String.valueOf(Float.valueOf(target.getMoneySurplus()) /
                    Integer.valueOf(target.getDaysSurplus())));
        }

        try {
            dbManager.update(target);
            TimerSetting.DB_UPDATE = true;
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 收起键盘
     */
    private void hideInputMethod(View view) {

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive())
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * TextSwitch内创建的index指示器的TextView
     */
    private TextView createTextView() {

        TextView tv = new TextView(context);
        tv.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        tv.setTextSize(28);
        tv.setTextColor(Color.WHITE);
        tv.setGravity(Gravity.CENTER);

        return tv;
    }

    /**
     * init TextSwitcher
     */
    private void initTextSwitcher(TextSwitcher tsDaysSurplus, Target target) {

        tsDaysSurplus.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                return createTextView();
            }
        });
        tsDaysSurplus.setText("0");
        if (target != null)
            tsDaysSurplus.setText(target.getDaysSurplus());
    }

}

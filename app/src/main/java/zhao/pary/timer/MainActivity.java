package zhao.pary.timer;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextSwitcher;
import android.widget.Toast;
import android.widget.ViewSwitcher;
import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;
import java.util.ArrayList;
import java.util.List;
import adapter.MainPagerAdapter;
import customview.TextViewFZ;
import model.DailyPay;
import model.Target;
import transformer.ZoomOutPageTransformer;
import utils.DateUtil;
import utils.SharedPreferencesHelper;

public class MainActivity extends BaseActivity {

    private static final String TAG = "====";

    private DbManager dbManager = x.getDb(TimerApplication.getInstance().getDaoConfig());

    private TextSwitcher tsTitle;

    private ArrayList<Target> targets = new ArrayList<>();

    private AlertDialog dialogExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getDataFromDb();

        getDisplayInfo();

        checkPassword();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (TimerSetting.DB_UPDATE) {

            recreate();
            TimerSetting.DB_UPDATE = Boolean.FALSE;
        }
    }

    /**
     * 存储屏幕宽度、高度
     */
    private void getDisplayInfo() {

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        SharedPreferencesHelper helper = SharedPreferencesHelper.getInstance(this);
        helper.putInt(TimerSetting.DISPLAY_WIDTH, metrics.widthPixels);
        helper.putInt(TimerSetting.DISPLAY_HEIGHT, metrics.heightPixels);
    }

    /**
     * 读取数据库中的数据进行刷新页面
     */
    private void getDataFromDb() {

        List<Target> targetList = selectedTargetList();

        if (targetList != null && !targetList.isEmpty()) {

            Target target;
            for (int i = 0; i < targetList.size(); i++) {

                target = targetList.get(i);
                int currentSurplus = DateUtil.getInstance().getCurrentDays(
                        DateUtil.getInstance().getDateFormat(System.currentTimeMillis()), target.getDateEnd());

                int daysSurplus = Integer.valueOf(target.getDaysSurplus());
                if(daysSurplus >0 && currentSurplus <= 0) {

                    target.setDaysSurplus(String.valueOf(0));
                    target.setInProgress(false);
                    try {
                        dbManager.update(target);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }

                    targetList.set(i, target);

                }

            }

            targets.addAll(targetList);
            checkDailyPays(targetList);

        }

    }

    /**
     * 获取数据库存储的数据
     */
    private List<Target> selectedTargetList() {

        List<Target> targetList = null;

        try {
            targetList = dbManager.selector(Target.class).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }

        return targetList;
    }

    /**
     * 检测DailyPay集合是否是最新的数据，不是的话补上数据
     */
    private void checkDailyPays(List<Target> targets) {

        if (targets == null || targets.isEmpty())
            return;

        Target target;
        DateUtil dateUtil = DateUtil.getInstance();

        for (int i = 0; i < targets.size(); i++) {

            target = targets.get(i);
            int targetId = target.getId();

            List<DailyPay> dailyPays = null;
            try {
                dailyPays = dbManager.selector(DailyPay.class)
                        .where("targetId", "=", targetId).findAll();
            } catch (DbException e) {
                e.printStackTrace();
            }

            int currentDays = dateUtil.getCurrentDays(target.getDateStart(),
                    dateUtil.getDateFormat(System.currentTimeMillis()));
            float fTotalDaily = Float.valueOf(target.getMoneyDaily());
            int lostDays = currentDays;

            if (dailyPays != null && !dailyPays.isEmpty()) {

                if (dailyPays.size() < currentDays)
                    lostDays = currentDays - dailyPays.size();

            }

            addLostDailyPays(targetId, fTotalDaily, lostDays);
        }
    }

    /**
     * 填补数据
     */
    private void addLostDailyPays(int targetId, float moneyTotalDaily, int lostNumber) {

        if(lostNumber == 0)
            return;

        List<DailyPay> dailyPayListAdd = new ArrayList<>();
        DailyPay dailyPay = new DailyPay(targetId, moneyTotalDaily, 0);

        for (int j = 0; j < lostNumber; j++) {

            dailyPayListAdd.add(dailyPay);
        }

        boolean isSave = false;
        try {
            isSave = dbManager.saveBindingId(dailyPayListAdd);
        } catch (DbException e) {
            e.printStackTrace();
        }

        if (isSave)
            Toast.makeText(this, "数据更新成功！", Toast.LENGTH_SHORT).show();
    }

    /**
     * 如果有设置了手势密码则先验证密码
     */
    private void checkPassword() {

        if (TimerSetting.IS_GESTURE_PASSWORD_PASS)
            return;

        String s = SharedPreferencesHelper.getInstance(getApplicationContext())
                .getString(TimerSetting.GESTURE_PASSWORD, "");
        if (!s.isEmpty()) {

            Intent intent = new Intent(this, GesturePasswordActivity.class);
            startActivity(intent);
        }
    }

    private void initView() {

        initViewPager();

        initTextSwitcher();
    }

    /**
     * 初始化设置ViewPager
     */
    private void initViewPager() {

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager_main);
        assert viewPager != null;

        //设置相隔两个item项目之间间隔
        viewPager.setPageMargin(this.getResources()
                .getDimensionPixelOffset(R.dimen.dimen_08));
        //尽量大于一屏幕可见个数，防止切换时相邻item贴在一起
        viewPager.setOffscreenPageLimit(targets.size());

        MainPagerAdapter adapter = new MainPagerAdapter(this);
        adapter.setTargets(targets);
        adapter.setDbManager(dbManager);

        viewPager.setAdapter(adapter);

        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                //更改顶部指示器数字
                tsTitle.setText(String.valueOf(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    /**
     * 初始化设置Fab
     */
    public void initFabFinger(View view) {

        Snackbar.make(view, "素锦", Snackbar.LENGTH_SHORT).show();
    }

    public void initFabLock(View view) {

        Snackbar.make(view, "This is Lock!", Snackbar.LENGTH_LONG).show();
    }

    /**
     * 跳转到设置页面
     */
    public void initFabSettings(View view) {

        startActivity(new Intent(this, SettingActivity.class));
    }

    public void initFabAdd(View view) {

        startActivity(new Intent(this, AddTripActivity.class));
    }

    /**
     * index指示器
     */
    private void initTextSwitcher() {

        tsTitle = (TextSwitcher) findViewById(R.id.ts_title);
        tsTitle.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {

                return createTextView();
            }
        });
        tsTitle.setText("0");
    }

    /**
     * TextSwitch内创建的index指示器的TextView
     */
    private TextViewFZ createTextView() {

        TextViewFZ tv = new TextViewFZ(this);
        tv.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        tv.setTextSize(this.getResources().getDimension(R.dimen.sp_16));
        tv.setTextColor(Color.WHITE);
        tv.setGravity(Gravity.CENTER);

        return tv;
    }

    /**
     * 返回键处理
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            popExitDialog();
            return true;
        } else if(keyCode == KeyEvent.KEYCODE_MENU) {

            startActivity(new Intent(this, SettingActivity.class));
            return true;
        } else {

            return super.onKeyDown(keyCode, event);
        }
    }

    /**
     * 创建退出的弹窗询问
     */
    private void popExitDialog() {

        if (dialogExit == null) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            dialogExit = builder.setMessage(R.string.exit_hint)
                    .setNegativeButton(R.string.no, null)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                            TimerApplication.getInstance().onTerminate();
                        }
                    })
                    .create();
        }

        dialogExit.show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TimerSetting.IS_GESTURE_PASSWORD_PASS = false;
    }
}

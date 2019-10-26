package zhao.pary.timer;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextSwitcher;
import android.widget.ViewSwitcher;
import customview.GesturePassword;
import customview.TextViewFZ;
import utils.Md5Utils;
import utils.SharedPreferencesHelper;

public class GesturePasswordActivity extends BaseActivity {

    private Context mContext;
    private TextSwitcher tsPwdHint;
    private GesturePassword mPwdView;
    private String firstPassword = "";
    private boolean hasGesturePwd = false;
    private boolean isFirstPassword = true;
    //设置里边要关掉手势密码的话Intent携带的值为true;
    private boolean isCloseGesturePassword = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_set);

        initView();
    }

    /**
     * 在这里进行初始化
     */
    private void initView() {

        mContext = getApplicationContext();

        hadGesturePassword();

        getIsCloseGesturePassword();

        initTvHint();

        initTvPwdForget();

        initPassword();
    }

    private void hadGesturePassword() {

        SharedPreferencesHelper helper = SharedPreferencesHelper.getInstance(mContext);
        hasGesturePwd = !helper.getString(TimerSetting.GESTURE_PASSWORD, "").isEmpty();
        isFirstPassword = !hasGesturePwd;
    }

    private void getIsCloseGesturePassword() {

        isCloseGesturePassword = getIntent()
                .getBooleanExtra(TimerSetting.IS_CLOSE_GESTURE_PASSWORD, false);
    }

    /**
     * 顶部提示信息
     */
    private void initTvHint() {

        tsPwdHint = (TextSwitcher) findViewById(R.id.ts_pwd_hint);
        tsPwdHint.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                return createTextView();
            }
        });
        tsPwdHint.setText(mContext.getText(R.string.please_set_password));
    }

    /**
     * TextSwitcher需要的的TextView创建方法
     */
    private TextViewFZ createTextView() {

        TextViewFZ tv = new TextViewFZ(mContext);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(16);
        tv.setTextColor(Color.BLACK);
        tv.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));

        return tv;
    }

    /**
     * 手势密码相关监听设置
     */
    private void initPassword() {

        GesturePassword mPwdView = (GesturePassword) findViewById(R.id.mPassWordView);
        mPwdView.setOnCompleteListener(new GesturePassword.OnCompleteListener() {
            @Override
            public void onComplete(String mPassword) {

                dealWithPassword(mPassword);
            }
        });
    }

    /**
     * "忘记密码"显示与否判断（根据时候有手势密码）
     */
    private void initTvPwdForget() {

        TextViewFZ tvForget = (TextViewFZ) findViewById(R.id.tv_forget);
        if (tvForget == null)
            return;

        if (hasGesturePwd)
            tvForget.setVisibility(View.VISIBLE);
        else
            tvForget.setVisibility(View.INVISIBLE);

    }

    /**
     * "忘记密码"点击事件
     */
    public void pwdForgetEvent(View view) {

        //忘记密码
//        startActivity(new Intent(this, ));
    }

    /**
     * 设置密码了启动验证密码页面按返回键的话就退出app
     */
    @Override
    public void onBackPressed() {
        if (!hasGesturePwd || isCloseGesturePassword)
            super.onBackPressed();
        else//如果是启动app进入密码页面则按返回键退出App
        {
            Snackbar.make(mPwdView, R.string.exit_hint, Snackbar.LENGTH_LONG)
                    .setAction(R.string.yes, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            TimerApplication.getInstance().onTerminate();
                        }
                    }).show();
        }

    }

    /**
     * 处理密码输入
     * */
    private void dealWithPassword(String mPassword) {

        //判断是否已经设置过密码
        SharedPreferencesHelper helper = SharedPreferencesHelper.getInstance(getApplicationContext());
        String sPassword = helper.getString(TimerSetting.GESTURE_PASSWORD, "");
        Md5Utils md5 = new Md5Utils();
        //如果没有则设置密码输入两次验证
        if (TextUtils.isEmpty(sPassword)) {

            if (isFirstPassword) {//第一次设置密码

                mPwdView.clearPassword();
                isFirstPassword = false;
                firstPassword = mPassword;
                tsPwdHint.setText(mContext.getText(R.string.please_reset_password));
            } else {//第二次设置

                if (firstPassword.equals(mPassword)) {//密码一致

                    mPwdView.clearPassword();
                    helper.putString(TimerSetting.GESTURE_PASSWORD, md5.toMd5(mPassword, ""));
                    setResult(TimerSetting.RESULT_CODE_OPEN_GESTURE_PWD);
                    finish();
                } else {//密码不一致

                    mPwdView.markError();
                    tsPwdHint.setText(mContext.getText(R.string.pwd_not_same));
                    ((TextViewFZ) tsPwdHint.getCurrentView()).setTextColor(
                            ContextCompat.getColor(mContext, R.color.colorRed));
                }

            }

        } else {//已经设置过密码

            if (sPassword.equals(md5.toMd5(mPassword, ""))) {//输入密码正确

                if(isCloseGesturePassword)
                    setResult(TimerSetting.RESULT_CODE_CLOSE_GESTURE_PWD);
                else
                    TimerSetting.IS_GESTURE_PASSWORD_PASS = true;
                finish();
            } else {//输入密码不正确

                mPwdView.markError();
                tsPwdHint.setText(mContext.getText(R.string.pwd_error));
                ((TextViewFZ) tsPwdHint.getCurrentView()).setTextColor(
                        ContextCompat.getColor(mContext, R.color.colorRed));
            }
        }
    }

}

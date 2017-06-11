package fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import utils.SharedPreferencesHelper;
import zhao.pary.timer.GesturePasswordActivity;
import zhao.pary.timer.R;
import zhao.pary.timer.TimerSetting;

/**
 * App的设置页面
 */

public class SettingFragment extends PreferenceFragment {

    private Activity context;
    private boolean checkGesturePwd = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference_setting);

        context = getActivity();

        initView();
    }

    private void initView() {

        initGesturePwd();

        initMaterialDesignDialog();
    }

    /**
     * 手势密码开关设置
     * */
    private void initGesturePwd() {

        CheckBoxPreference gesturePwd = (CheckBoxPreference) getPreferenceManager()
                .findPreference(this.getText(R.string.set_gesture_pwd));
        //设置默认选中状态
        String sp = SharedPreferencesHelper.getInstance(context)
                .getString(TimerSetting.GESTURE_PASSWORD, "");
        checkGesturePwd = !sp.isEmpty();
        gesturePwd.setChecked(checkGesturePwd);

        gesturePwd.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean b = Boolean.valueOf(newValue.toString());
                setGesturePwd(b);
                return true;
            }
        });

    }

    /**
     * 根据手势设置boolean结果进行处理
     * */
    private void setGesturePwd(boolean checked) {

        //开
        if (checked) {

            startActivityForResult(new Intent(context, GesturePasswordActivity.class),
                    TimerSetting.REQUEST_CODE_OPEN_GESTURE_PWD);
        } else {//关

            Intent intent = new Intent(context, GesturePasswordActivity.class);
            intent.putExtra(TimerSetting.IS_CLOSE_GESTURE_PASSWORD, true);
            startActivityForResult(intent, TimerSetting.REQUEST_CODE_CLOSE_GESTURE_PWD);
        }
    }

    /** MaterialDesignDialog */
    private void initMaterialDesignDialog() {

        Preference dialog = getPreferenceManager()
                .findPreference(this.getText(R.string.set_dialog_pop));
        dialog.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                popAlertDialog();
                return true;
            }
        });
    }

    /** 弹出AlertDialog */
    private void popAlertDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.set_dialog)
                .setMessage(R.string.set_dialog_pop_hint)
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    /**
     * 设置手势密码返回结果处理
     * */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (resultCode) {

            //设置手势密码成功
            case TimerSetting.RESULT_CODE_OPEN_GESTURE_PWD:

                setPreferenceValue(R.string.set_gesture_pwd, true);
                break;

            //验证密码成功
            case TimerSetting.RESULT_CODE_CLOSE_GESTURE_PWD:

                setPreferenceValue(R.string.set_gesture_pwd, false);
                clearGesturePassword();
                break;

            default:

                setPreferenceValue(R.string.set_gesture_pwd, checkGesturePwd);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 根据传入的key和value找到对应的CheckBoxPreference设置
     * */
    private void setPreferenceValue(int resId, boolean checked) {

        checkGesturePwd = checked;

        CheckBoxPreference cbp = (CheckBoxPreference) getPreferenceManager()
                .findPreference(this.getText(resId));
        cbp.setChecked(checked);
    }

    /**
     * 清除存储的手势密码
     * */
    private void clearGesturePassword() {

        SharedPreferencesHelper helper = SharedPreferencesHelper.getInstance(context);
        helper.putString(TimerSetting.GESTURE_PASSWORD, "");
    }
}

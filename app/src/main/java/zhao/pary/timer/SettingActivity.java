package zhao.pary.timer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import fragment.SettingFragment;

public class SettingActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        getFragmentManager().beginTransaction()
                .replace(R.id.activity_setting, new SettingFragment()).commit();
    }
}

package zhao.pary.timer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.xutils.x;

/**
 * Created by zpf on 2016/12/20.
 */

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TimerApplication.getInstance().addActivity(this);
        x.view().inject(this);
    }
}

package zhao.pary.timer;

import android.app.Activity;
import android.app.Application;
import org.xutils.DbManager;
import org.xutils.x;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/8/17.
 */

public class TimerApplication extends Application {

    private static final String DB_NAME = "timer.db";
    private ArrayList<Activity> activities = new ArrayList<>();
    private static TimerApplication myApplication;
    private DbManager.DaoConfig daoConfig;

    //单例模式获取唯一的myApplication
    public static TimerApplication getInstance() {

        synchronized (TimerApplication.class) {

            if (myApplication == null) {
                myApplication = new TimerApplication();
            }
        }
        return myApplication;
    }

    public DbManager.DaoConfig getDaoConfig() {

        return daoConfig;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG); // 开启debug会影响性能

        daoConfig = new DbManager.DaoConfig()
                .setDbName(DB_NAME)
                .setDbVersion(1)
                .setDbOpenListener(new DbManager.DbOpenListener() {
                    @Override
                    public void onDbOpened(DbManager db) {
                        // 开启WAL, 对写入加速提升巨大
                        db.getDatabase().enableWriteAheadLogging();
                    }
                })
                .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                    @Override
                    public void onUpgrade(DbManager db, int oldVersion, int newVersion) {


                    }
                });

    }

    //添加activity到集合里边
    public void addActivity(Activity activity) {
        activities.add(activity);
    }

    //清楚集合里的所有activity并退出程序的方法
    @Override
    public void onTerminate() {
        super.onTerminate();
        //增强for循环遍历所有的activity关闭
        for (Activity activity : activities) {
            activity.finish();
        }
//        android.os.Process.killProcess(android.os.Process.myPid());
    }

}
